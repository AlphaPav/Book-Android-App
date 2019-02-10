package com.example.alphapav.book.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alphapav.book.InitActivity;
import com.example.alphapav.book.R;
import com.example.alphapav.book.util.APIUtils;
import com.example.alphapav.book.util.BookInfo;
import com.example.alphapav.book.util.BookView;
import com.example.alphapav.book.util.GetData;
import com.example.alphapav.book.util.SharedHelper;
import com.example.alphapav.book.util.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class BooksellerFragment extends Fragment {
    String id = null;
    String username= null;
    String error = "";


    private EditText tx_phone;
    private TextView tx_username;
    private EditText tx_qq;
    private EditText tx_wx;
    View view;


    SharedHelper sh;
    User mUser = null;
    Context mContext;

    public BooksellerFragment() {
    }

    public static BooksellerFragment newInstance(String book_id,String user_name) {
        BooksellerFragment fragment = new BooksellerFragment();
        fragment.id = book_id;
        fragment.username= user_name;

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_bookseller, container, false);
        mContext = Objects.requireNonNull(getActivity()).getApplicationContext();
        sh = new SharedHelper(mContext);

        bindView();

        InitPost();


        return view;



    }

    void bindView(){

        tx_phone =(EditText) view.findViewById(R.id.bookseller_phone);
        tx_qq = (EditText)view.findViewById(R.id.bookseller_qq);
        tx_wx =(EditText) view.findViewById(R.id.bookseller_wx);
        tx_username= view.findViewById(R.id.bookseller_username);

    }



    void InitPost(){
        new Thread(){
            @Override
            public void run() {
                HashMap<String, String> args = new HashMap<>();
                args.put("Username", username);
                String res = GetData.getFormbodyPostData(APIUtils.GETUSER_URL, args);
                if(res == null){
                    handler.sendEmptyMessage(0x001);
                    return;
                }
                JSONObject res_json = JSONObject.fromObject(res);
                if(res_json == null){
                    handler.sendEmptyMessage(0x007);
                }
                else{
                    if(res_json.containsKey("success")){
                        String user_str = res_json.getString("user");
                        ArrayList<User> temp_user= new Gson().fromJson(user_str, new TypeToken<ArrayList<User>>(){}.getType());

                        if(temp_user != null && temp_user.size() > 0){
                            mUser = temp_user.get(0);
                            handler.sendEmptyMessage(0x003);
                        }
                        else{
                            error = res_json.getString("error");
                            handler.sendEmptyMessage(0x0913);
                        }
                    }
                    else if(!res_json.containsKey("login")){
                        handler.sendEmptyMessage(0x0926);
                    }
                    else{
                        handler.sendEmptyMessage(0x007);
                    }
                }


            }
        }.start();

    }

    void setUserinfo()
    {
        if(mUser==null)
        {
            return;
        }
        Log.i("XIAOXIE", "setUserinfo: "+mUser.Phone+mUser.QQ);



        tx_phone.setText(mUser.Phone);
        tx_phone.setFocusable(false);
        tx_phone.setFocusableInTouchMode(false);

        tx_qq.setText(mUser.QQ);
        tx_qq.setFocusable(false);
        tx_qq.setFocusableInTouchMode(false);

        tx_wx.setText(mUser.WX);
        tx_wx.setFocusable(false);
        tx_wx.setFocusableInTouchMode(false);

        tx_username.setText(mUser.Username);

    }

    //用于刷新界面和跳转页面
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {

                case 0x001:
                    Toast.makeText(mContext, "服务器未响应", Toast.LENGTH_SHORT).show();
                    break;
                case 0x003:
                    Toast.makeText(mContext, "获取成功", Toast.LENGTH_SHORT).show();
                    setUserinfo();

                    break;
                case 0x004:
                    Toast.makeText(mContext, "正在获取书籍信息", Toast.LENGTH_SHORT).show();
                    break;
                case 0x006:
                    Toast.makeText(mContext, "图片上传成功", Toast.LENGTH_SHORT).show();
                    break;
                case 0x00A:
                    Toast.makeText(mContext, "增加书籍成功", Toast.LENGTH_SHORT).show();
                    break;
                case 0x0926:
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(mContext, InitActivity.class);
                    startActivity(intent);
                    break;
                case 0x0913:
                    Toast.makeText(mContext, "错误：" + error, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(mContext, "Invalid msg.what", Toast.LENGTH_SHORT).show();
                    break;
            }
        };
    };


}
