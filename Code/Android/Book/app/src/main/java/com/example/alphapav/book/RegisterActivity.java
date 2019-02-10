package com.example.alphapav.book;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.alphapav.book.util.APIUtils;
import com.example.alphapav.book.util.GetData;
import com.example.alphapav.book.util.SharedHelper;

import net.sf.json.JSONObject;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    Context mContext;
    EditText edit_name;
    EditText edit_psw;
    EditText edit_psw_confirm;
    EditText edit_email;
    EditText edit_email_code;
    Button btn_ok;
    Button btn_cancel;
    Button btn_getEmailCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mContext= getApplicationContext();
        bindViews();


    }
    private void bindViews(){
        edit_name= (EditText) findViewById(R.id.register_edit_name);
        edit_psw= (EditText) findViewById(R.id.register_edit_psw);
        edit_psw_confirm= (EditText) findViewById(R.id.register_edit_psd_confirm);
        edit_email= (EditText)findViewById(R.id.register_edit_email);
        edit_email_code= (EditText) findViewById(R.id.register_edit_email_code);

        btn_getEmailCode= (Button) findViewById(R.id.register_btn_getEmailCode);
        btn_ok= (Button) findViewById(R.id.register_btn_ok);
        btn_cancel= (Button) findViewById(R.id.register_btn_cancel);

        btn_getEmailCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edit_email.getText().toString();
                if(email.equals(""))
                {
                    Toast.makeText(mContext,"邮箱不能为空！",Toast.LENGTH_SHORT).show();
                    return;
                }
                new Thread() {
                    public void run() {
                        int msg_what=0x007;
                        //begin request
                        try {
                            HashMap<String,String> map = new HashMap<>();
                            String email  = edit_email.getText().toString();
                            map.put("email",email);

                            String result= GetData.getFormbodyPostData(APIUtils.GETEMAILCODE, map);
                            JSONObject jsStr= JSONObject.fromObject(result);
                            if(jsStr == null){
                                handler.sendEmptyMessage(0x008);
                                return;
                            }
                            if(jsStr.containsKey("success") && "OK".equals(jsStr.getString("success"))){
                                msg_what=0x004;

                            }else if(jsStr.containsKey("error") )
                            {
                                if("邮箱不能为空！".equals(jsStr.getString("error")))
                                {
                                    msg_what=0x001;
                                }else if("邮箱格式错误，请检查邮箱！".equals(jsStr.getString("error")))
                                {
                                    msg_what=0x002;
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


        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String password= edit_psw.getText().toString();
                String password_confirm= edit_psw_confirm.getText().toString();

                if(password.equals(password_confirm))
                {
                    new Thread() {
                        public void run() {
                            int msg_what=0x007;
                            //begin request
                            try {


                                String email  = edit_email.getText().toString();
                                String code  = edit_email_code.getText().toString();
                                HashMap<String,String> map = new HashMap<>();
                                map.put("email",email);
                                map.put("code",code);
                                String result= GetData.getFormbodyPostData(APIUtils.VERIFYEMAILCODE, map);
                                if(result!=null)
                                {
                                    JSONObject jsStr= JSONObject.fromObject(result);
                                    if(jsStr.containsKey("success") && "OK".equals(jsStr.getString("success"))){
                                       // 邮箱验证成功，自动注册

                                        String username= edit_name.getText().toString();
                                        String password= edit_psw.getText().toString();
                                        HashMap<String,String> map1 = new HashMap<>();
                                        map1.put("username",username);
                                        map1.put("password",password);
                                        map1.put("email",email);
                                        String result1= GetData.getFormbodyPostData(APIUtils.REGISTER_URL, map1);
                                        if(result1!=null)
                                        {
                                            JSONObject jsStr1= JSONObject.fromObject(result1);

                                            if(jsStr1.containsKey("success") && "OK".equals(jsStr1.getString("success"))){
                                                msg_what= 0x006;
                                            }else if (jsStr1.containsKey("error"))
                                            {

                                                Log.i("error", jsStr1.getString("error").toString());
                                                Log.e("XIAOXIE", "run: " + jsStr1.getString("error"));
                                                if("邮箱不能为空！".equals(jsStr1.getString("error")))
                                                {
                                                    msg_what=0x001;
                                                }else if("密码不能为空".equals(jsStr1.getString("error")))
                                                {
                                                    msg_what=0x011;
                                                }else if("用户名已经被注册".equals(jsStr1.getString("error")))
                                                {
                                                    msg_what=0x012;
                                                }
                                                else if("用户名不能为空！".equals(jsStr1.getString("error")))
                                                {
                                                    msg_what=0x013;
                                                }
                                            }
                                        }
                                        else {
                                            msg_what=0x008; //服务器连接失败
                                        }
                                    }else if(jsStr.containsKey("error") )
                                    {
                                        if("邮箱不能为空！".equals(jsStr.getString("error")))
                                        {
                                            msg_what=0x001;
                                        }else if("邮箱格式错误，请检查邮箱！".equals(jsStr.getString("error")))
                                        {
                                            msg_what=0x002;
                                        }else if("验证码不能为空！".equals(jsStr.getString("error")))
                                        {
                                            msg_what=0x003;
                                        }
                                        else if("邮箱错误！".equals(jsStr.getString("error")))
                                        {
                                            msg_what=0x009;
                                        }
                                        else if("验证码错误！".equals(jsStr.getString("error")))
                                        {
                                            msg_what=0x0010;
                                        }

                                    }
                                }else
                                {
                                    msg_what=0x008; //服务器连接失败
                                }



                            }catch (Exception e) {
                                // TODO: handle exception
                                e.printStackTrace();
                            }
                            handler.sendEmptyMessage(msg_what);
                        }
                    }.start();

                }else
                {
                    Toast.makeText(mContext,"两次密码输入不同，请再确认！",Toast.LENGTH_SHORT).show();

                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                Toast.makeText(mContext,"switch to login activity~ ",Toast.LENGTH_SHORT).show();
                RegisterActivity.this.finish();
                System.out.println("RegisterActivity.this.finish();");
            }
        });

    }

    //用于刷新界面和跳转页面

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0x001:
                    Toast.makeText(RegisterActivity.this, "邮箱不能为空！", Toast.LENGTH_SHORT).show();
                    break;
                case 0x002:
                    Toast.makeText(RegisterActivity.this, "邮箱格式错误，请检查邮箱！", Toast.LENGTH_SHORT).show();
                    break;
                case 0x003:
                    Toast.makeText(RegisterActivity.this, "验证码不能为空！", Toast.LENGTH_SHORT).show();
                    break;
                case 0x004:
                    Toast.makeText(RegisterActivity.this, "成功发送验证码，请到邮箱查看", Toast.LENGTH_SHORT).show();
                    break;
                case 0x005:
                    Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                    break;
                case 0x006:
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));

                    break;
                case 0x007:
                    Toast.makeText(RegisterActivity.this, "Wrong response format", Toast.LENGTH_SHORT).show();
                    break;
                case 0x008:
                    Toast.makeText(RegisterActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
                    break;
                case 0x009:
                    Toast.makeText(RegisterActivity.this, "邮箱错误！", Toast.LENGTH_SHORT).show();
                    break;
                case 0x010:
                    Toast.makeText(RegisterActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                    break;
                case 0x011:
                    Toast.makeText(RegisterActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    break;
                case 0x012:
                    Toast.makeText(RegisterActivity.this, "用户名已经被注册", Toast.LENGTH_SHORT).show();
                    break;
                case 0x013:
                    Toast.makeText(RegisterActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(RegisterActivity.this, "Invalid msg.what", Toast.LENGTH_SHORT).show();
                    break;

            }
        };
    };

}
