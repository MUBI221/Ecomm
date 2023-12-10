package com.example.ecomm.Screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ecomm.MainActivity;
import com.example.ecomm.R;
import com.example.ecomm.databinding.ActivityAddNewAddressBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Pattern;

public class AddNewAddressActivity extends AppCompatActivity {

    ActivityAddNewAddressBinding binding;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String userId, forEdit, addressId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_address);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityAddNewAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferences = getSharedPreferences("myData", MODE_PRIVATE);
        editor = preferences.edit();
        userId = preferences.getString("userId",null);

        Bundle extra = getIntent().getExtras();
        if(!extra.isEmpty()){
            forEdit = extra.getString("edit");
            addressId = extra.getString("addressId");
        }
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddNewAddressActivity.super.onBackPressed();
            }
        });
        binding.clearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.nameEditText.setText(null);
            }
        });
        binding.clearText2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.addressEditText.setText(null);
            }
        });
        binding.nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(binding.nameEditText.getText().toString().length() > 0){
                    binding.clearText.setVisibility(View.VISIBLE);
                } else {
                    binding.clearText.setVisibility(View.GONE);
                }
                addressNameValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.addressEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(binding.addressEditText.getText().toString().length() > 0){
                    binding.clearText2.setVisibility(View.VISIBLE);
                } else {
                    binding.clearText2.setVisibility(View.GONE);
                }
                addressDetailValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        MainActivity.myRef.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Glide.with(AddNewAddressActivity.this).load(snapshot.child("image").getValue().toString().trim()).into(binding.profileimage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
            }
        });

        binding.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.connectionCheck(AddNewAddressActivity.this)){
                    MainActivity.myRef.child("Address").child(addressId).removeValue();
                    MainActivity.myRef.child("Users").child(userId).child("address").setValue("");
                    Dialog loaddialog = new Dialog(AddNewAddressActivity.this);
                    loaddialog.setContentView(R.layout.dialog_success);
                    loaddialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    loaddialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    loaddialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    loaddialog.getWindow().setGravity(Gravity.CENTER);
                    loaddialog.setCancelable(false);
                    loaddialog.setCanceledOnTouchOutside(false);
                    TextView message = loaddialog.findViewById(R.id.message);
                    message.setText("Address Removed Successfully!!!");
                    loaddialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loaddialog.dismiss();
                            AddNewAddressActivity.super.onBackPressed();
                        }
                    },2000);
                }
            }
        });

        if(forEdit.equals("true")){
            binding.navTitle.setText("Edit Address");
            binding.deleteBtn.setVisibility(View.VISIBLE);
            binding.addBtn.setText("Edit");
            MainActivity.myRef.child("Address").child(addressId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    binding.nameEditText.setText(snapshot.child("name").getValue().toString().trim());
                    binding.addressEditText.setText(snapshot.child("address").getValue().toString().trim());
                    if(snapshot.child("defaultStatus").getValue().toString().trim().equals("true")){
                        binding.defaultAddressCheckBox.setChecked(true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
    public boolean addressNameValidation(){
        String input = binding.nameEditText.getText().toString().trim();
        if(input.equals("")){
            binding.nameEditText.setError("Address Name is Required!!!");
            return false;
        } else if(!Pattern.compile("^[a-zA-Z\\s]*$").matcher(input).matches()){
            binding.nameEditText.setError("Address Name In Only Text!!!");
            return false;
        } else if(input.length() < 3){
            binding.nameEditText.setError("Address Name at least 3 characters!!!");
            return false;
        } else {
            binding.nameEditText.setError(null);
            return true;
        }
    }
    public boolean addressDetailValidation(){
        String input = binding.addressEditText.getText().toString().trim();
        if(input.equals("")){
            binding.addressEditText.setError("Address is Required!!!");
            return false;
        } else if(input.length() < 20){
            binding.addressEditText.setError("Address at least 20 characters!!!");
            return false;
        } else {
            binding.addressEditText.setError(null);
            return true;
        }
    }
    public void validation(){
        if(MainActivity.connectionCheck(AddNewAddressActivity.this)){
            boolean addressNameErr = false, addressDetailErr = false;
            addressNameErr = addressNameValidation();
            addressDetailErr = addressDetailValidation();
            if((addressNameErr && addressDetailErr) == true){
                if(forEdit.equals("true")){
                    MainActivity.myRef.child("Address").child(addressId).child("name").setValue(binding.nameEditText.getText().toString().trim());
                    MainActivity.myRef.child("Address").child(addressId).child("address").setValue(binding.addressEditText.getText().toString().trim());
                    if(binding.defaultAddressCheckBox.isChecked()){
                        MainActivity.myRef.child("Address").child(addressId).child("defaultStatus").setValue("true");
                        MainActivity.myRef.child("Users").child(userId).child("address").setValue(addressId);
                    } else {
                        MainActivity.myRef.child("Address").child(addressId).child("defaultStatus").setValue("");
                        MainActivity.myRef.child("Users").child(userId).child("address").setValue("");
                    }
                    Dialog loaddialog = new Dialog(AddNewAddressActivity.this);
                    loaddialog.setContentView(R.layout.dialog_success);
                    loaddialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    loaddialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    loaddialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    loaddialog.getWindow().setGravity(Gravity.CENTER);
                    loaddialog.setCancelable(false);
                    loaddialog.setCanceledOnTouchOutside(false);
                    TextView message = loaddialog.findViewById(R.id.message);
                    message.setText("Address Edited Successfully!!!");
                    loaddialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loaddialog.dismiss();
                            AddNewAddressActivity.super.onBackPressed();
                        }
                    },2000);
                } else {
                    String uploadId = MainActivity.myRef.child("Address").push().getKey();
                    HashMap<String,String> Obj = new HashMap<String,String>();
                    Obj.put("name",binding.nameEditText.getText().toString().trim());
                    Obj.put("address",binding.addressEditText.getText().toString().trim());
                    Obj.put("UID",userId);
                    Obj.put("defaultStatus","");
                    if(binding.defaultAddressCheckBox.isChecked()){
                        Obj.put("defaultStatus","true");
                        MainActivity.myRef.child("Users").child(userId).child("address").setValue(uploadId);
                    }
                    MainActivity.myRef.child("Address").child(uploadId).setValue(Obj);
                    Dialog loaddialog = new Dialog(AddNewAddressActivity.this);
                    loaddialog.setContentView(R.layout.dialog_success);
                    loaddialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    loaddialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    loaddialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    loaddialog.getWindow().setGravity(Gravity.CENTER);
                    loaddialog.setCancelable(false);
                    loaddialog.setCanceledOnTouchOutside(false);
                    TextView message = loaddialog.findViewById(R.id.message);
                    message.setText("Address Added Successfully!!!");
                    loaddialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loaddialog.dismiss();
                            AddNewAddressActivity.super.onBackPressed();
                        }
                    },2000);
                }

            }
        }
    }
}