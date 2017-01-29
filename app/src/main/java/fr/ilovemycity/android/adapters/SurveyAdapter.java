package fr.ilovemycity.android.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.ilovemycity.android.ILMCApp;
import fr.ilovemycity.android.R;
import fr.ilovemycity.android.events.RefreshSurveysEvent;
import fr.ilovemycity.android.models.Survey;
import fr.ilovemycity.android.webservice.bundles.VoteBundle;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

@SuppressWarnings("UnusedDeclaration")
public class SurveyAdapter extends RecyclerView
        .Adapter<SurveyAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "SurveyAdapter";
    private static ArrayList<Survey> mDataset;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {

        @BindView(R.id.tv_title) TextView        question;
        @BindView(R.id.yes) TextView  yes;
        @BindView(R.id.no) TextView  no;

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
            //myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        SurveyAdapter.myClickListener = myClickListener;
    }

    public SurveyAdapter(ArrayList<Survey> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view;

        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_survey, parent, false);

        return new DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, final int position) {

        final Survey survey = mDataset.get(position);

        holder.question.setText(survey.getQuestion());
        holder.yes.setText(String.format("OUI (%s)", survey.getYes()));
        holder.no.setText(String.format("NON (%s)", survey.getNo()));

        holder.yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answer(holder.context, survey, "y");
            }
        });


        holder.no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answer(holder.context, survey, "n");
            }
        });
    }

    public void answer(final Context context, Survey survey, String answer) {
        VoteBundle voteBundle = new VoteBundle();
        voteBundle.setVote(answer);

        ILMCApp.getAPI(context).answerSurvey(ILMCApp.getToken(), survey.getId(), voteBundle, new Callback<Survey>() {

            @Override
            public void success(Survey survey, Response response) {
                Toast.makeText(context, "Merci pour votre réponse", Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(new RefreshSurveysEvent());
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    public void addItem(Survey survey, int index) {
        mDataset.add(index, survey);
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
    public void addAll(ArrayList<Survey> list) {
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
