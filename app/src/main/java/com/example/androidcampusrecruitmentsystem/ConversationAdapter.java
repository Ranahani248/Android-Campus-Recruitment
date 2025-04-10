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

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {
    private List<String> studentIds;
    private FirebaseFirestore firestore;

    public ConversationAdapter(List<String> studentIds) {
        this.studentIds = studentIds;
        firestore = FirebaseFirestore.getInstance();
    }

    // Method to update the data set
// Modify updateData() method
    public void updateData(List<String> studentIds) {
        this.studentIds.clear();
        this.studentIds.addAll(studentIds);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_cards, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the student ID at the current position
        String studentId = studentIds.get(position);


        firestore.collection("Students").document(studentId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Get student details
                                String studentName = document.getString("name");
                                String studentEmail = document.getString("email");

                                // Set student details in the ViewHolder
                                holder.ConversationUser.setText(studentName);
                                holder.ConversationEmail.setText(studentEmail);

                                // Set onClickListener for the item
                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        // Open ChatActivity when item is clicked
                                        holder.openChatActivity(studentId, studentName, studentEmail);
                                    }
                                });
                            }
                        }
                    }
                });
    }
    @Override
    public int getItemCount() {
        return studentIds.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView ConversationUser;
        TextView ConversationEmail;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ConversationUser = itemView.findViewById(R.id.user_name);
            ConversationEmail = itemView.findViewById(R.id.msgtext);
        }
        public void openChatActivity(String studentId, String studentName, String studentEmail) {
            Intent intent = new Intent(itemView.getContext(), ChatActivity.class);
            intent.putExtra("studentId", studentId);
            intent.putExtra("studentName", studentName);
            intent.putExtra("studentEmail", studentEmail);
            itemView.getContext().startActivity(intent);
        }
    }
}