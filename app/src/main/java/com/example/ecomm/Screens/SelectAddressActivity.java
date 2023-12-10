package com.example.ecomm.Screens;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecomm.MainActivity;
import com.example.ecomm.R;
import com.example.ecomm.databinding.ActivitySelectAddressBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class SelectAddressActivity extends AppCompatActivity {

    ActivitySelectAddressBinding binding;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String userId, selectedAddressId;
    RadioButton selectedRadioButton = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_address);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivitySelectAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferences = getSharedPreferences("myData", MODE_PRIVATE);
        editor = preferences.edit();
        userId = preferences.getString("userId",null);
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectAddressActivity.super.onBackPressed();
            }
        });
        binding.newAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SelectAddressActivity.this, AddressActivity.class));
            }
        });
        MainActivity.myRef.child("Address").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int addressCount = 0;
                for(DataSnapshot ds : snapshot.getChildren()){
                    if(ds.child("UID").getValue().toString().trim().equals(userId)){
                        View addressItemLayout = LayoutInflater.from(SelectAddressActivity.this).inflate(R.layout.selected_address_listview,null);
                        TextView addressName, addressDetail, defaultStatus;
                        RadioButton radioBtn;
                        LinearLayout itemContainer;
                        addressName = addressItemLayout.findViewById(R.id.addressName);
                        addressDetail = addressItemLayout.findViewById(R.id.addressDetail);
                        defaultStatus = addressItemLayout.findViewById(R.id.defaultStatus);
                        radioBtn = addressItemLayout.findViewById(R.id.radioBtn);
                        itemContainer = addressItemLayout.findViewById(R.id.itemContainer);
                        addressName.setText(ds.child("name").getValue().toString().trim());
                        addressDetail.setText(ds.child("address").getValue().toString().trim());
                        if(ds.child("defaultStatus").getValue().toString().trim().equals("true")){
                            defaultStatus.setVisibility(View.VISIBLE);
                        }
                        radioBtn.setVisibility(View.VISIBLE);
                        if(addressCount == 0){
                            MainActivity.myRef.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dsnapshot) {
                                    if(dsnapshot.child("address").getValue().toString().trim().equals("")){
                                        radioBtn.setChecked(true);
                                        setRadioBtn(radioBtn, ds.getKey());
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        addressCount++;
                        MainActivity.myRef.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dsnapshot) {
                                if(!dsnapshot.child("address").getValue().toString().trim().equals("")){
                                    if(dsnapshot.child("address").getValue().toString().trim().equals(ds.getKey())){
                                        if (selectedRadioButton != null) {
                                            selectedRadioButton.setChecked(false);
                                        }
                                        radioBtn.setChecked(true);
                                        setRadioBtn(radioBtn, ds.getKey());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        itemContainer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (selectedRadioButton != null) {
                                    selectedRadioButton.setChecked(false);
                                }
                                radioBtn.setChecked(true);
                                setRadioBtn(radioBtn, ds.getKey());
                            }
                        });
                        radioBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (selectedRadioButton != null) {
                                    selectedRadioButton.setChecked(false);
                                }
                                radioBtn.setChecked(true);
                                setRadioBtn(radioBtn, ds.getKey());
                            }
                        });
                        binding.addressContainer.addView(addressItemLayout);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.connectionCheck(SelectAddressActivity.this)){
                    MainActivity.myRef.child("Users").child(userId).child("address").setValue(selectedAddressId);
                    SelectAddressActivity.super.onBackPressed();
                }
            }
        });
    }
    public void setRadioBtn(RadioButton radioBtn, String addressIds){
        selectedRadioButton = radioBtn;
        selectedAddressId = addressIds;
    }
}