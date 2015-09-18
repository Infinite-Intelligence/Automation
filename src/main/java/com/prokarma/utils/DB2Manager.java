package com.prokarma.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author spalu1
 *
 */
/**
 * @author spalu1
 *
 */
public class DB2Manager {

	private static DbReader dbReader = null;
	protected Properties requestProp;
	private Properties configProp;
	private String sqlPropFileName;
	private static Logger logger = LoggerFactory.getLogger(DB2Manager.class);

	public DB2Manager() throws Exception {
		dbReader = DbReader.getInstance(Constants.JDBC_DRVER_DB2,
				Constants.JDBC_CONNECTION_URL, Constants.DB_USER,
				Constants.DB_PASS);
		
		configProp      = PropertiesReader.loadPropertyFile(Constants.CONFIG_PROP_PATH);
		sqlPropFileName = configProp.getProperty(Constants.KEY_SQL_PROP_FILE);
	}

	/**
	 * Executes all the SQL queries passed to it 
	 * @param queries
	 * @return List of HashMap containing all the query results where Keys are column names
	 * @throws SQLException 
	 */
	public List<Map<String, String>> executeQueries(List<String> queries) throws SQLException {
		Iterator<String> queriesIt = queries.iterator();
		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();

		// Iterating through all the passed queries
		while (queriesIt.hasNext()) {
			String query = queriesIt.next();

			// Executing query
			List<Map<String, String>> results = dbReader.executeQuery(query);
			    if (results != null) {
				resultList.addAll(results);
			}
		}
		logger.info(resultList.toString());
		return resultList;
	}


	/**
	 * Returns value from a List of HashMap if Key is provided
	 * @param resultList
	 * @param key
	 * @return
	 */
	public String getItemValue(List<Map<String, String>> resultList, String key) {
		try {
			logger.debug("In getItemValue, key: " + key );
			Iterator<Map<String, String>> resultsIt = resultList.iterator();
			while (resultsIt.hasNext()) {
				Map<String, String> resultMap = resultsIt.next();
				for (Map.Entry<String, String> entry : resultMap.entrySet()) {
					if (entry.getKey().equals(key)) {
						logger.debug("In getItemValue, value: " + entry.getValue());
						return entry.getValue();
					}
				}
			}
		} catch(Exception e){
			logger.info(e.getMessage(),e);
		}
		return null;
	}

	
	/**
	 *  Get the Complete query from the property files 
	 * @param dbQueries
	 * @return
	 * @throws IOException
	 */
	public List<String> getQueries(String dbQueries) throws Exception,IOException {
		
		requestProp = new Properties();
		FileInputStream fis = new FileInputStream(
				Constants.SQL_QUERIES_PROP_PATH + sqlPropFileName);
		requestProp.load(fis);
		List<String> allQueries = new ArrayList<String>();
		String[] queryKeyValues = dbQueries.split(",");
		for (String queryKeyValue : queryKeyValues) {

			queryKeyValue = queryKeyValue.trim();
			if (!queryKeyValue.isEmpty()) {
				String[] keyValue = queryKeyValue.split(":");
				if(keyValue.length != 2) {
				    throw new Exception("Query or Parameter key missing in the excel sheet.");
				}
				String query1 = requestProp.getProperty(keyValue[0]);
				String param1 = requestProp.getProperty(keyValue[1]);
				if (param1 != null) {
					// Combines query and parameter
					query1 = ParamsUtil.replaceParams(query1, param1);
				}
				allQueries.add(query1);
				logger.info(query1);
			}

		}
		return allQueries;
	}

	/**
	 * 
	 * @param resultList
	 *            List of results hashmap
	 * @param keyrow
	 *            String in the form
	 *            "ColumnName[RowNum], where rownum = 0,1,2,3...."
	 * @return
	 */
	public String getRowWiseValue(List<Map<String, String>> resultList,
			String keyrow) {

		try {
			logger.debug("In getRowWiseValue, keyrow: " + keyrow );
			//Get the column name
			String key = keyrow.split("\\[")[0];
			String value = null;
			Integer rowNum = 0;
			if (keyrow.indexOf(".") != -1) {
				resultList =  getItemValueByQuery(resultList,key);
			}
			//Checks whether if '[' is present
			if (keyrow.indexOf('[') != -1) {
				//gets the rownumber from inside []
				rowNum = Integer.valueOf(keyrow.replaceAll(".*\\[|\\].*", ""));
			} else {
				//if there is no row number mentioned
				return getItemValue(resultList, key);
			}
			if (0 <= rowNum && resultList.size() > rowNum) {
				Map<String, String> row = resultList.get(rowNum);
				value = row.get(key);
			}
			return value;
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		}
		return null;
	}
    private List<Map<String, String>> getItemValueByQuery(List<Map<String, String>> resultList, String keyrow) {

	//Get query identifier
	String queryId = keyrow.substring(0, keyrow.indexOf("."));
	Iterator<Map <String,String>> it = resultList.iterator();
	List<Map<String, String>> newResultList = new ArrayList<Map<String, String>>();
	while(it.hasNext()) {
	    Map <String,String> resultMap =  it.next();
	    HashMap<String, String> newResultMap = new HashMap<String, String>();
	    for (Map.Entry<String, String> entry : resultMap.entrySet()) {
		if (entry.getKey().startsWith(queryId)) {
		    newResultMap.put(entry.getKey(), entry.getValue());
		}
	    }
	    if(!newResultMap.isEmpty()){
		newResultList.add(newResultMap);
	    }
	}
	return newResultList;
    }
}
