package fr.ilovemycity.android.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import fr.ilovemycity.android.R;
import fr.ilovemycity.android.common.TextAdapter;
import fr.ilovemycity.android.common.TextFragment;
import fr.ilovemycity.android.utils.dao.UserDAO;
import me.relex.circleindicator.CircleIndicator;

/**
 * Created by Fab on 23/10/15.
 * All rights reserved
 */

public class SplashActivity extends AppCompatActivity implements View.OnClickListener {

    protected ViewPager viewPager;
    protected CircleIndicator circleIndicator;

    protected LinearLayout btnRegister;
    protected LinearLayout btnSignIn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        if (userIsConnected()) {

            final Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);

            finish();
            overridePendingTransition(0, 0);

        }

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        circleIndicator = (CircleIndicator) findViewById(R.id.circle_indicator);

        btnRegister = (LinearLayout) findViewById(R.id.register);
        btnSignIn = (LinearLayout) findViewById(R.id.sign_in);

        btnRegister.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);

        TextFragment fragment = new TextFragment();
        TextFragment fragment_2 = new TextFragment();
        TextFragment fragment_3 = new TextFragment();

        Bundle bundle = new Bundle();
        bundle.putString("title", "Bonjour");
        bundle.putString("content", getString(R.string.home_text));
        fragment.setArguments(bundle);

        bundle = new Bundle();
        bundle.putString("title", "Contribuer");
        bundle.putString("content", getString(R.string.home_text_2));
        fragment_2.setArguments(bundle);

        bundle = new Bundle();
        bundle.putString("title", "Etre informé");
        bundle.putString("content", getString(R.string.home_text_3));
        fragment_3.setArguments(bundle);

        List<TextFragment> list = new ArrayList<>();
        list.add(fragment);
        list.add(fragment_2);
        list.add(fragment_3);

        List<String> titles = new ArrayList<>();
        titles.add("test");
        titles.add("test2");
        titles.add("test3");

        TextAdapter adapter = new TextAdapter(getSupportFragmentManager(), list, titles);

        viewPager.setAdapter(adapter);

        circleIndicator.setViewPager(viewPager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in:
                final Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
                break;
            case R.id.register:
                final Intent intent_reg = new Intent(getApplicationContext(), SignUpActivity.class);
                intent_reg.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent_reg);
                break;
        }
    }

    private boolean userIsConnected() {
        boolean connected = false;
        UserDAO userDAO = new UserDAO(this);
        if (!userDAO.getUsers().isEmpty())
            connected = true;
        return connected;
    }
}
