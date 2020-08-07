package org.origin.leon.orm.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.origin.leon.orm.config.DataSource;

public class ResourceUtils {
	
	private static final String DEFAULT_CONFIG_LOCATION = "AppProperties.properties";
	
	private static Properties properities = new Properties();
	
	static{
		InputStream is = DataSource.class.getClassLoader().getResourceAsStream(getConfigLocation());
		try {
			properities.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getConfigLocation(){
		return DEFAULT_CONFIG_LOCATION;
	}
	
	public static String getKey(String key){
		return properities.getProperty(key);
	}
	
	public static Properties getProperties(){
		return properities;
	}
	
}
