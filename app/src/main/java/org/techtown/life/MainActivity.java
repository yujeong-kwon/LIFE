package org.techtown.life;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pedro.library.AutoPermissions;
import com.stanfy.gsonxml.GsonXml;
import com.stanfy.gsonxml.GsonXmlBuilder;
import com.stanfy.gsonxml.XmlParserCreator;

import org.techtown.life.data.WeatherItem;
import org.techtown.life.data.WeatherResult;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnTabItemSelectedListener,
        OnRequestListener,  MyApplication.OnResponseListener {

    private static final String TAG = "MainActivity";

    Diary_list diary_list;
    Diary_write diary_write;
    Diary_calendar diary_calendar;


    BottomNavigationView bottomNavigation;

    Location currentLocation;
    GPSListener gpsListener;


    int locationCount = 0;
    String currentWeather;
    String currentAddress;
    String currentDateString;
    Date currentDate;

    public static NoteDatabase mDatabase = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        diary_list = new Diary_list();
        diary_write = new Diary_write();
        diary_calendar = new Diary_calendar();



        getSupportFragmentManager().beginTransaction().replace(R.id.container, diary_list).commit();

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener(){

                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.diary_list:
                                Toast.makeText(getApplicationContext(), "첫 번째 탭 선택됨", Toast.LENGTH_LONG).show();
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, diary_list).commit();

                                return true;
                            case R.id.diary_write:
                                Toast.makeText(getApplicationContext(), "두 번째 탭 선택됨", Toast.LENGTH_LONG).show();

                                return true;
                            case R.id.diary_cal:
                                Toast.makeText(getApplicationContext(), "세 번째 탭 선택됨", Toast.LENGTH_LONG).show();
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, diary_calendar).commit();

                                return true;

                        }
                        return false;
                    }
                }
        );

        AutoPermissions.Companion.loadAllPermissions(this, 101);

        setPicturePath();

        openDatabase();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mDatabase != null) {
            mDatabase.close();
            mDatabase = null;
        }
    }
    public void openDatabase() {
        // open database
        if (mDatabase != null) {
            mDatabase.close();
            mDatabase = null;
        }
        mDatabase = NoteDatabase.getInstance(this);
        boolean isOpen = mDatabase.open();
        if (isOpen) {
            Log.d(TAG, "Note database is open.");
        } else {
            Log.d(TAG, "Note database is not open.");
        }
    }

    public void setPicturePath(){
        String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        AppConstants.FOLDER_PHOTO = sdcardPath + File.separator + "photo";
    }
    public void onTabSelected(int position){
        if(position==0){
            bottomNavigation.setSelectedItemId(R.id.diary_list);
        }else if (position == 1){
            bottomNavigation.setSelectedItemId(R.id.diary_write);
        }else if(position == 2){
            bottomNavigation.setSelectedItemId(R.id.diary_cal);
        }
    }

    public void showFragment2(Note item) {
    /*
        diary_write = new Diary_write();
        diary_write.setItem(item);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, diary_write).commit();

     */

    }



    public void onRequest(String command){
        if(command != null){
            if(command.equals("getCurrentLocation")){
                getCurrentLocation();
            }
        }
    }

    public void getCurrentLocation(){
        //set current time
        currentDate = new Date();
        currentDateString = AppConstants.dateFormat3.format(currentDate);
        if(diary_write != null){
            diary_write.setDateString(currentDateString);
        }

        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try{
            currentLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(currentLocation != null){
                double latitude = currentLocation.getLatitude();
                double longitude = currentLocation.getLongitude();
                String message = "Last Location -> Latitude: " + latitude + "\nLongitude:" + longitude;
                println(message);

                getCurrentWeather();
                getCurrentAddress();
            }

            gpsListener = new GPSListener();
            long minTime = 10000;
            float minDistance = 0;

            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);

            println("Current location requested");
        } catch(SecurityException e){
            e.printStackTrace();
        }
    }

    public void stopLocationService(){
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try{
            manager.removeUpdates(gpsListener);
            println("Current location requested.");
        }catch(SecurityException e){
            e.printStackTrace();
        }
    }

    class GPSListener implements LocationListener{
        public void onLocationChanged(Location location){
            currentLocation = location;

            locationCount++;

            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();

            String message = "Current Location -> Latitude : " + latitude + "\nLongitude:" + longitude;
            println(message);

            getCurrentWeather();
            getCurrentAddress();
        }
        public void onProviderDisabled(String provider){}
        public void onProviderEnabled(String provider){}
        public void onStatusChanged(String provider, int status, Bundle extra){}
    }
    public void getCurrentAddress(){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;

        try{
            addresses = geocoder.getFromLocation(currentLocation.getLatitude(),currentLocation.getLongitude(),1);
        }catch(Exception e){
            e.printStackTrace();
        }

        if(addresses != null && addresses.size() > 0){
            Address address = addresses.get(0);
            currentAddress = address.getLocality() + " " + address.getSubLocality();
            String adminArea = address.getAdminArea();
            String country = address.getCountryName();
            println("Address: " + country + " " + adminArea + " " + currentAddress);

            if(diary_write != null){
                diary_write.setAddress(currentAddress);
            }
        }
    }
    public void getCurrentWeather(){
        Map<String, Double> gridMap = GridUtil.getGrid(currentLocation.getLatitude(),currentLocation.getLongitude());
        double gridX = gridMap.get("x");
        double gridY = gridMap.get("y");
        println("x -> " + gridX + ",y -> " + gridY);

        sendLocalWeatherReq(gridX, gridY);
    }
    public void sendLocalWeatherReq(double gridX, double gridY){
        String url = "http://www.kma.go.kr/wid/queryDFS.jsp";
        url += "?gridx=" + Math.round(gridX);
        url += "&gridy=" + Math.round(gridY);
        Map<String, String> params = new HashMap<String, String>();
        MyApplication.send(AppConstants.REQ_WEATHER_BY_GRID, Request.Method.GET,url, params, this);
    }
    public void processResponse(int requestCode, int responseCode, String response){
        if(responseCode == 200) {
            if(responseCode == AppConstants.REQ_WEATHER_BY_GRID){
                //Grid좌표를 이용한 날씨 정보 처리 응답
                //println("response -> " + response);
                XmlParserCreator parserCreator = new XmlParserCreator(){
                    @Override
                    public XmlPullParser createParser(){
                        try{
                            return XmlPullParserFactory.newInstance().newPullParser();
                            }catch(Exception e){
                            throw new RuntimeException(e);
                        }
                    }
                };

                GsonXml gsonXml = new GsonXmlBuilder()
                        .setXmlParserCreator(parserCreator)
                        .setSameNameLists(true)
                        .create();
                WeatherResult weather = gsonXml.fromXml(response, WeatherResult.class);
                //현재 기준 시간
                try{
                    Date tmDate =AppConstants.dateFormat.parse(weather.header.tm);
                    String tmDateText = AppConstants.dateFormat2.format(tmDate);
                    println("기준 시간: " + tmDateText);

                    for (int i = 0; i < weather.body.datas.size(); i++) {
                        WeatherItem item = weather.body.datas.get(i);
                        println("#" + i + " 시간 : " + item.hour + "시, " + item.day + "일째");
                        println("  날씨 : " + item.wfKor);
                        println("  기온 : " + item.temp + " C");
                        println("  강수확률 : " + item.pop + "%");

                        println("debug 1 : " + (int)Math.round(item.ws * 10));
                        float ws = Float.valueOf(String.valueOf((int)Math.round(item.ws * 10))) / 10.0f;
                        println("  풍속 : " + ws + " m/s");
                }
                    // set current weather
                    WeatherItem item = weather.body.datas.get(0);
                    currentWeather = item.wfKor;
                    if (diary_write != null) {
                        diary_write.setWeather(item.wfKor);
                    }

                    // stop request location service after 2 times
                    if (locationCount > 1) {
                        stopLocationService();
                    }
            } catch(Exception e){
                    e.printStackTrace();
                }
        } else {
                // Unknown request code
                println("Unknown request code : " + requestCode);

            }
    } else {
            println("Failure response code : " + responseCode);

        }
}
private void println(String data){
    Log.d(TAG, data);
    }
}