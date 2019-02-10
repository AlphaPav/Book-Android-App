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
 * Servlet implementation class verifyCode
 */
@WebServlet("/verifyCode")
public class verifyCode extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public verifyCode() {
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
		String code = request.getParameter("code");
		if(code.equals("")) {
			code = null;
		}
		
		//JSONObject
		//return 
		// error:"", login��"", email:""
		JSONObject res = new JSONObject();

		HttpSession session = request.getSession();
		if(email == null || email == "") {
			res.put("error", "���䲻��Ϊ�գ�");
		}
		else if(check.checkZjuEmail(email) == false) {
			res.put("error", "�����ʽ�����������䣡");
		}
		else if(code == null || code == "") {
			res.put("error", "��֤�벻��Ϊ�գ�");
		}
		else {
			String tempEmail = (String)session.getAttribute("tempEmail");
			String tempCode = (String)session.getAttribute("tempVerifyCode");
			
			if(!tempEmail.equals(email)) {
				res.put("error", "�������");
			}
			else if(!tempCode.equals(code)) {
				System.out.println("Code: " + tempCode);
				System.out.println("InputCode:" + code);
				res.put("error", "��֤�����");
			}
			else {
				res.put("success", "OK");
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
