package com.example.mannas.bakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.mannas.bakingapp.R;
import com.example.mannas.bakingapp.dummy.Ingredient;
import com.example.mannas.bakingapp.dummy.Recipe;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Mannas on 8/7/2017.
 */

public class ListProvider  implements RemoteViewsService.RemoteViewsFactory
{
    private Context context = null;
    private int appWidgetId;

    private ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();


    public ListProvider(Context context, Intent intent)
    {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        ingredients = intent.getBundleExtra("b").getParcelableArrayList(SimpleWidgetProvider.INGREDIENTS_KEY);
    }

    private void updateWidgetListView()
    {

    }

    @Override
    public int getCount()
    {
        return ingredients!=null?ingredients.size():0;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public RemoteViews getLoadingView()
    {
        // TODO Auto-generated method stub
        RemoteViews rm = new RemoteViews(context.getPackageName(),R.layout.loading);

        return rm;
    }

    @Override
    public RemoteViews getViewAt(int position)
    {
        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget_list_item);
//        remoteView.setTextViewText(R.id.quantity, ingredients.get(position).quantity.toString());
//        remoteView.setTextViewText(R.id.measure, ingredients.get(position).measure);
        remoteView.setTextViewText(R.id.ingredient, ingredients.get(position).ingredient);

        return remoteView;
    }

    @Override
    public int getViewTypeCount()
    {
        // TODO Auto-generated method stub
        return 1;
    }

    @Override
    public boolean hasStableIds()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        updateWidgetListView();
    }

    @Override
    public void onDataSetChanged()
    {
        // TODO Auto-generated method stub
        updateWidgetListView();
    }

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        ingredients.clear();

    }
}