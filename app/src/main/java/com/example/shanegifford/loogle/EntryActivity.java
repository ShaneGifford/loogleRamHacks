package com.example.shanegifford.loogle;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RatingBar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EntryActivity extends AppCompatActivity {
    public Toilet toilet;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        toilet = new Toilet();
        Bundle extras = getIntent().getExtras();
        toilet.setLatitude(extras.getDouble("lat"));    //unpack latitude and longitude from extras
        toilet.setLongitude(extras.getDouble("lon"));

        final RatingBar stars = findViewById(R.id.rating_stars);
        final CheckBox checkBox = findViewById(R.id.checkBox);

        final Button buttonSubmit = findViewById(R.id.button_submit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toilet.cleanliness = stars.getRating();         //when submit button is pressed, reads other inputs,
                toilet.isAccessible = checkBox.isChecked();     //sets Toilet parameters based on inputs and sends to the FireBase

                ref = FirebaseDatabase.getInstance().getReference("toilet " + Calendar.getInstance().getTime());    //generates unique ID's through system time
                ref.setValue(toilet);
                finish();
            }
        });
    }
}
