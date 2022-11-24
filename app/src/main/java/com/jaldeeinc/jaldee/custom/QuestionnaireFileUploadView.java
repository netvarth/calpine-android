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
import com.jaldeeinc.jaldee.Interface.IFilesInterface;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.adapter.FilesAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.model.AnswerLine;
import com.jaldeeinc.jaldee.model.GridColumnAnswerLine;
import com.jaldeeinc.jaldee.model.MediaTypeAndExtention;
import com.jaldeeinc.jaldee.response.DataGridColumns;
import com.jaldeeinc.jaldee.response.GetQuestion;

import java.util.ArrayList;
import java.util.List;

public class QuestionnaireFileUploadView extends LinearLayout implements IFilesInterface {

    private Context context;
    private AttributeSet attrs;
    private int styleAttr;

    private TextView tvQuestionName;
    private TextView tvManditory;
    private TextView tvHint, tvSupportedTypes;
    private TextView tvError;
    private RecyclerView rvFiles;
    private GetQuestion question;
    private DataGridColumns gridColumns;
    MultiSpinnerSearch filesSpinner;
    private FilesAdapter filesAdapter;
    private IFilesInterface iFilesInterface, listener;
    float itemPrice;


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
        if (gQuestion.getFileProperties().getFileTypes() != null) {
            setSupportedTypes(gQuestion.getFileProperties().getFileTypes().toString());
        } else {
            setSupportedTypes("jpeg,png,jpg,pdf,wmv,mp4,webm,flw,mov,avi,.wmv,.mp4,.webm,.flw,.mov,.avi,mpeg,.mpeg,wav,.wav");
        }

        ArrayList<KeyPairBoolData> filesList = new ArrayList<>();

        for (int i = 0; i < gQuestion.getFileProperties().getAllowedDocuments().size(); i++) {

            KeyPairBoolData obj = new KeyPairBoolData();
            obj.setName(gQuestion.getFileProperties().getAllowedDocuments().get(i));
            obj.setId(i);
            filesList.add(obj);

        }

        if (gQuestion.getAnswer() != null) {

            GridColumnAnswerLine answerLine = gQuestion.getAnswer();
            JsonObject column = answerLine.getColumn();

            if (column.get("fileUpload") != null) {

                JsonArray fileUploadList = column.getAsJsonArray("fileUpload");

                for (KeyPairBoolData k : filesList) {

                    for (JsonElement f : fileUploadList) {

                        JsonObject fileObj = f.getAsJsonObject();
                        String name = fileObj.get("caption").getAsString();
                        String type = fileObj.get("type").getAsString();
                        String filePath = "";
                        if (fileObj.get("path") != null && !fileObj.get("path").getAsString().trim().equalsIgnoreCase("")) {
                            filePath = fileObj.get("path").getAsString();
                        } else if (fileObj.get("s3path") != null && !fileObj.get("s3path").getAsString().trim().equalsIgnoreCase("")){
                            filePath = fileObj.get("s3path").getAsString();
                        }
                        if (k.getName().equalsIgnoreCase(name)) {

                            k.setImagePath(filePath);
                            k.setType(type);
                        }
                    }
                }

            }
        }


        // get file names and get files need to be done.

        rvFiles.setLayoutManager(new LinearLayoutManager(getContext()));
        filesAdapter = new FilesAdapter(filesList, getContext(), false, iFilesInterface);
        filesAdapter.setLabelName(gQuestion.getColumnId());
        rvFiles.setAdapter(filesAdapter);

        filesSpinner.setItems(filesList, new MultiSpinnerListener() {
            @Override
            public void onItemsSelected(List<KeyPairBoolData> selectedItems) {

                selectedItems = selectedItems == null ? new ArrayList<>() : selectedItems;

                filesAdapter.updateData(selectedItems);

            }
        });

    }
    public void setServiceOptionGridQuestionData(DataGridColumns gQuestion, float itemPrice) {
        setGridQuestionData(gQuestion);
        this.itemPrice = itemPrice;

    }
    public void setListener(IFilesInterface objListener) {

        listener = objListener;
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

    public void setSupportedTypes(String supportedTypes) {


        if (supportedTypes != null) {
            tvSupportedTypes.setText(supportedTypes);
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
        fileInfo.addProperty("action", "add");
        uploadList.add(fileInfo);
        answer.add("fileUpload", uploadList);
        obj.setAnswer(answer);


        return obj;

    }

    public GridColumnAnswerLine getGridFileUploadAnswerLine() {

        GridColumnAnswerLine obj = new GridColumnAnswerLine();
        obj.setColumnId(gridColumns.getColumnId());

        JsonObject column = new JsonObject();
        JsonArray fileUploadList = new JsonArray();

        List<KeyPairBoolData> files = filesAdapter.getFiles();

        for (int i = 0; i < files.size(); i++) {

            if (files.get(i).getImagePath() != null && !files.get(i).getImagePath().trim().equalsIgnoreCase("")) {

                if (!(files.get(i).getImagePath().contains("http://") || files.get(i).getImagePath().contains("https://"))) {

                    JsonObject fileInfo = new JsonObject();

                    String path = files.get(i).getImagePath();
                    MediaTypeAndExtention type = Config.getFileType(path);

                    if (type.getMediaType().equals(Constants.audioType) || type.getMediaType().equals(Constants.videoType)) {

                        String filename = path.substring(path.lastIndexOf("/") + 1);

                        fileInfo.addProperty("mimeType", type.getMediaTypeWithExtention().toString());
                        fileInfo.addProperty("url", filename);

                    } else {

                        fileInfo.addProperty("index", i);
                    }

                    fileInfo.addProperty("path", path);

                    fileInfo.addProperty("caption", files.get(i).getName());
                    fileInfo.addProperty("action", "add");
                    fileUploadList.add(fileInfo);

                }
            }
        }

        column.add("fileUpload", fileUploadList);

        obj.setColumn(column);
        if(fileUploadList.size() > 0){
            obj.setPrice(itemPrice);
            obj.setQuantity(1);
        }

        return obj;
    }


    @Override
    public void onFileUploadClick(KeyPairBoolData data, String labelName) {

        if (listener != null) {

            listener.onFileUploadClick(data, labelName);
        }

    }

    @Override
    public void onCloseClick(KeyPairBoolData data) {

    }

    public boolean isValid() {


        return true;
    }

}


