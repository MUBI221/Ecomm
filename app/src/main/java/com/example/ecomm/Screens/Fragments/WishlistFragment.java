package com.example.ecomm.Screens.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ecomm.MainActivity;
import com.example.ecomm.R;
import com.example.ecomm.Screens.Models.WishlistModel;
import com.example.ecomm.Screens.ProductDetailActivity;
import com.example.ecomm.Screens.SearchActivity;
import com.example.ecomm.databinding.FragmentCartBinding;
import com.example.ecomm.databinding.FragmentWishlistBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class WishlistFragment extends Fragment {

    FragmentWishlistBinding binding;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String userId;

    ArrayList<WishlistModel> datalist = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWishlistBinding.inflate(inflater, container, false);
        preferences = container.getContext().getSharedPreferences("myData", MODE_PRIVATE);
        editor = preferences.edit();
        userId = preferences.getString("userId",null);

        binding.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(container.getContext(), SearchActivity.class));
            }
        });
        MainActivity.myRef.child("Wishlist").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                datalist.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (userId.equals(ds.child("UID").getValue())) {
                        WishlistModel model = new WishlistModel(ds.getKey(), ds.child("UID").getValue().toString(),ds.child("PID").getValue().toString());
                        datalist.add(model);
                    }
                }

                if(datalist.size() > 0){
                    binding.gridView.setVisibility(View.VISIBLE);
                    binding.notfoundContainer.setVisibility(View.GONE);
                    Collections.reverse(datalist);
                    MyAdapter adapter = new MyAdapter(container.getContext(),datalist);
                    binding.gridView.setAdapter(adapter);
                } else {
                    binding.gridView.setVisibility(View.GONE);
                    binding.notfoundContainer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG", "onCancelled: " + error.getMessage());
            }
        });
        return binding.getRoot();
    }

    public class MyAdapter extends BaseAdapter{

        Context context;
        ArrayList<WishlistModel> data;

        boolean foundInFvrt = false;

        public MyAdapter(Context context, ArrayList<WishlistModel> data) {
            this.context = context;
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
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

            MainActivity.myRef.child("Products").child(data.get(i).getPID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        if(!snapshot.child("pDiscount").getValue().toString().equals("0")){
                            pDiscount.setVisibility(View.VISIBLE);
                            pDiscount.setText(snapshot.child("pDiscount").getValue().toString()+"% OFF");
                        } else {
                            pPriceOff.setVisibility(View.GONE);
                        }
                        pName.setText(snapshot.child("pName").getValue().toString());
                        pStock.setText(snapshot.child("pStock").getValue().toString()+" Stock");
                        pPriceOff.setText("$"+snapshot.child("pPrice").getValue().toString());
                        Glide.with(context).load(snapshot.child("pImage").getValue().toString()).into(pImage);

                        double discount = Double.parseDouble(snapshot.child("pDiscount").getValue().toString())/100;
                        double calcDiscount = Double.parseDouble(snapshot.child("pPrice").getValue().toString()) * discount;
                        double totalPrice = Double.parseDouble(snapshot.child("pPrice").getValue().toString()) - calcDiscount;
                        pPrice.setText("$"+Math.round(totalPrice));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ProductDetailActivity.class);
                    intent.putExtra("pid",data.get(i).getPID());
                    startActivity(intent);
                }
            });

            wishlistBtn.setImageResource(R.drawable.heart_gradient);
            wishlistBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity.myRef.child("Wishlist").child(data.get(i).getId()).removeValue();
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
                }
            });
            Animation anim = AnimationUtils.loadAnimation(context,R.anim.fadein);
            productItem.startAnimation(anim);
            anim.setStartOffset(i*20);
            return productItem;
        }
    }
}