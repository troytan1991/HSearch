package com.sgm.hsearch.service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sgm.hsearch.dao.HsDocumentMapper;
import com.sgm.hsearch.dto.SearchResultDto;
import com.sgm.hsearch.entity.HsDocument;

@Service
public class SearchFileServiceImpl implements SearchFileService {

	private static Logger logger = LoggerFactory.getLogger(SearchFileServiceImpl.class);
	@Autowired
	private HsDocumentMapper hsDocumentMapper;
	@Autowired
	private IndexService indexService;
	
	private String indexRootPath = "/HSearch/index";	//search beyond index path
	private String httpFileRootPath = "\\10.6.129.118"; //file system server, to calculate client visit file path
	private IndexReader indexReader;					//index reader, consume much time can be reuse
	public SearchFileServiceImpl() {
		try {
			indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexRootPath)));	//initial index reader
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 *search file with input str and classify
	 *@category classify
	 *@keyStr input key words 
	 */
	public List<SearchResultDto> searchFile(String category, String keyStr) {
		List<SearchResultDto> resultDocs = new ArrayList<SearchResultDto>();
		String pathWildcard = getWildcardStr(category);
		try {
			//重复利用reader资源
			IndexReader newReader = DirectoryReader.openIfChanged((DirectoryReader)indexReader);
			if(newReader!=null && indexReader != newReader){
				logger.debug("reader reopened");
				indexReader.close();
				indexReader = newReader;
			}
			
			IndexSearcher searcher = new IndexSearcher(indexReader);
			Analyzer analyzer = new SmartChineseAnalyzer();

			// 通过path限定文档范围
			Query pathQuery = new WildcardQuery(new Term("path", pathWildcard));
			// 标题检索
			Query titleQuery = new QueryParser("title", analyzer).parse(keyStr);
			// 内容检索
			Query contentQuery = new QueryParser("content", analyzer).parse(keyStr);
			// 关键字检索
			Query kwQuery = new QueryParser("keywords", analyzer).parse(keyStr);
			// 概要检索
			Query abstactQuery = new QueryParser("abstacts", analyzer).parse(keyStr);

			BooleanQuery query = new BooleanQuery.Builder().add(pathQuery, Occur.MUST).add(titleQuery, Occur.SHOULD)
					.add(contentQuery, Occur.SHOULD).add(kwQuery, Occur.SHOULD).add(abstactQuery, Occur.SHOULD).build();
			BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);
			TopDocs topDocs = searcher.search(query, 60);
			for (ScoreDoc match : topDocs.scoreDocs) {
				Document doc = searcher.doc(match.doc);
				//-----test results
//				Explanation explanation = searcher.explain(query, match.doc);
//				System.out.println(explanation.toString());
				
				SearchResultDto resultDoc = new SearchResultDto();
				resultDoc.setId(Long.parseLong(doc.get("ID")));
				resultDoc.setTitle(getHighlightText(analyzer, query, "title", doc));
				resultDoc.setUpdatedOn(doc.get("updatedOn"));
				resultDoc.setHighlightText(getHighlightText(analyzer, query, "content", doc));
				resultDoc.setPath(getHttpFilePath(doc.get("path")));
				resultDoc.setVisitCount(Integer.parseInt(doc.get("visitCount")));
				resultDocs.add(resultDoc);
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return resultDocs;
	}

	

	public void outAllFiles() {
		try {
			IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexRootPath)));
			IndexSearcher searcher = new IndexSearcher(indexReader);
			Query allQuery = new MatchAllDocsQuery();
			TopDocs topDocs = searcher.search(allQuery, 500);
			for (ScoreDoc match : topDocs.scoreDocs) {
				Document document = searcher.doc(match.doc);
				logger.debug(document.get("title"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * add visit count , index doc and return 
	 */
	public HsDocument getDocument(Long id) {
		hsDocumentMapper.addVisitCount(id);	//访问量自增1
		HsDocument document = hsDocumentMapper.selectByPrimaryKey(id);
		indexService.indexDoc(document);
		document.setPath(getHttpFilePath(document.getPath()));
		return document;
	}
	
	/***
	 * joint wildcard string, to match path string, filter categroy 
	 * @param category
	 * @return
	 */
	private String getWildcardStr(String category) {
		String result = "";
		if (category == null || "ALL".equals(category)) {
			result = "*";
		} else {
			result = "*" + category + "*";
		}
		return result;
	}
	/**
	 * construct highlight text with input string
	 * @param analyzer
	 * @param query
	 * @param fieldName
	 * @param document
	 * @return
	 */
	private String getHighlightText(Analyzer analyzer, Query query, String fieldName, Document document) {

		String highlightText = "";
		String srcStr = document.get(fieldName);
		if (srcStr != null && srcStr != "") {
			TokenStream tokenStream = analyzer.tokenStream(fieldName, srcStr);	//tokenize whole src string with analyzer and field
			QueryScorer scorer = new QueryScorer(query, fieldName);				//init scorer to score each fragment 
			Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);			//init fragmenter, define the rule that split src into fragments
			Highlighter highlighter = new Highlighter(new SimpleHTMLFormatter("<em>", "</em>"), scorer);	//define highlight format,out put <em>highlight text</em>
			highlighter.setTextFragmenter(fragmenter);							//set fragmenter
			String highStr;											
			try {
				highStr = highlighter.getBestFragment(tokenStream, srcStr);		//get highlight text, one best fragment
				if (highStr != null && highStr != "") {							
					highlightText = highStr;									//fragment useful, return 
				} else {
					int strLength = srcStr.length() > 100 ? 100 : srcStr.length();	//no match fragment, return src str, length less than 100
					highlightText = srcStr.substring(0, strLength);				
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InvalidTokenOffsetsException e) {
				e.printStackTrace();
			}
		}else if(document.get("abstracts")!=null){				//not useful so far
			highlightText = document.get("abstracts");
		}

		return highlightText;
	}
	private String getHttpFilePath(String path){
		String fileSuffix = path.substring(path.indexOf("docs"));
		return "\\"+httpFileRootPath +"\\"+ fileSuffix;
 	}
}
