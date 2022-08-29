package com.jaldeeinc.jaldee.adapter;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaldeeinc.jaldee.BuildConfig;
import com.jaldeeinc.jaldee.Interface.IFilesInterface;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.ImageActivity;
import com.jaldeeinc.jaldee.activities.VideoActivity;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.custom.KeyPairBoolData;
import com.jaldeeinc.jaldee.custom.MultiSpinnerListener;
import com.jaldeeinc.jaldee.custom.MultiSpinnerSearch;
import com.jaldeeinc.jaldee.model.LabelPath;
import com.jaldeeinc.jaldee.model.QuestionnaireBoolean;
import com.jaldeeinc.jaldee.model.QuestionnaireCheckbox;
import com.jaldeeinc.jaldee.model.QuestionnaireDateField;
import com.jaldeeinc.jaldee.model.QuestionnaireFileUploadModel;
import com.jaldeeinc.jaldee.model.QuestionnaireListModel;
import com.jaldeeinc.jaldee.model.QuestionnaireNumberModel;
import com.jaldeeinc.jaldee.model.QuestionnaireTextField;
import com.jaldeeinc.jaldee.model.QuestnnaireSingleFile;
import com.jaldeeinc.jaldee.response.DataGridColumns;
import com.jaldeeinc.jaldee.response.DataGridProperties;
import com.jaldeeinc.jaldee.response.Questions;

import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.Map;

public class ServiceOptionAddItemOptionDialog extends Dialog implements DatePickerDialog.OnDateSetListener, IFilesInterface {
    private ImageView iv_close;
    private TextView tv_item_name;
    private TextView tv_item_price;
    private CardView cv_submit;
    private String itemName;
    private Float itemPrice;
    private Questions questions;
    ArrayList<LabelPath> labelPaths = new ArrayList<>();
    private LinearLayout llParentLayout;
    private HashMap<String, View> viewsList = new HashMap<>();
    private String qLabelName = "";
    private Context context;
    private KeyPairBoolData fileObject = new KeyPairBoolData();
    private IFilesInterface iFilesInterface;
    String[] videoFormats = new String[]{"wmv", "mp4", "webm", "flw", "mov", "avi", ".wmv", ".mp4", ".webm", ".flw", ".mov", ".avi"};
    private int GALLERY_FOR_ONE = 1, CAMERA_FOR_ONE = 2;
    private int GALLERY = 3, CAMERA = 4;
    private Uri mImageUri;
    String singleFilePath = "";

