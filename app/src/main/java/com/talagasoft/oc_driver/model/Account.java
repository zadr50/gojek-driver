package com.talagasoft.oc_driver.model;

import android.content.Context;

import com.talagasoft.oc_driver.R;

import org.w3c.dom.Document;

/**
 * Created by compaq on 01/18/2017.
 */

public class Account {
    Context _context;
    private long _user_id;
    private String _user_name, _handphone, _alamat, _driver, _user_id2, _password, _location, _job;
    private String _msg,_url;
    private float _lat,_lng;
    private int _rate_avg,_rate_count, _status;

    public  Account(Context _context){
        this._context=_context;
        this._url=_context.getResources().getString(R.string.url_source);

    }
    public boolean getById(String vHp){
        String url = _url + "user_pos.php?hp=" + vHp + "&job=driver";
        HttpXml web = new HttpXml(url);
        _user_id= (long) web.getKeyFloat("user_id");
        _user_name=web.getKey("user_name");
        _handphone=web.getKey("handphone");
        _alamat=web.getKey("alamat");
        _driver=web.getKey("driver");
        _user_id2=web.getKey("user_id2");
        _password=web.getKey("user_password");
        _location=web.getKey("location");
        _job=web.getKey("job");
        _lat=web.getKeyFloat("lat");
        _lng=web.getKeyFloat("lng");
        _rate_avg=web.getKeyInt("rate_avg");
        _rate_count=web.getKeyInt("rate_count");
        _status=web.getKeyInt("status");
        return true;
    }
    public boolean getTransList(){
        String mUrl = _url + "order_trans_list.php?hp=" + _handphone;
        HttpXml web = new HttpXml();
        Document doc = web.GetUrl(mUrl);
        return true;
    }
    public boolean save(){

        String mUrl=this._url+"account_save.php?handphone=" + _handphone +
                "&alamat="+_alamat+"&nama="+_user_name+"&password="+_password;

        boolean lTrue=false;
        HttpXml web=new HttpXml();
        StringBuilder doc=web.GetUrlData(mUrl);
        if(doc != null) {
            if(doc.toString().contains("success")) {
                lTrue=true;
            }
        }
        return lTrue;
    }

    public String getError(){
        return _msg;
    }

    public int Saldo(String mNoHp){
        String mUrl=_url +"deposit_saldo.php?handphone="+mNoHp;
        HttpXml web=new HttpXml(mUrl);
        return Integer.parseInt(web.getKey("saldo"));
    }

    public long get_user_id() {
        return _user_id;
    }

    public void set_user_id(long _user_id) {
        this._user_id = _user_id;
    }

    public String get_user_name() {
        return _user_name;
    }

    public void set_user_name(String _user_name) {
        this._user_name = _user_name;
    }

    public String get_handphone() {
        return _handphone;
    }

    public void set_handphone(String _handphone) {
        this._handphone = _handphone;
    }

    public String get_alamat() {
        return _alamat;
    }

    public void set_alamat(String _alamat) {
        this._alamat = _alamat;
    }

    public String get_driver() {
        return _driver;
    }

    public void set_driver(String _driver) {
        this._driver = _driver;
    }

    public String get_user_id2() {
        return _user_id2;
    }

    public void set_user_id2(String _user_id2) {
        this._user_id2 = _user_id2;
    }

    public String get_password() {
        return _password;
    }

    public void set_password(String _password) {
        this._password = _password;
    }

    public String get_location() {
        return _location;
    }

    public void set_location(String _location) {
        this._location = _location;
    }

    public String get_job() {
        return _job;
    }

    public void set_job(String _job) {
        this._job = _job;
    }

    public float get_lat() {
        return _lat;
    }

    public void set_lat(float _lat) {
        this._lat = _lat;
    }

    public float get_lng() {
        return _lng;
    }

    public void set_lng(float _lng) {
        this._lng = _lng;
    }

    public int get_rate_avg() {
        return _rate_avg;
    }

    public void set_rate_avg(int _rate_avg) {
        this._rate_avg = _rate_avg;
    }

    public int get_rate_count() {
        return _rate_count;
    }

    public void set_rate_count(int _rate_count) {
        this._rate_count = _rate_count;
    }

    public int get_status() {
        return _status;
    }

    public void set_status(int _status) {
        this._status = _status;
    }
}
