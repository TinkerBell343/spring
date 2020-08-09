package org.origin.leon.orm.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

public class OrmUtils {

	public static Object resultSetToObj(Object data,Class<?> clazz){
		ResultSet rs = (ResultSet) data;
		Object obj = null;
		try {
			ResultSetMetaData rsdata = null;
			while(rs.next()) {
				rsdata  = rs.getMetaData();
				obj = clazz.newInstance();
				int count = rsdata.getColumnCount();
				Field[] fields = clazz.getDeclaredFields();
				for(int i=1;i<=count;i++){
					String fieldName = rsdata.getColumnLabel(i);
					for(Field field : fields) {
						if(field.getName().toLowerCase().equals(fieldName.toLowerCase())) {
							Method md = clazz.getDeclaredMethod(getFieldSetter(fieldName),field.getType());
							md.invoke(obj, rs.getString(i));
						}
					}
					
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return obj;
	}
	
	private static String getFieldSetter(String fieldName) {
		return "set"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
	}
	
}
