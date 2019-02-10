package com.example.alphapav.book;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import com.example.alphapav.book.util.BookView;
import com.example.alphapav.book.util.GetData;
import com.example.alphapav.book.util.SharedHelper;
import com.example.alphapav.book.util.TimeUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.w3c.dom.Text;

import java.io.File;
import java.net.URI;
import java.util.HashMap;

public class AddBookActivity extends AppCompatActivity {

    int RESULT_LOAD_IMAGE = 0x0926;

    String ISBN="";

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
    private ImageView imgview_return;
    private Button btn_ok;

    JSONObject book_json = null;
    private File file = null;
    private String pic_url = null;
    private Context mContext;
    String[]  ValidBookTypes={"人文类","传媒类","外语类", "经济类", "管理类", "理学类",
            "生命环境化学与地学类", "能源化工与高分子类", "机械与材料类","建筑与土木类", "电气与自动化类", "航空航天与过程装备类","计算机类",
            "信息类", "海洋类", "农学类", "生工食品类","医学类", "药学类", "艺术设计类", "其他类"};
    SharedHelper sh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        mContext= getApplicationContext();
        // get key
        ISBN = getIntent().getStringExtra("ISBN");
        bindViews();
        getISBNInfo();
    }
    private void bindViews()
    {
        sh = new SharedHelper(getApplicationContext());
        imgview_return= findViewById(R.id.headline_addbook_back);

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
        btn_ok = findViewById(R.id.addbool_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addbook();
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

    private void upload_image(String pic_path){
        try {
            file = new File(pic_path);
            if(file != null){
                Toast.makeText(AddBookActivity.this, "上传图片...", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(AddBookActivity.this, "未找到图片", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            file = null;
        }
    }

    private void addbook(){
        final String des = et_description.getText().toString();
        final String price = et_sellprice.getText().toString();
        final String type = ValidBookTypes[spinner.getSelectedItemPosition()];
        if (pic_url==null)
        {
            handler.sendEmptyMessage(0x00B);
            return;
        }
        new Thread(){
            @Override
            public void run() {
                super.run();

                HashMap<String, String> argu = new HashMap<>();
                argu.put("ISBN", ISBN);
                argu.put("CurrentPicture",pic_url);
                argu.put("BookType", type);
                argu.put("Description", des);
                argu.put("SellPrice", price);
                argu.put("Username", sh.get(SharedHelper.TEMPUSERNAME));

                String res = GetData.getFormbodyPostData(APIUtils.ADDBOOK_URL, argu);
                JSONObject res_json = JSONObject.fromObject(res);
                Log.e("XIAOXIE", "run: " + type);
                if(res_json.containsKey("login")){
                    if(res_json.containsKey("error")){
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

    private void getISBNInfo()
    {
        new Thread() {
            public void run() {
                int msg_what=0x006;
                //begin request
                try {
                    HashMap<String,String> map = new HashMap<>();

                    map.put("isbn",ISBN);
                    String result= GetData.getFormbodyPostData(APIUtils.ISBNBOOKINFO_URL, map);
                    JSONObject jsStr= JSONObject.fromObject(result);
                    if(jsStr == null){
                        msg_what = 0x007;
                    }else
                    {
                        if(jsStr.containsKey("error") ){
                            msg_what=0x002;
                            book_json = null;
                        }
                        else
                        {
                            book_json = jsStr;
                            msg_what = 0x003;
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


    //用于刷新界面和跳转页面
    @SuppressLint("HandlerLeak")

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {

                case 0x001:
                    Toast.makeText(AddBookActivity.this, "服务器未响应", Toast.LENGTH_SHORT).show();
                    break;
                case 0x002:
                    Toast.makeText(AddBookActivity.this, "ISBN错误", Toast.LENGTH_SHORT).show();
                    break;
                case 0x003:
                    Toast.makeText(AddBookActivity.this, "获取成功", Toast.LENGTH_SHORT).show();
                    set_book();
                    break;
                case 0x004:
                    Toast.makeText(AddBookActivity.this, "正在获取书籍信息", Toast.LENGTH_SHORT).show();
                    break;
                case 0x006:
                    Toast.makeText(AddBookActivity.this, "图片上传成功", Toast.LENGTH_SHORT).show();
                    if(pic_url != null){
                        iv_tempimg.setImageURL(pic_url);
                    }
                    break;
                case 0x00A:
                    Toast.makeText(AddBookActivity.this, "增加书籍成功", Toast.LENGTH_SHORT).show();
                    finish();

                    break;
                case 0x00B:
                    Toast.makeText(AddBookActivity.this, "请您先上传书籍现状照片！", Toast.LENGTH_SHORT).show();
                    break;

                case 0x0926:
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(getApplicationContext(), InitActivity.class);
                    startActivity(intent);
                    break;
                default:
                    Toast.makeText(AddBookActivity.this, "Invalid msg.what", Toast.LENGTH_SHORT).show();
                    break;
            }
        };
    };


    private void set_book(){
        if(book_json == null)
            return;

        if(book_json.containsKey("author"))
        {

            String author = book_json.getString("author");

            et_author.setText( author);


        }
        if(book_json.containsKey("catalog"))
        {
            Log.i("XIAOXIE",  book_json.getString("catalog"));
            et_catalog.setText( book_json.getString("catalog"));

        }
        if(book_json.containsKey("title"))
        {
            Log.i("XIAOXIE", book_json.getString("title"));
            et_bookname.setText( book_json.getString("title"));


        }
        if(book_json.containsKey("publisher"))
        {
            Log.i("XIAOXIE",  book_json.getString("publisher"));
            et_press.setText( book_json.getString("publisher"));

        }
        if(book_json.containsKey("summary"))
        {
            Log.i("XIAOXIE",  book_json.getString("summary"));
            et_abstract.setText( book_json.getString("summary"));

        }
        if(book_json.containsKey("price"))
        {
            Log.i("XIAOXIE",  book_json.getString("price"));
            String price = book_json.getString("price");
            //   System.out.println(price);
            price= price.split("元")[0];
            et_originalprice.setText( price);



        }
        et_isbn.setText(ISBN);
        et_isbn.setFocusable(false);
        et_isbn.setFocusableInTouchMode(false);

        if(book_json.containsKey("middeimg")){
            iv_originalimg.setImageURL(book_json.getString("middeimg"));

        }
    }


}
