package com.example.alphapav.book.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.app.SearchManager;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alphapav.book.BookDetailActivity;
import com.example.alphapav.book.LoginActivity;
import com.example.alphapav.book.MainActivity;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class SearchFragment extends BaseFragment {

    View view;
    private SharedHelper sh;
    private Context mContext;
    Spinner spinner;
    private SearchView mSearchView;

    String keyword= "";

    ArrayList<BookInfo> books = new ArrayList<>();


    ListView booklist = null;

    MyAdater myAdapter;

    String[]  ValidBookTypes={"人文类","传媒类","外语类", "经济类", "管理类", "理学类",
            "生命环境化学与地学类", "能源化工与高分子类", "机械与材料类","建筑与土木类", "电气与自动化类", "航空航天与过程装备类","计算机类",
            "信息类", "海洋类", "农学类", "生工食品类","医学类", "药学类", "艺术设计类", "其他类","所有类"};
    public static SearchFragment newInstance() {
        return new SearchFragment();
    }
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.fragment_search, container, false);
            mContext= Objects.requireNonNull(getActivity()).getApplicationContext();
            sh= new SharedHelper(mContext);
            bindViews();

        return view ;
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

            ori_price.setText(bookInfo.getPrice()+ "¥");

            return convertView;
        }
    }


    private void bindViews() {
        mSearchView = (SearchView) view.findViewById(R.id.searchview);
        spinner=(Spinner) view.findViewById(R.id.searchbook_spinner_booktype);

        ArrayAdapter<String> adapter= new ArrayAdapter<String>(
                mContext,android.R.layout.simple_spinner_dropdown_item ,ValidBookTypes);
        spinner.setAdapter(adapter);
        spinner.setSelection(ValidBookTypes.length-1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                              public void onItemSelected(AdapterView<?> parent, View view, int pos,
                                                                         long id) {
//                                                  Toast.makeText(parent.getContext(),
//                                                          "您选择了：\n" + parent.getItemAtPosition(pos).toString(),
//                                                          Toast.LENGTH_LONG).show();
                                                  searchbook();

                                              }

                                              @Override
                                              public void onNothingSelected(AdapterView<?> arg0) {
                                                  spinner.setSelection(ValidBookTypes.length-1);
                                              }
                                          });


        booklist = view.findViewById(R.id.searchbooklist);
        myAdapter = new MyAdater(mContext);
        booklist.setAdapter(myAdapter);

        mSearchView.setSubmitButtonEnabled(true);
        // 设置搜索文本监听
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            //输入完成后，提交时触发的方法，一般情况是点击输入法中的搜索按钮才会触发，表示现在正式提交了
            public boolean onQueryTextSubmit(String query)
            {
                if(TextUtils.isEmpty(query))
                {
                    Toast.makeText(mContext, "请输入查找内容！", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Log.i("XIAOXIE", "onQueryTextSubmit: "+ query);
                    keyword= query;
                    searchbook();
                }
                return true;
            }
            //在输入时触发的方法，当字符真正显示到searchView中才触发，像是拼音，在输入法组词的时候不会触发
            public boolean onQueryTextChange(String newText)
            {
//                if(TextUtils.isEmpty(newText))
//                {
////                    Toast.makeText(mContext, "请输入查找内容！", Toast.LENGTH_SHORT).show();
//
//                }
//                else
//                {
//                    Log.i("XIAOXIE", "onQueryTextSubmit: "+ newText);
//                    keyword= newText;
//                    searchbook();
//                }
//                return true;
                return false;

            }

        });


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
                    Toast.makeText(getActivity(), "搜索成功", Toast.LENGTH_SHORT).show();
                    myAdapter.notifyDataSetChanged();
                    break;
                default:
                    Toast.makeText(mContext, "Invalid msg.what", Toast.LENGTH_SHORT).show();
                    break;
            }
        };
    };


    private void searchbook()
    {
        final String booktype;
        Log.i("XIAOXIE", "searchbook: "+ValidBookTypes[spinner.getSelectedItemPosition()]);
        if(ValidBookTypes[spinner.getSelectedItemPosition()].equals("所有类"))
        {
            booktype="";
        }else
        {
            booktype= ValidBookTypes[spinner.getSelectedItemPosition()];
        }

        new Thread(){
            @Override
            public void run() {
                super.run();

                HashMap<String, String> argu = new HashMap<>();
                argu.put("Keyword",keyword);
                argu.put("BookType", booktype);
                argu.put("IsMyBook", "0");
                argu.put("Username", sh.get(SharedHelper.TEMPUSERNAME));

                String res = GetData.getFormbodyPostData(APIUtils.SELECTBOOK_URL, argu);
                JSONObject jsStr = JSONObject.fromObject(res);
                Log.e("XIAOXIE", "run: " + "selectbook");
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


