package com.example.shanegifford.loogle;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity {
    private boolean[] bValidToilets;
    ArrayList<Toilet> possibleToilets, ValidToilets;
    private Toilet locationC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Bundle extras = getIntent().getExtras();
        bValidToilets = extras.getBooleanArray("ToiletArray");      //unpack boolean array from extras
        ValidToilets = new ArrayList<Toilet>();

        final ValueEventListener toiletLoader = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                String provider = locationManager.getBestProvider(criteria, false);
                if (checkSelfPermission("android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED) {
                    Location location = locationManager.getLastKnownLocation(provider);     //get current phone location
                    locationC = new Toilet();
                    locationC.setLongitude(location.getLongitude());        //convert location into Toilet class
                    locationC.setLatitude(location.getLatitude());
                }

                possibleToilets = new ArrayList<Toilet>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Toilet toilet = child.getValue(Toilet.class);           //unpack Toilets from server snapshot into Toilet arraylist
                    possibleToilets.add(toilet);
                }
                for (int i = 0; i < bValidToilets.length; i++) {
                    if (bValidToilets[i]) {
                        ValidToilets.add((possibleToilets.get(i)));         //if corresponding boolean from array is true, (i.e. toilet passed all filters),
                    }                                                       //add toilet to ValidToilets listarray
                }
                for (int i = 0; i < ValidToilets.size(); i++) {
                    Button button = new Button(ResultsActivity.this);           //create new button in UI for each valid toilet, setting
                    button.setId(i);                                            //button text to distance from user.
                    final int bid = button.getId();
                    DecimalFormat numberFormat = new DecimalFormat("#.00");
                    button.setText(numberFormat.format(ValidToilets.get(bid).CalculationByDistance(locationC, ValidToilets.get(bid))) + " km away");
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    LinearLayout layout = (LinearLayout)findViewById(R.id.linearlayout);
                    layout.addView(button, params);
                    Button btn1 = ((Button) findViewById(bid));
                    btn1.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + ValidToilets.get(bid).getLatitude() + "," + ValidToilets.get(bid).getLongitude()));
                            intent.setPackage("com.google.android.apps.maps");
                            if (intent.resolveActivity(getPackageManager()) != null) {          //On button press, sends coords to Google Maps as destination through URI
                                startActivity(intent);                                          //and starts Google Maps app
                            }
                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ResultsActivity.this, "Database Error", Toast.LENGTH_LONG).show();
            }
        };

        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(toiletLoader);
    }
}
