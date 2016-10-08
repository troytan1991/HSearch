package com.sgm.hsearch.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;

import com.sgm.hsearch.entity.HsDocument;

public class DocumentIndex {

	public static boolean indexDoc(HsDocument doc, IndexWriter writer) {
		boolean indexResult = false;
		Document document = new Document();
		//StringField:no tokenized and indexed, store src as a term, always use for id,country
		Field idField = new StringField("ID", doc.getId().toString(), Store.YES);
		document.add(idField);
		
		Field pathField = new StringField("path", doc.getPath(), Store.YES);
		document.add(pathField);

		// self define field type
		FieldType titleType = new FieldType();
		titleType.setOmitNorms(false);	//enable norms, use self define weight
		titleType.setStored(true);		//store src string
		titleType.setTokenized(true);	//analyze src string, omit unimportant detail, impact the score
		titleType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);	//store term info, useful when highlight text
		Field titleField = new Field("title", doc.getTitle(), titleType);		//apply in title field
		titleField.setBoost(2l);		//set title weight 2,  default 1
		document.add(titleField);		//add field to doc

		//TextField: indexed,tokenized, no term info, use for body
		if (doc.getAbstracts() != null) {	
			Field abstractField = new TextField("abstracts", doc.getAbstracts(), Store.YES);
			abstractField.setBoost((float) 1.5);
			document.add(abstractField);
		}
		
		//content field define
		if (doc.getContent() != null) {
			FieldType contentType = new FieldType();
			contentType.setOmitNorms(false);
			contentType.setStored(true);
			contentType.setTokenized(true);
			contentType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
			Field contentField = new Field("content", doc.getContent(), contentType);
			document.add(contentField);
		}
		
		//key words field
		if (doc.getKeywords() != null) {
			Analyzer kwAnalyzer = new WhitespaceAnalyzer(); 	//analyze keyword with space, attention the form when store to db 
			TokenStream kwTokens = kwAnalyzer.tokenStream("keywords", doc.getKeywords());
			kwAnalyzer.close();
			Field kwField = new TextField("keywords", kwTokens);
			kwField.setBoost(3l);
			document.add(kwField);
		}

		if (doc.getUpdatedOn() != null) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
			Field dateField = new StringField("updatedOn", format.format(doc.getUpdatedOn()),Store.YES);
			document.add(dateField);
		}
		
		Field visitCount = new StringField("visitCount", doc.getVisitCount().toString(),Store.YES);
		document.add(visitCount);

		try {
			if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
				writer.addDocument(document);
			} else {
				writer.updateDocument(new Term("ID", doc.getId().toString()), document);
			}
			indexResult = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return indexResult;
	}
}
