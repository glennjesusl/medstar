package com.glennlim.medstar;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyRepo {
    private final MutableLiveData<ArrayList<Locations>> allLocations;
    private final ArrayList<Locations> locationList;

    public MyRepo(Application application) {

        locationList = new ArrayList<>();
        allLocations = new MutableLiveData<>();
    }

    public MutableLiveData<ArrayList<Locations>> callAPI(){

        Call<ResponseBody> call = RetrofitClient.getInstance().getLocationService().locationData();
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response.code() == 200) {

                    assert response.body() != null;
                    Log.d("onResponse", String.valueOf(response.body()));

                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(response.body().string());
                        Log.d("onResponseObject",obj.toString());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    try {
                        JSONArray arr = obj.getJSONArray("locations");
                        Log.d("onResponseArray", arr.toString());

                        for (int i = 0; i< arr.length(); i++){
                            JSONObject objA = arr.getJSONObject(i);

                            Locations modelRecycler = new Locations();
                            modelRecycler.setName(objA.getString("name"));
                            modelRecycler.setLatitude(objA.getString("latitude"));
                            modelRecycler.setLongitude(objA.getString("longitude"));

                            locationList.add(modelRecycler);
                        }

                        Log.d("onResponseList", locationList.toString());

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    allLocations.setValue(locationList);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }


        });
        return allLocations;
    }

}
