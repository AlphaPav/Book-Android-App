package  BookApp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;


import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class DB {
	public final static String url = "jdbc:mysql://127.0.0.1:3306/book_mysql?serverTimezone=UTC&&useSSL=false&&characterEncoding=gbk"; // database Url
	public final static String userName = "root";
	public final static String password = "";
	public final static String userTableName = "Table_User";
	public final static String bookTableName = "Table_Book";
    public static Connection getConnection()
    {
    	Connection connection = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			connection = (Connection)DriverManager.getConnection(url, userName, password);
			return connection;
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.toString());
		}
		return null;
		
    }
    
    public static String resultSetToJson(ResultSet rs) throws SQLException,JSONException  
    {  
    	System.out.println("resultSetToJson");
       // json数组  
       JSONArray array = new JSONArray();  
        
       // 获取列数  
       ResultSetMetaData metaData = rs.getMetaData();  
       int columnCount = metaData.getColumnCount();  
        
       // 遍历ResultSet中的每条数据  
        while (rs.next()) {  
            JSONObject jsonObj = new JSONObject();  
             
            // 遍历每一列  
            for (int i = 1; i <= columnCount; i++) {  
                String columnName =metaData.getColumnLabel(i);  
                String value = rs.getString(columnName);  
                jsonObj.put(columnName, value);  
                // System.out.println("\n"+value);
            }
            array.add(jsonObj);
        }  
        
       return array.toString();  
    }  
    
    
}
