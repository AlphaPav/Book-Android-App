package BookApp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class check {
	// this class is to check the valid 
	public static String getStringXor(String s1, String s2) {
		if(s1 == null || s2 == null) {
			return null;
		}
		
		if((s1.length() != s2.length()) || s1.length() == 0) {
			return null;
		}
		
		String realCode = "";
		for (int i = 0; i < s1.length(); i++) {
			int c1 = myChar2Int(s1.charAt(i));
			int c2 = myChar2Int(s2.charAt(i));
			if(c1 == -1 || c2 == -1) {
				return null;
			}
			// myXor
			char c = myInt2Char((c1 ^ c2) & 0x3f);
			if(c == '$') {
				return null;
			}
			realCode += c;
		}
		return realCode;
	}
	
	public static String CalculateSHA256(String plainText){
		if(plainText == null) {
			return null;
		}
		try {
            MessageDigest messageDigest = MessageDigest.getInstance("sha-256");
            messageDigest.update(plainText.getBytes("utf-8"));
            return bytesToHexString(messageDigest.digest());
        } catch (Exception e) {
            System.out.println(e.toString());
        } 
		return null;
	}
	
	private static String bytesToHexString(byte[] bytes){
        StringBuffer sb = new StringBuffer(bytes.length);
        String temp = null;
        for (int i = 0;i< bytes.length;i++){
            temp = Integer.toHexString(0xFF & bytes[i]);
            if (temp.length() <2){
                sb.append(0);
            }
            sb.append(temp);
        }
        return sb.toString();
    }
	
	private static int  myChar2Int(char c) {
		/*a-z A-Z 0-9 # * size = 10 + 26 + 26 = 64 = 2^6*/ 
		if(c >= 'a' && c <= 'z') {
			return c-'a';
		}
		if(c >= 'A' && c <= 'Z') {
			return (c-'A') + 26;
		}
		if(c >= '0' && c <= '9') {
			return (c-'0') + 52;
		}
		if(c == '#') {
			return 62;
		}
		if(c == '*') {
			return 63;
		}
		return -1;
	}
	
	private static char  myInt2Char(int x) {
		if(x >= 0 && x < 26) {
			return (char)(x +'a');
		}
		if(x >= 26 && x < 52) {
			return (char)(x - 26 +'A');
		}
		if(x >= 52 && x < 62) {
			return (char)(x - 52 +'0');
		}
		if(x == 62) {
			return '#';
		}
		if(x == 63) {
			return '*';
		}
		
		return '$';
	}
	
	public static boolean checkLogin(HttpServletRequest request) {
		
		//according to user's request, get the session
		HttpSession session = request.getSession();
		
		// get the user's openID
		String login = (String)session.getAttribute("login");
		
		if(login == null) {
			return false;
		}
		if(login.equals("yes"))
		{
			return true;
		}
		return false;
	}
	
	
	public static boolean checkZjuEmail(String email) {
		if(email == null) {
			return false;
		}
		
		int length = email.length();
		if(length < 11) {
			return false;
		}
		
		String backWord = email.substring(length-11);
		if(!backWord.equals("@zju.edu.cn")) {
			return false;
		}
		
		String userName = email.substring(0, length-11);
		for(int i = 0; i < userName.length(); i++) {
			char ch = userName.charAt(i);
			
			boolean underline = false, number = false, letter = false;
			if(ch >= 'a' && ch <= 'z') {
				letter = true;
			}
			if(ch >= 'A' && ch <= 'Z') {
				letter = true;
			}
			if(ch >= '0' && ch <= '9') {
				letter = true;
			}
			if(ch == '_') {
				underline = true;
			}
			
			if(underline || number || letter) {
				continue;
			}
			else {
				return false;
			}
		}
		
		
		return true;
	}
	
	public static boolean checkQQ(String QQ) {
		if(QQ == null || QQ.equals("")) {
			return true;
		}
		
		int length = QQ.length();
		if(length > 15) {
			return false;
		}
		
		// check if is all numbers
		for(int i = 0; i < length; i++) {
			char ch = QQ.charAt(i);
			if(!(ch <= '9' && ch >= '0')) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean checkPhone(String phone) {
		if(phone == null || phone.equals("")) {
			return true;
		}
		
		int length = phone.length();
		if(length != 11) {
			return false;
		}
		
		// check if is all numbers
		for(int i = 0; i < length; i++) {
			char ch = phone.charAt(i);
			if(!(ch <= '9' && ch >= '0')) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean checkWX(String WX) {
		if(WX == null || WX.equals("")) {
			return true;
		}
		
		int length = WX.length();
		if(length < 6 || length > 20) {
			return false;
		}
		
		char first = WX.charAt(0);
		if(!((first <= 'Z' && first >= 'A') || (first <= 'z' && first >= 'a'))) {
			return false;
		}
		
		// check if is all numbers
		for(int i = 1; i < length; i++) {
			char ch = WX.charAt(i);
			boolean underline = false, number = false, letter = false;
			if(ch >= 'a' && ch <= 'z') {
				letter = true;
			}
			if(ch >= 'A' && ch <= 'Z') {
				letter = true;
			}
			if(ch >= '0' && ch <= '9') {
				letter = true;
			}
			if(ch == '_' || ch == '-') {
				underline = true;
			}
			
			if(underline || number || letter) {
				continue;
			}
			else {
				return false;
			}
		}
		return true;
	}
	
	public static void main(String[] args)  
	{
		String url = "https://www.itemzheng.top/wx/books?isbn=97875076035";
		
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
			System.out.println(result);
		}
		catch (Exception e) {
		}
	}
}
