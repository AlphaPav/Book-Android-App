package com.example.alphapav.book.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alphapav.book.AddBookActivity;
import com.example.alphapav.book.MainActivity;
import com.example.alphapav.book.ModifyUserActivity;
import com.example.alphapav.book.MybooksActivity;
import com.example.alphapav.book.R;
import com.example.alphapav.book.base.BaseFragment;
import com.example.alphapav.book.util.APIUtils;
import com.example.alphapav.book.util.GetData;
import com.example.alphapav.book.util.SharedHelper;
import com.example.alphapav.book.LoginActivity;
import com.example.alphapav.book.util.TimeUtils;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;


import java.text.ParseException;
import java.util.List;
import java.util.Objects;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import net.sf.json.JSONObject;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;


public class UsercenterFragment  extends BaseFragment {

    View view;
    private SharedHelper sh;
    private Context mContext;
    private int REQUEST_CODE_SCAN = 111;
    private TextView result;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_usercenter, container, false);

        mContext= Objects.requireNonNull(getActivity()).getApplicationContext();
        sh= new SharedHelper(mContext);
        try {
            bindViews();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return view ;
    }
    public static UsercenterFragment newInstance() {
        return new UsercenterFragment();
    }

    //用于刷新界面和跳转页面
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0x001:
                    Toast.makeText(getActivity(), "登出成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    break;

                case 0x003:
                    Toast.makeText(getActivity(),"Wrong response format", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(mContext, LoginActivity.class));
                    break;
                default:
                    Toast.makeText(getActivity(), "Invalid msg.what", Toast.LENGTH_SHORT).show();
                    break;
            }
        };
    };



    private void bindViews() throws ParseException {
        Button btn_logout = (Button) view.findViewById(R.id.usercenter_btn_logout);
        Button btn_mybook = (Button) view.findViewById(R.id.usercenter_btn_mybook);
        Button btn_addbook = (Button) view.findViewById(R.id.usercenter_btn_addbook);
        Button btn_modifyinfo= (Button) view.findViewById(R.id.usercenter_btn_modifyuserinfo);

        result = view.findViewById(R.id.result);

        TextView text_usercenter_name=(TextView)view.findViewById(R.id.usercenter_name);

        text_usercenter_name.setText(" "+ sh.get(SharedHelper.TEMPUSERNAME)+" ");
        TextView text_usercenter_time=(TextView)view.findViewById(R.id.usercenter_time);
        long diff = TimeUtils.getMinuteDiff(TimeUtils.getCurrentTime(), sh.get(SharedHelper.LOGINTIME));
        text_usercenter_time.setText(" 已登陆"+diff+"分钟 ");
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    public void run() {
                        int msg_what=0x003;
                        //begin request
                        try {
                            HashMap<String,String> map = new HashMap<>();
                            String result= GetData.getFormbodyPostData(APIUtils.LOGOUT_URL, map);
                            if(result!=null)
                            {
                                JSONObject jsStr= JSONObject.fromObject(result);
                                if(jsStr.containsKey("success") && "OK".equals(jsStr.getString("success"))){

                                    msg_what=0x001;
                                }
                                else
                                {
                                    msg_what=0x003;
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

        btn_mybook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, MybooksActivity.class));
            }
        });

        btn_modifyinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, ModifyUserActivity.class));
            }
        });


        btn_addbook.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                /*test*/
//                String content= "9787302274629";
//                // get extra paraments
//                Intent intent = new Intent();
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.setClass(getActivity(), AddBookActivity.class);
//                intent.putExtra("ISBN", content);
//                // jump to login
//                startActivity(intent);

                AndPermission.with(getActivity())
                        .permission(Permission.CAMERA, Permission.READ_EXTERNAL_STORAGE)
                        .onGranted(new Action() {
                            @Override
                            public void onAction(List<String> permissions) {
                                Intent intent = new Intent(getActivity(), CaptureActivity.class);

                                startActivityForResult(intent, REQUEST_CODE_SCAN);
                            }
                        })
                        .onDenied(new Action() {
                            @Override
                            public void onAction(List<String> permissions) {
                                Uri packageURI = Uri.parse("package:" + getActivity().getPackageName());
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                startActivity(intent);

                                Toast.makeText(getActivity(), "没有权限无法扫描呦", Toast.LENGTH_LONG).show();
                            }
                        }).start();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
//                result.setText("扫描ISBN结果为：" + content);
                if(content.length()==13)
                {
                    // get extra paraments
                    Intent intent = new Intent(mContext, AddBookActivity.class);
                    intent.putExtra("ISBN", content);
                    // jump to login
                    startActivity(intent);
                }else
                {
                    Toast.makeText(getActivity(), "扫描出来的不是书籍的ISBN条形码，请重新扫描。", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

}