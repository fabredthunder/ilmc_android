package fr.ilovemycity.android.views;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.nguyenhoanglam.imagepicker.activity.ImagePicker;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.ilovemycity.android.ILMCApp;
import fr.ilovemycity.android.R;
import fr.ilovemycity.android.controllers.HomeActivity;
import fr.ilovemycity.android.events.GalleryResultEvent;
import fr.ilovemycity.android.events.RefreshMapEvent;
import fr.ilovemycity.android.events.RefreshTicketsEvent;
import fr.ilovemycity.android.models.Loc;
import fr.ilovemycity.android.models.Ticket;
import fr.ilovemycity.android.utils.ImageUtils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Fab on 25/06/2016.
 * All rights reserved
 */
public class TicketCreationFragment extends Fragment implements View.OnClickListener {

    /**************
     * ATTRIBUTES *
     **************/

    String[] types = {"Type", "Incident", "Danger", "Initiative", "Divers"};

    @BindView(R.id.iv_photo_ticket) ImageView   ivPhotoTicket;
    @BindView(R.id.tv_ticket_adress) TextView   tvTicketAdress;
    @BindView(R.id.input_title) EditText        etTitle;
    @BindView(R.id.input_description) EditText  etDescription;
    @BindView(R.id.tv_submit) TextView          tvSubmit;
    @BindView(R.id.spinner_type) Spinner        spinnerType;

    private LatLng      position;
    private File        photoFile = null;

    /***********
     * METHODS *
     ***********/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_ticket_creation, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        ivPhotoTicket.setOnClickListener(this);
        tvSubmit.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

        final ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item);

        for (String type : types) {
            adapter.add(type);
        }
        spinnerType.setAdapter(adapter);
        spinnerType.setSelection(0);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case (R.id.iv_photo_ticket) :
                //EventBus.getDefault().post(new OpenGalleryEvent());
                ImagePicker.create(getActivity())
                        .folderMode(true) // folder mode (false by default)
                        .folderTitle("Folder") // folder selection title
                        .imageTitle("Tap to select") // image selection title
                        .single() // single mode
                        .showCamera(true) // show camera or not (true by default)
                        .imageDirectory("Camera") // directory name for captured image  ("Camera" folder by default)
                        .start(HomeActivity.REQUEST_CAMERA); // start image picker activity with request code
                break;
            case (R.id.tv_submit) :

                Ticket ticket = new Ticket();
                ticket.setTitle(etTitle.getText().toString());
                ticket.setAddress(tvTicketAdress.getText().toString());
                ticket.setDescription(etDescription.getText().toString());
                ticket.setCityId(ILMCApp.getUser().getCityId());
                ticket.setType(types[spinnerType.getSelectedItemPosition()]);

                String type = types[spinnerType.getSelectedItemPosition()];

                if (type.equals("Type"))
                    type = "Divers";

                ticket.setType(type);

                ticket.setLocation(new Loc());

                ticket.getLocation().setLatitude(position.longitude);
                ticket.getLocation().setLongitude(position.latitude);

                ILMCApp.getAPI(getContext()).postTicket(ILMCApp.getToken(), ticket, new Callback<Ticket>() {

                    @Override
                    public void success(Ticket ticket, Response response) {
                        EventBus.getDefault().postSticky(new RefreshTicketsEvent("createdAt"));
                        EventBus.getDefault().post(new RefreshMapEvent());

                        if (photoFile != null) {
                            InputStream is;
                            try {
                                is = new FileInputStream(photoFile);
                                ImageUtils.uploadImage(is, 1, ticket.getId());
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }

                        Toast.makeText(getActivity(), "Ticket créé", Toast.LENGTH_SHORT).show();
                        getActivity().onBackPressed();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        System.out.println("FAIL : " + error.toString());
                    }
                });

                break;
        }

    }

    public void setPhoto(File photoFile) {

        if (photoFile != null) {
            this.photoFile = photoFile;
            ImageUtils.fillImageView(photoFile, ivPhotoTicket);
        }

    }

    public void setAdress(LatLng position) {

        Geocoder geocoder;
        List<Address> addresses = null;

        this.position = position;

        if (position.latitude != 0.0 || position.longitude != 0.0) {
            geocoder = new Geocoder(getActivity(), Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(position.latitude, position.longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            assert addresses != null;
            String address = addresses.get(0).getAddressLine(0);

            tvTicketAdress.setText(address);
        }

    }

    public void reinitialize() {

        ivPhotoTicket.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_photo));
        etTitle.setText("");
        etDescription.setText("");

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onGalleryResultEvent(GalleryResultEvent event) {
        //todo
    }
}
