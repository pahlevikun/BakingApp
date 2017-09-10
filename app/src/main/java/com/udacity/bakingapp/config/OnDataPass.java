package com.udacity.bakingapp.config;

import com.udacity.bakingapp.pojo.Step;

import java.util.ArrayList;

/**
 * Created by farhan on 9/10/17.
 */

public interface OnDataPass {
    public void onDataPass(ArrayList<Step> stepArrayList, int index);
}
