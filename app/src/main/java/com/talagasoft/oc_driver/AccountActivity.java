package com.talagasoft.oc_driver;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.talagasoft.oc_driver.model.Account;

public class AccountActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mNoHp;
    private EditText mNama;
    private EditText mAlamat;
    private  EditText mPassword;
    private SharedPreferences mSetting=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_activity);

        mSetting = getSharedPreferences(getResources().getString(R.string.setting), Context.MODE_WORLD_READABLE);

        mNoHp=(EditText) findViewById(R.id.handphone);
        mNama=(EditText) findViewById(R.id.nama);
        mAlamat=(EditText) findViewById(R.id.alamat);
        mPassword=(EditText) findViewById(R.id.password);
        mNama.setText(mSetting.getString("nama", "Guest"));
        Bundle b = new Bundle();
        b = getIntent().getExtras();
        if(b!=null) {
            mNoHp.setText(b.getString("no_hp"));
        }
        if(mNoHp.getText().toString()==""){
            mNoHp.setText(mSetting.getString("no_hp","0000"));
        }
        mNoHp.setEnabled(false);

        Account account=new Account(getBaseContext());
        if(account.getById(mNoHp.getText().toString())){
            mPassword.setText(account.get_password());
            mAlamat.setText(account.get_alamat());
            mNama.setText(account.get_user_name());
        }

    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        if(id==R.id.submit){
            if( saveAccount()){
                Toast.makeText(this,"Sukses data sudah masuk.",Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this,"Tidak bisa simpan data.",Toast.LENGTH_LONG).show();
            }
        } else if (id==R.id.cancel){
            finish();
        }
    }

    public boolean saveAccount() {
        Account account=new Account(getBaseContext());
        account.set_handphone(mNoHp.getText().toString());
        account.set_alamat(mAlamat.getText().toString());
        account.set_user_name(mNama.getText().toString());
        account.set_password(mPassword.getText().toString());
        return account.save();
    }
}
