package com.jaldeeinc.jaldee.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.custom.QuestionnaireBoolView;
import com.jaldeeinc.jaldee.custom.QuestionnaireDateView;
import com.jaldeeinc.jaldee.custom.QuestionnaireFileUploadView;
import com.jaldeeinc.jaldee.custom.QuestionnaireListView;
import com.jaldeeinc.jaldee.custom.QuestionnaireNumberView;
import com.jaldeeinc.jaldee.custom.QuestionnaireTextView;

import com.jaldeeinc.jaldee.model.DataGridAnswerLine;
import com.jaldeeinc.jaldee.model.GridColumnAnswerLine;
import com.jaldeeinc.jaldee.response.DataGridColumns;
import com.jaldeeinc.jaldee.response.DataGridProperties;
import com.jaldeeinc.jaldee.response.GetQuestion;
import com.jaldeeinc.jaldee.response.Questions;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DataGridFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DataGridFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String GRID_QUESTION = "gridQuestion";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LinearLayout llParentLayout;
    private CardView cvBack, cvAdd;

    private GetQuestion mQuestion = new GetQuestion();

    public DataGridFragment() {
        // Required empty public constructor
    }


    public static DataGridFragment newInstance(String param1, String param2) {
        DataGridFragment fragment = new DataGridFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static DataGridFragment newInstance(GetQuestion question) {
        DataGridFragment fragment = new DataGridFragment();
        Bundle args = new Bundle();
        args.putSerializable(GRID_QUESTION, question);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mQuestion = (GetQuestion) getArguments().getSerializable(GRID_QUESTION);

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

            try {
                createQuestionnaire(mQuestion);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


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


    private void createQuestionnaire(GetQuestion objQuestion) throws JSONException {

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
    }

    private void addDataToQuestionnaireGrid() {

        boolean isValid = false;

        DataGridAnswerLine answerLine = new DataGridAnswerLine();

        ArrayList<GridColumnAnswerLine> columnAnswersList  =  new ArrayList<>();

        int count = llParentLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = llParentLayout.getChildAt(i);

            if (view instanceof QuestionnaireTextView) {
                QuestionnaireTextView textView = (QuestionnaireTextView) view;

                isValid = textView.isValid();
                columnAnswersList.add(textView.getGridTextAnswerLine());

            } else if (view instanceof QuestionnaireBoolView) {
                QuestionnaireBoolView boolView = (QuestionnaireBoolView) view;

                isValid = boolView.isValid();
                columnAnswersList.add(boolView.getGridBoolAnswerLine());

            } else if (view instanceof QuestionnaireDateView) {
                QuestionnaireDateView dateView = (QuestionnaireDateView) view;

                isValid = dateView.isValid();
                columnAnswersList.add(dateView.getGridDateAnswerLine());

            } else if (view instanceof QuestionnaireNumberView) {
                QuestionnaireNumberView numberView = (QuestionnaireNumberView) view;

                isValid = numberView.isValid();
                columnAnswersList.add(numberView.getGridNumberAnswerLine());

            } else if (view instanceof QuestionnaireFileUploadView) {
                QuestionnaireFileUploadView fileUploadView = (QuestionnaireFileUploadView) view;

                isValid = fileUploadView.isValid();

            } else if (view instanceof QuestionnaireListView) {
                QuestionnaireListView listView = (QuestionnaireListView) view;

                isValid = listView.isValid();

            }
        }

        answerLine.setDataGrid(columnAnswersList);

        if (isValid){

            //sendDataToActivity
            
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }
}