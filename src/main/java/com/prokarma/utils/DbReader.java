

package com.prokarma.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 */
public class DbReader {

	private Connection connection = null;
	private ResultSet resultSet = null;
	private PreparedStatement preparedStatement = null;
	private ResultSetMetaData resultSetMetaData = null;

	private static Logger logger = LoggerFactory.getLogger(DbReader.class);

	/**
	 * This method is used to establish the database connection
	 * 
	 * @param driver
	 * @param url
	 * @param userName
	 * @param password
	 * @throws Exception 
	 * 
	 */
	public DbReader(String driver, String url, String userName,
			String password) throws Exception {
		try {
			logger.info(" Trying to connect database");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, userName, password);
			connection.setAutoCommit(false);
		} catch (Exception e) {
			logger.info(e.getMessage(),e);
			throw e;
		}
	}
	
	
	/**
	 * Execute single query 
	 * @param query
	 * @return
	 * @throws SQLException 
	 */
	public List<Map<String,String>> executeQuery(String query) throws SQLException{
		try {
			preparedStatement = connection.prepareStatement(query);
			
			// If it is a DML query, execute teh below code
			if(!query.split(" ")[0].equalsIgnoreCase("SELECT")) {
				int status = preparedStatement.executeUpdate();

				// Checking how many rows are updated.
				String _status=(status!=0)?""+status+" Row(s) affected":" No rows affected";
				logger.info("Execute Update, staus : "+ _status);
				connection.commit();
				return null;
			}

			resultSet = preparedStatement.executeQuery();
			resultSetMetaData = resultSet.getMetaData();

		} catch (SQLException e) {   
			logger.info(e.getMessage(), e);
			throw e;
		}
		return read();
	}


	private static volatile DbReader instance = null;

	/**
	 * This method creates and returns the DbReader instance
	 * 
	 * @param driver
	 * @param url
	 * @param userName
	 * @param password
	 * @return DbReader
	 * @throws Exception 
	 */
	public static DbReader getInstance(String driver, String url,
			String userName, String password) throws Exception {
		logger.info(" Trying to create instance for DB");
		if (instance == null) {
			synchronized (DbReader.class) {
				if (instance == null) {
					instance = new DbReader(driver, url, userName, password);
				}
			}
		}
		return instance;
	}

	/**
	 * This method reads the data from database
	 * 
	 * @return List of map objects
	 */
	private List<Map<String, String>> read() {
		logger.debug(" inside read() method");
		List<Map<String, String>> maplist = null;
		try {
			int colCount = resultSetMetaData.getColumnCount();

			maplist = new ArrayList<Map<String, String>>();
			while (resultSet.next()) {
				Map<String, String> map = new HashMap<String, String>();
				for (int i = 1; i <= colCount; i++) {
					map.put(resultSetMetaData.getColumnName(i).trim(),
							resultSet.getString(i).trim());
				}
				maplist.add(map);
			}
		} catch (Exception e) {
			logger.info(e.getMessage(),e);
		} finally {
			try {
				resultSet.close();

			} catch (SQLException e) {
				if (e.getMessage() != null && e.getMessage().isEmpty()) {
					logger.error(e.getMessage());
				} else {
					logger.error(" Problem occured while getting data from db");
				}
			}

		}
		return maplist;
	}
}
