package fr.ilovemycity.android.controllers;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.ilovemycity.android.ILMCApp;
import fr.ilovemycity.android.R;
import fr.ilovemycity.android.models.City;
import fr.ilovemycity.android.models.User;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Fab on 24/10/15.
 * All rights reserved
 */
public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.firstname) EditText      mFirstname;
    @BindView(R.id.lastname) EditText       mLastname;
    @BindView(R.id.email) EditText          mEmail;
    @BindView(R.id.pass) EditText           mPassword;
    @BindView(R.id.register) LinearLayout   register;
    @BindView(R.id.city_spinner) Spinner    mSpinner;

    private List<City> mCities;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);


        final ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, R.layout.spinner_item_white);
        adapter.add("Aucune");

        ILMCApp.getAPI(this).getCities(new Callback<List<City>>() {
            @Override
            public void success(List<City> cities, Response response) {
                mCities = cities;
                for (int i = 0; i < cities.size(); ++i){
                    adapter.add(cities.get(i).getName());
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
        mSpinner.setAdapter(adapter);

        mSpinner.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);


        register.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.register) {
            if (mFirstname.getText().toString().isEmpty() || mLastname.getText().toString().isEmpty() ||
                    mPassword.getText().toString().isEmpty() || mEmail.getText().toString().isEmpty()) {
                if (mFirstname.getText().toString().isEmpty())
                    mFirstname.setError(getString(R.string.mandatory));
                if (mLastname.getText().toString().isEmpty())
                    mLastname.setError(getString(R.string.mandatory));
                if (mPassword.getText().toString().isEmpty())
                    mPassword.setError(getString(R.string.mandatory));
                if (mEmail.getText().toString().isEmpty())
                    mEmail.setError(getString(R.string.mandatory));
            }
            else {
                if (isValidEmail(mEmail.getText().toString())) {

                    createUser();
                }
                else {
                    mEmail.setError(getString(R.string.invalid_email));
                }
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public void createUser() {
        User user = new User();
        user.setPassword(mPassword.getText().toString());
        user.setEmail(mEmail.getText().toString());
        user.setFirstname(mFirstname.getText().toString());
        user.setLastname(mLastname.getText().toString());

        if (mSpinner.getSelectedItemPosition() == 0)
            Toast.makeText(getApplicationContext(), "Choisissez une ville !", Toast.LENGTH_SHORT).show();
        else {

            user.setCityId(mCities.get(mSpinner.getSelectedItemPosition() - 1).getId());

            ILMCApp.getAPI(this).signUp(user, new Callback<User>() {
                @Override
                public void success(User user, Response response) {
                    Toast.makeText(SignUpActivity.this, "Inscription réussie", Toast.LENGTH_SHORT).show();

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
                    System.out.println("FAIL : " + error.toString());

                    Toast.makeText(getApplicationContext(), "Renseignez tous les champs", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}