package com.glennlim.medstar;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    private final LayoutInflater inflater;
    private ArrayList<Locations> dataModelArrayList;
    private final LocationItem locationItem;
    private final Activity activity;

    public Adapter(Context context, Activity activity, ArrayList<Locations> dataModelArrayList, LocationItem locationItem){

        inflater = LayoutInflater.from(context);
        this.dataModelArrayList = dataModelArrayList;
        this.locationItem = locationItem;
        this.activity = activity;

        Log.d("Adapter", dataModelArrayList.toString());
    }



    @NonNull
    @Override
    public Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_layout, parent, false);

        return new MyViewHolder(view,locationItem);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.MyViewHolder holder, int position) {

        holder.name.setText(dataModelArrayList.get(position).getName());
        holder.latitude.setText("Latitude : "+dataModelArrayList.get(position).getLatitude());
        holder.longitude.setText("Longitude : " + dataModelArrayList.get(position).getLongitude());

    }

    @Override
    public int getItemCount() {
        return dataModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name, latitude, longitude;
        LocationItem locationList;
        public MyViewHolder(@NonNull View itemView, LocationItem locationList) {
            super(itemView);

            name = itemView.findViewById(R.id.tv_name);
            latitude = itemView.findViewById(R.id.tv_lat);
            longitude = itemView.findViewById(R.id.tv_long);



            this.locationList = locationList;
            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            locationList.onListItemClick(dataModelArrayList.get(getAdapterPosition()));

        }
    }
    public interface LocationItem {
        void onListItemClick(Locations locations);

    }

    public void updateList(ArrayList<Locations> updateList){
        dataModelArrayList = updateList;
        notifyDataSetChanged();
    }

}