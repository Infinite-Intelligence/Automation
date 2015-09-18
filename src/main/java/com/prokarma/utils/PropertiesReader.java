
package com.prokarma.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 */
public class PropertiesReader {
    
    private static Logger logger = LoggerFactory.getLogger(PropertiesReader.class);

    /**
     * Loads the property file from the path provided and returns a Property
     * object
     * 
     * @param path
     * @return
     */
    public static Properties loadPropertyFile(String path) {
	logger.info("Loading the property file : " + path);
	Properties prop = new Properties();
	FileInputStream fis = null;

	try {
	    fis = new FileInputStream(path);
	    prop.load(fis);
	} catch (FileNotFoundException e) {
	    logger.info(e.getMessage(), e);
	} catch (IOException e) {
	    logger.info(e.getMessage(), e);
	} finally {
	    if (fis != null) {
		try {
		    fis.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}

	return prop;
    }
}

