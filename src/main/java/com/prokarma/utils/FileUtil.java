package com.prokarma.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {
    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    public static ExcelReader getExcelReader(String excelFilePath) {

	if (excelFilePath == null) {
	    return null;
	} else {
	    logger.info("Loading the Excel file : " + excelFilePath);
	    return ExcelReader.getInstance(excelFilePath);
	}
    }
    
    public static void createFile(String filepath, String fileName, String content) throws IOException {
	try {

	    if (createDirectory(filepath)) {
		File file = new File(filepath + fileName);
		file.createNewFile();

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(content);
		bw.close();

		logger.info("Directory/File is created!" + filepath + " "
			+ fileName);

	    }

	} catch (Exception e) {
	    logger.info(e.getMessage(),e);
	}

    }

    public static boolean createDirectory(String path) {
	File file = new File(path);
	if (!file.exists()) {
	    if (file.mkdirs()) {
		return true;
	    }
	} else {
	    return true;
	}
	return false;
    }

    public static void copyFile(String sourcePath, String destPath) throws IOException {

	File source = new File(sourcePath);
	File dest = new File(destPath);
	InputStream input = null;
	OutputStream output = null;
	try {
	    input = new FileInputStream(source);
	    output = new FileOutputStream(dest);
	    byte[] buf = new byte[1024];
	    int bytesRead;
	    while ((bytesRead = input.read(buf)) > 0) {
		output.write(buf, 0, bytesRead);
	    }
	} catch (Exception e) {
	    logger.info(e.getMessage(), e);
	} finally {
	    input.close();
	    output.close();
	}
    }

}


