package com.jaldeeinc.jaldee.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jaldeeinc.jaldee.Interface.IServiceOption;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.CheckBoxAdapter;
import com.jaldeeinc.jaldee.model.AnswerLine;
import com.jaldeeinc.jaldee.model.GridColumnAnswerLine;
import com.jaldeeinc.jaldee.model.QuestionnaireCheckbox;
import com.jaldeeinc.jaldee.response.DataGridColumns;
import com.jaldeeinc.jaldee.response.GetQuestion;
import com.jaldeeinc.jaldee.response.ListProperties;

import java.util.ArrayList;

public class QuestionnaireListView extends LinearLayout {

    private Context context;
    private AttributeSet attrs;
    private int styleAttr;

    private TextView tvQuestionName;
    private TextView tvManditory;
    private TextView tvHint;
    private TextView tvError;
    private RecyclerView rvCheckBoxes;
    private GetQuestion question;
    private DataGridColumns gridQuestion;
    private CheckBoxAdapter checkBoxAdapter;
    IServiceOption iServiceOptionListOptionChange;

    public QuestionnaireListView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public QuestionnaireListView(Context context, IServiceOption iServiceOptionListOptionChange) {
        super(context);
        this.context = context;
        this.iServiceOptionListOptionChange = iServiceOptionListOptionChange;
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


        rvCheckBoxes.setLayoutManager(new LinearLayoutManager(getContext()));
        checkBoxAdapter = new CheckBoxAdapter(new ArrayList<>(), getContext());
        rvCheckBoxes.setAdapter(checkBoxAdapter);


    }

    public void setGridQuestionData(DataGridColumns gQuestion) {

        gridQuestion = gQuestion;

        setQuestionName(gQuestion.getLabel());
        setMandatory(gQuestion.isMandatory() ? "*" : "");

        ArrayList<QuestionnaireCheckbox> checkBoxList = new ArrayList<>();
        for (String text : gQuestion.getListProperties().getValues()) {

            QuestionnaireCheckbox checkBoxObj = new QuestionnaireCheckbox(false, text);
            checkBoxList.add(checkBoxObj);

        }

        if (gQuestion.getAnswer() != null) {

            GridColumnAnswerLine answerLine = gQuestion.getAnswer();
            JsonObject column = answerLine.getColumn();

            JsonArray array = column.getAsJsonArray("list");

            for (QuestionnaireCheckbox c : checkBoxList) {

                for (JsonElement e : array) {

                    if (c.getText().equalsIgnoreCase(e.getAsString())) {

                        c.setChecked(true);
                    }
                }
            }
        }

        rvCheckBoxes.setLayoutManager(new LinearLayoutManager(getContext()));
        checkBoxAdapter = new CheckBoxAdapter(checkBoxList, gQuestion.getListProperties().getMaxAnswers(), getContext());
        rvCheckBoxes.setAdapter(checkBoxAdapter);


    }

    public void setServiceOptionGridQuestionData(DataGridColumns gQuestion, ArrayList<QuestionnaireCheckbox> checkBoxList, int maxAnswerable) {

        gridQuestion = gQuestion;

        setQuestionName(gQuestion.getLabel());
        setMandatory(gQuestion.isMandatory() ? "*" : "");

       /* ArrayList<QuestionnaireCheckbox> checkBoxList = new ArrayList<>();
        for (String text : gQuestion.getListProperties().getValues()) {

            QuestionnaireCheckbox checkBoxObj = new QuestionnaireCheckbox(false, text);
            checkBoxList.add(checkBoxObj);

        }*/

        if (gQuestion.getAnswer() != null) {

            GridColumnAnswerLine answerLine = gQuestion.getAnswer();
            JsonObject column = answerLine.getColumn();

            JsonArray array = column.getAsJsonArray("list");

            for (QuestionnaireCheckbox c : checkBoxList) {

                for (JsonElement e : array) {

                    if (c.getText().equalsIgnoreCase(e.getAsString())) {

                        c.setChecked(true);
                    }
                }
            }
        }

        rvCheckBoxes.setLayoutManager(new LinearLayoutManager(getContext()));
        checkBoxAdapter = new CheckBoxAdapter(checkBoxList, maxAnswerable, getContext(), iServiceOptionListOptionChange);
        rvCheckBoxes.setAdapter(checkBoxAdapter);
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

    public AnswerLine getListViewAnswerLine() {

        AnswerLine obj = new AnswerLine();
        obj.setLabelName(question.getLabelName());
        JsonObject answer = new JsonObject();
        JsonArray list = new JsonArray();
        ArrayList<QuestionnaireCheckbox> selectedCheckboxes = new ArrayList<>();

        if (checkBoxAdapter != null) {

            selectedCheckboxes = checkBoxAdapter.getSelectedCheckboxes();
        }
        for (QuestionnaireCheckbox item : selectedCheckboxes) {

            list.add(item.getText());
        }
        answer.add("list", list);

        obj.setAnswer(answer);


        return obj;

    }

    public GridColumnAnswerLine getGridListAnswerLine() {

        GridColumnAnswerLine obj = new GridColumnAnswerLine();
        obj.setColumnId(gridQuestion.getColumnId());

        JsonObject column = new JsonObject();
        JsonArray list = new JsonArray();

        ArrayList<QuestionnaireCheckbox> selectedCheckboxes = new ArrayList<>();

        if (checkBoxAdapter != null) {

            selectedCheckboxes = checkBoxAdapter.getSelectedCheckboxes();
        }
        float price = 0;
        for (QuestionnaireCheckbox item : selectedCheckboxes) {

            list.add(item.getText());
            float itemPrice = 0;
            if (item.getPrice() == null) {
                itemPrice = 0;
            } else {
                itemPrice = item.getPrice();
            }
            price = price + itemPrice;

            obj.setQuantity(1);
        }

        column.add("list", list);

        obj.setColumn(column);
        obj.setPrice(price);
        return obj;

    }


    public boolean isValid() {

        if (gridQuestion.isMandatory()) {
            ListProperties properties = new ListProperties();
            properties = gridQuestion.getListProperties();
            int minAnswers = properties.getMinAnswers();
            int maxAnswers = properties.getMaxAnswers();
            int checkedCount = 0;
            if (checkBoxAdapter != null) {
                checkedCount = checkBoxAdapter.getCheckedCount();
            }
            if (checkedCount <= 0) {
                tvError.setVisibility(View.VISIBLE);
                tvError.setText("Please select atleast " + 1 + " Item");
                return false;
            }

            if (minAnswers == 0 && maxAnswers == 0) {

                return true;
            }
            if (checkedCount < minAnswers) {

                tvError.setVisibility(View.VISIBLE);
                tvError.setText("Please select atleast " + minAnswers + " Item");
                return false;
            } else if (checkedCount > maxAnswers) {

                tvError.setVisibility(View.VISIBLE);
                tvError.setText("You can only select upto " + maxAnswers + " Answers");
                return false;

            } else {

                return true;
            }
        } else {

            return true;
        }
    }
}


