package com.example.ecomm.Screens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.example.ecomm.R;

public class SplashScreenActivity extends AppCompatActivity {

    ProgressBar loader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        loader = findViewById(R.id.loader);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loader.setVisibility(View.VISIBLE);
            }
        },2000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
               finish();
            }
        },4000);
    }
}