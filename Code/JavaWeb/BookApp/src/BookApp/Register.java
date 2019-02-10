package BookApp;



import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

/**
 * Servlet implementation class verifyCode
 */
@WebServlet("/Register")
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Register() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String email = request.getParameter("email");
		if(email.equals("")) {
			email = null;
		}
		String username = request.getParameter("username");
		if(username.equals("")) {
			username = null;
		}
		String password = request.getParameter("password");
		if(password.equals("")) {
			password = null;
		}

		JSONObject res = new JSONObject();
		if(username!=null) {

			if(email == null ) {
				res.put("error", "邮箱不能为空！");
			}
			else if(password == null) {
				res.put("error", "密码不能为空！");
			}
			else {
				try {
					if (User.isExist(username)==false)
					{
						User user = new User(username, password, email);
						user.InsertUserToDB();
						res.put("success", "OK");
						HttpSession session = request.getSession();
						session.setAttribute("username", username);
					}else {
						res.put("error", "用户名已经被注册");
					}
				}catch (Exception e) {
					// TODO: handle exception
					res.put("error", e.toString());
				}
			}
		}else {
			res.put("error", "用户名不能为空！");
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
