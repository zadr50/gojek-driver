package com.talagasoft.oc_driver.model;

import android.content.Context;
import android.util.Log;

import com.talagasoft.oc_driver.R;

import java.util.ArrayList;

/**
 * Created by andri on 04/30/2017.
 */

public class OrderCartController {
    //table order_header
    private int _order_no, _status, _item_count;
    private String _order_date,_driver_hp;
    private double _amount,_lat, _lon, _lat_driver,_lon_driver;
    private String _supp_name,_supp_address,_item_no;
    //varable
    Context _context;
    public OrderCartController(Context vContext){

        this._context=vContext;
    }

    public ArrayList<OrderCartItem> getAll(String vHp) {
        ArrayList<OrderCartItem> retVal=new ArrayList();

        String url =  this._context.getString(R.string.url_source) + "cart.php?hp="+vHp;
        HttpXml web = new HttpXml(url);
        web.getGroup("item");
        for (int i = 0; i < web.getCount(); i++) {
            OrderCartItem item=new OrderCartItem(_context);
            item.set_item_code(web.getKeyIndex(i,"item_no"));

            item.set_item_name(web.getKeyIndex(i,"item_name"));
            item.set_price(web.getKeyIndexFloat(i,"price"));
            item.set_amount_item(web.getKeyIndexFloat(i,"amount"));
            item.set_qty(web.getKeyIndexInt(i,"qty"));
            item.set_icon(web.getKeyIndex(i,"icon_file"));
            retVal.add(item);
        }
        return retVal;
    }


    public int get_order_no() {
        return _order_no;
    }

    public void set_order_no(int _order_no) {
        this._order_no = _order_no;
    }

    public int get_status() {
        return _status;
    }

    public void set_status(int _status) {
        this._status = _status;
    }

    public int get_item_count() {
        return _item_count;
    }

    public void set_item_count(int _item_count) {
        this._item_count = _item_count;
    }

    public String get_order_date() {
        return _order_date;
    }

    public void set_order_date(String _order_date) {
        this._order_date = _order_date;
    }

    public String get_driver_hp() {
        return _driver_hp;
    }

    public void set_driver_hp(String _driver_hp) {
        this._driver_hp = _driver_hp;
    }

    public double get_amount() {
        return _amount;
    }

    public void set_amount(double _amount) {
        this._amount = _amount;
    }

    public double get_lat() {
        return _lat;
    }

    public void set_lat(double _lat) {
        this._lat = _lat;
    }

    public double get_lon() {
        return _lon;
    }

    public void set_lon(double _lon) {
        this._lon = _lon;
    }

    public double get_lat_driver() {
        return _lat_driver;
    }

    public void set_lat_driver(double _lat_driver) {
        this._lat_driver = _lat_driver;
    }

    public double get_lon_driver() {
        return _lon_driver;
    }

    public void set_lon_driver(double _lon_driver) {
        this._lon_driver = _lon_driver;
    }

    public boolean loadByItem(String vItemNo) {
        this._item_no=vItemNo;
        String url=this._context.getString(R.string.url_source);
        HttpXml http=new HttpXml(url+"item_master_supplier.php?item_no="+vItemNo);
        this._supp_name=http.getKey("supp_name");
        this._supp_address=http.getKey("address");
        return true;
    }

    public String get_supp_name() {
        return _supp_name;
    }

    public String get_supp_address() {
        return _supp_address;
    }
}
