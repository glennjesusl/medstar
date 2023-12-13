package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    FusedLocationProviderClient fusedLocationProviderClient;
    GoogleMap map;
    double userLat, userLong;
    double selectedLat, selectedLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent intent = getIntent();
        selectedLat = Double.parseDouble(Objects.requireNonNull(intent.getStringExtra("latitude")));
        selectedLong = Double.parseDouble(Objects.requireNonNull(intent.getStringExtra("longitude")));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("To "+intent.getStringExtra("name"));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        if (mapFragment != null)
            mapFragment.getMapAsync(this::onMapReady);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setTrafficEnabled(true);

        fetchUserLocation();
        //displayDirection();
    }

    private void fetchUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                userLat = location.getLatitude();
                userLong = location.getLongitude();

                Log.d("displayDirection", userLat +","+ userLong);
                displayDirection(userLat, userLong);
            }
        });
    }

    private void displayDirection(double latitude, double longitude) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Uri.parse("https://maps.googleapis.com/maps/api/directions/json")
                .buildUpon()
                .appendQueryParameter("destination", selectedLat+","+selectedLong)
                .appendQueryParameter("origin", latitude+","+longitude)
                //.appendQueryParameter("origin", "8.9776756, 125.4093881")
                .appendQueryParameter("mode", "driving")
                .appendQueryParameter("key", "AIzaSyD3OFyan7RVBHQqPrls9i-79unOp9yptT4")
                .toString();
        Log.d("displayDirectionUrl", url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("onResponse", response.toString());
                    String status = response.getString("status");
                    if (status.equals("OK")) {
                        JSONArray routes = response.getJSONArray("routes");
                        Log.d("routes", routes.toString());
                        ArrayList<LatLng> points;
                        PolylineOptions polylineOptions = null;

                        for (int i = 0; i < routes.length(); i++) {
                            points = new ArrayList<>();
                            polylineOptions = new PolylineOptions();
                            JSONArray legs = routes.getJSONObject(i).getJSONArray("legs");
                            Log.d("legs", legs.toString());

                            for (int j = 0; j < legs.length(); j++) {
                                JSONArray steps = legs.getJSONObject(j).getJSONArray("steps");
                                Log.d("steps", steps.toString());

                                for (int k = 0; k < steps.length(); k++) {
                                    String polyline = steps.getJSONObject(k).getJSONObject("polyline").getString("points");
                                    List<LatLng> list = decodePoly(polyline);
                                    Log.d("list", list.toString());

                                    for (int l = 0; l < list.size(); l++) {
                                        LatLng position = new LatLng((list.get(l)).latitude, (list.get(l)).longitude);
                                        points.add(position);
                                    }
                                }
                            }
                            polylineOptions.addAll(points);
                            polylineOptions.width(12);
                            polylineOptions.color(ContextCompat.getColor(MapActivity.this, R.color.purple_500));
                            polylineOptions.geodesic(true);
                        }

                        map.addPolyline(polylineOptions);
                        //map.addMarker(new MarkerOptions().position(new LatLng(userLat, userLong)).title("Origin"));
                        map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)));
                        map.addMarker(new MarkerOptions().position(new LatLng(selectedLat, selectedLong)));

                        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                                //.include(new LatLng(userLat, userLong))
                                .include(new LatLng(latitude, longitude))
                                .include(new LatLng(selectedLat, selectedLong))
                                .build();

                        Point point = new Point();
                        getWindowManager().getDefaultDisplay().getSize(point);
                        //map.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, point.x, 150, 10));
                        map.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 200));
                        //map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(8.9776756, 125.4093881),10));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("catch", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RetryPolicy retryPolicy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);
        requestQueue.add(jsonObjectRequest);
    }


    //--This method is use to decode string to latlng
    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}