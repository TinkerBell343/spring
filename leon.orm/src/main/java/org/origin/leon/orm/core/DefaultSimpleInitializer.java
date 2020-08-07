package org.origin.leon.orm.core;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.origin.leon.orm.config.DataSource;

public class DefaultSimpleInitializer {
	
	private static DataSource config = new DataSource();
	
	private volatile static boolean isInitialized = false;
	
	private static DefaultSimpleInitializer instance = null;
	
	private static Logger logger = Logger.getLogger(DefaultSimpleInitializer.class.getName());
	
	private DefaultSimpleInitializer(){
	}
	
	private DefaultSimpleInitializer(Properties properties){
		config.setDbDriverClassName(properties.getProperty("datasource.dbDriver.class"));
		config.setDbUrl(properties.getProperty("datasource.db.url"));
		config.setUserName(properties.getProperty("datasource.db.username"));
		config.setPassword(properties.getProperty("datasource.db.password"));
	}
	
	public static DefaultSimpleInitializer getInstance(Properties properties){
		if(!isInitialized){
			synchronized (properties) {
				isInitialized = true;
				instance = new DefaultSimpleInitializer(properties);
			}
		}else{
			logger.setLevel(Level.ALL);
			logger.info("Initializer is already Initialized.");;
		}
		return instance;
	}
	
	public DataSource getDataSource(){
		return config;
	}

}
