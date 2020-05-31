package com.talagasoft.oc_driver.model;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.talagasoft.oc_driver.R;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by compaq on 01/13/2017.
 */

public class Penumpang {
    Context _context;
    String _msg="";
    String _url="";
    private String  TAG  = "Penumpang";
    private String mNoHpTo;
    private long _user_id;
    private String _user_name, _handphone, _alamat, _driver, _user_id2, _password,
            _location, _job, _jenis;
    private float _lat,_lng,_to_lat,_to_lng;
    private int _rate_avg,_rate_count, _status;

    public Penumpang(Context c) {
        _context=c;
        _url=_context.getResources().getString(R.string.url_source);
    }
    public Penumpang(){
        _url=_context.getResources().getString(R.string.url_source);
    }

    public boolean AcceptOrder(String mNomorHp, String mNoHpTo) {

        String mUrl=_url +"order_accept.php?handphone=" + mNomorHp + "&to=" + mNoHpTo;

        boolean lTrue=false;
        _msg="";
        HttpXml web=new HttpXml();
        StringBuilder doc=web.GetUrlData(mUrl);
        if(doc != null) {
            if(doc.toString().contains("success")) {
                lTrue=true;
            }
            _msg=doc.toString();
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

    public List<PenumpangRecord> getAllNewOrder(String mNomorHp, LatLng myLatLng) {
        String mUrl = _url + "order_list.php?hp=" + mNomorHp + "&lat=" + myLatLng.latitude + "&lng=" + myLatLng.longitude;
        HttpXml web = new HttpXml(mUrl);
        web.getGroup("people");
        List<PenumpangRecord> arPenumpang = new ArrayList<PenumpangRecord>();
        for(int i=0;i<web.getCount();i++) {

            PenumpangRecord pr=new PenumpangRecord();

            pr.setPhone(web.getKeyIndex(i,"handphone"));
            pr.setName(web.getKeyIndex(i,"user_name"));
            pr.setLat(web.getKeyIndexFloat(i,"lat"));
            pr.setLng(web.getKeyIndexFloat(i,"lng"));
            pr.setTo_lat(web.getKeyIndexFloat(i,"to_lat"));
            pr.setTo_lng(web.getKeyIndexFloat(i,"to_lng"));
            pr.setJenis(web.getKeyIndex(i,"jenis"));
            pr.setLokasi(web.getKeyIndex(i,"lokasi"));

            pr.set_item_name(web.getKeyIndex(i,"item_name"));
            pr.set_item_address(web.getKeyIndex(i,"item_address"));
            //pr.set_item_lat(web.getKeyIndexFloat(i,"item_lat"));
            //pr.set_item_lng(web.getKeyIndexFloat(i,"item_lng"));
            pr.set_item_price(web.getKeyIndexInt(i,"price"));
            pr.set_item_qty(web.getKeyIndexInt(i,"qty"));
            pr.set_item_total(web.getKeyIndexInt(i,"amount"));
            pr.set_order_id(web.getKeyIndexInt(i,"order_id"));

            arPenumpang.add(pr);
        }
        return arPenumpang;

        /*
        Document doc = web.GetUrl(mUrl);
        List<PenumpangRecord> arPenumpang = new ArrayList<PenumpangRecord>();
        if (doc != null) {
            Log.d(TAG, doc.toString());
            NodeList nl1, nl2, nl3;
            nl1 = doc.getElementsByTagName("people");
            if (nl1.getLength() > 0) {
                for (int i = 0; i < nl1.getLength(); i++) {
                    Node node1 = nl1.item(i);
                    nl2 = node1.getChildNodes();
                    Node latNode = nl2.item(web.getNodeIndex(nl2, "lat"));
                    double lat = Double.parseDouble(latNode.getTextContent());
                    Node lngNode = nl2.item(web.getNodeIndex(nl2, "lng"));
                    double lng = Double.parseDouble(lngNode.getTextContent());
                    Node hpNode = nl2.item(web.getNodeIndex(nl2, "handphone"));
                    String hp = String.valueOf(hpNode.getTextContent());
                    Node namaNode = nl2.item(web.getNodeIndex(nl2, "user_name"));
                    String nama = String.valueOf(namaNode.getTextContent());
                    Node toLatNode = nl2.item(web.getNodeIndex(nl2, "to_lat"));
                    double to_lat = Double.parseDouble(toLatNode.getTextContent());
                    Node toLngNode = nl2.item(web.getNodeIndex(nl2, "to_lng"));
                    double to_lng = Double.parseDouble(toLngNode.getTextContent());
                    Node jenisNode = nl2.item(web.getNodeIndex(nl2, "jenis"));
                    String jenis = String.valueOf(jenisNode.getTextContent());

                    arPenumpang.add(new PenumpangRecord(1, nama, lat, lng, hp, 1,to_lat,to_lng,jenis));
                    Log.d("getAllNewOrder","Nama="+nama+", lat="+lat+", lng="+lng+", hp="+hp+", to_lat="+to_lat+", to_lng="+to_lng);
                }
            }
        }
        */
    }

    public boolean FinishOrder(String mNoHp) {
        String mUrl=_url +"order_finish.php?handphone=" + mNoHp;

        boolean lTrue=false;
        _msg="";
        HttpXml web=new HttpXml();
        StringBuilder doc=web.GetUrlData(mUrl);
        if(doc != null) {
            if(doc.toString().contains("success")) {
                lTrue=true;
            }
            _msg=doc.toString();
        }
        return lTrue;
    }
    public boolean getById(String vHp){
        String url = _url + "user_pos.php?hp=" + vHp + "&job=penumpang";
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
        _to_lat=web.getKeyFloat("to_lat");
        _to_lng=web.getKeyFloat("to_lng");
        _jenis=web.getKey("jenis");

        return true;
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

    public float get_to_lat() {
        return _to_lat;
    }

    public void set_to_lat(float _to_lat) {
        this._to_lat = _to_lat;
    }

    public float get_to_lng() {
        return _to_lng;
    }

    public void set_to_lng(float _to_lng) {
        this._to_lng = _to_lng;
    }

    public String get_jenis() {
        return _jenis;
    }

    public void set_jenis(String _jenis) {
        this._jenis = _jenis;
    }
}

