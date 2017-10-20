package com.udacity.bakingapp;

import android.app.Fragment;
import android.app.ListFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.udacity.bakingapp.adapter.FragmentAdapter;
import com.udacity.bakingapp.adapter.RecyclerAdapter;
import com.udacity.bakingapp.pojo.Ingredient;
import com.udacity.bakingapp.pojo.Recipe;
import com.udacity.bakingapp.pojo.Step;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.udacity.bakingapp.RecipeDetailFragment.ARRAY_STEP;

public class RecipeListFragment extends Fragment {

    private ArrayList<Step> stepList = new ArrayList<Step>();
    private ArrayList<Ingredient> ingredientList = new ArrayList<Ingredient>();
    private ArrayList<Recipe> recipeList = new ArrayList<Recipe>();
    private String[] versionName;
    private RecyclerView recyclerView;
    private FragmentAdapter adapter;
    private LinearLayoutManager layoutManager;
    public StringBuffer temp = new StringBuffer();

    public RecipeListFragment() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState!=null){
            final int position = Integer.parseInt(savedInstanceState.getString("position"));

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    recyclerView.scrollToPosition(position);
                }
            }, 100);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int position = 0;
        if (layoutManager != null) {
            position = layoutManager.findFirstVisibleItemPosition();
        }
        outState.putString("position",""+position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        new Handler().postDelayed(new Thread() {
            @Override
            public void run() {
                stepList = ((RecipeDetailActivity)getActivity()).stepList;
                ingredientList = ((RecipeDetailActivity)getActivity()).ingredientList;

                for (int i = 0; i < ingredientList.size(); i++) {
                    temp.append((i + 1) + ". " + ingredientList.get(i).getIngredient()
                            + "\t(" + ingredientList.get(i).getQuantity() + " " + ingredientList.get(i).getMeasure() + ")\n");
                }
                adapter = new FragmentAdapter(getActivity(),stepList,temp+"");

                layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(adapter);
            }
        }, 50);


        return view;
    }

}
