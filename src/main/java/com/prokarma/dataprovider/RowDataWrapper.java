package com.prokarma.dataprovider;

import java.util.HashMap;

import com.prokarma.utils.Constants;

public class RowDataWrapper {
    private HashMap<String, String> rowData;
    private int currentTestcase;

    public RowDataWrapper(HashMap<String, String> rowData, int currentTestcase) {
	this.rowData = rowData;
	this.currentTestcase = currentTestcase;
    }

    public HashMap<String, String> getRowData() {
	return rowData;
    }
    
    public int getCurrentTestcase() {
	return currentTestcase;
    }

    public String toString() {
	return rowData.get(Constants.COLUMN_TCID).trim() + ": " + rowData.get("Api_Name").trim();
    }
}
