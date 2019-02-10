package com.example.alphapav.book;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alphapav.book.util.APIUtils;
import com.example.alphapav.book.util.BookInfo;
import com.example.alphapav.book.util.BookView;
import com.example.alphapav.book.util.GetData;
import com.example.alphapav.book.util.SharedHelper;
import com.google.gson.Gson;

import net.sf.json.JSONObject;

import java.io.File;
import java.util.HashMap;

public class MybookManagerActivity extends AppCompatActivity {

    String[]  ValidBookTypes={"人文类","传媒类","外语类", "经济类", "管理类", "理学类",
            "生命环境化学与地学类", "能源化工与高分子类", "机械与材料类","建筑与土木类", "电气与自动化类", "航空航天与过程装备类","计算机类",
            "信息类", "海洋类", "农学类", "生工食品类","医学类", "药学类", "艺术设计类", "其他类"};
    SharedHelper sh;
    int RESULT_LOAD_IMAGE = 0x0926;

    Spinner spinner;
    private TextView tv_selectImg;

    private BookView iv_originalimg;
    private BookView iv_tempimg;

    private EditText et_bookname;
    private EditText et_author;
    private EditText et_press;
    private EditText et_isbn;
    private EditText et_originalprice;
    private EditText et_abstract;
    private EditText et_catalog;
    private EditText et_sellprice;
    private EditText et_description;

    private Button btn_modifybook_ok;
    private Button btn_deletebook_ok;
    private Context mContext;
    private ImageView imgview_return;


