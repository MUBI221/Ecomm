package com.example.ecomm.Screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecomm.MainActivity;
import com.example.ecomm.R;
import com.example.ecomm.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    FirebaseAuth myAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference db = firebaseDatabase.getReference();

    ActivitySignupBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignupActivity.super.onBackPressed();
            }
        });

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignupActivity.super.onBackPressed();
            }
        });

        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
            }
        });

        binding.nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nameValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {

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
        binding.cpasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cPasswordValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {

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

    public boolean nameValidation(){
        String input = binding.nameEditText.getText().toString().trim();
        if(input.equals("")){
            binding.nameLayout.setError("Name is Required!!!");
            return false;
        } else if(!Pattern.compile("^[a-zA-Z\\s]*$").matcher(input).matches()){
            binding.nameLayout.setError("Name In Only Text!!!");
            return false;
        } else if(input.length() < 3){
            binding.nameLayout.setError("Name at least 3 characters!!!");
            return false;
        } else {
            binding.nameLayout.setError(null);
            return true;
        }
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

    public boolean cPasswordValidation(){
        String input = binding.cpasswordEditText.getText().toString().trim();
        String input2 = binding.passwordEditText.getText().toString().trim();
        if(input.equals("")){
            binding.cpasswordLayout.setError("Confirm Password is Required!!!");
            return false;
        } else if(input.length() < 8){
            binding.cpasswordLayout.setError("Confirm Password at least 8 characters!!!");
            return false;
        } else if(!input.equals(input2)) {
            binding.cpasswordLayout.setError("Confirm Password is not matched!!!");
            return false;
        } else {
            binding.cpasswordLayout.setError(null);
            return true;
        }
    }

    public void validation(){
        if(MainActivity.connectionCheck(SignupActivity.this)){
            boolean nameErr = false, emailErr = false, passwordErr = false, cpasswordErr = false;
            nameErr = nameValidation();
            emailErr = emailValidation();
            passwordErr = passwordValidation();
            cpasswordErr = cPasswordValidation();
            if((nameErr && emailErr && passwordErr && cpasswordErr) == true){
                Dialog loaddialog = new Dialog(SignupActivity.this);
                loaddialog.setContentView(R.layout.dialo_loading);
                loaddialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                loaddialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                loaddialog.getWindow().setGravity(Gravity.CENTER);
                loaddialog.setCancelable(false);
                loaddialog.setCanceledOnTouchOutside(false);
                TextView message = loaddialog.findViewById(R.id.message);
                message.setText("Creating...");
                loaddialog.show();
                // Signup Here
                myAuth.createUserWithEmailAndPassword(binding.emailEditText.getText().toString().trim(),binding.passwordEditText.getText().toString().trim())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                loaddialog.dismiss();
                                Dialog alertdialog = new Dialog(SignupActivity.this);
                                alertdialog.setContentView(R.layout.dialog_success);
                                alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                alertdialog.getWindow().setGravity(Gravity.CENTER);
                                alertdialog.setCancelable(false);
                                alertdialog.setCanceledOnTouchOutside(false);
                                alertdialog.show();
                                String currentDate = new SimpleDateFormat("dd-MMMM-yyyy", Locale.getDefault()).format(new Date());


                                FirebaseUser user = myAuth.getCurrentUser();

                                HashMap<String,String> obj = new HashMap<String,String>();
                                obj.put("name",binding.nameEditText.getText().toString().trim());
                                obj.put("email",binding.emailEditText.getText().toString().trim());
                                obj.put("image","");
                                obj.put("gender","");
                                obj.put("role","user");
                                obj.put("address","");
                                obj.put("shipping","");
                                obj.put("createdOn",currentDate);
                                obj.put("status","1");

                                db.child("Users").child(user.getUid()).setValue(obj);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        alertdialog.dismiss();
                                        SignupActivity.super.onBackPressed();
                                    }
                                },2000);

//                            SignupActivity.super.onBackPressed();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loaddialog.dismiss();
                                Dialog alertdialog = new Dialog(SignupActivity.this);
                                alertdialog.setContentView(R.layout.dialog_error);
                                alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                alertdialog.getWindow().setGravity(Gravity.CENTER);
                                alertdialog.setCancelable(false);
                                alertdialog.setCanceledOnTouchOutside(false);
                                TextView message = alertdialog.findViewById(R.id.message);
                                message.setText("Your is Already Exist!!!");
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