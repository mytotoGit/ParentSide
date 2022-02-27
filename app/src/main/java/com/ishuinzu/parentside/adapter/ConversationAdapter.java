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

import com.google.android.material.card.MaterialCardView;
import com.ishuinzu.parentside.R;
import com.ishuinzu.parentside.animation.PushDownAnimation;
import com.ishuinzu.parentside.object.ConversationObject;
import com.ishuinzu.parentside.ui.ListCallsActivity;
import com.ishuinzu.parentside.ui.ListMessagesActivity;

import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {
    private Context context;
    private List<ConversationObject> conversationObjects;
    private LayoutInflater inflater;
    private String type;

    public ConversationAdapter(Context context, List<ConversationObject> conversationObjects, String type) {
        this.context = context;
        this.conversationObjects = conversationObjects;
        this.inflater = LayoutInflater.from(context);
        this.type = type;
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
    public ConversationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_conversation, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ConversationAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        PushDownAnimation.setPushDownAnimationTo(holder.layoutConversation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equals("CALLS")) {
                    context.startActivity(new Intent(context, ListCallsActivity.class).putExtra("ADDRESS", conversationObjects.get(position).getAddress()));
                } else {
                    context.startActivity(new Intent(context, ListMessagesActivity.class).putExtra("ADDRESS", conversationObjects.get(position).getAddress()));
                }
            }
        });
        holder.txtFirstLetter.setText("" + conversationObjects.get(position).getAddress().charAt(0));
        holder.txtAddress.setText("" + conversationObjects.get(position).getAddress());
        holder.txtTotalMessages.setText("" + conversationObjects.get(position).getCount());
    }

    @Override
    public int getItemCount() {
        return conversationObjects.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView layoutConversation;
        private final TextView txtFirstLetter;
        private final TextView txtAddress;
        private final TextView txtTotalMessages;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            layoutConversation = itemView.findViewById(R.id.layoutConversation);
            txtFirstLetter = itemView.findViewById(R.id.txtFirstLetter);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            txtTotalMessages = itemView.findViewById(R.id.txtTotalMessages);
        }
    }
}