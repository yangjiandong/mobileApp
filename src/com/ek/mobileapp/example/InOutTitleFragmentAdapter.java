package com.ek.mobileapp.example;

import android.support.v4.app.FragmentManager;

class InOutTitleFragmentAdapter extends InOutFragmentAdapter {
    public InOutTitleFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return InOutFragmentAdapter.CONTENT[position % CONTENT.length];
    }
}