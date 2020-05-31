package com.talagasoft.oc_driver.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.talagasoft.oc_driver.R;
import com.talagasoft.oc_driver.model.OrderCartItem;
import com.talagasoft.oc_driver.model.Penumpang;
import com.talagasoft.oc_driver.model.PenumpangRecord;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by andri on 05/01/2017.
 */

public class PenumpangAdapter implements ListAdapter {
    Context _context;
    List<PenumpangRecord> _penumpang;
    private static LayoutInflater inflater=null;

    public PenumpangAdapter(Context baseContext, List<PenumpangRecord> arPenumpang) {
        this._context=baseContext;
        this._penumpang=arPenumpang;
        inflater = ( LayoutInflater ) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int i) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return _penumpang.size();
    }

    @Override
    public Object getItem(int i) {
        return _penumpang.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View vi = view;
        ViewHolder holder;
        if(vi==null){
            holder = new ViewHolder();
            vi = inflater.inflate(R.layout.order_row, null);
            holder.nama=(TextView)vi.findViewById(R.id.tanggal);
            holder.handphone=(TextView)vi.findViewById(R.id.amount);
            holder.jarak=(TextView)vi.findViewById(R.id.km);
            holder.tujuan=(TextView)vi.findViewById(R.id.tujuan);
            vi.setTag( holder );
        } else {
            holder = (ViewHolder) vi.getTag();
        }
        PenumpangRecord item = _penumpang.get(i);
        if (item  != null) {
            holder.nama.setText(item.getName());
            holder.handphone.setText(item.getPhone());
            holder.jarak.setText(""+item.getJarak()+" Km");
            holder.tujuan.setText(item.getTujuan());
        }
        return vi;
    }


    private class ViewHolder {
        TextView nama,handphone,jarak,tujuan;
    }

    @Override
    public int getItemViewType(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        if(_penumpang.size()>0){
            return _penumpang.size();
        } else {
            return 1;
        }
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
