package com.example.androidcampusrecruitmentsystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {
    private List<ConversationItem> conversationItemList;
    public ConversationAdapter(List<ConversationItem> conversationItemList) {
        this.conversationItemList = conversationItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_cards, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ConversationItem conversationItem = conversationItemList.get(position);
        holder.ConversationUser.setText(conversationItem.getConversationUser());
    }

    @Override
    public int getItemCount() {
        return conversationItemList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView ConversationUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ConversationUser = itemView.findViewById(R.id.user_name);
        }
    }
}
