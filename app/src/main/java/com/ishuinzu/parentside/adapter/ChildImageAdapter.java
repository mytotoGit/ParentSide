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
import com.ishuinzu.parentside.object.CameraImageObject;

import java.util.Date;
import java.util.List;

public class ChildImageAdapter extends RecyclerView.Adapter<ChildImageAdapter.ViewHolder> {
    private Context context;
    private List<CameraImageObject> cameraImageObjects;
    private LayoutInflater inflater;

    public ChildImageAdapter(Context context, List<CameraImageObject> cameraImageObjects) {
        this.context = context;
        this.cameraImageObjects = cameraImageObjects;
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
    public ChildImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_child_image, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ChildImageAdapter.ViewHolder holder, int position) {
        PushDownAnimation.setPushDownAnimationTo(holder.layoutChildImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        Glide.with(context).load(cameraImageObjects.get(position).getLink()).error(R.drawable.img_logo_transparent).into(holder.imgScreenshot);
        Date date = new Date(cameraImageObjects.get(position).getTime());
        holder.txtCapturedDate.setText(date.toString().substring(0, 19));
    }

    @Override
    public int getItemCount() {
        return cameraImageObjects.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout layoutChildImage;
        private final TextView txtCapturedDate;
        private final ImageView imgScreenshot;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            layoutChildImage = itemView.findViewById(R.id.layoutChildImage);
            txtCapturedDate = itemView.findViewById(R.id.txtCapturedDate);
            imgScreenshot = itemView.findViewById(R.id.imgScreenshot);
        }
    }
}