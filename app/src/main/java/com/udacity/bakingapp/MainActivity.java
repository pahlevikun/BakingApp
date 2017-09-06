package com.udacity.bakingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.udacity.bakingapp.adapter.RecyclerAdapter;
import com.udacity.bakingapp.config.APIConfig;
import com.udacity.bakingapp.idle.SimpleIdlingResource;
import com.udacity.bakingapp.pojo.Ingredient;
import com.udacity.bakingapp.pojo.Recipe;
import com.udacity.bakingapp.pojo.Step;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private boolean doubleBackToExitPressedOnce = false;
    private ProgressDialog loading;

    private ArrayList<Recipe> recipeList = new ArrayList<Recipe>();
    private ArrayList<Step> stepList;
    private ArrayList<Ingredient> ingredientList;

    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Nullable
    private SimpleIdlingResource mIdlingResource;

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new RecyclerAdapter(MainActivity.this,recipeList);
        //layoutManager = new LinearLayoutManager(getApplicationContext());
        if (getResources().getBoolean(R.bool.isTablet)){
            layoutManager = new GridLayoutManager(MainActivity.this,calculateNoOfColumns(MainActivity.this));
        }else{
            layoutManager = new GridLayoutManager(MainActivity.this,1);

        }
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        getRecipe();

    }

    public void getRecipe() {
        loading = ProgressDialog.show(MainActivity.this, "Please wait", "Getting recipe...", false, false);

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        StringRequest postRequest = new StringRequest(Request.Method.GET, APIConfig.GET_RECIPE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.d("Response", "Hasil valid" + response);
                        hideDialog();
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                stepList = new ArrayList<Step>();
                                ingredientList = new ArrayList<Ingredient>();
                                JSONObject recipe = jsonArray.getJSONObject(i);
                                int id = recipe.getInt("id");
                                String name = recipe.getString("name");
                                String image = recipe.getString("image");
                                int servings = recipe.getInt("servings");

                                JSONArray ingredientArray = recipe.getJSONArray("ingredients");
                                for (int j = 0; j < ingredientArray.length(); j++) {
                                    JSONObject ingredients = ingredientArray.getJSONObject(j);
                                    Double quantity = ingredients.getDouble("quantity");
                                    String measure = ingredients.getString("measure");
                                    String ingredient = ingredients.getString("ingredient");
                                    ingredientList.add(new Ingredient(j, quantity, measure, ingredient));
                                }
                                Log.d("HASIL","Ingredients : "+ingredientList.size());

                                JSONArray stepsArray = recipe.getJSONArray("steps");
                                for (int k = 0; k < stepsArray.length(); k++) {
                                    JSONObject steps = stepsArray.getJSONObject(k);
                                    int idSteps = steps.getInt("id");
                                    String shortDescription = steps.getString("shortDescription");
                                    String description = steps.getString("description");
                                    String videoURL = steps.getString("videoURL");
                                    String thumbnailURL = steps.getString("thumbnailURL");
                                    stepList.add(new Step(idSteps, shortDescription, description, videoURL, thumbnailURL));
                                }
                                Log.d("HASIL","Steps : "+stepList.size());
                                recipeList.add(new Recipe(id, name, ingredientList, stepList, servings, image));
                                adapter.notifyDataSetChanged();
                            }
                            Log.d("HASIL",""+recipeList.size()+"\nIngredients "+recipeList.get(0).getIngredients().size()+"\n"+recipeList.get(0).getName());                            Log.d("HASIL",""+recipeList.size()+"\nIngredients "+recipeList.get(2).getIngredients().size()+"\n"+recipeList.get(2).getName());
                            Log.d("HASIL",""+recipeList.size()+"\nIngredients "+recipeList.get(1).getIngredients().size()+"\n"+recipeList.get(1).getName());
                            Log.d("HASIL",""+recipeList.size()+"\nIngredients "+recipeList.get(2).getIngredients().size()+"\n"+recipeList.get(2).getName());
                            Log.d("HASIL",""+recipeList.size()+"\nIngredients "+recipeList.get(3).getIngredients().size()+"\n"+recipeList.get(3).getName());
                        } catch (Exception e) {

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Response", "Hasil error => " + error.toString());
                        hideDialog();
                        Toast.makeText(MainActivity.this, "Failed getting location, please try again!", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                return headers;
            }
        };
        int socketTimeout = 15000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 180;
        int noOfColumns = (int) (dpWidth / scalingFactor);
        return noOfColumns;
    }

    private void hideDialog() {
        if (loading.isShowing())
            loading.dismiss();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Double click for exit!", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
