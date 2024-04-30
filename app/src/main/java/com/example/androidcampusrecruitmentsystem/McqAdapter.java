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

    public class McqAdapter extends RecyclerView.Adapter<McqAdapter.ViewHolder> {
        List<McqQuestionItem> mcqQuestionItemList;
        RecyclerView recyclerView;
        Context context;

        public McqAdapter( RecyclerView recyclerView, Context context) {
            this.mcqQuestionItemList = new ArrayList<>();
            this.recyclerView = recyclerView;
            this.context = context;
        }
    public void addItem(McqQuestionItem mcqQuestionItem){
        mcqQuestionItemList.add(mcqQuestionItem);
        notifyDataSetChanged();
        recyclerView.post(() -> {
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(mcqQuestionItemList.size() - 1);
            if (viewHolder != null) {
                if (viewHolder instanceof McqAdapter.ViewHolder) {
                    McqAdapter.ViewHolder holder = (McqAdapter.ViewHolder) viewHolder;
                    holder.mcq_question.setText("");
                    holder.mcq_option1.setText("");
                    holder.mcq_option2.setText("");
                    holder.mcq_option3.setText("");
                    holder.mcq_option4.setText("");
                }
            }
        });
    }
    public List<McqQuestionItem> getMcqQuestionItemList() {
        return mcqQuestionItemList;
    }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mcq_test_item, parent, false);
            return new McqAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            McqQuestionItem item = mcqQuestionItemList.get(position);
            holder.bind(item);
        }

        @Override
        public int getItemCount() {
            return mcqQuestionItemList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            EditText mcq_question,mcq_option1,mcq_option2,mcq_option3,mcq_option4;
            TextView mcq_number;
            Button mcq_save ;
            ImageView mcqDelete, EditButton;
            ConstraintLayout constraintLayout;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mcq_number = itemView.findViewById(R.id.mcq_number);
                mcq_question = itemView.findViewById(R.id.mcq_question);
                mcq_option1 = itemView.findViewById(R.id.mcq_option1);
                mcq_option2 = itemView.findViewById(R.id.mcq_option2);
                mcq_option3 = itemView.findViewById(R.id.mcq_option3);
                mcq_option4 = itemView.findViewById(R.id.mcq_option4);
                mcq_save = itemView.findViewById(R.id.mcq_save);
                mcqDelete = itemView.findViewById(R.id.mcq_delete);
                constraintLayout = itemView.findViewById(R.id.detailsLayout);
                EditButton = itemView.findViewById(R.id.mcq_Edit);


                mcq_save.setOnClickListener(view -> {
                    if(mcq_question.getText().toString().isEmpty()){
                        mcq_question.setError("Enter Question");
                        return;
                    }
                    if(mcq_option1.getText().toString().isEmpty()){
                        mcq_option1.setError("Enter Option 1");
                        return;
                    }
                    if(mcq_option2.getText().toString().isEmpty()){
                        mcq_option2.setError("Enter Option 2");
                        return;
                    }
                    mcqQuestionItemList.get(getAdapterPosition()).setQuestion(mcq_question.getText().toString());
                    mcqQuestionItemList.get(getAdapterPosition()).setOption1(mcq_option1.getText().toString());
                    mcqQuestionItemList.get(getAdapterPosition()).setOption2(mcq_option2.getText().toString());
                    mcqQuestionItemList.get(getAdapterPosition()).setOption3(mcq_option3.getText().toString());
                    mcqQuestionItemList.get(getAdapterPosition()).setOption4(mcq_option4.getText().toString());
                    constraintLayout.setVisibility(View.GONE);
                });
                mcqDelete.setOnClickListener(v ->{
                    int currentPosition = getAdapterPosition();
                    if (currentPosition != RecyclerView.NO_POSITION) {
                        mcqQuestionItemList.remove(currentPosition);
                        notifyItemRemoved(currentPosition);
                        for (int i = 0 ; i < mcqQuestionItemList.size(); i++){
                            ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                            if(holder != null){
                                String number = String.valueOf(holder.getAdapterPosition()+1);
                                holder.mcq_number.setText("Question No "+ number);
                            }
                        }
                    }
                });
                EditButton.setOnClickListener(v -> {
                    if (constraintLayout.getVisibility() == View.VISIBLE) {
                        constraintLayout.setVisibility(View.GONE);
                    } else {
                        for (int i = 0 ; i < mcqQuestionItemList.size(); i++){
                            ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                            if(holder != null){
                                holder.constraintLayout.setVisibility(View.GONE);
                            }
                        }
                        constraintLayout.setVisibility(View.VISIBLE);
                    }
                });

            }
            public void bind(McqQuestionItem mcqQuestionItem){
                String number = String.valueOf(getAdapterPosition()+1);
                mcq_number.setText("Question No "+ number);
            }

        }
    }
