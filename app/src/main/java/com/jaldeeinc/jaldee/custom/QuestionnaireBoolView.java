package com.jaldeeinc.jaldee.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
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

public class QuestionnaireBoolView extends LinearLayout {

    private Context context;
    private AttributeSet attrs;
    private int styleAttr;

    private CustomTextViewSemiBold tvQuestionName;
    private CustomTextViewBold tvManditory;
    private CustomTextViewMedium tvHint;
    private CustomItalicTextViewNormal tvError;
    private RadioGroup radioGroup;
    private RadioButton rbYes, rbNo;
    private GetQuestion question;
    private DataGridColumns gridQuestions;


    public QuestionnaireBoolView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public QuestionnaireBoolView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        initView();
    }

    public QuestionnaireBoolView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.attrs = attrs;
        this.styleAttr = defStyleAttr;
        initView();
    }


    private void initView() {
        inflate(context, R.layout.boolean_item, this);

        tvQuestionName = findViewById(R.id.tv_questionName);
        tvManditory = findViewById(R.id.tv_manditory);
        radioGroup = findViewById(R.id.rg_radioGroup);
        rbYes = findViewById(R.id.rb_yes);
        rbNo = findViewById(R.id.rb_no);
        tvHint = findViewById(R.id.tv_hint);
        tvError = findViewById(R.id.tv_error);


    }

    public void setQuestionData(GetQuestion q) {

        question = q;

        setQuestionName(q.getLabelName());
        setHint(q.getHint());
        setMandatory(q.isMandatory() ? "*" : "");

        ArrayList values = (ArrayList) q.getLabelValues();

        if (values != null) {

            rbYes.setText(values.get(0).toString());
            rbNo.setText(values.get(1).toString());
        }

    }

    public void setGridQuestionData(DataGridColumns gQuestion) {

        gridQuestions = gQuestion;

        setQuestionName(gQuestion.getLabel());
        setMandatory(gQuestion.isMandatory() ? "*" : "");

        rbYes.setText("Yes");
        rbNo.setText("No");

        if (gQuestion.getAnswer() != null) {

            GridColumnAnswerLine answerLine = gQuestion.getAnswer();
            JsonObject column = answerLine.getColumn();

            if (column.get("bool") != null) {
                rbYes.setChecked(column.get("bool").getAsString().equalsIgnoreCase("yes"));
                rbNo.setChecked(column.get("bool").getAsString().equalsIgnoreCase("no"));
            }
        }

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

    public AnswerLine getBooleanAnswerLine() {

        AnswerLine obj = new AnswerLine();
        obj.setLabelName(question.getLabelName());

        JsonObject answer = new JsonObject();
        answer.addProperty("bool", rbYes.isChecked());

        obj.setAnswer(answer);

        return obj;

    }

    public GridColumnAnswerLine getGridBoolAnswerLine() {

        GridColumnAnswerLine obj = new GridColumnAnswerLine();
        obj.setColumnId(gridQuestions.getColumnId());

        JsonObject column = new JsonObject();
        column.addProperty("bool", rbYes.isChecked() ? "Yes" : (rbNo.isChecked() ? "No" : ""));

        obj.setColumn(column);

        return obj;

    }

    public boolean isValid() {

        if (!rbYes.isChecked() && !rbNo.isChecked()) {

            tvError.setVisibility(View.VISIBLE);
            tvError.setText("Select atleast one option");
            return false;
        } else {
            return true;
        }
    }
}

