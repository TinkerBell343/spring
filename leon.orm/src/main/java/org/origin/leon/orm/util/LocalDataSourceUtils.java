package org.origin.leon.orm.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.origin.leon.orm.config.DataSource;

public class LocalDataSourceUtils {
	
	public static Connection getConnection(DataSource datasource){
		
		Connection conn = null;
		
		try {
			Class.forName(datasource.getDbDriverClassName());
			conn = DriverManager.getConnection(datasource.getDbUrl(), datasource.getUserName(), datasource.getPassword());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return conn;
	}
	
	public static boolean releaseDataSource(Connection conn){
		if(conn != null){
			try{
				conn.close();
				return true;
			}catch(Exception e){
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
}
