package com.example.ecomm.Screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ecomm.MainActivity;
import com.example.ecomm.R;
import com.example.ecomm.Screens.Models.ProductModel;
import com.example.ecomm.Screens.Models.WishlistModel;
import com.example.ecomm.databinding.ActivitySearchBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class SearchActivity extends AppCompatActivity {

    ActivitySearchBinding binding;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String userId = "";
    ArrayList<ProductModel> datalist = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferences = getSharedPreferences("myData", MODE_PRIVATE);
        editor = preferences.edit();
        userId = preferences.getString("userId",null);
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchActivity.super.onBackPressed();
            }
        });
        binding.clearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.searchEditText.setText(null);
                search();
            }
        });
        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.searchedWord.setText(binding.searchEditText.getText().toString().trim());
                if(binding.searchEditText.getText().toString().length() > 0){
                    binding.clearText.setVisibility(View.VISIBLE);
                } else {
                    binding.clearText.setVisibility(View.GONE);
                }
                search();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        search();
    }
    public void search(){
        String input = binding.searchEditText.getText().toString().trim();
        if(input.equals("")){
            fetchData("");
            binding.notifyBar.setVisibility(View.GONE);
        } else {
            fetchData(input);
            binding.notifyBar.setVisibility(View.VISIBLE);
        }
    }

    public void fetchData(String data){
        MainActivity.myRef.child("Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    datalist.clear();
                    for(DataSnapshot ds : snapshot.getChildren()){
                        if(data.equals("")){
                            ProductModel model = new ProductModel(
                                    ds.getKey(),
                                    ds.child("pName").getValue().toString().trim(),
                                    ds.child("pPrice").getValue().toString().trim(),
                                    ds.child("pStock").getValue().toString().trim(),
                                    ds.child("pDiscount").getValue().toString().trim(),
                                    ds.child("pImage").getValue().toString().trim(),
                                    ds.child("pDesc").getValue().toString().trim(),
                                    ds.child("status").getValue().toString().trim()
                            );
                            datalist.add(model);
                        } else {
                            if(ds.child("pName").getValue().toString().trim().toLowerCase().contains(data.toLowerCase())){
                                ProductModel model = new ProductModel(
                                        ds.getKey(),
                                        ds.child("pName").getValue().toString().trim(),
                                        ds.child("pPrice").getValue().toString().trim(),
                                        ds.child("pStock").getValue().toString().trim(),
                                        ds.child("pDiscount").getValue().toString().trim(),
                                        ds.child("pImage").getValue().toString().trim(),
                                        ds.child("pDesc").getValue().toString().trim(),
                                        ds.child("status").getValue().toString().trim()
                                );
                                datalist.add(model);
                            }
                        }

                    }
                    if(datalist.size() > 0){
                        binding.gridView.setVisibility(View.VISIBLE);
                        binding.searchContainer.setVisibility(View.GONE);
                        binding.notfoundContainer.setVisibility(View.GONE);
                        Collections.reverse(datalist);
                        MyAdapter adapter = new MyAdapter(SearchActivity.this,datalist);
                        binding.gridView.setAdapter(adapter);
                    } else {
                        binding.gridView.setVisibility(View.GONE);
                        if(data.equals("")){
                            binding.searchContainer.setVisibility(View.VISIBLE);
                        } else {
                            binding.notfoundContainer.setVisibility(View.VISIBLE);
                        }
                    }
                    binding.totalCount.setText(datalist.size()+" found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public class MyAdapter extends BaseAdapter{

        Context context;
        ArrayList<ProductModel> data;

        boolean foundInFvrt = false;

        public MyAdapter(Context context, ArrayList<ProductModel> data) {
            this.context = context;
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View productItem = LayoutInflater.from(context).inflate(R.layout.product_listview,null);
            ImageView pImage, wishlistBtn;
            TextView pDiscount, pName, pRating, pStock, pPrice, pPriceOff;
            LinearLayout options, item;
            pImage = productItem.findViewById(R.id.pImage);
            wishlistBtn = productItem.findViewById(R.id.wishlistBtn);
            pDiscount = productItem.findViewById(R.id.pDiscount);
            pName = productItem.findViewById(R.id.pName);
            pRating = productItem.findViewById(R.id.pRating);
            pStock = productItem.findViewById(R.id.pStock);
            pPrice = productItem.findViewById(R.id.pPrice);
            pPriceOff = productItem.findViewById(R.id.pPriceOff);
            options = productItem.findViewById(R.id.options);
            item = productItem.findViewById(R.id.item);

            if(!data.get(i).getpDiscount().equals("0")){
                pDiscount.setVisibility(View.VISIBLE);
                pDiscount.setText(data.get(i).getpDiscount()+"% OFF");
            } else {
                pPriceOff.setVisibility(View.GONE);
            }
            pName.setText(data.get(i).getpName());
            pStock.setText(data.get(i).getpStock()+" Stock");
            pPriceOff.setText("$"+data.get(i).getpPrice());
            Glide.with(context).load(data.get(i).getpImage()).into(pImage);

            double discount = Double.parseDouble(data.get(i).getpDiscount())/100;
            double calcDiscount = Double.parseDouble(data.get(i).getpPrice()) * discount;
            double totalPrice = Double.parseDouble(data.get(i).getpPrice()) - calcDiscount;
            pPrice.setText("$"+Math.round(totalPrice));

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ProductDetailActivity.class);
                    intent.putExtra("pid",data.get(i).getId());
                    startActivity(intent);
                }
            });

            if (userId != null && !userId.equals("")) {
                try {
                    MainActivity.myRef.child("Wishlist").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                int favoriteCount = 0;
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    WishlistModel model = ds.getValue(WishlistModel.class);
                                    if(data.size()>0){
                                        if (model.getUID().equals(userId) && model.getPID().equals(data.get(i).getId())) {
                                            favoriteCount++;
                                        }
                                    }
                                }
                                wishlistBtn.setImageResource(R.drawable.heart_outlined);
                                if (favoriteCount > 0) {
                                    wishlistBtn.setImageResource(R.drawable.heart_gradient);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("TAG", "onCancelled: " + error.getMessage());
                        }
                    });

                } catch (Exception e){

                }
            }

            wishlistBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity.myRef.child("Wishlist").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    if (userId.equals(ds.child("UID").getValue()) && ds.child("PID").getValue().equals(data.get(i).getId())) {
                                        String fvrtItemId = ds.getKey();
                                        foundInFvrt = true;
                                        MainActivity.myRef.child("Wishlist").child(fvrtItemId).removeValue();
                                        wishlistBtn.setImageResource(R.drawable.heart_outlined);
                                        Dialog alertdialog = new Dialog(context);
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

                                        break;
                                    }
                                }
                            }

                            if (!foundInFvrt) {
                                HashMap<String, String> Obj = new HashMap<String,String>();
                                Obj.put("UID",userId);
                                Obj.put("PID",data.get(i).getId());
                                MainActivity.myRef.child("Wishlist").push().setValue(Obj);
                                wishlistBtn.setImageResource(R.drawable.heart_gradient);
                                Dialog alertdialog = new Dialog(context);
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
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("TAG", "onCancelled: " + error.getMessage());
                        }
                    });
                }
            });
            return productItem;
        }
    }
}