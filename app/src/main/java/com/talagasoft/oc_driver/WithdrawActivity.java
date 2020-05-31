package com.talagasoft.oc_driver;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.talagasoft.oc_driver.model.Deposit;

public class WithdrawActivity extends AppCompatActivity
        implements View.OnClickListener {

    private String mNoHp="";
    private EditText mNama;
    private EditText mBank;
    private EditText mJumlah;
    private SharedPreferences mSetting=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.withdraw_activity);
        Bundle b = new Bundle();
        b = getIntent().getExtras();
        if(b!=null) {
            mNoHp = b.getString("no_hp");
        }
        mSetting = getSharedPreferences(getResources().getString(R.string.setting), Context.MODE_WORLD_READABLE);
        if(mNoHp==""){
            mNoHp=mSetting.getString("no_hp","0000");
        }
        mNama=(EditText) findViewById(R.id.nama);
        mBank=(EditText) findViewById(R.id.bank);
        mJumlah=(EditText) findViewById(R.id.jumlah );

    }


    @Override
    public void onClick(View view) {
        int id=view.getId();
        if(id==R.id.submit){
            Deposit deposit=new Deposit(this);
            if( deposit.Save(mNoHp,mBank.getText().toString(),mNama.getText().toString(),
                    -1*Integer.parseInt(mJumlah.getText().toString())) ) {
                String msg="Sukses data penarikan anda sudah masuk, tunggu beberapa saat untuk ditransfer ke rekening anda.";
                Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this,deposit.getError(),Toast.LENGTH_LONG).show();
            }
        }
    }
}
