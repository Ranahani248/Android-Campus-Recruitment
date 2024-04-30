package com.example.androidcampusrecruitmentsystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class questionAdapter extends RecyclerView.Adapter<questionAdapter.ViewHolder> {
    List<questionTestItem> questionTestItemList;
    RecyclerView recyclerView;
    Context context;

    public questionAdapter( RecyclerView recyclerView, Context context) {
        this.questionTestItemList = new ArrayList<>();
        this.recyclerView = recyclerView;
        this.context = context;
    }

    public void addItem(questionTestItem questiontestItem){
        questionTestItemList.add(questiontestItem);

        notifyDataSetChanged();
        recyclerView.post(() -> {
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(questionTestItemList.size() - 1);
            if (viewHolder != null) {
                if (viewHolder instanceof questionAdapter.ViewHolder) {
                    questionAdapter.ViewHolder holder = (questionAdapter.ViewHolder) viewHolder;
                    holder.question.setText("");

                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_test_item, parent, false);
        return new questionAdapter.ViewHolder(view);
    }
    public List<questionTestItem> getQuestionTestItemList() {
        return questionTestItemList;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        questionTestItem item = questionTestItemList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return questionTestItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        EditText question;
        TextView question_number;
        Button question_save ;
        ImageView questionDelete, questionEditButton;
        ConstraintLayout constraintLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.question);
            question_number = itemView.findViewById(R.id.question_number);
            question_save = itemView.findViewById(R.id.question_save);
            questionDelete = itemView.findViewById(R.id.question_delete);
            questionEditButton = itemView.findViewById(R.id.question_Edit);
            constraintLayout = itemView.findViewById(R.id.questiondetailsLayout);

            questionEditButton.setOnClickListener(view -> {

                if (constraintLayout.getVisibility() == View.VISIBLE) {
                    constraintLayout.setVisibility(View.GONE);
                } else {
                    for (int i = 0 ; i < questionTestItemList.size(); i++){
                        questionAdapter.ViewHolder holder = (questionAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                        if(holder != null){
                            holder.constraintLayout.setVisibility(View.GONE);
                        }
                    }
                    constraintLayout.setVisibility(View.VISIBLE);
                }
            });
            questionDelete.setOnClickListener(view -> {
                int currentPosition = getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    questionTestItemList.remove(currentPosition);
                    notifyItemRemoved(currentPosition);
                    for (int i = 0 ; i < questionTestItemList.size(); i++){
                        questionAdapter.ViewHolder holder = (questionAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                        if(holder != null){
                            String number = String.valueOf(holder.getAdapterPosition()+1);
                            holder.question_number.setText("Question No "+ number);
                        }
                    }
                }

            });
            question_save.setOnClickListener(view -> {

                if(question.getText().toString().isEmpty()){
                    question.setError("Enter Question");
                    return;
                }
                questionTestItemList.get(getAdapterPosition()).setQuestion(question.getText().toString());
                constraintLayout.setVisibility(View.GONE);
            });

        }
        public void bind(questionTestItem mcqQuestionItem){
            String number = String.valueOf(getAdapterPosition()+1);
            question_number.setText("Question No "+ number);
        }

    }

}
