package fr.ilovemycity.android.controllers;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.ilovemycity.android.ILMCApp;
import fr.ilovemycity.android.R;
import fr.ilovemycity.android.models.User;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Fab on 24/10/15.
 * All rights reserved
 */
public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.email) EditText mEmail;
    @BindView(R.id.pass) EditText mPassword;
    @BindView(R.id.log) LinearLayout log;
    @BindView(R.id.login_fb_btn) LoginButton mFacebookLoginButton;

    private CallbackManager mCallbackManager = CallbackManager.Factory.create();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        setContentView(R.layout.activity_signin);

        ButterKnife.bind(this);

        log.setOnClickListener(this);

        float fbIconScale = 1.45f;
        Drawable drawable = ContextCompat.getDrawable(this,
                com.facebook.R.drawable.com_facebook_button_icon);
        drawable.setBounds(0, 0, (int) (drawable.getIntrinsicWidth() * fbIconScale),
                (int) (drawable.getIntrinsicHeight() * fbIconScale));
        mFacebookLoginButton.setCompoundDrawables(drawable, null, null, null);
        mFacebookLoginButton.setCompoundDrawablePadding(this.getResources().
                getDimensionPixelSize(R.dimen.fb_margin_override_textpadding));
        mFacebookLoginButton.setPadding(
                getResources().getDimensionPixelSize(R.dimen.fb_margin_override_lr),
                getResources().getDimensionPixelSize(R.dimen.fb_margin_override_top),
                0,
                getResources().getDimensionPixelSize(R.dimen.fb_margin_override_bottom));
        //mFacebookLoginButton.setFrag
        mFacebookLoginButton.setReadPermissions("public_profile email");
        mFacebookLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject me, GraphResponse response) {

                                String token = loginResult.getAccessToken().getToken();
                                //String base64EncodedCredentials = Base64.encodeToString(token.getBytes(), Base64.NO_WRAP);
                                String bearer = "Bearer " + token;

                                loginFacebook(bearer);

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,email,first_name,last_name,cover,picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
                exception.printStackTrace();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.log)
            checkUser(mEmail.getText().toString(), mPassword.getText().toString());

    }

    public void checkUser(String email, String password) {

        String credentials = email + ":" + password;
        String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        String basic = "Basic " + base64EncodedCredentials;

        ILMCApp.getAPI(getApplicationContext()).signIn(basic, new User(), new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                user.setUsername(user.getFirstname() + " " + user.getLastname());
                ILMCApp.createUser(user);

                final Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getApplicationContext().startActivity(intent);

                finish();
                overridePendingTransition(0, 0);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    public void loginFacebook(String auth) {

        ILMCApp.getAPI(getApplicationContext()).signInFb(auth, new User(), new Callback<User>() {
            @Override
            public void success(User user, Response response) {

                user.setUsername(user.getFirstname() + " " + user.getLastname());

                ILMCApp.deleteUser();
                ILMCApp.createUser(user);

                final Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getApplicationContext().startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }
}