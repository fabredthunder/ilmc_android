package fr.ilovemycity.android.views;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.ilovemycity.android.ILMCApp;
import fr.ilovemycity.android.R;
import fr.ilovemycity.android.events.OpenGalleryEvent;
import fr.ilovemycity.android.events.RefreshDrawerEvent;
import fr.ilovemycity.android.events.RefreshMapEvent;
import fr.ilovemycity.android.events.RefreshTicketsEvent;
import fr.ilovemycity.android.models.City;
import fr.ilovemycity.android.models.User;
import fr.ilovemycity.android.utils.ImageUtils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Fab on 21/06/2016.
 * All rights reserved
 */

public class ProfileFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.profile_pic) ImageView       profilePic;
    @BindView(R.id.input_email) EditText        email;
    @BindView(R.id.input_lastname) EditText     lastname;
    @BindView(R.id.input_firstname) EditText    firstname;
    @BindView(R.id.input_password) EditText     password;
    @BindView(R.id.input_curpassword) EditText  curPassword;
    @BindView(R.id.btn_validate) Button         validation;
    @BindView(R.id.city_spinner) Spinner        spinner;

    private User mUser = null;

    private List<City> mCities;
    private int initialPosition = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        ButterKnife.bind(this, view);

        mUser = ILMCApp.getUser();

        final ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item);
        adapter.add("Aucune");

        ILMCApp.getAPI(getActivity()).getCities(new Callback<List<City>>() {
            @Override
            public void success(List<City> cities, Response response) {
                mCities = cities;
                for (int i = 0; i < cities.size(); ++i){
                    adapter.add(cities.get(i).getName());
                    if (mUser.getCityId() != null && mUser.getCityId().equals(cities.get(i).getId()))
                        initialPosition = i + 1;
                    spinner.setAdapter(adapter);
                    spinner.setSelection(initialPosition);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });

        spinner.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        validation.setOnClickListener(this);
        profilePic.setOnClickListener(this);

        ImageUtils.fillImageView(mUser.getPicture(), profilePic);
        email.setText(mUser.getEmail());

        firstname.setText(mUser.getFirstname());
        lastname.setText(mUser.getLastname());

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_validate) {

            User updatedUser = new User();

            Log.e("EMAIL", email.getText().toString() + " " + ILMCApp.getUser().getEmail());

            if (!email.getText().toString().equals(ILMCApp.getUser().getEmail()))
                updatedUser.setEmail(email.getText().toString());
            updatedUser.setLastname(lastname.getText().toString());
            updatedUser.setFirstname(firstname.getText().toString());
            if (!curPassword.getText().toString().trim().equals(""))
                updatedUser.setCurrPassword(curPassword.getText().toString());
            if (!password.getText().toString().trim().equals(""))
                updatedUser.setPassword(password.getText().toString());
            if (spinner.getSelectedItemPosition() != 0)
                updatedUser.setCityId(mCities.get(spinner.getSelectedItemPosition() - 1).getId());

            ILMCApp.getAPI(ILMCApp.getContext()).updateMe(ILMCApp.getToken(), updatedUser, new Callback<User>() {
                @Override
                public void success(User user, Response response) {

                    User newUser = ILMCApp.getUser();
                    newUser.setEmail(user.getEmail() != null ? user.getEmail() : newUser.getEmail());
                    newUser.setPassword(user.getPassword() != null ? user.getPassword() : newUser.getPassword());
                    newUser.setFirstname(user.getFirstname() != null ? user.getFirstname() : newUser.getFirstname());
                    newUser.setLastname(user.getLastname() != null ? user.getLastname() : newUser.getLastname());
                    newUser.setUsername(user.getFirstname() + " " + user.getLastname());
                    newUser.setCityId(user.getCityId() != null ? user.getCityId() : newUser.getCityId());
                    newUser.setPicture(user.getPicture() != null ? user.getPicture() : newUser.getPicture());

                    ILMCApp.updateUser(newUser);

                    EventBus.getDefault().postSticky(new RefreshDrawerEvent(user.getCityId()));
                    EventBus.getDefault().postSticky(new RefreshTicketsEvent("createdAt"));
                    EventBus.getDefault().postSticky(new RefreshMapEvent());

                    Toast.makeText(getActivity(), "Modifications enregistrées", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void failure(RetrofitError error) {
                    error.printStackTrace();
                }
            });
        }
        else if (v.getId() == R.id.profile_pic) {
            EventBus.getDefault().post(new OpenGalleryEvent());
        }
    }

    public ImageView getProfilePic() {
        return profilePic;
    }
}
