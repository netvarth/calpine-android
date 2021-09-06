package com.jaldeeinc.jaldee.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.CheckBoxAdapter;
import com.jaldeeinc.jaldee.adapter.QGridItemAdapter;
import com.jaldeeinc.jaldee.model.AnswerLine;
import com.jaldeeinc.jaldee.model.DataGrid;
import com.jaldeeinc.jaldee.model.DataGridAnswerLine;
import com.jaldeeinc.jaldee.model.GridColumnAnswerLine;
import com.jaldeeinc.jaldee.model.QuestionnaireCheckbox;
import com.jaldeeinc.jaldee.response.DataGridColumns;
import com.jaldeeinc.jaldee.response.GetQuestion;

import java.util.ArrayList;

public class QuestionnaireGridItemView extends LinearLayout {

    private Context context;
    private AttributeSet attrs;
    private int styleAttr;

    private RecyclerView rvTextViews;
    private GetQuestion question;
    private DataGridColumns gridQuestion;
    private QGridItemAdapter qGridItemAdapter;


    public QuestionnaireGridItemView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public QuestionnaireGridItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        initView();
    }

    public QuestionnaireGridItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.attrs = attrs;
        this.styleAttr = defStyleAttr;
        initView();
    }


    private void initView() {
        inflate(context, R.layout.question_grid_item, this);

        rvTextViews = findViewById(R.id.rv_textViews);


        rvTextViews.setLayoutManager(new LinearLayoutManager(context));
        qGridItemAdapter = new QGridItemAdapter(new ArrayList<>(), context);
        rvTextViews.setAdapter(qGridItemAdapter);


    }

    public void setData(ArrayList<GridColumnAnswerLine> data) {

        rvTextViews.setLayoutManager(new LinearLayoutManager(context));
        qGridItemAdapter = new QGridItemAdapter(data, context);
        rvTextViews.setAdapter(qGridItemAdapter);
    }


}


