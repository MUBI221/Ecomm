package com.example.ecomm;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ecomm.Screens.LoginActivity;
import com.example.ecomm.Screens.SplashScreenActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    public static DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(myRef == null) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            myRef = FirebaseDatabase.getInstance().getReference();
        }
        startActivity(new Intent(MainActivity.this, SplashScreenActivity.class));
        finish();
    }
    public static boolean connectionCheck(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo dataConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if ((wifiConn != null && wifiConn.isConnected()) || (dataConn != null && dataConn.isConnected())) {
            return true;
        } else {
            Dialog loaddialog = new Dialog(context);
            loaddialog.setContentView(R.layout.dialog_connection_error);
            loaddialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            loaddialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            loaddialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            loaddialog.getWindow().setGravity(Gravity.CENTER);
            loaddialog.setCancelable(false);
            loaddialog.setCanceledOnTouchOutside(false);
            loaddialog.show();
            TextView messageTextViewTwo;
            messageTextViewTwo = loaddialog.findViewById(R.id.messageTextView);
            messageTextViewTwo.setText("Please Connect To The Internet To Proceed Further!!!");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loaddialog.dismiss();
                }
            }, 3000);
            return false;
        }
    }
}