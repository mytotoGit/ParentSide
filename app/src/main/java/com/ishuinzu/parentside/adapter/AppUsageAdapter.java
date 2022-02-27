package com.ishuinzu.parentside.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ishuinzu.parentside.R;
import com.ishuinzu.parentside.animation.PushDownAnimation;
import com.ishuinzu.parentside.app.Utils;
import com.ishuinzu.parentside.object.AppUsageObject;
import com.ishuinzu.parentside.ui.AppUsageActivity;

import java.util.List;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;

public class AppUsageAdapter extends RecyclerView.Adapter<AppUsageAdapter.ViewHolder> {
    private Context context;
    private List<AppUsageObject> appUsageObjects;
    private LayoutInflater inflater;
    // Total Usage Time
    private long total_usage;
    // Links List
    private List<String> linksObjects;

    public AppUsageAdapter(Context context, List<AppUsageObject> appUsageObjects) {
        this.context = context;
        this.appUsageObjects = appUsageObjects;
        this.inflater = LayoutInflater.from(context);
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setTotal_usage(long total_usage) {
        this.total_usage = total_usage;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setLinksObjects(List<String> linksObjects) {
        this.linksObjects = linksObjects;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AppUsageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_app_usage, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AppUsageAdapter.ViewHolder holder, int position) {
        PushDownAnimation.setPushDownAnimationTo(holder.layoutAppUsage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, AppUsageActivity.class).putExtra("NAME", appUsageObjects.get(position).getName()).putExtra("LINK", linksObjects.get(position)).putExtra("PACKAGE", appUsageObjects.get(position).getPackage_name()));
            }
        });
        holder.app_name.setText(appUsageObjects.get(position).getName());
        holder.app_usage.setText(Utils.formatMilliSeconds(appUsageObjects.get(position).getUsage_time()));
        holder.txtMobileUsage.setText(appUsageObjects.get(position).getMobile_data());
        holder.txtWifiUsage.setText(appUsageObjects.get(position).getWifi_data());
        holder.progressUsage.setProgress(appUsageObjects.get(position).getUsage_time(), total_usage);
        holder.progressUsage.setTextColor(context.getColor(R.color.background));
        holder.app_usage.setVisibility(View.VISIBLE);
        try {
            Glide.with(context).load(linksObjects.get(position)).into(holder.app_image);
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            holder.app_image.setImageResource(R.drawable.img_logo);
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return appUsageObjects.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RelativeLayout layoutAppUsage;
        private final ImageView app_image;
        private final TextView app_name, app_usage, txtMobileUsage, txtWifiUsage;
        private final CircularProgressIndicator progressUsage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            layoutAppUsage = itemView.findViewById(R.id.layoutAppUsage);
            app_image = itemView.findViewById(R.id.app_image);
            app_name = itemView.findViewById(R.id.app_name);
            app_usage = itemView.findViewById(R.id.app_usage);
            txtMobileUsage = itemView.findViewById(R.id.txtMobileUsage);
            txtWifiUsage = itemView.findViewById(R.id.txtWifiUsage);
            progressUsage = itemView.findViewById(R.id.progressUsage);
        }
    }
}