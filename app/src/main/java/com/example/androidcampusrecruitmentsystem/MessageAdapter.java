package com.example.androidcampusrecruitmentsystem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private final List<Messages> userMessagesList;
    private final FirebaseAuth mAuth;
    private final FirebaseFirestore db;

    public MessageAdapter(List<Messages> userMessagesList) {
        this.userMessagesList = userMessagesList;
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_messages_layout, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Messages message = userMessagesList.get(position);
        String messageType = message.getType();

        // Check the message type
        if (messageType.equals("text")) {
            String senderId = message.getFrom();
            String currentUserId = mAuth.getCurrentUser().getUid();

            // If the message is from the current user (sender)
            if (senderId.equals(currentUserId)) {
                holder.senderMessageText.setVisibility(View.VISIBLE);
                holder.receiverMessageText.setVisibility(View.GONE);

                holder.senderMessageText.setText(message.getMessage());
                holder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
                holder.senderMessageText.setTextColor(Color.BLACK);
                holder.senderMessageText.setText(message.getMessage() + "\n \n" + message.getTime() + " - " + message.getDate());
            } else { // The message is from the other user (receiver)
                holder.senderMessageText.setVisibility(View.GONE);
                holder.receiverMessageText.setVisibility(View.VISIBLE);

                holder.receiverMessageText.setText(message.getMessage());
                holder.receiverMessageText.setBackgroundResource(R.drawable.reciever_messages_layout);
                holder.receiverMessageText.setTextColor(Color.BLACK);
                holder.receiverMessageText.setText(message.getMessage() + "\n \n" + message.getTime() + " - " + message.getDate());

            }
        }
        if (message.getFrom().equals(mAuth.getCurrentUser().getUid())) {

            // Long click listener for deleting messages
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage("Are you sure you want to delete this message?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Call deleteMessage and pass the itemView
                                    deleteMessage(position, holder.itemView);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                    return true;
                }
            });
        }


    }



    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }
    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView senderMessageText, receiverMessageText, receiverName;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMessageText = itemView.findViewById(R.id.sender_messsage_text);
            receiverMessageText = itemView.findViewById(R.id.receiver_message_text);

//            receiverName = itemView.findViewById(R.id.receiver_name);
        }
    }
    private void deleteMessage(int position, View itemView) {
        if (position >= 0 && position < userMessagesList.size()) {
            String senderId = mAuth.getCurrentUser().getUid();
            String receiverId = userMessagesList.get(position).getTo();
            String messageId = userMessagesList.get(position).getMessageID();

            // Delete message from sender's collection
            db.collection("Messages")
                    .document(senderId)
                    .collection("list")
                    .document(receiverId)
                    .collection("msgs")
                    .document(messageId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        // Message deleted successfully from sender's collection
                        Toast.makeText(itemView.getContext(), "Message deleted", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Failed to delete message from sender's collection
                        Toast.makeText(itemView.getContext(), "Failed to delete message from sender's collection", Toast.LENGTH_SHORT).show();
                    });

            // Delete message from receiver's collection
            db.collection("Messages")
                    .document(receiverId)
                    .collection("list")
                    .document(senderId)
                    .collection("msgs")
                    .document(messageId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        // Message deleted successfully from receiver's collection
                        Toast.makeText(itemView.getContext(), "Message deleted", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Failed to delete message from receiver's collection
                        Toast.makeText(itemView.getContext(), "Failed to delete message from receiver's collection", Toast.LENGTH_SHORT).show();
                    });

            // Remove the item from the list
            userMessagesList.remove(position);
            notifyItemRemoved(position);
        } else {
            // Handle invalid position gracefully, perhaps show a message to the user
            Toast.makeText(itemView.getContext(), "Invalid position", Toast.LENGTH_SHORT).show();
        }
    }


    // Method to delete received message
}