package com.example.alphapav.book.base;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.alphapav.book.util.ActivityController;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityController.addActivity(this);
        ActivityController.setCurrentActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityController.removeActivity(this);
    }
}