package com.example.upscquiz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.viewholder> {

    private List<QuestionModel> questionmodelList;

    public BookmarkAdapter(List<QuestionModel> questionmodelList) {
        this.questionmodelList = questionmodelList;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_item,parent,false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        holder.setData(questionmodelList.get(position).getQuestion(),questionmodelList.get(position).getOptionCorrect());

    }

    @Override
    public int getItemCount() {
        return questionmodelList.size();
    }

    class viewholder extends RecyclerView.ViewHolder{
        private TextView question,answer;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.questionbm);
            answer = itemView.findViewById(R.id.answerbm);
        }
        private void setData(String ques, String ans) {
            this.question.setText(ques);
            this.answer.setText(ans);
        }

    }
}
