

package com.prokarma.dataprovider;

import java.util.HashMap;

import org.testng.annotations.DataProvider;

import com.prokarma.base.TestBase;
import com.prokarma.utils.Constants;

public class RowDataProvider extends TestBase{
    
    
    @DataProvider
    public static Object[][] getRowData() {
	Object[][] object =  new Object[reader.getRowCount(Constants.SHEET_NAME)-1][1];
	
	// Iterating through every row in the sheet
	for (int currentTestcase = 2; currentTestcase <= reader.getRowCount(Constants.SHEET_NAME); currentTestcase++) {	    
	    
		HashMap<String, String> rowData = reader.getRowData(Constants.SHEET_NAME, currentTestcase);
	    // allocating Row Data wrapper object to Object array
		object[currentTestcase-2][0]=new RowDataWrapper(rowData,currentTestcase);
	}
	return  object;
    }
}
