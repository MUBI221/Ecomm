package com.example.ecomm.Screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecomm.MainActivity;
import com.example.ecomm.R;
import com.example.ecomm.Screens.Admin.AdminDashboardActivity;
import com.example.ecomm.Screens.Fragments.AccountFragment;
import com.example.ecomm.Screens.Fragments.CartFragment;
import com.example.ecomm.Screens.Fragments.HomeFragment;
import com.example.ecomm.Screens.Fragments.OrdersFragment;
import com.example.ecomm.Screens.Fragments.WishlistFragment;
import com.example.ecomm.databinding.ActivityDashboardBinding;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardActivity extends AppCompatActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String userId;
    ActivityDashboardBinding binding;
    public static BadgeDrawable badgeCart, badgeWishlist;
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        preferences = getSharedPreferences("myData",MODE_PRIVATE);
        editor = preferences.edit();

        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new HomeFragment());
        badgeCart = binding.bottomAppBar.getOrCreateBadge(R.id.cart);
        badgeCart.setVisible(false);
        badgeCart.setBackgroundColor(getResources().getColor(R.color.myThemeLight));

        badgeWishlist = binding.bottomAppBar.getOrCreateBadge(R.id.wishlist);
        badgeWishlist.setVisible(false);
        badgeWishlist.setBackgroundColor(getResources().getColor(R.color.myThemeLight));

        binding.bottomAppBar.setOnItemSelectedListener(item -> {
            switch (item.getTitle().toString()){
                case "Home":
                    replaceFragment(new HomeFragment());
                    break;
                case "Cart":
                    replaceFragment(new CartFragment());
                    break;
                case "Orders":
                    replaceFragment(new OrdersFragment());
                    break;
                case "Wishlist":
                    replaceFragment(new WishlistFragment());
                    break;
                case "Account":
                    replaceFragment(new AccountFragment());
                    break;
            }
            return true;
        });

        userId = preferences.getString("userId",null);
        MainActivity.myRef.child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String roleCheck = snapshot.child("role").getValue().toString().trim();
                    if(roleCheck.equals("admin")){
//                        startActivity(new Intent(DashboardActivity.this, AdminDashboardActivity.class));
//                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        MainActivity.myRef.child("Wishlist").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int favoriteCount = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (userId.equals(ds.child("UID").getValue())) {
                        favoriteCount++;
                    }
                }
                updateWishlistCount(favoriteCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG", "onCancelled: " + error.getMessage());
            }
        });
        MainActivity.myRef.child("AddToCart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int cartCount = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (userId.equals(ds.child("UID").getValue())) {
                        cartCount++;
                    }
                }
                updateCartCount(cartCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG", "onCancelled: " + error.getMessage());
            }
        });
    }
    public void replaceFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.frame,fragment).commit();
    }

    public static void updateCartCount(int number){
        if (number > 0) {
            badgeCart.setVisible(true);
            badgeCart.setNumber(number);
        } else {
            badgeCart.setVisible(false);
        }
    }
    public static void updateWishlistCount(int number){
        if (number > 0) {
            badgeWishlist.setVisible(true);
            badgeWishlist.setNumber(number);
        } else {
            badgeWishlist.setVisible(false);
        }
    }
}