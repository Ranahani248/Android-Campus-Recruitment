package com.example.androidcampusrecruitmentsystem;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ConversationAdapter1 extends RecyclerView.Adapter<ConversationAdapter1.ViewHolder> {
    private List<String> recruiterIds;
    private FirebaseFirestore firestore;

    public ConversationAdapter1(List<String> recruiterIds) {
        this.recruiterIds = recruiterIds;
        firestore = FirebaseFirestore.getInstance();
    }

    // Method to update the data set
    public void updateData(List<String> recruiterIds) {
        this.recruiterIds.clear(); // Clear existing data
        this.recruiterIds.addAll(recruiterIds); // Add new data
        notifyDataSetChanged(); // Notify RecyclerView that data set has changed
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_cards, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the recruiter ID at the current position
        String recruiterId = recruiterIds.get(position);

        // Fetch recruiter details from Firestore using recruiterId
        firestore.collection("Recruiters").document(recruiterId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Get recruiter details
                                String recruiterName = document.getString("name");
                                String recruiterEmail = document.getString("email");

                                // Set recruiter details in the ViewHolder
                                holder.ConversationUser.setText(recruiterName);
                                holder.ConversationEmail.setText(recruiterEmail);

                                // Set onClickListener for the item
                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        // Open ChatActivity when item is clicked
                                        holder.openChatActivity(recruiterId, recruiterName, recruiterEmail);
                                    }
                                });
                            }
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return recruiterIds.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView ConversationUser;
        TextView ConversationEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ConversationUser = itemView.findViewById(R.id.user_name);
            ConversationEmail = itemView.findViewById(R.id.msgtext);
        }

        public void openChatActivity(String recruiterId, String recruiterName, String recruiterEmail) {
            Intent intent = new Intent(itemView.getContext(), RecuterChatActity.class);
            intent.putExtra("recruiterId", recruiterId);
            intent.putExtra("recruiterName", recruiterName);
            intent.putExtra("recruiterEmail", recruiterEmail);
            itemView.getContext().startActivity(intent);
        }
    }
}
