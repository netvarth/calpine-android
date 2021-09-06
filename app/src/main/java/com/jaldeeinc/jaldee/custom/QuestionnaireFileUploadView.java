package com.jaldeeinc.jaldee.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jaldeeinc.jaldee.Interface.IDataGrid;
import com.jaldeeinc.jaldee.Interface.IFilesInterface;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.DataGridAdapter;
import com.jaldeeinc.jaldee.adapter.FilesAdapter;
import com.jaldeeinc.jaldee.model.AnswerLine;
import com.jaldeeinc.jaldee.model.DataGridModel;
import com.jaldeeinc.jaldee.model.GridColumnAnswerLine;
import com.jaldeeinc.jaldee.model.QuestionnaireCheckbox;
import com.jaldeeinc.jaldee.response.DataGridColumns;
import com.jaldeeinc.jaldee.response.GetQuestion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class QuestionnaireFileUploadView extends LinearLayout implements IFilesInterface {

    private Context context;
    private AttributeSet attrs;
    private int styleAttr;

    private CustomTextViewSemiBold tvQuestionName;
    private CustomTextViewBold tvManditory;
    private CustomTextViewMedium tvHint, tvSupportedTypes;
    private CustomItalicTextViewNormal tvError;
    private RecyclerView rvFiles;
    private GetQuestion question;
    private DataGridColumns gridColumns;
    MultiSpinnerSearch filesSpinner;
    private IFilesInterface iFilesInterface;


    public QuestionnaireFileUploadView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public QuestionnaireFileUploadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        initView();
    }

    public QuestionnaireFileUploadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.attrs = attrs;
        this.styleAttr = defStyleAttr;
        initView();
    }


    private void initView() {
        inflate(context, R.layout.questnnarefile_upload, this);

        tvQuestionName = findViewById(R.id.tv_questionName);
        tvManditory = findViewById(R.id.tv_multipleFileManditory);
        tvHint = findViewById(R.id.tv_hint);
        tvError = findViewById(R.id.tv_error);
        tvSupportedTypes = findViewById(R.id.tv_supportedTypes);
        rvFiles = findViewById(R.id.rv_files);
        filesSpinner = findViewById(R.id.mfilesSpinner);
        iFilesInterface = this;


    }

    public void setQuestionData(GetQuestion q) {

        question = q;

        setQuestionName(q.getLabelName());
        setHint(q.getHint());
        setMandatory(q.isMandatory() ? "*" : "");

        // get file names and get files need to be done.

        rvFiles.setLayoutManager(new LinearLayoutManager(getContext()));
        FilesAdapter filesAdapter = new FilesAdapter(new ArrayList<>(), getContext(), false, iFilesInterface);
        filesAdapter.setLabelName(q.getLabelName());
        rvFiles.setAdapter(filesAdapter);

        filesSpinner.setItems(new ArrayList<>(), new MultiSpinnerListener() {
            @Override
            public void onItemsSelected(List<KeyPairBoolData> selectedItems) {

                selectedItems = selectedItems == null ? new ArrayList<>() : selectedItems;

                filesAdapter.updateData(selectedItems);

            }
        });

    }

    public void setGridQuestionData(DataGridColumns gQuestion) {

        gridColumns = gQuestion;

        setQuestionName(gQuestion.getLabel());
        setMandatory(gQuestion.isMandatory() ? "*" : "");

        ArrayList<KeyPairBoolData> filesList = new ArrayList<>();

        for (int i = 0; i < gQuestion.getFileProperties().getAllowedDocuments().size(); i++) {

            KeyPairBoolData obj = new KeyPairBoolData();
            obj.setName(gQuestion.getFileProperties().getAllowedDocuments().get(i));
            obj.setId(i);
            filesList.add(obj);

        }

        // get file names and get files need to be done.

        rvFiles.setLayoutManager(new LinearLayoutManager(getContext()));
        FilesAdapter filesAdapter = new FilesAdapter(filesList, getContext(), false, iFilesInterface);
        filesAdapter.setLabelName(gQuestion.getLabel());
        rvFiles.setAdapter(filesAdapter);

        filesSpinner.setItems(filesList, new MultiSpinnerListener() {
            @Override
            public void onItemsSelected(List<KeyPairBoolData> selectedItems) {

                selectedItems = selectedItems == null ? new ArrayList<>() : selectedItems;

                filesAdapter.updateData(selectedItems);

            }
        });

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

    public AnswerLine getFileUploadAnswerLine() {

        AnswerLine obj = new AnswerLine();
        obj.setLabelName(question.getLabelName());
        JsonObject answer = new JsonObject();
        JsonArray uploadList = new JsonArray();
        JsonObject fileInfo = new JsonObject();

        fileInfo.addProperty("index", 0);
        fileInfo.addProperty("caption", question.getFileProperties().getAllowedDocuments().get(0));
        fileInfo.addProperty("action",  "add");
        uploadList.add(fileInfo);
        answer.add("fileUpload", uploadList);
        obj.setAnswer(answer);


        return obj;

    }

    public GridColumnAnswerLine getGridFileUploadAnswerLine() {

        GridColumnAnswerLine obj = new GridColumnAnswerLine();
        obj.setColumnId(gridColumns.getColumnId());

        JsonObject column = new JsonObject();
        JsonArray list = new JsonArray();

        column.add("fileUpload", list);

        obj.setColumn(column);

        return obj;
    }


    @Override
    public void onFileUploadClick(KeyPairBoolData data, String labelName) {

    }

    @Override
    public void onCloseClick(KeyPairBoolData data) {

    }

    public boolean isValid() {



        return true;
    }


}


