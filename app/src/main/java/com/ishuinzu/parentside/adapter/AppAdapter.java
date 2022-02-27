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
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.FirebaseDatabase;
import com.ishuinzu.parentside.R;
import com.ishuinzu.parentside.animation.PushDownAnimation;
import com.ishuinzu.parentside.app.Preferences;
import com.ishuinzu.parentside.object.AppObject;

import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {
    private Context context;
    private List<AppObject> appObjects;
    private LayoutInflater inflater;

    public AppAdapter(Context context, List<AppObject> appObjects) {
        this.context = context;
        this.appObjects = appObjects;
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
    public AppAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_app, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AppAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        PushDownAnimation.setPushDownAnimationTo(holder.layoutApp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        PushDownAnimation.setPushDownAnimationTo(holder.btnBlock).setOnClickListener(view -> FirebaseDatabase.getInstance().getReference().child("apps").child(Preferences.getInstance(context).getParent().getId()).child(appObjects.get(position).getName()).child("is_selected_lock").setValue("true"));
        PushDownAnimation.setPushDownAnimationTo(holder.btnUnblock).setOnClickListener(view -> FirebaseDatabase.getInstance().getReference().child("apps").child(Preferences.getInstance(context).getParent().getId()).child(appObjects.get(position).getName()).child("is_selected_lock").setValue("false"));
        if (appObjects.get(position).getIs_selected_lock().equals("true")) {
            holder.btnBlock.setVisibility(View.GONE);
            holder.btnUnblock.setVisibility(View.VISIBLE);
        } else {
            holder.btnUnblock.setVisibility(View.GONE);
            holder.btnBlock.setVisibility(View.VISIBLE);
        }
        holder.app_name.setText(appObjects.get(position).getName());
        holder.app_package_name.setText(appObjects.get(position).getPackage_name());
        Glide.with(context).load(appObjects.get(position).getIcon_link()).transition(new DrawableTransitionOptions().crossFade()).into(holder.app_image);
        try {
            Glide.with(context).load(appObjects.get(position).getIcon_link()).into(holder.app_image);
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            holder.app_image.setImageResource(R.drawable.img_logo);
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return appObjects.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout layoutApp;
        private final ImageView app_image;
        private final TextView app_name, app_package_name;
        private final MaterialButton btnBlock;
        private final MaterialButton btnUnblock;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            layoutApp = itemView.findViewById(R.id.layoutApp);
            app_image = itemView.findViewById(R.id.app_image);
            app_name = itemView.findViewById(R.id.app_name);
            app_package_name = itemView.findViewById(R.id.app_package_name);
            btnBlock = itemView.findViewById(R.id.btnBlock);
            btnUnblock = itemView.findViewById(R.id.btnUnblock);
        }
    }
}