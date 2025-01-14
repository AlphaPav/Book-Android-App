package com.example.alphapav.book;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.alphapav.book.util.APIUtils;
import com.example.alphapav.book.util.Check;
import com.example.alphapav.book.util.GetData;
import com.example.alphapav.book.util.SharedHelper;
import com.example.alphapav.book.util.TimeUtils;

import net.sf.json.JSONObject;

import java.util.HashMap;

public class InitActivity extends AppCompatActivity {

    private String key;
    private SharedHelper sh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        // sharehelper
        sh = new SharedHelper(getApplicationContext());

        new Thread(){
            @Override
            public void run() {
                // send init message to server
                HashMap<String, String> argus = new HashMap<>();

                // Api url
                String url = APIUtils.INIT_URL;
                Log.e("XIAOXIE", "run: " + url);

                // Send Request
                String res = GetData.getFormbodyPostData(url, argus, true);
                if(res == null){
                    handler.sendEmptyMessage(0x002);
                    return;
                }
                JSONObject res_json = JSONObject.fromObject(res);

                // judge if success
                if(res_json.containsKey("success") && "OK".equals(res_json.getString("success"))){
                    // get history
                    String username = sh.get(SharedHelper.USERNAME);
                    String password = sh.get(SharedHelper.PASSWORD);
                    // try login
                    if(username!= null  && password != null){

                        // set argus
                        HashMap <String, String> arguments = new HashMap<>();
                        arguments.put("username", username);
                        arguments.put("password", password);

                        // try login
                        String login_url = APIUtils.LOGIN_URL;
                        String login_res = GetData.getFormbodyPostData(login_url, arguments, false);
                        JSONObject login_json = JSONObject.fromObject(login_res);

                        // check server
                        if(login_json == null){
                            handler.sendEmptyMessage(0x002);
                            return;
                        }

                        // judge if success
                        if(login_json.containsKey("success") && "OK".equals(login_json.getString("success"))){
                            sh.save(SharedHelper.TEMPUSERNAME, username);
                            sh.save(SharedHelper.LOGINTIME, TimeUtils.getCurrentTime());
                            handler.sendEmptyMessage(0x001);
                            return;
                        }
                    }
                    handler.sendEmptyMessage(0x003);
                }
                else{
                    handler.sendEmptyMessage(0x002);
                }

            }
        }.start();
    }

    // init to server
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0x001){      // login successfully
                Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
                // get extra paraments
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(InitActivity.this, MainActivity.class);
                // jump to login
                startActivity(intent);
            }
            else if(msg.what == 0x002){
                Toast.makeText(InitActivity.this, "服务器连接失败，请稍后再试！", Toast.LENGTH_SHORT).show();
            }
            else if(msg.what == 0x003){
                // get extra paraments
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(InitActivity.this, LoginActivity.class);

                // jump to login
                startActivity(intent);
            }
        }
    };
}
