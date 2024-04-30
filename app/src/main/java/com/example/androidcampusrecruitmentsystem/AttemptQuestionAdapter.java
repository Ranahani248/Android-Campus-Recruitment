package com.example.androidcampusrecruitmentsystem;



import android.content.Context;
import android.util.Log;
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
import java.util.Objects;

public class AttemptQuestionAdapter extends RecyclerView.Adapter<AttemptQuestionAdapter.ViewHolder> {
    List<AttemptquestionItem> mcqItemList;
    RecyclerView recyclerView;
    Context context;

    public AttemptQuestionAdapter( RecyclerView recyclerView, Context context, List<AttemptquestionItem> mcqItemList) {
        this.mcqItemList = mcqItemList;
        this.recyclerView = recyclerView;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attempt_question_item, parent, false);
        Log.d("TAG", "onCreateViewHolder: ");
        return new AttemptQuestionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AttemptquestionItem item = mcqItemList.get(position);
        holder.question.setText(mcqItemList.get(position).getQuestion());
        holder.bind(item);
    }

    @Override
    public int getItemCount() {

        return mcqItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView question;
        EditText answer;
        TextView question_number;
        Button question_save ;
        ImageView EditButton;
        ConstraintLayout constraintLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            question_number = itemView.findViewById(R.id.question_number);
            question = itemView.findViewById(R.id.question);
            answer = itemView.findViewById(R.id.answer);
            question_save = itemView.findViewById(R.id.question_save);
            constraintLayout = itemView.findViewById(R.id.questiondetailsLayout);
            EditButton = itemView.findViewById(R.id.question_Edit);

            question_save.setOnClickListener(view -> {
                if(answer.getText().toString().isEmpty()){
                    answer.setError("Answer cannot be empty");
                    answer.requestFocus();
                    return;
                }
                mcqItemList.get(getAdapterPosition()).setAnswer(answer.getText().toString());
                constraintLayout.setVisibility(View.GONE);
            });

            EditButton.setOnClickListener(v -> {
                if (constraintLayout.getVisibility() == View.VISIBLE) {
                    constraintLayout.setVisibility(View.GONE);
                } else {
                    for (int i = 0 ; i < mcqItemList.size(); i++){
                        ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                        if(holder != null){
                            holder.constraintLayout.setVisibility(View.GONE);
                        }
                    }
                    constraintLayout.setVisibility(View.VISIBLE);
                }
            });

        }
        public void bind(AttemptquestionItem mcqQuestionItem){
            String number = String.valueOf(getAdapterPosition()+1);
            question_number.setText("Question No "+ number);
        }

    }
}
