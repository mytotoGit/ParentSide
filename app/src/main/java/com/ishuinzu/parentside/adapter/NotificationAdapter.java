package com.ishuinzu.parentside.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ishuinzu.parentside.R;
import com.ishuinzu.parentside.animation.PushDownAnimation;
import com.ishuinzu.parentside.object.NotificationObject;

import java.util.Date;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private Context context;
    private List<NotificationObject> notificationObjects;
    private LayoutInflater inflater;

    public NotificationAdapter(Context context, List<NotificationObject> notificationObjects) {
        this.context = context;
        this.notificationObjects = notificationObjects;
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
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_notification, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
        PushDownAnimation.setPushDownAnimationTo(holder.layoutNotification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        holder.txtTitle.setText(notificationObjects.get(position).getTitle());
        holder.txtBody.setText(notificationObjects.get(position).getBody());
        Date date = new Date(notificationObjects.get(position).getTime());
        holder.txtDate.setText(date.toString().substring(0, 19));
    }

    @Override
    public int getItemCount() {
        return notificationObjects.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout layoutNotification;
        private final TextView txtTitle, txtBody, txtDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            layoutNotification = itemView.findViewById(R.id.layoutNotification);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtBody = itemView.findViewById(R.id.txtBody);
            txtDate = itemView.findViewById(R.id.txtDate);
        }
    }
}