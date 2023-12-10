package com.example.ecomm.Screens.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ecomm.R;
import com.example.ecomm.databinding.FragmentOrderActiveBinding;

public class OrderActiveFragment extends Fragment {

    FragmentOrderActiveBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOrderActiveBinding.inflate(inflater,container,false);

        return binding.getRoot();
    }
}