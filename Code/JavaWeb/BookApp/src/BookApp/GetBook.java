package  BookApp;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

/**
 * Servlet implementation class GetBook
 */
@WebServlet("/GetBook")
public class GetBook extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetBook() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("request : get Book and it's owner");
		
		JSONObject res = new JSONObject();
		boolean login = check.checkLogin(request);
		if(login) {
			res.put("login", "OK");
			try {
				int BookID=Integer.valueOf(request.getParameter("BookID")).intValue();
				
				String resultStr= BookInfo.GetBookFromDB(BookID);
				String Username =BookInfo.GetUsernameByBookID(BookID);
				String userStr=User.GetUserFromDB(Username);
		        if( resultStr!=null&& userStr!=null)
		        {
					res.put("success", "OK");
					res.put("result", resultStr);
					res.put("owner", userStr);
		        }else
		        {
		        	throw new Exception("Êý¾Ý¿â²éÑ¯Ê§°Ü");	
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
