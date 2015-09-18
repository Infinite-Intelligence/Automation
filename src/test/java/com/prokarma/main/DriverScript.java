package com.prokarma.main;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import com.jayway.restassured.response.Response;
import com.prokarma.dataprovider.RowDataProvider;
import com.prokarma.dataprovider.RowDataWrapper;
import com.prokarma.testfactory.APITestFactory;
import com.prokarma.utils.Constants;
import com.prokarma.utils.FileUtil;

public class DriverScript extends APITestFactory {

	private static Logger logger = LoggerFactory.getLogger(DriverScript.class);

	/**
	 * This method is the main method that starts the execution.
	 * 
	 * @throws IOException
	 */
	@Test(dataProviderClass = RowDataProvider.class, dataProvider = "getRowData")
	public void test(RowDataWrapper rowDataWrapper) throws IOException {
		testFailed=false;
		logger.info("\n\nExecuting test case "+rowDataWrapper+"\n");
		currentTestcase=rowDataWrapper.getCurrentTestcase();
		HashMap<String, String> rowData = rowDataWrapper.getRowData();
		String runMode = rowData.get(Constants.COLUMN_RUN_MODE).trim();
		tcid = rowData.get(Constants.COLUMN_TCID).trim();
		
		// Execute when Run Mode is Yes
		if (runMode.equalsIgnoreCase("YES")) {
			
			// Initialize all the values from test data sheet
			initialize(rowData);
			
			//TODO define runBootstrapSQLQueries()
			
			// Replaces the URL with path parameters
			replaceURLParameters(urlParameters);			
			
			Response response = getResponse();
			
			if (response != null) {
	
				FileUtil.createFile(fileoutpath, tcid + "_Response.txt",response.asString());
				validateResponse(response);

				// Updating the pass result only if there is no failure
				if (!testFailed) {
					testPassed();
				}
			}
		} else if (runMode.equalsIgnoreCase("NO")) {	    
			testSkipped(tcid+" : Test Skipped as Run Mode was NO");

		}else{
			testFailed("Unable to read Run Mode");
		}
	}	
}
