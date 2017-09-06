package com.udacity.bakingapp;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
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

    private RecyclerView recyclerView;
    private Recipe recipe;
    private RecyclerDetailAdapter adapter;

    private ImageView imageView;
    private TextView textView;

    private int id;

    public StringBuffer temp = new StringBuffer();

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
                id = intent.getIntExtra("id",0);
            } else {
                finish();
            }
        }
        stepList = recipeList.get(id).getSteps();
        ingredientList = recipeList.get(id).getIngredients();

        Log.d("HASIL",""+stepList.size());
        ArrayList<String> recipeIngredientsForWidgets= new ArrayList<>();

        for (int i = 0; i< ingredientList.size(); i++){
            temp.append((i+1)+". "+ingredientList.get(i).getIngredient()
                    +"\t("+ingredientList.get(i).getQuantity()+" "+ingredientList.get(i).getMeasure()+")\n");
            recipeIngredientsForWidgets.add((i+1)+". "+ingredientList.get(i).getIngredient()
                    +"\t("+ingredientList.get(i).getQuantity()+" "+ingredientList.get(i).getMeasure()+")");
        }
        UpdateBakingService.startBakingService(RecipeDetailActivity.this,recipeIngredientsForWidgets);

        if (findViewById(R.id.fragment_container) != null){

            // However if we are being restored from a previous state, then we don't
            // need to do anything and should return or we could end up with overlapping Fragments
            if (savedInstanceState != null){
                return;
            }

            // Create an Instance of Fragment
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("arraylist", stepList);
            VersionsFragment versionsFragment = new VersionsFragment();
            versionsFragment.setArguments(bundle);
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, versionsFragment)
                    .commit();
        }

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        UpdateBakingService.startBakingService(RecipeDetailActivity.this,recipeIngredientsForWidgets);*/
    }

    @Override
    public void OnSelectionChanged(int versionNameIndex) {
        DescriptionFragment descriptionFragment = (DescriptionFragment) getFragmentManager()
                .findFragmentById(R.id.description_fragment);

        if (descriptionFragment != null){
            // If description is available, we are in two pane layout
            // so we call the method in DescriptionFragment to update its content
            descriptionFragment.setDescription(versionNameIndex);
        } else {
            DescriptionFragment newDesriptionFragment = new DescriptionFragment();
            Bundle args = new Bundle();

            args.putInt(DescriptionFragment.KEY_POSITION,versionNameIndex);
            args.putParcelableArrayList("arraylist", stepList);
            newDesriptionFragment.setArguments(args);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the backStack so the User can navigate back
            fragmentTransaction.replace(R.id.fragment_container,newDesriptionFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
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