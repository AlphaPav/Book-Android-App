package BookApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class books
 */
@WebServlet("/books")
public class books extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public books() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Get the information 
		String isbn = request.getParameter("isbn");
		System.out.println("Get isbn : " + isbn);
//		String isbn = "9787302274629";
		JSONObject res = new JSONObject();
		if(isbn == null || isbn.equals("")) {
			res.put("error", "isbn error");
			response.getWriter().write(res.toString());
			response.getWriter().flush();
			response.getWriter().close();
		}
		else {
			//the request address
			String url = "https://api.douban.com/v2/book/isbn/" + isbn;
			
			//the request result
			String result = "";
			
			//begin request
			try {
				URL myUrl = new URL(url);
				
				//connect
				HttpURLConnection connection = (HttpURLConnection)myUrl.openConnection();
				
				//setting
				connection.setConnectTimeout(5000); 
				connection.setRequestMethod("GET"); 
				//begin connet
				connection.connect();
				
				int resultCode = connection.getResponseCode();
				if(resultCode == HttpURLConnection.HTTP_OK) {
					//Success
					StringBuffer buffer = new StringBuffer();
					String line = new String();
					
					//Get the response
					BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
					while((line = responseReader.readLine()) != null) {
						buffer.append(line).append("\n");
					}
					responseReader.close();
					//System.out.print("Get response : " + buffer.toString());
					result = buffer.toString();
				}
				//OutputStreamWriter outputStreamWriter = new OutputStreamWriter(response.getOutputStream(), "UTF-8");
				System.out.println(result);
				
				JSONObject jsonobj = JSONObject.fromObject(result);
				//  System.out.println(jsonobj);
				JSONArray authoerArray=jsonobj.getJSONArray("author");
				String author ="";
				for (int i=0;i<authoerArray.size();i++){
			        if(i==0)
			        {
			        	author += authoerArray.getString(i);
			        }else {
			        	author += " " +authoerArray.getString(i);
			        }
				}
				// System.out.println(author);
			        
				JSONObject ImageObj=jsonobj.getJSONObject("images");
				String middeimg=ImageObj.getString("medium");
				// System.out.println(middeimg);
			        
				String catalog=jsonobj.getString("catalog");
				// System.out.println(catalog);
			        
				String title=jsonobj.getString("title");
				//   System.out.println(title);
			        
				String publisher=jsonobj.getString("publisher");
				//    System.out.println(publisher);
			        
				String price =jsonobj.getString("price");
				//   System.out.println(price);
			    price= price.split("Ԫ")[0];
			    // System.out.println(price);
			    
			    
				String summary =jsonobj.getString("summary");
				// System.out.println(summary);
				if(title.length()>100)
				  {
					title=title.substring(0,99);
	
				  }
				if(publisher.length()>100)
				  {
					publisher=publisher.substring(0,99);
	
				  }
				if(price.length()>50)
				  {
					price=price.substring(0,49);
	
				  }
				if(author.length()>50)
				  {
					author=author.substring(0,49);
	
				  }
				if(summary.length()>10000)
				  {
					System.out.println("summary.length()>10000");
					summary=summary.substring(0,9999);

				  }
				if(catalog.length()>10000)
				  {System.out.println("catalog.length()>10000");
					catalog=catalog.substring(0,9999);
				  }
				
				
				res.put("author", author);
				
				res.put("middeimg", middeimg);
				res.put("price", price);
				res.put("publisher", publisher);
				
				res.put("title", title);
				res.put("catalog", catalog);
				res.put("summary", summary);
				System.out.println(res.toString());
				
				response.setHeader("Content-type", "text/json;charset=UTF-8");
				response.getWriter().write(res.toString());
				response.getWriter().flush();
				response.getWriter().close();
			
//				outputStreamWriter.write(result);
//				outputStreamWriter.flush();
//				outputStreamWriter.close();
			}catch (Exception e) {
				res.put("error", "isbn error");
				response.getWriter().write(res.toString());
				response.getWriter().flush();
				response.getWriter().close();
			}finally {
				
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
