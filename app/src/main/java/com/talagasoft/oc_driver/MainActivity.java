package com.talagasoft.oc_driver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.maps.model.LatLng;
import com.talagasoft.oc_driver.model.Deposit;
import com.talagasoft.oc_driver.model.Penumpang;
import com.talagasoft.oc_driver.model.SettingServer;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener  {

    SharedPreferences mSetting = null;
    Boolean mLoggedIn=false;
    String mNama="",mNoHp="",mNoHpPenumpang="",mPlaceTujuanText="";
    float mToLat,mToLng,mToLatEnd,mToLngEnd;
    int mMode=0;
    TextView mDeposit;
    TextView mPoint;
    TextView txtPenumpang;
    SettingServer mSetServer;
    // Connection detector class
    ConnectionDetector cd;
    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();
    AdView mAdView;
    Penumpang mPenumpang;
    private int mTarif,mDompetAmount;
    String mTujuan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        DecimalFormat df = new DecimalFormat("###,###.##"); // or pattern "###,###.##$"
        mDeposit= (TextView) findViewById(R.id.deposit);
        mSetting = getSharedPreferences(getResources().getString(R.string.setting), Context.MODE_WORLD_READABLE);

        txtPenumpang=(TextView)findViewById(R.id.penumpang);
        String sDepo=mSetting.getString("deposit","0");
        if(sDepo.isEmpty())sDepo="0";
        Double o = Double.parseDouble(sDepo);
        mNoHpPenumpang=mSetting.getString("no_hp_penumpang","");
        mPlaceTujuanText=mSetting.getString("tujuan_nama","");
        mToLat=mSetting.getFloat("tujuan_lat",0);
        mToLng=mSetting.getFloat("tujuan_lng",0);
        mToLatEnd=mSetting.getFloat("tujuan_lat_end",0);
        mToLngEnd=mSetting.getFloat("tujuan_lng_end",0);
        mMode=mSetting.getInt("mode",0);
        mLoggedIn = mSetting.getBoolean("logged_in", false);
        mNama = mSetting.getString("nama", "Guest");
        mNoHp = mSetting.getString("no_hp", "0000");

        cd = new ConnectionDetector(getApplicationContext());
        // flag for Internet connection status
        Boolean isInternetPresent = false;
        // Check if Internet present
        isInternetPresent = cd.isConnectingToInternet();
        if (!isInternetPresent) {
            // Internet Connection is not present
            alert.showAlertDialog(this, "Internet Connection Error","Please connect to working Internet connection", false);
            // stop executing code by return
            return;
        }


        this.setTitle("Hai " + mNama);

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-1869511402643723~8811604696");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //ambil data dari web thread
        new getServerDataAsync().execute();

    }
    private class getServerDataAsync extends AsyncTask<Void, Integer, String>
    {
        String TAG = getClass().getSimpleName();

        protected void onPreExecute (){
            Log.d(TAG + " PreExceute","On pre Exceute......");
        }

        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround","On doInBackground...");
            mSetServer=new SettingServer(getBaseContext());
            mTarif=mSetServer.tarif();
            mDompetAmount=new Deposit(getBaseContext()).Saldo(mNoHp);

            if(!mNoHpPenumpang.isEmpty()){
                mPenumpang=new Penumpang(getBaseContext());
                mPenumpang.getById(mNoHpPenumpang);
                mTujuan = new GPSTracker(getBaseContext()).getAddress(
                        new LatLng(mPenumpang.get_to_lat(),mPenumpang.get_to_lng())
                );
            }
            return "You are at PostExecute";
        }

        protected void onProgressUpdate(Integer...a){
            Log.d(TAG + " onProgressUpdate", "You are in progress update ... " + a[0]);
        }

        protected void onPostExecute(String result) {
            Log.d(TAG + " onPostExecute", "" + result);
            DecimalFormat df = new DecimalFormat("###,###.##"); // or pattern "###,###.##$"

            if(mPenumpang!=null) {
                txtPenumpang.setText("" + mPenumpang.get_user_name() + ", " + mNoHpPenumpang +
                        ", " + mTujuan);
            }
            SharedPreferences.Editor editor = mSetting.edit();
            editor.putString("deposit", String.valueOf(mDompetAmount));
            editor.putInt("tarif",mTarif);
            editor.commit();
            mDeposit.setText(df.format(mDompetAmount));
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        callMenu(id);
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        mNama = mSetting.getString("nama", "Guest");
        this.setTitle("Hai " + mNama);

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        callMenu(id);


        return true;
    }
    private void callMenu(int id){
        Intent intent=null;
        if (id == R.id.cmdMonitor || id == R.id.nav_monitor) {
            intent = new Intent("com.talagasoft.oc_driver.MapsActivity");
            intent.putExtra("no_hp", mNoHp);
            intent.putExtra("nama", mNama);
            intent.putExtra("jenis","antar");
            startActivity(intent);

        } else if (id == R.id.cmdDompet || id == R.id.nav_dompet) {
            intent = new Intent("com.talagasoft.oc_driver.DompetActivity");
            intent.putExtra("no_hp", mNoHp);
            intent.putExtra("nama", mNama);
            startActivity(intent);

        }else if (id == R.id.cmdProfil || id == R.id.nav_profile) {
            intent = new Intent("com.talagasoft.oc_driver.AccountActivity");
            intent.putExtra("no_hp", mNoHp);
            intent.putExtra("nama", mNama);
            intent.putExtra("jenis","argo");
            startActivity(intent);

        } else if (id == R.id.action_logout || id == R.id.cmdLogout || id == R.id.nav_logout) {

            SharedPreferences.Editor editor = mSetting.edit();
            //Adding values to editor
            editor.putBoolean("logged_in", false);
            editor.putString("no_hp", "0000");
            editor.putString("nama", "Guest");

            //Saving values to editor
            editor.commit();
            Toast.makeText(this, "Anda sudah logout, terimakasih. ", Toast.LENGTH_LONG);
            //alert.showAlertDialog(this, "Berhasil logout.","Silahkan dijalankan lagi aplikasi dan masukkan user baru anda.", false ) ;
            restart(this, 2);
        }

    }
    @Override
    public void onClick(View view) {
        callMenu(view.getId());
    }
    public static void restart(Context context, int delay) {
        if (delay == 0) {
            delay = 1;
        }
        Log.e("", "restarting app");
        Intent restartIntent = context.getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName() );
        PendingIntent intent = PendingIntent.getActivity(
                context, 0,
                restartIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.set(AlarmManager.RTC, System.currentTimeMillis() + delay, intent);
        System.exit(2);
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
