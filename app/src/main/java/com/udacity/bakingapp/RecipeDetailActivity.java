package com.udacity.bakingapp;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.udacity.bakingapp.config.OnDataPass;
import com.udacity.bakingapp.config.OnVersionNameSelectionChangeListener;
import com.udacity.bakingapp.pojo.Ingredient;
import com.udacity.bakingapp.pojo.Recipe;
import com.udacity.bakingapp.pojo.Step;
import com.udacity.bakingapp.widget.UpdateBakingService;

import java.util.ArrayList;

/**
 * Created by farhan on 9/3/17.
 */

public class RecipeDetailActivity extends AppCompatActivity implements OnVersionNameSelectionChangeListener, OnDataPass {

    private int ids=0, id;
    public StringBuffer temp = new StringBuffer();

    private ArrayList<Ingredient> ingredientList = new ArrayList<Ingredient>();
    private ArrayList<Recipe> recipeList = new ArrayList<Recipe>();
    public ArrayList<Step> stepList = new ArrayList<Step>();

    final static String KEY_POSITION = "POSITION";
    final static String ARRAY_STEP = "STEPLIST";
    final static String FRAGMENT = "FRAGMENT";

    private DescriptionFragment descriptionFragment = new DescriptionFragment();
    private OnDataPass onDataPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onDataPass = (OnDataPass) this;

        setContentView(R.layout.activity_recipe_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

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
        passData(stepList,ids);

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
                descriptionFragment.setDescription(ids);
            } else {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(DescriptionFragment.ARRAY_STEP, stepList);
                VersionsFragment versionsFragment = new VersionsFragment();
                versionsFragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, versionsFragment)
                        .commit();
            }
        }
    }

    @Override
    public void OnSelectionChanged(int versionNameIndex) {
        DescriptionFragment descriptionFragment = (DescriptionFragment) getFragmentManager().findFragmentById(R.id.description_fragment);
        ids = versionNameIndex;
            if (descriptionFragment != null ) {
                descriptionFragment.setDescription(versionNameIndex);
            } else {
                DescriptionFragment newDesriptionFragment = new DescriptionFragment();
                Bundle args = new Bundle();
                args.putInt(KEY_POSITION, versionNameIndex);
                args.putParcelableArrayList(ARRAY_STEP, stepList);
                newDesriptionFragment.setArguments(args);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, newDesriptionFragment,FRAGMENT);
                fragmentTransaction.commit();
            }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_POSITION, ids);
        outState.putParcelableArrayList(ARRAY_STEP, stepList);
        passData(stepList,ids);
    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//    }

    public void passData(ArrayList<Step> arrayList,int index) {
        onDataPass.onDataPass(arrayList,index);
    }

    @Override
    public void onDataPass(ArrayList<Step> arrayList, int index) {
        stepList = arrayList;
        ids = index;
        Log.d("HASILFRAG","Interface Act "+ids);
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