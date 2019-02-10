package com.example.alphapav.book;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.alphapav.book.fragment.HomeFragment;
import com.example.alphapav.book.fragment.SearchFragment;
import com.example.alphapav.book.fragment.UsercenterFragment;
import com.example.alphapav.book.util.ActivityUtils;
import com.example.alphapav.book.util.SharedHelper;
import com.example.alphapav.book.util.TimeUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private List<Fragment> mFragmentList;
    private Fragment mCurrentFragment;
    private Toolbar mToolbar;
    private RadioGroup mTabRadioGroup;
    private SharedHelper sh;
    private Context mContext;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    try {
                        switchFragment(mCurrentFragment, mFragmentList.get(0));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    return true;
                case R.id.navigation_search:
                    try {
                        switchFragment(mCurrentFragment, mFragmentList.get(1));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    return true;
                case R.id.navigation_usercenter:
                    try {
                        switchFragment(mCurrentFragment, mFragmentList.get(2));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return true;
            }
            return false;
        }
    };

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("LOGTAG"," Mainactivity onCreate(Bundle savedInstanceState)");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mContext= getApplicationContext();
        sh= new SharedHelper(mContext);

        mFragmentList = new ArrayList<>();
        mFragmentList.add(HomeFragment.newInstance());
        mFragmentList.add(SearchFragment.newInstance());
        mFragmentList.add(UsercenterFragment.newInstance());

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), mFragmentList.get(0), R.id.fragment);
        mCurrentFragment = mFragmentList.get(0);



    }

    public void switchFragment(Fragment from, Fragment to) throws ParseException {
        if (mCurrentFragment != to) {
            mCurrentFragment = to;
            FragmentTransaction transaction = getSupportFragmentManager().
                    beginTransaction();

            if (!to.isAdded()) {
                Log.i("LOGCAT","to is added not true");
                transaction.hide(from).add(R.id.fragment, to).commit();

            } else {
                TextView text_usercenter_time=(TextView)findViewById(R.id.usercenter_time);
                long diff = TimeUtils.getMinuteDiff(TimeUtils.getCurrentTime(), sh.get(SharedHelper.LOGINTIME));
                if(text_usercenter_time != null)
                    text_usercenter_time.setText(" 已登陆"+diff+"分钟 ");
                transaction.hide(from).show(to).commit();


            }
        }
    }
}
