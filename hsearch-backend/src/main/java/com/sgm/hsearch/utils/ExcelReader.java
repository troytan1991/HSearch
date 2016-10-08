package com.sgm.hsearch.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader implements FileReader {

	public String readFile(File file) {
		StringBuilder sb = new StringBuilder();
		boolean isE2007 = false; 				//default less than 2007
		Workbook wb = null;
		if (file.getName().endsWith("xlsx"))	
			isE2007 = true;					
		try {
			InputStream input = new FileInputStream(file); 	//file to input stream
			if (isE2007)
				wb = new XSSFWorkbook(input);
			else
				wb = new HSSFWorkbook(input);		//init workbook
			for (int num = 0; num < wb.getNumberOfSheets(); num++) { //traverse all sheets
				Sheet sheet = wb.getSheetAt(num); 			
				Iterator<Row> rows = sheet.rowIterator(); 		//get rows
				while (rows.hasNext()) {						
					Row row = rows.next(); 					
					Iterator<Cell> cells = row.cellIterator(); //get cells
					while (cells.hasNext()) {
						Cell cell = cells.next();				
						sb.append(cell.toString()+" ");			//read cell
					}
					sb.append("\r\n");
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}finally {
			if(wb!=null){
				try {
					wb.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}
	
}
