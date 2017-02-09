package fr.ilovemycity.android.views;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.ilovemycity.android.controllers.EventsActivity;
import fr.ilovemycity.android.ILMCApp;
import fr.ilovemycity.android.R;
import fr.ilovemycity.android.controllers.SurveysActivity;
import fr.ilovemycity.android.events.DisplayEventsEvent;
import fr.ilovemycity.android.events.DisplaySurveysEvent;
import fr.ilovemycity.android.events.RefreshDrawerEvent;
import fr.ilovemycity.android.models.City;
import fr.ilovemycity.android.models.Event;
import fr.ilovemycity.android.models.Survey;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CityFragment extends Fragment {

    /**************
     * ATTRIBUTES *
     **************/

    @BindView(R.id.no_city) TextView noCity;
    @BindView(R.id.city_name) TextView cityName;
    @BindView(R.id.city_phone) TextView cityPhone;
    @BindView(R.id.city_address) TextView cityAddress;
    @BindView(R.id.city_web) TextView cityWebsite;
    @BindView(R.id.iv_backdrop) ImageView ivBackdrop;
    @BindView(R.id.city_infos) LinearLayout cityInfos;
    @BindView(R.id.btn_events) Button btnEvents;
    @BindView(R.id.btn_survey) Button btnSurvey;

    private String mCityID = null;
    private City mCity = null;

    private List<Event> mEvents = null;
    private List<Survey> mSurveys = null;

    /***********
     * METHODS *
     ***********/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_city, container, false);

        ButterKnife.bind(this, view);

        mCityID = null;
        if (ILMCApp.getUser() != null)
            mCityID = ILMCApp.getUser().getCityId();

        if (mCityID != null) {
            noCity.setVisibility(View.GONE);
            cityInfos.setVisibility(View.VISIBLE);

            ILMCApp.getAPI(getContext()).getCity(ILMCApp.getToken(), mCityID, new Callback<City>() {
                @Override
                public void success(City city, Response response) {
                    mCity = city;
                    cityName.setText(city.getName());
                    cityPhone.setText(city.getPhoneNumber());
                    cityAddress.setText(city.getAddress());
                    cityWebsite.setText(city.getWebsite());
                    if (city.getPictures() != null)
                        Glide.with(getContext())
                                .load(city.getPictures()[0])
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(ivBackdrop);
                }

                @Override
                public void failure(RetrofitError error) {
                    error.printStackTrace();
                }
            });
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        ILMCApp.getAPI(getContext()).getCityEvents(ILMCApp.getToken(), mCityID, new Callback<List<Event>>() {

            @Override
            public void success(List<Event> events, Response response) {
                mEvents = events;
                if (events.size() > 0) {
                    btnEvents.setText(String.format("%s évenèments à venir", events.size()));
                    btnEvents.setBackgroundResource(R.drawable.btn_lightblue);
                } else {
                    btnEvents.setText(R.string.no_event);
                    btnEvents.setBackgroundResource(R.drawable.btn_gray);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });

        ILMCApp.getAPI(getContext()).getCitySurveys(ILMCApp.getToken(), mCityID, new Callback<List<Survey>>() {

            @Override
            public void success(List<Survey> surveys, Response response) {
                mSurveys = surveys;
                if (surveys.size() > 0) {
                    btnSurvey.setText(String.format("%s sondages en cours", surveys.size()));
                    btnSurvey.setBackgroundResource(R.drawable.btn_lightblue);
                } else {
                    btnEvents.setText(R.string.no_survey);
                    btnEvents.setBackgroundResource(R.drawable.btn_gray);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**********
     * EVENTS *
     **********/

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onRefreshDrawerEvent(RefreshDrawerEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        if (event.cityId != null) {
            noCity.setVisibility(View.GONE);
            cityInfos.setVisibility(View.VISIBLE);

            ILMCApp.getAPI(getContext()).getCity(ILMCApp.getToken(), event.cityId, new Callback<City>() {
                @Override
                public void success(City city, Response response) {
                    mCity = city;
                    cityName.setText(city.getName());
                    cityAddress.setText(city.getAddress());
                    cityPhone.setText(city.getPhoneNumber());
                    cityWebsite.setText(city.getWebsite());
                    if (city.getPictures() != null)
                        Glide.with(getContext())
                                .load(city.getPictures()[0])
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(ivBackdrop);
                }

                @Override
                public void failure(RetrofitError error) {
                    error.printStackTrace();
                }
            });
        }
    }

    @OnClick(R.id.btn_go)
    public void onClickBtnGo() {
        openMaps();
    }

    @OnClick(R.id.btn_events)
    public void onClickBtnEvents() {
        final Intent intent = new Intent(ILMCApp.getContext(), EventsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ILMCApp.getContext().startActivity(intent);
        EventBus.getDefault().postSticky(new DisplayEventsEvent(mEvents, mCity.getName()));
    }

    @OnClick(R.id.btn_survey)
    public void onClickBtnSurvey() {
        final Intent intent = new Intent(ILMCApp.getContext(), SurveysActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ILMCApp.getContext().startActivity(intent);
        EventBus.getDefault().postSticky(new DisplaySurveysEvent(mSurveys, mCity.getName()));
    }

    private void openMaps() {

        AlertDialog annotationDialog = new AlertDialog.Builder(
                getActivity(),
                R.style.AlertDialogCustom)
                .setPositiveButton("Go !", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (mCity.getLocation() != null) {
                            String geo = "google.navigation:q=" + mCity.getLocation().getCoordinates()[0] + "," + mCity.getLocation().getCoordinates()[1];

                            Uri gmmIntentUri = Uri.parse(geo);
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                startActivity(mapIntent);
                            }
                        } else
                            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();

                    }
                })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Cancel Action
                    }
                })
                .setTitle("On se rend à votre mairie ?")
                .create();
        annotationDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        annotationDialog.show();

    }
}