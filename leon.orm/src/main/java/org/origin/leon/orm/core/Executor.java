package org.origin.leon.orm.core;

import java.sql.Connection;
import java.sql.SQLException;

import org.origin.leon.orm.config.DataSource;
import org.origin.leon.orm.util.LocalDataSourceUtils;

public class Executor {
	
	private DefaultSimpleInitializer initializer;
	
	private Executor(){
		
	}
	
	public Executor(DefaultSimpleInitializer initializer){
		this.initializer = initializer;
	}
	
	public <T> T execute(StatementCallBack<T> callback){
		DataSource dateSource = initializer.getDataSource();
		Connection conn = LocalDataSourceUtils.getConnection(dateSource);
		try {
			return callback.doInStatement(conn.createStatement());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	

}
