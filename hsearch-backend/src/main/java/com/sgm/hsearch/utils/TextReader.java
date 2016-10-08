package com.sgm.hsearch.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class TextReader implements FileReader{

	public String readFile(File file) {
		String encoding = getFilecharset(file);
		StringBuilder sBuilder= new StringBuilder();
		BufferedReader bReader = null;
		try {
			bReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file),encoding));;
			String line = null;
			while ((line=bReader.readLine())!=null) {
				sBuilder.append(line+"\r\n");
			}
		} catch (Exception e) {
		}finally {
			try {
				bReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sBuilder.toString();
	}
	
	private String getFilecharset(File file) {
		BufferedInputStream bis =null;
		String code = "UTF-8";    
		try {
			bis = new BufferedInputStream(new FileInputStream(file));    
			int p = (bis.read() << 8) + bis.read();    
			switch (p) {    
			case 0xefbb:    
				code = "UTF-8";    
				break;    
			case 0xfffe:    
				code = "Unicode";    
				break;    
			case 0xfeff:    
				code = "UTF-16BE";    
				break;    
			default:    
				code = "GBK";    
			}    
			
		} catch (Exception e) {
			// TODO: handle exception
		}finally {
			try {
				bis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
        return code;  
	}
}
