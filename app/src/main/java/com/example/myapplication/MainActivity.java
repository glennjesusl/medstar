package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Adapter.LocationItem {

    Adapter adapter;
    RecyclerView recyclerView;
    ArrayList<Locations> modelRecyclerArrayList;
    MainViewModel mainViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("GOIN' Exam");

        modelRecyclerArrayList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        initRecycler();

        mainViewModel.loadData().observe(this, new Observer<ArrayList<Locations>>() {
            @Override
            public void onChanged(ArrayList<Locations> locations) {

                if(locations!=null){
                    modelRecyclerArrayList = locations;
                    adapter.updateList(modelRecyclerArrayList);
                }

                adapter.notifyDataSetChanged();

            }
        });
    }

    private void initRecycler() {
        adapter = new Adapter(this,this, modelRecyclerArrayList,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

    }




    @Override
    public void onListItemClick(Locations locations) {

        Intent intent = new Intent(MainActivity.this,MapActivity.class);
        intent.putExtra("name",locations.getName());
        intent.putExtra("latitude",locations.getLatitude());
        intent.putExtra("longitude",locations.getLongitude());

        startActivity(intent);
    }
}