package com.sgm.hsearch.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

public class WordReader implements FileReader {

	public String readFile(File file) {

		String result = "";
		InputStream is = null;
		XWPFWordExtractor extractorx = null;
		WordExtractor extractor = null;
		try {
			is = new FileInputStream(file);
			if (file.getName().endsWith("docx")) {
				XWPFDocument doc = new XWPFDocument(is);
				extractorx = new XWPFWordExtractor(doc);
				result = extractorx.getText();
			} else {
				extractor = new WordExtractor(is);
				result = extractor.getText();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(is !=null)
					is.close();
				if(extractor !=null)
					extractor.close();
				if(extractorx!=null)
					extractorx.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return result;
	}

}
