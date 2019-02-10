package BookApp;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

@WebServlet("/GetUser")
public class GetUser extends HttpServlet {

	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GetUser() {
	        super();
	        // TODO Auto-generated constructor stub
	    }
	 
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("request : get  owner info");
		
		JSONObject res = new JSONObject();
		boolean login = check.checkLogin(request);
		if(login) {
			res.put("login", "OK");
			try {
				String _username =  request.getParameter("Username");
				String userStr=User.GetUserFromDB(_username);
		        if(userStr!=null)
		        {
					res.put("success", "OK");
					res.put("user", userStr);
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
