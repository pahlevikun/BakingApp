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
import com.udacity.bakingapp.RecipeDetailActivity;
import com.udacity.bakingapp.StepActivity;
import com.udacity.bakingapp.pojo.Recipe;
import com.udacity.bakingapp.pojo.Step;

import java.util.List;

/**
 * Created by farhan on 6/30/17.
 */

public class RecyclerDetailAdapter extends RecyclerView.Adapter<RecyclerDetailAdapter.ViewHolder> {
    private List<Step> movieData;
    private Context context;
    private Step recipe;

    public RecyclerDetailAdapter(Context context, List<Step> movieData) {
        this.movieData = movieData;
        this.context = context;
    }

    @Override
    public RecyclerDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_movie, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerDetailAdapter.ViewHolder viewHolder, final int i) {

        viewHolder.textView1.setText(movieData.get(i).getId() + " " + movieData.get(i).getShortDescription());
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recipe = new Step(movieData.get(i).getId(),
                        movieData.get(i).getShortDescription(),
                        movieData.get(i).getDescription(),
                        movieData.get(i).getVideoURL(),
                        movieData.get(i).getThumbnailURL());
                Intent intent = new Intent(context, StepActivity.class);
                intent.putExtra("title", "Step-" + movieData.get(i).getId());
                intent.putExtra("parcel", recipe);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView1;
        private CardView cardView;

        public ViewHolder(View view) {
            super(view);
            textView1 = (TextView) view.findViewById(R.id.textViewTitle);
            cardView = (CardView) view.findViewById(R.id.cardViewMovie);
        }
    }

}

