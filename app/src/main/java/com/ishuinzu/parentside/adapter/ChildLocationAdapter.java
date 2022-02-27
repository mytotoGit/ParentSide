package com.ishuinzu.parentside.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.ishuinzu.parentside.R;
import com.ishuinzu.parentside.animation.PushDownAnimation;
import com.ishuinzu.parentside.object.ChildLocation;
import com.ishuinzu.parentside.ui.FullScreenMapActivity;

import java.util.List;

public class ChildLocationAdapter extends RecyclerView.Adapter<ChildLocationAdapter.ViewHolder> {
    private Context context;
    private List<ChildLocation> childLocations;
    private LayoutInflater inflater;

    public ChildLocationAdapter(Context context, List<ChildLocation> childLocations) {
        this.context = context;
        this.childLocations = childLocations;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ChildLocationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_child_location, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ChildLocationAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder.mapTrackingLocation != null) {
            holder.mapTrackingLocation.onCreate(null);
            holder.mapTrackingLocation.onResume();
            holder.mapTrackingLocation.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull GoogleMap googleMap) {
                    MapsInitializer.initialize(context);
                    LatLng trackLatLong = new LatLng(childLocations.get(position).getTracking_location().getLatitude(), childLocations.get(position).getTracking_location().getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(trackLatLong).title("Tracking Location"));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(trackLatLong, 15));
                }
            });
        }

        if (holder.mapLocation != null) {
            holder.mapLocation.onCreate(null);
            holder.mapLocation.onResume();
            holder.mapLocation.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull GoogleMap googleMap) {
                    MapsInitializer.initialize(context);

                    if (childLocations.get(position).getTracking()) {
                        LatLng currentLatLong = new LatLng(childLocations.get(position).getCurrent_location().getLatitude(), childLocations.get(position).getCurrent_location().getLongitude());
                        googleMap.addMarker(new MarkerOptions().position(currentLatLong).title("Child Location"));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 15));
                    } else {
                        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
                        LatLng startLatLong = new LatLng(childLocations.get(position).getStart_location().getLatitude(), childLocations.get(position).getStart_location().getLongitude());
                        LatLng endLatLong = new LatLng(childLocations.get(position).getLast_location().getLatitude(), childLocations.get(position).getLast_location().getLongitude());
                        options.add(startLatLong);
                        options.add(endLatLong);
                        googleMap.addPolyline(options);
                        googleMap.addMarker(new MarkerOptions().position(startLatLong).title("Start"));
                        googleMap.addMarker(new MarkerOptions().position(endLatLong).title("End"));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startLatLong, 15));
                    }
                }
            });
        }
        PushDownAnimation.setPushDownAnimationTo(holder.btnFullLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, FullScreenMapActivity.class).putExtra("TYPE", "DOUBLE").putExtra("LAT1", childLocations.get(position).getStart_location().getLatitude()).putExtra("LNG1", childLocations.get(position).getStart_location().getLongitude()).putExtra("LAT2", childLocations.get(position).getLast_location().getLatitude()).putExtra("LNG2", childLocations.get(position).getLast_location().getLongitude()));
            }
        });
        PushDownAnimation.setPushDownAnimationTo(holder.btnFullTrackingLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, FullScreenMapActivity.class).putExtra("TYPE", "SINGLE").putExtra("LAT", childLocations.get(position).getTracking_location().getLatitude()).putExtra("LNG", childLocations.get(position).getTracking_location().getLongitude()));
            }
        });
        holder.txtDate.setText(childLocations.get(position).getDate().getDay() + "/" + childLocations.get(position).getDate().getMonth() + "/" + childLocations.get(position).getDate().getYear());
        holder.txtStartEndTime.setText(getStringValue(childLocations.get(position).getStart_time().getHour()) + ":" + getStringValue(childLocations.get(position).getStart_time().getMinute()) + " - " + getStringValue(childLocations.get(position).getEnd_time().getHour()) + ":" + getStringValue(childLocations.get(position).getEnd_time().getMinute()));
    }

    private String getStringValue(int intValue) {
        if (intValue <= 9) {
            return "0" + intValue;
        }
        return "" + intValue;
    }

    @Override
    public int getItemCount() {
        return childLocations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout layoutChildLocation;
        private final MapView mapTrackingLocation;
        private final MapView mapLocation;
        private ImageView btnFullTrackingLocation;
        private ImageView btnFullLocation;
        private TextView txtStartEndTime;
        private TextView txtDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            layoutChildLocation = itemView.findViewById(R.id.layoutChildLocation);
            mapTrackingLocation = itemView.findViewById(R.id.mapTrackingLocation);
            mapLocation = itemView.findViewById(R.id.mapLocation);
            btnFullTrackingLocation = itemView.findViewById(R.id.btnFullTrackingLocation);
            btnFullLocation = itemView.findViewById(R.id.btnFullLocation);
            txtStartEndTime = itemView.findViewById(R.id.txtStartEndTime);
            txtDate = itemView.findViewById(R.id.txtDate);
        }
    }
}