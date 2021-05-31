package com.jaldeeinc.jaldee.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jaldeeinc.jaldee.Interface.IFilesInterface;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.CheckBoxAdapter;
import com.jaldeeinc.jaldee.adapter.FilesAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomEditTextRegular;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.KeyPairBoolData;
import com.jaldeeinc.jaldee.custom.MultiSpinnerListener;
import com.jaldeeinc.jaldee.custom.MultiSpinnerSearch;
import com.jaldeeinc.jaldee.model.AnswerLine;
import com.jaldeeinc.jaldee.model.BookingModel;
import com.jaldeeinc.jaldee.model.QuestionnaireBoolean;
import com.jaldeeinc.jaldee.model.QuestionnaireCheckbox;
import com.jaldeeinc.jaldee.model.QuestionnaireDateField;
import com.jaldeeinc.jaldee.model.QuestionnaireFileUploadModel;
import com.jaldeeinc.jaldee.model.QuestionnaireInput;
import com.jaldeeinc.jaldee.model.QuestionnaireListModel;
import com.jaldeeinc.jaldee.model.QuestionnaireNumberModel;
import com.jaldeeinc.jaldee.model.QuestionnaireTextField;
import com.jaldeeinc.jaldee.model.QuestnnaireSingleFile;
import com.jaldeeinc.jaldee.response.ListProperties;
import com.jaldeeinc.jaldee.response.Questionnaire;
import com.jaldeeinc.jaldee.response.Questions;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomQuestionnaire extends AppCompatActivity implements IFilesInterface, DatePickerDialog.OnDateSetListener {


    @BindView(R.id.ll_mainLayout)
    LinearLayout llParentLayout;

    @BindView(R.id.cv_submit)
    CardView cvSubmit;

    private Context mContext;
    private IFilesInterface iFilesInterface;
    private KeyPairBoolData fileObject = new KeyPairBoolData();
    private static final String IMAGE_DIRECTORY = "/Jaldee" + "";
    String[] imgExtsSupported = new String[]{"jpg", "jpeg", "png"};
    String[] fileExtsSupported = new String[]{"jpg", "jpeg", "png", "pdf"};
    private int GALLERY_FOR_ONE = 1, CAMERA_FOR_ONE = 2;
    private int GALLERY = 3, CAMERA = 4;

    private Uri mImageUri;
    String path;
    File f;
    File file;
    String singleFilePath = "";
    Bitmap bitmap;

    ArrayList<String> imagePathList = new ArrayList<>();
    ArrayList<String> bookingImagesList = new ArrayList<>();


    ArrayList<String> list = new ArrayList<>();
    private Questionnaire questionnaire = new Questionnaire();
    private String userNotes;

    private HashMap<String, View> viewsList = new HashMap<>();
    private String qLabelName = "";
    private int accountId;
    private int serviceId;
    public BookingModel bookingModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_questionnaire);
        ButterKnife.bind(this);
        mContext = CustomQuestionnaire.this;
        iFilesInterface = this;

        Intent intent = getIntent();
        bookingModel = (BookingModel) intent.getSerializableExtra("data");
        if (bookingModel != null) {
            serviceId = bookingModel.getServiceInfo().getServiceId();
            accountId = bookingModel.getAccountId();
            questionnaire = bookingModel.getQuestionnaire();
            bookingImagesList = bookingModel.getImagesList();
            userNotes = bookingModel.getMessage();
        }

        if (questionnaire != null){

            createQuestionnaire(questionnaire.getQuestionsList());
        }

        cvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try {
                    submitQuestionnaire();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void submitQuestionnaire() throws JSONException {

        QuestionnaireInput input = new QuestionnaireInput();
        input.setQuestionnaireId(questionnaire.getId());
        ArrayList<AnswerLine> answerLines = new ArrayList<>();

        if (validForm(questionnaire.getQuestionsList())) {

            for (Questions question : questionnaire.getQuestionsList()) {

                if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("fileUpload")) {

                    if (question.getGetQuestion().getFileProperties().getAllowedDocuments().size() == 1) {

                        AnswerLine obj = new AnswerLine();
                        obj.setLabelName(question.getGetQuestion().getLabelName());
                        JSONObject answer = new JSONObject();
                        JSONArray uploadList = new JSONArray();
                        JSONObject fileInfo = new JSONObject();
                        fileInfo.put("index", 0);
                        fileInfo.put("caption", "");
                        fileInfo.put("action", "add");
                        uploadList.put(fileInfo);
                        answer.put("fileUpload", uploadList);
                        answerLines.add(obj);

                        View singleFileUploadView = viewsList.get(question.getGetQuestion().getLabelName());
                        ImageView ivSingleFile = (ImageView) singleFileUploadView.findViewById(R.id.iv_file);

                        BitmapDrawable drawable = (BitmapDrawable) ivSingleFile.getDrawable();
                        Bitmap bitmap = drawable.getBitmap();
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                        String path = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), bitmap, "Title", null);
                        imagePathList.add(path);


                    } else {

                        AnswerLine obj = new AnswerLine();
                        obj.setLabelName(question.getGetQuestion().getLabelName());
                        JSONObject answer = new JSONObject();
                        JSONArray uploadList = new JSONArray();

                        View fileUploadView = viewsList.get(question.getGetQuestion().getLabelName());
                        RecyclerView rvFiles = (RecyclerView) fileUploadView.findViewById(R.id.rv_files);
                        FilesAdapter filesAdapter = (FilesAdapter) rvFiles.getAdapter();

                        List<KeyPairBoolData> files = filesAdapter.getFiles();

                        for (int i = 0; i<files.size();i++) {
                            JSONObject fileInfo = new JSONObject();
                            fileInfo.put("index", i + 1);
                            fileInfo.put("caption", "");
                            fileInfo.put("action", "add");
                            uploadList.put(fileInfo);
                        }
                        answer.put("fileUpload", uploadList);
                        answerLines.add(obj);

                        for (int j = 0; j<files.size();j++){

                            imagePathList.add(files.get(j).getImagePath());
                        }
                    }

                } else if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("list")) {

                    View listFieldView = viewsList.get(question.getGetQuestion().getLabelName());
                    RecyclerView rvCheckBoxes = (RecyclerView) listFieldView.findViewById(R.id.rv_checkBoxes);
                    CheckBoxAdapter checkBoxAdapter = (CheckBoxAdapter) rvCheckBoxes.getAdapter();
                    ArrayList<QuestionnaireCheckbox> selectedCheckboxes = new ArrayList<>();
                    if (checkBoxAdapter != null) {

                        selectedCheckboxes = checkBoxAdapter.getSelectedCheckboxes();
                    }
                    AnswerLine obj = new AnswerLine();
                    obj.setLabelName(question.getGetQuestion().getLabelName());
                    JSONObject answer = new JSONObject();
                    JSONArray list = new JSONArray();
                    for (QuestionnaireCheckbox item : selectedCheckboxes) {

                        list.put(item.getText());
                    }
                    answer.put("list", list);

                    obj.setAnswer(answer);
                    answerLines.add(obj);


                } else if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("bool")) {

                    View boolFieldView = viewsList.get(question.getGetQuestion().getLabelName());
                    RadioButton radioButtonYes = (RadioButton) boolFieldView.findViewById(R.id.rb_yes);

                    AnswerLine obj = new AnswerLine();
                    obj.setLabelName(question.getGetQuestion().getLabelName());

                    JSONObject answer = new JSONObject();
                    answer.put("bool", radioButtonYes.isSelected());

                    obj.setAnswer(answer);
                    answerLines.add(obj);

                } else if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("plainText")) {

                    View textFieldView = viewsList.get(question.getGetQuestion().getLabelName());
                    CustomEditTextRegular etTextField = (CustomEditTextRegular) textFieldView.findViewById(R.id.et_textBox);

                    AnswerLine obj = new AnswerLine();
                    obj.setLabelName(question.getGetQuestion().getLabelName());

                    JSONObject answer = new JSONObject();
                    answer.put("plainText", etTextField.getText().toString());

                    obj.setAnswer(answer);
                    answerLines.add(obj);

                } else if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("number")) {

                    View numberFieldView = viewsList.get(question.getGetQuestion().getLabelName());
                    CustomEditTextRegular etTextField = (CustomEditTextRegular) numberFieldView.findViewById(R.id.et_textBox);

                    AnswerLine obj = new AnswerLine();
                    obj.setLabelName(question.getGetQuestion().getLabelName());

                    JSONObject answer = new JSONObject();
                    answer.put("number", etTextField.getText().toString());

                    obj.setAnswer(answer);
                    answerLines.add(obj);

                } else if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("date")) {

                    View numberFieldView = viewsList.get(question.getGetQuestion().getLabelName());
                    CustomTextViewSemiBold tvDate = (CustomTextViewSemiBold) numberFieldView.findViewById(R.id.tv_date);

                    AnswerLine obj = new AnswerLine();
                    obj.setLabelName(question.getGetQuestion().getLabelName());

                    JSONObject answer = new JSONObject();
                    answer.put("date", tvDate.getText().toString());

                    obj.setAnswer(answer);
                    answerLines.add(obj);

                }
            }

            input.setAnswerLines(answerLines);
            
