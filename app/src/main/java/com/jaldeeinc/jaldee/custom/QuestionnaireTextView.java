package com.jaldeeinc.jaldee.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.jaldeeinc.jaldee.Interface.IDataGrid;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.DataGridAdapter;
import com.jaldeeinc.jaldee.model.AnswerLine;
import com.jaldeeinc.jaldee.model.DataGridModel;
import com.jaldeeinc.jaldee.model.GridColumnAnswerLine;
import com.jaldeeinc.jaldee.response.DataGridColumns;
import com.jaldeeinc.jaldee.response.GetQuestion;

import java.util.ArrayList;

public class QuestionnaireTextView extends LinearLayout {

    private Context context;
    private AttributeSet attrs;
    private int styleAttr;

    private CustomTextViewSemiBold tvQuestionName;
    private CustomTextViewBold tvManditory;
    private CustomEditTextRegular etTextBox;
    private CustomTextViewMedium tvHint;
    private CustomItalicTextViewNormal tvError;
    private GetQuestion question;
    private DataGridColumns gridQuestion;


    public QuestionnaireTextView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public QuestionnaireTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        initView();
    }

    public QuestionnaireTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.attrs = attrs;
        this.styleAttr = defStyleAttr;
        initView();
    }


    private void initView() {
        inflate(context, R.layout.edittext_field, this);

        tvQuestionName = findViewById(R.id.tv_questionName);
        tvManditory = findViewById(R.id.tv_manditory);
        etTextBox = findViewById(R.id.et_textBox);
        tvHint = findViewById(R.id.tv_hint);
        tvError = findViewById(R.id.tv_error);

    }

    public void setQuestionData(GetQuestion q) {

        question = q;

        setQuestionName(q.getLabelName());
        setHint(q.getHint());
        setMandatory(q.isMandatory() ? "*" : "");

    }

    public void setGridQuestionData(DataGridColumns gQuestion) {

        gridQuestion = gQuestion;

        setQuestionName(gQuestion.getLabel());
        setMandatory(gQuestion.isMandatory() ? "*" : "");

    }

    public void setAnswerData(GetQuestion q) {

        question = q;

        setQuestionName(q.getLabelName());
        setHint(q.getHint());
        setMandatory(q.isMandatory() ? "*" : "");

    }

    public void setQuestionName(String questionName) {

        if (tvQuestionName != null && questionName != null) {
            tvQuestionName.setText(questionName);
        }
    }

    public void setTextBox(String text) {

        if (etTextBox != null) {
            etTextBox.setText(text);
        }
    }

    public void setMandatory(String mandatory) {
        if (mandatory.trim().equalsIgnoreCase("")) {
            tvManditory.setVisibility(View.GONE);
        } else {
            tvManditory.setVisibility(View.VISIBLE);
        }
    }

    public void setHint(String hint) {

        hint = (hint == null) ? "" : hint;
        tvHint.setText(hint);
        if (hint.trim().equalsIgnoreCase("")) {
            tvHint.setVisibility(View.GONE);
        } else {
            tvHint.setVisibility(View.VISIBLE);
        }
    }

    public void setError(String error) {

        error = (error == null) ? "" : error;

        if (error.trim().equalsIgnoreCase("")) {
            tvError.setVisibility(View.GONE);
        } else {
            tvError.setVisibility(View.VISIBLE);
        }
    }

    public AnswerLine getTextAnswerLine() {

        AnswerLine obj = new AnswerLine();
        obj.setLabelName(question.getLabelName());

        JsonObject answer = new JsonObject();
        answer.addProperty("plainText", etTextBox.getText().toString());

        obj.setAnswer(answer);


        return obj;

    }

    public GridColumnAnswerLine getGridTextAnswerLine() {

        GridColumnAnswerLine obj = new GridColumnAnswerLine();
        obj.setColumnId(gridQuestion.getColumnId());

        JsonObject column = new JsonObject();
        column.addProperty("plainText", etTextBox.getText().toString());

        obj.setColumn(column);

        return obj;

    }

    public boolean isValid() {


        return true;
    }
}


