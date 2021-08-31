package com.jaldeeinc.jaldee.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.Fragment.DataGridFragment;
import com.jaldeeinc.jaldee.Fragment.HomeTabFragment;
import com.jaldeeinc.jaldee.Interface.IDataGrid;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.CustomQuestionnaire;
import com.jaldeeinc.jaldee.adapter.DataGridAdapter;
import com.jaldeeinc.jaldee.model.DataGridModel;
import com.jaldeeinc.jaldee.response.GetQuestion;

import java.util.ArrayList;

/**
 * Created by Sravan Velnati on 24/08/21.
 */

public class QuestionnaireGridView extends LinearLayout {

    private Context context;
    private AttributeSet attrs;
    private int styleAttr;

    private CustomTextViewSemiBold tvQuestionName;
    private CustomTextViewBold tvManditory;
    private LinearLayout llAdd;
    private CustomTextViewMedium tvHint;
    private CustomItalicTextViewNormal tvError;
    private RecyclerView rvDataTable;
    private GetQuestion question;
    private DataGridAdapter dataGridAdapter;
    private ArrayList<DataGridModel> dataList = new ArrayList<>();
    FragmentManager fragmentManager;
    IDataGrid iDataGrid;


    public QuestionnaireGridView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public QuestionnaireGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        initView();
    }

    public QuestionnaireGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.attrs = attrs;
        this.styleAttr = defStyleAttr;
        initView();
    }


    private void initView() {
        inflate(context, R.layout.data_grid, this);

        tvQuestionName = findViewById(R.id.tv_questionName);
        tvManditory = findViewById(R.id.tv_manditory);
        llAdd = findViewById(R.id.ll_add);
        tvHint = findViewById(R.id.tv_hint);
        tvError = findViewById(R.id.tv_error);
        rvDataTable = findViewById(R.id.rv_dataTable);

        dataGridAdapter  = new DataGridAdapter(dataList,context);
        rvDataTable.setLayoutManager(new LinearLayoutManager(context));
        rvDataTable.setAdapter(dataGridAdapter);

    }

    public void setQuestionData(GetQuestion q) {

        question = q;

        setQuestionName(q.getLabel());

    }

    public void setQuestionName(String questionName) {

        if (tvQuestionName != null && questionName != null) {
            tvQuestionName.setText(questionName);
        }
    }

    public void setMandatory(String mandatory) {
        if (tvManditory != null) {
            tvManditory.setText(mandatory);
        }
    }

    public void setHint(String hint) {

        if (tvHint != null) {
            tvHint.setText(hint);
        }
    }

    public void setError(String error) {

        if (tvError != null) {
            tvError.setText(error);
        }
    }


    public LinearLayout getLlAdd() {
        return llAdd;
    }
}

