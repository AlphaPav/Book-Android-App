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
import android.widget.TextView;
import android.widget.Toast;

import com.example.alphapav.book.util.APIUtils;
import com.example.alphapav.book.util.GetData;
import com.example.alphapav.book.util.SharedHelper;
import com.example.alphapav.book.util.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class ModifyUserActivity extends AppCompatActivity {

    private TextView tx_usename;

    private EditText et_qq;
    private EditText et_wx;
    private EditText et_phone;
    private Button btn_modify_ok;
    private Context mContext;
    private SharedHelper sh;
    JSONObject user_json=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_user);

        mContext= getApplicationContext();
        sh = new SharedHelper(getApplicationContext());
        bindViews();
        getInfo();

    }

    private void bindViews() {
       et_qq=  findViewById(R.id.modifyuser_qq);
       et_wx=findViewById(R.id.modifyuser_wx);
       et_phone= findViewById(R.id.modifyuser_phone);
       tx_usename= findViewById(R.id.modifyuser_username);
       tx_usename.setText(sh.get(SharedHelper.TEMPUSERNAME));
       btn_modify_ok=findViewById(R.id.modifyuser_btn_ok);
       btn_modify_ok.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               updateUserInfo();
           }
       });



    }

    private void updateUserInfo()
    {

        final String _QQ= et_qq.getText().toString();
        final String _WX= et_wx.getText().toString();
        final String _Phone= et_phone.getText().toString();

        new Thread(){
            @Override
            public void run() {
                super.run();
                HashMap<String, String> argu = new HashMap<>();
                argu.put("Username", sh.get(SharedHelper.TEMPUSERNAME));
                argu.put("QQ", _QQ);
                argu.put("Phone",_Phone );
                argu.put("WX",_WX );
                String res = GetData.getFormbodyPostData(APIUtils.MODIFYINFO_URL, argu);
                JSONObject res_json = JSONObject.fromObject(res);

                Log.e("XIAOXIE", "run: " + "update user info");
                if(res_json.containsKey("login")){
                    if(res_json.containsKey("success")){
                        handler.sendEmptyMessage(0x002);
                    }
                    else{

                        handler.sendEmptyMessage(0x003);
                    }
                }
                else{
                    handler.sendEmptyMessage(0x0926);
                }
            }
        }.start();

    }



    private void getInfo()
    {
        new Thread(){
        @Override
            public void run() {
                super.run();
                HashMap<String, String> argu = new HashMap<>();
                argu.put("Username", sh.get(SharedHelper.TEMPUSERNAME));
                String res = GetData.getFormbodyPostData(APIUtils.GETUSER_URL, argu);
                user_json = JSONObject.fromObject(res);
                Log.e("XIAOXIE", "run: " + "get user info");
                if(user_json.containsKey("login")){
                    if(user_json.containsKey("success")){
                        handler.sendEmptyMessage(0x001);
                    }
                    else{

                        handler.sendEmptyMessage(0x003);
                    }
                }
                else{
                    handler.sendEmptyMessage(0x0926);
                }
            }
        }.start();

    }

    //用于刷新界面和跳转页面
    @SuppressLint("HandlerLeak")

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {

                case 0x001:
                    Toast.makeText(ModifyUserActivity.this, "成功获取用户信息", Toast.LENGTH_SHORT).show();
                    setUser();
                    break;
                case 0x002:
                    Toast.makeText(ModifyUserActivity.this, "成功更新个人信息", Toast.LENGTH_SHORT).show();
                    break;
                case 0x003:
                    Toast.makeText(ModifyUserActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                    break;

                case 0x0926:
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(getApplicationContext(), InitActivity.class);
                    startActivity(intent);
                    break;
                default:
                    Toast.makeText(ModifyUserActivity.this, "Invalid msg.what", Toast.LENGTH_SHORT).show();
                    break;
            }
        };
    };

    private void setUser()
    {
        if(user_json== null)
            return;

        if(user_json.containsKey("user"))
        {
            String user_str= user_json.getString("user");

            ArrayList<User> temp_userlist = new Gson().fromJson(user_str,

                    new TypeToken<ArrayList<User>>(){}.getType());

           User temp_user= temp_userlist.get(0);
           et_phone.setText(temp_user.Phone);
           et_qq.setText(temp_user.QQ);
           et_wx.setText(temp_user.WX);

        }

    }


}
