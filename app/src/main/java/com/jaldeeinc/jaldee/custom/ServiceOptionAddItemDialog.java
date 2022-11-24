package com.jaldeeinc.jaldee.custom;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jaldeeinc.jaldee.Interface.IFilesInterface;
import com.jaldeeinc.jaldee.Interface.IServiceOption;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.adapter.FilesAdapter;
import com.jaldeeinc.jaldee.adapter.ServiceOptionListAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.model.DataGrid;
import com.jaldeeinc.jaldee.model.GridColumnAnswerLine;
import com.jaldeeinc.jaldee.model.QuestionnaireCheckbox;
import com.jaldeeinc.jaldee.response.CatalogItem;
import com.jaldeeinc.jaldee.response.DataGridColumns;
import com.jaldeeinc.jaldee.response.DataGridProperties;
import com.jaldeeinc.jaldee.response.GetQuestion;
import com.jaldeeinc.jaldee.utils.SharedPreference;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServiceOptionAddItemDialog extends Fragment implements IServiceOption {
    Context mContext;
    int position = -1;
    private GetQuestion mQuestion = new GetQuestion();
    private DataGrid mAnswer = new DataGrid();
    IServiceOption iServiceOptionListOptionChange;
    String selectedItemName;
    Float selectedItemPrice;
    LinearLayout llParentLayout;
    private CardView cv_submit, cv_cover;
    private TextView tv_totalprice;
    private KeyPairBoolData fileObject = new KeyPairBoolData();
    private String qLabelName = "";
    private HashMap<String, View> viewsList = new HashMap<>();
    private int GALLERY = 3, CAMERA = 4;
    private Uri mImageUri;
    boolean isEdit;
    ServiceOptionGridView gridView;
    Dialog itemOptionsDialog;

    private static final String GRID_QUESTION = "gridQuestion";
    private static final String GRID_ANSWERS = "gridAnswers";
    private static final String POSITION = "position";
    private static final String ISEDIT = "isEdit";
//
//    public ServiceOptionAddItemDialog(Context mContext) {
//        this.mContext = mContext;
//        this.position = -1;
//        iServiceOptionListOptionChange = this;
//
//    }
//
//    public ServiceOptionAddItemDialog(Context mContext, int position) {
//        this.mContext = mContext;
//        this.position = position;
//        iServiceOptionListOptionChange = this;
//
//    }

    public static ServiceOptionAddItemDialog newInstance(String question) {
        ServiceOptionAddItemDialog fragment = new ServiceOptionAddItemDialog();
        Bundle args = new Bundle();
        args.putString(GRID_QUESTION, question);
        args.putString(GRID_ANSWERS, null);
        args.putInt(POSITION, -1);
        fragment.setArguments(args);

        return fragment;
    }

    public static ServiceOptionAddItemDialog newInstance(String question, String answer, int position, boolean isEdit) {
        ServiceOptionAddItemDialog fragment = new ServiceOptionAddItemDialog();
        Bundle args = new Bundle();
        args.putString(GRID_QUESTION, question);
        args.putString(GRID_ANSWERS, answer);
        args.putInt(POSITION, position);
        args.putBoolean(ISEDIT, isEdit);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iServiceOptionListOptionChange = this;
        mContext = getContext();

        if (getArguments() != null) {
            position = getArguments().getInt(POSITION, -1);
            isEdit = getArguments().getBoolean(ISEDIT, true);
        }
        String question = SharedPreference.getInstance(mContext).getStringValue(Constants.QUESTION, "");
        String answer = SharedPreference.getInstance(mContext).getStringValue(Constants.ANSWER, null);

        if (question != null && !question.trim().equalsIgnoreCase("")) {

            try {

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                mQuestion = gson.fromJson(question, GetQuestion.class);

            } catch (JsonSyntaxException e) {
                mQuestion = new GetQuestion();
                e.printStackTrace();
            }

        } else {

            mQuestion = new GetQuestion();
        }
        if (answer != null && !answer.trim().equalsIgnoreCase("")) {

            try {

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                mAnswer = gson.fromJson(answer, DataGrid.class);

            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        } else {

            mAnswer = null;
        }
//    }
//    @Override
//    public void onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
        //View view = inflater.inflate(R.layout.service_option_add_item_dialog, container, false);
        if (mAnswer != null && mAnswer.getDataGridListColumn().size() > 0) {  // if edit items
            JsonArray ja = mAnswer.getDataGridListColumn().get(0).getColumn().getAsJsonArray("list");
            if (ja.get(0).toString().trim() != null && !ja.get(0).toString().trim().isEmpty()) {
                selectedItemName = ja.get(0).getAsString();
            }
            if (selectedItemName != null && !selectedItemName.trim().isEmpty()) {
                serviceOptionAddItemOptionsDialog(selectedItemName);
            } else {
                DynamicToast.make(mContext, "Please try again",
                        ContextCompat.getColor(mContext, R.color.white), ContextCompat.getColor(mContext, R.color.red), Toast.LENGTH_SHORT).show();
            }
        } else {
            final Dialog dialog = new Dialog(mContext);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.service_option_add_item_dialog);
            DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
            int width = (int) (metrics.widthPixels * 1);
            dialog.setCancelable(false);
            dialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
            ImageView ivClose = dialog.findViewById(R.id.iv_close);
            RecyclerView rvList = dialog.findViewById(R.id.rv_list);
            Button btnAdd = dialog.findViewById(R.id.btnAdd);

            DataGridProperties dataGridProperties = mQuestion.getDataGridListProperties();
            ArrayList<DataGridColumns> gridListColumns = dataGridProperties.getGridListColumns();

            DataGridColumns dataGridColumn = gridListColumns.get(0);
            //for (DataGridColumns dataGridColumn : gridListColumns) {
            if (dataGridColumn.getDataType().equalsIgnoreCase("list")) {
                Map<String, Object> itemAndPrices;
                ArrayList<String> values = (ArrayList) dataGridColumn.getListProperties().getValues();
                ArrayList<QuestionnaireCheckbox> questionnaireCheckboxes = new ArrayList<>();
                if (dataGridColumn.getListProperties().getBasePrice() != null) {
                    itemAndPrices = Config.jsonStringToMap(dataGridColumn.getListProperties().getBasePrice().replaceAll("\\s", ""));
                    for (String v : values) {
                        if (itemAndPrices.get(v.replaceAll("\\s", "")) != null) {  //create check box if  value v is present in itemPrices only
                            QuestionnaireCheckbox questionnaireCheckbox = new QuestionnaireCheckbox();
                            questionnaireCheckbox.setText(v);
                            questionnaireCheckbox.setPrice(Float.parseFloat(itemAndPrices.get(v.replaceAll("\\s", "")).toString()));
                            questionnaireCheckbox.setChecked(false);
                            questionnaireCheckbox.setBase(true); // set baselist or not
                            questionnaireCheckboxes.add(questionnaireCheckbox);
                        }
                    }
                } else {
                    for (String v : values) {
                        QuestionnaireCheckbox questionnaireCheckbox = new QuestionnaireCheckbox();
                        questionnaireCheckbox.setText(v);
                        questionnaireCheckbox.setPrice(null);
                        questionnaireCheckbox.setChecked(false);
                        questionnaireCheckbox.setBase(true); // set baselist or not
                        questionnaireCheckboxes.add(questionnaireCheckbox);
                    }
                }
                rvList.setLayoutManager(new LinearLayoutManager(mContext));
                ServiceOptionListAdapter serviceOptionListAdapter = new ServiceOptionListAdapter(mContext, questionnaireCheckboxes, iServiceOptionListOptionChange);
                rvList.setAdapter(serviceOptionListAdapter);
                serviceOptionListAdapter.notifyDataSetChanged();
            }
            //}

            dialog.show();

            ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreference.getInstance(mContext).setValue(Constants.QUESTION, "");
                    SharedPreference.getInstance(mContext).setValue(Constants.ANSWER, "");
                    dialog.cancel();
                }
            });
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectedItemName != null && !selectedItemName.trim().isEmpty()) {
                        dialog.cancel();
                        serviceOptionAddItemOptionsDialog(selectedItemName);
                    } else {
                        DynamicToast.make(mContext, "Please select an item",
                                ContextCompat.getColor(mContext, R.color.white), ContextCompat.getColor(mContext, R.color.red), Toast.LENGTH_SHORT).show();

                    }

                }
            });
        }
        //return view;
    }

    public void setGridView(ServiceOptionGridView gView) {

        gridView = gView;

    }

    private void createQuestionnaire(GetQuestion objQuestion) {

        try {

            ArrayList<DataGridColumns> questionsList = objQuestion.getDataGridListProperties().getGridListColumns();

            JSONObject priceGridList = new JSONObject(objQuestion.getPriceGridList());
            JSONObject selectedPriceGridList = priceGridList.getJSONObject(selectedItemName);

            //for (DataGridColumns question : questionsList) {
            for (int i = 0; i < questionsList.size(); i++) {
                DataGridColumns question = questionsList.get(i);
                if (question.getDataType().equalsIgnoreCase("fileUpload")) {

                    QuestionnaireFileUploadView fileUploadView = new QuestionnaireFileUploadView(mContext);
                    fileUploadView.setServiceOptionGridQuestionData(question, getItemPrice(selectedPriceGridList, question));
                    fileUploadView.setListener(new IFilesInterface() {
                        @Override
                        public void onFileUploadClick(KeyPairBoolData data, String labelName) {

                            fileObject = new KeyPairBoolData();
                            fileObject.setId(data.getId());
                            fileObject.setName(data.getName());
                            qLabelName = labelName;
                            openImageOptions();

                        }

                        @Override
                        public void onCloseClick(KeyPairBoolData data) {

                        }
                    });


                    llParentLayout.addView(fileUploadView);
                    viewsList.put(question.getColumnId(), fileUploadView);

                } else if (question.getDataType().equalsIgnoreCase("plainText")) {

                    QuestionnaireTextView textView = new QuestionnaireTextView(mContext, iServiceOptionListOptionChange);
                    textView.setServiceOptionGridQuestionData(question, getItemPrice(selectedPriceGridList, question));

                    llParentLayout.addView(textView);
                    viewsList.put(question.getColumnId(), textView);

                } else if (question.getDataType().equalsIgnoreCase("date")) {

                    QuestionnaireDateView dateView = new QuestionnaireDateView(mContext, iServiceOptionListOptionChange);
                    dateView.setServiceOptionGridQuestionData(question, getItemPrice(selectedPriceGridList, question));

                    llParentLayout.addView(dateView);
                    viewsList.put(question.getColumnId(), dateView);


                } else if (question.getDataType().equalsIgnoreCase("number")) {

                    QuestionnaireNumberView numberView = new QuestionnaireNumberView(mContext, iServiceOptionListOptionChange);
                    numberView.setServiceOptionGridQuestionData(question, getItemPrice(selectedPriceGridList, question));

                    llParentLayout.addView(numberView);
                    viewsList.put(question.getColumnId(), numberView);


                } else if (question.getDataType().equalsIgnoreCase("bool")) {

                    QuestionnaireBoolView boolView = new QuestionnaireBoolView(mContext, iServiceOptionListOptionChange);
                    boolView.setServiceOptionGridQuestionData(question, getItemPrice(selectedPriceGridList, question));

                    llParentLayout.addView(boolView);
                    viewsList.put(question.getColumnId(), boolView);


                } else if (question.getDataType().equalsIgnoreCase("list")) {
                    Map<String, Object> itemAndPrices;
                    ArrayList<String> values = (ArrayList) question.getListProperties().getValues();
                    ArrayList<QuestionnaireCheckbox> questionnaireCheckboxes = new ArrayList<>();
                    if (question.getListProperties().getBasePrice() != null) {
                        itemAndPrices = Config.jsonStringToMap(question.getListProperties().getBasePrice().replaceAll("\\s", ""));
                        for (String v : values) {
                            if (itemAndPrices.get(v.replaceAll("\\s", "")) != null) {  //create check box if  value v is present in itemPrices only
                                QuestionnaireCheckbox questionnaireCheckbox = new QuestionnaireCheckbox();
                                questionnaireCheckbox.setText(v);
                                questionnaireCheckbox.setPrice(Float.parseFloat(itemAndPrices.get(v.replaceAll("\\s", "")).toString()));
                                if (selectedItemName.equals(v)) {
                                    questionnaireCheckbox.setChecked(true);
                                    questionnaireCheckbox.setBase(true); // set baselist or not
                                } else {
                                    questionnaireCheckbox.setChecked(false);
                                    questionnaireCheckbox.setBase(true); // set baselist or not
                                }
                                questionnaireCheckboxes.add(questionnaireCheckbox);
                            }
                        }
                    } else {
                        if (selectedItemName != null) {
                            //JSONObject j = priceGridList.getJSONObject(selectedItemName);
                            if (selectedPriceGridList.has(question.getColumnId())) {

                                Map<String, Object> kj = new Gson().fromJson(
                                        String.valueOf(selectedPriceGridList.getJSONObject(question.getColumnId())), new TypeToken<HashMap<String, Object>>() {
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
                            } else {
                                for (String v : values) {
                                    QuestionnaireCheckbox questionnaireCheckbox = new QuestionnaireCheckbox();
                                    questionnaireCheckbox.setText(v);
                                    questionnaireCheckbox.setPrice(null);
                                    if (selectedItemName.equals(v)) {
                                        questionnaireCheckbox.setChecked(true);
                                        questionnaireCheckbox.setBase(true); // set baselist or not
                                    } else {
                                        questionnaireCheckbox.setChecked(false);
                                        questionnaireCheckbox.setBase(false); // set baselist or not
                                    }
                                    questionnaireCheckboxes.add(questionnaireCheckbox);
                                }
                            }
                        }
                           /* for (String v : values) {
                            QuestionnaireCheckbox questionnaireCheckbox = new QuestionnaireCheckbox();
                            questionnaireCheckbox.setText(v);
                            questionnaireCheckbox.setPrice(null);
                            if(selectedItemName.equals(v)){
                                questionnaireCheckbox.setChecked(true);
                                questionnaireCheckbox.setBase(true); // set baselist or not
                            } else {
                                questionnaireCheckbox.setChecked(false);
                                questionnaireCheckbox.setBase(false); // set baselist or not
                            }
                            questionnaireCheckboxes.add(questionnaireCheckbox);
                        }*/
                    }
                    QuestionnaireListView listView = new QuestionnaireListView(mContext, iServiceOptionListOptionChange);
                    listView.setServiceOptionGridQuestionData(question, questionnaireCheckboxes, question.getListProperties().getMaxAnswerable());
                    if (question.getOrder() == 1) {
                        listView.setVisibility(View.GONE); // for hide first base list
                    }
                    llParentLayout.addView(listView);
                    viewsList.put(question.getColumnId(), listView);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        calculatePrice();
    }

    private float getItemPrice(JSONObject selectedPriceGridListt, DataGridColumns questionn) {
        float itemPrice = 0;
        if (selectedPriceGridListt.has(questionn.getColumnId())) {
            Map<String, Object> kj = new Gson().fromJson(
                    String.valueOf(selectedPriceGridListt), new TypeToken<HashMap<String, Object>>() {
                    }.getType());
            itemPrice = Float.parseFloat(kj.get(questionn.getColumnId()).toString());
        } else {
            itemPrice = 0;
        }
        return itemPrice;
    }

    private void calculatePrice() {
        ArrayList<GridColumnAnswerLine> columnAnswersList = new ArrayList<>();
        float totalPrice = 0;
        int count = llParentLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = llParentLayout.getChildAt(i);
            GridColumnAnswerLine gridColumnAnswerLine = new GridColumnAnswerLine();
            if (view instanceof QuestionnaireTextView) {
                QuestionnaireTextView textView = (QuestionnaireTextView) view;

                gridColumnAnswerLine = textView.getGridTextAnswerLine();
                totalPrice = totalPrice + gridColumnAnswerLine.getPrice();

                columnAnswersList.add(textView.getGridTextAnswerLine());


            } else if (view instanceof QuestionnaireBoolView) {
                QuestionnaireBoolView boolView = (QuestionnaireBoolView) view;

                gridColumnAnswerLine = boolView.getGridBoolAnswerLine();
                totalPrice = totalPrice + gridColumnAnswerLine.getPrice();

                columnAnswersList.add(boolView.getGridBoolAnswerLine());

            } else if (view instanceof QuestionnaireDateView) {
                QuestionnaireDateView dateView = (QuestionnaireDateView) view;

                gridColumnAnswerLine = dateView.getGridDateAnswerLine();
                totalPrice = totalPrice + gridColumnAnswerLine.getPrice();

                columnAnswersList.add(dateView.getGridDateAnswerLine());

            } else if (view instanceof QuestionnaireNumberView) {
                QuestionnaireNumberView numberView = (QuestionnaireNumberView) view;

                gridColumnAnswerLine = numberView.getGridNumberAnswerLine();
                totalPrice = totalPrice + gridColumnAnswerLine.getPrice();

                columnAnswersList.add(numberView.getGridNumberAnswerLine());

            } else if (view instanceof QuestionnaireFileUploadView) {
                QuestionnaireFileUploadView fileUploadView = (QuestionnaireFileUploadView) view;

                gridColumnAnswerLine = fileUploadView.getGridFileUploadAnswerLine();
                totalPrice = totalPrice + gridColumnAnswerLine.getPrice();

                columnAnswersList.add(fileUploadView.getGridFileUploadAnswerLine());

            } else if (view instanceof QuestionnaireListView) {
                QuestionnaireListView listView = (QuestionnaireListView) view;

                gridColumnAnswerLine = listView.getGridListAnswerLine();
                totalPrice = totalPrice + gridColumnAnswerLine.getPrice();

                columnAnswersList.add(listView.getGridListAnswerLine());

            }
        }
        tv_totalprice.setText("Price ₹ " + Config.getAmountNoOrTwoDecimalPoints(totalPrice));
    }

    private void addDataToQuestionnaireGrid() {

        ArrayList<Boolean> validList = new ArrayList<>();

        cv_submit.setClickable(false);

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

        dataGridObj.setDataGridListColumn(columnAnswersList);

        if (validList.contains(false)) {

            cv_submit.setClickable(true);

        } else {

            gridView.updateDataGrid(dataGridObj, position);
            itemOptionsDialog.cancel();

        }

    }

    private void openImageOptions() {

        try {

            Dialog dialog = new Dialog(mContext);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.camera_options);
            dialog.getWindow().getAttributes().windowAnimations = R.style.slidingUpAndDown;
            DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
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

    private void openCamera() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {


                    ((AppCompatActivity) mContext).requestPermissions(new String[]{
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

    private void openGallery() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if ((ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ((AppCompatActivity) mContext).requestPermissions(new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, GALLERY);

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


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY) {
            if (data != null) {
                try {
                    if (data.getData() != null) {
                        Uri uri = data.getData();

                        String mimeType = this.mContext.getContentResolver().getType(uri);
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
                                photoFile = Config.createFile(mContext, extension, true);
                            } catch (IOException ex) {
                                Toast.makeText(mContext, "Error occurred while creating the file", Toast.LENGTH_SHORT).show();

                                // Log.d(TAG, "Error occurred while creating the file");
                            }

                            InputStream inputStream = mContext.getContentResolver().openInputStream(data.getData());
                            FileOutputStream fileOutputStream = new FileOutputStream(photoFile);
                            // Copying
                            Config.copyStream(inputStream, fileOutputStream);
                            fileOutputStream.close();
                            inputStream.close();
                        } catch (Exception e) {
                            Toast.makeText(mContext, "onActivityResult: " + e.toString(), Toast.LENGTH_SHORT).show();

                            //Log.d(TAG, "onActivityResult: " + e.toString());
                        }
                        String orgFilePath = photoFile.getAbsolutePath();

                        if (orgFilePath == null) {
                            orgFilePath = Config.getFilePathFromURI(mContext, uri, extension);
                        }

                        View fileUploadView = viewsList.get(qLabelName);
                        RecyclerView rvFiles = (RecyclerView) fileUploadView.findViewById(R.id.rv_files);
                        TextView tvSupportedTypes = fileUploadView.findViewById(R.id.tv_supportedTypes);
                        FilesAdapter filesAdapter = (FilesAdapter) rvFiles.getAdapter();

                        if (tvSupportedTypes.getText().toString().contains(extension)) {
                            fileObject.setImagePath(orgFilePath);
                            filesAdapter.updateFileObject(fileObject);
                        } else {

                            Toast.makeText(mContext, "File type not supported", Toast.LENGTH_SHORT).show();
                        }


                    } else if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            String mimeType = this.mContext.getContentResolver().getType(uri);
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
                                    photoFile = Config.createFile(mContext, extension, true);
                                } catch (IOException ex) {
                                    Toast.makeText(mContext, "Error occurred while creating the file", Toast.LENGTH_SHORT).show();

                                    // Log.d(TAG, "Error occurred while creating the file");
                                }

                                InputStream inputStream = mContext.getContentResolver().openInputStream(data.getData());
                                FileOutputStream fileOutputStream = new FileOutputStream(photoFile);
                                // Copying
                                Config.copyStream(inputStream, fileOutputStream);
                                fileOutputStream.close();
                                inputStream.close();
                            } catch (Exception e) {
                                Toast.makeText(mContext, "onActivityResult: " + e.toString(), Toast.LENGTH_SHORT).show();

                                //Log.d(TAG, "onActivityResult: " + e.toString());
                            }
                            String orgFilePath = photoFile.getAbsolutePath();

                            if (orgFilePath == null) {
                                orgFilePath = Config.getFilePathFromURI(mContext, uri, extension);
                            }

                            View fileUploadView = viewsList.get(qLabelName);
                            RecyclerView rvFiles = (RecyclerView) fileUploadView.findViewById(R.id.rv_files);
                            TextView tvSupportedTypes = fileUploadView.findViewById(R.id.tv_supportedTypes);
                            FilesAdapter filesAdapter = (FilesAdapter) rvFiles.getAdapter();

                            if (tvSupportedTypes.getText().toString().contains(extension)) {
                                fileObject.setImagePath(orgFilePath);
                                filesAdapter.updateFileObject(fileObject);
                            } else {
                                Toast.makeText(mContext, "File type not supported", Toast.LENGTH_SHORT).show();
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
                    photoFile = Config.createFile(mContext, "png", true);//////////
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
                if (path != null) {
                    mImageUri = Uri.parse(path);

                    View fileUploadView = viewsList.get(qLabelName);
                    RecyclerView rvFiles = (RecyclerView) fileUploadView.findViewById(R.id.rv_files);
                    FilesAdapter filesAdapter = (FilesAdapter) rvFiles.getAdapter();

                    fileObject.setImagePath(path);
                    filesAdapter.updateFileObject(fileObject);
                }
                /*try {
                    bytes.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }
        }
    }

    @Override
    public void updateTotalPrice() {
        calculatePrice();
    }

    @Override
    public void radioListItemSelected(String s, Float price) {
        this.selectedItemName = s;
        this.selectedItemPrice = price;
    }

    @Override
    public void savetoDataBase(CatalogItem itemDetails, String serviceOption, String serviceOptionAtachedImages) {

    }

    @Override
    public KeyPairBoolData openImageOptions(KeyPairBoolData fileObject, String qLabelName, HashMap<String, View> viewsList) {
        return null;
    }

    private void serviceOptionAddItemOptionsDialog(String selectedItemNme) {

        if (selectedItemNme != null && !selectedItemNme.trim().isEmpty()) {
            itemOptionsDialog = new Dialog(mContext);
            itemOptionsDialog.getWindow().getAttributes().windowAnimations = R.style.slidingUpAndDown;
            itemOptionsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            itemOptionsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            itemOptionsDialog.setContentView(R.layout.service_option_add_item_options_dialog);
            DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
            int width = (int) (metrics.widthPixels * 1);
            itemOptionsDialog.setCancelable(false);
            itemOptionsDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
            itemOptionsDialog.getWindow().setGravity(Gravity.BOTTOM);
            llParentLayout = itemOptionsDialog.findViewById(R.id.ll_mainLayout);
            ImageView iv_close = itemOptionsDialog.findViewById(R.id.iv_close);
            TextView tv_item_name = itemOptionsDialog.findViewById(R.id.tv_item_name);
            TextView tv_item_price = itemOptionsDialog.findViewById(R.id.tv_item_price);
            tv_totalprice = itemOptionsDialog.findViewById(R.id.tv_totalprice);
            cv_submit = itemOptionsDialog.findViewById(R.id.cv_submit);
            cv_cover = itemOptionsDialog.findViewById(R.id.cv_cover);
            tv_item_name.setText(selectedItemNme);
            if(!isEdit) {
                cv_submit.setVisibility(View.GONE);
                cv_cover.setVisibility(View.VISIBLE);
            } else {
                cv_submit.setVisibility(View.VISIBLE);
                cv_cover.setVisibility(View.GONE);
            }
            if (selectedItemPrice != null && !selectedItemPrice.equals("")) {
                tv_item_price.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(selectedItemPrice) + "");
                tv_item_price.setVisibility(View.VISIBLE);
            } else {
                tv_item_price.setVisibility(View.GONE);
            }
            itemOptionsDialog.show();
            if (mQuestion != null) {

                if (mAnswer == null) {

                    for (DataGridColumns question : mQuestion.getDataGridListProperties().getGridListColumns()) {
                        question.setAnswer(null);
                    }
                    createQuestionnaire(mQuestion);
                } else {

                    for (DataGridColumns question : mQuestion.getDataGridListProperties().getGridListColumns()) {

                        for (GridColumnAnswerLine answer : mAnswer.getDataGridListColumn()) {

                            if (question.getColumnId().equalsIgnoreCase(answer.getColumnId())) {

                                question.setAnswer(answer);
                            }
                        }
                    }
                    createQuestionnaire(mQuestion);
                }
            }
            iv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreference.getInstance(mContext).setValue(Constants.QUESTION, "");
                    SharedPreference.getInstance(mContext).setValue(Constants.ANSWER, "");
                    itemOptionsDialog.cancel();
                }
            });
            cv_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    addDataToQuestionnaireGrid();
                }
            });
            if(!isEdit) {
                cv_cover.setOnClickListener(new View.OnClickListener() {   //this empty click listner is important for in case of not editable service qnr
                    @Override
                    public void onClick(View view) {

                    }
                });
            }

        } else {
            DynamicToast.make(mContext, "Please select an item",
                    ContextCompat.getColor(mContext, R.color.white), ContextCompat.getColor(mContext, R.color.red), Toast.LENGTH_SHORT).show();

        }
    }
}