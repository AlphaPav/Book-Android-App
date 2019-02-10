package  BookApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

/**
 * Servlet implementation class onlogin
 */
@WebServlet("/onlogin")
public class onlogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public onlogin() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// TODO Auto-generated method stub
		
		//Get the information 
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		System.out.println("Get info : " + username +password );
	
		
		JSONObject res = new JSONObject();
		
		// Judge if valid openID
		if(username != null && username!=""  && password !=null && password!= "") {
			try {
				User user = User.getUserByUsername(username);
				if (user==null)
				{
					res.put("error", "not exist username");
				}else
				{
					if(user.getPassword().equals(password))
					{
						// save it to sessions
						HttpSession session = request.getSession();
						// set the user's username
						session.setAttribute("username", username);
						session.setAttribute("login", "yes");
						session.setMaxInactiveInterval(10 * 60);
						
						res.put("success", "OK");	

					}else {
						res.put("error", "password incorrect");
					}
				}
				
			}catch (Exception e) {
				// TODO: handle exception
				res.put("error", e.toString());
				e.printStackTrace();
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
