package com.ishuinzu.parentside.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.ishuinzu.parentside.R;
import com.ishuinzu.parentside.animation.PushDownAnimation;
import com.ishuinzu.parentside.object.CallObject;

import java.util.Date;
import java.util.List;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;

public class CallAdapter extends RecyclerView.Adapter<CallAdapter.ViewHolder> {
    private Context context;
    private List<CallObject> callObjects;
    private LayoutInflater inflater;

    public CallAdapter(Context context, List<CallObject> callObjects) {
        this.context = context;
        this.callObjects = callObjects;
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

    @NonNull
    @Override
    public CallAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_call, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CallAdapter.ViewHolder holder, int position) {
        PushDownAnimation.setPushDownAnimationTo(holder.layoutCall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        holder.txtDate.setText("" + new Date(Long.parseLong(callObjects.get(position).getCall_date())).toString());
        holder.txtGeoLocation.setText("" + callObjects.get(position).getGeo_code_location());
        holder.progressDuration.setProgress(Integer.parseInt(callObjects.get(position).getDuration()), 300);
    }

    @Override
    public int getItemCount() {
        return callObjects.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView layoutCall;
        private final TextView txtDate, txtGeoLocation;
        private final CircularProgressIndicator progressDuration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            layoutCall = itemView.findViewById(R.id.layoutCall);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtGeoLocation = itemView.findViewById(R.id.txtGeoLocation);
            progressDuration = itemView.findViewById(R.id.progressDuration);
        }
    }
}