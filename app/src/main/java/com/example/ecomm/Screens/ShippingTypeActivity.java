package com.example.ecomm.Screens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.example.ecomm.R;
import com.example.ecomm.databinding.ActivityAddressBinding;
import com.example.ecomm.databinding.ActivityShippingTypeBinding;

public class ShippingTypeActivity extends AppCompatActivity {

    ActivityShippingTypeBinding binding;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_type);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityShippingTypeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferences = getSharedPreferences("myData", MODE_PRIVATE);
        editor = preferences.edit();
        userId = preferences.getString("userId",null);
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShippingTypeActivity.super.onBackPressed();
            }
        });
    }
}