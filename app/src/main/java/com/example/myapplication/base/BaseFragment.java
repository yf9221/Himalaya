package com.example.myapplication.base;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public abstract class BaseFragment extends Fragment {
    private View mrootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mrootView = OnSubViewLoaded(inflater,container);
        return mrootView;
    }

    protected abstract View OnSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) ;
}
