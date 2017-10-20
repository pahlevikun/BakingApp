package com.udacity.bakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;

import com.udacity.bakingapp.R;

/**
 * Created by farhan on 9/29/17.
 */

public class WidgetProvider extends AppWidgetProvider {


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_view2);
//        SharedPreferences shared;
//
//        shared = context.getSharedPreferences("APP", context.MODE_PRIVATE);
//        String recipe = shared.getString("RECIPE", "");
//        Log.d("HASILSHARED",""+recipe);
//        views.setTextViewText(R.id.textViewWidgetRecipe,recipe);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("update_widget")) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_view2);
            SharedPreferences shared;

            shared = context.getSharedPreferences("APP", context.MODE_PRIVATE);
            String recipe = shared.getString("RECIPE", "");
            Log.d("HASILSHARED",""+recipe);
            if (recipe!=null){
                views.setTextViewText(R.id.textViewWidgetRecipe,recipe);
            }

            AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, WidgetProvider.class), views);
        }
    }
}