package com.udacity.bakingapp;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.udacity.bakingapp.config.OnVersionNameSelectionChangeListener;
import com.udacity.bakingapp.pojo.Step;

import java.util.ArrayList;

public class VersionsFragment extends ListFragment {

    private ArrayList<Step> stepList = new ArrayList<Step>();
    private String[] versionName;
    private DescriptionFragment description = new DescriptionFragment();

    public VersionsFragment() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        stepList = ((RecipeDetailActivity)getActivity()).stepList;
        versionName =new String [stepList.size()+1];
        versionName[0] = "\n"+((RecipeDetailActivity)getActivity()).temp;
        for (int i =0; i<stepList.size();i++){
            versionName[i+1] = i+". "+stepList.get(i).getShortDescription();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, versionName);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        OnVersionNameSelectionChangeListener listener = (OnVersionNameSelectionChangeListener) getActivity();
        listener.OnSelectionChanged(position);
    }
}
