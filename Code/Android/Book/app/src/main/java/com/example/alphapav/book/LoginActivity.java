package com.example.alphapav.book;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.alphapav.book.util.APIUtils;
import com.example.alphapav.book.util.GetData;
import com.example.alphapav.book.util.SharedHelper;
import com.example.alphapav.book.util.TimeUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private EditText edit_account;
    private EditText edit_psw;
    private  Button btn_login;
    private Button btn_register;
    private  CheckBox check_rememberpsw;
    private Context mContext;
    private  String url = APIUtils.LOGIN_URL;
    private SharedHelper sh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext= getApplicationContext();
        sh= new SharedHelper(mContext);
        bindViews();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation== Configuration.ORIENTATION_LANDSCAPE)
        {
            System.out.println("当前屏幕为横屏");

        }else
        {
            System.out.println("当前屏幕为竖屏");
        }
    }




    private void bindViews(){
        edit_account= (EditText) findViewById(R.id.login_edit_account);
        edit_psw= (EditText) findViewById(R.id.login_edit_psw);
        btn_login= (Button) findViewById(R.id.login_btn_login);
        btn_register= (Button) findViewById(R.id.login_btn_register);
        check_rememberpsw= (CheckBox) findViewById(R.id.login_remember);
        /* read the sh content*/
        String sh_username = sh.get(SharedHelper.USERNAME);
        String sh_password = sh.get(SharedHelper.PASSWORD);
        edit_account.setText(sh_username);
        edit_psw.setText(sh_password);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                Toast.makeText(mContext,"switch to register activity~ ",Toast.LENGTH_SHORT).show();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String username= edit_account.getText().toString();
//                String password= edit_psw.getText().toString();
                new Thread() {
                    public void run() {
                        int msg_what=0x006;
                        //begin request
                        try {
                            HashMap<String,String> map = new HashMap<>();
                            String username= edit_account.getText().toString();
                            String password= edit_psw.getText().toString();
                            map.put("username",username);
                            map.put("password",password);

                            String result= GetData.getFormbodyPostData(url, map);
                            JSONObject jsStr= JSONObject.fromObject(result);
                            if(jsStr == null){
                                handler.sendEmptyMessage(0x007);
                                return;
                            }else
                            {
                                if(jsStr.containsKey("success") && "OK".equals(jsStr.getString("success"))){
                                    if(check_rememberpsw.isChecked())
                                    {
                                        System.out.println("记住密码");
                                        //save username, password
                                        sh.save(SharedHelper.USERNAME, username);
                                        sh.save(SharedHelper.PASSWORD, password);

                                    }
                                    //save temp username
                                    sh.save(SharedHelper.TEMPUSERNAME, username);
                                    sh.save(SharedHelper.LOGINTIME, TimeUtils.getCurrentTime());
                                    msg_what=0x005;

                                }else if(jsStr.containsKey("error") )
                                {
                                    if("not exist username".equals(jsStr.getString("error")))
                                    {
                                        msg_what=0x001;
                                    }else if("password incorrect".equals(jsStr.getString("error")))
                                    {
                                        msg_what=0x002;
                                    }
                                }
                            }
                        }catch (Exception e) {
                            // TODO: handle exception
                            e.printStackTrace();
                        }
                        handler.sendEmptyMessage(msg_what);
                    }
                }.start();

            }
        });
    }


    //用于刷新界面和跳转页面

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0x001:
                    Toast.makeText(LoginActivity.this, "请您先注册，再访问我们的app", Toast.LENGTH_SHORT).show();
                    break;
                case 0x002:
                    Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                    break;

                case 0x005:
                    Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    break;
                case 0x006:
                    Toast.makeText(LoginActivity.this, "Wrong response format", Toast.LENGTH_SHORT).show();
                    break;
                case 0x007:
                    Toast.makeText(LoginActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(LoginActivity.this, "Invalid msg.what", Toast.LENGTH_SHORT).show();
                    break;

            }
        };
    };
}
