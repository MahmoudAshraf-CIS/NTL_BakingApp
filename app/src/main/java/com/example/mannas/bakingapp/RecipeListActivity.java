package com.example.mannas.bakingapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.mannas.bakingapp.Content.RecipesLoader;
import com.example.mannas.bakingapp.dummy.Recipe;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * An activity representing a list of Recipes. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RecipeActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RecipeListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Recipe>>{

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    public static boolean mTwoPane = false;
    Integer RECIPES_LOADER_ID =1 ;
    RecipesListAdapter lsAdapter;
    final String LS_ADAPTER_DATA_SET_KEY = "lsDataSet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        View recyclerView = findViewById(R.id.recipe_list);
        assert recyclerView != null;
        if (findViewById(R.id.recipe_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
        setupRecyclerView((RecyclerView) recyclerView);
        if( savedInstanceState !=null){
            ArrayList<Recipe> ls = savedInstanceState.getParcelableArrayList(LS_ADAPTER_DATA_SET_KEY);
            lsAdapter.changeDataSet(ls);
        }else{
            getSupportLoaderManager().restartLoader(RECIPES_LOADER_ID,null,this).forceLoad();
        }

        if(getIntent()!=null&& getIntent().getBooleanExtra(SimpleWidgetProvider.IS_WIDGET_KEY,false)){
            Intent intent = new Intent(getApplicationContext(), RecipeActivity.class);
            intent.putExtra(RecipeActivity.ARG_ITEM, getIntent().getParcelableExtra(SimpleWidgetProvider.RECIPE_KEY) );
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);

        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

        super.onSaveInstanceState(outState, outPersistentState);
    }

    public RecipeListActivity getInstance(){
        return this;
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(LS_ADAPTER_DATA_SET_KEY,  lsAdapter.getDataSet());
        super.onSaveInstanceState(outState);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        lsAdapter = new RecipesListAdapter(new ArrayList<Recipe>());
        recyclerView.setAdapter(lsAdapter);
        if(mTwoPane)
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),3));
    }

    @Override
    public Loader<ArrayList<Recipe>> onCreateLoader(int id, Bundle args) {if(id == RECIPES_LOADER_ID){
            return new RecipesLoader(getApplicationContext());
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Recipe>> loader, ArrayList<Recipe> data) {


        if(data!=null && loader.getId()==RECIPES_LOADER_ID){
            lsAdapter.changeDataSet(data);
            SharedPreferences.Editor e = getSharedPreferences(getPackageName(),MODE_PRIVATE).edit();
            Gson g =new Gson();
            String json = g.toJson(data);

            e.putString(SimpleWidgetProvider.DATA_KEY, json);
            e.apply();
            Toast.makeText(getApplicationContext(),"finished loading ",Toast.LENGTH_LONG).show();
        }else if( loader.getId()==RECIPES_LOADER_ID){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            getSupportLoaderManager().restartLoader(RECIPES_LOADER_ID,null,getInstance()).forceLoad();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
            builder.setMessage("Failed to load List of recipes, retry again ?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }


    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Recipe>> loader) {

    }










    public class RecipesListAdapter
            extends RecyclerView.Adapter<RecipesListAdapter.ViewHolder> {

        private ArrayList<Recipe> mValues;

        public RecipesListAdapter(ArrayList<Recipe> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recipe_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder,int position) {
            Picasso.with(holder.itemView.getContext())
                    .load(mValues.get(position).getImageURL())
                    .error(R.drawable.ic_eat)
                    .placeholder(R.drawable.ic_eat)
                    .into(holder.img);
            holder.serving.setText(mValues.get(position).getServings().toString());
            holder.mName.setText(mValues.get(position).getName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, RecipeActivity.class);
                        intent.putExtra(RecipeActivity.ARG_ITEM, mValues.get(holder.getAdapterPosition()));
                        context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final ImageView img;
            final TextView mName,serving;

            ViewHolder(View view) {
                super(view);
                mName = (TextView) view.findViewById(R.id.name);
                img = (ImageView) view.findViewById(R.id.img);
                serving = (TextView) view.findViewById(R.id.serving);
            }
        }

        void changeDataSet(ArrayList<Recipe> newDataSet){
            mValues = newDataSet;
            notifyDataSetChanged();
        }

        ArrayList<Recipe> getDataSet(){
            return mValues;
        }
    }






}
