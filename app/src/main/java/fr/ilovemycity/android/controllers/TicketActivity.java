package fr.ilovemycity.android.controllers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.luseen.datelibrary.DateHelper;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.ilovemycity.android.ILMCApp;
import fr.ilovemycity.android.R;
import fr.ilovemycity.android.events.OpenTicketEvent;
import fr.ilovemycity.android.events.RefreshMapEvent;
import fr.ilovemycity.android.events.RefreshTicketsEvent;
import fr.ilovemycity.android.models.Msg;
import fr.ilovemycity.android.models.Ticket;
import fr.ilovemycity.android.utils.ImageUtils;
import fr.ilovemycity.android.utils.ToastUtils;
import fr.ilovemycity.android.utils.components.ObservableScrollView;
import fr.ilovemycity.android.utils.components.WorkaroundMapFragment;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TicketActivity extends AppCompatActivity implements OnMapReadyCallback, ObservableScrollView.OnScrollChangedListener {

    /**************
     * ATTRIBUTES *
     **************/

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.scroll_view) ObservableScrollView scrollView;
    @BindView(R.id.tv_creator) TextView tvCreator;
    @BindView(R.id.tv_date) TextView tvDate;
    @BindView(R.id.creator_img) ImageView ivCreator;
    @BindView(R.id.tv_title) TextView tvTitle;
    @BindView(R.id.tv_desc) TextView tvDesc;
    @BindView(R.id.iv_status) ImageView ivStatus;
    @BindView(R.id.tv_status) TextView tvStatus;

    private Drawable mActionBarBg = null;
    @BindView(R.id.toolbar_title) TextView mTvActionBarTitle;
    @BindView(R.id.iv_backdrop) ImageView mIvBackdrop;
    @BindView(R.id.iv_backdrop_overlay) ImageView mIvBackdropOverlay;
    @BindView(R.id.like_btn) ImageView mLikeBtn;

    private Ticket mTicket = null;

    private List<Msg> mMsg = null;
    private int msgPosition = 0;
    @BindView(R.id.tv_msg_header) TextView tvMsgHeader;
    @BindView(R.id.tv_msg_creator) TextView tvMsgCreator;
    @BindView(R.id.tv_msg_date) TextView tvMsgDate;
    @BindView(R.id.tv_msg_desc) TextView tvMsgDesc;
    @BindView(R.id.iv_user) ImageView ivMsgCreator;
    @BindView(R.id.iv_delete_msg) ImageView ivMsgDelete;
    @BindView(R.id.tv_empty) TextView tvEmptyMsg;
    @BindView(R.id.tv_popularity) TextView tvPopularity;

    @BindView(R.id.arrow_left) ImageView arrowLeft;
    @BindView(R.id.arrow_right) ImageView arrowRight;

    GoogleMap mMap = null;

    /***********
     * METHODS *
     ***********/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        setContentView(R.layout.activity_ticket);

        ButterKnife.bind(this);

        ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map)).getMapAsync(this);
        ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map)).setListener(new WorkaroundMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                //scrollView.requestDisallowInterceptTouchEvent(true);
            }
        });

        scrollView.setOnScrollChangedListener(this);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Prepare the color of the fading action bar
        mActionBarBg = new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        mActionBarBg.setAlpha(0);
        getSupportActionBar().setBackgroundDrawable(mActionBarBg);
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
    public void onScrollChanged(ScrollView scrollView, int x, int y, int oldX, int oldY) {
        final int headerHeight = mIvBackdrop.getHeight();
        final float ratio = (float) Math.min(Math.max(y, 0), headerHeight) / headerHeight;
        final int newAlpha = (int) (ratio * 255);
        mActionBarBg.setAlpha(newAlpha);
        mTvActionBarTitle.setTextColor(Color.argb(newAlpha, 255, 255, 255));

        // scroll the background
        if (ratio < 1) {
            final int yDst = (int) (headerHeight * ratio / -2f);
            mIvBackdrop.scrollTo(0, yDst);
            mIvBackdropOverlay.scrollTo(0, yDst);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @OnClick(R.id.like_btn)
    public void onClickLike() {

        if (!mTicket.isVote()) {
            ILMCApp.getAPI(getApplicationContext()).voteTicket(ILMCApp.getToken(), mTicket.getId(), mTicket, new Callback<Ticket>() {
                @Override
                public void success(Ticket ticket, Response response) {
                    EventBus.getDefault().postSticky(new RefreshTicketsEvent("createdAt"));
                    ToastUtils.makeToast(getApplicationContext(), "Merci pour votre vote !", 2000);
                    mLikeBtn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_like));

                    mTicket.setVotes(mTicket.getVotes() + 1);
                    mTicket.setVote(true);

                    String pop = ((mTicket.getCreator().getId().equals(ILMCApp.getUser().getId())) ? "Votre" : "Ce")
                            + " ticket a reçu " + mTicket.getVotes() + " like(s) !";
                    tvPopularity.setText(pop);
                }

                @Override
                public void failure(RetrofitError error) {
                    error.printStackTrace();
                    ToastUtils.makeToast(getBaseContext(), "Une erreur est survenue !", 2000);
                }
            });
        } else {
            ILMCApp.getAPI(getApplicationContext()).unvoteTicket(ILMCApp.getToken(), mTicket.getId(), new Callback<Ticket>() {
                @Override
                public void success(Ticket ticket, Response response) {
                    EventBus.getDefault().postSticky(new RefreshTicketsEvent("createdAt"));
                    ToastUtils.makeToast(getApplicationContext(), "Vous avez retiré votre vote !", 2000);
                    mLikeBtn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_like_empty));

                    mTicket.setVotes(mTicket.getVotes() - 1);
                    mTicket.setVote(false);

                    String pop = ((mTicket.getCreator().getId().equals(ILMCApp.getUser().getId())) ? "Votre" : "Ce")
                            + " ticket a reçu " + mTicket.getVotes() + " like(s) !";
                    tvPopularity.setText(pop);
                }

                @Override
                public void failure(RetrofitError error) {
                    error.printStackTrace();
                    ToastUtils.makeToast(getBaseContext(), "Une erreur est survenue !", 2000);
                }
            });
        }
    }

    @OnClick(R.id.iv_edit)
    public void onClickEdit() {
        editDescription();
    }

    @OnClick(R.id.iv_delete)
    public void onClickDelete() {
        deleteTicket();
    }

    @OnClick(R.id.iv_walk)
    public void onClickWalk() {
        openMaps();
    }

    @OnClick(R.id.arrow_left)
    public void onClickArrowL() {
        arrowRight.setVisibility(View.VISIBLE);
        if (msgPosition > 0) {
            msgPosition--;
            if (msgPosition == 0)
                arrowLeft.setVisibility(View.GONE);
            bindMessage();
        }
    }

    @OnClick(R.id.fab)
    public void onClickFab() {
        newMessage();
    }


    @OnClick(R.id.arrow_right)
    public void onClickArrowR() {
        arrowLeft.setVisibility(View.VISIBLE);
        if (msgPosition < mMsg.size()) {
            msgPosition++;
            if (msgPosition == (mMsg.size() - 1))
                arrowRight.setVisibility(View.GONE);
            bindMessage();
        }
    }

    @OnClick(R.id.iv_delete_msg)
    public void onClickDeleteMsg() {
        ILMCApp.getAPI(getApplicationContext()).delMessage(ILMCApp.getToken(), mMsg.get(msgPosition).getId(), new Callback<Object>() {
            @Override
            public void success(Object ticket, Response response) {
                EventBus.getDefault().postSticky(new RefreshTicketsEvent("createdAt"));
                msgPosition = 0;
                arrowLeft.setVisibility(View.GONE);
                addMessages();
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                ToastUtils.makeToast(getApplicationContext(), "Une erreur est survenue", 2000);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setIndoorEnabled(true);
        googleMap.setBuildingsEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        googleMap.setMyLocationEnabled(false);

        mMap = googleMap;
    }

    /**********
     * EVENTS *
     **********/

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onOpenTicketEvent(OpenTicketEvent event) {
        EventBus.getDefault().removeStickyEvent(event);

        mTicket = event.ticket;
        bind();

       ILMCApp.getAPI(getApplicationContext()).getTicket(ILMCApp.getToken(), event.ticket.getId(), new Callback<Ticket>() {
            @Override
            public void success(Ticket ticket, Response response) {

                mTicket = ticket;
                bind();

                LatLng coordinate = new LatLng(ticket.getLocation().getCoordinates()[0], ticket.getLocation().getCoordinates()[1]);

                CameraUpdate center = CameraUpdateFactory.newLatLng(coordinate);
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                mMap.moveCamera(center);
                mMap.animateCamera(zoom);
                mMap.getUiSettings().setCompassEnabled(false);
                mMap.getUiSettings().setZoomControlsEnabled(false);
                mMap.getUiSettings().setZoomGesturesEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mMap.getUiSettings().setAllGesturesEnabled(false);

                mMap.addMarker(new MarkerOptions().position(coordinate)
                        .draggable(false)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    /***********
     * BINGING *
     ***********/

    private void bind() {

        if (mTicket != null) {

            if (mTicket.isVote())
                mLikeBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_like));

            if (mTicket.getCreatorId().equals(ILMCApp.getUser().getId()))
                findViewById(R.id.param_layout).setVisibility(View.VISIBLE);

            if (mTicket.getPictures() != null)
                Glide.with(this)
                        .load(mTicket.getPictures().get(0))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .into(mIvBackdrop);
            else
                Glide.with(this)
                        .load(R.drawable.empty_ticket)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .into(mIvBackdrop);

            setTitle(mTicket.getTitle());
            tvTitle.setText(mTicket.getTitle());
            tvDesc.setText(mTicket.getDescription());

            mTvActionBarTitle.setText(mTicket.getTitle());
            mTvActionBarTitle.setTextColor(Color.argb(0, 255, 255, 255));

            tvCreator.setText(String.format(getString(R.string.created_by), mTicket.getCreator().getFullName()));

            /**
             * Date gestion
             */

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.FRANCE);
            Date date = null;
            try {
                date = sdf.parse(mTicket.getCreatedAt());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            DateHelper dateHelper = new DateHelper(date);

            String sDate = "Le " + dateHelper.getDay() + " " + dateHelper.getMonthLongName() + " " + dateHelper.getYear()
                    + " à " + dateHelper.getHourOnly() + "h" + dateHelper.getMinuteOnly();
            tvDate.setText(sDate);

            /**
             * Avatar owner
             */

            ImageUtils.fillImageView(mTicket.getCreator().getPicture(), ivCreator);

            /**
             * Status
             */

            switch (mTicket.getStatus()) {
                case "o":
                    ivStatus.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.status_open_anim));
                    tvStatus.setText(R.string.awaiting);
                    break;
                case "p":
                    ivStatus.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.status_pending_anim));
                    tvStatus.setText(R.string.pending);
                    break;
                case "c":
                    ivStatus.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.status_close_anim));
                    tvStatus.setText(R.string.solved);
                    break;
                default:
                    break;
            }

            AnimationDrawable frameAnimation = (AnimationDrawable) ivStatus.getDrawable();
            frameAnimation.start();

            String pop = ((mTicket.getCreator().getId().equals(ILMCApp.getUser().getId())) ? "Votre" : "Ce")
                    + " ticket a reçu " + mTicket.getVotes() + " like(s) !";
            tvPopularity.setText(pop);

            addMessages();

        }

    }

    private void editDescription() {
        LayoutInflater inflater = (TicketActivity.this).getLayoutInflater();
        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.dialog_textbox, null);
        final EditText entry = (EditText) view.findViewById(R.id.et_entry);

        if (mTicket.getDescription() != null)
            entry.setText(mTicket.getDescription());

        entry.setSelection(entry.getText().length());

        AlertDialog annotationDialog = new AlertDialog.Builder(
                TicketActivity.this,
                R.style.AlertDialogCustom)
                .setPositiveButton("Enregistrer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Ticket ticket = new Ticket();
                        ticket.setDescription(entry.getText().toString());

                        ILMCApp.getAPI(getApplicationContext()).updateTicket(ILMCApp.getToken(), mTicket.getId(), ticket, new Callback<Ticket>() {
                            @Override
                            public void success(Ticket ticket, Response response) {
                                EventBus.getDefault().postSticky(new RefreshTicketsEvent("createdAt"));
                                EventBus.getDefault().postSticky(new RefreshMapEvent());
                                tvDesc.setText(ticket.getDescription());
                                ToastUtils.makeToast(getApplicationContext(), "Description modifiée", 2000);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                error.printStackTrace();
                                ToastUtils.makeToast(getApplicationContext(), "Une erreur est survenue", 2000);
                            }
                        });
                    }
                })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Cancel Action
                    }
                })
                .setTitle("Votre description")
                .setView(view)
                .create();

        Window window = annotationDialog.getWindow();
        if (window != null) {
            annotationDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            annotationDialog.show();
        }
    }

    private void deleteTicket() {

        if (mTicket.getStatus().equals("o")) {
            AlertDialog annotationDialog = new AlertDialog.Builder(
                    TicketActivity.this,
                    R.style.AlertDialogCustom)
                    .setPositiveButton("Supprimer", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            ILMCApp.getAPI(getApplicationContext()).deleteTicket(ILMCApp.getToken(), mTicket.getId(), new Callback<Object>() {
                                @Override
                                public void success(Object s, Response response) {
                                    EventBus.getDefault().postSticky(new RefreshTicketsEvent("createdAt"));
                                    EventBus.getDefault().postSticky(new RefreshMapEvent());
                                    onBackPressed();
                                    ToastUtils.makeToast(getApplicationContext(), "Ticket supprimé", 2000);
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    EventBus.getDefault().postSticky(new RefreshTicketsEvent("createdAt"));
                                    EventBus.getDefault().postSticky(new RefreshMapEvent());
                                    onBackPressed();
                                    ToastUtils.makeToast(getApplicationContext(), "Ticket supprimé", 2000);
                                }
                            });
                        }
                    })
                    .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Cancel Action
                        }
                    })
                    .setTitle("Vous allez supprimer votre ticket")
                    .create();

            Window window = annotationDialog.getWindow();
            if (window != null) {
                annotationDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                annotationDialog.show();
            }
        } else
            ToastUtils.makeToast(getApplicationContext(), "Impossible de supprimer un ticket en cours ou clos !", 2000);
    }

    private void openMaps() {

        AlertDialog annotationDialog = new AlertDialog.Builder(
                TicketActivity.this,
                R.style.AlertDialogCustom)
                .setPositiveButton("Go !", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String geo = "google.navigation:q=" + mTicket.getLocation().getCoordinates()[0] + "," + mTicket.getLocation().getCoordinates()[1];

                        Uri gmmIntentUri = Uri.parse(geo);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        if (mapIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(mapIntent);
                        }

                    }
                })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Cancel Action
                    }
                })
                .setTitle("Naviguer vers cet endroit ?")
                .create();

        Window window = annotationDialog.getWindow();
        if (window != null) {
            annotationDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            annotationDialog.show();
        }

    }

    private void newMessage() {

        LayoutInflater inflater = (TicketActivity.this).getLayoutInflater();
        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.dialog_textbox, null);

        final EditText entry = (EditText) view.findViewById(R.id.et_entry);
        entry.setHint("Tapez ici ...");

        AlertDialog annotationDialog = new AlertDialog.Builder(
                TicketActivity.this,
                R.style.AlertDialogCustom)
                .setPositiveButton("Envoyer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Msg msg = new Msg();
                        msg.setText(entry.getText().toString());

                        ILMCApp.getAPI(getApplicationContext()).postMessage(ILMCApp.getToken(), mTicket.getId(), msg, new Callback<Msg>() {
                            @Override
                            public void success(Msg msg, Response response) {
                                EventBus.getDefault().postSticky(new RefreshTicketsEvent("createdAt"));
                                msgPosition = 0;
                                addMessages();
                                ToastUtils.makeToast(getApplicationContext(), "Message envoyé", 2000);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                error.printStackTrace();
                                ToastUtils.makeToast(getApplicationContext(), "Une erreur est survenue", 2000);
                            }
                        });
                    }
                })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Cancel Action
                    }
                })
                .setTitle("Votre message")
                .setView(view)
                .create();

        Window window = annotationDialog.getWindow();
        if (window != null) {
            annotationDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            annotationDialog.show();
        }

    }

    private void addMessages() {

        ILMCApp.getAPI(this).getMessages(ILMCApp.getToken(), mTicket.getId(), new Callback<List<Msg>>() {
                    @Override
                    public void success(List<Msg> msg, Response response) {
                        mMsg = msg;

                        String header = "Messages (" +  msg.size() + ")";
                        tvMsgHeader.setText(header);

                        if (msg.size() > 0) {
                            tvEmptyMsg.setVisibility(View.GONE);
                            if (msg.size() == 1)
                                arrowRight.setVisibility(View.GONE);
                            else
                                arrowRight.setVisibility(View.VISIBLE);
                            bindMessage();
                        }
                        else {
                            arrowRight.setVisibility(View.GONE);
                            arrowLeft.setVisibility(View.GONE);
                            tvEmptyMsg.setVisibility(View.VISIBLE);
                            findViewById(R.id.msg_card_view).setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        error.printStackTrace();
                    }
                }

        );

    }

    private void bindMessage() {

        findViewById(R.id.msg_header).setVisibility(View.VISIBLE);
        findViewById(R.id.msg_card_view).setVisibility(View.VISIBLE);

        if (mMsg.get(msgPosition).getCreator().getId().equals(ILMCApp.getUser().getId()))
            ivMsgDelete.setVisibility(View.VISIBLE);
        else
            ivMsgDelete.setVisibility(View.GONE);

        if (mMsg.get(msgPosition) != null)
            Picasso.with(this)
                    .load(mMsg.get(msgPosition).getCreator().getPicture())
                    .placeholder(R.drawable.ic_img_empty_avatar)
                    .error(R.drawable.ic_img_empty_avatar)
                    .fit()
                    .centerCrop()
                    .into(ivMsgCreator);

        tvMsgDesc.setText(mMsg.get(msgPosition).getText());
        tvMsgCreator.setText(mMsg.get(msgPosition).getCreator().getFullName());

        /**
         * Date gestion
         */

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.FRANCE);
        Date date = null;
        try {
            date = sdf.parse(mMsg.get(msgPosition).getCreatedAt());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DateHelper dateHelper = new DateHelper(date);
        String sDate = "Le " + dateHelper.getDay() + " " + dateHelper.getMonthLongName() + " " + dateHelper.getYear()
                + " à " + dateHelper.getHourOnly() + "h" + dateHelper.getMinuteOnly();
        tvMsgDate.setText(sDate);

    }
}