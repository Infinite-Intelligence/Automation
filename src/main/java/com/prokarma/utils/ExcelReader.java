package com.prokarma.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelReader {
    

	private FileInputStream fis = null;
	public  FileOutputStream fileOut =null;
	private Workbook workbook = null;
	private Sheet sheet = null;
	private Row row = null;
	private Cell cell = null;
	public static final Logger logger = LoggerFactory.getLogger(ExcelReader.class);
	private static volatile ExcelReader instance = null;
	private static volatile Map<String,ExcelReader> excelReaderMap = new HashMap<String, ExcelReader>();
	private String path;

	public ExcelReader(String path) {   
		// LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory(); 
		try { 
		this.path = path;
		fis = new FileInputStream(this.path); 
		this.workbook = WorkbookFactory.create(fis); 
		fis.close(); 
		} catch (Exception e) { 
				logger.error(e.getMessage()); 
				} 
			 }
		//}

		// creating singleton Object for each excelFile
		public static ExcelReader getInstance(String excelFilePath) {
			if (instance == null || !excelReaderMap.containsKey(excelFilePath)) {
				synchronized (ExcelReader.class) {
					if (instance == null || !excelReaderMap.containsKey(excelFilePath)) {
						instance = new ExcelReader(excelFilePath);
						excelReaderMap.put(excelFilePath, instance);
					}
				}
			}       
			return excelReaderMap.get(excelFilePath);
		}


		// returns the row count in a sheet
		public int getRowCount(String sheetName) {
			//logger.info(" inside getRowCount() method");
			
			int index = workbook.getSheetIndex(sheetName);
			
			if (index == -1)
				return 0;
			else {
				sheet = workbook.getSheetAt(index);
				int number = sheet.getLastRowNum() + 1;
				return number;
			}

		}

		// returns the columnData in a sheet with colNum
		public List<String> getColumnData(String sheetName, int colNum) {
			//logger.info(" inside getColumnData() method");

			List<String> list = null;
			try {

				list = new ArrayList<String>();
				int index = workbook.getSheetIndex(sheetName);
				if (index == -1) return list;

				sheet = workbook.getSheetAt(index);
				list = this.getEntireColumnCellValues(sheet, colNum);
				return list;

			} catch (Exception e) {
				if (e.getMessage() != null && e.getMessage().isEmpty()) {
					logger.error(e.getMessage());
				} else {
					//logger.error(" column " + colNum + " does not exist in file");
				}
				return list;
			}

		}

		// returns the columnData in a sheet with colName
		public List<String> getColumnData(String sheetName, String colName) {
			//logger.info(" inside getColumnData() method");
			List<String> list = null;
			try {

				list = new ArrayList<String>();
				int index = workbook.getSheetIndex(sheetName);
				if (index == -1) return list;

				sheet = workbook.getSheetAt(index);
				row = sheet.getRow(0);
				if (row == null) return list;

				int colNum = 0;
				for (int i = 0; i < row.getLastCellNum(); i++) {
					if (row.getCell(i).getStringCellValue().trim()
							.equals(colName.trim())){
						colNum = i;
					}                   
				}           
				if (colNum == -1) return list;  

				list = this.getEntireColumnCellValues(sheet,colNum);
				return list;

			} catch (Exception e) {
				if(e.getMessage() != null && e.getMessage().isEmpty()){
					//logger.error(e.getMessage());
				} else{
					//logger.error(" column " + colName + " does not exist in file");
				}   
				return list;
			}
		}


		// returns the data from a cell
		public String getCellData(String sheetName, String colName, int rowNum) {
			//logger.info(" inside getCellData() method");
			try {
				if (rowNum <= 0) return "";

				int index = workbook.getSheetIndex(sheetName);
				int col_Num = -1;
				if (index == -1) return "";

				sheet = workbook.getSheetAt(index);
				row = sheet.getRow(0);
				for (int i = 0; i < row.getLastCellNum(); i++) {
					if (row.getCell(i).getStringCellValue().trim()
							.equals(colName.trim()))
						col_Num = i;
				}
				if (col_Num == -1) return "";

				sheet = workbook.getSheetAt(index);
				row = sheet.getRow(rowNum - 1);
				if (row == null) return "";

				cell = row.getCell(col_Num);
				return this.getCellValue(cell);

			} catch (Exception e) {
				if(e.getMessage() != null && e.getMessage().isEmpty()){
					//logger.error(e.getMessage());
				} else{
					//logger.error("row " + rowNum + " or column " + colName + " does not exist in file");
				}
				return "row " + rowNum + " or column " + colName + " does not exist in file";
			}
		}

		// returns the data from a cell
		public String getCellData(String sheetName, int colNum, int rowNum) {
			//logger.info(" inside getCellData() method");
			try {
				if (rowNum <= 0) return "";

				int index = workbook.getSheetIndex(sheetName);

				if (index == -1) return "";

				sheet = workbook.getSheetAt(index);
				row = sheet.getRow(rowNum - 1);
				if (row == null) return "";

				cell = row.getCell(colNum);

				return this.getCellValue(cell);

			} catch (Exception e) {
				if(e.getMessage() != null && e.getMessage().isEmpty()){
					//logger.error(e.getMessage());
				} else{
					//logger.error("row " + rowNum + " or column " + colNum + " does not exist  in file");
				}
				return "row " + rowNum + " or column " + colNum + " does not exist  in file";
			}
		}

		// find whether sheets exists
		public boolean isSheetExist(String sheetName) {

			//logger.info(" inside isSheetExist() method");
			int index = workbook.getSheetIndex(sheetName);
			if (index == -1) {
				index = workbook.getSheetIndex(sheetName.toUpperCase());
				if (index == -1) return false;
				else
					return true;
			} else
				return true;
		}

		// returns number of columns in a sheet
		public int getColumnCount(String sheetName) {

			//logger.info(" inside getColumnCount() method");
			if (!isSheetExist(sheetName)) return -1;

			sheet = workbook.getSheet(sheetName);
			row = sheet.getRow(0);

			if (row == null) return -1;

			return row.getLastCellNum();

		}

		//returns row number
		public int getCellRowNum(String sheetName, String colName, String cellValue) {
			//logger.info(" inside getCellRowNum() method");

			for (int i = 2; i <= getRowCount(sheetName); i++) {
				if (getCellData(sheetName, colName, i).equalsIgnoreCase(cellValue)) {
					return i;
				}
			}
			return -1;

		}

		// returns Row data in a sheet
		public HashMap<String, String> getRowData(String sheetName, int rowNum) {

			//logger.info(" inside getRowData() method");
			HashMap<String, String> hashMap = null;
			try {

				hashMap = new HashMap<String, String>();            
				if (rowNum <= 0) return hashMap;

				int index = workbook.getSheetIndex(sheetName);          
				if (index == -1) return hashMap;

				sheet = workbook.getSheetAt(index);
				row = sheet.getRow(rowNum - 1);
				Row headerRow = sheet.getRow(0);

				if (headerRow == null || row == null) return hashMap;

				for (int i = 0; i < headerRow.getLastCellNum(); i++) {
					hashMap.put(this.getCellValue(headerRow.getCell(i)), this.getCellValue(row.getCell(i)));                
				}
				return hashMap;

			} catch (Exception e) {
				if(e.getMessage() != null && e.getMessage().isEmpty()){
					//logger.error(e.getMessage());
				} else{
					//logger.error("row " + rowNum + " does not exist  in file");
				}
				return hashMap;
			}
		}

		// get the cell data as string.
		private String getCellValue(Cell cell){

			if (cell == null)
				return "";
			if (cell.getCellType() == Cell.CELL_TYPE_STRING)
				return cell.getStringCellValue();
			else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC
					|| cell.getCellType() == Cell.CELL_TYPE_FORMULA) {

				String cellText = String.valueOf(cell.getNumericCellValue());
				if (DateUtil.isCellDateFormatted(cell)) {
					// format in form of M/D/YY
					double d = cell.getNumericCellValue();

					Calendar cal = Calendar.getInstance();
					cal.setTime(DateUtil.getJavaDate(d));
					cellText = (String.valueOf(cal.get(Calendar.YEAR)))
							.substring(2);
					cellText = cal.get(Calendar.DAY_OF_MONTH) + "/"
							+ cal.get(Calendar.MONTH) + 1 + "/" + cellText;

				}
				return cellText;
			} else if (cell.getCellType() == Cell.CELL_TYPE_BLANK)
				return "";
			else
				return String.valueOf(cell.getBooleanCellValue());

		}

		// get entire column cell data as a list    
		private List<String> getEntireColumnCellValues(Sheet sheet, int colNum){

			List<String> list = new ArrayList<String>();
			for (Row row : sheet) {
				if (row.getRowNum() != 0) {
					cell = row.getCell(colNum);
					if (cell != null) {
						if (cell.getCellType() == Cell.CELL_TYPE_STRING)
							list.add(cell.getStringCellValue());
						else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC
								|| cell.getCellType() == Cell.CELL_TYPE_FORMULA) {

							String cellText = String.valueOf(cell
									.getNumericCellValue());
							if (DateUtil.isCellDateFormatted(cell)) {
								// format in form of M/D/YY
								double d = cell.getNumericCellValue();

								Calendar cal = Calendar.getInstance();
								cal.setTime(DateUtil.getJavaDate(d));
								cellText = (String.valueOf(cal
										.get(Calendar.YEAR))).substring(2);
								cellText = cal.get(Calendar.MONTH) + 1 + "/"
										+ cal.get(Calendar.DAY_OF_MONTH) + "/"
										+ cellText;
							}

							list.add(cellText);
						} else if (cell.getCellType() == Cell.CELL_TYPE_BLANK)
							list.add("");
						else
							list.add(String.valueOf(cell.getBooleanCellValue()));
					}
				}
			}       
			return list;        
		}
	
		
		public boolean setCellData(String sheetName,String colName,int rowNum, String data){
			try{
//			fis = new FileInputStream(path); 
//			workbook = new Workbook(fis);
			if(rowNum<=0)
				return false;
			
			int index = workbook.getSheetIndex(sheetName);
			int colNum=-1;
			if(index==-1)
				return false;
			
			
			sheet = workbook.getSheetAt(index);
			

			row=sheet.getRow(0);
			for(int i=0;i<row.getLastCellNum();i++){
				//System.out.println(row.getCell(i).getStringCellValue().trim());
				if(row.getCell(i).getStringCellValue().trim().equals(colName))
					colNum=i;
			}
			if(colNum==-1)
				return false;

			sheet.autoSizeColumn(colNum); 
			row = sheet.getRow(rowNum-1);
			if (row == null)
				row = sheet.createRow(rowNum-1);
			
			cell = row.getCell(colNum);	
			if (cell == null)
		        cell = row.createCell(colNum);

		    // cell style
		    //CellStyle cs = workbook.createCellStyle();
		    //cs.setWrapText(true);
		    //cell.setCellStyle(cs);
		    cell.setCellValue(data);

		    fileOut = new FileOutputStream(path);

			workbook.write(fileOut);

		    fileOut.close();	
		    
		    fis = new FileInputStream(path); 
			this.workbook = WorkbookFactory.create(fis); 

			}
			catch(Exception e){
				logger.info(e.getMessage(),e);
				return false; 
			}
			return true;
		}

}
