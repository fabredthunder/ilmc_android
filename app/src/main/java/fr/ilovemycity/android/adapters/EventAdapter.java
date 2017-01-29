package fr.ilovemycity.android.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.ilovemycity.android.R;
import fr.ilovemycity.android.models.Event;

@SuppressWarnings("UnusedDeclaration")
public class EventAdapter extends RecyclerView
        .Adapter<EventAdapter
        .DataObjectHolder> {
    private static ArrayList<Event> mDataset;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {

        @BindView(R.id.tv_title) TextView        title;
        @BindView(R.id.tv_description) TextView  desc;
        @BindView(R.id.tv_where) TextView  where;
        @BindView(R.id.iv_event) ImageView      image;

        Context context;

        public DataObjectHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            context = itemView.getContext();

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        EventAdapter.myClickListener = myClickListener;
    }

    public EventAdapter(ArrayList<Event> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view;

        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_event, parent, false);

        return new DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, final int position) {

        final Event event = mDataset.get(position);

        holder.title.setText(event.getName());
        holder.desc.setText(event.getDescription());
        holder.where.setText(String.format("à %s", event.getAddress()));

        if (event.getPicture() != null && !event.getPicture().equals(""))
            Glide.with(holder.context)
                    .load(event.getPicture())
                    .centerCrop()
                    .crossFade()
                    .into(holder.image);
    }

    public void addItem(Event event, int index) {
        mDataset.add(index, event);
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
    public void addAll(ArrayList<Event> list) {
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
