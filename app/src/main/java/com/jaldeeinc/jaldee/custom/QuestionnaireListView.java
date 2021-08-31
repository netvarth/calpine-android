package com.jaldeeinc.jaldee.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.CheckBoxAdapter;
import com.jaldeeinc.jaldee.model.AnswerLine;
import com.jaldeeinc.jaldee.model.QuestionnaireCheckbox;
import com.jaldeeinc.jaldee.response.DataGridColumns;
import com.jaldeeinc.jaldee.response.GetQuestion;

import java.util.ArrayList;

public class QuestionnaireListView extends LinearLayout {

    private Context context;
    private AttributeSet attrs;
    private int styleAttr;

    private CustomTextViewSemiBold tvQuestionName;
    private CustomTextViewBold tvManditory;
    private CustomTextViewMedium tvHint;
    private CustomItalicTextViewNormal tvError;
    private RecyclerView rvCheckBoxes;
    private GetQuestion question;
    private DataGridColumns gridQuestion;
    private CheckBoxAdapter checkBoxAdapter;


    public QuestionnaireListView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public QuestionnaireListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        initView();
    }

    public QuestionnaireListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.attrs = attrs;
        this.styleAttr = defStyleAttr;
        initView();
    }


    private void initView() {
        inflate(context, R.layout.list_fielditem, this);

        tvQuestionName = findViewById(R.id.tv_questionName);
        tvManditory = findViewById(R.id.tv_manditory);
        tvHint = findViewById(R.id.tv_hint);
        tvError = findViewById(R.id.tv_error);
        rvCheckBoxes = findViewById(R.id.rv_checkBoxes);



    }

    public void setQuestionData(GetQuestion q) {

        question = q;

        setQuestionName(q.getLabelName());
        setHint(q.getHint());
        setMandatory(q.isMandatory() ? "*" : "");

        getCheckboxList();


        rvCheckBoxes.setLayoutManager(new LinearLayoutManager(getContext()));
        CheckBoxAdapter checkBoxAdapter = new CheckBoxAdapter(new ArrayList<>(), getContext());
        rvCheckBoxes.setAdapter(checkBoxAdapter);


    }

    public void setGridQuestionData(DataGridColumns gQuestion) {

        gridQuestion = gQuestion;

        setQuestionName(gQuestion.getLabel());
        setMandatory(gQuestion.isMandatory() ? "*" : "");

//        getCheckboxList();


        rvCheckBoxes.setLayoutManager(new LinearLayoutManager(getContext()));
        CheckBoxAdapter checkBoxAdapter = new CheckBoxAdapter(new ArrayList<>(), getContext());
        rvCheckBoxes.setAdapter(checkBoxAdapter);


    }

    public void setAnswerData(GetQuestion q) {

        question = q;

        setQuestionName(q.getLabelName());
        setHint(q.getHint());
        setMandatory(q.isMandatory() ? "*" : "");

    }

    private ArrayList<QuestionnaireCheckbox> getCheckboxList() {

        ArrayList<String> list = (ArrayList) question.getLabelValues();

        ArrayList<QuestionnaireCheckbox> checkBoxList = new ArrayList<>();
        for (String text : list) {

            QuestionnaireCheckbox checkBoxObj = new QuestionnaireCheckbox(false, text);
            checkBoxList.add(checkBoxObj);

        }

        for (QuestionnaireCheckbox checkbox : checkBoxList) {

//            for (String item : listModel.getSelectedItems()) {
//
//                if (item.equalsIgnoreCase(checkbox.getText())) {
//
//                    checkbox.setChecked(true);
//                }
//            }
        }

        return checkBoxList;
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

    public AnswerLine getListViewAnswerLine() {

        AnswerLine obj = new AnswerLine();
        obj.setLabelName(question.getLabelName());
        JsonObject answer = new JsonObject();
        JsonArray list = new JsonArray();

//        if (checkBoxAdapter != null) {
//
//            selectedCheckboxes = checkBoxAdapter.getSelectedCheckboxes();
//        }
//        for (QuestionnaireCheckbox item : selectedCheckboxes) {
//
//            list.add(item.getText());
//        }
        answer.add("list", list);

        obj.setAnswer(answer);


        return obj;

    }

    public boolean isValid() {


        return true;
    }
}


