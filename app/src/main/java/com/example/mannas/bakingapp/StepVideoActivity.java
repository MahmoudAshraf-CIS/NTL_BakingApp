package com.example.mannas.bakingapp;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class StepVideoActivity extends AppCompatActivity {




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_video);
        Intent i = getIntent();
        StepVideoFragment fragment = new StepVideoFragment();
        fragment.setArguments(i.getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.ingredient_activity,fragment,StepVideoFragment.class.getName()).commit();


    }



}