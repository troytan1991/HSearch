package com.sgm.hsearch.utils;

import java.io.File;

public class FileService implements FileReader{

	public String readFile(File file) {
		String result = "";
		String fileName = file.getName();
		String prefix = fileName.substring(fileName.lastIndexOf(".")+1);
		FileReader reader = null;
		if("doc".equals(prefix) || "docx".equals(prefix)){
			reader = new WordReader();
		}else if("xlsx".equals(prefix)||"xls".equals(prefix)){
			reader = new ExcelReader();
		}else if ("txt".equals(prefix)||"csv".equals(prefix)) {
			reader = new TextReader();
		}
		try {
			if(reader !=null && !fileName.startsWith("~"))
				result= reader.readFile(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.replaceAll("\\n[\\s| ]*\r", "").replaceAll("(\\n){2,}", "\r\n");	//remove redundant enter key
	}
}
