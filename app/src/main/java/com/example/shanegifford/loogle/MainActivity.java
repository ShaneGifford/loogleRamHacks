package com.example.shanegifford.loogle;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private TextView latituteField;
    private TextView longitudeField;
    private LocationManager locationManager;
    private String provider;
    private Location location;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private ValueEventListener toiletFinder;
    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latituteField = (TextView) findViewById(R.id.textLat);
        longitudeField = (TextView) findViewById(R.id.textLong);

        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!service.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please enable GPS", Toast.LENGTH_LONG).show();
        }
        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        location = new Location(provider);

        database = FirebaseDatabase.getInstance();


        toiletFinder = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Location closest = null;
                double dist = 0;
                if (checkSelfPermission("android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED) {
                    location = locationManager.getLastKnownLocation(provider);
                }
                if (location != null) {
                    long numToilets = dataSnapshot.getChildrenCount();
                    for (int i = 0; i < numToilets; i++) {
                        Location coord = toilet.getValue(Location.class)[i];
                        if (closest != null) {
                            if (dist > location.distanceTo(coord)) {
                                closest = coord;
                                dist = location.distanceTo(coord);
                            }
                        }
                        else {
                            closest = coord;
                            dist = location.distanceTo(coord);      //replace with better distance formula
                        }
                    }
                    if (closest != null) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:" + closest.getLatitude() + "," + closest.getLongitude()));
                        intent.setPackage("com.google.android.apps.maps");
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Can't find toilets from server", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "Can't find location", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Database Error", Toast.LENGTH_LONG).show();
            }
        };


        Button btn_filters = (Button) findViewById(R.id.button_filters);
        btn_filters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, FilterActivity.class);
                startActivity(i);
            }
        });

        Button btn_emergency = (Button) findViewById(R.id.button_emergency);
        btn_emergency.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 ref = database.getReference();
                 ref.addListenerForSingleValueEvent(toiletFinder);
                 //use closest toilet return value
             }
        });

        Button btn_getCoords = (Button) findViewById(R.id.button_getCoords);
        btn_getCoords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission("android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED) {
                    location = locationManager.getLastKnownLocation(provider);
                }
                if (location != null) {
                    latituteField.setText("Latitude: " + Double.toString(location.getLatitude()));
                    longitudeField.setText("Longitude: " + Double.toString(location.getLongitude()));

                    DatabaseReference myRef = database.getReference("coords" + Calendar.getInstance().getTime());

                    myRef.setValue(location);

                    Toast.makeText(MainActivity.this, "Data sent to FB", Toast.LENGTH_LONG).show();
                }
            }
        });


    }
}
