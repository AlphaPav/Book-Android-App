package com.example.alphapav.book.util;


import android.util.Log;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.UUID;

import static android.net.sip.SipErrorCode.TIME_OUT;

public class GetData {
    // to keep in the same session ID
    static String sessionID = null;
    private static final String TAG = "uploadFile";
    private static final int TIME_OUT = 10 * 1000;//超时时间
    private static final String CHARSET = "utf-8";//设置编码

    public static String getFormbodyPostData(String url, HashMap<String, String> paramsMap, boolean first)
    {
        try {
            URL myUrl = new URL(url);

            Log.e("XIAOXIE", "getFormbodyPostData: " + myUrl.getPath());

            //connect
            HttpURLConnection connection = (HttpURLConnection)myUrl.openConnection();

            //setting
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("POST");
            if(sessionID != null){
                connection.setRequestProperty("cookie", sessionID);
            }

            connection.setDoOutput(true);
            connection.setUseCaches(false);
            //begin connet
            connection.connect();

            StringBuffer params = new StringBuffer();

            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    params.append("&");
                }
                Log.e("KEY", "Key: " + key);
                params.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                pos++;
            }

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream(), "utf-8");
            outputStreamWriter.write(params.toString());
            outputStreamWriter.flush();
            outputStreamWriter.close();

            if(first){
                //get set-cookie
                String cookieval = connection.getHeaderField("set-cookie");
                if(cookieval != null) {
                    //get sessionId
                    sessionID = cookieval.substring(0, cookieval.indexOf(";"));
                }
            }

            int resultCode = connection.getResponseCode();
            Log.e("GetData", "getFormbodyPostData: rescode = " + resultCode);
            if(resultCode == HttpURLConnection.HTTP_OK) {
                //Success
                StringBuffer buffer = new StringBuffer();
                String line;
                //Get the response
                BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                while((line = responseReader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }
                responseReader.close();
                String result= buffer.toString();
                System.out.print("Get response : " + result);
                System.out.print("Get response : " + buffer.toString());

                return result;
            }
            else {
                System.out.println("Nothing");
            }
        }catch (Exception e) {
            Log.e("GetData", "getFormbodyPostData: " + e.toString());
            e.printStackTrace();
        }
        return null;
    }

    public static String getFormbodyPostData(String url, HashMap<String, String> paramsMap)
    {
        return getFormbodyPostData(url, paramsMap, false);
    }

    public static String uploadImage(String RequestURL, File file) {
        String result = "";
        String BOUNDARY = UUID.randomUUID().toString();//边界标识 随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data";//内容类型
        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true);//允许输入流
            conn.setDoOutput(true);//允许输出流
            conn.setUseCaches(false);//不允许使用缓存
            conn.setRequestMethod("POST");//请求方式
            conn.setRequestProperty("Charset", CHARSET);//设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            if(sessionID != null){
                conn.setRequestProperty("cookie", sessionID);
            }
            conn.connect();

            if (file != null) {
                //当文件不为空，把文件包装并且上传
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(PREFIX + BOUNDARY + LINE_END);
                dos.writeBytes("Content-Disposition: form-data; " + "name=\"inputName\";filename=\"" + file.getName() + "\"" + LINE_END);
                dos.writeBytes(LINE_END);

                FileInputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = -1;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());

                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();
                /*
                 * 获取响应码  200=成功
                 * 当响应成功，获取响应的流  
                 */
                int res = conn.getResponseCode();
                if (res == 200) {
                    InputStream input = conn.getInputStream();
                    StringBuilder sbs = new StringBuilder();
                    int ss;
                    while ((ss = input.read()) != -1) {
                        sbs.append((char) ss);
                    }
                    result = sbs.toString();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


}
