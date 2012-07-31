package com.ek.mobileapp.example;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class InOutFragmentAdapter extends FragmentPagerAdapter {
    protected static final String[] CONTENT = new String[] { "住院", "门诊", };

    private int mCount = CONTENT.length;

    public InOutFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0){
            return InFragment.newInstance("住院");
        }else{
            return OutFragment.newInstance("门诊");
        }
        //return TestFragment.newInstance(CONTENT[position % CONTENT.length]);
    }

    @Override
    public int getCount() {
        return mCount;
    }

    public void setCount(int count) {
        if (count > 0 && count <= 10) {
            mCount = count;
            notifyDataSetChanged();
        }
    }
}