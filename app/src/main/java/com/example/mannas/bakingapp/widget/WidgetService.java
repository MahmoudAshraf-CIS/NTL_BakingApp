package com.example.mannas.bakingapp.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Mannas on 8/7/2017.
 */

public class WidgetService extends RemoteViewsService
{

    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent)
    {
        return (new ListProvider(this.getApplicationContext(), intent));
    }

}