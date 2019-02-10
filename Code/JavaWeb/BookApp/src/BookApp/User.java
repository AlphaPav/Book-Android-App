package  BookApp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



public class User {	
	private static String DBurl = DB.url;
	private static String DBuserName = DB.userName;
	private static String DBpassword = DB.password;
	private static String DBtableName = DB.userTableName;
	
	private String username = null, password= null, email = null, QQ = null, WX = null, phone = null;
	public User(String _username, String _password, String _email)
	{
		username= _username;
		password= _password;
		email=_email;
	}
	
	public User()
	{}
	public String getEmail() {
		return email;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getQQ() {
		if(QQ == null) {
			return "";
		}
		return QQ;
	}
	
	public String getWX() {
		if(WX == null) {
			return "";
		}
		return WX;
	}
	
	public String getPhone() {
		if(phone == null){
			return "";
		}
		return phone;
	}
	
	public boolean InsertUserToDB() throws Exception
	{
		if (username==null || password==null|| email==null) return false;
		
		
		Connection connection = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = (Connection)DriverManager.getConnection(DBurl, DBuserName, DBpassword);
	
			int res;
			String sql = "insert into " + DBtableName + " values(?, ?, ?, null, null, null)";
	
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, password);
			preparedStatement.setString(3, email);
			res = preparedStatement.executeUpdate();

			if(res <= 0) {
				throw new Exception("数据插入失败");	
			}
			
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static String GetUserFromDB(String _username)
	{
		try {
			  Connection connection= DB.getConnection();
				if(connection!=null)
				{
					try {
						ResultSet resultSet;
						String sql0 = "select * from " + DBtableName + " where Username = ?";
						PreparedStatement pS = connection.prepareStatement(sql0);
						pS.setString(1, _username);
						resultSet = pS.executeQuery();
						String resultStr= DB.resultSetToJson(resultSet);
					 
					    connection.close();
					    return resultStr;
					} catch (Exception e) {
						connection.close();
						throw new Exception("数据库查询失败");	
					}
				}else
				{
					 throw new Exception("数据库连接失败");	
				}
			 
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return null;
	}
	
	public void updateEmail(String email) throws Exception{
		String _username = username;
		if(_username == null) {
			throw new Exception("用户唯一标识为空");
		}
		
		if(!check.checkZjuEmail(email)) {
			throw new Exception("邮箱错误");
		}
		
		Connection connection = null;
		Class.forName("com.mysql.cj.jdbc.Driver");
		try {
			connection = (Connection)DriverManager.getConnection(DBurl, DBuserName, DBpassword);
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.toString());
			throw new Exception("数据库连接失败");
		}
		
		String sql = "update " + DBtableName + " set Email = ? where Username = ?";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, email);
			preparedStatement.setString(2, _username);
			preparedStatement.executeUpdate();
			this.email = email;
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getStackTrace());
			throw new Exception("邮箱修改失败");
		}
		
	}
	
	public void updateUserInfo(String QQ, String WX, String phone) throws Exception{
		String _username = username;
		if(_username == null) {
			throw new Exception("用户唯一标识为空");
		}
		if(!check.checkQQ(QQ)) {
			throw new Exception("QQ号错误");
		}
		if(!check.checkWX(WX)) {
			throw new Exception("微信号错误");
		}
		if(!check.checkPhone(phone)) {
			throw new Exception("手机号格式错误");
		}
		
		Connection connection = null;
		Class.forName("com.mysql.cj.jdbc.Driver");
		try {
			connection = (Connection)DriverManager.getConnection(DBurl, DBuserName, DBpassword);
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.toString());
			throw new Exception("数据库连接失败");
		}
		
		String sql = "update " + DBtableName + " set QQ = ? , WX = ?, Phone = ? where Username = ?";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, QQ);
			preparedStatement.setString(2, WX);
			preparedStatement.setString(3, phone);
			preparedStatement.setString(4, _username);
			preparedStatement.executeUpdate();
			
			this.QQ = QQ;
			this.phone = phone;
			this.WX = WX;
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getStackTrace());
			throw new Exception("用户信息更新失败");
		}
		
	}
	
	public static User getUserByUsername(String _username) throws Exception{
	
		Connection connection = null;
		Class.forName("com.mysql.cj.jdbc.Driver");
		try {
			connection = (Connection)DriverManager.getConnection(DBurl, DBuserName, DBpassword);
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.toString());
			throw new Exception("数据库连接失败");
		}
		
		ResultSet resultSet;
		String sql = "select Password,Email, QQ , WX, Phone from " + DBtableName + " where Username = ?";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, _username);
			resultSet = preparedStatement.executeQuery();
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getStackTrace());
			throw new Exception("数据库查询失败");
		}
		
		if(resultSet.next()) {
			User user = new User(_username,resultSet.getString("Password"), resultSet.getString("Email") );
			user.QQ = resultSet.getString("QQ");
			user.WX = resultSet.getString("WX");
			user.phone = resultSet.getString("Phone");

			return user;
		}else
		{
			return null;
		}
	
	}
	
	public static boolean isExist(String _username) throws Exception{
		if(_username == null) {
			throw new Exception("用户唯一标识为空");
		}
		
		Connection connection = null;
		Class.forName("com.mysql.cj.jdbc.Driver");
		try {
			connection = (Connection)DriverManager.getConnection(DBurl, DBuserName, DBpassword);
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.toString());
			throw new Exception("数据库连接失败");
		}
		
		ResultSet resultSet;
		String sql = "select * from " + DBtableName + " where Username = ?";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, _username);
			resultSet = preparedStatement.executeQuery();
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getStackTrace());
			throw new Exception("数据库查询失败");
		}
		
		if(resultSet.next()) {
			connection.close();
			return true;
		}
		if(connection!=null) connection.close();
		return false;
		
	}
	
	/*
	public static void main(String[] args) {
		String xx = "abcdefghijklmnopqrstuvwxyzac";
		try {
			User user = getUseByOpenId(xx);
			user.updateEmail("3160102332@zju.edu.cn");
			user.updateUserInfo("1563078464", "zhengyuhang785", "18868102513");
			System.out.println(user.email);
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.toString());
		}
		
	}
	*/
}
	