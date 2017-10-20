package com.udacity.bakingapp;

import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.udacity.bakingapp.pojo.Ingredient;
import com.udacity.bakingapp.pojo.Recipe;
import com.udacity.bakingapp.pojo.Step;
import com.udacity.bakingapp.widget.WidgetProvider;

import java.util.ArrayList;

/**
 * Created by farhan on 9/3/17.
 */

public class RecipeDetailActivity extends AppCompatActivity /*implements OnRecipeSelect */{

    private int ids=0, id;
    public StringBuffer temp = new StringBuffer();

    public ArrayList<Ingredient> ingredientList = new ArrayList<Ingredient>();
    private ArrayList<Recipe> recipeList = new ArrayList<Recipe>();
    public ArrayList<Step> stepList = new ArrayList<Step>();

    private SharedPreferences shared;
    private LinearLayout linearLayout;

    final static String KEY_POSITION = "POSITION";
    final static String ARRAY_STEP = "STEPLIST";
    final static String FRAGMENT = "FRAGMENT";

    private RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();

    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        Intent intent = getIntent();
        if (intent != null) {
            setTitle(intent.getStringExtra("title"));
            if (intent.hasExtra("parcel")) {
                recipeList = intent.getParcelableArrayListExtra("parcel");
                id = intent.getIntExtra("id", 0);
            } else {
                finish();
            }
        }
        stepList = recipeList.get(id).getSteps();
        ingredientList = recipeList.get(id).getIngredients();



        for (int i = 0; i < ingredientList.size(); i++) {
            temp.append((i + 1) + ". " + ingredientList.get(i).getIngredient()
                    + "\t(" + ingredientList.get(i).getQuantity() + " " + ingredientList.get(i).getMeasure() + ")\n");
        }
        try{
            shared = getSharedPreferences("APP", MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            editor.putString("RECIPE", temp+"");
            editor.apply();
            Log.d("HASILSHARED",""+temp);

            Intent updateWidget = new Intent(RecipeDetailActivity.this, WidgetProvider.class);
            updateWidget.setAction("update_widget");
            PendingIntent pending = PendingIntent.getBroadcast(RecipeDetailActivity.this, 0, updateWidget, PendingIntent.FLAG_CANCEL_CURRENT);
            pending.send();
        }catch (Exception e){
            Log.d("EXCEPTION",""+e);
        }

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                recipeDetailFragment.setDescription(ids);
            } else {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(RecipeDetailFragment.ARRAY_STEP, stepList);
                RecipeListFragment recipeListFragment = new RecipeListFragment();
                recipeListFragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, recipeListFragment)
                        .commit();
            }
        }

        destroyActivity();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_POSITION, ids);
        outState.putParcelableArrayList(ARRAY_STEP, stepList);
        outState.putParcelableArrayList(ARRAY_STEP+"2", ingredientList);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState!=null){
            ids = savedInstanceState.getInt(KEY_POSITION);
            stepList = savedInstanceState.getParcelableArrayList(ARRAY_STEP);
            ingredientList = savedInstanceState.getParcelableArrayList(ARRAY_STEP+"2");

            ids = savedInstanceState.getInt(KEY_POSITION,0);

            RecipeDetailFragment recipeDetailFragment = (RecipeDetailFragment) getFragmentManager().findFragmentById(R.id.description_fragment);
            if (recipeDetailFragment != null ) {
                recipeDetailFragment.setDescription(ids);
            } else {
                RecipeDetailFragment newDesriptionFragment = new RecipeDetailFragment();
                Bundle args = new Bundle();
                args.putInt(KEY_POSITION, ids);
                args.putParcelableArrayList(ARRAY_STEP, stepList);
                newDesriptionFragment.setArguments(args);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, newDesriptionFragment,FRAGMENT);
                fragmentTransaction.commit();
            }

        }
    }

    public void destroyActivity() {
        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action.equals(getString(R.string.receiverBakingApp))) {
                    int versionNameIndex = intent.getExtras().getInt(getString(R.string.versionNameIndex));

                    RecipeDetailFragment recipeDetailFragment = (RecipeDetailFragment) getFragmentManager().findFragmentById(R.id.description_fragment);
                    ids = versionNameIndex;
                    if (recipeDetailFragment != null ) {
                        recipeDetailFragment.setDescription(versionNameIndex);
                    } else {
                        RecipeDetailFragment newDesriptionFragment = new RecipeDetailFragment();
                        Bundle args = new Bundle();
                        args.putInt(KEY_POSITION, versionNameIndex);
                        args.putParcelableArrayList(ARRAY_STEP, stepList);
                        newDesriptionFragment.setArguments(args);
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, newDesriptionFragment,FRAGMENT);
                        fragmentTransaction.commit();
                    }
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter(getString(R.string.receiverBakingApp)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}