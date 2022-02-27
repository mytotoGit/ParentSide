package com.ishuinzu.parentside.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ishuinzu.parentside.R;
import com.ishuinzu.parentside.animation.PushDownAnimation;
import com.ishuinzu.parentside.object.ScreenshotObject;

import java.util.Date;
import java.util.List;

public class ScreenshotAdapter extends RecyclerView.Adapter<ScreenshotAdapter.ViewHolder> {
    private Context context;
    private List<String> keys;
    private List<ScreenshotObject> screenshotObjects;
    private LayoutInflater inflater;

    public ScreenshotAdapter(Context context, List<ScreenshotObject> screenshotObjects, List<String> keys) {
        this.context = context;
        this.screenshotObjects = screenshotObjects;
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
    public ScreenshotAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_screenshot, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ScreenshotAdapter.ViewHolder holder, int position) {
        PushDownAnimation.setPushDownAnimationTo(holder.layoutScreenshot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        Glide.with(context).load(screenshotObjects.get(position).getLink()).error(R.drawable.img_logo_transparent).into(holder.imgScreenshot);
        Date date = new Date(Long.parseLong(keys.get(position).substring(3)));
        holder.txtCapturedDate.setText(date.toString().substring(0, 19));
    }

    @Override
    public int getItemCount() {
        return screenshotObjects.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout layoutScreenshot;
        private final TextView txtCapturedDate;
        private final ImageView imgScreenshot;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            layoutScreenshot = itemView.findViewById(R.id.layoutScreenshot);
            txtCapturedDate = itemView.findViewById(R.id.txtCapturedDate);
            imgScreenshot = itemView.findViewById(R.id.imgScreenshot);
        }
    }
}