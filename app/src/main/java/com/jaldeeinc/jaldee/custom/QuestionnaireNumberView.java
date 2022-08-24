package com.jaldeeinc.jaldee.custom;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.jaldeeinc.jaldee.Interface.IServiceOption;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.model.AnswerLine;
import com.jaldeeinc.jaldee.model.GridColumnAnswerLine;
import com.jaldeeinc.jaldee.response.DataGridColumns;
import com.jaldeeinc.jaldee.response.GetQuestion;

public class QuestionnaireNumberView extends LinearLayout {

    private Context context;
    private AttributeSet attrs;
    private int styleAttr;

    private TextView tvQuestionName;
    private TextView tvManditory;
    private EditText etTextBox;
    private TextView tvHint;
    private TextView tvError;
    private GetQuestion question;
    private DataGridColumns gridQuestion;
    IServiceOption iServiceOptionListOptionChange;
    float itemPrice;


    public QuestionnaireNumberView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public QuestionnaireNumberView(Context context, IServiceOption iServiceOptionListOptionChange) {
        super(context);
        this.context = context;
        this.iServiceOptionListOptionChange = iServiceOptionListOptionChange;
        initView();
    }

    public QuestionnaireNumberView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        initView();
    }

    public QuestionnaireNumberView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.attrs = attrs;
        this.styleAttr = defStyleAttr;
        initView();
    }


    private void initView() {
        inflate(context, R.layout.numberfield_item, this);

        tvQuestionName = findViewById(R.id.tv_questionName);
        tvManditory = findViewById(R.id.tv_manditory);
        etTextBox = findViewById(R.id.et_textBox);
        tvHint = findViewById(R.id.tv_hint);
        tvError = findViewById(R.id.tv_error);
        etTextBox.addTextChangedListener(new TextWatcher() {
            String before = "", after = "";

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                before = charSequence.toString().trim();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (iServiceOptionListOptionChange != null) {

                    float valueAfter = 0, valueBefore = 0;
                    boolean isvalueAfterIsNumber = false;
                    boolean isvalueBeforeIsNumber = false;

                    after = editable.toString().trim();
                    if (!after.isEmpty()) {
                        try {
                            valueAfter = Float.parseFloat(after);
                            isvalueAfterIsNumber = true;
                        } catch (NumberFormatException e) {
                            isvalueAfterIsNumber = false;
                        }
                    } else {
                        valueAfter = 0;
                        isvalueAfterIsNumber = true;
                    }
                    if (before != null && !before.isEmpty()) {
                        try {
                            valueBefore = Float.parseFloat(before);
                            isvalueBeforeIsNumber = true;
                        } catch (NumberFormatException e) {
                            isvalueBeforeIsNumber = false;
                        }
                    } else {
                        valueBefore = 0;
                        isvalueBeforeIsNumber = true;
                    }
                    if ((valueBefore == 0 && valueAfter != 0 && isvalueAfterIsNumber) || (valueBefore != 0 && valueAfter == 0 && isvalueBeforeIsNumber)) {
                        iServiceOptionListOptionChange.updateTotalPrice();
                    }
                }
            }
        });
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

        if (gQuestion.getAnswer() != null) {

            GridColumnAnswerLine answerLine = gQuestion.getAnswer();
            JsonObject column = answerLine.getColumn();

            setTextBox(column.get("number").getAsString());
        }

    }

    public void setServiceOptionGridQuestionData(DataGridColumns gQuestion, float itemPrice) {
        setGridQuestionData(gQuestion);
        this.itemPrice = itemPrice;
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
            text = text == null ? "" : text;
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

    public AnswerLine getNumberAnswerLine() {

        AnswerLine obj = new AnswerLine();
        obj.setLabelName(question.getLabelName());

        JsonObject answer = new JsonObject();
        answer.addProperty("number", etTextBox.getText().toString());

        obj.setAnswer(answer);


        return obj;

    }

    public GridColumnAnswerLine getGridNumberAnswerLine() {

        GridColumnAnswerLine obj = new GridColumnAnswerLine();
        obj.setColumnId(gridQuestion.getColumnId());

        JsonObject column = new JsonObject();
        column.addProperty("number", etTextBox.getText().toString());

        obj.setColumn(column);
        if(!etTextBox.getText().toString().trim().isEmpty()) {
            obj.setPrice(itemPrice);
            obj.setQuantity(1);
        }
        return obj;

    }

    public boolean isValid() {

        if (gridQuestion.isMandatory() && etTextBox.getText().toString().trim().equalsIgnoreCase("")) {

            tvError.setVisibility(View.VISIBLE);
            tvError.setText("Enter " + gridQuestion.getColumnId());
            return false;
        } else {

            return true;
        }
    }
}


