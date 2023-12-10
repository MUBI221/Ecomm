package com.example.ecomm.Screens.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ecomm.CheckoutActivity;
import com.example.ecomm.MainActivity;
import com.example.ecomm.R;
import com.example.ecomm.Screens.DashboardActivity;
import com.example.ecomm.Screens.LoginActivity;
import com.example.ecomm.Screens.Models.AddToCartModel;
import com.example.ecomm.Screens.ProductsActivity;
import com.example.ecomm.Screens.SearchActivity;
import com.example.ecomm.databinding.FragmentCartBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.zip.CheckedInputStream;

public class CartFragment extends Fragment {

    FragmentCartBinding binding;
    ArrayList<AddToCartModel> datalist = new ArrayList<>();
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String userId;
    int grandTotal = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentCartBinding.inflate(inflater, container, false);
        preferences = container.getContext().getSharedPreferences("myData", MODE_PRIVATE);
        editor = preferences.edit();
        userId = preferences.getString("userId",null);
//        DashboardActivity.updateCartCount(20);
        binding.btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(container.getContext(), CheckoutActivity.class));
            }
        });
        binding.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(container.getContext(), SearchActivity.class));
            }
        });

        fetchCartData();

        MainActivity.myRef.child("AddToCart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                setGrandTotal(0,false);
                for(DataSnapshot ds: snapshot.getChildren()){
                    if(userId.equals(ds.child("UID").getValue().toString().trim())){
                        MainActivity.myRef.child("Products").child(ds.child("PID").getValue().toString().trim()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                                if(datasnapshot.exists()){
                                    double discount = Double.parseDouble(datasnapshot.child("pDiscount").getValue().toString())/100;
                                    double calcDiscount = Double.parseDouble(datasnapshot.child("pPrice").getValue().toString().trim()) * discount;
                                    double totalPrice = Double.parseDouble(datasnapshot.child("pPrice").getValue().toString().trim()) - calcDiscount;
                                    int total = ((int) Math.round(totalPrice)*Integer.parseInt(ds.child("qty").getValue().toString().trim()));
                                    setGrandTotal(total,true);
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

        return binding.getRoot();
    }

    public void fetchCartData(){
        MainActivity.myRef.child("AddToCart").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                datalist.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    if(userId.equals(ds.child("UID").getValue().toString().trim())){
                        AddToCartModel model = new AddToCartModel(
                                ds.getKey(),
                                ds.child("PID").getValue().toString().trim(),
                                ds.child("UID").getValue().toString().trim(),
                                ds.child("qty").getValue().toString().trim()
                        );
                        datalist.add(model);
                    }
                }
//                Collections.reverse(datalist);
                if(datalist.size() > 0){
                    MyAdapter adapter = new MyAdapter(getContext(),datalist);
                    binding.listView.setAdapter(adapter);
                    binding.notfoundContainer.setVisibility(View.GONE);
                    binding.cartContainer.setVisibility(View.VISIBLE);
                } else {
                    binding.notfoundContainer.setVisibility(View.VISIBLE);
                    binding.cartContainer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void GrandTotal(int itemPrice, TextView totalTextView){
        totalTextView.setText("$"+itemPrice);
    }

    public void setGrandTotal(int itemPrice,boolean status){
        if(status == true){
            grandTotal += itemPrice;
            CartFragment.GrandTotal(grandTotal,binding.totalPrice);
        } else {
            grandTotal = 0;
        }
    }

    public class MyAdapter extends BaseAdapter{

        Context context;
        ArrayList<AddToCartModel> data;

        public MyAdapter(Context context, ArrayList<AddToCartModel> data) {
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
            View itemView = LayoutInflater.from(context).inflate(R.layout.product_cart_listview,null);
            ImageView pImage, qtyMinus, qtyAdd, delete;
            TextView pName, pQtyTextView, pPriceTextView, pPriceOffTextView, pDiscountTextView;
            pImage = itemView.findViewById(R.id.pImage);
            pName = itemView.findViewById(R.id.pName);
            pPriceTextView = itemView.findViewById(R.id.pPrice);
            pPriceOffTextView = itemView.findViewById(R.id.pPriceOff);
            pQtyTextView = itemView.findViewById(R.id.pQty);
            qtyMinus = itemView.findViewById(R.id.qtyMinus);
            qtyAdd = itemView.findViewById(R.id.qtyAdd);
            delete = itemView.findViewById(R.id.delete);
            pDiscountTextView = itemView.findViewById(R.id.pDiscount);


            MainActivity.myRef.child("Products").child(data.get(i).getPID()).addListenerForSingleValueEvent(new ValueEventListener() {
                int pPrice = 0, pStock = 0, pQty = 0, pDiscount = 0;
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){

                        pQty = Integer.parseInt(data.get(i).getQty());
                        pPrice = Integer.parseInt(snapshot.child("pPrice").getValue().toString().trim());
                        pStock = Integer.parseInt(snapshot.child("pStock").getValue().toString().trim());
                        pDiscount = Integer.parseInt(snapshot.child("pDiscount").getValue().toString().trim());


                        pQtyTextView.setText(""+pQty);
                        Glide.with(context).load(snapshot.child("pImage").getValue().toString().trim()).into(pImage);
                        pName.setText(snapshot.child("pName").getValue().toString().trim());
                        pPriceOffTextView.setText("$"+(pPrice*pQty));
                        if(pDiscount > 0){
                            pPriceOffTextView.setVisibility(View.VISIBLE);
                            pDiscountTextView.setVisibility(View.VISIBLE);
                            pDiscountTextView.setText(pDiscount+"% OFF");
                        }
                        double discount = Double.parseDouble(snapshot.child("pDiscount").getValue().toString())/100;
                        double calcDiscount = Double.parseDouble(snapshot.child("pPrice").getValue().toString().trim()) * discount;
                        double totalPrice = Double.parseDouble(snapshot.child("pPrice").getValue().toString().trim()) - calcDiscount;
                        pPriceTextView.setText("$"+(Math.round(totalPrice)*pQty));

                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Dialog loaddialog = new Dialog(context);
                                loaddialog.setContentView(R.layout.bottom_cart_delete);
                                loaddialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                loaddialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                loaddialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationBottom;
                                loaddialog.getWindow().setGravity(Gravity.BOTTOM);
                                loaddialog.setCancelable(false);
                                loaddialog.setCanceledOnTouchOutside(false);
                                Button cancelBtn, yesBtn;
                                TextView pQtyDialog, pNameDialog, pPriceDialog, pPriceOffDialog, pDiscountDialog;
                                ImageView pImageDialog;
                                cancelBtn = loaddialog.findViewById(R.id.cancelBtn);
                                yesBtn = loaddialog.findViewById(R.id.yesBtn);
                                pQtyDialog = loaddialog.findViewById(R.id.pQty);
                                pImageDialog = loaddialog.findViewById(R.id.pImage);
                                pNameDialog = loaddialog.findViewById(R.id.pName);
                                pPriceDialog = loaddialog.findViewById(R.id.pPrice);
                                pPriceOffDialog = loaddialog.findViewById(R.id.pPriceOff);
                                pDiscountDialog = loaddialog.findViewById(R.id.pDiscount);
                                if(pDiscount > 0){
                                    pPriceOffDialog.setVisibility(View.VISIBLE);
                                    pDiscountDialog.setVisibility(View.VISIBLE);
                                    pDiscountDialog.setText(pDiscount+"% OFF");
                                }
                                pQtyDialog.setText(""+pQty);
                                pNameDialog.setText(snapshot.child("pName").getValue().toString().trim());
                                pPriceDialog.setText("$"+(Math.round(totalPrice)*pQty));
                                pPriceOffDialog.setText("$"+(pPrice*pQty));
                                Glide.with(context).load(snapshot.child("pImage").getValue().toString().trim()).into(pImageDialog);
                                yesBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        MainActivity.myRef.child("AddToCart").child(data.get(i).getId()).removeValue();
                                        fetchCartData();
                                        Dialog alertdialog = new Dialog(context);
                                        alertdialog.setContentView(R.layout.dialog_success);
                                        alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                        alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        alertdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                                        alertdialog.getWindow().setGravity(Gravity.CENTER);
                                        alertdialog.setCancelable(false);
                                        alertdialog.setCanceledOnTouchOutside(false);
                                        TextView message = alertdialog.findViewById(R.id.message);
                                        message.setText("Product Remove Successfully From Cart!!!");
                                        alertdialog.show();
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                alertdialog.dismiss();
                                                loaddialog.dismiss();
                                            }
                                        },2000);
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

                        qtyAdd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(pQty < pStock ){
                                    pQty++;
                                    pQtyTextView.setText(""+pQty);
                                    MainActivity.myRef.child("AddToCart").child(data.get(i).getId()).child("qty").setValue(""+pQty);
                                    double discount = Double.parseDouble(""+pDiscount)/100;
                                    double calcDiscount = Double.parseDouble(""+pPrice) * discount;
                                    double totalPrice = Double.parseDouble(""+pPrice) - calcDiscount;
                                    pPriceTextView.setText("$"+(Math.round(totalPrice) * pQty));
                                    pPriceOffTextView.setText("$"+(pPrice * pQty));
                                }
                            }
                        });
                        qtyMinus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (pQty > 1){
                                    pQty--;
                                    pQtyTextView.setText(""+pQty);
                                    MainActivity.myRef.child("AddToCart").child(data.get(i).getId()).child("qty").setValue(""+pQty);
                                    double discount = Double.parseDouble(""+pDiscount)/100;
                                    double calcDiscount = Double.parseDouble(""+pPrice) * discount;
                                    double totalPrice = Double.parseDouble(""+pPrice) - calcDiscount;
                                    pPriceTextView.setText("$"+(Math.round(totalPrice) * pQty));
                                    pPriceOffTextView.setText("$"+(pPrice * pQty));
                                }
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            Animation anim = AnimationUtils.loadAnimation(context,R.anim.fadein);
            itemView.startAnimation(anim);
//            anim.setStartOffset(i*20);

            return itemView;
        }

    }
}