package com.example.alphapav.book.fragment;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alphapav.book.AddBookActivity;
import com.example.alphapav.book.BookDetailActivity;
import com.example.alphapav.book.LoginActivity;
import com.example.alphapav.book.R;
import com.example.alphapav.book.base.BaseFragment;
import com.example.alphapav.book.util.APIUtils;
import com.example.alphapav.book.util.BookInfo;
import com.example.alphapav.book.util.BookView;
import com.example.alphapav.book.util.GetData;
import com.example.alphapav.book.util.SharedHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.sf.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class HomeFragment extends BaseFragment {

    View view;
    private SharedHelper sh;
    private Context mContext;

    ArrayList<BookInfo> books = new ArrayList<>();

    ListView booklist = null;

    MyAdater myAdapter;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        mContext= Objects.requireNonNull(getActivity()).getApplicationContext();
        sh= new SharedHelper(mContext);
        bindViews();
        InitPost();
        return view ;
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

    private void bindViews() {
        booklist = view.findViewById(R.id.books);
        myAdapter = new MyAdater(mContext);
        booklist.setAdapter(myAdapter);
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
                    // get extra paraments
                    Intent intent = new Intent(mContext, BookDetailActivity.class);
                    intent.putExtra("book", new Gson().toJson(bookInfo));
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
            ori_price.setPaintFlags(ori_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            ori_price.setText(bookInfo.getPrice()+"¥");

            return convertView;
        }
    }

    public void InitPost()
    {
        new Thread(){
            @Override
            public void run() {
                super.run();

                HashMap<String, String> argu = new HashMap<>();
                argu.put("IsMyBook", "0");
                argu.put("BookType","");
                argu.put("Keyword", "");
                argu.put("Username", sh.get(SharedHelper.TEMPUSERNAME));

                String res = GetData.getFormbodyPostData(APIUtils.SELECTBOOK_URL, argu);

                JSONObject jsStr= JSONObject.fromObject(res);
                Log.i("XIAOXIE", "run: " + "selectbook");
                Log.i("XIAOXIE", res);

                if(jsStr.containsKey("login")){
                    if(jsStr.containsKey("error")){
                        handler.sendEmptyMessage(0x007);
                    }
                    else{
                        Log.i("XIAOXIE", jsStr.getString("result"));
                        ArrayList<BookInfo> temp_books = new Gson().fromJson(jsStr.getString("result"), new TypeToken<ArrayList<BookInfo>>(){}.getType());
                        if(temp_books != null){
                            books = temp_books;
                            handler.sendEmptyMessage(0x00A);
                        }
                    }
                }
                else{
                    handler.sendEmptyMessage(0x0926);
                }
            }
        }.start();


    }


}
