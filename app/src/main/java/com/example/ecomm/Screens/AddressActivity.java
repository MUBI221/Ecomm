package com.example.ecomm.Screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ecomm.MainActivity;
import com.example.ecomm.R;
import com.example.ecomm.Screens.Models.AddressModel;
import com.example.ecomm.databinding.ActivityAddressBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AddressActivity extends AppCompatActivity {

    ActivityAddressBinding binding;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String userId;
    ArrayList<AddressModel> datalist = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferences = getSharedPreferences("myData", MODE_PRIVATE);
        editor = preferences.edit();
        userId = preferences.getString("userId",null);
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddressActivity.super.onBackPressed();
            }
        });
        binding.btnNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddressActivity.this,AddNewAddressActivity.class);
                intent.putExtra("edit","false");
                intent.putExtra("addressId","");
                startActivity(intent);
            }
        });
        MainActivity.myRef.child("Address").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                datalist.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    if(userId.equals(ds.child("UID").getValue().toString().trim())){
                        AddressModel model = new AddressModel(
                                ds.getKey(),
                                ds.child("UID").getValue().toString().trim(),
                                ds.child("name").getValue().toString().trim(),
                                ds.child("address").getValue().toString().trim(),
                                ds.child("defaultStatus").getValue().toString().trim()
                        );
                        datalist.add(model);
                    }
                }
                MyAdapter adapter = new MyAdapter(AddressActivity.this,datalist);
                binding.listview.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public class MyAdapter extends BaseAdapter{

        Context context;
        ArrayList<AddressModel> data;

        public MyAdapter(Context context, ArrayList<AddressModel> data) {
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
            View itemView = LayoutInflater.from(context).inflate(R.layout.address_listview,null);
            TextView addressName, defaultStatus, addressDetail;
            ImageView editBtn;
            addressName = itemView.findViewById(R.id.addressName);
            defaultStatus = itemView.findViewById(R.id.defaultStatus);
            addressDetail = itemView.findViewById(R.id.addressDetail);
            editBtn = itemView.findViewById(R.id.editBtn);
            if(data.get(i).getDefaultStatus().equals("true")){
                defaultStatus.setVisibility(View.VISIBLE);
            }
            addressName.setText(data.get(i).getName());
            addressDetail.setText(data.get(i).getAddress());
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,AddNewAddressActivity.class);
                    intent.putExtra("edit","true");
                    intent.putExtra("addressId",""+data.get(i).getId());
                    startActivity(intent);
                }
            });
            return itemView;
        }
    }
}