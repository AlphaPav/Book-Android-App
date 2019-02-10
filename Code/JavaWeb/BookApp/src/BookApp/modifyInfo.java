package  BookApp;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

/**
 * Servlet implementation class modifyInfo
 */
@WebServlet("/modifyInfo")
public class modifyInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public modifyInfo() {
        super();
        // TODO Auto-generated constructor stub
        
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		// return 
		// error: "", login: ""  success:"", QQ, WX, phone
		JSONObject res = new JSONObject();
		Boolean login = check.checkLogin(request);
		if(login) {
			HttpSession session = request.getSession();
			System.out.println("Get request modify user info");
			String _username = request.getParameter("Username");
			if(_username != null && _username !="") {
				res.put("login", "OK");
				String QQ = request.getParameter("QQ");
				String phone = request.getParameter("Phone");
				String WX = request.getParameter("WX");
				if(QQ.equals("")) {
					QQ = null;
				}
				if(WX.equals("")) {
					WX = null;
				}
				if(phone.equals("")) {
					phone = null;
				}
				
				
				if(!check.checkQQ(QQ)) {
					System.out.println("Get QQ: " + QQ);
					res.put("error", "QQ号格式有误！");
				}
				else if(!check.checkWX(WX)) {
					res.put("error", "WX号格式有误！");
				}
				else if(!check.checkPhone(phone)) {
					res.put("error", "手机号格式有误！");
				}
				else {
					// UserManage update the information
					try {					
						
						User user =User.getUserByUsername(_username);
						user.updateUserInfo(QQ, WX, phone);
						res.put("success", "OK");
					}catch (Exception e) {
						// TODO: handle exception
						res.put("error", e.toString());
					}
				}
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
