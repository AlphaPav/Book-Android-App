package com.example.alphapav.book.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.sf.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class DetailFragment extends Fragment {

    String id = null;

    View view;
    BookView ori_img, current_img;
    ViewPager viewPager = null;
    private ArrayList<View> pageview;

    String error = "";
    BookInfo bookInfo = null;

    Context mContext;

    Spinner spinner;
    private EditText et_bookname;
    private EditText et_author;
    private EditText et_press;
    private EditText et_isbn;
    private EditText et_originalprice;
    private EditText et_abstract;
    private EditText et_catalog;
    private EditText et_sellprice;
    private EditText et_description;

    String[]  ValidBookTypes={"人文类","传媒类","外语类", "经济类", "管理类", "理学类",
            "生命环境化学与地学类", "能源化工与高分子类", "机械与材料类","建筑与土木类", "电气与自动化类", "航空航天与过程装备类","计算机类",
            "信息类", "海洋类", "农学类", "生工食品类","医学类", "药学类", "艺术设计类", "其他类"};
    SharedHelper sh;




    public DetailFragment() {

    }

    public static DetailFragment newInstance(String book_id) {
        DetailFragment fragment = new DetailFragment();
        fragment.id = book_id;
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
        view = inflater.inflate(R.layout.fragment_detail, container, false);

        viewPager = view.findViewById(R.id.viewPager);
        pageview =new ArrayList<View>();

        mContext = Objects.requireNonNull(getActivity()).getApplicationContext();
        sh = new SharedHelper(mContext);

        add_image();
        InitPost();
        bindView();

        return view;
    }

    void bindView(){

        et_bookname = view.findViewById(R.id.addbook_bookname);
        et_author = view.findViewById(R.id.addbook_author);
        et_press = view.findViewById(R.id.addbook_press);
        et_isbn = view.findViewById(R.id.addbook_ISBN);

        et_originalprice= view.findViewById(R.id.addbook_originalprice);
        et_abstract=view.findViewById(R.id.addbook_abstract);
        et_catalog= view.findViewById(R.id.addbook_catalog);
        et_sellprice= view.findViewById(R.id.addbook_sellprice);
        et_description= view.findViewById(R.id.addbook_description);


        spinner=(Spinner) view.findViewById(R.id.addbook_spinner_booktype);

        ArrayAdapter<String> adapter= new ArrayAdapter<String>(
                mContext,android.R.layout.simple_spinner_dropdown_item
                , ValidBookTypes);
        spinner.setAdapter(adapter);
        spinner.setClickable(false);
    }

    void InitPost(){
        new Thread(){
            @Override
            public void run() {
                HashMap<String, String> args = new HashMap<>();
                args.put("BookID", id);
                String res = GetData.getFormbodyPostData(APIUtils.GETBOOK_URL, args);
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
                        String book_str = res_json.getString("result");
                        ArrayList<BookInfo> temp_book = new  Gson().fromJson(book_str, new TypeToken<ArrayList<BookInfo>>(){}.getType());

                        if(temp_book != null && temp_book.size() > 0){
                            bookInfo = temp_book.get(0);
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

    void updateView(){
        if(bookInfo == null)
            return;

        et_author.setText(bookInfo.getAuthor());
        et_author.setFocusable(false);
        et_author.setFocusableInTouchMode(false);

        et_catalog.setText(bookInfo.getCatalog());
        et_catalog.setFocusable(false);
        et_catalog.setFocusableInTouchMode(false);

        et_bookname.setText(bookInfo.getTitle());
        et_bookname.setFocusable(false);
        et_bookname.setFocusableInTouchMode(false);

        et_press.setText( bookInfo.getPublisher());
        et_press.setFocusable(false);
        et_press.setFocusableInTouchMode(false);

        et_abstract.setText( bookInfo.getSummary());
        et_abstract.setFocusable(false);
        et_abstract.setFocusableInTouchMode(false);

        et_originalprice.setText(bookInfo.getPrice());
        et_originalprice.setFocusable(false);
        et_originalprice.setFocusableInTouchMode(false);

        et_isbn.setText(bookInfo.getISBN());
        et_isbn.setFocusable(false);
        et_isbn.setFocusableInTouchMode(false);

        et_sellprice.setText(bookInfo.getSellPrice());
        et_sellprice.setFocusable(false);
        et_sellprice.setFocusableInTouchMode(false);

        et_description.setText(bookInfo.getDescription());
        et_description.setFocusable(false);
        et_description.setFocusableInTouchMode(false);


        int index = 0;
        for(int i = 0; i < ValidBookTypes.length; i++){
            if(ValidBookTypes[i].equals(bookInfo.getBookType())){
                index = i;
            }
        }
        spinner.setSelection(index);
        spinner.setEnabled(false);
    }

    void update(){
        if(bookInfo == null){
            return;
        }

        ori_img.setImageURL(bookInfo.getImagePath());
        current_img.setImageURL(bookInfo.getCurrentImg());

        updateView();
    }

    //用于刷新界面和跳转页面
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {

                case 0x001:
                    Toast.makeText(mContext, "服务器未响应", Toast.LENGTH_SHORT).show();
                    break;
                case 0x002:
                    Toast.makeText(mContext, "ISBN错误", Toast.LENGTH_SHORT).show();
                    break;
                case 0x003:
                    update();
                    Toast.makeText(mContext, "获取成功", Toast.LENGTH_SHORT).show();
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



    void add_image(){
        LayoutInflater inflater = getLayoutInflater();


        View cur_img_view = inflater.inflate(R.layout.item_image, null);
        current_img = cur_img_view.findViewById(R.id.myimg);
        pageview.add(cur_img_view);

        View ori_img_view = inflater.inflate(R.layout.item_image, null);
        ori_img = ori_img_view.findViewById(R.id.myimg);
        pageview.add(ori_img_view);


        PagerAdapter mPagerAdapter = new PagerAdapter(){

            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return pageview.size();
            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0==arg1;
            }
            //是从ViewGroup中移出当前View
            public void destroyItem(View arg0, int arg1, Object arg2) {
                ((ViewPager) arg0).removeView(pageview.get(arg1));
            }

            //返回一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中
            public Object instantiateItem(View arg0, int arg1){
                ((ViewPager)arg0).addView(pageview.get(arg1));
                return pageview.get(arg1);
            }
        };

        //绑定适配器
        viewPager.setAdapter(mPagerAdapter);
    }

}
