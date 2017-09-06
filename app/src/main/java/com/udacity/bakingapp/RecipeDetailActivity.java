package com.udacity.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.bakingapp.adapter.RecyclerAdapter;
import com.udacity.bakingapp.adapter.RecyclerDetailAdapter;
import com.udacity.bakingapp.pojo.Ingredient;
import com.udacity.bakingapp.pojo.Recipe;
import com.udacity.bakingapp.pojo.Step;
import com.udacity.bakingapp.widget.UpdateBakingService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by farhan on 9/3/17.
 */

public class RecipeDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Recipe recipe;
    private RecyclerDetailAdapter adapter;

    private ImageView imageView;
    private TextView textView;

    private int id;

    private StringBuffer temp = new StringBuffer();

    private ArrayList<Ingredient> ingredientList = new ArrayList<Ingredient>();
    private ArrayList<Recipe> recipeList = new ArrayList<Recipe>();
    private ArrayList<Step> stepList = new ArrayList<Step>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        imageView = (ImageView) findViewById(R.id.imageViewIngredients);
        textView = (TextView) findViewById(R.id.textViewIngredients);
        Intent intent = getIntent();
        if (intent != null) {
            setTitle(intent.getStringExtra("title"));
            if (intent.hasExtra("parcel")) {
                //recipe = intent.getParcelableExtra("parcel");
                recipeList = intent.getParcelableArrayListExtra("parcel");
                id = intent.getIntExtra("id",0);
            } else {
                finish();
            }
        }
        stepList = recipeList.get(id).getSteps();
        ingredientList = recipeList.get(id).getIngredients();

        ArrayList<String> recipeIngredientsForWidgets= new ArrayList<>();

        for (int i = 0; i< ingredientList.size(); i++){
            temp.append((i+1)+". "+ingredientList.get(i).getIngredient()
                    +"\n\t\t"+ingredientList.get(i).getQuantity()+" "+ingredientList.get(i).getMeasure()+"\n\n");
            recipeIngredientsForWidgets.add((i+1)+". "+ingredientList.get(i).getIngredient()
                    +"\n\t\t"+ingredientList.get(i).getQuantity()+" "+ingredientList.get(i).getMeasure());
        }
        textView.setText(removeLastChar(temp+""));
        if (recipeList.get(id).getImage().isEmpty()){
            imageView.setImageResource(R.drawable.ic_novideo);
        }else{
            Picasso.with(this)
                    .load(recipeList.get(id).getImage())
                    .into(imageView);
        }
        adapter = new RecyclerDetailAdapter(RecipeDetailActivity.this,stepList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        //update widget
        UpdateBakingService.startBakingService(RecipeDetailActivity.this,recipeIngredientsForWidgets);
    }

    private static String removeLastChar(String str) {
        return str.substring(0, str.length() - 2);
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