package org.origin.leon.orm.util;

import java.lang.reflect.Field;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

public class OrmUtils {

	public static Object resultSetToObj(Object data,Class<?> clazz){
		ResultSetMetaData rsdata = (ResultSetMetaData) data;
		Object obj = null;
		try {
			obj = clazz.newInstance();
			int count = rsdata.getColumnCount();
			for(int i=1;i<=count;i++){
				String fieldName = rsdata.getColumnLabel(i);
				Field field = clazz.getDeclaredField(fieldName.toLowerCase());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
		return obj;
	}
	
	private static String formatColumnName(String name){
		String head = name.substring(0,1).toLowerCase();
		return head + name.substring(1);
	}
}
