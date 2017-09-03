package com.udacity.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.udacity.bakingapp.adapter.RecyclerAdapter;
import com.udacity.bakingapp.adapter.RecyclerDetailAdapter;
import com.udacity.bakingapp.pojo.Ingredient;
import com.udacity.bakingapp.pojo.Recipe;
import com.udacity.bakingapp.pojo.Step;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by farhan on 9/3/17.
 */

public class RecipeDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Recipe recipe;
    private RecyclerDetailAdapter adapter;

    private List<Ingredient> ingredientList = new ArrayList<Ingredient>();
    private List<Recipe> recipeList = new ArrayList<Recipe>();
    private List<Step> stepList = new ArrayList<Step>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        Intent intent = getIntent();
        if (intent != null) {
            setTitle(intent.getStringExtra("title"));
            if (intent.hasExtra("parcel")) {
                recipe = intent.getParcelableExtra("parcel");
            } else {
                finish();
            }
        }
        stepList = recipe.steps;
        adapter = new RecyclerDetailAdapter(RecipeDetailActivity.this,stepList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
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