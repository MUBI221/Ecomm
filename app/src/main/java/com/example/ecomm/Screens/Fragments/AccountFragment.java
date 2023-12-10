package com.example.ecomm.Screens.Fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ecomm.MainActivity;
import com.example.ecomm.R;
import com.example.ecomm.Screens.AddressActivity;
import com.example.ecomm.Screens.LoginActivity;
import com.example.ecomm.Screens.ProductsActivity;
import com.example.ecomm.Screens.SignupActivity;
import com.example.ecomm.databinding.FragmentAccountBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class AccountFragment extends Fragment {

    FragmentAccountBinding binding;
    FirebaseAuth myAuth = FirebaseAuth.getInstance();
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String userId;
    StorageReference mStorage;
    StorageTask uploadTask;
    Uri imageUri;

    // Profile Image Dialog Components
    Dialog imagedialog;
    CircleImageView image;
    ImageButton imageadd;
    Button cancelBtn, saveChangesBtn;
    TextView imageErrTextView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentAccountBinding.inflate(inflater, container, false);
        preferences = inflater.getContext().getSharedPreferences("myData", MODE_PRIVATE);
        editor = preferences.edit();
        userId = preferences.getString("userId",null);
        mStorage = FirebaseStorage.getInstance().getReference();
        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog loaddialog = new Dialog(container.getContext());
                loaddialog.setContentView(R.layout.bottom_logout);
                loaddialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                loaddialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                loaddialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationBottom;
                loaddialog.getWindow().setGravity(Gravity.BOTTOM);
                loaddialog.setCancelable(false);
                loaddialog.setCanceledOnTouchOutside(false);
                Button cancelBtn, yesBtn;
                cancelBtn = loaddialog.findViewById(R.id.cancelBtn);
                yesBtn = loaddialog.findViewById(R.id.yesBtn);
                yesBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myAuth.signOut();
                        editor.clear();
                        editor.commit();
                        startActivity(new Intent(getContext(), LoginActivity.class));
                        getActivity().finish();
                    }
                });
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loaddialog.dismiss();
                    }
                });
                loaddialog.show();
            }
        });
        MainActivity.myRef.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    binding.name.setText(snapshot.child("name").getValue().toString().trim());
                    binding.email.setText(snapshot.child("email").getValue().toString().trim());
                    if(!snapshot.child("image").getValue().toString().trim().equals("")){
                        Glide.with(container.getContext()).load(snapshot.child("image").getValue().toString().trim()).into(binding.profileimage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.editadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagedialog = new Dialog(container.getContext());
                imagedialog.setContentView(R.layout.dialog_profile_image);
                imagedialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                imagedialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                imagedialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                imagedialog.getWindow().setGravity(Gravity.CENTER);
                imagedialog.setCancelable(false);
                imagedialog.setCanceledOnTouchOutside(false);
                image = imagedialog.findViewById(R.id.image);
                imageadd = imagedialog.findViewById(R.id.imageadd);
                cancelBtn = imagedialog.findViewById(R.id.cancelBtn);
                saveChangesBtn = imagedialog.findViewById(R.id.saveChangesBtn);
                imageErrTextView = imagedialog.findViewById(R.id.imageErrTextView);
                imagedialog.show();
                imageadd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/*");
                        startActivityForResult(intent, 420);
                    }
                });
                saveChangesBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(uploadTask != null && uploadTask.isInProgress()){
                            Toast.makeText(container.getContext(), "Image Upload In Process!!!", Toast.LENGTH_SHORT).show();
                        } else {
                            validation("false");
                        }
                    }
                });
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imagedialog.dismiss();
                    }
                });
                MainActivity.myRef.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            if(!snapshot.child("image").getValue().toString().trim().equals("")){
                                Glide.with(container.getContext()).load(snapshot.child("image").getValue().toString().trim()).into(image);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        binding.addressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(container.getContext(), AddressActivity.class));
            }
        });
        return binding.getRoot();
    }
    public boolean imageValidation(){
        if(imageUri == null){
            imageErrTextView.setText("Profile Image is Required!!!");
            imageErrTextView.setVisibility(View.VISIBLE);
            return false;
        } else {
            imageErrTextView.setText("");
            imageErrTextView.setVisibility(View.GONE);
            return true;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 420 && resultCode == RESULT_OK){
            imageUri = data.getData();
            image.setImageURI(imageUri);
        }
    }
    private String getFileExtension(Uri uri){
        ContentResolver cr = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }
    private void validation(String imageStatus) {
        if(MainActivity.connectionCheck(getContext())){
            boolean imageErr = false;
            if(imageStatus.equals("true")){
                imageErr = true;
            } else {
                imageErr = imageValidation();
            }
            if((imageErr) == true){
                if(imageUri != null){
                    Dialog loading = new Dialog(getContext());
                    loading.setContentView(R.layout.dialo_loading);
                    loading.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    loading.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    loading.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    loading.getWindow().setGravity(Gravity.CENTER);
                    loading.setCancelable(false);
                    loading.setCanceledOnTouchOutside(false);
                    TextView message = loading.findViewById(R.id.message);
                    message.setText("Uploading...");
                    loading.show();
                    uploadTask = mStorage.child("Profiles/"+userId+"."+getFileExtension(imageUri)).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    loading.dismiss();
                                    String photoLink = uri.toString();

                                    MainActivity.myRef.child("Users").child(userId).child("image").setValue(photoLink);
                                    Dialog alertdialog = new Dialog(getContext());
                                    alertdialog.setContentView(R.layout.dialog_success);
                                    alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                    alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    alertdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                                    alertdialog.getWindow().setGravity(Gravity.CENTER);
                                    alertdialog.setCancelable(false);
                                    alertdialog.setCanceledOnTouchOutside(false);
                                    TextView message = alertdialog.findViewById(R.id.message);
                                    message.setText("Product Edit Successfully!!!");
                                    alertdialog.show();

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            alertdialog.dismiss();
                                            imagedialog.dismiss();
                                            MainActivity.myRef.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if(snapshot.exists()){
                                                        if(!snapshot.child("image").getValue().toString().trim().equals("")){
                                                            Glide.with(getContext()).load(snapshot.child("image").getValue().toString().trim()).into(binding.profileimage);
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    },2000);

                                }
                            });
                        }
                    });
                }
            }
        }
    }
}