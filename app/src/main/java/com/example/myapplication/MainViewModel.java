package com.example.myapplication;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainViewModel extends AndroidViewModel {

    private final MyRepo repository ;

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = new MyRepo(application);

    }

    public MutableLiveData<ArrayList<Locations>> loadData() {
        return repository.callAPI();
    }
}