package fr.ilovemycity.android.controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.ilovemycity.android.ILMCApp;
import fr.ilovemycity.android.R;
import fr.ilovemycity.android.events.LaunchCityEvent;
import fr.ilovemycity.android.events.LaunchMapEvent;
import fr.ilovemycity.android.events.LaunchProfileEvent;
import fr.ilovemycity.android.events.LaunchTicketEvent;
import fr.ilovemycity.android.events.OpenGalleryEvent;
import fr.ilovemycity.android.events.RefreshDrawerEvent;
import fr.ilovemycity.android.events.RefreshTicketsEvent;
import fr.ilovemycity.android.utils.ImageUtils;
import fr.ilovemycity.android.utils.ToastUtils;
import fr.ilovemycity.android.utils.components.CustomDrawerToggle;
import fr.ilovemycity.android.views.CityFragment;
import fr.ilovemycity.android.views.MapFragment;
import fr.ilovemycity.android.views.ProfileFragment;
import fr.ilovemycity.android.views.TicketListFragment;
import pl.aprilapps.easyphotopicker.EasyImage;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, DrawerLayout.DrawerListener {

    /**************
     * ATTRIBUTES *
     **************/

    public static final int REQUEST_CAMERA = 98;
    public static final int REQUEST_GALLERY = 99;

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.nav_view) NavigationView mNavigationView;

    private static Fragment currentFragment;
    private static TicketListFragment ticketListFragment = new TicketListFragment();
    private static MapFragment mapFragment = new MapFragment();
    private static CityFragment cityFragment = new CityFragment();
    private static ProfileFragment profileFragment = new ProfileFragment();

    /**
     * Double back click tools
     */
    private boolean doubleBackToExitPressedOnce = false;
    private Toast doubleTapToast;

    // Drawer elements
    private ImageView ivPicture;
    private TextView tvName;
    private TextView tvEmail;

    private int currentId = R.id.nav_tickets;

    /***********
     * METHODS *
     ***********/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        setUpFragments();
        setUpDrawer(mToolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setUpDrawer(Toolbar toolbar) {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        CustomDrawerToggle toggle = new CustomDrawerToggle(this, drawer, toolbar, mNavigationView,
                findViewById(R.id.fragment_container));


        if (drawer != null) {
            drawer.addDrawerListener(toggle);
            drawer.addDrawerListener(this);
        }
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        // Initiate Drawer Header

        View headerView = navigationView != null ? navigationView.inflateHeaderView(R.layout.drawer_header) : null;
        if (headerView != null) {

            ivPicture = (ImageView) headerView.findViewById(R.id.circleView);
            tvName = (TextView) headerView.findViewById(R.id.name);
            tvEmail = (TextView) headerView.findViewById(R.id.email);

            String picture = ILMCApp.getUser().getPicture();
            if (picture != null && !picture.equals(""))
                ImageUtils.fillImageView(ILMCApp.getUser().getPicture(), ivPicture);
            if (!ILMCApp.getUser().getUsername().equals(""))
                tvName.setText(ILMCApp.getUser().getUsername());
            if (!ILMCApp.getUser().getEmail().equals(""))
                tvEmail.setText(ILMCApp.getUser().getEmail());

        }

        RelativeLayout logout = (RelativeLayout) findViewById(R.id.logout);
        if (logout != null) {
            logout.setOnClickListener(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
            File file = new File(images.get(0).getPath());
            onPhotoReturned(file);
        }
    }

    private void onPhotoReturned(File photoFile) {

        if (currentId == R.id.nav_my_profile) {
            try {

                InputStream is = new FileInputStream(photoFile);
                ImageUtils.uploadImage(is, 0, null);
                ImageUtils.fillImageView(photoFile, (ImageView) findViewById(R.id.circleView));
                ImageUtils.fillImageView(photoFile, profileFragment.getProfilePic());

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else if (currentId == R.id.nav_map) {
            mapFragment.setTicketImage(photoFile);
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else if (mapFragment.getContainer().getVisibility() == View.VISIBLE) {
            mapFragment.closeContainer();
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();

                if (doubleTapToast != null)
                    doubleTapToast.cancel();

                return;
            }

            this.doubleBackToExitPressedOnce = true;
            doubleTapToast = ToastUtils.makeToast(this, getString(R.string.double_tap_toast), 2000);

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.creation:
                EventBus.getDefault().post(new RefreshTicketsEvent("createdAt"));
                break;
            case R.id.update:
                EventBus.getDefault().post(new RefreshTicketsEvent("updatedAt"));
                break;
            case R.id.vote:
                EventBus.getDefault().post(new RefreshTicketsEvent("votes"));
                break;
            case R.id.status:
                EventBus.getDefault().post(new RefreshTicketsEvent("status"));
                break;


        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        /**
         * Check if id != currentId to avoid opening twice
         */

        if (currentId != id) {

            if (id == R.id.nav_tickets) {
                EventBus.getDefault().post(new LaunchTicketEvent());
            } else if (id == R.id.nav_map) {
                EventBus.getDefault().post(new LaunchMapEvent());
            } else if (id == R.id.nav_my_city) {
                EventBus.getDefault().post(new LaunchCityEvent());
            } else if (id == R.id.nav_my_profile) {
                EventBus.getDefault().post(new LaunchProfileEvent());
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null)
            drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.logout:
                ILMCApp.deleteUser();

                final Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);

                break;
        }
    }

    private void setUpFragments() {

        /**
         * When the app start, the current fragment is ticketListFragment
         */

        currentFragment = ticketListFragment;

        /**
         * Initialize the FragmentTransaction object
         */

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        /**
         * Add all the fragments to the container
         */

        fragmentTransaction.add(R.id.fragment_container, ticketListFragment);
        fragmentTransaction.add(R.id.fragment_container, mapFragment);
        fragmentTransaction.add(R.id.fragment_container, cityFragment);
        fragmentTransaction.add(R.id.fragment_container, profileFragment);

        /**
         * Initially hide all the fragments except ticketListFragment
         */

        fragmentTransaction.show(ticketListFragment);

        fragmentTransaction.hide(mapFragment);
        fragmentTransaction.hide(profileFragment);
        fragmentTransaction.hide(cityFragment);

        /**
         * Initial commit
         */

        fragmentTransaction.commit();

    }

    /**
     * Call this function to display a fragment
     * @param fragment      => Fragment to displayed
     * @param id            => Fragment id in the nav
     * @param title         => Title in ActionBar
     */
    private void launchFragment(Fragment fragment, int id, String title) {

        currentId = id;

        mNavigationView.setCheckedItem(id);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.hide(currentFragment);

        fragmentTransaction.show(fragment);

        currentFragment = fragment;
        fragmentTransaction.commit();

        mToolbar.setTitle(title);
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**********
     * EVENTS *
     **********/

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLaunchTicketEvent(LaunchTicketEvent event) {
        launchFragment(ticketListFragment, R.id.nav_tickets, getString(R.string.app_name));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLaunchMapEvent(LaunchMapEvent event) {
        mapFragment.reinitialize();
        launchFragment(mapFragment, R.id.nav_map, getString(R.string.menu_map));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLaunchProfileEvent(LaunchProfileEvent event) {
        launchFragment(profileFragment, R.id.nav_my_profile, ILMCApp.getUser().getUsername());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLaunchCityEvent(LaunchCityEvent event) {
        launchFragment(cityFragment, R.id.nav_my_city, "Ma ville");
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onRefreshDrawerEvent(RefreshDrawerEvent event) {
        EventBus.getDefault().removeStickyEvent(event);

        if (!ILMCApp.getUser().getPicture().equals(""))
            ImageUtils.fillImageView(ILMCApp.getUser().getPicture(), ivPicture);
        if (!ILMCApp.getUser().getUsername().equals(""))
            tvName.setText(ILMCApp.getUser().getUsername());
        if (!ILMCApp.getUser().getEmail().equals(""))
            tvEmail.setText(ILMCApp.getUser().getEmail());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOpenGalleryEvent(OpenGalleryEvent event) {
        EasyImage.openGallery(this, 0);
    }


    /*****************
     * DRAWER EVENTS *
     *****************/

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {
        hideKeyboard();
    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }
}
