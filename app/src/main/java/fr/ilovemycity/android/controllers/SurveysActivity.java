package fr.ilovemycity.android.controllers;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.ilovemycity.android.ILMCApp;
import fr.ilovemycity.android.R;
import fr.ilovemycity.android.adapters.SurveyAdapter;
import fr.ilovemycity.android.events.DisplaySurveysEvent;
import fr.ilovemycity.android.events.RefreshSurveysEvent;
import fr.ilovemycity.android.models.Survey;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SurveysActivity extends AppCompatActivity {

    /**************
     * ATTRIBUTES *
     **************/

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.title) TextView title;
    @BindView(R.id.rv_events) RecyclerView mRecyclerView;

    private RecyclerView.Adapter<SurveyAdapter.DataObjectHolder> mAdapter;
    private ArrayList<Survey> mDataSet;

    /***********
     * METHODS *
     ***********/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        setContentView(R.layout.activity_events);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mDataSet = new ArrayList<>();
        setUpRecyclerView(mRecyclerView);
    }


    private void setUpRecyclerView(RecyclerView rv) {
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setNestedScrollingEnabled(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onDisplaySurveys(DisplaySurveysEvent event) {
        EventBus.getDefault().removeStickyEvent(event);

        title.setText(String.format("Sondages à %s", event.cityName));

        for (int i = 0; i < event.surveys.size(); ++i)
            mDataSet.add(event.surveys.get(i));

        if (mRecyclerView != null) {
            mAdapter = new SurveyAdapter(mDataSet);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshSurveys(RefreshSurveysEvent event) {

        ILMCApp.getAPI(this).getCitySurveys(ILMCApp.getToken(), ILMCApp.getUser().getCityId(), new Callback<List<Survey>>() {

            @Override
            public void success(List<Survey> surveys, Response response) {
                mDataSet.clear();
                for (int i = 0; i < surveys.size(); ++i)
                    mDataSet.add(surveys.get(i));

                if (mRecyclerView != null) {
                    mAdapter = new SurveyAdapter(mDataSet);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }
}