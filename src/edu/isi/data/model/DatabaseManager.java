package edu.isi.data.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
	private static DatabaseManager _dbMgr = null;
	
	protected Connection _conn;
	
	private DatabaseManager() {
		
	}
	
	public static DatabaseManager inst() {
		if (_dbMgr == null) {
			_dbMgr = new DatabaseManager();
		}
		return _dbMgr;
	}
	
	public void connect(String fileName) {
		try {
			Class.forName("org.sqlite.JDBC");
			_conn = DriverManager.getConnection("jdbc:sqlite:" + fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean isConnected() {
		try {
			if (_conn == null || _conn.isClosed()) 
				return false;
		} catch (SQLException e) {
			return false;
		}
		return true;
	}
	
	public Statement getStatement() throws Exception {
		return _conn.createStatement();
	}
	
	public ResultSet getTables(String type) {
		try {
			return _conn.getMetaData().getTables(null, null, type, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
