package com.example.ecomm.Screens.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ecomm.R;
import com.example.ecomm.Screens.Adapters.OrderViewPagerAdapter;
import com.example.ecomm.databinding.FragmentOrdersBinding;
import com.google.android.material.tabs.TabLayout;

public class OrdersFragment extends Fragment {

    FragmentOrdersBinding binding;
    OrderViewPagerAdapter orderViewPagerAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentOrdersBinding.inflate(inflater, container, false);
        orderViewPagerAdapter = new OrderViewPagerAdapter(this);
        binding.viewPager.setAdapter(orderViewPagerAdapter);
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.tabLayout.getTabAt(position).select();
            }
        });
        return binding.getRoot();
    }
}