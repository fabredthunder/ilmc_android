package fr.ilovemycity.android.utils;

import android.os.AsyncTask;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

import fr.ilovemycity.android.ILMCApp;
import fr.ilovemycity.android.R;
import fr.ilovemycity.android.models.Ticket;
import fr.ilovemycity.android.models.User;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Fab on 20/06/2016.
 * All rights reserved
 */
public class ImageUtils {

    private static final int PROFILE_PICTURE = 0;
    private static final int TICKET_PICTURE = 1;

    private static String lastUploadUrl;

    private static Cloudinary   mCloudinary;
    private static InputStream  mInputStream;

    public static void uploadImage(InputStream inputStream, int type, String ticketId) {

        mCloudinary = ILMCApp.getCloundinary();
        mInputStream = inputStream;

        UploadTask uploadTask = new UploadTask(type, ticketId);
        uploadTask.execute();
    }

    private static class UploadTask extends AsyncTask<Void, Void, Void> {

        int type;
        String ticketId;

        UploadTask(int type, String ticketId){
            this.type = type;
            this.ticketId = ticketId;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Map result = mCloudinary.uploader().upload(mInputStream, ObjectUtils.emptyMap());
                lastUploadUrl = result.get("url").toString();

                if (type == PROFILE_PICTURE)
                    updatePicture(lastUploadUrl);
                else if (type == TICKET_PICTURE)
                    updateTicketPicture(lastUploadUrl, ticketId);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private static void updatePicture(final String newPic) {

        User updatedUser = new User();
        updatedUser.setPicture(newPic);

        ILMCApp.getAPI(ILMCApp.getContext()).updateMe(ILMCApp.getToken(), updatedUser, new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                ILMCApp.updateUser(user);
                lastUploadUrl = null;
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("FAIL : " + error.toString());
            }
        });

    }

    private static void updateTicketPicture(final String newPic, String ticketId) {

        Ticket updatedTicket = new Ticket();

        ArrayList<String> pictures = new ArrayList<>();
        pictures.add(0, newPic);
        updatedTicket.setPictures(pictures);

        ILMCApp.getAPI(ILMCApp.getContext()).updateTicket(ILMCApp.getToken(), ticketId, updatedTicket, new Callback<Ticket>() {
            @Override
            public void success(Ticket ticket, Response response) {
                lastUploadUrl = null;
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });

    }

    public static void  fillImageView(String url, ImageView imageView) {

        Glide.with(ILMCApp.getContext())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .error(R.drawable.ic_img_empty_avatar)
                .placeholder(R.drawable.ic_img_empty_avatar)
                .into(imageView);

    }

    public static void  fillImageView(File file, ImageView imageView) {

        Glide.with(ILMCApp.getContext())
                .load(file)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .placeholder(R.drawable.ic_img_empty_avatar)
                .error(R.drawable.ic_img_empty_avatar)
                .into(imageView);

    }

}
