package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.QuestionnaireGridItemView;
import com.jaldeeinc.jaldee.model.AnswerLine;
import com.jaldeeinc.jaldee.model.DataGridAnswerLine;
import com.jaldeeinc.jaldee.model.GridColumnAnswerLine;

import java.util.ArrayList;

public class QGridItemAdapter extends RecyclerView.Adapter<QGridItemAdapter.ViewHolder> {

    ArrayList<GridColumnAnswerLine> answerLines = new ArrayList<>();
    public Context context;


    public QGridItemAdapter(ArrayList<GridColumnAnswerLine> answerLines, Context context) {
        this.answerLines = answerLines;
        this.context = context;
    }

    @NonNull
    @Override
    public QGridItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.q_grid_text, parent, false);
        return new QGridItemAdapter.ViewHolder(v, false);

    }

    @Override
    public void onBindViewHolder(@NonNull QGridItemAdapter.ViewHolder viewHolder, int position) {

        final GridColumnAnswerLine answer = answerLines.get(position);

        JsonObject column = answer.getColumn();

        if (answer.getColumnId() != null) {

            viewHolder.tvQuestion.setText(answer.getColumnId().toUpperCase());
        }

        if (column != null) {
            String answerString = null;
            if (column.has("plainText")) {
                answerString = column.get("plainText").getAsString();
            } else if (column.has("number")) {
                answerString = column.get("number").getAsString();
            } else if (column.has("bool")) {
                answerString = column.get("bool").getAsString();
            } else if (column.has("date")) {
                answerString = column.get("date").getAsString();
            } else if (column.has("list")) {

                JsonArray list = column.getAsJsonArray("list");

                ArrayList<String> values = new ArrayList<>();

                for (JsonElement e : list) {
                    values.add(e.toString());
                }

                String s = TextUtils.join(",", values);
                answerString = s;
            } else if (column.has("fileUpload")) {

                JsonArray fileUploadList = column.getAsJsonArray("fileUpload");
                answerString = fileUploadList.size() + " Attachment(s)";
            }

            if (answerString != null && !answerString.isEmpty()) {
                viewHolder.tvAnswer.setText(answerString);
            } else {
                viewHolder.tvAnswer.setText("N/A");
            }
        }
    }

    @Override
    public int getItemCount() {
        return answerLines.size();
    }

    public void updateData(ArrayList<GridColumnAnswerLine> data) {

        answerLines = data;
        notifyDataSetChanged();

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public CustomTextViewBold tvQuestion, tvAnswer;

        public ViewHolder(@NonNull View itemView, boolean isLoading) {

            super(itemView);

            tvQuestion = itemView.findViewById(R.id.tv_question);
            tvAnswer = itemView.findViewById(R.id.tv_answer);

        }
    }

}
