package com.udacity.bakingapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.udacity.bakingapp.R;
import com.udacity.bakingapp.RecipeDetailFragActivity;
import com.udacity.bakingapp.RecipeDetailActActivity;
import com.udacity.bakingapp.pojo.Ingredient;
import com.udacity.bakingapp.pojo.Recipe;
import com.udacity.bakingapp.pojo.Step;

import java.util.ArrayList;

/**
 * Created by farhan on 6/30/17.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private ArrayList<Recipe> movieData;
    private ArrayList<Step> steps;
    private ArrayList<Ingredient> ingredients;
    private Context context;

    public RecyclerAdapter(Context context, ArrayList<Recipe> movieData, ArrayList<Ingredient> ingredients, ArrayList<Step> steps) {
        this.movieData = movieData;
        this.context = context;
        this.steps = steps;
        this.ingredients = ingredients;
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_recycler, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder viewHolder, final int i) {

        if (!movieData.get(i).getImage().isEmpty()){
            Picasso.with(context).load(movieData.get(i).getImage()).into(viewHolder.imageView);
        }else{
            Picasso.with(context).load(R.drawable.ic_no_video).into(viewHolder.imageView);
        }
        viewHolder.textView1.setText(movieData.get(i).getName());
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (context.getResources().getBoolean(R.bool.isTablet)){
                    Intent intent = new Intent(context, RecipeDetailFragActivity.class);
                    intent.putExtra("title",movieData.get(i).getName());
                    intent.putParcelableArrayListExtra("parcel",movieData);
                    intent.putExtra("id",i);

                    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    Gson gson = new Gson();
                    String json1 = gson.toJson(steps);
                    String json2 = gson.toJson(ingredients);
                    editor.putString("JSON1", json1);
                    editor.putString("JSON2", json2);
                    editor.commit();

                    Log.d("HASIL",""+json1);
                    context.startActivity(intent);
                }else{
                    Intent intent = new Intent(context, RecipeDetailActActivity.class);
                    intent.putExtra("title",movieData.get(i).getName());
                    intent.putParcelableArrayListExtra("parcel",movieData);
                    intent.putExtra("id",i);

                    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    Gson gson = new Gson();
                    String json1 = gson.toJson(steps);
                    String json2 = gson.toJson(ingredients);
                    editor.putString("JSON1", json1);
                    editor.putString("JSON2", json2);
                    editor.commit();

                    Log.d("HASIL",""+json1);
                    context.startActivity(intent);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return movieData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textView1;
        private ImageView imageView;
        private CardView cardView;

        public ViewHolder(View view) {
            super(view);
            textView1 = (TextView) view.findViewById(R.id.textViewTitle);
            cardView = (CardView) view.findViewById(R.id.cardViewMovie);
            imageView = (ImageView) view.findViewById(R.id.imageView);

        }
    }

}

