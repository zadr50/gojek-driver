package com.talagasoft.oc_driver.model;

/**
 * Created by compaq on 03/13/2016.
 */
public class PenumpangRecord {
    private long id;
    private String name,phone,jenis;
    private double lat;
    private double lng;
    private int rate;
    private double to_lat, to_lng;
    private float _jarak;
    private String _tujuan,_address;

    //items
    private String _item_name,_item_address;
    private int _item_qty, _item_price, _item_total,_order_id;
    private float _item_lat, _item_lng;
    private String _lokasi;

    public PenumpangRecord(){}

    public PenumpangRecord(long id, String name, double lat, double lng, String phone,
                           int rate,double to_lat, double to_lng,String jenis,
                           int vOrderId){
        super();
        this.id=id;
        this.name=name;
        this.lat=lat;
        this.lng=lng;
        this.phone=phone;
        this.rate=rate;
        this.to_lat=to_lat;
        this.to_lng=to_lng;
        this.jenis=jenis;
        this._order_id=vOrderId;

    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public double getTo_lat() {
        return to_lat;
    }

    public void setTo_lat(double to_lat) {
        this.to_lat = to_lat;
    }

    public double getTo_lng() {
        return to_lng;
    }

    public void setTo_lng(double to_lng) {
        this.to_lng = to_lng;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public String get_item_name() {
        return _item_name;
    }
    public void set_item_name(String value){
        _item_name=value;
    }

    public int get_item_qty() {
        return _item_qty;
    }
    public void set_item_qty(int value){
        _item_qty = value;
    }

    public int get_item_price() {
        return _item_price;
    }
    public void set_item_price(int value){
        _item_price = value;
    }

    public int get_item_total() {
        return _item_total;
    }
    public void set_item_total(int value){
        _item_total = value;
    }

    public String get_item_address() {
        return _item_address;
    }
    public void set_item_address(String value) {
        _item_address=value;
    }

    public float get_item_lat() {
        return _item_lat;
    }
    public void set_item_lat(float value) {
        _item_lat=value;
    }

    public float get_item_lng() {
        return _item_lng;
    }
    public void set_item_lng(float value) {
        _item_lng = value;
    }

    public int get_order_id() {
        return _order_id;
    }

    public void set_order_id(int _order_id) {
        this._order_id = _order_id;
    }

    public float getJarak() {
        return _jarak;
    }
    public void setJarak(float vJarak){this._jarak=vJarak;};

    public String getTujuan() {
        if(_tujuan==null) return "";
        return _tujuan;
    }
    public void setTujuan(String vData){this._tujuan=vData;};

    public void setAddress(String vData){
        this._address=vData;
    }
    public String getAddress() {
        return _address;
    }

    public void setLokasi(String vlokasi) {
        this._lokasi = vlokasi;
        this._tujuan=vlokasi;
    }
    public String getLokasi(){return _lokasi;}
}
