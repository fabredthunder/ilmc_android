package fr.ilovemycity.android.common;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * @author Cyril Leroux
 *         Created 16/12/2014.
 */
public class TextAdapter extends FragmentPagerAdapter {

    private final List<TextFragment> mFragments;
    private final List<String> mTtitles;

    public TextAdapter(FragmentManager fm, List<TextFragment> fragments, List<String> titles) {
        super(fm);
        mFragments = fragments;
        mTtitles = titles;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTtitles.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    public int getItemPosition(Object object){
        return POSITION_NONE;
    }
}