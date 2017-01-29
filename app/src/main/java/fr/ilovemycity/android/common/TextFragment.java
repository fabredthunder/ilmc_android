package fr.ilovemycity.android.common;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fr.ilovemycity.android.R;

/**
 * Created by Fab on 24/10/15.
 */
public class TextFragment extends Fragment {

    protected TextView tvTitle;
    protected TextView tvContent;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_text, container, false);

        tvTitle = (TextView) root.findViewById(R.id.tv_title);
        tvContent = (TextView) root.findViewById(R.id.tv_content);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            tvTitle.setText(bundle.getString("title"));
            tvContent.setText(bundle.getString("content"));
        }

        return root;
    }

}
