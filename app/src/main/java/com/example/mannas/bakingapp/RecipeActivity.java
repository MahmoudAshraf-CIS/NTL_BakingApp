package com.example.mannas.bakingapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mannas.bakingapp.dummy.Ingredient;
import com.example.mannas.bakingapp.dummy.Recipe;
import com.example.mannas.bakingapp.dummy.Step;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * An activity representing a single Recipe detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link RecipeListActivity}.
 */
public class RecipeActivity extends AppCompatActivity {
    public static final String ARG_ITEM = "item";
    private Recipe mItem;
    public static boolean mTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);


        mTwoPane = findViewById(R.id.detail)!=null;


        if(savedInstanceState!=null)
            mItem = savedInstanceState.getParcelable("mItem");
        else
            mItem = getIntent().getParcelableExtra(ARG_ITEM);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null && mItem !=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(mItem.getName()!=null?mItem.getName() : "Recipe");
        }
        setMasterView();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("mItem",mItem);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
//            NavUtils.navigateUpTo(this, new Intent(this, RecipeListActivity.class));
            Intent intent = NavUtils.getParentActivityIntent(this);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            NavUtils.navigateUpTo(this, intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void setMasterView(){
        RecyclerView ingredients = (RecyclerView) findViewById(R.id.ingredients_recycler);
        assert ingredients!=null;
        ingredients.setAdapter(new IngredientsAdapter(mItem.getIngredients()));
        ImageView img = (ImageView) findViewById(R.id.img);
        try{
            Picasso.with(this).load(mItem.getImageURL())
                .error(R.drawable.ic_eat)
                .placeholder(R.drawable.ic_eat)
                .into(img);
        }catch (Exception e){
            img.setImageResource(R.drawable.ic_eat);
            e.printStackTrace();
        }
        ViewPager steps_view_pager = (ViewPager) findViewById(R.id.steps_view_pager);
        StepsViewPagerAdapter stepsAdapter = new StepsViewPagerAdapter(mItem.getSteps(),getLayoutInflater());
        steps_view_pager.setAdapter(stepsAdapter);
        steps_view_pager.setPageMargin( Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics())));

    }

    public class StepsViewPagerAdapter extends PagerAdapter{
        ArrayList<Step> mSteps;
        LayoutInflater mInflater;
        public StepsViewPagerAdapter(ArrayList<Step> steps,LayoutInflater inflater) {
            mSteps = steps;
            mInflater = inflater;
        }

        @Override
        public int getCount() {
            return mSteps==null?0:mSteps.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = mInflater.inflate(R.layout.step_list_item,null);
            TextView step_index = (TextView) view.findViewById(R.id.step_index);
            TextView steps_total_cnt = (TextView) view.findViewById(R.id.steps_total_cnt);
            TextView short_description = (TextView) view.findViewById(R.id.short_description);
            Integer index = mSteps.get(position).id+1;
            step_index.setText(index.toString());
            Integer sz=mSteps.size();
            steps_total_cnt.setText(sz.toString());
            short_description.setText(mSteps.get(position).shortDescription);

            ImageView img = (ImageView) view.findViewById(R.id.play);
            try{
                Picasso.with(getApplicationContext()).load(mSteps.get(position).thumbnailURL)
                        .error(R.drawable.play).placeholder(R.drawable.play)
                        .into(img);
            }catch (Exception e){
                img.setImageResource(R.drawable.play);
                e.printStackTrace();
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mTwoPane){
                        //// TODO: 7/28/2017 place new Fragment
                        StepVideoFragment f = new StepVideoFragment();
                        Bundle b = new Bundle();
                        b.putParcelableArrayList(StepVideoFragment.ARG_INGREDIENTS,mSteps);
                        b.putInt(StepVideoFragment.ARG_INDEX , position);
                        f.setArguments(b);
                        getSupportFragmentManager().beginTransaction().replace(R.id.detail , f).commit();
                    }else{
                        Toast.makeText(getApplicationContext(),"play activity",Toast.LENGTH_LONG).show();
                        Intent i = new Intent(getApplicationContext(),StepVideoActivity.class);
                        i.putParcelableArrayListExtra(StepVideoFragment.ARG_INGREDIENTS,mSteps);
                        i.putExtra(StepVideoFragment.ARG_INDEX,position);
                        startActivity(i);
                    }
                }
            });

            container.addView(view,0);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(((View) object));
        }
    }

    public class IngredientsAdapter
            extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> {

        private ArrayList<Ingredient> mValues;

        public IngredientsAdapter(ArrayList<Ingredient> items) {
            mValues = items;
        }

        @Override
        public IngredientsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ingredients_list_item, parent, false);
            return new IngredientsAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final IngredientsAdapter.ViewHolder holder, final int position) {
            holder.ingredient.setText(mValues.get(position).ingredient);
            holder.measure.setText(mValues.get(position).measure);
            holder.quantity.setText(mValues.get(position).quantity.toString());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dialog = new Dialog(RecipeActivity.this);
                    dialog.setContentView(R.layout.ingredients_list_item_dialog);

                        TextView quantity,measure,ingredient;
                        quantity = (TextView) dialog.findViewById(R.id.quantity);
                        measure = (TextView) dialog.findViewById(R.id.measure);
                        ingredient = (TextView) dialog.findViewById(R.id.ingredient);
                        ingredient.setText(mValues.get(position).ingredient);
                        measure.setText(mValues.get(position).measure);
                        quantity.setText(mValues.get(position).quantity.toString());

                    dialog.setCancelable(true);
                    dialog.setTitle("Ingredient "+(position+1));
                    dialog.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            final TextView quantity,measure,ingredient;

            ViewHolder(View view) {
                super(view);
                quantity = (TextView) view.findViewById(R.id.quantity);
                measure = (TextView) view.findViewById(R.id.measure);
                ingredient = (TextView) view.findViewById(R.id.ingredient);
            }
        }

    }


//    public class StepsAdapter
//            extends RecyclerView.Adapter<StepsAdapter.ViewHolder> {
//
//        private ArrayList<Step> mValues;
//
//        public StepsAdapter(ArrayList<Step> items) {
//            mValues = items;
//        }
//
//        @Override
//        public StepsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.ingredients_list_item, parent, false);
//            return new StepsAdapter.ViewHolder(view);
//        }
//        @Override
//        public void onBindViewHolder(final StepsAdapter.ViewHolder holder, int position) {
//
//        }
//
//        @Override
//        public int getItemCount() {
//            return mValues.size();
//        }
//
//        class ViewHolder extends RecyclerView.ViewHolder {
//            ViewHolder(View view) {
//                super(view);
//            }
//        }
//
//    }



}
