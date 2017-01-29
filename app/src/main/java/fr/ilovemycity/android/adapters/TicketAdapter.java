package fr.ilovemycity.android.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.ilovemycity.android.ILMCApp;
import fr.ilovemycity.android.R;
import fr.ilovemycity.android.controllers.TicketActivity;
import fr.ilovemycity.android.events.OpenTicketEvent;
import fr.ilovemycity.android.models.Ticket;
import fr.ilovemycity.android.utils.ToastUtils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

@SuppressWarnings("UnusedDeclaration")
public class TicketAdapter extends RecyclerView
        .Adapter<TicketAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "TicketAdapter";
    private static ArrayList<Ticket> mDataset;
    private static MyClickListener myClickListener;
    private static int mType;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {

        @BindView(R.id.tv_title) TextView        title;
        @BindView(R.id.tv_city) TextView         city;
        @BindView(R.id.iv_ticket) ImageView      image;
        @BindView(R.id.creator_img) ImageView    creatorImage;
        @BindView(R.id.like_btn) ImageView       likeBtn;
        @BindView(R.id.iv_status) ImageView      ivStatus;

        Context context;

        public DataObjectHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            context = itemView.getContext();

            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        TicketAdapter.myClickListener = myClickListener;
    }

    public TicketAdapter(ArrayList<Ticket> myDataset, int type) {
        mDataset = myDataset;
        mType = type;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view;

        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_ticket, parent, false);

        return new DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, final int position) {

        final Ticket ticket = mDataset.get(position);

        holder.title.setText(ticket.getTitle());
        holder.city.setText(ticket.getAddress());
        
        if (ticket.isVote())
            holder.likeBtn.setImageDrawable(ContextCompat.getDrawable(holder.context, R.drawable.ic_like));
        
        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!ticket.isVote()) {
                    ILMCApp.getAPI(holder.context).voteTicket(ILMCApp.getToken(), ticket.getId(), ticket, new Callback<Ticket>() {
                        @Override
                        public void success(Ticket ticket, Response response) {
                            ToastUtils.makeToast(holder.context, "Merci pour votre vote !", 2000);
                            holder.likeBtn.setImageDrawable(ContextCompat.getDrawable(holder.context, R.drawable.ic_like));

                            ticket.setVotes(ticket.getVotes() + 1);
                            ticket.setVote(true);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            error.printStackTrace();
                            ToastUtils.makeToast(holder.context, "Une erreur est survenue !", 2000);
                        }
                    });
                } else {
                    ILMCApp.getAPI(holder.context).unvoteTicket(ILMCApp.getToken(), ticket.getId(), new Callback<Ticket>() {
                        @Override
                        public void success(Ticket ticket, Response response) {
                            ToastUtils.makeToast(holder.context, "Vous avez retiré votre vote !", 2000);
                            holder.likeBtn.setImageDrawable(ContextCompat.getDrawable(holder.context, R.drawable.ic_like_empty));

                            ticket.setVotes(ticket.getVotes() - 1);
                            ticket.setVote(false);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            error.printStackTrace();
                            ToastUtils.makeToast(holder.context, "Une erreur est survenue !", 2000);
                        }
                    });
                }
            }
        });

        switch (ticket.getStatus()) {
            case "o":
                holder.ivStatus.setImageDrawable(ContextCompat.getDrawable(holder.context, R.drawable.status_open_anim));
                break;
            case "p":
                holder.ivStatus.setImageDrawable(ContextCompat.getDrawable(holder.context, R.drawable.status_pending_anim));
                break;
            case "c":
                holder.ivStatus.setImageDrawable(ContextCompat.getDrawable(holder.context, R.drawable.status_close_anim));
                break;
            default:
                break;
        }

        AnimationDrawable frameAnimation = (AnimationDrawable) holder.ivStatus.getDrawable();
        frameAnimation.start();

        // Using picasso to load ticket image from URL
        if (ticket.getPictures() != null && !ticket.getPictures().get(0).equals(""))
            Picasso.with(ILMCApp.getContext())
                    .load(ticket.getPictures().get(0))
                    .fit()
                    .centerCrop()
                    .into(holder.image);
        else
            holder.image.setImageDrawable(ContextCompat.getDrawable(ILMCApp.getContext(), R.drawable.empty_ticket));

        if (ticket.getCreator() != null) {
            holder.creatorImage.setVisibility(View.VISIBLE);
            if (ticket.getCreator().getPicture() != null)
                Picasso.with(ILMCApp.getContext())
                        .load(ticket.getCreator().getPicture())
                        .fit()
                        .centerCrop()
                        .into(holder.creatorImage);
            else
                holder.creatorImage.setImageDrawable(ContextCompat.getDrawable(ILMCApp.getContext(), R.drawable.ic_img_empty_avatar));
        }
        else
            holder.creatorImage.setVisibility(View.GONE);

        setOnItemClickListener(new MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (mDataset != null && mDataset.get(position) != null) {
                    final Intent intent = new Intent(ILMCApp.getContext(), TicketActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ILMCApp.getContext().startActivity(intent);
                    EventBus.getDefault().postSticky(new OpenTicketEvent(mDataset.get(position)));
                }
            }
        });
    }

    public void addItem(Ticket ticket, int index) {
        mDataset.add(index, ticket);
        notifyItemInserted(index);
    }

    public void deleteItem(int index, Context context) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    public void clear() {
        mDataset.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(ArrayList<Ticket> list) {
        mDataset.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    interface MyClickListener {
        void onItemClick(int position, View v);
    }
}
