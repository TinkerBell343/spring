package org.origin.leon.orm.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.origin.leon.orm.core.Executor;
import org.origin.leon.orm.core.StatementCallBack;
import org.origin.leon.orm.util.OrmUtils;

public class Query implements StatementCallBack<Object> {
	
	private String sql;
	
	private Executor executor;
	
	public Object doInStatement(Statement stmt) {
		if(sql == null || sql.isEmpty()){
			return null;
		}
		try {
			ResultSet rs = stmt.executeQuery(sql);
			ResultSetMetaData metaData = null;
			while(rs.next()){
				metaData = rs.getMetaData();
			}
			return metaData;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Object query(){
		return executor.execute(this);
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public Executor getExecutor() {
		return executor;
	}

	public void setExecutor(Executor executor) {
		this.executor = executor;
	}
	
	

}
