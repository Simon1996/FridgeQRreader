package com.gnirt69.qrcodesannerexample;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by NgocTri on 6/18/2016.
 */
public class MainActivity extends Activity implements ZXingScannerView.ResultHandler {
    private static final String USERS = "users";
    private ZXingScannerView mScannerView;
    String res;
    DatabaseReference mDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDataBase = FirebaseDatabase.getInstance().getReference();
    }

    public void onClick(View v) {
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        //Do anything with result here :D
        Log.w("handleResult", result.getText());
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Scan result");
//        builder.setMessage(result.getText());
//        AlertDialog alertDialog = builder.create();
        //  alertDialog.show();
        getValueFromServer(result.getText().toString());
        //Resume scanning
        //mScannerView.resumeCameraPreview(this);
    }

    private void getValueFromServer(String result) {
        res = result;
        mDataBase.child(USERS).child(result).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    mDataBase.child(USERS).child(dataSnapshot.getKey()).setValue(1);
                    makeToastOK();
                } else {
                    makeToast();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void makeToastOK() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan result");
        builder.setMessage("All ok, User founded");
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void makeToast() {
        Toast.makeText(this, "No such user", Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onDestroy() {
        mDataBase.child(USERS).child(res).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    mDataBase.child(USERS).child(dataSnapshot.getKey()).setValue(0);
                }
                else{
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        super.onDestroy();
    }
}