    private File file = null;
    private String pic_url = null;
    BookInfo bookInfo=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mybook_manager);

        mContext= getApplicationContext();
        String content;
        content = getIntent().getStringExtra("Book");
        bookInfo= new Gson().fromJson(content,BookInfo.class);
        pic_url= bookInfo.getCurrentImg();
        bindViews();
        set_book();
    }

    private void bindViews()
    {
        sh = new SharedHelper(getApplicationContext());

        imgview_return= findViewById(R.id.headline_updatamybook_back);

        imgview_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        et_bookname= findViewById(R.id.addbook_bookname);
        et_author=findViewById(R.id.addbook_author);

        et_press= findViewById(R.id.addbook_press);
        et_isbn=findViewById(R.id.addbook_ISBN);

        et_originalprice= findViewById(R.id.addbook_originalprice);
        et_abstract=findViewById(R.id.addbook_abstract);
        et_catalog= findViewById(R.id.addbook_catalog);
        et_sellprice= findViewById(R.id.addbook_sellprice);
        et_description= findViewById(R.id.addbook_description);


        spinner=(Spinner) findViewById(R.id.addbook_spinner_booktype);

        ArrayAdapter<String> adapter= new ArrayAdapter<String>(
                this,android.R.layout.simple_spinner_dropdown_item
                , ValidBookTypes);
        spinner.setAdapter(adapter);

        tv_selectImg = findViewById(R.id.addbool_selectImg);
        tv_selectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // begin choose Image
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
                /* result will be handled @function onActivityResult*/
            }
        });

        iv_tempimg = findViewById(R.id.addbook_tempimg);

        iv_originalimg = findViewById(R.id.addbook_originalimg);
        btn_deletebook_ok = findViewById(R.id.deletebook_ok);
        btn_modifybook_ok = findViewById(R.id.modifybook_ok);

        btn_deletebook_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletebook();
            }
        });


        btn_modifybook_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatebook();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // get image
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null){
            Uri selected = data.getData();
            String [] filepath = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selected, filepath, null, null, null);
            cursor.moveToFirst();

            int col_index = cursor.getColumnIndex(filepath[0]);
            String pic_path = cursor.getString(col_index);
            cursor.close();

            upload_image(pic_path);
            Log.e("XIAOXIE", pic_path);
        }
    }


    private void deletebook()
    {
        new Thread(){
            @Override
            public void run() {
                super.run();

                HashMap<String, String> argu = new HashMap<>();
                argu.put("BookId", bookInfo.getBookID());
                argu.put("Username", sh.get(SharedHelper.TEMPUSERNAME));

                String res = GetData.getFormbodyPostData(APIUtils.DELETEBOOK_URL, argu);
                JSONObject res_json = JSONObject.fromObject(res);
                Log.e("XIAOXIE", "run: " + "deletebook");
                if(res_json.containsKey("login")){
                    if(res_json.containsKey("success")){
                        handler.sendEmptyMessage(0x007);
                    }
                    else{

                        handler.sendEmptyMessage(0x008);
                    }
                }
                else{
                    handler.sendEmptyMessage(0x0926);
                }
            }
        }.start();


    }
    private void upload_image(String pic_path){
        try {
            file = new File(pic_path);
            if(file != null){
                Toast.makeText(MybookManagerActivity.this, "上传图片...", Toast.LENGTH_SHORT).show();
                new Thread(){
                    @Override
                    public void run() {
                        String res = GetData.uploadImage(APIUtils.UPLOADPIC_URL, file);
                        Log.e("XIAOXIE", "run: " + res);
                        JSONObject res_json = JSONObject.fromObject(res);
                        if(res_json.containsKey("url")){
                            pic_url = APIUtils.PICTURES_URL + "?pic=" + res_json.getString("url");
                            handler.sendEmptyMessage(0x006);
                        }

                    }
                }.start();
            }
            else{
                Toast.makeText(MybookManagerActivity.this, "未找到图片", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            file = null;
        }
    }

    private void updatebook(){
        final String des = et_description.getText().toString();
        final String price = et_sellprice.getText().toString();
        final String type = ValidBookTypes[spinner.getSelectedItemPosition()];

        new Thread(){
            @Override
            public void run() {
                super.run();

                HashMap<String, String> argu = new HashMap<>();
                argu.put("BookId",bookInfo.getBookID() );
                argu.put("ISBN",bookInfo.getISBN() );
                argu.put("CurrentPicture",pic_url);  // may be new pic_url
                argu.put("BookType", type);
                argu.put("Description", des);
                argu.put("SellPrice", price);
                argu.put("Username", sh.get(SharedHelper.TEMPUSERNAME));

                String res = GetData.getFormbodyPostData(APIUtils.MODIFYBOOK_URL, argu);
                JSONObject res_json = JSONObject.fromObject(res);
                Log.e("XIAOXIE", "run: " + type);
                if(res_json.containsKey("login")){
                    if(res_json.containsKey("success")){
                        handler.sendEmptyMessage(0x003);
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

                case 0x001:
                    Toast.makeText(MybookManagerActivity.this, "服务器未响应", Toast.LENGTH_SHORT).show();
                    break;
                case 0x002:
                    Toast.makeText(MybookManagerActivity.this, "ISBN错误", Toast.LENGTH_SHORT).show();
                    break;
                case 0x003:
                    Toast.makeText(MybookManagerActivity.this, "修改书籍信息成功", Toast.LENGTH_SHORT).show();
                    break;
                case 0x004:
                    Toast.makeText(MybookManagerActivity.this, "正在获取书籍信息", Toast.LENGTH_SHORT).show();
                    break;
                case 0x006:
                    Toast.makeText(MybookManagerActivity.this, "图片上传成功", Toast.LENGTH_SHORT).show();
                    if(pic_url != null){
                        iv_tempimg.setImageURL(pic_url);
                    }
                    break;
                case 0x007:
                    Toast.makeText(MybookManagerActivity.this, "删除书籍成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(mContext, MybooksActivity.class));
                    break;
                case 0x008:
                    Toast.makeText(MybookManagerActivity.this, "删除书籍失败", Toast.LENGTH_SHORT).show();
                    break;

                case 0x00A:
                    Toast.makeText(MybookManagerActivity.this, "修改书籍失败", Toast.LENGTH_SHORT).show();
                    break;
                case 0x0926:
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(getApplicationContext(), InitActivity.class);
                    startActivity(intent);
                    break;
                default:
                    Toast.makeText(MybookManagerActivity.this, "Invalid msg.what", Toast.LENGTH_SHORT).show();
                    break;
            }
        };
    };


    private void set_book(){
        if(bookInfo == null)
            return;

        et_author.setText( bookInfo.getAuthor());
//        et_author.setFocusable(false);
//        et_author.setFocusableInTouchMode(false);


        et_catalog.setText(bookInfo.getCatalog());
//        et_catalog.setFocusable(false);
//        et_catalog.setFocusableInTouchMode(false);


        et_bookname.setText( bookInfo.getTitle());
//        et_bookname.setFocusable(false);
//        et_bookname.setFocusableInTouchMode(false);

        et_press.setText(bookInfo.getPublisher());
//        et_press.setFocusable(false);
//        et_press.setFocusableInTouchMode(false);

        et_abstract.setText(bookInfo.getSummary());
//        et_abstract.setFocusable(false);
//        et_abstract.setFocusableInTouchMode(false);

        et_originalprice.setText(bookInfo.getPrice());
//        et_originalprice.setFocusable(false);
//        et_originalprice.setFocusableInTouchMode(false);

        iv_originalimg.setImageURL(bookInfo.getImagePath());
        et_isbn.setText(bookInfo.getISBN());
//        et_isbn.setFocusable(false);
//        et_isbn.setFocusableInTouchMode(false);


        et_description.setText(bookInfo.getDescription());
        et_sellprice.setText(bookInfo.getSellPrice());


        iv_tempimg.setImageURL(bookInfo.getCurrentImg());


        for (int i=0;i<ValidBookTypes.length;i++)
        {
            if(bookInfo.getBookType().equals(ValidBookTypes[i]))
            {
                spinner.setSelection(i);
                break;
            }
        }


    }

}
