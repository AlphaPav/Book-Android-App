package  BookApp;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

/**
 * Servlet implementation class DeleteBook
 */
@WebServlet("/DeleteBook")
public class DeleteBook extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteBook() {
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
			System.out.println("Get request delete book");
			try {
				int BookID=Integer.valueOf(request.getParameter("BookId")).intValue();
				String _username =  request.getParameter("Username");
				
				System.out.println("\nBookID:"+BookID);
		        if( BookInfo.DeleteBookFromDB(_username, BookID))
		        {
		        	System.out.println("delete success");
					res.put("success", "OK");
		        }else
		        {
		        	throw new Exception("Êý¾Ý¿âÉ¾³ýÊ§°Ü");	
		        }
			}catch (Exception e) {
				// TODO: handle exception
				res.put("error", e.toString());
			}
		}		
		//define the response
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/json; charset=utf-8");
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
