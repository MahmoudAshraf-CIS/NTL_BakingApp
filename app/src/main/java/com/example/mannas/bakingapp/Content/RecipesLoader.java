package com.example.mannas.bakingapp.Content;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.mannas.bakingapp.Utility;
import com.example.mannas.bakingapp.dummy.Recipe;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Mannas on 7/26/2017.
 */

public class RecipesLoader extends AsyncTaskLoader<ArrayList<Recipe>> {
    private final String URL = "http://go.udacity.com/android-baking-app-json";
    private final String LOG_TAG = this.getClass().getName();
    private final String ERR_DOWNLOADING = "Error Downloading";
    private final String ERR_PARSING = "Error Parsing";
    OkHttpClient client;

    public RecipesLoader(Context context) {
        super(context);
        client = new OkHttpClient().newBuilder().followRedirects(true).build();
    }

    @Override
    public ArrayList<Recipe> loadInBackground() {
        String response="";
        try {
            response = Download(URL);
        } catch (IOException e) {
            Log.e(LOG_TAG,ERR_DOWNLOADING);
            e.printStackTrace();
        }

        try {
            return Parse(response);
        } catch (JSONException e) {
            Log.e(LOG_TAG,ERR_PARSING);
            e.printStackTrace();
            return null;
        }
    }

    String Download(String url) throws IOException {
        if(!Utility.isOffline(getContext())){
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }else{
            //// TODO: get from cache
            return "";
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
