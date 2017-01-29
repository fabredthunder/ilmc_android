package fr.ilovemycity.android.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.ilovemycity.android.ILMCApp;
import fr.ilovemycity.android.R;
import fr.ilovemycity.android.adapters.TicketAdapter;
import fr.ilovemycity.android.events.LaunchMapEvent;
import fr.ilovemycity.android.events.RefreshTicketsEvent;
import fr.ilovemycity.android.models.Ticket;
import fr.ilovemycity.android.utils.NetworkUtils;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Fab on 28/05/2016.
 * All rights reserved
 */
public class TicketListFragment extends Fragment implements SpaceOnClickListener, SwipeRefreshLayout.OnRefreshListener {

    /**************
     * ATTRIBUTES *
     **************/

    @BindView(R.id.tickets)
    RecyclerView mRecyclerView;
    @BindView(R.id.space)
    SpaceNavigationView spaceNavigationView;
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView.Adapter<TicketAdapter.DataObjectHolder> mAdapter;
    private ArrayList<Ticket> mDataSet;

    private int currentIndex = 0;

    /***********
     * METHODS *
     ***********/

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ticket_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        // Configure the refreshing colors
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeRefreshLayout.setOnRefreshListener(this);

        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("ALL TICKETS", R.drawable.ic_menu_ticket));
        spaceNavigationView.addSpaceItem(new SpaceItem("MY TICKETS", R.drawable.ic_menu_profile));
        spaceNavigationView.setCentreButtonIcon(R.drawable.ic_ticket_creation);
        spaceNavigationView.setSpaceOnClickListener(this);

        mDataSet = new ArrayList<>();

        setUpRecyclerView(mRecyclerView);

        if (NetworkUtils.amIOnline(getContext()))
            setUpAllTickets("createdAt");
    }

    private void setUpRecyclerView(RecyclerView rv) {
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rv.setNestedScrollingEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onRefresh() {
        if (currentIndex == 0)
            setUpAllTickets("createdAt");
        else
            setUpMyTickets();
    }

    public void setUpMyTickets() {

        mDataSet.clear();

        ILMCApp.getAPI(ILMCApp.getContext()).getMyTickets(ILMCApp.getToken(), 50, 0, new retrofit.Callback<List<Ticket>>() {

            @Override
            public void success(List<Ticket> tickets, Response response) {

                for (int i = 0; i < tickets.size(); ++i)
                    mDataSet.add(tickets.get(i));

                if (mRecyclerView != null) {
                    mAdapter = new TicketAdapter(mDataSet, 1);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("FAIL : " + error.toString());
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void setUpAllTickets(final String order) {

        mDataSet.clear();

        ILMCApp.getAPI(ILMCApp.getContext()).getTickets(ILMCApp.getToken(), 50, 0, order, ILMCApp.getCityId(), new retrofit.Callback<List<Ticket>>() {

            @Override
            public void success(List<Ticket> tickets, Response response) {

                for (int i = 0; i < tickets.size(); ++i)
                    mDataSet.add(tickets.get(i));

                if (mRecyclerView != null) {
                    mAdapter = new TicketAdapter(mDataSet, 0);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("FAIL : " + error.toString());
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onCentreButtonClick() {
        EventBus.getDefault().post(new LaunchMapEvent());
    }

    @Override
    public void onItemClick(int itemIndex, String itemName) {

        currentIndex = itemIndex;

        switch (itemIndex) {

            case 0:
                setUpAllTickets("createdAt");
                break;
            case 1:
                setUpMyTickets();
                break;

        }

    }

    @Override
    public void onItemReselected(int itemIndex, String itemName) {
        // DONT GIVE A SHIT
    }

    /***********
     * EVENTS *
     ***********/

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onRefreshTicketsEvent(RefreshTicketsEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        if (currentIndex == 0)
            setUpAllTickets(event.order != null ? event.order : "createdAt");
        else
            setUpMyTickets();
    }
}
