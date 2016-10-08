package com.sgm.hsearch.service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sgm.hsearch.dao.HsDocumentMapper;
import com.sgm.hsearch.entity.HsDocument;
import com.sgm.hsearch.utils.DocumentIndex;

@Service
public class IndexServiceImpl implements IndexService {

	private static Logger logger = LoggerFactory.getLogger(IndexServiceImpl.class);
	private String indexRootPath;	//index location

	@Autowired
	private HsDocumentMapper documentMapper;

	public IndexServiceImpl() {
		indexRootPath = "/HSearch/index";	//init location, disk with running env
	}

	/**
	 * retrieve all docs from db, and index them
	 */
	public int indexDbDocs() {	
		int successNum = 0;
		IndexWriter writer = getwriter();
		if (writer == null) {
			logger.error("get writer failed!");
			return 0;
		}
		List<HsDocument> documents = documentMapper.selectAll();
		for (HsDocument hsDocument : documents) {
			logger.debug("index doc:"+hsDocument.getPath());
			if (DocumentIndex.indexDoc(hsDocument, writer)) {
				successNum++;
			}
		}
		try {
			writer.commit();	//flush and combine middle file
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				if(writer!=null)
					writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return successNum;
	}

	/**
	 * index one provided doc
	 */
	public boolean indexDoc(HsDocument doc) {	
		
		IndexWriter writer = getwriter();
		if (writer == null) {
			logger.error("get writer failed!");
			return false;
		}
		boolean flag = DocumentIndex.indexDoc(doc, writer);
		try {
			writer.commit();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return flag;
	}
		
	/**
	 * clean index dir, for recreating 
	 */
	@Override
	public void cleanIndex() {
		IndexWriter writer = getwriter();
		try {
			logger.info("clean all index:"+ indexRootPath);
			writer.deleteAll();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(writer!=null){
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * get index writer with index dir 
	 * @return
	 */
	private IndexWriter getwriter() {
		IndexWriter writer = null;
		Analyzer analyzer = new SmartChineseAnalyzer();		//smartcn to analyze Chinese natural languages
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);	
		iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);			//if exist, update; else create
		try {
			Directory dir = FSDirectory.open(Paths.get(indexRootPath));	
			writer = new IndexWriter(dir, iwc);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return writer;
	}

	
}
