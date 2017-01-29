package fr.ilovemycity.android.views;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

import fr.ilovemycity.android.ILMCApp;
import fr.ilovemycity.android.R;
import fr.ilovemycity.android.controllers.TicketActivity;
import fr.ilovemycity.android.events.OpenTicketEvent;
import fr.ilovemycity.android.events.RefreshMapEvent;
import fr.ilovemycity.android.models.Ticket;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Fab on 21/06/2016.
 * All rights reserved
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    /**************
     * ATTRIBUTES *
     **************/

    private final int MY_PERMISSION_REQUEST = 0;

    MapView gMapView;
    private SupportMapFragment mapFragment;
    public static GoogleMap   mMap = null;

    private boolean isMarkerOnScreen;
    private Marker  mMarker;
    private LatLng  mMarkerPosition;

    private Button btnSet;
    private Button btnCreate;

    private LocationManager locationManager;

    private Snackbar snackbar = null;

    private TicketCreationFragment ticketCreationFragment;

    private FrameLayout container;

    /***********
     * METHODS *
     ***********/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_map, container, false);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

        isMarkerOnScreen = false;

        ticketCreationFragment = new TicketCreationFragment();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnCreate = (Button) view.findViewById(R.id.button_create);
        btnSet = (Button) view.findViewById(R.id.button_set);
        container = (FrameLayout) view.findViewById(R.id.subcontainer);

        snackbar = Snackbar.make(view, "La localisation est desactivée !", Snackbar.LENGTH_LONG)
                .setAction("Activer", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).setActionTextColor(ContextCompat.getColor(getContext(), R.color.green));

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.subcontainer, ticketCreationFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        if (mMap != null)
            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_REQUEST);
            } else {
                if (Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    moveMapToMyLocation();
                }
            }

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (!isMarkerOnScreen) {
                    mMarker = mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
                    mMarkerPosition = mMarker.getPosition();
                    isMarkerOnScreen = true;
                    btnSet.setVisibility(View.GONE);
                    btnCreate.setVisibility(View.VISIBLE);
                }
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {
                ILMCApp.getAPI(ILMCApp.getContext()).getTickets(ILMCApp.getToken(), 50, 0, "createdAt", ILMCApp.getCityId(), new retrofit.Callback<List<Ticket>>() {

                    @Override
                    public void success(List<Ticket> tickets, Response response) {

                        for (int i = 0; i < tickets.size(); ++i) {
                            if (tickets.get(i).getTitle().equals(marker.getTitle())) {
                                final Intent intent = new Intent(ILMCApp.getContext(), TicketActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                ILMCApp.getContext().startActivity(intent);
                                EventBus.getDefault().postSticky(new OpenTicketEvent(tickets.get(i)));
                                return;
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        System.out.println("FAIL : " + error.toString());
                    }
                });
            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {



            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                mMarkerPosition = marker.getPosition();
                ticketCreationFragment.setAdress(mMarkerPosition);
            }
        });

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {

                if (!isLocationActivated(locationManager)) {
                    if (snackbar != null && !snackbar.isShown())
                        snackbar.show();
                }
                return false;
            }
        });

        //gMapView.onResume();

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMarker != null && container.getVisibility() == View.GONE) {
                    ticketCreationFragment.setAdress(mMarkerPosition);
                    container.setVisibility(View.VISIBLE);
                }
            }
        });

        addMarkers();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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

    public static void addMarkers() {

        ILMCApp.getAPI(ILMCApp.getContext()).getTickets(ILMCApp.getToken(), 50, 0, "createdAt", ILMCApp.getCityId(), new retrofit.Callback<List<Ticket>>() {

            @Override
            public void success(List<Ticket> tickets, Response response) {

                for (int i = 0; i < tickets.size(); ++i) {

                    mMap.addMarker(new MarkerOptions().position(new LatLng(tickets.get(i).getLocation().getCoordinates()[0], tickets.get(i).getLocation().getCoordinates()[1]))
                            .draggable(false)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                            .title(tickets.get(i).getTitle())
                            .snippet(tickets.get(i).getDescription()));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("FAIL : " + error.toString());
            }
        });

    }

    private void moveMapToMyLocation() {

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (isLocationActivated(locationManager)) {

            Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            LatLng coordinate = new LatLng(lat, lng);

            CameraUpdate center = CameraUpdateFactory.newLatLng(coordinate);
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

            mMap.moveCamera(center);
            mMap.animateCamera(zoom);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    if (Build.VERSION.SDK_INT >= 23 &&
                            ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                        moveMapToMyLocation();
                    }

                } else {
                    // permission denied
                    break;
                }
            }
        }
    }

    private boolean isLocationActivated(LocationManager lm) {

        boolean gps_enabled = false ,network_enabled = false;

        try{
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch(Exception ex){
            System.out.println("Exception : " + ex);

        }
        try{
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch(Exception ex){
            System.out.println("Exception : " + ex);
        }

        return gps_enabled && network_enabled;

    }

    public void reinitialize() {

        if (mMarker != null) {

            removeMarker();

            btnSet.setVisibility(View.VISIBLE);
            btnCreate.setVisibility(View.GONE);

            moveMapToMyLocation();

        }
    }

    public void setTicketImage(File photoFile) {

        ticketCreationFragment.setPhoto(photoFile);

    }

    public FrameLayout getContainer() {
        return container;
    }

    public void closeContainer() {

        container.setVisibility(View.GONE);
        ticketCreationFragment.reinitialize();

        btnSet.setVisibility(View.VISIBLE);
        btnCreate.setVisibility(View.GONE);

        removeMarker();
    }

    private void removeMarker() {
        if (mMarker != null)
            mMarker.remove();
        mMarker = null;
        isMarkerOnScreen = false;
    }

    /***********
     * EVENTS *
     ***********/

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onRefreshMapEvent(RefreshMapEvent event) {
        mMap.clear();
        addMarkers();
    }
}
