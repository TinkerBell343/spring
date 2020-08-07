package org.origin.leon.orm;

import org.origin.leon.orm.db.SqlTemplate;
import org.origin.leon.orm.model.TestQuery;

/**
 * Hello world!
 *
 */
public class AppMain 
{
    public static void main( String[] args )
    {
    	SqlTemplate template = new SqlTemplate();
    	System.out.println(template.queryForObj("select now() time",TestQuery.class));
    }
}
