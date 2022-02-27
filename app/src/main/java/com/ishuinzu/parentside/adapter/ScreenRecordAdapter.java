package com.ishuinzu.parentside.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.ishuinzu.parentside.R;
import com.ishuinzu.parentside.animation.PushDownAnimation;
import com.ishuinzu.parentside.object.RecordingObject;
import com.ishuinzu.parentside.ui.PlayVideoActivity;

import java.util.Date;
import java.util.List;

public class ScreenRecordAdapter extends RecyclerView.Adapter<ScreenRecordAdapter.ViewHolder> {
    private Context context;
    private List<String> keys;
    private List<RecordingObject> recordingObjects;
    private LayoutInflater inflater;

    public ScreenRecordAdapter(Context context, List<RecordingObject> recordingObjects, List<String> keys) {
        this.context = context;
        this.recordingObjects = recordingObjects;
        this.keys = keys;
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
    public ScreenRecordAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_recording, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ScreenRecordAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        PushDownAnimation.setPushDownAnimationTo(holder.layoutRecording).setOnClickListener(v -> context.startActivity(new Intent(context, PlayVideoActivity.class).putExtra("URL", recordingObjects.get(position).getLink()).putExtra("DATE", new Date(Long.parseLong(keys.get(position).substring(10))).toString().substring(0, 19))));
        PushDownAnimation.setPushDownAnimationTo(holder.btnOpenRecording).setOnClickListener(v -> context.startActivity(new Intent(context, PlayVideoActivity.class).putExtra("URL", recordingObjects.get(position).getLink()).putExtra("DATE", new Date(Long.parseLong(keys.get(position).substring(10))).toString().substring(0, 19))));
        Date date = new Date(Long.parseLong(keys.get(position).substring(10)));
        holder.txtCapturedDate.setText("Recorded : " + date.toString().substring(0, 19));
    }

    @Override
    public int getItemCount() {
        return recordingObjects.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView layoutRecording;
        private final TextView txtCapturedDate;
        private final MaterialButton btnOpenRecording;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            layoutRecording = itemView.findViewById(R.id.layoutRecording);
            txtCapturedDate = itemView.findViewById(R.id.txtCapturedDate);
            btnOpenRecording = itemView.findViewById(R.id.btnOpenRecording);
        }
    }
}