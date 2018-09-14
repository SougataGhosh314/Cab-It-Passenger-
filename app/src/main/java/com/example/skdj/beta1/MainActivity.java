package com.example.skdj.beta1;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.tasks.OnSuccessListener;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Iterator;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, PlaceSelectionListener {

    public static final String fl = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String cl = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int per_req_code = 1234;
    private static GoogleMap map;
    private boolean mLocationPermissionGranted;
    MySupportMapFragment itsMySupportMapFragment;
    private static final float DfZoom = 15f;
    private FusedLocationProviderClient mFusedLocation;
    Window window;
    DrawerLayout drawer;
    LatLngBounds.Builder builder = new LatLngBounds.Builder();
    Intent i;
    public static double strtLan;
    public static double strtLong;
    int flag = 0;
    Snackbar snackbar;
    SharedPreferences prefs;
    FloatingActionMenu actionMenu;
    FloatingActionButton actionButton,actionButton1;
    private ViewPager mViewPager;
    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;
    View view1,view2,view3,view4;
    SubActionButton button1=null,button2=null,button3=null,button4=null;
    String MY_PREFS_NAME = "Start";
    double dest_latitude;
    String destinationName;
    URL url;
    double dest_longitude;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            strtLan = intent.getDoubleExtra("latitude", 0.0);
            strtLong = intent.getDoubleExtra("longitude", 0.0);
            Log.d("task", "got location");
            Log.d("Location", strtLan + "  " + strtLong);
        }
    };
    //For placeFragment by google
    @Override
    public void onPlaceSelected(Place place) {
      //  Toast.makeText(this, place.getName(), Toast.LENGTH_LONG).show();
        dest_latitude= place.getLatLng().latitude;
        dest_longitude= place.getLatLng().longitude;
        destinationName=place.getName().toString();
        showLocations();
    }

    @Override
    public void onError(Status status) {
        Toast.makeText(this, status.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        //To check if user is already register
        String restoredText = prefs.getString("name", null);
        if (restoredText == null) {
            flag=1;
            Intent i = new Intent(this, FormActivity.class);
            startActivity(i);
            return;
        }
        window = getWindow();
        //To creating transparency in top status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(0x00000000); // transparent
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            window.addFlags(flags);
        }
        //Displaying the content
        setContentView(R.layout.activity_main);
        //service Intent initialization for continous location feed
        i = new Intent(this, LocationUpdater.class);
        // DrawerLayout initialize
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {
                Log.d("task", "on drraer slide");
                actionButton.setVisibility(View.INVISIBLE);
                view1.setVisibility(View.INVISIBLE);
                view2.setVisibility(View.INVISIBLE);
                view3.setVisibility(View.INVISIBLE);
                view4.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onDrawerOpened(@NonNull View view) {
                Log.d("task", "on drawer opened");
//                actionMenu.getActivityContentView().setVisibility(View.GONE);
                actionButton.setVisibility(View.INVISIBLE);
                view1.setVisibility(View.INVISIBLE);
                view2.setVisibility(View.INVISIBLE);
                view3.setVisibility(View.INVISIBLE);
                view4.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onDrawerClosed(@NonNull View view) {
                Log.d("task", "on drawer closed");
                actionButton.setVisibility(View.VISIBLE);
                view1.setVisibility(View.VISIBLE);
                view2.setVisibility(View.VISIBLE);
                view3.setVisibility(View.VISIBLE);
                view4.setVisibility(View.VISIBLE);
            }

            @Override
            public void onDrawerStateChanged(int i) {
                //To handle the drawer event
                if(drawer.isDrawerOpen(GravityCompat.START)== false) {
                    Log.d("task", "on drwaer state changed");
                    actionButton.setVisibility(View.VISIBLE);
                    view1.setVisibility(View.VISIBLE);
                    view2.setVisibility(View.VISIBLE);
                    view3.setVisibility(View.VISIBLE);
                    view4.setVisibility(View.VISIBLE);
                }
                else
                {
                    actionButton.setVisibility(View.INVISIBLE);
                    view1.setVisibility(View.INVISIBLE);
                    view2.setVisibility(View.INVISIBLE);
                    view3.setVisibility(View.INVISIBLE);
                    view4.setVisibility(View.INVISIBLE);
                }
            }
        });
        toggle.syncState();
        //Navigation view initialize
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //Placesearch Fragment provide by google
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(this);
        EditText edt= (EditText)findViewById(R.id.place_autocomplete_search_input);
        edt.setBackgroundColor(Color.argb(0xFF, 0xFF, 0xFF, 0xFF)); //Setting background color of search input

        try {
            url = new URL(getResources().getString(R.string.getTaxis)); //String.xml folder contain the url for getTaxis
        }
        catch (MalformedURLException me)
        {

        }
        //Register the broadcast reciever
        registerReceiver(broadcastReceiver, new IntentFilter(LocationUpdater.BD_KEY));
        //To set status icon dark
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        //To initialize mapFragment by MySupportMapFragment(class already created)
        itsMySupportMapFragment = new MySupportMapFragment();
        MySupportMapFragment.MapViewCreatedListener mapViewCreatedListener = new MySupportMapFragment.MapViewCreatedListener() {
            @Override
            public void onMapCreated() {
                PGMaps();
            }
        };
        //For mapFragment transaction handling
        itsMySupportMapFragment.itsMapViewCreatedListener = mapViewCreatedListener;
        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.map, itsMySupportMapFragment);
        transaction.commit();
        //Internet error snackbar initialize
        snackbar=Snackbar.make(findViewById(android.R.id.content), "No internet Connection", Snackbar.LENGTH_INDEFINITE)
                .setAction("Try Again", new View.OnClickListener() {;
                    @Override
                    public void onClick(View view) {
                        internetStatus();
                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ));

        //Go button code start
         ShowGoAction();
// Go button code end


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Clicked", "button1 clicked");
                FetchVehicle();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FetchVehicle();
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FetchVehicle();
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FetchVehicle();
            }
        });
    }
    //Timer Thread to show taxis in every 5 sec
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.d("task", "In run");
            if(internetStatus()== true)
               new FindTaxis().execute(url);
            handler.postDelayed(this, 5000);
        }
    };
    //When map is ready to be handled
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        startService(i); //start the service to get latitude and longitude continously
        //For location permission to show location for first time and move camera
        getLocationPermission();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(false);// Hide default myLocation button
        //Starting the timer to get the get taxis from the server after every 5 sec
        handler.postDelayed(runnable, 5000);
    }
    //To show source and destination together in the map after the place selection in Searchplace Fragment
    private void showLocations() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(strtLan, strtLong));
        builder.include(new LatLng(dest_latitude, dest_longitude));
        LatLngBounds bounds= builder.build();
        int padding =400;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        map.animateCamera(cu);
        map.addMarker(new MarkerOptions().position(new LatLng(dest_latitude, dest_longitude)));
    }
    //Taking googlemap object ref
    public void PGMaps() {
        //get ready for to recieve map
        itsMySupportMapFragment.getMapAsync(this);

    }
    //To find device location and moving the camera
    public void getDeviceLocation() {

        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);
        try {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Log.d("Mainactivity", "inside try");
            mFusedLocation.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location == null)
                        Log.d("Mainactivity", "Location not found");
                    else {
                        Log.d("Mainactivity", "Location fouund");
                        moveCamera(new LatLng(location.getLatitude(), location.getLongitude()), DfZoom);
                    }
                }
            });
        }
        catch (Exception e){
            Log.d("Mainactivity", "Permisssion not granted");

        }
    }
    // To get the locationPermission
    private void getLocationPermission() {
        String[] permission = {fl, cl};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), fl) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), cl) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                getDeviceLocation();
            } else
                ActivityCompat.requestPermissions(this, permission, per_req_code);
        } else {
            ActivityCompat.requestPermissions(this, permission, per_req_code);
        }
    }

    //Getting result from permission dialog if showed at the begining
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case per_req_code: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++)
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            return;
                        }
                }
                mLocationPermissionGranted = true;
                getDeviceLocation();
                // init();
            }

        }
    }
    //To move the camera of map
    public void moveCamera(LatLng latLng, float z)
    {
        Log.d("serial", "8");
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,z));
    }

    // For handling the click on the nav bar
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();

        if (id == R.id.nav_first_layout) {
            Intent i= new Intent(this, Update.class);
            startActivity(i);
        } else if (id == R.id.nav_second_layout) {

        } else if (id == R.id.nav_third_layout) {

        }  else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    //On back press we first close drawer layout if open and if not we close the activity
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
        }
    }
    //Hamburger button to open drawer layout
    public void openDrawer(View v){
        drawer=(DrawerLayout)findViewById(R.id.drawer_layout);
        if(!(drawer.isDrawerOpen(GravityCompat.START))){
            drawer.openDrawer(GravityCompat.START);
        }
    }
    @Override
    protected void onStop() {
        //on loosing focus from app we stop location service and also stop recieving broadcast
        super.onStop();
        if(flag==0) {
            unregisterReceiver(broadcastReceiver);
            stopService(i);
        }
    }

    // Method to be executed after getting all the taxis from server
    public static void gotTaxis(String s) {
        map.clear();
        s= s.substring(s.indexOf('['), s.indexOf(']')+1); //Get json array as String by extracting substring
        try {
            JSONArray jsonArray= new JSONArray(s);
            //Looping through all the jsonObject in the Array
            for(int i=0;i<jsonArray.length();i++)
            {
                JSONObject jsonObject= jsonArray.getJSONObject(i);
                // Adding marker for each of the vechicle type with different color
                if(jsonObject.getInt("type")== 1) {
                    map.addMarker(new MarkerOptions().position(new LatLng(jsonObject.getDouble("lat"), jsonObject.getDouble("lon"))).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                }
                else if(jsonObject.getInt("type")== 2) {
                    map.addMarker(new MarkerOptions().position(new LatLng(jsonObject.getDouble("lat"), jsonObject.getDouble("lon"))).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                }
                else if(jsonObject.getInt("type")== 3) {
                    map.addMarker(new MarkerOptions().position(new LatLng(jsonObject.getDouble("lat"), jsonObject.getDouble("lon"))).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                }
                else if(jsonObject.getInt("type")== 4) {
                    map.addMarker(new MarkerOptions().position(new LatLng(jsonObject.getDouble("lat"), jsonObject.getDouble("lon"))).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                }
                else if(jsonObject.getInt("type")== 5) {
                    map.addMarker(new MarkerOptions().position(new LatLng(jsonObject.getDouble("lat"), jsonObject.getDouble("lon"))).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                }
                else if(jsonObject.getInt("type")== 6) {
                    map.addMarker(new MarkerOptions().position(new LatLng(jsonObject.getDouble("lat"), jsonObject.getDouble("lon"))).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("task", s);
    }
    //To get back to my position in a click
    public void BackToMyLocation(View v){
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(strtLan, strtLong), DfZoom));
    }
    @Override
    public void onResume() {
        //On Resume we restart the service and broadcast recieve
        super.onResume();
        if(flag==0) {
            startService(i);
            registerReceiver(broadcastReceiver, new IntentFilter(LocationUpdater.BD_KEY));
        }
    }
    //check internet connection
    public boolean internetStatus()
    {
        if(CheckNetwork.isInternetAvailable(MainActivity.this)) //returns true if internet available
        {
            //do something. loadwebview.
            snackbar.dismiss();
            return true;
        }
        else
        {
           snackbar.show();
           return false;
        }
    }
    public void ShowGoAction()
    {
        ImageView icon = new ImageView(this);
        icon.setImageResource(R.mipmap.go_asset_round);

        actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .build();

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) actionButton.getLayoutParams();
        lp.gravity = Gravity.CENTER;
        actionButton.setPosition(5, lp);
        actionButton.setBackgroundColor(Color.argb(0x00, 0x00, 0x00, 0x00));
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);

//Image Resources
        ImageView itemIcon1 = new ImageView(this);
        itemIcon1.setImageResource(R.mipmap.auto_vehicle_round);

        ImageView itemIcon2 = new ImageView(this);
        itemIcon2.setImageResource(R.mipmap.mini_vehicle_round);

        ImageView itemIcon3 = new ImageView(this);
        itemIcon3.setImageResource(R.mipmap.sedan_vehicle_round);

        ImageView itemIcon4 = new ImageView(this);
        itemIcon4.setImageResource(R.mipmap.suv_vehicle_round);

//Button 1
        view1 = getLayoutInflater().inflate(R.layout.sub_menu_button1, null);
        ImageView img1 = view1.findViewById(R.id.subActionImage);
        TextView txt1 = view1.findViewById(R.id.subActionLabel);
        txt1.setText("Auto");
        txt1.getBackground();
        img1.setImageResource(R.mipmap.auto_vehicle_round);

//Button 2
        view2 = getLayoutInflater().inflate(R.layout.sub_menu_button2, null);
        ImageView img2 = view2.findViewById(R.id.subActionImage);
        TextView txt2 = view2.findViewById(R.id.subActionLabel);
        txt2.setText("Mini");
        img2.setImageResource(R.mipmap.mini_vehicle_round);

//Button 3
        view3 = getLayoutInflater().inflate(R.layout.sub_menu_button3, null);
        ImageView img3 = view3.findViewById(R.id.subActionImage);
        TextView txt3 = view3.findViewById(R.id.subActionLabel);
        txt3.setText("Sedan");
        img3.setImageResource(R.mipmap.sedan_vehicle_round);

//Button 4
        view4 = getLayoutInflater().inflate(R.layout.sub_menu_button4, null);
        ImageView img4 = view4.findViewById(R.id.subActionImage);
        TextView txt4 = view4.findViewById(R.id.subActionLabel);
        txt4.setText("SUV");
        img4.setImageResource(R.mipmap.suv_vehicle_round);

        button1 = itemBuilder.setContentView(view1).build();
        button2 = itemBuilder.setContentView(view2).build();
        button3 = itemBuilder.setContentView(view3).build();
//SubActionButton button4 = itemBuilder.setContentView(itemIcon4).build();
        button4 = itemBuilder.setContentView(view4).build();

        button1.setBackgroundColor(Color.argb(0x00, 0x33, 0x99, 0xFF));
        button2.setBackgroundColor(Color.argb(0x00, 0x33, 0x99, 0xFF));
        button3.setBackgroundColor(Color.argb(0x00, 0x33, 0x99, 0xFF));
        button4.setBackgroundColor(Color.argb(0x00, 0x33, 0x99, 0xFF));

//Floating Action Menu
        actionMenu = new FloatingActionMenu.Builder(this)
                .setStartAngle(180)
                .setEndAngle(360)
                .addSubActionView(button1, 300, 300)
                .addSubActionView(button2, 300, 300)
                .addSubActionView(button3, 300, 300)
                .addSubActionView(button4, 300, 300)
                .attachTo(actionButton)
                .build();
    }
    public void FetchVehicle()
    {
        if(destinationName==null)
        {
            Toast.makeText(getApplicationContext(), "Please select destination", Toast.LENGTH_LONG).show();
            return;
        }
        actionButton.setVisibility(View.INVISIBLE);
        actionButton.setEnabled(false);
        button1.setVisibility(View.INVISIBLE);
        button1.setEnabled(false);
        button2.setVisibility(View.INVISIBLE);
        button2.setEnabled(false);
        button3.setVisibility(View.INVISIBLE);
        button3.setEnabled(false);
        button4.setVisibility(View.INVISIBLE);
        button4.setEnabled(false);
        ImageView icon = new ImageView(MainActivity.this);
        icon.setImageResource(R.drawable.ic_action_cancelgo);

        actionButton1 = new FloatingActionButton.Builder(MainActivity.this)
                .setContentView(icon)
                .build();


        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) actionButton1.getLayoutParams();
        lp.gravity = Gravity.CENTER;
        actionButton1.setPosition(5, lp);

        actionButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.removeAllViews();
                actionButton1.setVisibility(View.INVISIBLE);
                actionButton1.setEnabled(false);
                ShowGoAction();
            }
        });

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mCardAdapter = new CardPagerAdapter();
        mCardAdapter.addCardItem(new CardItem("Auto","From: Army Institute of technology\nTo: "+destinationName+"\n Price: 5000Rs"));
        mCardAdapter.addCardItem(new CardItem("Bike","From: Army Institute of technology\nTo: "+destinationName+"\n Price: 1000Rs"));
        mCardAdapter.addCardItem(new CardItem("Pink Car","From: Army Institute of technology\nTo: "+destinationName+"\n Price: 2000Rs"));
        mCardAdapter.addCardItem(new CardItem("Sedan","From: Army Institute of technology\nTo: "+destinationName+"\n Price: 3000Rs"));

        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);

        mViewPager.setAdapter(mCardAdapter);
        mViewPager.setPageTransformer(true, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mCardAdapter);
        mCardShadowTransformer.enableScaling(true);

    }
}
