package com.example.shanegifford.loogle;

import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.google.firebase.database.ValueEventListener;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class FilterActivity extends AppCompatActivity {

    public Toilet criteria;
    private LocationManager locationManager;
    private String provider;
    private Location location;
    private Toilet locationC;
    public Toilet[] toilets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        criteria = new Toilet();

        final RatingBar starsMin = findViewById(R.id.stars_min);
        final EditText editText = findViewById(R.id.editText);

        final CheckBox accessCheck = findViewById(R.id.access_check);
        accessCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        final Button buttonSearch = findViewById(R.id.button_search);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                criteria.cleanliness = starsMin.getRating();
                criteria.isAccessible = accessCheck.isChecked();
                criteria.setLatitude(Double.parseDouble(editText.getText().toString()));

                ValueEventListener toiletFinder = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Toilet> possibleToilets = new List<Toilet>() {
                            @Override
                            public int size() {
                                return 0;
                            }

                            @Override
                            public boolean isEmpty() {
                                return false;
                            }

                            @Override
                            public boolean contains(Object o) {
                                return false;
                            }

                            @NonNull
                            @Override
                            public Iterator<Toilet> iterator() {
                                return null;
                            }

                            @NonNull
                            @Override
                            public Object[] toArray() {
                                return new Object[0];
                            }

                            @NonNull
                            @Override
                            public <T> T[] toArray(@NonNull T[] ts) {
                                return null;
                            }

                            @Override
                            public boolean add(Toilet toilet) {
                                return false;
                            }

                            @Override
                            public boolean remove(Object o) {
                                return false;
                            }

                            @Override
                            public boolean containsAll(@NonNull Collection<?> collection) {
                                return false;
                            }

                            @Override
                            public boolean addAll(@NonNull Collection<? extends Toilet> collection) {
                                return false;
                            }

                            @Override
                            public boolean addAll(int i, @NonNull Collection<? extends Toilet> collection) {
                                return false;
                            }

                            @Override
                            public boolean removeAll(@NonNull Collection<?> collection) {
                                return false;
                            }

                            @Override
                            public boolean retainAll(@NonNull Collection<?> collection) {
                                return false;
                            }

                            @Override
                            public void clear() {

                            }

                            @Override
                            public Toilet get(int i) {
                                return null;
                            }

                            @Override
                            public Toilet set(int i, Toilet toilet) {
                                return null;
                            }

                            @Override
                            public void add(int i, Toilet toilet) {

                            }

                            @Override
                            public Toilet remove(int i) {
                                return null;
                            }

                            @Override
                            public int indexOf(Object o) {
                                return 0;
                            }

                            @Override
                            public int lastIndexOf(Object o) {
                                return 0;
                            }

                            @Override
                            public ListIterator<Toilet> listIterator() {
                                return null;
                            }

                            @NonNull
                            @Override
                            public ListIterator<Toilet> listIterator(int i) {
                                return null;
                            }

                            @NonNull
                            @Override
                            public List<Toilet> subList(int i, int i1) {
                                return null;
                            }
                        };
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            possibleToilets.add(child.getValue(Toilet.class));
                        }
                        if (checkSelfPermission("android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED) {
                            location = locationManager.getLastKnownLocation(provider);
                            locationC = new Toilet();
                            locationC.setLongitude(location.getLongitude());
                            locationC.setLatitude(location.getLatitude());
                        }
                        for (Toilet toilet : possibleToilets) {
                            if (toilet.cleanliness < criteria.cleanliness || (criteria.isAccessible && !toilet.isAccessible)) {
                                possibleToilets.remove(toilet);
                            }
                            else {
                                double currentDist = Toilet.CalculationByDistance(locationC, toilet);
                                if (currentDist > criteria.getLatitude()) {
                                    possibleToilets.remove(toilet);
                                }
                            }
                        }
                        toilets = (Toilet[])possibleToilets.toArray();
                        Intent i = new Intent(FilterActivity.this, ResultsActivity.class);
                        i.putExtra("ToiletList", toilets);
                        startActivity(i);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(FilterActivity.this, "Database Error", Toast.LENGTH_LONG).show();
                    }
                };
            }
        });
    }
}