    public ServiceOptionAddItemOptionDialog(@NonNull Context context, String itemName, Float itemPrice, Questions questions) {
        super(context);
        this.context = context;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.questions = questions;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_option_add_item_options_dialog);
        iFilesInterface = this;
        llParentLayout = findViewById(R.id.ll_mainLayout);
        iv_close = findViewById(R.id.iv_close);
        tv_item_name = findViewById(R.id.tv_item_name);
        tv_item_price = findViewById(R.id.tv_item_price);
        cv_submit = findViewById(R.id.cv_submit);
        tv_item_name.setText(itemName);
        if (itemPrice != null && !itemPrice.equals("")) {
            tv_item_price.setText("₹ "  + Config.getAmountNoOrTwoDecimalPoints(itemPrice));
            tv_item_price.setVisibility(View.VISIBLE);
        } else {
            tv_item_price.setVisibility(View.GONE);
        }
        try {
            createQuestionnaire(questions);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });
    }

    private void createQuestionnaire(Questions question) throws JSONException {
        if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("dataGridList")) {
            DataGridProperties dataGridProperties = question.getGetQuestion().getDataGridListProperties();
            ArrayList<DataGridColumns> gridListColumns = dataGridProperties.getGridListColumns();

            String baseSelectedItem = null;
            JSONObject priceGridList = new JSONObject(question.getGetQuestion().getPriceGridList());
            for (int k = 1; k < gridListColumns.size(); k++) {
                DataGridColumns dataGridColumn = gridListColumns.get(k);

                //for (DataGridColumns dataGridColumn : gridListColumns) {
                if (dataGridColumn.getDataType().equalsIgnoreCase("list")) {

                    QuestionnaireListModel listModel = new QuestionnaireListModel();
                    //listModel.setQuestionName(question.getGetQuestion().getLabel());
                    listModel.setQuestionName(dataGridColumn.getLabel());
                    listModel.setManditory(dataGridColumn.isMandatory());
                    listModel.setLabelName(dataGridColumn.getLabel());
                    //listModel.setHint(question.getGetQuestion().getHint());
                    Map<String, Object> itemAndPrices;
                    ArrayList<String> values = (ArrayList) dataGridColumn.getListProperties().getValues();
                    ArrayList<QuestionnaireCheckbox> questionnaireCheckboxes = new ArrayList<>();
                        /*if (dataGridColumn.getListProperties().getBasePrice() != null) {
                            itemAndPrices = Config.jsonStringToMap(dataGridColumn.getListProperties().getBasePrice());
                            for (String v : values) {
                                QuestionnaireCheckbox questionnaireCheckbox = new QuestionnaireCheckbox();
                                questionnaireCheckbox.setText(v);
                                questionnaireCheckbox.setPrice(Float.parseFloat(itemAndPrices.get(v).toString()));
                                questionnaireCheckbox.setChecked(false);
                                questionnaireCheckbox.setBase(true); // set baselist or not
                                questionnaireCheckboxes.add(questionnaireCheckbox);
                            }
                            baseSelectedItem = questionnaireCheckboxes.get(0).getText();
                            if(dataGridColumn.isMandatory() && isEdit){
                                questionnaireCheckboxes.get(0).setChecked(true);
                                tv_total_price.setText(String.valueOf(questionnaireCheckboxes.get(0).getPrice()));
                            }
                        } else {*/
                    baseSelectedItem = itemName;

                    if (baseSelectedItem != null) {
                        JSONObject j = priceGridList.getJSONObject(baseSelectedItem);
                        //JSONObject k = j.getJSONObject(dataGridColumn.getColumnId());
                        Map<String, Object> kj = new Gson().fromJson(
                                String.valueOf(j.getJSONObject(dataGridColumn.getColumnId())), new TypeToken<HashMap<String, Object>>() {
                                }.getType());
                        for (Map.Entry<String, Object> entry : kj.entrySet()) {
                            System.out.println(entry.getKey() + "/" + entry.getValue());
                            QuestionnaireCheckbox questionnaireCheckbox = new QuestionnaireCheckbox();
                            questionnaireCheckbox.setText(entry.getKey());
                            questionnaireCheckbox.setPrice(Float.parseFloat(entry.getValue().toString()));
                            questionnaireCheckbox.setChecked(false);
                            questionnaireCheckbox.setBase(false); // set baselist or not

                            questionnaireCheckboxes.add(questionnaireCheckbox);
                        }
                    }
                    //}
                    listModel.setQuestionnaireCheckboxes(questionnaireCheckboxes);
                    listModel.setLabels(values);

                    listModel.setMaxAnswerable(dataGridColumn.getListProperties().getMaxAnswerable());

                        /*for (AnswerLine answerLine : qInput.getAnswerLines()) {
                            //if (answerLine != null && answerLine.getLabelName().equalsIgnoreCase(question.getGetQuestion().getLabelName())) {
                            if (answerLine != null && answerLine.getLabelName().equalsIgnoreCase(dataGridColumn.getLabel())) {
                                JSONObject txtObj = new JSONObject(answerLine.getAnswer().toString());
                                JSONArray list = (JSONArray) txtObj.get("list");
                                ArrayList<String> selectedItems = new ArrayList<>();
                                for (int i = 0; i < list.length(); i++) {
                                    selectedItems.add(list.optString(i));
                                }
                                listModel.setSelectedItems(selectedItems);
                            }
                        }*/


                    addListField(listModel);

                } else if (dataGridColumn.getDataType().equalsIgnoreCase("plainText")) {

                    QuestionnaireTextField textField = new QuestionnaireTextField();
                    textField.setQuestionName(dataGridColumn.getLabel());
                    textField.setManditory(dataGridColumn.isMandatory());
                    textField.setLabelName(dataGridColumn.getLabel());
                    //textField.setHint(question.getGetQuestion().getHint());

                        /*for (AnswerLine answerLine : qInput.getAnswerLines()) {
                            if (answerLine != null && answerLine.getLabelName().equalsIgnoreCase(question.getGetQuestion().getLabelName())) {
                                JSONObject txtObj = new JSONObject(answerLine.getAnswer().toString());
                                String text = txtObj.optString("plainText");
                                if (text.equalsIgnoreCase("05:30 AM")) {
                                    textField.setText("");
                                } else {
                                    textField.setText(text);
                                }
                            }
                        }*/
                    addTextFieldView(textField);

                } else if (dataGridColumn.getDataType().equalsIgnoreCase("number")) {

                    QuestionnaireNumberModel numberField = new QuestionnaireNumberModel();
                    numberField.setQuestionName(dataGridColumn.getLabel());
                    numberField.setManditory(dataGridColumn.isMandatory());
                    numberField.setLabelName(dataGridColumn.getLabel());
                    //numberField.setHint(question.getGetQuestion().getHint());

                        /*for (AnswerLine answerLine : qInput.getAnswerLines()) {
                            if (answerLine != null && answerLine.getLabelName().equalsIgnoreCase(question.getGetQuestion().getLabelName())) {
                                JSONObject txtObj = new JSONObject(answerLine.getAnswer().toString());
                                int number = txtObj.optInt("number");
                                numberField.setNumber(String.valueOf(number));
                            }
                        }*/

                    addNumberFieldView(numberField);

                } else if (dataGridColumn.getDataType().equalsIgnoreCase("date")) {

                    QuestionnaireDateField dateField = new QuestionnaireDateField();
                    dateField.setQuestionName(dataGridColumn.getLabel());
                    dateField.setManditory(dataGridColumn.isMandatory());
                    dateField.setLabelName(dataGridColumn.getLabel());
                    //dateField.setHint(question.getHint());

                        /*for (AnswerLine answerLine : qInput.getAnswerLines()) {
                            if (answerLine != null && answerLine.getLabelName().equalsIgnoreCase(question.getLabelName())) {
                                JSONObject txtObj = new JSONObject(answerLine.getAnswer().toString());
                                String text = txtObj.optString("date");
                                if (text.equalsIgnoreCase("05:30 AM")) {
                                    dateField.setDate("");
                                } else {
                                    dateField.setDate(text);
                                }
                            }
                        }*/

                    addDateFieldView(dateField);
                } else if (dataGridColumn.getDataType().equalsIgnoreCase("bool")) {

                    QuestionnaireBoolean boolField = new QuestionnaireBoolean();
                    boolField.setQuestionName(dataGridColumn.getLabel());
                    boolField.setManditory(dataGridColumn.isMandatory());
                    boolField.setLabelName(dataGridColumn.getLabel());
                    //boolField.setHint(question.getHint());

                    ArrayList values = new ArrayList();
                    values.add(0, "Yes");
                    values.add(1, "No");
                    boolField.setLabels(values);

                        /*for (AnswerLine answerLine : qInput.getAnswerLines()) {
                            if (answerLine != null && answerLine.getLabelName().equalsIgnoreCase(question.getLabelName())) {
                                JSONObject txtObj = new JSONObject(answerLine.getAnswer().toString());
                                Boolean isSelected = txtObj.optBoolean("bool");
                                boolField.setSelected(isSelected);
                            }
                        }*/

                    addBooleanField(boolField);

                } else if (dataGridColumn.getDataType().equalsIgnoreCase("fileUpload")) {

                    if (dataGridColumn.getFileProperties() != null && dataGridColumn.getFileProperties().getAllowedDocuments() != null && dataGridColumn.getFileProperties().getAllowedDocuments().size() > 1) {

                        QuestionnaireFileUploadModel model = new QuestionnaireFileUploadModel();
                        model.setQuestionName(dataGridColumn.getLabel());
                        model.setManditory(dataGridColumn.isMandatory());
                        //model.setLabelName(dataGridColumn.getLabelName());
                        model.setLabelName(dataGridColumn.getLabel());
                        // model.setHint(question.getGetQuestion().getHint());
                        model.setAllowedTypes(dataGridColumn.getFileProperties().getFileTypes());

                        ArrayList<KeyPairBoolData> filesList = new ArrayList<>();

                        for (int i = 0; i < dataGridColumn.getFileProperties().getAllowedDocuments().size(); i++) {

                            KeyPairBoolData obj = new KeyPairBoolData();
                            obj.setName(dataGridColumn.getFileProperties().getAllowedDocuments().get(i));
                            obj.setId(i);
                            filesList.add(obj);

                        }
                        model.setFileNames(filesList);

                        ArrayList<KeyPairBoolData> dataList = new ArrayList<>();
                        for (LabelPath l : labelPaths) {
                            //if (l.getLabelName().equalsIgnoreCase(question.getGetQuestion().getLabelName())) {
                            if (l.getLabelName().equalsIgnoreCase(dataGridColumn.getLabel())) {
                                KeyPairBoolData file = new KeyPairBoolData(l.getFileName(), l.getPath(), l.getType());
                                dataList.add(file);
                            }
                        }
                        model.setFiles(dataList);

                        addFileUploadView(model);

                    } else {
/*
                            QuestnnaireSingleFile singleFile = new QuestnnaireSingleFile();
                            singleFile.setQuestionName(dataGridColumn.getLabel());
                            singleFile.setManditory(dataGridColumn.isMandatory());
                            //singleFile.setLabelName(dataGridColumn.getLabelName());
                            singleFile.setLabelName(dataGridColumn.getLabel());
                            //singleFile.setHint(dataGridColumn.getHint());

                            for (LabelPath l : labelPaths) {
                                //if (l.getLabelName().equalsIgnoreCase(dataGridColumn.getLabelName())) {
                                    if (l.getLabelName().equalsIgnoreCase(dataGridColumn.getLabel())) {
                                        singleFile.setFilePath(l.getPath());
                                    singleFile.setType(l.getType());
                                }
                            }

                            addSingleFileUploadView(singleFile);*/


                        QuestnnaireSingleFile singleFile = new QuestnnaireSingleFile();
                        singleFile.setQuestionName(dataGridColumn.getLabel());
                        singleFile.setManditory(dataGridColumn.isMandatory());
                        //singleFile.setLabelName(dataGridColumn.getLabelName());
                        singleFile.setLabelName(dataGridColumn.getLabel());
                        //singleFile.setHint(dataGridColumn.getHint());
                        singleFile.setAllowedTypes(dataGridColumn.getFileProperties().getFileTypes());

                        for (LabelPath l : labelPaths) {
                            //if (l.getLabelName().equalsIgnoreCase(dataGridColumn.getLabelName())) {
                            if (l.getLabelName().equalsIgnoreCase(dataGridColumn.getLabel())) {
                                singleFile.setFilePath(l.getPath());
                                singleFile.setType(l.getType());
                            }
                        }

                        addSingleFileUploadView(singleFile);
                    }

                }
            }
        }
    }

    private void addListField(QuestionnaireListModel listModel) {

        View listFieldView = getLayoutInflater().inflate(R.layout.list_fielditem, null, false);

        TextView tvQuestionName = (TextView) listFieldView.findViewById(R.id.tv_questionName);
        TextView tvTextFieldManditory = (TextView) listFieldView.findViewById(R.id.tv_manditory);
        RecyclerView rvCheckBoxes = (RecyclerView) listFieldView.findViewById(R.id.rv_checkBoxes);
        TextView tvHint = (TextView) listFieldView.findViewById(R.id.tv_hint);


        tvQuestionName.setText(listModel.getQuestionName());

        if (listModel.isManditory()) {
            tvTextFieldManditory.setVisibility(View.VISIBLE);
        } else {
            tvTextFieldManditory.setVisibility(View.GONE);
        }

        if (listModel.getHint() != null && !listModel.getHint().trim().equalsIgnoreCase("")) {
            tvHint.setVisibility(View.VISIBLE);
            tvHint.setText(listModel.getHint());
        } else {
            tvHint.setVisibility(View.GONE);
        }

        /*for (QuestionnaireCheckbox checkbox : checkBoxList) {

            for (String item : listModel.getSelectedItems()) {

                if (item.equalsIgnoreCase(checkbox.getText())) {

                    checkbox.setChecked(true);
                }
            }
        }*/

        rvCheckBoxes.setLayoutManager(new LinearLayoutManager(getContext()));
        CheckBoxAdapter checkBoxAdapter = new CheckBoxAdapter(listModel.getQuestionnaireCheckboxes(), listModel.maxAnswerable, getContext());
        rvCheckBoxes.setAdapter(checkBoxAdapter);
        checkBoxAdapter.notifyDataSetChanged();

        llParentLayout.addView(listFieldView);
        viewsList.put(listModel.getLabelName(), listFieldView);

    }

    private void addTextFieldView(QuestionnaireTextField textField) {

        View textFieldView = getLayoutInflater().inflate(R.layout.edittext_field, null, false);

        TextView tvQuestionName = (TextView) textFieldView.findViewById(R.id.tv_questionName);
        TextView tvTextFieldManditory = (TextView) textFieldView.findViewById(R.id.tv_manditory);
        EditText etTextField = (EditText) textFieldView.findViewById(R.id.et_textBox);
        TextView tvHint = (TextView) textFieldView.findViewById(R.id.tv_hint);


        tvQuestionName.setText(textField.getQuestionName());

        if (textField.getHint() != null && !textField.getHint().trim().equalsIgnoreCase("")) {
            tvHint.setVisibility(View.VISIBLE);
            tvHint.setText(textField.getHint());
        } else {
            tvHint.setVisibility(View.GONE);
        }

        etTextField.setText(textField.getText());

        if (textField.isManditory()) {
            tvTextFieldManditory.setVisibility(View.VISIBLE);
        } else {
            tvTextFieldManditory.setVisibility(View.GONE);
        }

        llParentLayout.addView(textFieldView);
        viewsList.put(textField.getLabelName(), textFieldView);

    }

    private void addNumberFieldView(QuestionnaireNumberModel numberField) {

        View dateFieldView = getLayoutInflater().inflate(R.layout.numberfield_item, null, false);

        TextView tvQuestionName = (TextView) dateFieldView.findViewById(R.id.tv_questionName);
        TextView tvTextFieldManditory = (TextView) dateFieldView.findViewById(R.id.tv_manditory);
        EditText etTextField = (EditText) dateFieldView.findViewById(R.id.et_textBox);
        TextView tvHint = (TextView) dateFieldView.findViewById(R.id.tv_hint);


        tvQuestionName.setText(numberField.getQuestionName());

        etTextField.setText(String.valueOf(numberField.getNumber()));

        if (numberField.getHint() != null && !numberField.getHint().trim().equalsIgnoreCase("")) {
            tvHint.setVisibility(View.VISIBLE);
            tvHint.setText(numberField.getHint());
        } else {
            tvHint.setVisibility(View.GONE);
        }

        if (numberField.isManditory()) {
            tvTextFieldManditory.setVisibility(View.VISIBLE);
        } else {
            tvTextFieldManditory.setVisibility(View.GONE);
        }

        llParentLayout.addView(dateFieldView);
        viewsList.put(numberField.getLabelName(), dateFieldView);

    }

    private void addDateFieldView(QuestionnaireDateField dateField) {

        View dateFieldView = getLayoutInflater().inflate(R.layout.date_item, null, false);

        TextView tvQuestionName = (TextView) dateFieldView.findViewById(R.id.tv_questionName);
        TextView tvTextFieldManditory = (TextView) dateFieldView.findViewById(R.id.tv_manditory);
        RelativeLayout rlCalender = (RelativeLayout) dateFieldView.findViewById(R.id.rl_calender);
        TextView tvDate = (TextView) dateFieldView.findViewById(R.id.tv_date);
        TextView tvHint = (TextView) dateFieldView.findViewById(R.id.tv_hint);

        tvQuestionName.setText(dateField.getQuestionName());

        tvDate.setText(dateField.getDate());

        if (dateField.getHint() != null && !dateField.getHint().trim().equalsIgnoreCase("")) {
            tvHint.setVisibility(View.VISIBLE);
            tvHint.setText(dateField.getHint());
        } else {
            tvHint.setVisibility(View.GONE);
        }

        if (dateField.isManditory()) {
            tvTextFieldManditory.setVisibility(View.VISIBLE);
        } else {
            tvTextFieldManditory.setVisibility(View.GONE);
        }

        rlCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                qLabelName = dateField.getLabelName();
                com.jaldeeinc.jaldee.custom.DatePicker mDatePickerDialogFragment;
                mDatePickerDialogFragment = new com.jaldeeinc.jaldee.custom.DatePicker();
                mDatePickerDialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "DATE PICK");
            }
        });


        llParentLayout.addView(dateFieldView);
        viewsList.put(dateField.getLabelName(), dateFieldView);


    }

    private void addBooleanField(QuestionnaireBoolean boolField) {

        View boolFieldView = getLayoutInflater().inflate(R.layout.boolean_item, null, false);

        TextView tvQuestionName = (TextView) boolFieldView.findViewById(R.id.tv_questionName);
        TextView tvTextFieldManditory = (TextView) boolFieldView.findViewById(R.id.tv_manditory);
        RadioGroup radioGroup = (RadioGroup) boolFieldView.findViewById(R.id.rg_radioGroup);
        RadioButton radioButtonYes = (RadioButton) boolFieldView.findViewById(R.id.rb_yes);
        RadioButton radioButtonNo = (RadioButton) boolFieldView.findViewById(R.id.rb_no);
        TextView tvHint = (TextView) boolFieldView.findViewById(R.id.tv_hint);


        tvQuestionName.setText(boolField.getQuestionName());

        if (boolField.getHint() != null && !boolField.getHint().trim().equalsIgnoreCase("")) {
            tvHint.setVisibility(View.VISIBLE);
            tvHint.setText(boolField.getHint());
        } else {
            tvHint.setVisibility(View.GONE);
        }

        if (boolField.isManditory()) {
            tvTextFieldManditory.setVisibility(View.VISIBLE);
        } else {
            tvTextFieldManditory.setVisibility(View.GONE);
        }

        if (boolField.getLabels() != null) {

            radioButtonYes.setText(boolField.getLabels().get(0));
            radioButtonNo.setText(boolField.getLabels().get(1));
        }

        if (boolField.isSelected() != null && boolField.isSelected()) {

            radioButtonYes.setChecked(true);
        } else if (boolField.isSelected() != null && !boolField.isSelected()) {

            radioButtonNo.setChecked(true);
        }

        llParentLayout.addView(boolFieldView);
        viewsList.put(boolField.getLabelName(), boolFieldView);

    }


    private void addFileUploadView(QuestionnaireFileUploadModel model) {

        View fileUploadView = getLayoutInflater().inflate(R.layout.questnnarefile_upload, null, false);

        TextView tvQuestionName = fileUploadView.findViewById(R.id.tv_questionName);
        MultiSpinnerSearch filesSpinner = (MultiSpinnerSearch) fileUploadView.findViewById(R.id.mfilesSpinner);
        RecyclerView rvFiles = (RecyclerView) fileUploadView.findViewById(R.id.rv_files);
        TextView tvMutipleFileManditory = fileUploadView.findViewById(R.id.tv_multipleFileManditory);
        TextView tvSupportedTypes = fileUploadView.findViewById(R.id.tv_supportedTypes);
        TextView tvHint = fileUploadView.findViewById(R.id.tv_hint);

        tvQuestionName.setText(model.getQuestionName());
        tvSupportedTypes.setText(model.getAllowedTypes().toString());

        if (model.getHint() != null && !model.getHint().trim().equalsIgnoreCase("")) {
            tvHint.setVisibility(View.VISIBLE);
            tvHint.setText(model.getHint());
        } else {
            tvHint.setVisibility(View.GONE);
        }

        if (model.isManditory()) {
            tvMutipleFileManditory.setVisibility(View.VISIBLE);
        } else {

            tvMutipleFileManditory.setVisibility(View.GONE);
        }

        rvFiles.setLayoutManager(new LinearLayoutManager(context));
        FilesAdapter filesAdapter = new FilesAdapter(model.getFiles(), context, false, iFilesInterface);
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


    private void addSingleFileUploadView(QuestnnaireSingleFile singleFile) {

        View fileUploadView = getLayoutInflater().inflate(R.layout.singlefile_upload, null, false);

        TextView tvQuestionName = fileUploadView.findViewById(R.id.tv_questionName);
        TextView tvMutipleFileManditory = fileUploadView.findViewById(R.id.tv_singleFileManditory);
        TextView tvSupportedTypes = fileUploadView.findViewById(R.id.tv_supportedTypes);
        LinearLayout llUpload = (LinearLayout) fileUploadView.findViewById(R.id.ll_upload);
        ImageView ivSingleFile = (ImageView) fileUploadView.findViewById(R.id.iv_file);
        ImageView ivClose = (ImageView) fileUploadView.findViewById(R.id.iv_close);
        TextView tvHint = fileUploadView.findViewById(R.id.tv_hint);
        TextView tvPath = fileUploadView.findViewById(R.id.tv_path);

        tvQuestionName.setText(singleFile.getQuestionName());
        tvSupportedTypes.setText(singleFile.getAllowedTypes().toString());

        if (singleFile.getFilePath() != null && !singleFile.getFilePath().trim().equalsIgnoreCase("")) {

            tvPath.setText(singleFile.getFilePath());
            if (singleFile.getFilePath().contains("http://") || singleFile.getFilePath().contains("https://")) {

                if (singleFile.getType().equalsIgnoreCase(".pdf")) {

                    ivSingleFile.setImageDrawable(context.getResources().getDrawable(R.drawable.pdfs));
                } else {

                    Glide.with(context).load(singleFile.getFilePath()).into(ivSingleFile);
                }
            } else {
                if (singleFile.getFilePath().substring(singleFile.getFilePath().lastIndexOf(".") + 1).equals("pdf")) {

                    ivSingleFile.setImageDrawable(context.getResources().getDrawable(R.drawable.pdfs));

                } else if (singleFile.getFilePath().substring(singleFile.getFilePath().lastIndexOf(".") + 1).equals("mp3")) {

                    ivSingleFile.setImageDrawable(context.getResources().getDrawable(R.drawable.audio_icon));

                } else if (Arrays.asList(videoFormats).contains(singleFile.getFilePath().substring(singleFile.getFilePath().lastIndexOf(".") + 1))) {

                    ivSingleFile.setImageDrawable(context.getResources().getDrawable(R.drawable.video_icon));

                } else {

                    ivSingleFile.setImageBitmap(BitmapFactory.decodeFile(singleFile.getFilePath()));
                }
            }
            ivSingleFile.setVisibility(View.VISIBLE);
            ivClose.setVisibility(View.VISIBLE);

        } else {

            ivClose.setVisibility(View.GONE);
        }

        if (singleFile.getHint() != null && !singleFile.getHint().trim().equalsIgnoreCase("")) {
            tvHint.setVisibility(View.VISIBLE);
            tvHint.setText(singleFile.getHint());
        } else {
            tvHint.setVisibility(View.GONE);
        }

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

                if (ivSingleFile.getDrawable() != null) {

                    ivSingleFile.setImageDrawable(null);
                    ivClose.setVisibility(View.GONE);
                } else {


                }
            }
        });

        ivSingleFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String imagePath = tvPath.getText().toString();

                if (imagePath.contains("http://") || imagePath.contains("https://")) {

                    if (singleFile.getType().equalsIgnoreCase(".pdf")) {

                        Config.openOnlinePdf(context, singleFile.getFilePath());

                    } else {

                        Intent intent = new Intent(context, ImageActivity.class);
                        intent.putExtra("urlOrPath", singleFile.getFilePath());
                        ((AppCompatActivity)context).startActivity(intent);
                    }

                } else {

                    String extension = "";

                    if (imagePath.contains(".")) {
                        extension = imagePath.substring(imagePath.lastIndexOf(".") + 1);
                    }

                    if (imagePath.substring(imagePath.lastIndexOf(".") + 1).equals("pdf")) {

                        Config.openPdf(context.getApplicationContext(), imagePath);

                    } else if (Arrays.asList(videoFormats).contains(extension)) {

                        Intent intent = new Intent(context, VideoActivity.class);
                        intent.putExtra("urlOrPath", imagePath);
                        ((AppCompatActivity)context).startActivity(intent);

                    } else if (extension.contains("mp3")) {

                        playAudio(imagePath);

                    } else {

                        Intent intent = new Intent(context, ImageActivity.class);
                        intent.putExtra("urlOrPath", imagePath);
                        ((AppCompatActivity)context).startActivity(intent);
                    }
                }
            }
        });


        llParentLayout.addView(fileUploadView);
        viewsList.put(singleFile.getLabelName(), fileUploadView);

    }

    private void playAudio(String imagePath) {

        Intent i = new Intent(android.content.Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        i.setDataAndType(FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", new File(imagePath)), "audio/*");
        ((AppCompatActivity)context).startActivity(i);
    }

    private void openImageOptions() {

        try {

            Dialog dialog = new Dialog(context);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.camera_options);
            dialog.getWindow().getAttributes().windowAnimations = R.style.slidingUpAndDown;
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
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
                if ((ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ((AppCompatActivity)context).requestPermissions(new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, GALLERY);

                    return;
                } else {
                    Intent intent = new Intent();
                    intent.setType("*/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    ((AppCompatActivity)context).startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY);
                }
            } else {

                Intent intent = new Intent();
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                ((AppCompatActivity)context).startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void openCamera() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {


                    ((AppCompatActivity)context).requestPermissions(new String[]{
                            Manifest.permission.CAMERA}, CAMERA);

                    return;
                } else {
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    Intent cameraIntent = new Intent();
                    cameraIntent.setType("image/*");
                    cameraIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                    cameraIntent.setAction(Intent.ACTION_GET_CONTENT);
                    ((AppCompatActivity)context).startActivityForResult(intent, CAMERA);
                }
            } else {

                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                Intent cameraIntent = new Intent();
                cameraIntent.setType("image/*");
                cameraIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                cameraIntent.setAction(Intent.ACTION_GET_CONTENT);
                ((AppCompatActivity)context).startActivityForResult(intent, CAMERA);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

//        View fileUploadView = viewsList.get(question.getGetQuestion().getLabelName());
//        RecyclerView rvFiles = (RecyclerView) fileUploadView.findViewById(R.id.rv_files);
//        FilesAdapter filesAdapter = (FilesAdapter) rvFiles.getAdapter();
//
//        List<KeyPairBoolData> files = filesAdapter.getFiles();


    }

    private void openGalleryForOneImage() {

        try {

            Dialog dialog = new Dialog(context);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.camera_options);
            dialog.getWindow().getAttributes().windowAnimations = R.style.slidingUpAndDown;
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
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
                if ((ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ((AppCompatActivity)context).requestPermissions(new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, GALLERY_FOR_ONE);

                    return;
                } else {
                    Intent intent = new Intent();
                    intent.setType("*/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    ((AppCompatActivity)context).startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_FOR_ONE);
                }
            } else {

                Intent intent = new Intent();
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                ((AppCompatActivity)context).startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_FOR_ONE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void openCameraforOneImage() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {


                    ((AppCompatActivity)context).requestPermissions(new String[]{
                            Manifest.permission.CAMERA}, CAMERA_FOR_ONE);

                    return;
                } else {
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    Intent cameraIntent = new Intent();
                    cameraIntent.setType("image/*");
                    cameraIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                    cameraIntent.setAction(Intent.ACTION_GET_CONTENT);
                    ((AppCompatActivity)context).startActivityForResult(intent, CAMERA_FOR_ONE);

                }
            } else {

                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                Intent cameraIntent = new Intent();
                cameraIntent.setType("image/*");
                cameraIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                cameraIntent.setAction(Intent.ACTION_GET_CONTENT);
                ((AppCompatActivity)context).startActivityForResult(intent, CAMERA_FOR_ONE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
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

        View numberFieldView = viewsList.get(qLabelName);
        TextView tvDate = numberFieldView.findViewById(R.id.tv_date);
        String selectedDate = dateParser.format(mCalender.getTime());
        tvDate.setText(selectedDate);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

       //////////// super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_FOR_ONE) {
            if (data != null) {
                try {
                    if (data.getData() != null) {
                        Uri uri = data.getData();

                        String mimeType = this.context.getContentResolver().getType(uri);
                        String uriString = uri.toString();
                        String extension = "";
                        if (uriString.contains(".")) {
                            extension = uriString.substring(uriString.lastIndexOf(".") + 1);
                        }

                        if (mimeType != null) {
                            extension = mimeType.substring(mimeType.lastIndexOf("/") + 1);
                        }
                        File photoFile = null;

                        try {
                            // Creating file
                            try {
                                photoFile = Config.createFile(context, extension, true);
                            } catch (IOException ex) {
                                Toast.makeText(context, "Error occurred while creating the file", Toast.LENGTH_SHORT).show();

                                // Log.d(TAG, "Error occurred while creating the file");
                            }

                            InputStream inputStream = context.getContentResolver().openInputStream(uri);
                            FileOutputStream fileOutputStream = new FileOutputStream(photoFile);
                            // Copying
                            Config.copyStream(inputStream, fileOutputStream);
                            fileOutputStream.close();
                            inputStream.close();
                        } catch (Exception e) {
                            Toast.makeText(context, "onActivityResult: " + e.toString(), Toast.LENGTH_SHORT).show();

                            //Log.d(TAG, "onActivityResult: " + e.toString());
                        }
                        String orgFilePath = photoFile.getAbsolutePath();

                        if (orgFilePath == null) {
                            orgFilePath = Config.getFilePathFromURI(context, uri, extension);
                        }

                        View fileUploadView = viewsList.get(qLabelName);
                        RecyclerView rvFiles = (RecyclerView) fileUploadView.findViewById(R.id.rv_files);
                        TextView tvSupportedTypes = fileUploadView.findViewById(R.id.tv_supportedTypes);
                        FilesAdapter filesAdapter = (FilesAdapter) rvFiles.getAdapter();

                        if (tvSupportedTypes.getText().toString().contains(extension)) {
                            fileObject.setImagePath(orgFilePath);
                            filesAdapter.updateFileObject(fileObject);
                        } else {

                            Toast.makeText(context, "File type not supported", Toast.LENGTH_SHORT).show();
                        }


                    } else if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            String mimeType = this.context.getContentResolver().getType(uri);
                            String uriString = uri.toString();
                            String extension = "";
                            if (uriString.contains(".")) {
                                extension = uriString.substring(uriString.lastIndexOf(".") + 1);
                            }

                            if (mimeType != null) {
                                extension = mimeType.substring(mimeType.lastIndexOf("/") + 1);
                            }
                            File photoFile = null;

                            try {
                                // Creating file
                                try {
                                    photoFile = Config.createFile(context, extension, true);
                                } catch (IOException ex) {
                                    Toast.makeText(context, "Error occurred while creating the file", Toast.LENGTH_SHORT).show();

                                    // Log.d(TAG, "Error occurred while creating the file");
                                }

                                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                                FileOutputStream fileOutputStream = new FileOutputStream(photoFile);
                                // Copying
                                Config.copyStream(inputStream, fileOutputStream);
                                fileOutputStream.close();
                                inputStream.close();
                            } catch (Exception e) {
                                Toast.makeText(context, "onActivityResult: " + e.toString(), Toast.LENGTH_SHORT).show();

                                //Log.d(TAG, "onActivityResult: " + e.toString());
                            }
                            String orgFilePath = photoFile.getAbsolutePath();

                            if (orgFilePath == null) {
                                orgFilePath = Config.getFilePathFromURI(context, uri, extension);
                            }

                            View fileUploadView = viewsList.get(qLabelName);
                            RecyclerView rvFiles = (RecyclerView) fileUploadView.findViewById(R.id.rv_files);
                            TextView tvSupportedTypes = fileUploadView.findViewById(R.id.tv_supportedTypes);
                            FilesAdapter filesAdapter = (FilesAdapter) rvFiles.getAdapter();

                            if (tvSupportedTypes.getText().toString().contains(extension)) {
                                fileObject.setImagePath(orgFilePath);
                                filesAdapter.updateFileObject(fileObject);
                            } else {
                                Toast.makeText(context, "File type not supported", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else if (requestCode == CAMERA_FOR_ONE) {
            if (data != null && data.getExtras() != null) {
                File photoFile = null;/////////
                // ///////
                try {//////////
                    photoFile = Config.createFile(context, "png", true);//////////
                } catch (IOException e) {/////////////
                    e.printStackTrace();///////////
                }///////////
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");/////////
                try (FileOutputStream out = new FileOutputStream(photoFile)) {////////////
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance////////////
                    // PNG is a lossless format, the compression factor (100) is ignored/////////
                } catch (IOException e) {////////////
                    e.printStackTrace();///////////
                }////////
                String path = photoFile.getAbsolutePath();////////
                /*Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                String path = saveImage(bitmap);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);*/
                if (path != null) {
                    mImageUri = Uri.parse(path);

                    View fileUploadView = viewsList.get(qLabelName);
                    RecyclerView rvFiles = (RecyclerView) fileUploadView.findViewById(R.id.rv_files);
                    FilesAdapter filesAdapter = (FilesAdapter) rvFiles.getAdapter();

                    fileObject.setImagePath(path);
                    filesAdapter.updateFileObject(fileObject);
                }
               /* try {
                    bytes.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/


            }
        } else if (requestCode == GALLERY) {

            if (data != null) {
                try {
                    if (data.getData() != null) {
                        Uri uri = data.getData();

                        String mimeType = this.context.getContentResolver().getType(uri);
                        String uriString = uri.toString();
                        String extension = "";
                        if (uriString.contains(".")) {
                            extension = uriString.substring(uriString.lastIndexOf(".") + 1);
                        }

                        if (mimeType != null) {
                            extension = mimeType.substring(mimeType.lastIndexOf("/") + 1);
                        }
                        File photoFile = null;

                        try {
                            // Creating file
                            try {
                                photoFile = Config.createFile(context, extension, true);
                            } catch (IOException ex) {
                                Toast.makeText(context, "Error occurred while creating the file", Toast.LENGTH_SHORT).show();

                                // Log.d(TAG, "Error occurred while creating the file");
                            }

                            InputStream inputStream = context.getContentResolver().openInputStream(uri);
                            FileOutputStream fileOutputStream = new FileOutputStream(photoFile);
                            // Copying
                            Config.copyStream(inputStream, fileOutputStream);
                            fileOutputStream.close();
                            inputStream.close();
                        } catch (Exception e) {
                            Toast.makeText(context, "onActivityResult: " + e.toString(), Toast.LENGTH_SHORT).show();

                            //Log.d(TAG, "onActivityResult: " + e.toString());
                        }
                        String orgFilePath = photoFile.getAbsolutePath();

                        if (orgFilePath == null) {
                            orgFilePath = Config.getFilePathFromURI(context, uri, extension);
                        }
                        /*Uri uri = data.getData();
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
                        if (orgFilePath == null) {
                            orgFilePath = getFilePathFromURI(mContext, uri, extension);
                        }*/


                        singleFilePath = orgFilePath;
                        View fileUploadView = viewsList.get(qLabelName);
                        ImageView ivSingleFile = (ImageView) fileUploadView.findViewById(R.id.iv_file);
                        TextView tvPath = fileUploadView.findViewById(R.id.tv_path);
                        TextView tvSupportedTypes = fileUploadView.findViewById(R.id.tv_supportedTypes);
                        ImageView ivClose = (ImageView) fileUploadView.findViewById(R.id.iv_close);
                        ivClose.setVisibility(View.VISIBLE);
                        tvPath.setText(orgFilePath);

                        if (tvSupportedTypes.getText().toString().contains(extension)) {

                            if (orgFilePath != null && orgFilePath.substring(orgFilePath.lastIndexOf(".") + 1).equals("pdf")) {

                                ivSingleFile.setImageDrawable(context.getResources().getDrawable(R.drawable.pdfs));

                            } else if (Arrays.asList(videoFormats).contains(extension)) {

                                ivSingleFile.setImageDrawable(context.getResources().getDrawable(R.drawable.video_icon));

                            } else if (orgFilePath.substring(orgFilePath.lastIndexOf(".") + 1).equals("mp3")) {

                                ivSingleFile.setImageDrawable(context.getResources().getDrawable(R.drawable.audio_icon));

                            } else {
                                ivSingleFile.setImageBitmap(BitmapFactory.decodeFile(orgFilePath));
                            }

                        } else {

                            Toast.makeText(context, "File type not supported", Toast.LENGTH_SHORT).show();
                        }

                    } else if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            String mimeType = this.context.getContentResolver().getType(uri);
                            String uriString = uri.toString();
                            String extension = "";
                            if (uriString.contains(".")) {
                                extension = uriString.substring(uriString.lastIndexOf(".") + 1);
                            }

                            if (mimeType != null) {
                                extension = mimeType.substring(mimeType.lastIndexOf("/") + 1);
                            }
                            File photoFile = null;

                            try {
                                // Creating file
                                try {
                                    photoFile = Config.createFile(context, extension, true);
                                } catch (IOException ex) {
                                    Toast.makeText(context, "Error occurred while creating the file", Toast.LENGTH_SHORT).show();

                                    // Log.d(TAG, "Error occurred while creating the file");
                                }

                                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                                FileOutputStream fileOutputStream = new FileOutputStream(photoFile);
                                // Copying
                                Config.copyStream(inputStream, fileOutputStream);
                                fileOutputStream.close();
                                inputStream.close();
                            } catch (Exception e) {
                                Toast.makeText(context, "onActivityResult: " + e.toString(), Toast.LENGTH_SHORT).show();

                                //Log.d(TAG, "onActivityResult: " + e.toString());
                            }
                            String orgFilePath = photoFile.getAbsolutePath();

                            if (orgFilePath == null) {
                                orgFilePath = Config.getFilePathFromURI(context, uri, extension);
                            }

                            singleFilePath = orgFilePath;

                            View fileUploadView = viewsList.get(qLabelName);
                            ImageView ivSingleFile = (ImageView) fileUploadView.findViewById(R.id.iv_file);
                            TextView tvPath = fileUploadView.findViewById(R.id.tv_path);
                            TextView tvSupportedTypes = fileUploadView.findViewById(R.id.tv_supportedTypes);
                            ImageView ivClose = (ImageView) fileUploadView.findViewById(R.id.iv_close);
                            ivClose.setVisibility(View.VISIBLE);
                            tvPath.setText(orgFilePath);

                            if (tvSupportedTypes.getText().toString().contains(extension)) {

                                if (orgFilePath != null && orgFilePath.substring(orgFilePath.lastIndexOf(".") + 1).equals("pdf")) {
                                    ivSingleFile.setImageDrawable(context.getResources().getDrawable(R.drawable.pdfs));

                                } else if (Arrays.asList(videoFormats).contains(extension)) {

                                    ivSingleFile.setImageDrawable(context.getResources().getDrawable(R.drawable.video_icon));

                                } else if (orgFilePath.substring(orgFilePath.lastIndexOf(".") + 1).equals("mp3")) {

                                    ivSingleFile.setImageDrawable(context.getResources().getDrawable(R.drawable.audio_icon));

                                } else {
                                    ivSingleFile.setImageBitmap(BitmapFactory.decodeFile(orgFilePath));
                                }
                            } else {
                                Toast.makeText(context, "File type not supported", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else if (requestCode == CAMERA) {

            if (data != null && data.getExtras() != null) {
                File photoFile = null;/////////
                // ///////
                try {//////////
                    photoFile = Config.createFile(context, "png", true);//////////
                } catch (IOException e) {/////////////
                    e.printStackTrace();///////////
                }///////////
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");/////////
                try (FileOutputStream out = new FileOutputStream(photoFile)) {////////////
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance////////////
                    // PNG is a lossless format, the compression factor (100) is ignored/////////
                } catch (IOException e) {////////////
                    e.printStackTrace();///////////
                }////////
                String path = photoFile.getAbsolutePath();////////
                /*Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                String path = saveImage(bitmap);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);*/
                if (path != null) {
                    mImageUri = Uri.parse(path);

                    singleFilePath = path;
                    View fileUploadView = viewsList.get(qLabelName);
                    ImageView ivSingleFile = (ImageView) fileUploadView.findViewById(R.id.iv_file);
                    ImageView ivClose = (ImageView) fileUploadView.findViewById(R.id.iv_close);
                    TextView tvPath = fileUploadView.findViewById(R.id.tv_path);
                    ivSingleFile.setImageBitmap(BitmapFactory.decodeFile(path));
                    ivClose.setVisibility(View.VISIBLE);
                    tvPath.setText(path);


                }
               /* try {
                    bytes.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/


            }
        }
    }
}
