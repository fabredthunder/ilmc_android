package fr.ilovemycity.android.utils.components;

import android.app.Activity;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.TranslateAnimation;

import fr.ilovemycity.android.R;

/**
 * Created by Fab on 03/08/2016.
 * All rights reserved
 */
public class CustomDrawerToggle extends ActionBarDrawerToggle {

    /**************
     * ATTRIBUTES *
     **************/

    protected float lastTranslate = 0.0f;

    private NavigationView mNavigationView;
    private View mFrame;

    /***********
     * METHODS *
     ***********/

    public CustomDrawerToggle(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar, NavigationView navigationView, View frame) {
        super(activity, drawerLayout, toolbar, R.string.open, R.string.close);

        this.mNavigationView = navigationView;
        this.mFrame = frame;

        //setDrawerIndicatorEnabled(false);
    }

    public void onDrawerSlide(View drawerView, float slideOffset) {

        float moveFactor = 0;
        if (mNavigationView != null) {
            moveFactor = (mNavigationView.getWidth() * slideOffset);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mFrame.setTranslationX(moveFactor);
        } else {
            TranslateAnimation anim = new TranslateAnimation(lastTranslate, moveFactor, 0.0f, 0.0f);
            anim.setDuration(0);
            anim.setFillAfter(true);
            mFrame.startAnimation(anim);
        }
    }

}
