package com.example.shanegifford.loogle;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class FilterActivity extends AppCompatActivity {

    public Toilet filters;
    private LocationManager locationManager;
    private String provider;
    private Location location;
    private Toilet locationC;
    public Toilet[] toilets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        filters = new Toilet();

        final RatingBar starsMin = findViewById(R.id.stars_min);
        final EditText editText = findViewById(R.id.editText);
        final CheckBox accessCheck = findViewById(R.id.access_check);

        final Button buttonSearch = findViewById(R.id.button_search);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filters.cleanliness = starsMin.getRating();
                filters.isAccessible = accessCheck.isChecked();
                String inputString = editText.getText().toString();
                filters.setLatitude(Double.parseDouble((inputString.length() == 0 ? "100" : inputString) + ".0"));

                final ValueEventListener toiletFilter = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<Toilet> possibleToilets = new ArrayList<Toilet>();
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            Toilet toilet = child.getValue(Toilet.class);
                            possibleToilets.add(toilet);
                        }
                        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        Criteria criteria = new Criteria();
                        provider = locationManager.getBestProvider(criteria, false);
                        if (checkSelfPermission("android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED) {
                            location = locationManager.getLastKnownLocation(provider);
                            locationC = new Toilet();
                            locationC.setLongitude(location.getLongitude());
                            locationC.setLatitude(location.getLatitude());
                        }
                        boolean[] toiletElims = new boolean[possibleToilets.size()];
                        for (int i = 0; i < toiletElims.length; i++) {
                            toiletElims[i] = true;
                        }
                        for (Toilet toilet : possibleToilets) {
                            if (toilet.cleanliness < filters.cleanliness || (filters.isAccessible && !toilet.isAccessible)) {
                                toiletElims[possibleToilets.indexOf(toilet)] = false;
                            }
                            else {
                                double currentDist = toilet.CalculationByDistance(locationC, toilet);
                                if (currentDist > filters.getLatitude()) {
                                    toiletElims[possibleToilets.indexOf(toilet)] = false;
                                }
                            }
                        }
                        Intent i = new Intent(FilterActivity.this, ResultsActivity.class);
                        i.putExtra("ToiletArray", toiletElims);
                        startActivity(i);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(FilterActivity.this, "Database Error", Toast.LENGTH_LONG).show();
                    }
                };

                FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(toiletFilter);
            }
        });
    }
}
