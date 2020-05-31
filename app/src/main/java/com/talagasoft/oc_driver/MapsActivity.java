package com.talagasoft.oc_driver;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.talagasoft.oc_driver.adapter.PenumpangAdapter;
import com.talagasoft.oc_driver.model.HttpXml;
import com.talagasoft.oc_driver.model.Penumpang;
import com.talagasoft.oc_driver.model.PenumpangRecord;

import org.w3c.dom.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends  AbstractMapActivity
        implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener,
        LocationListener,  OnConnectionFailedListener,OnLocationChangedListener,
        GoogleApiClient.ConnectionCallbacks, OnMapClickListener, GoogleMap.OnMapLongClickListener {

    //global
    String TAG="MapsActivity";
    SharedPreferences mSetting;
    private boolean needsInit=false;
    String mWebsite="";

    //google API
    Location location;
    LocationManager locationManager;
    GoogleMap map;
    Criteria criteria;
    private GoogleApiClient mGoogleApiClient;
    private int PLACE_PICKER_REQUEST = 1;
    PolylineOptions mPolyline;

    //driver variabel
    String mNomorHp = "", mNama="";
    Bitmap mIconDriver=null,mIconPeople=null,mIconFood,mIconCar;
    LatLng myLatLng,myLatLngOld;

    //penumpang variabel
    float mToLat,mToLng;        //  lokasi penjemputan
    float mToLatEnd,mToLngEnd;  //  lokasi tujuan di antar
    private Place mPlaceTujuan;
    String mPlaceTujuanText="",mJenisPenumpang="";
    String mNoHpPenumpang="",mNamaPenumpang="";
    TextView mSelNama;
    private Marker mSelMarker;
    List<PenumpangRecord> arPenumpang = new ArrayList<PenumpangRecord>();
    int mSelIndex=0;     // selected index from arPenumpang
    TextView lblInfo;

    int mMode=0;    //0 - mode driver lg cari penumpang
                    //1 - mode driver lg menuju tempat penumpang
                    //2 -
                    //3 - mode driver antar ke tempat tujuan
    int MODE_CARI=0,MODE_JEMPUT=1,MODE_ANTAR=3;

    //--handle mulai jalan ke tempat penumpang
    TimerTask _task;
    final Handler _handler = new Handler();
    Timer _timer;

    // controls
    Button btnStopTujuan,btnStart,btnCall,btnChat,btnRefresh,btnSiap,btnStop;
    AdView mAdView;

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
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
    public void onMapClick(LatLng latLng) {

    }
    @Override
    public void onMapReady(final GoogleMap map1) {
        this.map = map1;
        map.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
        map.setOnInfoWindowClickListener(this);
        map.setMyLocationEnabled(true);
        myLocation();

        if (needsInit && myLatLng != null ) {
            needsInit=false;
            CameraUpdate center=CameraUpdateFactory.newLatLng(myLatLng);
            CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);
            map.moveCamera(center);
            map.animateCamera(zoom);

        }
        //langsung scan penumpang
        //Button btnRefresh=(Button)findViewById(R.id.btnRefesh);
        //btnRefresh.setVisibility(View.GONE);
        startTimer();
    }
    private double getRadius(int inKm){
        double latDistance = Math.toRadians(myLatLng.latitude - inKm);
        double lngDistance = Math.toRadians(myLatLng.longitude - inKm);
        double a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)) +
                (Math.cos(Math.toRadians(myLatLng.latitude))) *
                        (Math.cos(Math.toRadians(inKm))) *
                        (Math.sin(lngDistance / 2)) *
                        (Math.sin(lngDistance / 2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double dist = 6371 / c;
        if (dist<50){
                    /* Include your code here to display your records */
        }
        return dist;

    }

    private void getCalonPenumpang(){
        if (arPenumpang == null) {
            Log.i("getCalonPenumpang","Tidak ada penumpang yang submit.");
            return;
        }
        String[] values = new String[arPenumpang.size()];
        for(int i=0;i<arPenumpang.size();i++) {
            PenumpangRecord p = arPenumpang.get(i);
            addMarkerPenumpang(map, p.getLat(), p.getLng(),p.getName(), p.getPhone(), p.getJenis());
            values[i]=p.getName();
        }

    }
    public void myLocation(){
        getLocation();
        if(location==null && myLatLng==null){
            Log.d(TAG,"location==null");
            myLatLng=new LatLng(-6.584711,107.4667);
        } else {
            if (myLatLng != null){
                if ( ! (myLatLng.latitude==location.getLatitude() && myLatLng.longitude==location.getLongitude())){
                    myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    Log.d("myLocation","Receipt new location "+myLatLng.toString());
                    return;
                }
            }
            myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        }
    }
    private void PushMyLatLng(){
        if(myLatLng==null){
            Log.i("PustMyLatLng:","is null retun");
            return;
        }

        if(myLatLngOld != null) {
            if (myLatLng.latitude == myLatLngOld.latitude && myLatLng.longitude == myLatLngOld.longitude) {
                Log.d("PushMyLatLng", "Same with old myLatLng, skipped...");
                return;
            }
        }
        String mUrl=getResources().getString(R.string.url_source)+"pushme.php?hp="+mNomorHp+"&lat="+myLatLng.latitude+"&lng="+myLatLng.longitude;
        HttpXml web=new HttpXml();
        StringBuilder doc=web.GetUrlData(mUrl);
        if(doc==null){
          Log.d("PustMyLatLng", String.valueOf(R.string.no_internet));
        };
    }
    private void DrawRoutePoly(){
        if(mPolyline==null){
            Log.d(TAG,"mPolyline is null");
            return;
        }
        mPolyline.width(5);
        mPolyline.color(Color.BLUE);
        Polyline line = map.addPolyline(mPolyline);
    }

    public void DrawRoute(LatLng from,LatLng to){

        float mDistance = getDistanceKm(from,to);

        Document document= null;
        try {
            document = new GMapV2Direction().getDocument(from,to,"drive");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(document == null){
            Toast.makeText(getBaseContext(),"Unable DrawRoute",Toast.LENGTH_LONG).show();
            return;
        }
        ArrayList<LatLng> oLat=new GMapV2Direction().getDirection(document);

        mPolyline=new PolylineOptions();
        for(int i=0;i<oLat.size();i++){
            mPolyline.add(oLat.get(i));
        }
    }


    public String getDistance(LatLng my_latlong, LatLng frnd_latlong) {
        float distance=getDistanceFloat(my_latlong,frnd_latlong);
        String dist = distance + " M";

        if (distance > 1000.0f) {
            distance = distance / 1000.0f;
            dist = distance + " KM";
        }
        return dist;
    }
    public float getDistanceKm(LatLng my_latlong, LatLng frnd_latlong) {
        float distance=getDistanceFloat(my_latlong,frnd_latlong);
        if (distance > 1000.0f) {
            distance = distance / 1000.0f;
        } else {
            distance=1.0f;
        }
        return distance;

    }
    public float getDistanceFloat(LatLng my_latlong, LatLng frnd_latlong) {
        Location l1 = new Location("One");
        l1.setLatitude(my_latlong.latitude);
        l1.setLongitude(my_latlong.longitude);

        Location l2 = new Location("Two");
        l2.setLatitude(frnd_latlong.latitude);
        l2.setLongitude(frnd_latlong.longitude);

        float distance = l1.distanceTo(l2);
        return distance;
    }
    public double CalculationByDistance(double initialLat, double initialLong, double finalLat, double finalLong){
        /*PRE: All the input values are in radians!*/
        double latDiff = finalLat - initialLat;
        double longDiff = finalLong - initialLong;
        double earthRadius = 6371; //In Km if you want the distance in km

        double distance = 2*earthRadius*Math.asin(Math.sqrt(Math.pow(Math.sin(latDiff/2.0),2)+Math.cos(initialLat)*Math.cos(finalLat)*Math.pow(Math.sin(longDiff/2),2)));

        return distance;

    }
    private void moveToCurrentLocation(GoogleMap mMap, LatLng currentLocation)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 3000, null);


    }
    @Override
    public void onInfoWindowClick(Marker marker) {
        mSelMarker=marker;
        ShowTujuan();
    }
    private void ShowTujuan(){
        if(arPenumpang==null)return;
        if(arPenumpang.size()==0)return;

        if (mSelMarker == null) {
            Toast.makeText(this, "Pilih Penumpang !", Toast.LENGTH_LONG).show();
            return;
        }
        for(int i=0;i<arPenumpang.size();i++){
            if(arPenumpang.get(i).getPhone().equals(mSelMarker.getSnippet())){
                String info = "";

                PenumpangRecord p = arPenumpang.get(i);
                mSelIndex=i;
                mToLatEnd = (float) p.getTo_lat();
                mToLngEnd = (float) p.getTo_lng();
                mNoHpPenumpang = p.getPhone();
                mJenisPenumpang = p.getJenis();

                mPlaceTujuanText = p.getTujuan();
                //if(mPlaceTujuanText!=null)mPlaceTujuanText = AddressFromLatLng(new LatLng(mToLatEnd,mToLngEnd));
                String sAsal = p.getAddress();
                //if(sAsal.isEmpty())sAsal=AddressFromLatLng(new LatLng(p.getLat(),p.getLng()));


                if(mJenisPenumpang.equals("item")){
                    info = "Nama: "+mSelMarker.getTitle()
                            + ", Hp: "+mNoHpPenumpang+",Item: "+ p.get_item_name()+", Qty: "+p.get_item_qty() +
                            ", Harga: " + p.get_item_price()+", Total: "+p.get_item_total() +
                            ", Alamat: " + p.get_item_address()+", OrderId: "+p.get_order_id();
                    Intent intent=new Intent("com.talagasoft.oc_driver.OrderFoodActivity");
                    intent.putExtra("no_hp",mNoHpPenumpang);
                    startActivity(intent);

                } else {
                    info = "Nama: "+mSelMarker.getTitle()
                            + ", Hp: "+mNoHpPenumpang
                            + ", Tujuan: " + mPlaceTujuanText
                            + " - " + mToLatEnd +  "/" + mToLngEnd
                            + ", Asal: " + sAsal;
                }
                mSelNama.setText(info);
                saveSettingTujuan();

            }
        }


    }

    private void addMarkerMe(GoogleMap map, double lat, double lon,
                           String title, String snippet) {
        map.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
                .title(title)
//                .icon(BitmapDescriptorFactory.fromBitmap(mIconDriver))
                .snippet(snippet));
    }
    private void addMarkerPenumpang(
        GoogleMap map, double lat, double lon, String title,
        String snippet,String sJenis) {

        MarkerOptions marker;
        if (sJenis.contains("item")) {
            marker = new MarkerOptions().position(new LatLng(lat, lon))
                    .title(title)
                    .icon(BitmapDescriptorFactory.fromBitmap(mIconFood))
                    .draggable(false)
                    .snippet(snippet);
        } else if (sJenis.contains("car")){
            marker = new MarkerOptions().position(new LatLng(lat, lon))
                    .title(title)
                    .icon(BitmapDescriptorFactory.fromBitmap(mIconCar))
                    .draggable(false)
                    .snippet(snippet);

        } else {
            marker = new MarkerOptions().position(new LatLng(lat, lon))
                    .title(title)
                    .icon(BitmapDescriptorFactory.fromBitmap(mIconPeople))
                    .draggable(false)
                    .snippet(snippet);

        }
        map.addMarker(marker);
    }
    private void addMarkerTo(
            GoogleMap map, double lat, double lon, String title, String snippet) {
        map.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
                .title(title)
                .draggable(false)
                .snippet(snippet));
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(getClass().getSimpleName(),
                String.format("%f:%f", location.getLatitude(),
                location.getLongitude()));

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //if( mAdapter != null )
        //    mAdapter.setGoogleApiClient( mGoogleApiClient );
        //getLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }



    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-1869511402643723~8811604696");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        setupControls();

        stopTimer();
        lblInfo=(TextView) findViewById(R.id.lblInfo);

        mWebsite=getResources().getString(R.string.url_source);
        mSelNama= (TextView) findViewById(R.id.lblTujuan);
        mSelNama.setText("Ready");
        mIconDriver = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.drawable.driver);
        mIconPeople = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.drawable.people);
        mIconCar = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.drawable.people_car);
        mIconFood = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.drawable.people_food);

        loadSetting();
        setupButtonClick();

        if (readyToGo()) {
            MapFragment mapFrag=(MapFragment)getFragmentManager().findFragmentById(R.id.map);
            if (savedInstanceState == null) {
                needsInit=true;
            }
            mapFrag.getMapAsync(this);
            mGoogleApiClient = new GoogleApiClient
                    .Builder( this )
                    .enableAutoManage( this, 0, this )
                    .addApi( Places.GEO_DATA_API )
                    .addApi( Places.PLACE_DETECTION_API )
                    .addConnectionCallbacks( this )
                    .addOnConnectionFailedListener( this )
                    .build();

        }

        //ambil data dari web thread
        new getServerDataAsync().execute();

        //ketika kembali load tetapi masih dalam keadaan antar, kembali tampilkan rutenya
        if((mMode==MODE_ANTAR || mMode==MODE_JEMPUT) && !mNoHpPenumpang.isEmpty() ){
            if(mMode==MODE_JEMPUT){
                //btnSiap.setVisibility(View.GONE);
                //btnStop.setVisibility(View.VISIBLE);
            }
            if(mMode==MODE_ANTAR){
                //btnSiap.setVisibility(View.GONE);
                //btnStopTujuan.setVisibility(View.VISIBLE);
            }
        } else {
            mMode = MODE_CARI;
        }

    }

    private class getServerDataAsync extends AsyncTask<Void, Integer, String>
    {
        String TAG = getClass().getSimpleName();

        protected void onPreExecute (){
            Log.d(TAG + " PreExceute","On pre Exceute......");
        }

        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround","On doInBackground...");

            PushMyLatLng();

            if(!mNoHpPenumpang.isEmpty() ){
                Penumpang user=new Penumpang(getBaseContext());
                user.getById(mNoHpPenumpang);
                mToLatEnd=user.get_to_lat();
                mToLngEnd=user.get_to_lng();
                mNamaPenumpang=user.get_user_name();
                mJenisPenumpang=user.get_jenis();
                mToLat=user.get_lat();
                mToLng=user.get_lng();
            }
            if(myLatLng!=null && mToLat!=0 && mToLng!=0) {
                DrawRoute(myLatLng, new LatLng(mToLat, mToLng));
            }
            if (myLatLng != null ) {
                arPenumpang = new Penumpang(getBaseContext()).getAllNewOrder(mNama, myLatLng);
                Log.i("Penumpang","Terdapat "+arPenumpang.size()+" penumpang.");
            }
            return "You are at PostExecute";
        }

        protected void onProgressUpdate(Integer...a){
            Log.d(TAG + " onProgressUpdate", "You are in progress update ... " + a[0]);
        }

        protected void onPostExecute(String result) {
            Log.d(TAG + " onPostExecute", "" + result);
            DecimalFormat df = new DecimalFormat("###,###.##"); // or pattern "###,###.##$"

            if(myLatLng!=null) {
                lblInfo.setText("My LatLon: " + Math.round(myLatLng.latitude) + "/" + Math.round(myLatLng.longitude));
                moveToCurrentLocation(map, myLatLng);
            }

            SharedPreferences.Editor editor = mSetting.edit();
            editor.putFloat("tujuan_lat", mToLat);
            editor.putFloat("tujuan_lng", mToLng);
            editor.putFloat("tujuan_lat_end", mToLatEnd);
            editor.putFloat("tujuan_lng_end", mToLngEnd);
            editor.commit();

            DrawRoutePoly();

            getCalonPenumpang();
            addMarkerMe(map, myLatLng.latitude, myLatLng.longitude, "Me", mNama);
            addMarkerPenumpang(map,mToLat,mToLng, mNamaPenumpang,mNoHpPenumpang,mJenisPenumpang);
            PushMyLatLng();
        }
    }

    private boolean sudahPilihPenumpang(){
        if (mNoHpPenumpang.isEmpty()) {
            Toast.makeText(getBaseContext(), "Pilih Penumpang !", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    private void setupControls(){
        //init control
        btnStopTujuan = (Button) findViewById(R.id.btnStopTujuan);
        btnStart=(Button) findViewById(R.id.btnStartTujuan);
        btnCall=(Button) findViewById(R.id.btnCall);
        btnChat=(Button) findViewById(R.id.btnChat);
        btnRefresh=(Button) findViewById(R.id.btnRefesh);
        btnSiap=(Button) findViewById(R.id.btnSiap);
        btnStop =(Button) findViewById(R.id.btnStop);

    }
    private void setupButtonClick(){

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //btnRefresh.setVisibility(View.GONE);
                mMode = MODE_CARI;
                //startTimer();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopTimer();
                map.clear();
                addMarkerMe(map, myLatLng.latitude, myLatLng.longitude,"Me", mNama);
                //btnStop.setVisibility(View.GONE);
                //Button btnStartTujuan=(Button)findViewById(R.id.btnStartTujuan);
                //btnStartTujuan.setVisibility(View.VISIBLE);
                Toast.makeText(getBaseContext(),"Silahkan naikan penumpang, klik tombol Start " +
                        " apabila sudah mulai mengantarkan ke tempat tujuan.",Toast.LENGTH_LONG).show();
            }
        });
        btnSiap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( !sudahPilihPenumpang()) return;
                ambilPenumpang(view);
            }
        });

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!sudahPilihPenumpang()) return;
                startChat();
            }
        });

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!sudahPilihPenumpang())return;
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mNoHpPenumpang));
                startActivity(intent);
            }
        });
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!sudahPilihPenumpang())return;
                //mulai antar penumpang ke tujuan
                mMode = MODE_ANTAR;
                //startTimer();
                // btnStart.setVisibility(View.GONE);
                //btnStopTujuan.setVisibility(View.VISIBLE);
                addMarkerMe(map, myLatLng.latitude, myLatLng.longitude, "Me", mNama);
                addMarkerPenumpang(map, mToLatEnd, mToLngEnd,mNoHpPenumpang, mPlaceTujuanText,
                        mJenisPenumpang);
                DrawRoute(new LatLng(mToLat,mToLng), new LatLng(mToLatEnd, mToLngEnd));
                DrawRoutePoly();
            }
        });
        btnStopTujuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( !sudahPilihPenumpang()) return;
                finishOrder();
                finish();
            }
        });
        Button btnPenumpangList=(Button)findViewById(R.id.cmdPenumpang);
        btnPenumpangList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showListPenumpang();
            }
        });
        Button btnHide=(Button)findViewById(R.id.cmdHide);
        btnHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout divPnp=(LinearLayout) findViewById(R.id.divPnp);
                divPnp.setVisibility(View.GONE);

            }
        });

    }

    private void showListPenumpang(){
        final LinearLayout divPnp=(LinearLayout) findViewById(R.id.divPnp);
        divPnp.setVisibility(View.VISIBLE);
        final ListView lstPnp=(ListView)findViewById(R.id.lstPnp);
        lstPnp.setAdapter(new PenumpangAdapter(getBaseContext(),arPenumpang));
        lstPnp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                divPnp.setVisibility(View.GONE);
                PenumpangRecord pn=arPenumpang.get(i);
                moveToCurrentLocation(map,new LatLng(pn.getLat(),pn.getLng()));
            }
        });
    }
    private void ambilPenumpang(View view) {
        if(mSelMarker==null)return;
        new AlertDialog.Builder(view.getContext())
                .setTitle("Konfimasi")
                .setMessage("Ambil penumpang ini ? " + mSelMarker.getTitle())
                .setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if ( acceptOrder() ){
                                    Toast.makeText(getBaseContext(),
                                            "Data kesiapan anda sudah masuk server, silahkan jemput." +
                                                    "Apabila sudah ditempat klik tombol Stop diatas.",
                                            Toast.LENGTH_LONG).show();
                                    //btnRefresh.setVisibility(View.INVISIBLE);
                                    //btnSiap.setVisibility(View.INVISIBLE);
                                    //btnStop.setVisibility(View.VISIBLE);
                                    mMode = MODE_JEMPUT;    //driver sedang menuju penumpang
                                    DrawRoute(myLatLng,mSelMarker.getPosition());
                                    saveSettingTujuan();
                                } else {
                                    Toast.makeText(getBaseContext(),"Gagal kirim, coba lagi !",Toast.LENGTH_LONG).show();
                                };
                            }
                        })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    private void finishOrder(){
        Penumpang p=new Penumpang(getBaseContext());
        if ( p.FinishOrder(mNoHpPenumpang) ) {
            mMode = MODE_CARI;
            mSelMarker=null; //reset
            ///startTimer();
            //Button btnStopTujuan=(Button)findViewById(R.id.btnStopTujuan);
            //btnStopTujuan.setVisibility(View.GONE);
            //Button btnSiap=(Button)findViewById(R.id.btnSiap);
            //btnSiap.setVisibility(View.VISIBLE);
            Toast.makeText(getBaseContext(),"Tugas anda sudah selesai, " +
                    "penghasilan sudah ditambahkan di dompet anda. " +
                    "Terimakasih.",Toast.LENGTH_LONG).show();
            resetSettingTujuan();
            finish();

        } else {
            Toast.makeText(getBaseContext(),"Ada kesalahan tutup order ! " +
                    " Silahkan informasikan ke bagian admin " +
                    " untuk memastikan saldo dompet anda bertambah.",Toast.LENGTH_LONG).show();
        }
    }
    private void saveSettingTujuan(){
        //save to setting tujuan untuk menghindari buffer kosong
        SharedPreferences.Editor editor = mSetting.edit();
        editor.putFloat("tujuan_lat", mToLat);
        editor.putFloat("tujuan_lng", mToLng);
        editor.putString("tujuan_name", mPlaceTujuanText);
        editor.putString("no_hp_penumpang",mNoHpPenumpang);
        editor.putFloat("tujuan_lat_end", mToLatEnd);
        editor.putFloat("tujuan_lng_end", mToLngEnd);
        editor.putInt("mode", mMode);
        editor.commit();
    }
    private void resetSettingTujuan(){
        SharedPreferences.Editor editor = mSetting.edit();
        editor.putFloat("tujuan_lat", 0);
        editor.putFloat("tujuan_lng", 0);
        editor.putString("tujuan_name", "");
        editor.putInt("mode", MODE_CARI);
        editor.putString("no_hp_penumpang","");
        editor.putFloat("tujuan_lat_end", 0);
        editor.putFloat("tujuan_lng_end", 0);
        editor.commit();
    }
    private void loadSetting(){
        if ( mSetting == null) mSetting = getSharedPreferences("setting_gojek", Context.MODE_PRIVATE);
        mNama=mSetting.getString("nama", "Guest");
        mNomorHp=mSetting.getString("no_hp", "0000000000");
        mMode=mSetting.getInt("mode",0);
        mNoHpPenumpang=mSetting.getString("no_hp_penumpang","");
        mPlaceTujuanText=mSetting.getString("tujuan_nama","");
        mToLat=mSetting.getFloat("tujuan_lat",0);
        mToLng=mSetting.getFloat("tujuan_lng",0);
        mToLatEnd=mSetting.getFloat("tujuan_lat_end",0);
        mToLngEnd=mSetting.getFloat("tujuan_lng_end",0);
    }
    private void startTimer() {
        if (_timer == null) {
            _timer = new Timer();
        }
        _task = new TimerTask() {
            public void run() {
                _handler.post(new Runnable() {
                    public void run() {

                        Log.d("statTimer", "Start Timer unning with mode : "+mMode);
                        myLocation();
                        map.clear();
                        updateLokasiPenumpang();
                        addMarkerMe(map, myLatLng.latitude, myLatLng.longitude, mNama, mNomorHp);
                        if( mMode == MODE_JEMPUT ) {
                            addMarkerPenumpang(map, mToLat,mToLng,mPlaceTujuanText, mNoHpPenumpang,
                                    mJenisPenumpang);
                            //DrawRoute(myLatLng, new LatLng(mToLat, mToLng));
                            DrawRoutePoly();

                        }  else if (mMode == MODE_ANTAR){
                            addMarkerMe(map, myLatLng.latitude, myLatLng.longitude,mNama, mNomorHp);
                            addMarkerPenumpang(map, mToLatEnd, mToLngEnd,mNoHpPenumpang, mPlaceTujuanText,
                                    mJenisPenumpang);
                            //DrawRoute(myLatLng, new LatLng(mToLatEnd, mToLngEnd));
                            DrawRoutePoly();
                        } else {        // MODE_CARI
                            if(arPenumpang==null) {
                                arPenumpang = new Penumpang(getBaseContext()).getAllNewOrder(mNama, myLatLng);
                            }
                            if(arPenumpang.size()==0) {
                                arPenumpang = new Penumpang(getBaseContext()).getAllNewOrder(mNama, myLatLng);
                            }
                            getCalonPenumpang();
                        }
                    }
                });
            }};


        _timer.schedule(_task, 6000, 60000);
    }
    public void stopTimer() {
        if (_task != null) {
            Log.d("TIMER", "timer canceled");
            _timer.cancel();
            _timer=null;
        }
    }
    private void updateLokasiPenumpang(){
        if(arPenumpang==null)return;
        if(arPenumpang.size()==0)return;
        String sTujuan,sAsal;
        float nJarak;
        PenumpangRecord pr=new PenumpangRecord();
        for(int i=0;i<arPenumpang.size();i++){
            pr=arPenumpang.get(i);
            if(pr.getTujuan()==null){
                nJarak=getDistanceKm(myLatLng,new LatLng(pr.getLat(),pr.getLng()));
                sTujuan = AddressFromLatLng(new LatLng(pr.getTo_lat(),pr.getTo_lng()));
                sAsal = AddressFromLatLng(new LatLng(pr.getLat(),pr.getLng()));

                arPenumpang.get(i).setTujuan(sTujuan);
                arPenumpang.get(i).setJarak(nJarak);
                arPenumpang.get(i).setAddress(sAsal);
                i=arPenumpang.size();
            }
        }
    }



    private boolean acceptOrder() {
        boolean ret=false;
        Penumpang penumpang = new Penumpang(this);
        mNoHpPenumpang = mSelMarker.getSnippet();
        if (penumpang.AcceptOrder(mNomorHp, mNoHpPenumpang)){
            ret=true;
        }
        return ret;
    }
    private void getLocation() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        if(locationManager == null){
            Log.d(TAG,"locationManager == null");
        }
        try {


            location = locationManager.getLastKnownLocation(locationManager
                    .getBestProvider(criteria, false));
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    6000, 1, (LocationListener) this);

            if (location == null) { //gps provider error try passive
                location = locationManager.getLastKnownLocation(locationManager
                        .getBestProvider(criteria, false));
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        2000, 1, (LocationListener) this);
            }
            if (location == null) { //gps provider error try passive
                location = locationManager.getLastKnownLocation(locationManager
                        .getBestProvider(criteria, false));
                locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER,
                        2000, 1, (LocationListener) this);
            }
            if (location == null) {
                if (map != null) {
                    location = map.getMyLocation();
                }
            }
        } catch(Exception e) {
            Toast.makeText(getBaseContext(),"GPS disconnected !" + e.toString(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if( mGoogleApiClient != null ) {
            try {
                mGoogleApiClient.connect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStop() {
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }
    private void displayPlace( Place place ) {
        if(myLatLng == null || place == null){
            Log.d(TAG,"myLatLng null or place null, unknown Curent Location");
            return;
        }

        String content = "";
        if( !TextUtils.isEmpty( place.getName() ) ) {
            content += "Name: " + place.getName() + "\n";
        }
        if( !TextUtils.isEmpty( place.getAddress() ) ) {
            content += "Address: " + place.getAddress() + "\n";
        }
        if( !TextUtils.isEmpty( place.getPhoneNumber() ) ) {
            content += "Phone: " + place.getPhoneNumber();
        }
        if( !TextUtils.isEmpty( place.getLatLng().toString() ) ) {
            content += "LatLng: " + place.getLatLng().toString() + "\n";
        }
        mPlaceTujuan = place;
        mPlaceTujuanText = content;
        mToLat = (float) mPlaceTujuan.getLatLng().latitude;
        mToLng = (float) mPlaceTujuan.getLatLng().longitude;

        saveSettingTujuan();
        DrawRoute(myLatLng,place.getLatLng());

        myLocation();
    }
    private void displayPlaceChildx(){
        float lat = mSetting.getFloat("tujuan_lat", 0);
        float lng=mSetting.getFloat("tujuan_lng",0);
        String tname=mSetting.getString("tujuan_name","");
        addMarkerTo(map, lat, lng,tname, mPlaceTujuanText);
    }

    private String AddressFromLatLng(LatLng ll) {
        Geocoder geocoder;

        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        String address="";
        try {
            addresses = geocoder.getFromLocation(ll.latitude, ll.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            if(addresses.size()>0) {
                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return address;
    }

    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
        if( requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK ) {
            mPlaceTujuan=PlacePicker.getPlace( data, this );
            displayPlace( mPlaceTujuan );
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }
    private void startChat(){
        startActivity(new Intent("com.talagasoft.oc_driver.ChatActivity"));
    }
    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

}
