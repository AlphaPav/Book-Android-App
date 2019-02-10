package  BookApp;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

/**
 * Servlet implementation class SelectBook
 */
@WebServlet("/SelectBook")
public class SelectBook extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SelectBook() {
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
			System.out.println("Get request select book");
			try {
				int IsMyBook=Integer.valueOf(request.getParameter("IsMyBook")).intValue();
				String BookType =request.getParameter("BookType");
				String Keyword = request.getParameter("Keyword");
				String _username =  request.getParameter("Username");
				
				System.out.println("\nIsMyBook:"+IsMyBook
						    + "\nBookType:"+ BookType + "\nKeyword:"+Keyword);
				
				if(IsMyBook!=0&&IsMyBook!=1)
				{
					throw new Exception("IsMyBook不是0也不是1，不合法");	
				}
				if(BookType==null)
				{
					throw new Exception("BookType为空");	
				}else if(BookType!="")
				{   
					int i;
					for(i=0;i<BookInfo.ValidBookTypes.length;i++)
					{
						if(BookInfo.ValidBookTypes[i].equals(BookType))
						{
							break;
						}
					}
					if(i==BookInfo.ValidBookTypes.length)
					{
						throw new Exception("BookType不合法");	
					}
				}
				
				String resultStr= BookInfo.SearchBookFromDB(_username, IsMyBook,BookType,Keyword);
		        if( resultStr!=null)
		        {
					res.put("success", "OK");
					res.put("result", resultStr);
					
		        }else
		        {
		        	throw new Exception("数据库查询失败");	
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
	
//	public static void main(String[] args)  
//	{
//		
//
//		
//	}


}
