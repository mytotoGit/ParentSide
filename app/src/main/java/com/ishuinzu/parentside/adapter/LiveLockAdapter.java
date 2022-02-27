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
import com.ishuinzu.parentside.app.Utils;
import com.ishuinzu.parentside.object.LiveLockObject;

import java.util.Date;
import java.util.List;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;

public class LiveLockAdapter extends RecyclerView.Adapter<LiveLockAdapter.ViewHolder> {
    private Context context;
    private List<LiveLockObject> liveLockObjects;
    private LayoutInflater inflater;

    public LiveLockAdapter(Context context, List<LiveLockObject> liveLockObjects) {
        this.context = context;
        this.liveLockObjects = liveLockObjects;
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
    public LiveLockAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_live_lock, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull LiveLockAdapter.ViewHolder holder, int position) {
        PushDownAnimation.setPushDownAnimationTo(holder.layoutLiveLock).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        Date startDate = new Date(liveLockObjects.get(position).getStart());
        Date endDate = new Date(liveLockObjects.get(position).getEnd());

        holder.progressDuration.setProgress(Utils.formatMilliMinutes(liveLockObjects.get(position).getEnd() - liveLockObjects.get(position).getStart()), 300);
        holder.txtStart.setText(startDate.toString().substring(0, 19));
        holder.txtEnd.setText(endDate.toString().substring(0, 19));
    }

    @Override
    public int getItemCount() {
        return liveLockObjects.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout layoutLiveLock;
        private final TextView txtStart, txtEnd;
        private final CircularProgressIndicator progressDuration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            layoutLiveLock = itemView.findViewById(R.id.layoutLiveLock);
            txtStart = itemView.findViewById(R.id.txtStart);
            txtEnd = itemView.findViewById(R.id.txtEnd);
            progressDuration = itemView.findViewById(R.id.progressDuration);
        }
    }
}