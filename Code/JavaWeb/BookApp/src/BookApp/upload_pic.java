package  BookApp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem; 
import org.apache.commons.fileupload.disk.DiskFileItemFactory; 
import org.apache.commons.fileupload.servlet.ServletFileUpload; 

import net.sf.json.JSONObject;

/**
 * Servlet implementation class upload_pic
 */
@WebServlet("/upload_pic")
public class upload_pic extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public upload_pic() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    /* login, error, success, url */
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean login = check.checkLogin(request);
		
		// trans to json
		JSONObject res = new JSONObject();
		if(login) {
			res.put("login", "OK");
			// get save path
			String savePath = this.getServletContext().getRealPath("/WEB-INF/upload");
			System.out.println("Get " + savePath);
			
			File file = new File(savePath);
			// judge directory
			if (!file.exists() && !file.isDirectory()) {
				System.out.println(savePath + " does not exist, need creat!");
				// create directory
				file.mkdir();
			}
			
			// receive file
			DiskFileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			try {
				List<FileItem> list = upload.parseRequest(request);
					
				for (FileItem item : list) {
					//pic extension
					String ext = item.getName().substring(item.getName().lastIndexOf(".")+1);
					// get file name
					String filename = "pic_" + (System.currentTimeMillis()/1000)+"." + ext;
					System.out.println("filename=" + filename);
					res.put("url", filename);
					
					// get bytes
					InputStream in = item.getInputStream();
					byte buffer[] = new byte[1024];
					FileOutputStream output = new FileOutputStream(savePath+ "/" + filename);
					int len = 0;
					while ((len = in.read(buffer)) > 0) {
						output.write(buffer, 0, len);
					}
					in.close();
					output.flush();
					output.close();
					res.put("path", filename);
			}
				
				
			} catch (Exception e) {
				res.put("error", "ÕÕÆ¬ÉÏ´«Ê§°Ü");
			}
			
		}		
		else {
			System.out.println("UNLOGIN");
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
