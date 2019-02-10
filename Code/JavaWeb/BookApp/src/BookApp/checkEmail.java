package  BookApp;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.Date;
import java.util.Properties;
import java.util.Random;


/**
 * Servlet implementation class checkEmail
 */
@WebServlet("/checkEmail")
public class checkEmail extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public checkEmail() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//Get the information 
		String email = request.getParameter("email");
		
		if(email.equals("")) {
			email = null;
		}
		System.out.println("Get email : " + email);
		
		// trans to json
		JSONObject res = new JSONObject();


			try {
				if(email == null || email == "") {
					res.put("error", "邮箱不能为空！");
				}
				else if(check.checkZjuEmail(email) == false) {
					res.put("error", "邮箱格式错误，请检查邮箱！");
				}
				else {
					String varifyCode = sendEmail(email);
					System.out.println("发送邮件成功");
					
					HttpSession session = request.getSession();
					session.setAttribute("tempEmail", email);
					session.setAttribute("tempVerifyCode", varifyCode);
					res.put("success", "OK");
				}
			}catch (Exception e) {
				// TODO: handle exception
				res.put("error", e.toString());
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
	
	private static String sendEmail(String emailAddress) throws Exception {
		String myEmail = "ZjuBookInfo@163.com";
		String myEmailPassword = "ItemZheng0926";
//		String myEmail = "alphapav@163.com";
//		String myEmailPassword = "xiechuchu123";
		
		String myEmailSMTPHost = "smtp.163.com";
		
		Properties prop = new Properties();
		prop.setProperty("mail.transport.protocol", "smtp");	// SMTP协议
		prop.setProperty("mail.smtp.host", myEmailSMTPHost);	// SMTP host address
		prop.setProperty("mail.smtp.auth", "true");
		prop.setProperty("mail.smtp.socketFactory.port", "465");
		prop.setProperty("mail.debug", "true");
		prop.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); 
		prop.put("mail.smtp.starttls.enable", "true");
		// form verify code
		int length = 6;
		Random random = new Random();
		
		String str="zxcvbnmlkjhgfdsaqwertyuiopQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
		// 26 + 26 + 10
		String varifyCode = "";
		for(int i = 0; i < length; i++) {
			int ran = random.nextInt(str.length());
			varifyCode += str.charAt(ran);
		}
		// end form verify code
		
		System.out.println("Form verify code susccessfully: " + varifyCode);
		
		Session session = Session.getInstance(prop);
		session.setDebug(true);
		
		MimeMessage message = formVerifyEmail(session, myEmail, emailAddress, varifyCode);
		
		Transport transport = session.getTransport();
		transport.connect(myEmail, myEmailPassword);
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();
		
		return varifyCode;
	}
	
	private static MimeMessage formVerifyEmail(Session session, String sendEmail, String receiveEmail, String varifyCode) throws Exception{
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(sendEmail, "Zju Book Information", "UTF-8"));
		message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiveEmail, "app用户", "UTF-8"));
		message.setSubject("浙大旧书出售信息APP验证邮箱", "UTF-8");
		message.setContent("欢迎使用浙大旧书出售信息APP，您的验证码为： " + varifyCode + "。如非本人操作，请检查账户安全。", "text/html;charset=UTF-8");
		message.setSentDate(new Date());
		message.saveChanges();
		return message;
	}
	
	public static void main(String[] args)  
	{
//		try {
//			System.out.println(sendEmail("3160102332@zju.edu.cn"));
//		}catch (Exception e) {
//			// TODO: handle exception
//		}
	}

}
