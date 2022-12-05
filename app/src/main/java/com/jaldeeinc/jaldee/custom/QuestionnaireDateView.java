package com.jaldeeinc.jaldee.custom;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.jaldeeinc.jaldee.Interface.IServiceOption;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.model.AnswerLine;
import com.jaldeeinc.jaldee.model.GridColumnAnswerLine;
import com.jaldeeinc.jaldee.response.DataGridColumns;
import com.jaldeeinc.jaldee.response.GetQuestion;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class QuestionnaireDateView extends LinearLayout implements DatePickerDialog.OnDateSetListener {

    private Context context;
    private AttributeSet attrs;
    private int styleAttr;

    CustomDateTimePicker custom;

    private TextView tvQuestionName, tvDate;
    private TextView tvManditory;
    private TextView tvHint;
    private TextView tvError;
    private ImageView ivCalender;
    private GetQuestion question;
    private DataGridColumns gridQuestion;
    IServiceOption iServiceOptionListOptionChange;
    float itemPrice;


    public QuestionnaireDateView(Context context) {
        super(context);
        this.context = context;
        initView();
    }
    public QuestionnaireDateView(Context context, IServiceOption iServiceOptionListOptionChange) {
        super(context);
        this.context = context;
        this.iServiceOptionListOptionChange = iServiceOptionListOptionChange;
        initView();
    }

    public QuestionnaireDateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        initView();
    }

    public QuestionnaireDateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.attrs = attrs;
        this.styleAttr = defStyleAttr;
        initView();
    }

    private void initView() {
        inflate(context, R.layout.date_item, this);

        tvQuestionName = findViewById(R.id.tv_questionName);
        tvDate = findViewById(R.id.tv_date);
        tvManditory = findViewById(R.id.tv_manditory);
        tvHint = findViewById(R.id.tv_hint);
        tvError = findViewById(R.id.tv_error);
        ivCalender = findViewById(R.id.iv_calender);


        custom = new CustomDateTimePicker(getContext(),
                new CustomDateTimePicker.ICustomDateTimeListener() {

                    @Override
                    public void onSet(Dialog dialog, Calendar calendarSelected,
                                      Date dateSelected, int year, String monthFullName,
                                      String monthShortName, int monthNumber, int day,
                                      String weekDayFullName, String weekDayShortName,
                                      int hour24, int hour12, int min, int sec,
                                      String AM_PM) {
                        tvDate.setText("");
                        tvDate.setText(calendarSelected.get(Calendar.DAY_OF_MONTH) + "-" +  (monthNumber + 1) + "-" + year);
                    }

                    @Override
                    public void onCancel() {

                    }
                });

        custom.setDate(Calendar.getInstance());


        ivCalender.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                custom.showDialog();
            }
        });
        tvDate.addTextChangedListener(new TextWatcher() {
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
                    after = editable.toString().trim();
                    if ((before.isEmpty() && !after.isEmpty()) || (!before.isEmpty() && after.isEmpty())) {
                        iServiceOptionListOptionChange.updateTotalPrice();
                    }
                }
            }
        });

    }

    public ImageView getIvCalender() {
        return ivCalender;
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

            if (column.get("date") != null) {

                if (column.get("date").getAsString().equalsIgnoreCase("05:30 AM")) {
                    setDate("");
                } else {
                    setDate(column.get("date").getAsString());
                }
            } else {
                setDate("");
            }
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

    public void setDate(String date) {
        if (tvDate != null) {
            date = date == null ? "" : date;
            tvDate.setText(date);
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

    public AnswerLine getDateAnswerLine() {

        AnswerLine obj = new AnswerLine();
        obj.setLabelName(question.getLabelName());

        JsonObject answer = new JsonObject();
        answer.addProperty("date", tvDate.getText().toString());

        obj.setAnswer(answer);

        return obj;

    }

    public GridColumnAnswerLine getGridDateAnswerLine() {

        GridColumnAnswerLine obj = new GridColumnAnswerLine();
        obj.setColumnId(gridQuestion.getColumnId());

        JsonObject column = new JsonObject();
        column.addProperty("date", tvDate.getText().toString());

        obj.setColumn(column);
        if(!tvDate.getText().toString().trim().isEmpty()) {
            obj.setPrice(itemPrice);
            obj.setQuantity(1);
        }
        return obj;

    }


    public boolean isValid() {

        if (gridQuestion.isMandatory() && tvDate.getText().toString().trim().equalsIgnoreCase("")) {

            tvError.setVisibility(View.VISIBLE);
            tvError.setText("Select " + gridQuestion.getColumnId());
            return false;
        } else {

            return true;
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {

        SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd",
                Locale.ENGLISH);
        Calendar mCalender = Calendar.getInstance();
        mCalender.set(Calendar.YEAR, year);
        mCalender.set(Calendar.MONTH, month);
        mCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String selectedDate = dateParser.format(mCalender.getTime());
        tvDate.setText(selectedDate);
    }
}


