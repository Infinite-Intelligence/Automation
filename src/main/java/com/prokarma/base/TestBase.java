

package com.prokarma.base;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeSuite;

import com.prokarma.utils.Constants;
import com.prokarma.utils.DateTimeUtil;
import com.prokarma.utils.ExcelReader;
import com.prokarma.utils.FileUtil;
import com.prokarma.utils.PropertiesReader;

public class TestBase {
    protected static ExcelReader reader;
    protected Properties requestProp;
    protected Properties errorCodesProp;
    protected Properties configProp;
    protected String currentTimeStamp;
    
    private String requestPropFileName;
    private String errorPropFileName;
    
    private static Logger logger = LoggerFactory.getLogger(TestBase.class);

    @BeforeSuite
    public void setUp() throws IOException {
	logger.info("Loading all the required files");

	//Loading config.properties
	configProp = PropertiesReader.loadPropertyFile(Constants.CONFIG_PROP_PATH);
	
	requestPropFileName = configProp.getProperty(Constants.KEY_REQ_PROP_FILE);
	errorPropFileName   = configProp.getProperty(Constants.KEY_ERR_PROP_FILE);
	
	// Loading Request property file
	requestProp = PropertiesReader.loadPropertyFile(Constants.REQUEST_PROP_PATH + requestPropFileName);

	// Loading Error Codes property file
	errorCodesProp = PropertiesReader.loadPropertyFile(Constants.ERROR_CODES_PROP_PATH + errorPropFileName);

	// Loading the Test data excel sheet
	String path = Constants.TESTDATA_PATH + configProp.getProperty(Constants.KEY_TESTDATA);
	
	reader = FileUtil.getExcelReader(path);
	
	currentTimeStamp = DateTimeUtil.getCurrentTimeStamp();
    }

}
