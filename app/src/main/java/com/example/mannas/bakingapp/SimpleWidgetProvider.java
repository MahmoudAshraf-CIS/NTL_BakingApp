package com.example.mannas.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.RemoteViews;

import com.example.mannas.bakingapp.dummy.Recipe;
import com.example.mannas.bakingapp.dummy.Step;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Mannas on 7/26/2017.
 */
public class SimpleWidgetProvider extends AppWidgetProvider {
    public static String DATA_KEY = "data";
    public static String IS_WIDGET_KEY = "widget";
    public static String RECIPE_KEY = "recipe";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int count = appWidgetIds.length;

        for (int i = 0; i < count; i++) {
            int widgetId = appWidgetIds[i];
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);


            SharedPreferences sp = context.getSharedPreferences(context.getPackageName(),Context.MODE_PRIVATE);
            String json = sp.getString(DATA_KEY ,"");
            Recipe recipe;
            try {
                ArrayList<Recipe> ls = Parse(json);
                Random random = new Random();
                Integer index = random.nextInt(ls.size());
                recipe = ls.get(index);
                if(recipe.getImageURL()!=null){
                    Picasso.with(context).load(recipe.getImageURL())
                            .error(R.drawable.ic_eat)
                            .into(remoteViews,R.id.imageView,appWidgetIds);
                }else {
                    remoteViews.setImageViewResource(R.id.imageView,R.drawable.ic_eat);
                }

                remoteViews.setTextViewText(R.id.name, recipe.getName());
                Intent in = new Intent(context, RecipeListActivity.class);
                in.putExtra(IS_WIDGET_KEY,true);
                in.putExtra(RECIPE_KEY,recipe);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, in, 0);
                remoteViews.setOnClickPendingIntent(R.id.widget, pendingIntent);

            } catch (JSONException e) {
                e.printStackTrace();
            }




            Intent intent = new Intent(context, SimpleWidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//            remoteViews.setOnClickPendingIntent(R.id.actionButton, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    ArrayList<Recipe> Parse(String json) throws JSONException {
        if(json!=""){
            JSONArray arr = new JSONArray(json);
            ArrayList<Recipe> ls = new ArrayList<>();
            for(int i=0;i<arr.length();i++){
                ls.add(new Gson().fromJson(arr.get(i).toString(),Recipe.class));
            }
            return ls;
        }
        return null;
    }
}