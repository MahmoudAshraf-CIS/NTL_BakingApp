package com.example.mannas.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.example.mannas.bakingapp.R;
import com.example.mannas.bakingapp.RecipeListActivity;
import com.example.mannas.bakingapp.dummy.Recipe;
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
    public static String INGREDIENTS_KEY = "INGREDIENTS";



    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int count = appWidgetIds.length;

        for (int i = 0; i < count; i++) {
            int widgetId = appWidgetIds[i];
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
            String json =  context.getSharedPreferences(context.getPackageName(),Context.MODE_PRIVATE).getString(DATA_KEY ,"");
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
                // set the recipe Name
                remoteViews.setTextViewText(R.id.name, recipe.getName());
                // set click to launch activity
                Intent in = new Intent( context, RecipeListActivity.class);
                in.putExtra(IS_WIDGET_KEY,true);
                in.putExtra(RECIPE_KEY,recipe);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, in, 0);
                remoteViews.setOnClickPendingIntent(R.id.relative, pendingIntent);

                //set the list of ingredients
                Intent in2 = new Intent(context, WidgetService.class);
                in2.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
                Bundle b = new Bundle();
                b.putParcelableArrayList(INGREDIENTS_KEY,recipe.getIngredients());
                in2.putExtra("b",b);
//                String ingredients_json = (new Gson()).toJson(recipe.getIngredients());
//                in2.putParcelableArrayListExtra(INGREDIENTS_KEY , recipe.getIngredients());
                in2.setData(Uri.parse(in2.toUri(Intent.URI_INTENT_SCHEME)));

                remoteViews.setRemoteAdapter(R.id.widget_list, in2);


            } catch (JSONException e) {
                e.printStackTrace();
            }


            Intent intent = new Intent(context, SimpleWidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//            remoteViews.setOnClickPendingIntent(R.id.actionButton, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
        }


    }

    public static ArrayList<Recipe> Parse(String json) throws JSONException {
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