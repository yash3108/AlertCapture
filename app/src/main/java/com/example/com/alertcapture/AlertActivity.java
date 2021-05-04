package com.example.com.alertcapture;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static java.lang.Thread.sleep;

public class AlertActivity extends AppCompatActivity {

    Switch sw1, sw2;
    //Button stop;
    LocationManager locationManager;
    private static final int REQUEST_LOCATION=1;
    String s1, latitude,longitude;
    double lo,lal;
    Vibrator vibrator;
    double f_lat, f_lon;
    double rEarth = 6371.01, epsilon = 0.000001;
    double x_upper, x_lower, y_upper, y_lower;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        sw1 = (Switch)findViewById(R.id.switch1);
        sw2 = (Switch)findViewById(R.id.stop);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        SharedPreferences sharedPreferences = getSharedPreferences("save", MODE_PRIVATE);
        sw1.setChecked(sharedPreferences.getBoolean("value", false));

        sw1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sw1.isChecked()){
                    SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                    editor.putBoolean("value", true);
                    editor.apply();
                    sw1.setChecked(true);
                }
                else{
                    SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                    editor.putBoolean("value", false);
                    editor.apply();
                    sw1.setChecked(false);
                }
            }
        });



        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference demoref1 = database.getReference("Vibrate/LocationVar");
//        DatabaseReference demoref2 = database.getReference("Vibrate/V1");

        demoref1.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                sw2.setChecked(false);
                Boolean b = sharedPreferences.getBoolean("value", false);
                if(b) {
                    String value = (String) dataSnapshot.getValue();
                    s1 = value;
                    System.out.println("Value" + value);
                    Log.i("Value: ", value);
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        OnGPS();
                    } else {
                        getLocation();
                    }
                    if (initiateAlert()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                            //vibrate();
                                long[] pattern = {60, 120, 180, 240, 300, 360, 420, 480};
                                vibrator.vibrate(pattern, -1);
//                            if (b == true) {
//                            v.vibrate(VibrationEffect.createOneShot(500,
//                                    VibrationEffect.DEFAULT_AMPLITUDE));
//                            }
                        } else {
                            //deprecated in API 26
                            //vibrate();
                                long[] pattern = {60, 120, 180, 240, 300, 360, 420, 480};
                                vibrator.vibrate(pattern, -1);
//                            if (b == true) {
//                            v.vibrate(500);
//                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }


            private void getLocation() {
                if (ActivityCompat.checkSelfPermission(AlertActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AlertActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AlertActivity.this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                } else {
                    Location LocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    Location LocationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    Location LocationPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

                    if (LocationGPS != null) {
                        double lat = LocationGPS.getLatitude();
                        double longi = LocationGPS.getLongitude();

                        latitude = String.valueOf(lat);
                        longitude = String.valueOf(longi);

                        lo = Double.parseDouble(longitude);
                        lal = Double.parseDouble(latitude);

//                                    showlocation.setText("LOCATION" + "\n" + "LATITUDE" + latitude + "\n" + "LONGITUDE" + longitude);
                    } else if (LocationNetwork != null) {
                        double lat = LocationNetwork.getLatitude();
                        double longi = LocationNetwork.getLongitude();

                        latitude = String.valueOf(lat);
                        longitude = String.valueOf(longi);

                        lo = Double.parseDouble(longitude);
                        lal = Double.parseDouble(latitude);

//                                    showlocation.setText("LOCATION" + "\n" + "LATITUDE" + latitude + "\n" + "LONGITUDE" + longitude);
                    } else if (LocationPassive != null) {
                        double lat = LocationPassive.getLatitude();
                        double longi = LocationPassive.getLongitude();

                        latitude = String.valueOf(lat);
                        longitude = String.valueOf(longi);

                        lo = Double.parseDouble(longitude);
                        lal = Double.parseDouble(latitude);

//                                    showlocation.setText("LOCATION" + "\n" + "LATITUDE" + latitude + "\n" + "LONGITUDE" + longitude);
                    } else {
                        Toast.makeText(AlertActivity.this, "CANNOT DETECT", Toast.LENGTH_SHORT).show();
                    }

                }
                Log.i("Latitude",latitude);
                Log.i("Longitude", longitude);
            }

            private void OnGPS() {
                final AlertDialog.Builder builder = new AlertDialog.Builder(AlertActivity.this);
                builder.setMessage("ENABLE GPS").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }

            void vibrate(){
                long[] pattern = {500};
                vibrator.vibrate(pattern, 0);
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(sw2.isChecked()){
                    vibrator.cancel();
                    return;
                }
                else{
                    vibrate();
                }
            }

        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent switchActivityIntent = new Intent(this, MainActivity.class);
        startActivity(switchActivityIntent);
    }

    void convertDataFromFirebase(String s){
        String s1[] = s.split("[/]",0);
        f_lat = Double.parseDouble(s1[0]);
        f_lon = Double.parseDouble(s1[1]);
    }

    double pointRadialDistance_x(double lat, double lon, int bearing, int distance){
        double rlat1 = Math.toRadians(lat);
        double rlon1 = Math.toRadians(lon);
        double rbearing = Math.toRadians(bearing);
        double rdistance = distance / rEarth;
        double rlon;

        double rlat = Math.asin( Math.sin(rlat1) * Math.cos(rdistance) + Math.cos(rlat1) * Math.sin(rdistance) * Math.cos(rbearing));

        if (Math.cos(rlat) == 0 || Math.abs(Math.cos(rlat)) < epsilon){
            rlon = rlon1;

        }
        else{
            rlon = ( (rlon1 - Math.asin( Math.sin(rbearing)* Math.sin(rdistance) / Math.cos(rlat) ) + Math.PI ) % (2*Math.PI) ) - Math.PI;
        }

        double lats = Math.toDegrees(rlat);
        double lons = Math.toDegrees(rlon);

        return (lats);
    }

    double pointRadialDistance_y(double lat, double lon, int bearing, int distance){
        double rlat1 = Math.toRadians(lat);
        double rlon1 = Math.toRadians(lon);
        double rbearing = Math.toRadians(bearing);
        double rdistance = distance / rEarth;
        double rlon;

        double rlat = Math.asin( Math.sin(rlat1) * Math.cos(rdistance) + Math.cos(rlat1) * Math.sin(rdistance) * Math.cos(rbearing));

        if (Math.cos(rlat) == 0 || Math.abs(Math.cos(rlat)) < epsilon){
            rlon = rlon1;

        }
        else{
            rlon = ( (rlon1 - Math.asin( Math.sin(rbearing)* Math.sin(rdistance) / Math.cos(rlat) ) + Math.PI ) % (2*Math.PI) ) - Math.PI;
        }

        double lats = Math.toDegrees(rlat);
        double lons = Math.toDegrees(rlon);

        return (lons);
    }

    void calculate(){
        x_upper = pointRadialDistance_x(f_lat, f_lon, 0, 5);
        x_lower = pointRadialDistance_x(f_lat, f_lon, 180, 5);
        y_upper = pointRadialDistance_y(f_lat, f_lon, 270, 5);
        y_lower = pointRadialDistance_y(f_lat, f_lon, 90, 5);
    }

    boolean initiateAlert(){
        convertDataFromFirebase(s1);
        calculate();
        if(lal <= x_upper && lal >= x_lower && lo <= y_upper && lo >= y_lower){
            return true;
        }
        return false;
    }

}