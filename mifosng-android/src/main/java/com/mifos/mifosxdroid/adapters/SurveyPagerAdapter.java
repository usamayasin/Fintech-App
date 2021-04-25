/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by Nasim Banu on 28,January,2016.
 */
public class SurveyPagerAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> fragments;
    private int currentPage = 0;

    public SurveyPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }


    public Fragment getItem(int position) {
        return this.fragments.get(position);
    }

    public int getCount() {
        return this.fragments.size();
    }

    public int getCurrentPage() {
        return currentPage;
    }


    public void setCurrentPage(int position) {
        currentPage = position;
    }
}