//            ApiSubmitQuestionnnaire(input);

            bookingModel.setQuestionnaireInput(input);
            bookingModel.setQuestionnaireImages(imagePathList);

            Intent intent = new Intent(CustomQuestionnaire.this,ReconfirmationActivity.class);
            intent.putExtra("data",bookingModel);
            startActivity(intent);

        }




    }


    private boolean validForm(ArrayList<Questions> questionsList) {

        for (Questions question : questionsList) {

            if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("fileUpload") && question.getGetQuestion().isMandatory()) {

                if (question.getGetQuestion().getFileProperties().getAllowedDocuments().size() > 1) {

                    View fileUploadView = viewsList.get(question.getGetQuestion().getLabelName());
                    RecyclerView rvFiles = (RecyclerView) fileUploadView.findViewById(R.id.rv_files);
                    FilesAdapter filesAdapter = (FilesAdapter) rvFiles.getAdapter();

                    List<KeyPairBoolData> files = filesAdapter.getFiles();

                    if (files.size() > question.getGetQuestion().getFileProperties().getMaxNoOfFile()) {

                        DynamicToast.make(CustomQuestionnaire.this, "Maximum allowed files " + question.getGetQuestion().getFileProperties().getMaxNoOfFile(), AppCompatResources.getDrawable(
                                CustomQuestionnaire.this, R.drawable.ic_info_black),
                                ContextCompat.getColor(CustomQuestionnaire.this, R.color.white), ContextCompat.getColor(CustomQuestionnaire.this, R.color.green), Toast.LENGTH_SHORT).show();

                        return false;

                    } else if (files.size() < question.getGetQuestion().getFileProperties().getMinNoOfFile()) {

                        DynamicToast.make(CustomQuestionnaire.this, "Upload atleast " + question.getGetQuestion().getFileProperties().getMinNoOfFile()+" file(s)", AppCompatResources.getDrawable(
                                CustomQuestionnaire.this, R.drawable.ic_info_black),
                                ContextCompat.getColor(CustomQuestionnaire.this, R.color.white), ContextCompat.getColor(CustomQuestionnaire.this, R.color.green), Toast.LENGTH_SHORT).show();

                        return false;

                    }

                } else {

                    View singleFileUploadView = viewsList.get(question.getGetQuestion().getLabelName());
                    ImageView ivSingleFile = (ImageView) singleFileUploadView.findViewById(R.id.iv_file);

                    if (ivSingleFile.getDrawable() == null) {

                        DynamicToast.make(CustomQuestionnaire.this, "Please upload image", AppCompatResources.getDrawable(
                                CustomQuestionnaire.this, R.drawable.ic_info_black),
                                ContextCompat.getColor(CustomQuestionnaire.this, R.color.white), ContextCompat.getColor(CustomQuestionnaire.this, R.color.green), Toast.LENGTH_SHORT).show();

                        return false;
                    }


                }

            } else if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("list") && question.getGetQuestion().isMandatory()) {

                View listFieldView = viewsList.get(question.getGetQuestion().getLabelName());
                RecyclerView rvCheckBoxes = (RecyclerView) listFieldView.findViewById(R.id.rv_checkBoxes);
                CheckBoxAdapter checkBoxAdapter = (CheckBoxAdapter) rvCheckBoxes.getAdapter();

                ListProperties properties = new ListProperties();
                properties = question.getGetQuestion().getListProperties();
                int minAnswers = properties.getMinAnswers();
                int maxAnswers = properties.getMaxAnswers();
                int checkedCount = 0;
                if (checkBoxAdapter != null) {
                    checkedCount = checkBoxAdapter.getCheckedCount();
                }
                if (checkedCount < minAnswers) {

                    DynamicToast.make(CustomQuestionnaire.this, "Please select atleast " + minAnswers + " Checkbox", AppCompatResources.getDrawable(
                            CustomQuestionnaire.this, R.drawable.ic_info_black),
                            ContextCompat.getColor(CustomQuestionnaire.this, R.color.white), ContextCompat.getColor(CustomQuestionnaire.this, R.color.green), Toast.LENGTH_SHORT).show();

                    return false;
                } else if (checkedCount > maxAnswers) {

                    DynamicToast.make(CustomQuestionnaire.this, "You can only select upto " + maxAnswers + " Answers", AppCompatResources.getDrawable(
                            CustomQuestionnaire.this, R.drawable.ic_info_black),
                            ContextCompat.getColor(CustomQuestionnaire.this, R.color.white), ContextCompat.getColor(CustomQuestionnaire.this, R.color.green), Toast.LENGTH_SHORT).show();
                    return false;

                }

            } else if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("bool") && question.getGetQuestion().isMandatory()) {

                View boolFieldView = viewsList.get(question.getGetQuestion().getLabelName());
                RadioGroup radioGroup = (RadioGroup) boolFieldView.findViewById(R.id.rg_radioGroup);
                RadioButton radioButtonYes = (RadioButton) boolFieldView.findViewById(R.id.rb_yes);
                RadioButton radioButtonNo = (RadioButton) boolFieldView.findViewById(R.id.rb_no);

                if (!radioButtonYes.isSelected() && !radioButtonNo.isSelected()) {

                    DynamicToast.make(CustomQuestionnaire.this, "Select atleast one option", AppCompatResources.getDrawable(
                            CustomQuestionnaire.this, R.drawable.ic_info_black),
                            ContextCompat.getColor(CustomQuestionnaire.this, R.color.white), ContextCompat.getColor(CustomQuestionnaire.this, R.color.green), Toast.LENGTH_SHORT).show();
                    return false;
                }

            } else if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("plainText") && question.getGetQuestion().isMandatory()) {

                View textFieldView = viewsList.get(question.getGetQuestion().getLabelName());
                CustomEditTextRegular etTextField = (CustomEditTextRegular) textFieldView.findViewById(R.id.et_textBox);

                if (etTextField.getText().toString().trim().equalsIgnoreCase("")) {

                    DynamicToast.make(CustomQuestionnaire.this, "Enter " + question.getGetQuestion().getLabelName(), AppCompatResources.getDrawable(
                            CustomQuestionnaire.this, R.drawable.ic_info_black),
                            ContextCompat.getColor(CustomQuestionnaire.this, R.color.white), ContextCompat.getColor(CustomQuestionnaire.this, R.color.green), Toast.LENGTH_SHORT).show();
                    return false;
                }

            } else if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("number") && question.getGetQuestion().isMandatory()) {

                View numberFieldView = viewsList.get(question.getGetQuestion().getLabelName());
                CustomEditTextRegular etTextField = (CustomEditTextRegular) numberFieldView.findViewById(R.id.et_textBox);

                if (etTextField.getText().toString().trim().equalsIgnoreCase("")) {

                    DynamicToast.make(CustomQuestionnaire.this, "Enter " + question.getGetQuestion().getLabelName(), AppCompatResources.getDrawable(
                            CustomQuestionnaire.this, R.drawable.ic_info_black),
                            ContextCompat.getColor(CustomQuestionnaire.this, R.color.white), ContextCompat.getColor(CustomQuestionnaire.this, R.color.green), Toast.LENGTH_SHORT).show();
                    return false;
                }

            } else if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("date") && question.getGetQuestion().isMandatory()) {

                View dateFieldView = viewsList.get(question.getGetQuestion().getLabelName());
                CustomTextViewSemiBold tvDate = (CustomTextViewSemiBold) dateFieldView.findViewById(R.id.tv_date);

                if (tvDate.getText().toString().trim().equalsIgnoreCase("")) {

                    DynamicToast.make(CustomQuestionnaire.this, "Select " + question.getGetQuestion().getLabelName(), AppCompatResources.getDrawable(
                            CustomQuestionnaire.this, R.drawable.ic_info_black),
                            ContextCompat.getColor(CustomQuestionnaire.this, R.color.white), ContextCompat.getColor(CustomQuestionnaire.this, R.color.green), Toast.LENGTH_SHORT).show();
                    return false;
                }

            }
        }


        return true;
    }


    private void createQuestionnaire(ArrayList<Questions> questionsList) {

        for (Questions question : questionsList) {

            if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("fileUpload")) {

                if (question.getGetQuestion().getFileProperties() != null && question.getGetQuestion().getFileProperties().getAllowedDocuments() != null && question.getGetQuestion().getFileProperties().getAllowedDocuments().size() > 1) {

                    QuestionnaireFileUploadModel model = new QuestionnaireFileUploadModel();
                    model.setQuestionName(question.getGetQuestion().getLabel());
                    model.setManditory(question.getGetQuestion().isMandatory());
                    model.setLabelName(question.getGetQuestion().getLabelName());
                    ArrayList<KeyPairBoolData> filesList = new ArrayList<>();

                    for (int i = 0; i < question.getGetQuestion().getFileProperties().getAllowedDocuments().size(); i++) {

                        KeyPairBoolData obj = new KeyPairBoolData();
                        obj.setName(question.getGetQuestion().getFileProperties().getAllowedDocuments().get(i));
                        obj.setId(i);
                        filesList.add(obj);

                    }
                    model.setFileNames(filesList);
                    addFileUploadView(model);

                } else {

                    QuestnnaireSingleFile singleFile = new QuestnnaireSingleFile();
                    singleFile.setQuestionName(question.getGetQuestion().getLabel());
                    singleFile.setManditory(question.getGetQuestion().isMandatory());
                    singleFile.setLabelName(question.getGetQuestion().getLabelName());

                    addSingleFileUploadView(singleFile);
                }

            } else if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("plainText")) {

                QuestionnaireTextField textField = new QuestionnaireTextField();
                textField.setQuestionName(question.getGetQuestion().getLabel());
                textField.setManditory(question.getGetQuestion().isMandatory());
                textField.setLabelName(question.getGetQuestion().getLabelName());

                addTextFieldView(textField);

            } else if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("date")) {

                QuestionnaireDateField dateField = new QuestionnaireDateField();
                dateField.setQuestionName(question.getGetQuestion().getLabel());
                dateField.setManditory(question.getGetQuestion().isMandatory());
                dateField.setLabelName(question.getGetQuestion().getLabelName());


                addDateFieldView(dateField);
            } else if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("number")) {

                QuestionnaireNumberModel numberField = new QuestionnaireNumberModel();
                numberField.setQuestionName(question.getGetQuestion().getLabel());
                numberField.setManditory(question.getGetQuestion().isMandatory());
                numberField.setLabelName(question.getGetQuestion().getLabelName());

                addNumberFieldView(numberField);

            } else if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("bool")) {

                QuestionnaireBoolean boolField = new QuestionnaireBoolean();
                boolField.setQuestionName(question.getGetQuestion().getLabel());
                boolField.setManditory(question.getGetQuestion().isMandatory());
                boolField.setLabelName(question.getGetQuestion().getLabelName());
                ArrayList values = (ArrayList) question.getGetQuestion().getLabelValues();
                boolField.setLabels(values);

                addBooleanField(boolField);

            } else if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("list")) {

                QuestionnaireListModel listModel = new QuestionnaireListModel();
                listModel.setQuestionName(question.getGetQuestion().getLabel());
                listModel.setManditory(question.getGetQuestion().isMandatory());
                listModel.setLabelName(question.getGetQuestion().getLabelName());
                ArrayList values = (ArrayList) question.getGetQuestion().getLabelValues();
                listModel.setLabels(values);

                addListField(listModel);

            }
        }
    }


    private void addSingleFileUploadView(QuestnnaireSingleFile singleFile) {

        View fileUploadView = getLayoutInflater().inflate(R.layout.singlefile_upload, null, false);

        CustomTextViewSemiBold tvQuestionName = (CustomTextViewSemiBold) fileUploadView.findViewById(R.id.tv_questionName);
        CustomTextViewBold tvMutipleFileManditory = (CustomTextViewBold) fileUploadView.findViewById(R.id.tv_singleFileManditory);
        LinearLayout llUpload = (LinearLayout) fileUploadView.findViewById(R.id.ll_upload);
        ImageView ivSingleFile = (ImageView) fileUploadView.findViewById(R.id.iv_file);
        ImageView ivClose = (ImageView) fileUploadView.findViewById(R.id.iv_close);


        tvQuestionName.setText(singleFile.getQuestionName());

        if (singleFile.isManditory()) {
            tvMutipleFileManditory.setVisibility(View.VISIBLE);
        } else {
            tvMutipleFileManditory.setVisibility(View.GONE);
        }

        llUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                qLabelName = singleFile.getLabelName();
                openImageOptions();
            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });


        llParentLayout.addView(fileUploadView);
        viewsList.put(singleFile.getLabelName(), fileUploadView);

    }

    private void addFileUploadView(QuestionnaireFileUploadModel model) {

        View fileUploadView = getLayoutInflater().inflate(R.layout.questnnarefile_upload, null, false);

        CustomTextViewSemiBold tvQuestionName = (CustomTextViewSemiBold) fileUploadView.findViewById(R.id.tv_questionName);
        MultiSpinnerSearch filesSpinner = (MultiSpinnerSearch) fileUploadView.findViewById(R.id.mfilesSpinner);
        RecyclerView rvFiles = (RecyclerView) fileUploadView.findViewById(R.id.rv_files);
        CustomTextViewBold tvMutipleFileManditory = (CustomTextViewBold) fileUploadView.findViewById(R.id.tv_multipleFileManditory);

        tvQuestionName.setText(model.getQuestionName());

        if (model.isManditory()) {
            tvMutipleFileManditory.setVisibility(View.VISIBLE);
        } else {

            tvMutipleFileManditory.setVisibility(View.GONE);
        }

        rvFiles.setLayoutManager(new LinearLayoutManager(this));
        FilesAdapter filesAdapter = new FilesAdapter(new ArrayList<KeyPairBoolData>(), mContext, false, iFilesInterface);
        filesAdapter.setLabelName(model.getLabelName());
        rvFiles.setAdapter(filesAdapter);

        filesSpinner.setItems(model.getFileNames(), new MultiSpinnerListener() {
            @Override
            public void onItemsSelected(List<KeyPairBoolData> selectedItems) {

                selectedItems = selectedItems == null ? new ArrayList<>() : selectedItems;

                filesAdapter.updateData(selectedItems);

            }
        });


        llParentLayout.addView(fileUploadView);
        viewsList.put(model.getLabelName(), fileUploadView);

    }

    private void addTextFieldView(QuestionnaireTextField textField) {

        View textFieldView = getLayoutInflater().inflate(R.layout.edittext_field, null, false);

        CustomTextViewSemiBold tvQuestionName = (CustomTextViewSemiBold) textFieldView.findViewById(R.id.tv_questionName);
        CustomTextViewBold tvTextFieldManditory = (CustomTextViewBold) textFieldView.findViewById(R.id.tv_manditory);
        CustomEditTextRegular etTextField = (CustomEditTextRegular) textFieldView.findViewById(R.id.et_textBox);


        tvQuestionName.setText(textField.getQuestionName());

        if (textField.isManditory()) {
            tvTextFieldManditory.setVisibility(View.VISIBLE);
        } else {
            tvTextFieldManditory.setVisibility(View.GONE);
        }

        etTextField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                // Place the logic here for your output edittext


            }
        });

        llParentLayout.addView(textFieldView);
        viewsList.put(textField.getLabelName(), textFieldView);


    }

    private void addDateFieldView(QuestionnaireDateField dateField) {

        View dateFieldView = getLayoutInflater().inflate(R.layout.date_item, null, false);

        CustomTextViewSemiBold tvQuestionName = (CustomTextViewSemiBold) dateFieldView.findViewById(R.id.tv_questionName);
        CustomTextViewBold tvTextFieldManditory = (CustomTextViewBold) dateFieldView.findViewById(R.id.tv_manditory);
        RelativeLayout rlCalender = (RelativeLayout) dateFieldView.findViewById(R.id.rl_calender);
        CustomTextViewSemiBold tvDate = (CustomTextViewSemiBold) dateFieldView.findViewById(R.id.tv_date);

        tvQuestionName.setText(dateField.getQuestionName());

        if (dateField.isManditory()) {
            tvTextFieldManditory.setVisibility(View.VISIBLE);
        } else {
            tvTextFieldManditory.setVisibility(View.GONE);
        }

        rlCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                com.jaldeeinc.jaldee.custom.DatePicker mDatePickerDialogFragment;
                mDatePickerDialogFragment = new com.jaldeeinc.jaldee.custom.DatePicker();
                mDatePickerDialogFragment.show(getSupportFragmentManager(), "DATE PICK");
            }
        });


        llParentLayout.addView(dateFieldView);
        viewsList.put(dateField.getLabelName(), dateFieldView);


    }

    private void addNumberFieldView(QuestionnaireNumberModel numberField) {

        View dateFieldView = getLayoutInflater().inflate(R.layout.numberfield_item, null, false);

        CustomTextViewSemiBold tvQuestionName = (CustomTextViewSemiBold) dateFieldView.findViewById(R.id.tv_questionName);
        CustomTextViewBold tvTextFieldManditory = (CustomTextViewBold) dateFieldView.findViewById(R.id.tv_manditory);
        CustomEditTextRegular etTextField = (CustomEditTextRegular) dateFieldView.findViewById(R.id.et_textBox);


        tvQuestionName.setText(numberField.getQuestionName());

        if (numberField.isManditory()) {
            tvTextFieldManditory.setVisibility(View.VISIBLE);
        } else {
            tvTextFieldManditory.setVisibility(View.GONE);
        }

        etTextField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                // Place the logic here for your output edittext


            }
        });

        llParentLayout.addView(dateFieldView);
        viewsList.put(numberField.getLabelName(), dateFieldView);

    }

    private void addBooleanField(QuestionnaireBoolean boolField) {

        View boolFieldView = getLayoutInflater().inflate(R.layout.boolean_item, null, false);

        CustomTextViewSemiBold tvQuestionName = (CustomTextViewSemiBold) boolFieldView.findViewById(R.id.tv_questionName);
        CustomTextViewBold tvTextFieldManditory = (CustomTextViewBold) boolFieldView.findViewById(R.id.tv_manditory);
        RadioGroup radioGroup = (RadioGroup) boolFieldView.findViewById(R.id.rg_radioGroup);
        RadioButton radioButtonYes = (RadioButton) boolFieldView.findViewById(R.id.rb_yes);
        RadioButton radioButtonNo = (RadioButton) boolFieldView.findViewById(R.id.rb_no);


        tvQuestionName.setText(boolField.getQuestionName());

        if (boolField.isManditory()) {
            tvTextFieldManditory.setVisibility(View.VISIBLE);
        } else {
            tvTextFieldManditory.setVisibility(View.GONE);
        }

        if (boolField.getLabels() != null) {

            radioButtonYes.setText(boolField.getLabels().get(0));
            radioButtonNo.setText(boolField.getLabels().get(1));
        }


        llParentLayout.addView(boolFieldView);
        viewsList.put(boolField.getLabelName(), boolFieldView);

    }

    private void addListField(QuestionnaireListModel listModel) {

        View listFieldView = getLayoutInflater().inflate(R.layout.list_fielditem, null, false);

        CustomTextViewSemiBold tvQuestionName = (CustomTextViewSemiBold) listFieldView.findViewById(R.id.tv_questionName);
        CustomTextViewBold tvTextFieldManditory = (CustomTextViewBold) listFieldView.findViewById(R.id.tv_manditory);
        LinearLayout llParent = (LinearLayout) listFieldView.findViewById(R.id.ll_parent);
        RecyclerView rvCheckBoxes = (RecyclerView) listFieldView.findViewById(R.id.rv_checkBoxes);


        tvQuestionName.setText(listModel.getQuestionName());

        if (listModel.isManditory()) {
            tvTextFieldManditory.setVisibility(View.VISIBLE);
        } else {
            tvTextFieldManditory.setVisibility(View.GONE);
        }

        list = listModel.getLabels();

        ArrayList<QuestionnaireCheckbox> checkBoxList = new ArrayList<>();
        for (String text : list) {

            QuestionnaireCheckbox checkBoxObj = new QuestionnaireCheckbox(false, text);
            checkBoxList.add(checkBoxObj);

        }

        rvCheckBoxes.setLayoutManager(new LinearLayoutManager(this));
        CheckBoxAdapter checkBoxAdapter = new CheckBoxAdapter(checkBoxList, mContext);
        rvCheckBoxes.setAdapter(checkBoxAdapter);


        llParentLayout.addView(listFieldView);
        viewsList.put(listModel.getLabelName(), listFieldView);


    }


    @Override
    public void onFileUploadClick(KeyPairBoolData data, String labelName) {

        fileObject = new KeyPairBoolData();
        fileObject.setId(data.getId());
        fileObject.setName(data.getName());
        qLabelName = labelName;
        openGalleryForOneImage();
    }

    @Override
    public void onCloseClick(KeyPairBoolData data) {

        fileObject = data;

    }

    private void openGalleryForOneImage() {

        try {

            Dialog dialog = new Dialog(CustomQuestionnaire.this);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.camera_options);
            dialog.getWindow().getAttributes().windowAnimations = R.style.slidingUpAndDown;
            DisplayMetrics metrics = CustomQuestionnaire.this.getResources().getDisplayMetrics();
            int width = (int) (metrics.widthPixels * 1);
            dialog.getWindow().setGravity(Gravity.BOTTOM);
            dialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout llGallery = dialog.findViewById(R.id.ll_gallery);
            LinearLayout llCamera = dialog.findViewById(R.id.ll_camera);

            llCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    openCameraforOneImage();
                    dialog.dismiss();
                }
            });

            llGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    openGalleryforOneImage();
                    dialog.dismiss();
                }
            });

            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openGalleryforOneImage() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if ((ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_FOR_ONE);

                    return;
                } else {
                    Intent intent = new Intent();
                    intent.setType("*/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_FOR_ONE);
                }
            } else {

                Intent intent = new Intent();
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_FOR_ONE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void openCameraforOneImage() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {


                    requestPermissions(new String[]{
                            Manifest.permission.CAMERA}, CAMERA_FOR_ONE);

                    return;
                } else {
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    Intent cameraIntent = new Intent();
                    cameraIntent.setType("image/*");
                    cameraIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                    cameraIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, CAMERA_FOR_ONE);
                }
            } else {

                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                Intent cameraIntent = new Intent();
                cameraIntent.setType("image/*");
                cameraIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                cameraIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, CAMERA_FOR_ONE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_FOR_ONE) {
            if (data != null) {
                try {
                    if (data.getData() != null) {
                        Uri uri = data.getData();
                        String orgFilePath = getRealPathFromURI(uri, this);
                        String filepath = "";//default fileName

                        String mimeType = this.mContext.getContentResolver().getType(uri);
                        String uriString = uri.toString();
                        String extension = "";
                        if (uriString.contains(".")) {
                            extension = uriString.substring(uriString.lastIndexOf(".") + 1);
                        }

                        if (mimeType != null) {
                            extension = mimeType.substring(mimeType.lastIndexOf("/") + 1);
                        }
                        if (Arrays.asList(fileExtsSupported).contains(extension)) {
                            if (orgFilePath == null) {
                                orgFilePath = getFilePathFromURI(mContext, uri, extension);
                            }
                        } else {
                            Toast.makeText(mContext, "File type not supported", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        View fileUploadView = viewsList.get(qLabelName);
                        RecyclerView rvFiles = (RecyclerView) fileUploadView.findViewById(R.id.rv_files);
                        FilesAdapter filesAdapter = (FilesAdapter) rvFiles.getAdapter();

                        fileObject.setImagePath(orgFilePath);
                        filesAdapter.updateFileObject(fileObject);


                    } else if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri imageUri = item.getUri();
                            String orgFilePath = getRealPathFromURI(imageUri, this);
                            String filepath = "";//default fileName

                            String mimeType = mContext.getContentResolver().getType(imageUri);
                            String uriString = imageUri.toString();
                            String extension = "";
                            if (uriString.contains(".")) {
                                extension = uriString.substring(uriString.lastIndexOf(".") + 1);
                            }

                            if (mimeType != null) {
                                extension = mimeType.substring(mimeType.lastIndexOf("/") + 1);
                            }
                            if (Arrays.asList(fileExtsSupported).contains(extension)) {
                                if (orgFilePath == null) {
                                    orgFilePath = getFilePathFromURI(mContext, imageUri, extension);
                                }
                            } else {
                                Toast.makeText(mContext, "File type not supported", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            View fileUploadView = viewsList.get(qLabelName);
                            RecyclerView rvFiles = (RecyclerView) fileUploadView.findViewById(R.id.rv_files);
                            FilesAdapter filesAdapter = (FilesAdapter) rvFiles.getAdapter();

                            fileObject.setImagePath(orgFilePath);
                            filesAdapter.updateFileObject(fileObject);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else if (requestCode == CAMERA_FOR_ONE) {
            if (data != null) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                String path = saveImage(bitmap);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                if (path != null) {
                    mImageUri = Uri.parse(path);

                    View fileUploadView = viewsList.get(qLabelName);
                    RecyclerView rvFiles = (RecyclerView) fileUploadView.findViewById(R.id.rv_files);
                    FilesAdapter filesAdapter = (FilesAdapter) rvFiles.getAdapter();

                    fileObject.setImagePath(getFilePathFromURI(mImageUri, mContext));
                    filesAdapter.updateFileObject(fileObject);
                }
                try {
                    bytes.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        } else if (requestCode == GALLERY) {

            if (data != null) {
                try {
                    if (data.getData() != null) {
                        Uri uri = data.getData();
                        String orgFilePath = getRealPathFromURI(uri, this);
                        String filepath = "";//default fileName

                        String mimeType = this.mContext.getContentResolver().getType(uri);
                        String uriString = uri.toString();
                        String extension = "";
                        if (uriString.contains(".")) {
                            extension = uriString.substring(uriString.lastIndexOf(".") + 1);
                        }

                        if (mimeType != null) {
                            extension = mimeType.substring(mimeType.lastIndexOf("/") + 1);
                        }
                        if (Arrays.asList(fileExtsSupported).contains(extension)) {
                            if (orgFilePath == null) {
                                orgFilePath = getFilePathFromURI(mContext, uri, extension);
                            }
                        } else {
                            Toast.makeText(mContext, "File type not supported", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        singleFilePath = orgFilePath;
                        View fileUploadView = viewsList.get(qLabelName);
                        ImageView ivSingleFile = (ImageView) fileUploadView.findViewById(R.id.iv_file);
                        ivSingleFile.setImageBitmap(BitmapFactory.decodeFile(orgFilePath));


                    } else if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri imageUri = item.getUri();
                            String orgFilePath = getRealPathFromURI(imageUri, this);
                            String filepath = "";//default fileName

                            String mimeType = mContext.getContentResolver().getType(imageUri);
                            String uriString = imageUri.toString();
                            String extension = "";
                            if (uriString.contains(".")) {
                                extension = uriString.substring(uriString.lastIndexOf(".") + 1);
                            }

                            if (mimeType != null) {
                                extension = mimeType.substring(mimeType.lastIndexOf("/") + 1);
                            }
                            if (Arrays.asList(fileExtsSupported).contains(extension)) {
                                if (orgFilePath == null) {
                                    orgFilePath = getFilePathFromURI(mContext, imageUri, extension);
                                }
                            } else {
                                Toast.makeText(mContext, "File type not supported", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            singleFilePath = orgFilePath;
                            View fileUploadView = viewsList.get(qLabelName);
                            ImageView ivSingleFile = (ImageView) fileUploadView.findViewById(R.id.iv_file);
                            ivSingleFile.setImageBitmap(BitmapFactory.decodeFile(orgFilePath));

                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else if (requestCode == CAMERA) {

            if (data != null) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                String path = saveImage(bitmap);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                if (path != null) {
                    mImageUri = Uri.parse(path);

                    singleFilePath = path;
                    View fileUploadView = viewsList.get(qLabelName);
                    ImageView ivSingleFile = (ImageView) fileUploadView.findViewById(R.id.iv_file);
                    ivSingleFile.setImageBitmap(BitmapFactory.decodeFile(path));


                }
                try {
                    bytes.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }
    }

    private void openImageOptions() {

        try {

            Dialog dialog = new Dialog(CustomQuestionnaire.this);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.camera_options);
            dialog.getWindow().getAttributes().windowAnimations = R.style.slidingUpAndDown;
            DisplayMetrics metrics = CustomQuestionnaire.this.getResources().getDisplayMetrics();
            int width = (int) (metrics.widthPixels * 1);
            dialog.getWindow().setGravity(Gravity.BOTTOM);
            dialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout llGallery = dialog.findViewById(R.id.ll_gallery);
            LinearLayout llCamera = dialog.findViewById(R.id.ll_camera);

            llCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    openCamera();
                    dialog.dismiss();
                }
            });

            llGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    openGallery();
                    dialog.dismiss();
                }
            });

            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openGallery() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if ((ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY);

                    return;
                } else {
                    Intent intent = new Intent();
                    intent.setType("*/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY);
                }
            } else {

                Intent intent = new Intent();
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void openCamera() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {


                    requestPermissions(new String[]{
                            Manifest.permission.CAMERA}, CAMERA);

                    return;
                } else {
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    Intent cameraIntent = new Intent();
                    cameraIntent.setType("image/*");
                    cameraIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                    cameraIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, CAMERA);
                }
            } else {

                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                Intent cameraIntent = new Intent();
                cameraIntent.setType("image/*");
                cameraIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                cameraIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, CAMERA);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    // files related

    public static String getFilePathFromURI(Context context, Uri contentUri, String extension) {
        //copy file and send new file path
        String fileName = getFileNameInfo(contentUri);
        if (!TextUtils.isEmpty(fileName)) {
            String ext = "";
            if (fileName.contains(".")) {
            } else {
                ext = "." + extension;
            }
            File wallpaperDirectoryFile = new File(
                    Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY + File.separator + fileName + ext);
            copy(context, contentUri, wallpaperDirectoryFile);
            return wallpaperDirectoryFile.getAbsolutePath();
        }
        return null;
    }

    protected static String getFileNameInfo(Uri uri) {
        if (uri == null) {
            return null;
        }
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        return fileName;
    }

    public static void copy(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            FileOutputStream outputStream = new FileOutputStream(dstFile);
            IOUtils.copy(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getRealPathFromURI(Uri contentURI, Activity context) {
        String[] projection = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = context.managedQuery(contentURI, projection, null,
                null, null);
        if (cursor == null)
            return null;
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        if (cursor.moveToFirst()) {
            String s = cursor.getString(column_index);
            // cursor.close();
            return s;
        }
        // cursor.close();
        return null;
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        if (myBitmap != null) {
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        }
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(mContext,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String getPDFPath(Uri uri) {

        final String id = DocumentsContract.getDocumentId(uri);
        final Uri contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = mContext.getApplicationContext().getContentResolver().query(contentUri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public String getFilePathFromURI(Uri contentUri, Context context) {
        //copy file and send new file path
        String fileName = getFileName(contentUri);
        if (!TextUtils.isEmpty(fileName)) {
            File copyFile = new File(context.getExternalCacheDir() + File.separator + fileName);
            //copy(context, contentUri, copyFile);
            return copyFile.getAbsolutePath();
        }
        return null;
    }

    public String getFileName(Uri uri) {
        if (uri == null) return null;
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        return fileName;
    }

    public String getRealFilePath(Uri uri) {
        String path = uri.getPath();
        String[] pathArray = path.split(":");
        String fileName = pathArray[pathArray.length - 1];
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {

        SimpleDateFormat dateParser = new SimpleDateFormat("dd-MM-yyyy",
                Locale.ENGLISH);
        Calendar mCalender = Calendar.getInstance();
        mCalender.set(Calendar.YEAR, year);
        mCalender.set(Calendar.MONTH, month);
        mCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String selectedDate = dateParser.format(mCalender.getTime()).toString();
//        tvDate.setText(selectedDate);
    }
}