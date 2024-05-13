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

public class CheckQuestionAdapter extends RecyclerView.Adapter<CheckQuestionAdapter.ViewHolder> {
    List<AttemptquestionItem> mcqItemList;
    RecyclerView recyclerView;
    Context context;

    public CheckQuestionAdapter( RecyclerView recyclerView, Context context, List<AttemptquestionItem> mcqItemList) {
        this.mcqItemList = mcqItemList;
        this.recyclerView = recyclerView;
        this.context = context;
    }
    public List<AttemptquestionItem> getquestionList(){
        return mcqItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_question_item, parent, false);
        Log.d("TAG", "onCreateViewHolder: ");
        return new CheckQuestionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AttemptquestionItem item = mcqItemList.get(position);
        holder.question.setText(mcqItemList.get(position).getQuestion());
        holder.answer.setText(mcqItemList.get(position).getAnswer());
        holder.bind(item);
    }
    @Override
    public int getItemCount() {

        return mcqItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView question, answer;
        TextView question_number;

        ImageView EditButton;
        ConstraintLayout constraintLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            question_number = itemView.findViewById(R.id.check_question_number);
            question = itemView.findViewById(R.id.check_question);
            answer = itemView.findViewById(R.id.check_answer);
            constraintLayout = itemView.findViewById(R.id.check_questiondetailsLayout);
            EditButton = itemView.findViewById(R.id.check_question_Edit);



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
