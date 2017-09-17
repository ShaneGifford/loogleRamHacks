package com.example.shanegifford.loogle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ResultsActivity extends AppCompatActivity {
    private boolean[] validToilets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Bundle extras = getIntent().getExtras();
        validToilets = extras.getBooleanArray("ToiletArray");


    }
}
