package com.talagasoft.oc_driver;

import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.talagasoft.oc_driver.adapter.FoodCartAdapter;
import com.talagasoft.oc_driver.model.OrderCartController;
import com.talagasoft.oc_driver.model.OrderCartItem;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class OrderFoodsActivity extends AppCompatActivity {
    TextView txtTotal,txtOutlet,txtAlamat;
    ListView lstData;
    String mSuppCode="",mNoHp,mAlamat,mSuppName,mItemNo="";
    String TAG = getClass().getSimpleName();
    Double mTotal;
    FoodCartAdapter foodCartAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_foods);
        //mSuppCode=getIntent().getStringExtra("supp_code");
        mNoHp=getIntent().getStringExtra("no_hp");
        txtTotal=(TextView)findViewById(R.id.txtTotal);
        txtOutlet=(TextView)findViewById(R.id.txtOutlet);
        txtAlamat=(TextView)findViewById(R.id.txtAlamat);
        lstData=(ListView)findViewById(R.id.lstData);

        new getServerDataAsync().execute();

    }

    private class getServerDataAsync extends AsyncTask<Void, Integer, String>
    {

        protected void onPreExecute (){

            Log.d(TAG + " PreExceute","On pre Exceute......");
        }

        protected String doInBackground(Void...arg0) {
            Log.d(TAG + " DoINBackGround","On doInBackground...");
            foodCartAdapter=new FoodCartAdapter(getBaseContext(),mNoHp);
            OrderCartController order = new OrderCartController(getBaseContext());
            ArrayList<OrderCartItem> items= order.getAll(mNoHp);
            mTotal= Double.valueOf(0);
            for(int i=0;i<items.size();i++){
                mTotal+=items.get(i).get_amount_item();
            }
            mItemNo=items.get(0).get_item_code();
            if(order.loadByItem(mItemNo)){
                mSuppName=order.get_supp_name();
                mAlamat=order.get_supp_address();
            }


            return "You are at PostExecute";
        }

        protected void onProgressUpdate(Integer...a){
            Log.d(TAG + " onProgressUpdate", "You are in progress update ... " + a[0]);
        }

        protected void onPostExecute(String result) {
            Log.d(TAG + " onPostExecute", "" + result);
            lstData.setAdapter(foodCartAdapter);
            DecimalFormat df = new DecimalFormat("###,###.##"); // or pattern "###,###.##$"
            txtTotal.setText(df.format(mTotal));
            txtAlamat.setText(mAlamat);
            txtOutlet.setText(mSuppName);
        }
    }
}
