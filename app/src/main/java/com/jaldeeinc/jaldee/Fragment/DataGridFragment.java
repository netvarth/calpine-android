package com.jaldeeinc.jaldee.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.custom.QuestionnaireBoolView;
import com.jaldeeinc.jaldee.custom.QuestionnaireDateView;
import com.jaldeeinc.jaldee.custom.QuestionnaireFileUploadView;
import com.jaldeeinc.jaldee.custom.QuestionnaireGridView;
import com.jaldeeinc.jaldee.custom.QuestionnaireListView;
import com.jaldeeinc.jaldee.custom.QuestionnaireNumberView;
import com.jaldeeinc.jaldee.custom.QuestionnaireTextView;

import com.jaldeeinc.jaldee.model.DataGrid;
import com.jaldeeinc.jaldee.model.DataGridAnswerLine;
import com.jaldeeinc.jaldee.model.GridColumnAnswerLine;
import com.jaldeeinc.jaldee.response.DataGridColumns;
import com.jaldeeinc.jaldee.response.GetQuestion;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DataGridFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DataGridFragment extends Fragment {

    private static final String GRID_QUESTION = "gridQuestion";
    private static final String GRID_ANSWERS = "gridAnswers";
    private static final String POSITION = "position";

    private LinearLayout llParentLayout;
    private CardView cvBack, cvAdd;
    QuestionnaireGridView gridView;
    int position = -1;

    private GetQuestion mQuestion = new GetQuestion();
    private DataGrid mAnswer = new DataGrid();


    public DataGridFragment() {
        // Required empty public constructor
    }


    public static DataGridFragment newInstance(GetQuestion question) {
        DataGridFragment fragment = new DataGridFragment();
        Bundle args = new Bundle();
        args.putSerializable(GRID_QUESTION, question);
        args.putSerializable(GRID_ANSWERS, null);
        args.putInt(POSITION, -1);
        fragment.setArguments(args);
        return fragment;
    }

    public static DataGridFragment newInstance(GetQuestion question, DataGrid answer, int position) {
        DataGridFragment fragment = new DataGridFragment();
        Bundle args = new Bundle();
        args.putSerializable(GRID_QUESTION, question);
        args.putSerializable(GRID_ANSWERS, answer);
        args.putInt(POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mQuestion = (GetQuestion) getArguments().getSerializable(GRID_QUESTION);
            mAnswer = (DataGrid) getArguments().getSerializable(GRID_ANSWERS);
            position = getArguments().getInt(POSITION, -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_data_grid, container, false);

        llParentLayout = view.findViewById(R.id.ll_parentLayout);
        cvBack = view.findViewById(R.id.cv_back);
        cvAdd = view.findViewById(R.id.cv_add);

        if (mQuestion != null) {

            if (mAnswer == null) {

                for (DataGridColumns question : mQuestion.getDataGridProperties().getGridList()) {

                    question.setAnswer(null);
                }
                createQuestionnaire(mQuestion);
            } else {

                for (DataGridColumns question : mQuestion.getDataGridProperties().getGridList()) {

                    for (GridColumnAnswerLine answer : mAnswer.getDataGridColumn()) {

                        if (question.getColumnId().equalsIgnoreCase(answer.getColumnId())) {

                            question.setAnswer(answer);
                        }
                    }

                }
                createQuestionnaire(mQuestion);
            }
        }

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    getFragmentManager().popBackStack("DataGrid", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    return true;
                }
                return false;
            }
        });

        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getFragmentManager().popBackStack("DataGrid", FragmentManager.POP_BACK_STACK_INCLUSIVE);

            }
        });

        cvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addDataToQuestionnaireGrid();
            }
        });


        return view;
    }

    public void setGridView(QuestionnaireGridView gView) {

        gridView = gView;

    }

    private void createQuestionnaire(GetQuestion objQuestion) {

        try {

            ArrayList<DataGridColumns> questionsList = objQuestion.getDataGridProperties().getGridList();

            for (DataGridColumns question : questionsList) {

                if (question.getDataType().equalsIgnoreCase("fileUpload")) {

                    QuestionnaireFileUploadView fileUploadView = new QuestionnaireFileUploadView(getContext());
                    fileUploadView.setGridQuestionData(question);

                    llParentLayout.addView(fileUploadView);

                } else if (question.getDataType().equalsIgnoreCase("plainText")) {

                    QuestionnaireTextView textView = new QuestionnaireTextView(getContext());
                    textView.setGridQuestionData(question);

                    llParentLayout.addView(textView);

                } else if (question.getDataType().equalsIgnoreCase("date")) {

                    QuestionnaireDateView dateView = new QuestionnaireDateView(getContext());
                    dateView.setGridQuestionData(question);

                    llParentLayout.addView(dateView);

                } else if (question.getDataType().equalsIgnoreCase("number")) {

                    QuestionnaireNumberView numberView = new QuestionnaireNumberView(getContext());
                    numberView.setGridQuestionData(question);

                    llParentLayout.addView(numberView);

                } else if (question.getDataType().equalsIgnoreCase("bool")) {

                    QuestionnaireBoolView boolView = new QuestionnaireBoolView(getContext());
                    boolView.setGridQuestionData(question);

                    llParentLayout.addView(boolView);

                } else if (question.getDataType().equalsIgnoreCase("list")) {

                    QuestionnaireListView listView = new QuestionnaireListView(getContext());
                    listView.setGridQuestionData(question);

                    llParentLayout.addView(listView);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void addDataToQuestionnaireGrid() {

        ArrayList<Boolean> validList = new ArrayList<>();

        cvAdd.setClickable(false);

        DataGrid dataGridObj = new DataGrid();
        ArrayList<GridColumnAnswerLine> columnAnswersList = new ArrayList<>();

        int count = llParentLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = llParentLayout.getChildAt(i);

            if (view instanceof QuestionnaireTextView) {
                QuestionnaireTextView textView = (QuestionnaireTextView) view;

                validList.add(textView.isValid());
                columnAnswersList.add(textView.getGridTextAnswerLine());

            } else if (view instanceof QuestionnaireBoolView) {
                QuestionnaireBoolView boolView = (QuestionnaireBoolView) view;

                validList.add(boolView.isValid());

                columnAnswersList.add(boolView.getGridBoolAnswerLine());

            } else if (view instanceof QuestionnaireDateView) {
                QuestionnaireDateView dateView = (QuestionnaireDateView) view;

                validList.add(dateView.isValid());

                columnAnswersList.add(dateView.getGridDateAnswerLine());

            } else if (view instanceof QuestionnaireNumberView) {
                QuestionnaireNumberView numberView = (QuestionnaireNumberView) view;

                validList.add(numberView.isValid());

                columnAnswersList.add(numberView.getGridNumberAnswerLine());

            } else if (view instanceof QuestionnaireFileUploadView) {
                QuestionnaireFileUploadView fileUploadView = (QuestionnaireFileUploadView) view;

                validList.add(fileUploadView.isValid());

                columnAnswersList.add(fileUploadView.getGridFileUploadAnswerLine());

            } else if (view instanceof QuestionnaireListView) {
                QuestionnaireListView listView = (QuestionnaireListView) view;

                validList.add(listView.isValid());

                columnAnswersList.add(listView.getGridListAnswerLine());

            }
        }

        dataGridObj.setDataGridColumn(columnAnswersList);

        if (validList.contains(false)) {

            cvAdd.setClickable(true);

        } else {

            gridView.updateDataGrid(dataGridObj, position);

            getFragmentManager().popBackStack("DataGrid", FragmentManager.POP_BACK_STACK_INCLUSIVE);

        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }
}