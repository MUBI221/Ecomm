package com.example.ecomm.Screens.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ecomm.R;
import com.example.ecomm.databinding.FragmentOrderCompletedBinding;
import com.example.ecomm.databinding.FragmentOrdersBinding;

public class OrderCompletedFragment extends Fragment {

    FragmentOrderCompletedBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOrderCompletedBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }
}