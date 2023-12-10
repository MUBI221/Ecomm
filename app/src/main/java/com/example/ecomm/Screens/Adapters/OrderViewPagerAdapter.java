package com.example.ecomm.Screens.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.ecomm.Screens.Fragments.AccountFragment;
import com.example.ecomm.Screens.Fragments.HomeFragment;
import com.example.ecomm.Screens.Fragments.OrderActiveFragment;
import com.example.ecomm.Screens.Fragments.OrderCompletedFragment;

public class OrderViewPagerAdapter extends FragmentStateAdapter {


    public OrderViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new OrderActiveFragment();
            case 1:
                return new OrderCompletedFragment();
            default:
                return new OrderActiveFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
