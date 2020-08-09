package com.example.favoritelocations;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Intent intent;
    static List<String> list,latlist,longlist;
    static List<LatLng> locations;
    static ArrayAdapter<String> arrayAdapter;
    static SharedPreferences locationprefernces;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationprefernces=this.getSharedPreferences("com.example.favoritelocations", Context.MODE_PRIVATE);
        ListView locationlist=(ListView)findViewById(R.id.locationlist);
        try{
        list=(ArrayList<String>)ObjectSerializer.deserialize(locationprefernces.getString("favnames",ObjectSerializer.serialize((Serializable)new ArrayList<String>())));
        latlist=(ArrayList<String>)ObjectSerializer.deserialize(locationprefernces.getString("favlatlocs",ObjectSerializer.serialize((Serializable)new ArrayList<String>())));
        longlist=(ArrayList<String>)ObjectSerializer.deserialize(locationprefernces.getString("favlonglocs",ObjectSerializer.serialize((Serializable)new ArrayList<String>())));
        locations=new ArrayList<LatLng>();
        if(latlist.size()>0){
            locations.clear();
            for(int i=0;i<latlist.size();i++){
                locations.add(new LatLng(Double.parseDouble(latlist.get(i)),Double.parseDouble(longlist.get(i))));
            }
        }
        }catch (Exception e){
            Log.i("Object deSerializer error",""+e);
            Toast.makeText(MainActivity.this, "Something went wrong while fetching data", Toast.LENGTH_SHORT).show();
        }
        if(list.size()==0){
            list.add("Add New Location");
            try {
                locationprefernces.edit().putString("favnames",ObjectSerializer.serialize((Serializable) list)).apply();
                //Log.i("Objects friends",ObjectSerializer.serialize((Serializable) list)+"");
            }catch (Exception e){
                Log.i("Object serializer error list",e+"");
                Toast.makeText(MainActivity.this, "Something went wrong while adding data", Toast.LENGTH_SHORT).show();
            }
        }
        if(locations.size()==0) {
            locations.add(new LatLng(0, 0));
            latlist.add("0");
            longlist.add("0");
            try {
                locationprefernces.edit().putString("favlatlocs",ObjectSerializer.serialize((Serializable) latlist)).apply();
                locationprefernces.edit().putString("favlonglocs",ObjectSerializer.serialize((Serializable) longlist)).apply();
            }catch (Exception e){
                Log.i("Object serializer error locations",e+"");
                Toast.makeText(MainActivity.this, "Something went wrong while adding data", Toast.LENGTH_SHORT).show();
            }
        }
        arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
        locationlist.setAdapter(arrayAdapter);
        locationlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mapfunction(view,i);
            }
        });
    }
    public void mapfunction(View view,int i){
        intent=new Intent(this,MapsActivity.class);
        intent.putExtra("currentselection",i);
        startActivity(intent);
    }
}