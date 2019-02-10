package  BookApp;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

/**
 * Servlet implementation class ModifyBook
 */
@WebServlet("/ModifyBook")
public class ModifyBook extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ModifyBook() {
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
			System.out.println("Get request modify book");
			try {
				int BookID=Integer.valueOf(request.getParameter("BookId")).intValue();
				String ISBN = request.getParameter("ISBN");
				String CurrentImgPath =request.getParameter("CurrentPicture");
				String BookType =request.getParameter("BookType");
				String SellPrice = request.getParameter("SellPrice");
				String Description = request.getParameter("Description");
				String _username =  request.getParameter("Username");
				System.out.println("\nBookID:"+BookID+"\nISBN:"+ ISBN + "\nCurrentPicturePath:"+ CurrentImgPath
						    + "\nBookType:"+ BookType + "\nSellPrice:"+SellPrice+ "\nDescription:"+Description);
				
				if(ISBN==null)
				{
					throw new Exception("ISBN为空指针");	
				}
				if(Description==null)
				{
					throw new Exception("Description为空指针");
				}
				if(BookType==null)
				{
					throw new Exception("BookType为空指针");	
				}else
				{   
					int i;
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
				
		        if( BookInfo.UpdateBookToDB(_username, BookID, ISBN, CurrentImgPath, BookType, SellPrice,Description))
		        {
					res.put("success", "OK");
		        }else
		        {
		        	throw new Exception("数据库更新失败");	
		        }
			}catch (Exception e) {
				// TODO: handle exception
				res.put("error", e.toString());
			}
		}		
		//define the response
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/json; charset=utf-8");
		System.out.println(res.toString());
		response.getWriter().append(res.toString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
