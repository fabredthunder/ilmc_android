package fr.ilovemycity.android;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.cloudinary.Cloudinary;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import fr.ilovemycity.android.models.User;
import fr.ilovemycity.android.utils.dao.UserDAO;
import fr.ilovemycity.android.webservice.IlmcAPI;
import retrofit.RestAdapter;

/**
 * Created by Fab on 25/02/2016.
 * All rights reserved
 */
public class ILMCApp extends Application {

    private static ILMCApp instance;

    private static User        mUser;
    private static IlmcAPI     mAPI;
    private static Cloudinary  mCloudinary;

    // Facebook access token
    private AccessToken mAccessToken;

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();

        // Initialize Facebook SDK
        FacebookSdk.sdkInitialize(getApplicationContext(), new FacebookSdk.InitializeCallback() {
            @Override
            public void onInitialized() {
                if (AccessToken.getCurrentAccessToken() == null)
                    Log.i("DakarPresse", "Not logged in Facebook yet");
                else {
                    Log.i("DakarPresse", "Logged in Facebook");
                    mAccessToken = AccessToken.getCurrentAccessToken();
                }
            }
        });
        AppEventsLogger.activateApp(this);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getString(R.string.base_url))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        mAPI = restAdapter.create(IlmcAPI.class);

        UserDAO userDAO = new UserDAO(this);
        mUser = userDAO.getUser();

        initCloudinary();
    }

    public static Context getContext(){
        return instance;
    }

    public static IlmcAPI getAPI(Context context) {
        return get(context).mAPI;
    }

    private static ILMCApp get(Context context) {
        if (context instanceof Activity) {
            return (ILMCApp) ((Activity) context).getApplication();
        }
        return (ILMCApp) context.getApplicationContext();
    }

    /**
     * Users related functions
     */

    public static User getUser() {
        return mUser;
    }

    public static void updateUser(final User user) {
        UserDAO userDAO = new UserDAO(getContext());
        userDAO.updateUser(user);
        mUser = user;
    }

    public static void deleteUser() {
        if (mUser != null) {
            UserDAO userDAO = new UserDAO(getContext());
            userDAO.deleteUser(mUser.getId());
        }
    }

    public static void createUser(final User user) {
        UserDAO userDAO = new UserDAO(getContext());
        userDAO.createUser(user);
        mUser = user;
    }

    public static String getToken() {
        return mUser.getToken();
    }

    public static String getCityId() {
        return mUser.getCityId() != null ? mUser.getCityId() : null;
    }

    /**
     * Cloudinary helpers
     */

    private void initCloudinary() {
        mCloudinary = new Cloudinary(getString(R.string.key_cloudinary));
    }

    public static Cloudinary getCloundinary() {
        return mCloudinary;
    }

    /**
     * Facebook
     */

    public AccessToken getFacebookAccessToken() {
        return mAccessToken;
    }
}
