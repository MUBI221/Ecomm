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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ecomm.MainActivity;
import com.example.ecomm.R;
import com.example.ecomm.databinding.ActivityProductDetailBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ProductDetailActivity extends AppCompatActivity {

    ActivityProductDetailBinding binding;
    String PID = "",userId = "";
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    boolean foundInFvrt = false;
    String fvrtItemId = "";
    int pPrice = 0, pStock = 0, pQty = 1, pDiscount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferences = getSharedPreferences("myData", MODE_PRIVATE);
        editor = preferences.edit();
        userId = preferences.getString("userId",null);

        Bundle extra = getIntent().getExtras();
        if(extra != null){
            PID = extra.getString("pid");
        }

        MainActivity.myRef.child("Products").child(PID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Glide.with(ProductDetailActivity.this).load(snapshot.child("pImage").getValue().toString().trim()).into(binding.pImage);
                    binding.pName.setText(snapshot.child("pName").getValue().toString().trim());
                    binding.pNameTitle.setText(snapshot.child("pName").getValue().toString().trim());
                    binding.pDesc.setText(snapshot.child("pDesc").getValue().toString().trim());

                    if(Integer.parseInt(snapshot.child("pStock").getValue().toString().trim()) < 1){
                        binding.pStock.setText("Out Of Stock");
                        binding.qtyContainer.setVisibility(View.GONE);
                        binding.btnAddToCart.setVisibility(View.GONE);
                        binding.totalContainer.setVisibility(View.GONE);
                        binding.btnOutOfStock.setVisibility(View.VISIBLE);
                    } else {
                        binding.pStock.setText(snapshot.child("pStock").getValue().toString().trim()+" Stock");
                    }

                    if(Integer.parseInt(snapshot.child("pDiscount").getValue().toString().trim()) > 0){
                        binding.pDiscount.setVisibility(View.VISIBLE);
                        binding.pPriceOff.setVisibility(View.VISIBLE);
                        binding.pDiscount.setText(snapshot.child("pDiscount").getValue().toString().trim()+"% OFF");
                    }

                    binding.pQty.setText(""+pQty);
                    double discount = Double.parseDouble(snapshot.child("pDiscount").getValue().toString().trim())/100;
                    double calcDiscount = Double.parseDouble(snapshot.child("pPrice").getValue().toString().trim()) * discount;
                    double totalPrice = Double.parseDouble(snapshot.child("pPrice").getValue().toString().trim()) - calcDiscount;
                    binding.totalPrice.setText("$"+Math.round(totalPrice));
                    binding.pPriceOff.setText("$"+ snapshot.child("pPrice").getValue().toString().trim());
                    setData(
                            snapshot.child("pPrice").getValue().toString().trim(),
                            snapshot.child("pStock").getValue().toString().trim(),
                            snapshot.child("pDiscount").getValue().toString().trim()
                    );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        MainActivity.myRef.child("AddToCart").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (userId.equals(ds.child("UID").getValue()) && ds.child("PID").getValue().equals(PID)) {
                        setQty(ds.child("qty").getValue().toString().trim());
                        MainActivity.myRef.child("Products").child(ds.child("PID").getValue().toString().trim()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                                if(datasnapshot.exists()){
                                    double discount = Double.parseDouble(datasnapshot.child("pDiscount").getValue().toString())/100;
                                    double calcDiscount = Double.parseDouble(datasnapshot.child("pPrice").getValue().toString().trim()) * discount;
                                    double totalPrice = Double.parseDouble(datasnapshot.child("pPrice").getValue().toString().trim()) - calcDiscount;
                                    int total = ((int) Math.round(totalPrice)*Integer.parseInt(ds.child("qty").getValue().toString().trim()));
                                    binding.totalPrice.setText("$"+total);
                                    binding.pPriceOff.setText("$"+(Integer.parseInt(datasnapshot.child("pPrice").getValue().toString().trim()) * Integer.parseInt(ds.child("qty").getValue().toString().trim())));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG", "onCancelled: " + error.getMessage());
            }
        });

        binding.btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCart();
            }
        });

        fetchWishlist();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProductDetailActivity.super.onBackPressed();
            }
        });

        binding.qtyMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minusQty();
            }
        });

        binding.qtyAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addQty();
            }
        });

        binding.wishlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wishlistBtnClicked();
            }
        });


    }
    public void setQty(String qty){
        pQty = Integer.parseInt(qty);
        binding.pQty.setText(""+pQty);
    }
    public void fetchWishlist(){
        MainActivity.myRef.child("Wishlist").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int favoriteCount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (userId.equals(ds.child("UID").getValue()) && ds.child("PID").getValue().equals(PID)) {
                            favoriteCount++;
                            setWishlistId(ds.getKey());
                        }
                    }

                    if (favoriteCount > 0) {
                        binding.wishlistBtn.setImageResource(R.drawable.heart_gradient);
                        setWishlistStatus(true);
                    } else {
                        setWishlistStatus(false);
                    }
                } else {
                    setWishlistStatus(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG", "onCancelled: " + error.getMessage());
            }
        });
    }
    public void setWishlistStatus(Boolean status){
        foundInFvrt = status;
    }
    public void setWishlistId(String Id){
        fvrtItemId = Id;
    }

    public void wishlistBtnClicked(){
        if(foundInFvrt == true){
            MainActivity.myRef.child("Wishlist").child(fvrtItemId).removeValue();
//            setWishlistStatus(false);
            binding.wishlistBtn.setImageResource(R.drawable.heart_outlined);
            Dialog alertdialog = new Dialog(ProductDetailActivity.this);
            alertdialog.setContentView(R.layout.dialog_success);
            alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            alertdialog.getWindow().setGravity(Gravity.CENTER);
            alertdialog.setCancelable(false);
            alertdialog.setCanceledOnTouchOutside(false);
            TextView message = alertdialog.findViewById(R.id.message);
            message.setText("Product Removed From Wishlist Successfully");
            alertdialog.show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    alertdialog.dismiss();
                }
            },2000);
        } else if(foundInFvrt == false) {
            HashMap<String, String> Obj = new HashMap<String,String>();
            Obj.put("UID",userId);
            Obj.put("PID",PID);
            MainActivity.myRef.child("Wishlist").push().setValue(Obj);
//            setWishlistStatus(true);
//            binding.wishlistBtn.setImageResource(R.drawable.heart_gradient);
            Dialog alertdialog = new Dialog(ProductDetailActivity.this);
            alertdialog.setContentView(R.layout.dialog_success);
            alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            alertdialog.getWindow().setGravity(Gravity.CENTER);
            alertdialog.setCancelable(false);
            alertdialog.setCanceledOnTouchOutside(false);
            TextView message = alertdialog.findViewById(R.id.message);
            message.setText("Product is Added into Wishlist");
            alertdialog.show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    alertdialog.dismiss();
                }
            },2000);
        }
        fetchWishlist();
    }
    public void setData(String pPriceVal, String pStockVal, String pDiscountVal){
        pPrice = Integer.parseInt(pPriceVal);
        pStock = Integer.parseInt(pStockVal);
        pDiscount = Integer.parseInt(pDiscountVal);
    }
    public void addQty(){
        if(pQty < pStock ){
            pQty++;
            binding.pQty.setText(""+pQty);
            double discount = Double.parseDouble(""+pDiscount)/100;
            double calcDiscount = Double.parseDouble(""+pPrice) * discount;
            double totalPrice = Double.parseDouble(""+pPrice) - calcDiscount;
            binding.totalPrice.setText("$"+(Math.round(totalPrice) * pQty));
            binding.pPriceOff.setText("$"+(pPrice * pQty));
        }
    }
    public void minusQty(){
        if (pQty > 1){
            pQty--;
            binding.pQty.setText(""+pQty);
            double discount = Double.parseDouble(""+pDiscount)/100;
            double calcDiscount = Double.parseDouble(""+pPrice) * discount;
            double totalPrice = Double.parseDouble(""+pPrice) - calcDiscount;
            binding.totalPrice.setText("$"+(Math.round(totalPrice) * pQty));
            binding.pPriceOff.setText("$"+(pPrice * pQty));
        }
    }
    public void addToCart(){
        MainActivity.myRef.child("AddToCart").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int cartCount = 0;
                String cartId = "";
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (userId.equals(ds.child("UID").getValue()) && ds.child("PID").getValue().equals(PID)) {
                        cartCount++;
                        cartId = ds.getKey();
                    }
                }

                if (cartCount > 0) {
                    MainActivity.myRef.child("AddToCart").child(cartId).child("qty").setValue(binding.pQty.getText().toString().trim());
                    Dialog alertdialog = new Dialog(ProductDetailActivity.this);
                    alertdialog.setContentView(R.layout.dialog_success);
                    alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    alertdialog.getWindow().setGravity(Gravity.CENTER);
                    alertdialog.setCancelable(false);
                    alertdialog.setCanceledOnTouchOutside(false);
                    TextView message = alertdialog.findViewById(R.id.message);
                    message.setText("Product Quantity Updated into Cart Successfully");
                    alertdialog.show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            alertdialog.dismiss();
                        }
                    },2000);
                } else {
                    HashMap<String,String> Obj = new HashMap<String, String>();
                    Obj.put("PID",PID);
                    Obj.put("UID",userId);
                    Obj.put("qty",binding.pQty.getText().toString().trim());
                    MainActivity.myRef.child("AddToCart").push().setValue(Obj);
                    Dialog alertdialog = new Dialog(ProductDetailActivity.this);
                    alertdialog.setContentView(R.layout.dialog_success);
                    alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    alertdialog.getWindow().setGravity(Gravity.CENTER);
                    alertdialog.setCancelable(false);
                    alertdialog.setCanceledOnTouchOutside(false);
                    TextView message = alertdialog.findViewById(R.id.message);
                    message.setText("Product Added into Cart Successfully");
                    alertdialog.show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            alertdialog.dismiss();
                        }
                    },2000);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG", "onCancelled: " + error.getMessage());
            }
        });
    }
}