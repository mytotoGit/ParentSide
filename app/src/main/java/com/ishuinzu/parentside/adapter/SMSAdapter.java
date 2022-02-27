package com.ishuinzu.parentside.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ishuinzu.parentside.R;
import com.ishuinzu.parentside.animation.PushDownAnimation;
import com.ishuinzu.parentside.object.SMSObject;

import java.util.Date;
import java.util.List;

public class SMSAdapter extends RecyclerView.Adapter<SMSAdapter.ViewHolder> {
    private Context context;
    private List<SMSObject> smsObjects;
    private LayoutInflater inflater;

    public SMSAdapter(Context context, List<SMSObject> smsObjects) {
        this.context = context;
        this.smsObjects = smsObjects;
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
    public SMSAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_sms, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull SMSAdapter.ViewHolder holder, int position) {
        PushDownAnimation.setPushDownAnimationTo(holder.layoutSMS).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        if (smsObjects.get(position).getType().equals("INBOX")) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 4, 180, 4);
            holder.layoutSMS.setLayoutParams(params);
        }
        if (smsObjects.get(position).getType().equals("SENT")) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(180, 4, 8, 4);
            holder.layoutSMS.setLayoutParams(params);
        }
        holder.txtDate.setText(new Date(Long.parseLong(smsObjects.get(position).getDate().getReceived())).toString().substring(0, 19));
        holder.txtMessage.setText(smsObjects.get(position).getBody());
    }

    @Override
    public int getItemCount() {
        return smsObjects.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RelativeLayout layoutSMS;
        private final TextView txtDate, txtMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            layoutSMS = itemView.findViewById(R.id.layoutSMS);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtMessage = itemView.findViewById(R.id.txtMessage);
        }
    }
}