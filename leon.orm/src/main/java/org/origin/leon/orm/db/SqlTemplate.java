package org.origin.leon.orm.db;

import org.origin.leon.orm.core.DefaultSimpleInitializer;
import org.origin.leon.orm.core.Executor;
import org.origin.leon.orm.util.OrmUtils;
import org.origin.leon.orm.util.ResourceUtils;

public class SqlTemplate {
	
	private DefaultSimpleInitializer initializer;
	
	private Query query = null;

	public SqlTemplate(){
		initializer = DefaultSimpleInitializer.getInstance(ResourceUtils.getProperties());
		query = new Query();
    	query.setExecutor(new Executor(initializer));
	}
	
	public Object queryForObj(String sql,Class<?> result){
		query.setSql(sql);
		return OrmUtils.resultSetToObj(query.query(),result);
	}
}
