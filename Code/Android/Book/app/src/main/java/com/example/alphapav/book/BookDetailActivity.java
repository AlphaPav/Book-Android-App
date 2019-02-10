package com.example.alphapav.book;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.alphapav.book.fragment.BooksellerFragment;
import com.example.alphapav.book.fragment.DetailFragment;
import com.example.alphapav.book.util.APIUtils;
import com.example.alphapav.book.util.BookInfo;
import com.example.alphapav.book.util.GetData;
import com.example.alphapav.book.util.SharedHelper;
import com.example.alphapav.book.util.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BookDetailActivity extends AppCompatActivity {

    Context mContext;
    SharedHelper sh;

    // to manage fragments and switch view
    private ViewPager mViewPager;
    private RadioGroup mTabRadioGroup;
    private List<Fragment> mFragments;
    private FragmentPagerAdapter mAdapter;

    static BookInfo bookInfo = null;
    static User owner = null;

    String error = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        mContext = getApplicationContext();
        sh = new SharedHelper(mContext);


        get_book();

        initView();
        if(bookInfo == null){
            finish();
        }

    }

    private void get_book(){
        Intent intent = getIntent();
        String bookStr = intent.getStringExtra("book");
        if(bookStr == null || "".equals(bookStr)){
            return;
        }
        // try
        try {
            bookInfo = new Gson().fromJson(bookStr, BookInfo.class);
        }catch (Exception e){
             bookInfo = null;
        }
    }

    private void initView() {
        // find view
        mViewPager = findViewById(R.id.fragment_vp);
        mTabRadioGroup = findViewById(R.id.tabs_rg);

        // init fragment
        mFragments = new ArrayList<>(2);
        mFragments.add(DetailFragment.newInstance(bookInfo.getBookID()));
        mFragments.add(BooksellerFragment.newInstance(bookInfo.getBookID(),bookInfo.getUsername()));

        // init view pager
        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), mFragments);
        mViewPager.setAdapter(mAdapter);

        // register listener
        mViewPager.addOnPageChangeListener(mPageChangeListener);
        mTabRadioGroup.setOnCheckedChangeListener(mOnCheckedChangeListener);
    }

    // change page by navigation(ratio)
    private RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
        for (int i = 0; i < group.getChildCount(); i++) {
            if (group.getChildAt(i).getId() == checkedId) {
                mViewPager.setCurrentItem(i);
                return;
            }
        }
        }
    };

    //Adapter
    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> mList;

        // to manage the
        public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            this.mList = list;
        }

        @Override
        public Fragment getItem(int position) {
            return this.mList == null ? null : this.mList.get(position);
        }

        @Override
        public int getCount() {
            return this.mList == null ? 0 : this.mList.size();
        }
    }


    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        // change ratio group chosen by choose page
        @Override
        public void onPageSelected(int position) {
            for (int i = 0; i < 3; i++) {
                RadioButton radioButton = (RadioButton) mTabRadioGroup.getChildAt(i);
                if (i == position) {
                    radioButton.setChecked(true);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };
}
