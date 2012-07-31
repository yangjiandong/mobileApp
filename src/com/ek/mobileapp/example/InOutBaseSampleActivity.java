package com.ek.mobileapp.example;

import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.viewpagerindicator.PageIndicator;

public abstract class InOutBaseSampleActivity extends FragmentActivity {

    InOutFragmentAdapter mAdapter;
    ViewPager mPager;
    PageIndicator mIndicator;
}
