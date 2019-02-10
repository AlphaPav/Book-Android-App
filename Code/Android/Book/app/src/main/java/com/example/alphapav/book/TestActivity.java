package com.example.alphapav.book;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Debug;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.alphapav.book.util.APIUtils;
import com.example.alphapav.book.util.GetData;
import com.example.alphapav.book.util.SharedHelper;

import net.sf.json.JSONObject;

import java.util.HashMap;

public class TestActivity extends AppCompatActivity {
    private Button btn_ok;

    SharedHelper sh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        bindViews();



    }

    private void bindViews()
    {
        sh = new SharedHelper(getApplicationContext());
        btn_ok= findViewById(R.id.test_btn);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInfo();
            }
        });

    }

    private void getInfo()
    {

        new Thread(){
            @Override
            public void run() {
                super.run();

                HashMap<String, String> argu = new HashMap<>();
                argu.put("QQ", "912960502");
                argu.put("WX", "xiecl0926");
                argu.put("Phone", "18868105130");
                argu.put("Username", sh.get(SharedHelper.TEMPUSERNAME));

                String res = GetData.getFormbodyPostData(APIUtils.MODIFYINFO_URL, argu);

                JSONObject jsStr= JSONObject.fromObject(res);
                Log.i("XIAOXIE", "run: " + "ModifyInfo");
                Log.i("XIAOXIE", res);

                if(jsStr.containsKey("login")){
                    if(jsStr.containsKey("error")){
                        handler.sendEmptyMessage(0x007);
                    }
                    else{

                        handler.sendEmptyMessage(0x00A);
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
                case 0x00A:
                    Toast.makeText(TestActivity.this, "OK", Toast.LENGTH_SHORT).show();
                    break;
                case 0x007:
                    Toast.makeText(TestActivity.this, "error", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Toast.makeText(TestActivity.this, "Invalid msg.what", Toast.LENGTH_SHORT).show();
                    break;
            }
        };
    };


}
