package com.mystartup.bezanberimapp;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class HealthViewPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;



    public HealthViewPagerAdapter(FragmentManager manager, Context context) {
        super(manager);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {

            case 0:
                return new DayFragment();
            case 1:
                return new WeekFragment();
            case 2:
                return new MonthFragment();
           default:
                return new YearFragment();

        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {

            case 0:
               return "day";
            case 1:
                return "Week";
            case 2:
                return "Month";
            case 3:
                return "Year";

                default:
                    return null;

        }
    }

}
