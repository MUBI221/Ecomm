package com.example.ecomm.Screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecomm.MainActivity;
import com.example.ecomm.R;
import com.example.ecomm.Screens.Admin.AdminDashboardActivity;
import com.example.ecomm.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth myAuth = FirebaseAuth.getInstance();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference db = firebaseDatabase.getReference();
    ActivityLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferences = getSharedPreferences("myData",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.super.onBackPressed();
            }
        });

        if(!sharedPreferences.getString("loginStatus","").isEmpty()){
            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
            finish();
        }
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
            }
        });

        binding.emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignupActivity.class));
            }
        });
        binding.facebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglerBtn(binding.facebookTxt);
            }
        });
        binding.googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglerBtn(binding.googleTxt);
            }
        });
        binding.appleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglerBtn(binding.appleTxt);
            }
        });
    }

    public void togglerBtn(TextView textView){
        binding.facebookTxt.setVisibility(View.GONE);
        binding.googleTxt.setVisibility(View.GONE);
        binding.appleTxt.setVisibility(View.GONE);
        textView.setVisibility(View.VISIBLE);
    }

    public boolean emailValidation(){
        String input = binding.emailEditText.getText().toString().trim();
        String pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if(input.equals("")){
            binding.emailLayout.setError("Email Address is Required!!!");
            return false;
        } else if(!input.matches(pattern)){
            binding.emailLayout.setError("Enter Valid Email Address!!!");
            return false;
        } else {
            binding.emailLayout.setError(null);
            return true;
        }
    }

    public boolean passwordValidation(){
        String input = binding.passwordEditText.getText().toString().trim();
        if(input.equals("")){
            binding.passwordLayout.setError("Password is Required!!!");
            return false;
        } else if(input.length() < 8){
            binding.passwordLayout.setError("Password at least 8 characters!!!");
            return false;
        } else {
            binding.passwordLayout.setError(null);
            return true;
        }
    }

    public void validation(){
        if(MainActivity.connectionCheck(LoginActivity.this)){
            boolean emailErr = false, passwordErr = false;
            emailErr = emailValidation();
            passwordErr = passwordValidation();
            if((emailErr && passwordErr ) == true){
                Dialog loaddialog = new Dialog(LoginActivity.this);
                loaddialog.setContentView(R.layout.dialo_loading);
                loaddialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                loaddialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                loaddialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                loaddialog.getWindow().setGravity(Gravity.CENTER);
                loaddialog.setCancelable(false);
                loaddialog.setCanceledOnTouchOutside(false);
                TextView message = loaddialog.findViewById(R.id.message);
                message.setText("Login...");
                loaddialog.show();
                // Login Here
                myAuth.signInWithEmailAndPassword(binding.emailEditText.getText().toString().trim(),binding.passwordEditText.getText().toString().trim())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                loaddialog.dismiss();
                                Dialog alertdialog = new Dialog(LoginActivity.this);
                                alertdialog.setContentView(R.layout.dialog_success);
                                alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                alertdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                                alertdialog.getWindow().setGravity(Gravity.CENTER);
                                alertdialog.setCancelable(false);
                                alertdialog.setCanceledOnTouchOutside(false);
                                TextView message = alertdialog.findViewById(R.id.message);
                                message.setText("Login Successfully!!!");

                                FirebaseUser user = myAuth.getCurrentUser();

                                db.child("Users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            String role = snapshot.child("role").getValue().toString().trim();
                                            String status = snapshot.child("status").getValue().toString().trim();
                                            if(status.equals("1")){
                                                alertdialog.show();
                                                if(binding.rememberMe.isChecked()){
                                                    editor.putString("loginStatus","true");
                                                    editor.commit();
                                                }

                                                editor.putString("userId",user.getUid());
                                                editor.putString("role",role);
                                                editor.putString("status",status);
                                                editor.commit();

                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        alertdialog.dismiss();
                                                        startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                                                        finish();
                                                        if(role.equals("user")){
                                                        } else if(role.equals("admin")){
//                                                        startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
//                                                        finish();
                                                        }

                                                    }
                                                },3000);
                                            } else if(status.equals("0")){
                                                Toast.makeText(LoginActivity.this, "Your Account is Suspended By Admin!!!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loaddialog.dismiss();
                                Dialog alertdialog = new Dialog(LoginActivity.this);
                                alertdialog.setContentView(R.layout.dialog_error);
                                alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                alertdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                                alertdialog.getWindow().setGravity(Gravity.CENTER);
                                alertdialog.setCancelable(false);
                                alertdialog.setCanceledOnTouchOutside(false);
                                TextView message = alertdialog.findViewById(R.id.message);
                                message.setText("Email Address OR Password is wrong!!!");
                                alertdialog.show();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        alertdialog.dismiss();
                                    }
                                },3000);
                            }
                        });
            }
        }
    }

}