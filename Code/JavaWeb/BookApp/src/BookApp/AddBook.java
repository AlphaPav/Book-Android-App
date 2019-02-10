package BookApp;


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

/**
 * Servlet implementation class AddBook
 */
@WebServlet("/AddBook")
public class AddBook extends HttpServlet {
	private static final long serialVersionUID = 1L;
	/*
	 public static void main(String[] args) throws ClientProtocolException, IOException {
		RetrieveDocumentByURL document= new RetrieveDocumentByURL("https://api.douban.com/v2/book/isbn/:9787212058937");
		System.out.println( document.book.getAuthor());
	 }
	*/
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddBook() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		JSONObject res = new JSONObject();
		boolean login = check.checkLogin(request);
		if(login) {
			res.put("login", "OK");	
			System.out.println("Get request add book");
			
			try {
				String ISBN = request.getParameter("ISBN");
				String CurrentImgPath =request.getParameter("CurrentPicture");
				String BookType =request.getParameter("BookType");
				String SellPrice = request.getParameter("SellPrice");
				String Description = request.getParameter("Description");
				String Username = request.getParameter("Username");
				if(ISBN==null)
				{
					throw new Exception("ISBN为空");	
				}
				if(Description==null)
				{
					throw new Exception("Description为空指针");
				}
				if(BookType==null)
				{
					throw new Exception("BookType为空");	
				}
				else
				{   
					int i;
					System.out.println(BookType);
					for(i=0;i<BookInfo.ValidBookTypes.length;i++)
					{
						if(BookInfo.ValidBookTypes[i].equals(BookType))
						{
							break;
						}
					}
					if(i==BookInfo.ValidBookTypes.length)
					{
						throw new Exception("BookType不合法");	
					}
				}
				
				BookInfo newbook = new BookInfo(Username,ISBN,CurrentImgPath,BookType,SellPrice,Description);
				String bookUrl= "https://api.douban.com/v2/book/isbn/:"+ISBN;
		        if( newbook.RetrieveDocumentByURL(bookUrl) ==true)
		        {
		        	System.out.println("Successful to get the information of Book: " + ISBN);
		        	if (newbook.AddBookToDB())
		        	{
						res.put("success", "OK");
		        	}else
			        {
			        	throw new Exception("添加书本到数据库失败");	
			        }
		        }else
		        {
		        	throw new Exception("从ISBN获取数据失败");	
		        }
			}catch (Exception e) {
				// TODO: handle exception
				res.put("error", e.toString());
			}
		}
		//define the response
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/json; charset=utf-8");
		
		//return the session ID
		
		PrintWriter out = response.getWriter();
		System.out.println(res.toString());
		out.write(res.toString());
		out.flush();
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
