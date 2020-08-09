package com.example.favoritelocations;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    LocationManager locationManager;
    LocationListener locationListener;
    Location loc;
    Intent intent;
    Marker marker;
    private GoogleMap mMap;
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    //checkPermission();
                }
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        intent=getIntent();
    }
    public boolean checkPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            return checkPermission();
        }
        else {
            return true;
        }
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        locationManager=(LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                //Log.i("Location Cnaged",location.toString());
               //Log.i("Location Cnaged",intent.getIntExtra("currentselection",0)+" "+MainActivity.firsttime);
                if(intent.getIntExtra("currentselection",0)==0 && checkPermission()==true){
                    if(loc==null || (!(loc.getLatitude()==location.getLatitude() && loc.getLongitude()==location.getLongitude()))){
                        Log.i("On location change","detect");
                        if(marker!=null)
                            marker.remove();
                        loc = location;
                        LatLng current = new LatLng(loc.getLatitude(), loc.getLongitude());
                        marker=mMap.addMarker(new MarkerOptions().position(current).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 15));
                    }
                }
            }
        };
        if(checkPermission()==true){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            if(intent.getIntExtra("currentselection",0)==0) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastknownlocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                loc = lastknownlocation;
                LatLng current = new LatLng(loc.getLatitude(), loc.getLongitude());
                marker=mMap.addMarker(new MarkerOptions().position(current).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 15));
            }
            else{
                Location placeLocation=new Location(LocationManager.GPS_PROVIDER);
                placeLocation.setLatitude(MainActivity.locations.get(intent.getIntExtra("currentselections",1)).latitude);
                placeLocation.setLongitude(MainActivity.locations.get(intent.getIntExtra("currentselections",1)).longitude);
                //Log.i("Error check",intent.getIntExtra("currentselections",1)+" "+placeLocation);
                LatLng latlng1 = new LatLng(placeLocation.getLatitude(), placeLocation.getLongitude());
                mMap.addMarker(new MarkerOptions()
                        .position(latlng1)
                        .title(MainActivity.list.get(intent.getIntExtra("currentselections",1))));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng1, 15));
            }
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                try {
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    String adress="";
                    List<Address> listadress=geocoder.getFromLocation(latLng.latitude,latLng.longitude,5);
                    if(listadress!=null && listadress.size()>0) {
//                        Log.i("Placeinfo",listadress.get(0)+"");
                        if (listadress.get(0).getThoroughfare() != null) {
                            if(listadress.get(0).getSubThoroughfare() != null){
                                adress += listadress.get(0).getSubThoroughfare()+" ";
                            }
                            adress += listadress.get(0).getThoroughfare()+" ";
                        }
//                        if (listadress.get(0).getLocality() != null) {
//                            adress += listadress.get(0).getLocality() + " ";
//                        }
                    }
                    if(adress.equals("")){
                        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm yyyy-MM-dd");
                        adress+=sdf.format(new Date());
                    }
                    mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(adress)
                            .snippet("Your marker snippet"));
                    //Log.i("Longclick info", "" + latLng);
                    //Log.i("Longclick info", "" + listadress.get(0));
                    MainActivity.locations.add(latLng);
                    MainActivity.latlist.add(String.valueOf(latLng.latitude));
                    MainActivity.longlist.add(String.valueOf(latLng.longitude));
                    MainActivity.list.add(adress);
                    MainActivity.arrayAdapter.notifyDataSetChanged();
                    try {
                        MainActivity.locationprefernces.edit().putString("favnames",ObjectSerializer.serialize((Serializable) MainActivity.list)).apply();
                        MainActivity.locationprefernces.edit().putString("favlatlocs",ObjectSerializer.serialize((Serializable) MainActivity.latlist)).apply();
                        MainActivity.locationprefernces.edit().putString("favlonglocs",ObjectSerializer.serialize((Serializable) MainActivity.longlist)).apply();
                        Toast.makeText(MapsActivity.this, "Location saved", Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        Log.i("Object serializer error maps",e+"");
                        Toast.makeText(MapsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(MapsActivity.this, "Something went wrong while adding data", Toast.LENGTH_SHORT).show();
                }
            }
        });
        }

    }
}