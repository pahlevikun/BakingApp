package com.udacity.bakingapp;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.bakingapp.adapter.FragmentAdapter;
import com.udacity.bakingapp.pojo.Ingredient;
import com.udacity.bakingapp.pojo.Recipe;
import com.udacity.bakingapp.pojo.Step;

import java.util.ArrayList;

public class RecipeListFragment extends Fragment {

    private ArrayList<Step> stepList = new ArrayList<Step>();
    private ArrayList<Ingredient> ingredientList = new ArrayList<Ingredient>();
    private ArrayList<Recipe> recipeList = new ArrayList<Recipe>();
    private String[] versionName;
    private RecyclerView recyclerView;
    private TextView textView;
    private FragmentAdapter adapter;
    private LinearLayoutManager layoutManager;
    public StringBuffer temp = new StringBuffer();
    private BroadcastReceiver broadcastReceiver;
    private NestedScrollView scrollView;

    public RecipeListFragment() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            final int[] position = savedInstanceState.getIntArray("ARTICLE_SCROLL_POSITION");
            stepList = savedInstanceState.getParcelableArrayList("ARRAYSTEP");
            stepList = savedInstanceState.getParcelableArrayList("ARRAYINGREDIENTS");
            Log.d("HASIL","POSISI AMBIL "+position[0]+","+position[1]);
            if (position != null)
                scrollView.post(new Runnable() {
                    public void run() {
                        scrollView.scrollTo(position[0], position[1]);
                    }
                });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray("ARTICLE_SCROLL_POSITION", new int[]{scrollView.getScrollX(), scrollView.getScrollY()});
        outState.putParcelableArrayList("ARRAYSTEP",stepList);
        outState.putParcelableArrayList("ARRAYINGREDIENTS",ingredientList);
        Log.d("HASIL","POSISI SIMPAN "+scrollView.getScrollX()+","+scrollView.getScrollY());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        textView = (TextView) view.findViewById(R.id.text);
        scrollView = (NestedScrollView) view.findViewById(R.id.scrollView);
        new Handler().postDelayed(new Thread() {
            @Override
            public void run() {

                if (savedInstanceState != null) {
                    final int[] position = savedInstanceState.getIntArray("ARTICLE_SCROLL_POSITION");
                    stepList = savedInstanceState.getParcelableArrayList("ARRAYSTEP");
                    stepList = savedInstanceState.getParcelableArrayList("ARRAYINGREDIENTS");
                    Log.d("HASIL","POSISI AMBIL "+position[0]+","+position[1]);
                    if (position != null)
                        scrollView.post(new Runnable() {
                            public void run() {
                                scrollView.scrollTo(position[0], position[1]);
                            }
                        });
                }

                stepList = ((RecipeDetailFragActivity) getActivity()).stepList;
                ingredientList = ((RecipeDetailFragActivity) getActivity()).ingredientList;

                for (int i = 0; i < ingredientList.size(); i++) {
                    temp.append((i + 1) + ". " + ingredientList.get(i).getIngredient()
                            + "\t(" + ingredientList.get(i).getQuantity() + " " + ingredientList.get(i).getMeasure() + ")\n");
                }
                textView.setText(temp + "");
                FragmentAdapter adapter1 = new FragmentAdapter(getActivity(), stepList, temp + "");
                LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(layoutManager1);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(adapter1);

            }
        }, 50);

        destroyActivity();

        return view;
    }

    public void destroyActivity() {
        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action.equals(getString(R.string.receiverBakingApp))) {
                    final int versionNameIndex = intent.getExtras().getInt(getString(R.string.versionNameIndex));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.scrollToPosition(versionNameIndex);
                            Log.d("POSITION", "dest " + versionNameIndex);
                        }
                    }, 100);
                }
            }
        };
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(getString(R.string.receiverBakingApp)));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            getActivity().unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }

}
