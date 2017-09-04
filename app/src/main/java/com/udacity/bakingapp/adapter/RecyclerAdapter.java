package com.udacity.bakingapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.udacity.bakingapp.MainActivity;
import com.udacity.bakingapp.R;
import com.udacity.bakingapp.RecipeDetailActivity;
import com.udacity.bakingapp.pojo.Recipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by farhan on 6/30/17.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private ArrayList<Recipe> movieData;
    private Context context;
    private Recipe recipe;

    public RecyclerAdapter(Context context, ArrayList<Recipe> movieData) {
        this.movieData = movieData;
        this.context = context;
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_movie, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder viewHolder, final int i) {

        viewHolder.textView1.setText(movieData.get(i).getName());
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*recipe = new Recipe(movieData.get(i).getId(),
                                    movieData.get(i).getName(),
                                    movieData.get(i).getIngredients(),
                                    movieData.get(i).getSteps(),
                                    movieData.get(i).getServings(),
                                    movieData.get(i).getImage());*/
                Intent intent = new Intent(context, RecipeDetailActivity.class);
                intent.putExtra("title",movieData.get(i).getName());
                intent.putParcelableArrayListExtra("parcel",movieData);
                intent.putExtra("id",i);
                //intent.putExtra("parcel",recipe);
                context.startActivity(intent);
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

