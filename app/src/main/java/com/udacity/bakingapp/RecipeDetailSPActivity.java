package com.udacity.bakingapp;

import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.udacity.bakingapp.adapter.FragmentAdapter;
import com.udacity.bakingapp.pojo.Ingredient;
import com.udacity.bakingapp.pojo.Recipe;
import com.udacity.bakingapp.pojo.Step;
import com.udacity.bakingapp.widget.WidgetProvider;

import java.util.ArrayList;

/**
 * Created by farhan on 9/3/17.
 */

public class RecipeDetailSPActivity extends AppCompatActivity {

    private int ids=0, id;

    private SharedPreferences shared;
    private LinearLayout linearLayout;

    final static String KEY_POSITION = "POSITION";
    final static String ARRAY_STEP = "STEPLIST";
    final static String FRAGMENT = "FRAGMENT";

    private RecipeDetailFragment recipeDetailFragment = new RecipeDetailFragment();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail_sp);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        textView = (TextView) findViewById(R.id.text);
        scrollView = (NestedScrollView) findViewById(R.id.scrollView);

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

            Intent updateWidget = new Intent(RecipeDetailSPActivity.this, WidgetProvider.class);
            updateWidget.setAction("update_widget");
            PendingIntent pending = PendingIntent.getBroadcast(RecipeDetailSPActivity.this, 0, updateWidget, PendingIntent.FLAG_CANCEL_CURRENT);
            pending.send();
        }catch (Exception e){
            Log.d("EXCEPTION",""+e);
        }

        for (int i = 0; i < ingredientList.size(); i++) {
            temp.append((i + 1) + ". " + ingredientList.get(i).getIngredient()
                    + "\t(" + ingredientList.get(i).getQuantity() + " " + ingredientList.get(i).getMeasure() + ")\n");
        }
        textView.setText(temp + "");
        FragmentAdapter adapter1 = new FragmentAdapter(RecipeDetailSPActivity.this, stepList, temp + "");
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(RecipeDetailSPActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager1);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter1);

        destroyActivity();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_POSITION, ids);
        outState.putParcelableArrayList(ARRAY_STEP, stepList);
        outState.putParcelableArrayList(ARRAY_STEP+"2", ingredientList);

        SharedPreferences prefs = getSharedPreferences("BAKINGAPP", Context.MODE_PRIVATE);
        prefs.edit().putInt("POSITION", ids).apply();


        outState.putIntArray("ARTICLE_SCROLL_POSITION", new int[]{scrollView.getScrollX(), scrollView.getScrollY()});
        outState.putParcelableArrayList("ARRAYSTEP",stepList);
        outState.putParcelableArrayList("ARRAYINGREDIENTS",ingredientList);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState!=null){
            ids = savedInstanceState.getInt(KEY_POSITION);
            stepList = savedInstanceState.getParcelableArrayList(ARRAY_STEP);
            ingredientList = savedInstanceState.getParcelableArrayList(ARRAY_STEP+"2");

            ids = savedInstanceState.getInt(KEY_POSITION,0);

            final int[] position = savedInstanceState.getIntArray("ARTICLE_SCROLL_POSITION");
            stepList = savedInstanceState.getParcelableArrayList("ARRAYSTEP");
            stepList = savedInstanceState.getParcelableArrayList("ARRAYINGREDIENTS");
            if (position != null)
                scrollView.post(new Runnable() {
                    public void run() {
                        scrollView.scrollTo(position[0], position[1]);
                    }
                });

        }
    }

    public void destroyActivity() {
        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action.equals(getString(R.string.receiverBakingApp))) {
                    int versionNameIndex = intent.getExtras().getInt(getString(R.string.versionNameIndex));
                    ids = versionNameIndex;
                    SharedPreferences prefs = getSharedPreferences("BAKINGAPP", Context.MODE_PRIVATE);
                    prefs.edit().putInt("POSITION", ids).apply();
                    if (!getResources().getBoolean(R.bool.isTablet)) {
                        Intent intent1 = new Intent(RecipeDetailSPActivity.this, RecipeVideoActivity.class);
                        intent1.putExtra("ARRAY",stepList);
                        intent1.putExtra("POSISIS",versionNameIndex);
                        Log.d("HASIL","PINDAH");
                        startActivity(intent1);
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