package com.example.ecomm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ecomm.Screens.Fragments.CartFragment;
import com.example.ecomm.Screens.SelectAddressActivity;
import com.example.ecomm.databinding.ActivityCheckoutBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class CheckoutActivity extends AppCompatActivity {

    ActivityCheckoutBinding binding;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String userId;
    int grandTotal = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferences = getSharedPreferences("myData", MODE_PRIVATE);
        editor = preferences.edit();
        userId = preferences.getString("userId",null);
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckoutActivity.super.onBackPressed();
            }
        });

        binding.locationEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CheckoutActivity.this, SelectAddressActivity.class));
            }
        });
        MainActivity.myRef.child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String AddressIdFromUser = snapshot.child("address").getValue().toString().trim();
                if(snapshot.child("shipping").getValue().toString().trim().equals("")){
                    binding.chooseShippingBtn.setVisibility(View.VISIBLE);
                    binding.shippingContainer.setVisibility(View.GONE);
                } else {
                    binding.chooseShippingBtn.setVisibility(View.GONE);
                    binding.shippingContainer.setVisibility(View.VISIBLE);
                }
                if(AddressIdFromUser.equals("")){
                    MainActivity.myRef.child("Address").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int addressCount = 0;
                            for(DataSnapshot ds: snapshot.getChildren()){
                                if(ds.child("UID").getValue().toString().trim().equals(userId)){
                                    if(addressCount == 0){
                                        binding.locationName.setText(ds.child("name").getValue().toString().trim());
                                        binding.locationAddress.setText(ds.child("address").getValue().toString().trim());
                                    }
                                    addressCount++;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    MainActivity.myRef.child("Address").child(AddressIdFromUser).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                            binding.locationName.setText(datasnapshot.child("name").getValue().toString().trim());
                            binding.locationAddress.setText(datasnapshot.child("address").getValue().toString().trim());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        MainActivity.myRef.child("AddToCart").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                setGrandTotal(0,false);
                for(DataSnapshot ds: snapshot.getChildren()){
                    if(userId.equals(ds.child("UID").getValue().toString().trim())){
                        MainActivity.myRef.child("Products").child(ds.child("PID").getValue().toString().trim()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                                if(datasnapshot.exists()){
                                    int pQty = Integer.parseInt(ds.child("qty").getValue().toString().trim());
                                    int pPrice = Integer.parseInt(datasnapshot.child("pPrice").getValue().toString().trim());
                                    int pStock = Integer.parseInt(datasnapshot.child("pStock").getValue().toString().trim());
                                    int pDiscount = Integer.parseInt(datasnapshot.child("pDiscount").getValue().toString().trim());

                                    double discount = Double.parseDouble(datasnapshot.child("pDiscount").getValue().toString())/100;
                                    double calcDiscount = Double.parseDouble(datasnapshot.child("pPrice").getValue().toString().trim()) * discount;
                                    double totalPrice = Double.parseDouble(datasnapshot.child("pPrice").getValue().toString().trim()) - calcDiscount;

                                    View itemView = getLayoutInflater().inflate(R.layout.product_checkout_listview,null);
                                    ImageView pImage;
                                    TextView pName, pQtyTextView, pPriceTextView, pPriceOffTextView, pDiscountTextView;
                                    pImage = itemView.findViewById(R.id.pImage);
                                    pName = itemView.findViewById(R.id.pName);
                                    pPriceTextView = itemView.findViewById(R.id.pPrice);
                                    pPriceOffTextView = itemView.findViewById(R.id.pPriceOff);
                                    pQtyTextView = itemView.findViewById(R.id.pQty);
                                    pDiscountTextView = itemView.findViewById(R.id.pDiscount);

                                    pQtyTextView.setText(""+pQty);
                                    Glide.with(CheckoutActivity.this).load(datasnapshot.child("pImage").getValue().toString().trim()).into(pImage);
                                    pName.setText(datasnapshot.child("pName").getValue().toString().trim());
                                    pPriceOffTextView.setText("$"+(pPrice*pQty));
                                    if(pDiscount > 0){
                                        pPriceOffTextView.setVisibility(View.VISIBLE);
                                        pDiscountTextView.setVisibility(View.VISIBLE);
                                        pDiscountTextView.setText(pDiscount+"% OFF");
                                    }
                                    pPriceTextView.setText("$"+(Math.round(totalPrice)*pQty));
                                    setGrandTotal((int) Math.round(totalPrice)*pQty,true);
                                    binding.orderList.addView(itemView);
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

            }
        });
    }
    public void setGrandTotal(int itemPrice,boolean status){
        if(status == true){
            grandTotal += itemPrice;
            binding.totalAmount.setText("$"+grandTotal);
        } else {
            grandTotal = 0;
        }
    }
}