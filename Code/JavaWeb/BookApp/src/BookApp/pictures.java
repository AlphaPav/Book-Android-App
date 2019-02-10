package  BookApp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class pictures
 */
@WebServlet("/pictures")
public class pictures extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public pictures() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		//Get the information 
		String pic_name = request.getParameter("pic");
		if(pic_name == null || pic_name.equals("") || pic_name.equals("undefined")) {
			return;
		}
		
		// get save path
		String savePath = this.getServletContext().getRealPath("/WEB-INF/upload");
		System.out.println("Get " + savePath);
		response.setHeader("Access-Control-Allow-Origin", "*");
		File file = new File(savePath+"/"+pic_name);
		if(file.exists()) {
			FileInputStream fis = new FileInputStream(file);
	        int file_size = fis.available();
	        if(file_size > 0) {
	        	byte[] buff = new byte[file_size];
	            fis.read(buff);
	            fis.close();
	            
	            // response
	            response.setContentType("image/*");
	            OutputStream out = response.getOutputStream();
	            out.write(buff);
	            out.close();
	        }
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
