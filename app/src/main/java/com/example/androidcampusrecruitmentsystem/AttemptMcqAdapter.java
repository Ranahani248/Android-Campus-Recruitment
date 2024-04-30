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

public class AttemptMcqAdapter extends RecyclerView.Adapter<AttemptMcqAdapter.ViewHolder> {
    List<AttempMcqItem> mcqItemList;
    RecyclerView recyclerView;
    Context context;

    public AttemptMcqAdapter( RecyclerView recyclerView, Context context, List<AttempMcqItem> mcqItemList) {
        this.mcqItemList = mcqItemList;
        this.recyclerView = recyclerView;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attempt_mcq_item, parent, false);
        Log.d("TAG", "onCreateViewHolder: ");
        return new AttemptMcqAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AttempMcqItem item = mcqItemList.get(position);
        holder.mcq_question.setText(mcqItemList.get(position).getQuestion());
        holder.mcq_option1.setText(mcqItemList.get(position).getOption1());
        holder.mcq_option2.setText(mcqItemList.get(position).getOption2());
        if(Objects.equals(mcqItemList.get(position).getOption3(), "")){
            holder.mcq_option3.setVisibility(View.GONE);
        }
        if (Objects.equals(mcqItemList.get(position).getOption4(), "")) {
            holder.mcq_option4.setVisibility(View.GONE);
        }
        holder.mcq_option3.setText(mcqItemList.get(position).getOption3());
        holder.mcq_option4.setText(mcqItemList.get(position).getOption4());
        holder.bind(item);
    }

    @Override
    public int getItemCount() {

        return mcqItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mcq_question,mcq_option1,mcq_option2,mcq_option3,mcq_option4;
        EditText mcq_answer;
        TextView mcq_number;
        Button mcq_save ;
        ImageView EditButton;
        ConstraintLayout constraintLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mcq_number = itemView.findViewById(R.id.mcq_number);
            mcq_question = itemView.findViewById(R.id.mcq_question);
            mcq_option1 = itemView.findViewById(R.id.mcq_option1);
            mcq_option2 = itemView.findViewById(R.id.mcq_option2);
            mcq_option3 = itemView.findViewById(R.id.mcq_option3);
            mcq_option4 = itemView.findViewById(R.id.mcq_option4);
            mcq_answer = itemView.findViewById(R.id.mcq_answer);
            mcq_save = itemView.findViewById(R.id.mcq_save);
            constraintLayout = itemView.findViewById(R.id.detailsLayout);
            EditButton = itemView.findViewById(R.id.mcq_Edit);

            mcq_save.setOnClickListener(view -> {
               if(mcq_answer.getText().toString().isEmpty()){
                   mcq_answer.setError("Answer cannot be empty");
                   mcq_answer.requestFocus();
                return;
               }
                mcqItemList.get(getAdapterPosition()).setAnswer(mcq_answer.getText().toString());
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
        public void bind(AttempMcqItem mcqQuestionItem){
            String number = String.valueOf(getAdapterPosition()+1);
            mcq_number.setText("Question No "+ number);
        }

    }
}
