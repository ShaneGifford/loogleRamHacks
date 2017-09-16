package com.example.shanegifford.loogle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickFilters() {
        //send to Filters activity
    }

    public void onClickEmergency() {
        //send to Emergency activity
    }
}
