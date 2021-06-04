package com.example.myapplication.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.myapplication.utils.FragmentCreator;

public class MainContextAdapter extends FragmentPagerAdapter {
    public MainContextAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        return FragmentCreator.getFragment(position);
    }

    @Override
    public int getCount() {
        return FragmentCreator.PAGE_COUNT;
    }
}
