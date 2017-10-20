package com.udacity.bakingapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.bakingapp.R;
import com.udacity.bakingapp.pojo.Step;

import java.util.ArrayList;

/**
 * Created by farhan on 6/30/17.
 */

public class FragmentAdapter extends RecyclerView.Adapter<FragmentAdapter.ViewHolder> {
    private ArrayList<Step> movieData;
    private Context context;
    private String recipe;

    public FragmentAdapter(Context context, ArrayList<Step> movieData, String recipe) {
        this.movieData = movieData;
        this.context = context;
        this.recipe = recipe;
    }

    @Override
    public FragmentAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_recycler, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FragmentAdapter.ViewHolder viewHolder, final int i) {
            viewHolder.textView1.setText(movieData.get(i).getId() +". "+ movieData.get(i).getShortDescription());
            viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context.getString(R.string.receiverBakingApp));
                    intent.putExtra(context.getString(R.string.versionNameIndex),i);
                    context.sendBroadcast(intent);
                }
            });

    }

    @Override
    public int getItemCount() {
        return movieData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textView1;
        private CardView cardView;

        public ViewHolder(View view) {
            super(view);
            textView1 = (TextView) view.findViewById(R.id.textViewTitle);
            cardView = (CardView) view.findViewById(R.id.cardViewMovie);

        }
    }

}

