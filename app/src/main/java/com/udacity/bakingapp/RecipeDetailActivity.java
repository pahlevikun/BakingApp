package com.udacity.bakingapp;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.udacity.bakingapp.adapter.RecyclerDetailAdapter;
import com.udacity.bakingapp.config.OnVersionNameSelectionChangeListener;
import com.udacity.bakingapp.pojo.Ingredient;
import com.udacity.bakingapp.pojo.Recipe;
import com.udacity.bakingapp.pojo.Step;
import com.udacity.bakingapp.widget.UpdateBakingService;

import java.util.ArrayList;

/**
 * Created by farhan on 9/3/17.
 */

public class RecipeDetailActivity extends AppCompatActivity implements OnVersionNameSelectionChangeListener {

    private int id;

    public StringBuffer temp = new StringBuffer();

    private static String STACK_RECIPE_DETAIL = "STACK_RECIPE_DETAIL";

    private ArrayList<Ingredient> ingredientList = new ArrayList<Ingredient>();
    private ArrayList<Recipe> recipeList = new ArrayList<Recipe>();
    public ArrayList<Step> stepList = new ArrayList<Step>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent intent = getIntent();
        if (intent != null) {
            setTitle(intent.getStringExtra("title"));
            if (intent.hasExtra("parcel")) {
                //recipe = intent.getParcelableExtra("parcel");
                recipeList = intent.getParcelableArrayListExtra("parcel");
                id = intent.getIntExtra("id", 0);
            } else {
                finish();
            }
        }
        stepList = recipeList.get(id).getSteps();
        ingredientList = recipeList.get(id).getIngredients();

        Log.d("HASIL", "" + stepList.size());
        ArrayList<String> recipeIngredientsForWidgets = new ArrayList<>();

        for (int i = 0; i < ingredientList.size(); i++) {
            temp.append((i + 1) + ". " + ingredientList.get(i).getIngredient()
                    + "\t(" + ingredientList.get(i).getQuantity() + " " + ingredientList.get(i).getMeasure() + ")\n");
            recipeIngredientsForWidgets.add((i + 1) + ". " + ingredientList.get(i).getIngredient()
                    + "\t(" + ingredientList.get(i).getQuantity() + " " + ingredientList.get(i).getMeasure() + ")");
        }
        UpdateBakingService.startBakingService(RecipeDetailActivity.this, recipeIngredientsForWidgets);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("arraylist", stepList);
            VersionsFragment versionsFragment = new VersionsFragment();
            versionsFragment.setArguments(bundle);
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, versionsFragment)
                    .commit();
        }
    }

    @Override
    public void OnSelectionChanged(int versionNameIndex) {
        DescriptionFragment descriptionFragment = (DescriptionFragment) getFragmentManager()
                .findFragmentById(R.id.description_fragment);

        if (descriptionFragment != null) {
            descriptionFragment.setDescription(versionNameIndex);
        } else {
            DescriptionFragment newDesriptionFragment = new DescriptionFragment();
            Bundle args = new Bundle();

            args.putInt(DescriptionFragment.KEY_POSITION, versionNameIndex);
            args.putParcelableArrayList("arraylist", stepList);
            newDesriptionFragment.setArguments(args);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, newDesriptionFragment);
            fragmentTransaction.commit();
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