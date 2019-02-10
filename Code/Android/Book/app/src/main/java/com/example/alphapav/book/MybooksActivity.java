package com.example.alphapav.book;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alphapav.book.fragment.HomeFragment;
import com.example.alphapav.book.util.APIUtils;
import com.example.alphapav.book.util.BookInfo;
import com.example.alphapav.book.util.BookView;
import com.example.alphapav.book.util.GetData;
import com.example.alphapav.book.util.SharedHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MybooksActivity extends AppCompatActivity {

    private SharedHelper sh;
    private Context mContext;

    ArrayList<BookInfo> books = new ArrayList<>();

    ListView booklist = null;

    MyAdater myAdapter;
    private ImageView imgview_return;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mybooks);

        mContext = getApplicationContext();
        sh = new SharedHelper(mContext);


        imgview_return= findViewById(R.id.headline_mybooks_back);

        imgview_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });


        booklist = findViewById(R.id.books);
        myAdapter = new MyAdater(mContext);
        booklist.setAdapter(myAdapter);

        InitPost();
    }

    public class MyAdater extends BaseAdapter {

        private LayoutInflater layoutInflater;

        public MyAdater(Context context){
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return books.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = layoutInflater.inflate(R.layout.item_book, null);
            final BookInfo bookInfo = books.get(position);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*test*/
                    String content= new Gson().toJson(bookInfo);
                    // get extra paraments
                    Intent intent = new Intent();
                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(mContext, MybookManagerActivity.class);
                    intent.putExtra("Book", content);
                    // jump to login
                    startActivity(intent);

                }
            });


            //image
            BookView img = convertView.findViewById(R.id.img);
            img.setImageURL(bookInfo.getImagePath());

            //name
            TextView txt_name = convertView.findViewById(R.id.name);
            txt_name.setText(bookInfo.getTitle());

            //author
            TextView txt_author = convertView.findViewById(R.id.author);
            txt_author.setText(bookInfo.getAuthor());

            //type
            TextView txt_type = convertView.findViewById(R.id.type);
            txt_type.setText(bookInfo.getBookType());

            Button btn_price = convertView.findViewById(R.id.sell_price);
            btn_price.setText(bookInfo.getSellPrice() + "¥");



            TextView ori_price = convertView.findViewById(R.id.ori_price);
            ori_price.setText(bookInfo.getPrice()+ "¥");

            ori_price.setPaintFlags(ori_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            return convertView;
        }
    }


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0x001:
                    Toast.makeText(getApplicationContext(), "登出成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    break;

                case 0x003:
                    Toast.makeText(getApplicationContext(),"Wrong response format", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(mContext, LoginActivity.class));
                    break;
                case 0x007:
                    Toast.makeText(mContext, "error", Toast.LENGTH_SHORT).show();
                    break;
                case 0x00A:
                    myAdapter.notifyDataSetChanged();
                    break;
                default:
                    Toast.makeText(mContext, "Invalid msg.what", Toast.LENGTH_SHORT).show();
                    break;
            }
        };
    };

    private void InitPost() {
        new Thread() {
            @Override
            public void run() {
                super.run();

                HashMap<String, String> argu = new HashMap<>();
                argu.put("IsMyBook", "1");
                argu.put("BookType", "");
                argu.put("Keyword", "");
                argu.put("Username", sh.get(SharedHelper.TEMPUSERNAME));

                String res = GetData.getFormbodyPostData(APIUtils.SELECTBOOK_URL, argu);

                JSONObject jsStr = JSONObject.fromObject(res);
                Log.i("XIAOXIE", "run: " + "selectbook");
                Log.i("XIAOXIE", res);

                if (jsStr.containsKey("login")) {
                    if (jsStr.containsKey("error")) {
                        handler.sendEmptyMessage(0x007);
                    } else {
                        Log.i("XIAOXIE", jsStr.getString("result"));
                        ArrayList<BookInfo> temp_books = new Gson().fromJson(jsStr.getString("result"), new TypeToken<ArrayList<BookInfo>>() {
                        }.getType());
                        if (temp_books != null) {
                            books = temp_books;
                            handler.sendEmptyMessage(0x00A);
                        }
                    }
                } else {
                    handler.sendEmptyMessage(0x0926);
                }
            }
        }.start();
    }
}
