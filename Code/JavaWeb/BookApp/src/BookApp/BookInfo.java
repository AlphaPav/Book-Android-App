package BookApp;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class BookInfo {

	private static String tableName = DB.bookTableName;
	
	public static String[] ValidBookTypes={"������","��ý��","������", "������", "������", "��ѧ��",
			"����������ѧ���ѧ��", "��Դ������߷�����", "��е�������","��������ľ��", "�������Զ�����", "���պ��������װ����","�������", 
			"��Ϣ��", "������", "ũѧ��", "����ʳƷ��","ҽѧ��", "ҩѧ��", "���������", "������"};
	 private String BookID;
	 private String Username;
	 private String ISBN;
	 private String CurrentImg;
	 private String BookType;
	 private String SellPrice;
	 private String  Description;
	 
	  private String Title;
	  private String Author;
	  private String OriPrice;
	  private String Publisher;
	 
	  private String Summary;
	 
	  private String OriImg;
	  
	  private String Catalog;
	  
	  public BookInfo(String _Username,String _ISBN, String _CurrentImgPath,String _BookType, String _SellPrice, String _Description) {
		  Username= _Username;
		  ISBN= _ISBN;
		  CurrentImg=_CurrentImgPath;
		  BookType=_BookType;
		  SellPrice= _SellPrice;
		  Description=_Description;
		  if(_Description.length()>10000)
		  {
			  System.out.println("Description.length()>10000");
			  Description=_Description.substring(0,9999);
			  System.out.println(Description);
		  }
	  }
	  public static String GetUsernameByBookID(int _BookID)
	  {
		  String _Username;
		  try {
			  Connection connection= DB.getConnection();
				if(connection!=null)
				{
					try {
						ResultSet resultSet;
						String sql0 = "select Username from " + tableName + " where BookID = ?";
						System.out.println("Begin sql : " + sql0);
						PreparedStatement pS = connection.prepareStatement(sql0);
						pS.setInt(1, _BookID);
						resultSet = pS.executeQuery();
						if(resultSet.next()) {//todo: check only one result 
							_Username= resultSet.getString("Username");
							connection.close();
							return _Username;
						}
					} catch (Exception e) {
						connection.close();
						throw new Exception("���ݿ��ѯʧ��");	
					}
				}else
				{
					 throw new Exception("���ݿ�����ʧ��");	
				}
			 
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		  return null;
	  }
	  public static String GetBookFromDB(int _BookID)
	  {
		  try {
			  Connection connection= DB.getConnection();
				if(connection!=null)
				{
					try {
						ResultSet resultSet;
						String sql0 = "select * from " + tableName + " where BookID = ?";
						System.out.println("Begin sql : " + sql0);
						PreparedStatement pS = connection.prepareStatement(sql0);
						pS.setInt(1, _BookID);
						resultSet = pS.executeQuery();
						String resultStr= DB.resultSetToJson(resultSet);
			
					    connection.close();
					    return resultStr;
					} catch (Exception e) {
						connection.close();
						throw new Exception("���ݿ��ѯʧ��");	
					}
				}else
				{
					 throw new Exception("���ݿ�����ʧ��");	
				}
			 
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		  return null;
	  }
	  
	  public static String SearchBookFromDB(String _Username,int _IsMyBook, String _BookType,String _Keyword )
	  {
		  try {
			  if(User.isExist(_Username))
			  {
				  Connection connection= DB.getConnection();
					if(connection!=null)
					{
						ResultSet resultSet;
						if(_IsMyBook==1) //search my book
						{
							if(_Keyword=="") //no keyword
							{
								if(_BookType=="") //no type
								{
									
									String sql = "select * from " + tableName + " where Username = ?";
									System.out.println("Begin sql : " + sql);
									PreparedStatement pS = connection.prepareStatement(sql);
									pS.setString(1, _Username);
									resultSet = pS.executeQuery();
								}else //search with type
								{
									String sql = "select * from " + tableName + " where Username = ? and BookType = ?";
									System.out.println("Begin sql : " + sql);
									PreparedStatement pS = connection.prepareStatement(sql);
									pS.setString(1, _Username);
									pS.setString(2, _BookType);
									resultSet = pS.executeQuery();
								}
							}else // with keyword
							{
								if(_BookType=="") //no type
								{
									
									System.out.println("mybook=1 keyword type ");
									String sql = "select * from " + tableName + " where Username = ? and concat(Title,Author,Publisher,Summary,Catalog,BookType,Description) like '%%" + _Keyword+ "%%'";
									System.out.println("Begin sql : " + sql);
									//String sql = "select * from " + tableName + " where Username = ? and concat(Title) like '%%" + _Keyword+ "%%'";
									PreparedStatement pS = connection.prepareStatement(sql);
									pS.setString(1, _Username);
									
									resultSet = pS.executeQuery();
								}else //search with type
								{
									String sql = "select * from " + tableName + " where Username = ? and BookType = ? and concat(Title,Author,Publisher,Summary,Catalog,BookType,Description) like '%%" + _Keyword+ "%%'";
									System.out.println("Begin sql : " + sql);
									PreparedStatement pS = connection.prepareStatement(sql);
									pS.setString(1, _Username);
									pS.setString(2, _BookType);
								
									resultSet = pS.executeQuery();
								}
							}
							
						}else //search all book
						{
							if(_Keyword=="") //no keyword
							{
								if(_BookType=="") //no type
								{
									String sql = "select * from " + tableName;
									System.out.println("Begin sql : " + sql);
									PreparedStatement pS = connection.prepareStatement(sql);
								
									resultSet = pS.executeQuery();
								}else //search with type
								{
									String sql = "select * from " + tableName + " where BookType = ?";
									System.out.println("Begin sql : " + sql);
									PreparedStatement pS = connection.prepareStatement(sql);
									pS.setString(1, _BookType);
									resultSet = pS.executeQuery();
								}
								
							}else //with keyword
							{
								if(_BookType=="") //no type
								{
									String sql = "select * from " + tableName + " where concat(Title,Author,Publisher,Summary,Catalog,BookType,Description) like '%%" + _Keyword+ "%%'";
									System.out.println("Begin sql : " + sql);
									PreparedStatement pS = connection.prepareStatement(sql);
									
									resultSet = pS.executeQuery();
								}else //search with type
								{
									String sql = "select * from " + tableName + " where BookType = ? and concat(Title,Author,Publisher,Summary,Catalog,BookType,Description) like '%%" + _Keyword+ "%%'";
									System.out.println("Begin sql : " + sql);
									PreparedStatement pS = connection.prepareStatement(sql);
									pS.setString(1, _BookType);
									
									resultSet = pS.executeQuery();
								}
							}
							
						}
						
					    String resultStr= DB.resultSetToJson(resultSet);
					    // System.out.println(resultStr);
					    connection.close();
					    return resultStr;

					}else
					{
						System.out.println("���ݿ�����ʧ��");
						throw new Exception("���ݿ�����ʧ��");	
					}
				  
			  }else {
				  throw new Exception("�û������ڣ�����ע��");	
			  }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		  return null;
	  }
	  
	 public static Boolean DeleteBookFromDB(String _Username,int _BookID)
	 {
		  try {
			  if(User.isExist(_Username))
				{
				  Connection connection= DB.getConnection();
					if(connection!=null)
					{
						ResultSet resultSet;
						String sql0 = "select Username from " + tableName + " where BookID = ?";
						System.out.println("Begin sql :" + sql0);
						try {
							PreparedStatement pS = connection.prepareStatement(sql0);
							pS.setInt(1, _BookID);
							resultSet = pS.executeQuery();
						}catch (Exception e) {
							// TODO: handle exception
							System.out.println(e.toString());
							connection.close();
							throw new Exception("��֤Username��BookID������ϵʱ��ѯ���ݿ�ʧ��");
						}
						
						if(resultSet.next()) {
							if(_Username.equals(resultSet.getString("Username")))
							{
								
							}else
							{
								connection.close();
								throw new Exception("��Ȩ��ɾ���鼮��Username����BookID��ӵ����");
							}
						}else {
							connection.close();
							throw new Exception("��֤Username��BookID������ϵʱ��ѯ���ݿ�ʱΪ��");
						}

					    
						String sql = "delete from " + tableName + " where BookID = ?";
						System.out.println("Begin sql :" + sql);
						PreparedStatement preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setInt(1, _BookID);
					
						int res;
						res = preparedStatement.executeUpdate();
						if(res <= 0) {
							connection.close();
							throw new Exception("ɾ��ʧ��");	
						}else 
						{
							connection.close();
							return true;
						}
					}else
					{
						 throw new Exception("���ݿ�����ʧ��");	
					}
				}else
				{
					 throw new Exception("�û������ڣ�����ע��");	
				}
			  
			 
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		 return false;
	 }
	
	  public static Boolean UpdateBookToDB(String _Username, int _BookID,String _ISBN,String _CurrentImgPath, String _BookType,String _SellPrice, String _Description)
	  {
		  try {
			  if(User.isExist(_Username))
				{
				  Connection connection= DB.getConnection();
					if(connection!=null)
					{
						ResultSet resultSet;
						String sql0 = "select Username from " + tableName + " where BookID = ?";
						System.out.println("Begin sql :" + sql0);
						try {
							PreparedStatement pS = connection.prepareStatement(sql0);
							pS.setInt(1, _BookID);
							resultSet = pS.executeQuery();
						}catch (Exception e) {
							// TODO: handle exception
							System.out.println(e.getStackTrace());
							connection.close();
							System.out.println("��֤Username��BookID������ϵʱ��ѯ���ݿ�ʧ��");
							throw new Exception("��֤Username��BookID������ϵʱ��ѯ���ݿ�ʧ��");
						}
						
						if(resultSet.next()) {
							if(_Username.equals(resultSet.getString("Username")))
							{
								
							}else
							{
								connection.close();
								System.out.println("��Ȩ�޸����鼮��Ϣ��Username����BookID��ӵ����");
								throw new Exception("��Ȩ�޸����鼮��Ϣ��Username����BookID��ӵ����");
							}
						}else {
							connection.close();
							System.out.println("��֤Username��BookID������ϵʱ��ѯ���ݿ�ʱΪ��");
							throw new Exception("��֤Username��BookID������ϵʱ��ѯ���ݿ�ʱΪ��");
						}
						
						 if(_Description.length()>10000)
						  {
							  System.out.println("Description.length()>10000");
							  _Description=_Description.substring(0,9999);
							  System.out.println(_Description);
						  }
						String sql = "update " + tableName + " set ISBN = ? , CurrentImg = ?, BookType = ?, SellPrice = ?, Description = ? where BookID = ?";
						PreparedStatement preparedStatement = connection.prepareStatement(sql);
						preparedStatement.setString(1, _ISBN);
						preparedStatement.setString(2, _CurrentImgPath);
						preparedStatement.setString(3, _BookType);
						preparedStatement.setString(4, _SellPrice);
						preparedStatement.setString(5, _Description);
						preparedStatement.setInt(6, _BookID);
						int res;
						res = preparedStatement.executeUpdate();
						if(res <= 0) {
							connection.close();
							System.out.println("���ݸ���ʧ��");
							throw new Exception("���ݸ���ʧ��");	
						}else 
						{
							connection.close();
							return true;
						}
					}else
					{ System.out.println("���ݿ�����ʧ��");
						 throw new Exception("���ݿ�����ʧ��");	
					}
				}else
				{ System.out.println("�û������ڣ�����ע��");
					 throw new Exception("�û������ڣ�����ע��");	
				}
			  
			 
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		
		  return false;
	  }
	  
	public boolean AddBookToDB()
	  {
		  try {
			if(User.isExist(Username))
			{
				Connection connection= DB.getConnection();
				if(connection!=null)
				{
					int res;
					String sql="insert into " + tableName +
			                "(Username,Title,ISBN,Author,OriPrice,Publisher,OriImg,Summary,Catalog,CurrentImg,BookType,SellPrice,Description) "+
			                "values(?,?,?,?,?,?,?,?,?,?,?,?,?)";

					
					PreparedStatement preparedStatement = connection.prepareStatement(sql);
					preparedStatement.setString(1, Username);
					preparedStatement.setString(2, Title);
					preparedStatement.setString(3, ISBN);
					preparedStatement.setString(4, Author);
					preparedStatement.setString(5, OriPrice);
					preparedStatement.setString(6, Publisher);
					preparedStatement.setString(7, OriImg);
					preparedStatement.setString(8, Summary);
					preparedStatement.setString(9, Catalog);
					preparedStatement.setString(10, CurrentImg);
					preparedStatement.setString(11, BookType);
					preparedStatement.setString(12, SellPrice);
					preparedStatement.setString(13, Description);
					res = preparedStatement.executeUpdate();
					if(res <= 0) {
						connection.close();
						System.out.println("���ݲ���ʧ��");
						throw new Exception("���ݲ���ʧ��");	
					}else 
					{
						ResultSet resultSet;
						String sql2 = "select BookID from " + tableName + " where Username = ? and CurrentImg = ?";
						System.out.println("Begin sql :" + sql2);
						PreparedStatement pS = connection.prepareStatement(sql2);
						pS.setString(1, Username);
						pS.setString(2, CurrentImg);
						resultSet = pS.executeQuery();
						// System.out.println(resultSet);
						if(resultSet.next()) {//todo: check only one result 
							BookID = resultSet.getString("BookID");
							connection.close();
							return true;
						}else
						{
							connection.close();
							System.out.println("�޷����BookID,����ʧ��");
							throw new Exception("�޷����BookID,����ʧ��");	
						}
					}
				
				}else {
					System.out.println("��������ʧ��");
					throw new Exception("��������ʧ��");	
				}
			 }else {
				 System.out.println("�û������ڣ�����ע��");
				 throw new Exception("�û������ڣ�����ע��");	
			 }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
		  return false;

	  }
	 
	  public Boolean RetrieveDocumentByURL(String url) throws ClientProtocolException, IOException{
		   try{
			   DefaultHttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				HttpResponse response = client.execute(get);
				HttpEntity entity = response.getEntity();
				String result = EntityUtils.toString(entity, "GBK");
				JSONObject jsonobj = JSONObject.fromObject(result);
				//  System.out.println(jsonobj);
				JSONArray authoerArray=jsonobj.getJSONArray("author");
				String author ="";
				for (int i=0;i<authoerArray.size();i++){
			        if(i==0)
			        {
			        	author += authoerArray.getString(i);
			        }else {
			        	author += " " +authoerArray.getString(i);
			        }
				}
				// System.out.println(author);
			        
				JSONObject ImageObj=jsonobj.getJSONObject("images");
				String middeimg=ImageObj.getString("medium");
				// System.out.println(middeimg);
			        
				String catalog=jsonobj.getString("catalog");
				// System.out.println(catalog);
			        
				String title=jsonobj.getString("title");
				//   System.out.println(title);
			        
				String publisher=jsonobj.getString("publisher");
				//    System.out.println(publisher);
			        
				String price =jsonobj.getString("price");
				//   System.out.println(price);
			    price= price.split("Ԫ")[0];
			    // System.out.println(price);
			    
			    
				String summary =jsonobj.getString("summary");
				// System.out.println(summary);
				if(title.length()>100)
				  {
					title=title.substring(0,99);
	
				  }
				if(publisher.length()>100)
				  {
					publisher=publisher.substring(0,99);
	
				  }
				if(price.length()>50)
				  {
					price=price.substring(0,49);
	
				  }
				if(author.length()>50)
				  {
					author=author.substring(0,49);
	
				  }
				if(summary.length()>10000)
				  {
					System.out.println("summary.length()>10000");
					summary=summary.substring(0,9999);

				  }
				if(catalog.length()>10000)
				  {System.out.println("catalog.length()>10000");
					catalog=catalog.substring(0,9999);
				  }
				setAuthor(author);
				setCatalog(catalog);
				setImagePath(middeimg);
				setPrice(price);
				setPublisher(publisher);
				setSummary(summary);
				setTitle(title);
				return true;
		   }catch (Exception e) {
			// TODO: handle exception
			   System.out.println(e.toString());
		   }
		   return false;
	 }
	  
	  
	  
	  /**
	   * @return the imagePath
	   */
	  public String getImagePath() {
	    return this.OriImg;
	  }
	 
	  /**
	   * @param imagePath
	   *            the imagePath to set
	   */
	  public void setImagePath(String imagePath) {
	    this.OriImg = imagePath;
	  }
	 
	 
	 
	  /**
	   * @return the title
	   */
	  public String getTitle() {
	    return this.Title;
	  }
	 
	  /**
	   * @param title
	   *            the title to set
	   */
	  public void setTitle(String title) {
	    this.Title = title;
	  }
	 
	  /**
	   * @return the author
	   */
	  public String getAuthor() {
	    return this.Author;
	  }
	 
	  /**
	   * @param author
	   *            the author to set
	   */
	  public void setAuthor(String author) {
	    this.Author = author;
	  }
	 
	  /**
	   * @return the price
	   */
	  public String getPrice() {
	    return this.OriPrice;
	  }
	 
	  /**
	   * @param price
	   *            the price to set
	   */
	  public void setPrice(String price) {
	    this.OriPrice = price;
	  }
	 

	 
	  /**
	   * @return the publisher
	   */
	  public String getPublisher() {
	    return this.Publisher;
	  }
	 
	  /**
	   * @param publisher
	   *            the publisher to set
	   */
	  public void setPublisher(String publisher) {
	    this.Publisher = publisher;
	  }
	 

	  /**
	   * @return the summary
	   */
	  public String getSummary() {
	    return this.Summary;
	  }
	 
	  /**
	   * @param summary
	   *            the summary to set
	   */
	  public void setSummary(String summary) {
	    this.Summary = summary;
	  }
	  
	  /**
	   * @return the catalog
	   */
	  public String getCatalog() {
	    return this.Catalog;
	    
	    
	  }
	 
	  /**
	   * @param summary
	   *            the summary to set
	   */
	  public void setCatalog(String catalog) {
	    this.Catalog = catalog;
	  }
	 
	  
	  
		 
	  public String getBookID() {
		return this.BookID;
	   }
	  public void setBookID(String BookID) {
		  this.BookID =BookID;
	   }
	  
	  public String getUsername() {
		return this.Username;
	   }
	  public void setUsername(String Username) {
		  this.Username =Username;
	   }
	  
	  public String getISBN() {
		return this.ISBN;
	   }
	  public void setISBN(String ISBN) {
		  this.ISBN =ISBN;
	   }
	  
	  
	  public String getCurrentImg() {
		return this.CurrentImg;
	   }	  
	  public void setCurrentImg(String CurrentImg) {
		  this.CurrentImg =CurrentImg;
	   }
	  
	  public String getBookType() {
		return this.BookType;
	   }	  
	  public void setBookType(String BookType) {
		  this.BookType =BookType;
	   }
	  
	  
	  public String getSellPrice() {
		return this.SellPrice;
	   }	  
	  public void setSellPrice(String SellPrice) {
		  this.SellPrice =SellPrice;
	   }
}
