package org.origin.leon.orm.core;

import java.sql.Statement;

public interface StatementCallBack<T> {

	public T doInStatement(Statement stmt);
	
}
