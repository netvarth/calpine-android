package com.jaldeeinc.jaldee.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jaldeeinc.jaldee.Interface.IDataGridListener;
import com.jaldeeinc.jaldee.Interface.IFilesInterface;
import com.jaldeeinc.jaldee.Interface.IServiceOption;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.CheckBoxAdapter;
import com.jaldeeinc.jaldee.adapter.FilesAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.KeyPairBoolData;
import com.jaldeeinc.jaldee.custom.ServiceOptionAddItemDialog;
import com.jaldeeinc.jaldee.custom.ServiceOptionGridView;
import com.jaldeeinc.jaldee.model.AnswerLine;
import com.jaldeeinc.jaldee.model.DataGrid;
import com.jaldeeinc.jaldee.model.GridColumnAnswerLine;
import com.jaldeeinc.jaldee.model.LabelPath;
import com.jaldeeinc.jaldee.model.QuestionnairInpt;
import com.jaldeeinc.jaldee.model.QuestionnaireCheckbox;
import com.jaldeeinc.jaldee.response.CatalogItem;
import com.jaldeeinc.jaldee.response.Questionnaire;
import com.jaldeeinc.jaldee.response.Questions;
import com.jaldeeinc.jaldee.response.SearchService;
import com.jaldeeinc.jaldee.response.ServiceInfo;
import com.jaldeeinc.jaldee.utils.SharedPreference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.omjoonkim.skeletonloadingview.SkeletonLoadingView;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceOptionActivity extends AppCompatActivity implements IFilesInterface, DatePickerDialog.OnDateSetListener, IServiceOption {
    LinearLayout llParentLayout, ll_total_price;
    CardView cvSubmit;
    CardView cvBack;
    TextView tv_total_price, tv_providerName, tv_serviceName, tv_description;
    ImageView iv_service_image;
    SkeletonLoadingView shimmer;

    private Context mContext;
    private String providerName;
    private int providerId;
    private int serviceId;
    private IFilesInterface iFilesInterface;
    private KeyPairBoolData fileObject = new KeyPairBoolData();
    private IServiceOption iServiceOptionListOptionChange;

    ArrayList<LabelPath> labelPaths = new ArrayList<>();

    private Questionnaire questionnaire = new Questionnaire();
    private ServiceInfo serviceInfo = new ServiceInfo();
    private static SearchService checkInInfo = new SearchService();

    ArrayList<String> list = new ArrayList<>();
    private HashMap<String, View> viewsList = new HashMap<>();
    private String qLabelName = "";
    private int GALLERY_FOR_ONE = 1, CAMERA_FOR_ONE = 2;
    private int GALLERY = 3, CAMERA = 4;
    private Uri mImageUri;
    String singleFilePath = "";
    public String path;
    private boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_option);
        mContext = ServiceOptionActivity.this;
        iFilesInterface = this;
        iServiceOptionListOptionChange = this;

        isEdit = true;///////////////////////////

        requestMultiplePermissions();

        Intent intent = getIntent();
        providerName = intent.getStringExtra("providerName");
        serviceId = intent.getIntExtra("serviceId", 0);
        providerId = intent.getIntExtra("providerId", 0);
        serviceInfo = (ServiceInfo) intent.getSerializableExtra("serviceInfo");
        checkInInfo = (SearchService) intent.getSerializableExtra("checkInInfo");

        if (serviceInfo != null) {
            serviceId = serviceInfo.getServiceId();
        }
        initializations();
        apiGetServiceOptionQnr();
        if (providerName != null) {
            tv_providerName.setText(providerName);
        }
        shimmer.setVisibility(View.VISIBLE);
        Glide.with(mContext)
                .load(R.drawable.service_avatar)
                .apply(new RequestOptions().error(R.drawable.icon_noimage).fitCenter())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        //on load failed
                        shimmer.setVisibility(View.GONE);
                        iv_service_image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_noimage));
                        iv_service_image.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        //on load success
                        shimmer.setVisibility(View.GONE);
                        iv_service_image.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .into(iv_service_image);
        if (serviceInfo.getDescription() != null && !serviceInfo.getDescription().trim().isEmpty()) {
            tv_description.setText(serviceInfo.getDescription());
            tv_description.setVisibility(View.VISIBLE);
        } else {
            tv_description.setVisibility(View.GONE);
        }
        String name = serviceInfo.getServiceName();
        tv_serviceName.setText(name);

        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
        cvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    submitServiceOptionsQNR();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void apiGetServiceOptionQnr() {
        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        //final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        //mDialog.show();
        Call<Questionnaire> call = apiService.getServiceOptionQnr(serviceId, 0, providerId);
        call.enqueue(new Callback<Questionnaire>() {
            @Override
            public void onResponse(Call<Questionnaire> call, Response<Questionnaire> response) {
                Config.logV("URL------getQNR response---------" + response.raw().request().url().toString().trim());
                Config.logV("Response--code-------------------------" + response.code());
                if (response.code() == 200) {
                    questionnaire = response.body();
                    try {
//                        Map<String, Object> retMap = new Gson().fromJson(
//                                questionnaire.getQuestionsList().get(0).getGetQuestion().getPriceGridList(), new TypeToken<HashMap<String, Object>>() {
//                                }.getType()
//                        );
                        if (questionnaire.getQuestionsList() != null) {
                            createQuestionnaire(questionnaire.getQuestionsList());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Questionnaire> call, Throwable t) {

            }
        });
    }


    private void createQuestionnaire(ArrayList<Questions> questionsList) throws JSONException {
        for (Questions question : questionsList) {
            if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("dataGridList")) {
                //QuestionnaireGridView gridView = new QuestionnaireGridView(this);
                ServiceOptionGridView gridView = new ServiceOptionGridView(this, iServiceOptionListOptionChange, true);
                gridView.setQuestionData(question.getGetQuestion());
                gridView.getLlAdd().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        SharedPreference.getInstance(ServiceOptionActivity.this).setValue(Constants.QUESTION, "");
                        SharedPreference.getInstance(mContext).setValue(Constants.ANSWER, "");

                        SharedPreference.getInstance(mContext).setValue(Constants.QUESTION, new Gson().toJson(question.getGetQuestion()));

                        ServiceOptionAddItemDialog serviceOptionAddItemDialog = ServiceOptionAddItemDialog.newInstance("");
                        serviceOptionAddItemDialog.setGridView(gridView);
                        final FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.container, serviceOptionAddItemDialog).addToBackStack("DataGrid")
                                .commit();
//                        ServiceOptionAddItemDialog serviceOptionAddItemDialog = new ServiceOptionAddItemDialog(mContext);
//                        serviceOptionAddItemDialog.setGridView(gridView);
//                        serviceOptionAddItemDialog.ServiceOptnAddItemDialog();
                    }
                });

                gridView.setiDataGridListener(new IDataGridListener() {
                    @Override
                    public void onEditClick(DataGrid gridObj, int position) {

                        SharedPreference.getInstance(ServiceOptionActivity.this).setValue(Constants.QUESTION, "");
                        SharedPreference.getInstance(mContext).setValue(Constants.ANSWER, "");

                        SharedPreference.getInstance(mContext).setValue(Constants.QUESTION, new Gson().toJson(question.getGetQuestion()));
                        SharedPreference.getInstance(mContext).setValue(Constants.ANSWER, new Gson().toJson(gridObj));

                        ServiceOptionAddItemDialog serviceOptionAddItemDialog = ServiceOptionAddItemDialog.newInstance("", "", position, true);
                        serviceOptionAddItemDialog.setGridView(gridView);
                        final FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.container, serviceOptionAddItemDialog).addToBackStack("DataGrid")
                                .commit();
//                        ServiceOptionAddItemDialog serviceOptionAddItemDialog = new ServiceOptionAddItemDialog(mContext, position);
//                        serviceOptionAddItemDialog.setGridView(gridView);
//                        serviceOptionAddItemDialog.ServiceOptnAddItemDialog();
                    }
                });

                llParentLayout.addView(gridView);
                viewsList.put(question.getGetQuestion().getLabelName(), gridView);

            }
        }
        calculatePrice();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

    }

    @Override
    public void onFileUploadClick(KeyPairBoolData data, String labelName) {

    }

    @Override
    public void onCloseClick(KeyPairBoolData data) {

    }

    @Override
    public void updateTotalPrice() {
        calculatePrice();
    }

    @Override
    public void radioListItemSelected(String s, Float price) {

    }

    @Override
    public void savetoDataBase(CatalogItem itemDetails, String serviceOption, String serviceOptionAtachedImages) {

    }

    @Override
    public KeyPairBoolData openImageOptions(KeyPairBoolData fileObject, String qLabelName, HashMap<String, View> viewsList) {
        return null;
    }


    private void initializations() {
        llParentLayout = findViewById(R.id.ll_mainLayout);
        cvSubmit = findViewById(R.id.cv_submit);
        cvBack = findViewById(R.id.cv_back);
        tv_total_price = findViewById(R.id.tv_total_price);
        tv_providerName = findViewById(R.id.tv_providerName);
        tv_serviceName = findViewById(R.id.tv_serviceName);
        tv_description = findViewById(R.id.tv_description);
        iv_service_image = findViewById(R.id.iv_service_image);
        shimmer = findViewById(R.id.shimmer);
        ll_total_price = findViewById(R.id.ll_total_price);

        cvSubmit.setEnabled(false);
        cvSubmit.setAlpha(.3f);
    }

    private void requestMultiplePermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
//                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            //openSettingsDialog();fc
                            Toast.makeText(getApplicationContext(), "You Denied the Permission", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void submitServiceOptionsQNR() throws JSONException {

        QuestionnairInpt input = new QuestionnairInpt();
        input.setQuestionnaireId(questionnaire.getId());
        ArrayList<AnswerLine> answerLines = new ArrayList<>();

        //if (validForm(questionnaire.getQuestionsList())) {

        labelPaths = new ArrayList<>();

        for (Questions question : questionnaire.getQuestionsList()) {

            if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("fileUpload")) {

                if (question.getGetQuestion().getFileProperties().getAllowedDocuments().size() == 1) {

                    View singleFileUploadView = viewsList.get(question.getGetQuestion().getLabelName());
                    ImageView ivSingleFile = (ImageView) singleFileUploadView.findViewById(R.id.iv_file);
                    TextView tvPath = singleFileUploadView.findViewById(R.id.tv_path);
                    TextView tvSupportedTypes = singleFileUploadView.findViewById(R.id.tv_supportedTypes);


                    if (!tvPath.getText().toString().trim().equalsIgnoreCase("")) {
                        path = tvPath.getText().toString();
                    }

                    if (path != null && !path.trim().equalsIgnoreCase("")) {

                        if (!(path.contains("http://") || path.contains("https://"))) {

                            AnswerLine obj = new AnswerLine();
                            obj.setLabelName(question.getGetQuestion().getLabelName());
                            JsonObject answer = new JsonObject();
                            JsonArray uploadList = new JsonArray();
                            JsonObject fileInfo = new JsonObject();
                            String filename = null;
                            String mimeType = Config.getMimeType(path);

                            //if (mimeType != null && (mimeType.toLowerCase().contains("audio") || mimeType.toLowerCase().contains("video") || mimeType.contains("image/jpeg"))) {
                            if (mimeType != null) {

                                filename = path.substring(path.lastIndexOf("/") + 1);

                                fileInfo.addProperty("mimeType", mimeType);
                                fileInfo.addProperty("url", filename);

                            } else {
                                    /*if (!from.equalsIgnoreCase(Constants.ORDERS)) {
                                        fileInfo.addProperty("index", labelPaths.size());
                                    }*/
                                filename = path.substring(path.lastIndexOf("/") + 1);

                                fileInfo.addProperty("mimeType", mimeType);
                                fileInfo.addProperty("url", filename);
                            }
                            File file = new File(path);
                            int file_size = Integer.parseInt(String.valueOf(file.length()));
                            fileInfo.addProperty("caption", question.getGetQuestion().getFileProperties().getAllowedDocuments().get(0));
                            fileInfo.addProperty("action", isEdit ? "update" : "add");
                            fileInfo.addProperty("size", file_size);
                            uploadList.add(fileInfo);
                            answer.add("fileUpload", uploadList);
                            obj.setAnswer(answer);
                            answerLines.add(obj);

                            LabelPath lPath = new LabelPath(labelPaths.size(), question.getGetQuestion().getLabelName(), path, filename, mimeType);
                            labelPaths.add(lPath);
                        }
                    }

                } else {

                    AnswerLine obj = new AnswerLine();
                    obj.setLabelName(question.getGetQuestion().getLabelName());
                    JsonObject answer = new JsonObject();
                    JsonArray uploadList = new JsonArray();

                    View fileUploadView = viewsList.get(question.getGetQuestion().getLabelName());
                    RecyclerView rvFiles = (RecyclerView) fileUploadView.findViewById(R.id.rv_files);
                    TextView tvSupportedTypes = fileUploadView.findViewById(R.id.tv_supportedTypes);
                    FilesAdapter filesAdapter = (FilesAdapter) rvFiles.getAdapter();

                    List<KeyPairBoolData> files = filesAdapter.getFiles();

                    for (int i = 0; i < files.size(); i++) {

                        if (files.get(i).getImagePath() != null && !files.get(i).getImagePath().trim().equalsIgnoreCase("")) {

                            if (!(files.get(i).getImagePath().contains("http://") || files.get(i).getImagePath().contains("https://"))) {

                                JsonObject fileInfo = new JsonObject();

                                String path = files.get(i).getImagePath();
                                String mimeType = Config.getMimeType(path);

                                String filename = path.substring(path.lastIndexOf("/") + 1);

                                //if (mimeType != null && (mimeType.toLowerCase().contains("audio") || mimeType.toLowerCase().contains("video") || mimeType.contains("image/jpeg"))) {
                                if (mimeType != null) {
                                    fileInfo.addProperty("mimeType", mimeType);
                                    fileInfo.addProperty("url", filename);

                                } else {

                                    fileInfo.addProperty("index", labelPaths.size());
                                }
                                fileInfo.addProperty("caption", files.get(i).getName());
                                fileInfo.addProperty("action", isEdit ? "update" : "add");
                                uploadList.add(fileInfo);

                                answer.add("fileUpload", uploadList);
                                obj.setAnswer(answer);
                                answerLines.add(obj);

                                LabelPath lPath = new LabelPath(labelPaths.size(), question.getGetQuestion().getLabelName(), files.get(i).getImagePath(), filename, mimeType);
                                labelPaths.add(lPath);
                            }
                        }
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
                JsonObject answer = new JsonObject();
                JsonArray list = new JsonArray();
                for (QuestionnaireCheckbox item : selectedCheckboxes) {

                    list.add(item.getText());
                }
                answer.add("list", list);

                obj.setAnswer(answer);
                answerLines.add(obj);

            } else if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("bool")) {

                View boolFieldView = viewsList.get(question.getGetQuestion().getLabelName());
                RadioButton radioButtonYes = (RadioButton) boolFieldView.findViewById(R.id.rb_yes);

                AnswerLine obj = new AnswerLine();
                obj.setLabelName(question.getGetQuestion().getLabelName());

                JsonObject answer = new JsonObject();
                answer.addProperty("bool", radioButtonYes.isChecked());

                obj.setAnswer(answer);
                answerLines.add(obj);

            } else if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("plainText")) {

                View textFieldView = viewsList.get(question.getGetQuestion().getLabelName());
                EditText etTextField = (EditText) textFieldView.findViewById(R.id.et_textBox);

                AnswerLine obj = new AnswerLine();
                obj.setLabelName(question.getGetQuestion().getLabelName());

                JsonObject answer = new JsonObject();
                answer.addProperty("plainText", etTextField.getText().toString());

                obj.setAnswer(answer);
                answerLines.add(obj);

            } else if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("number")) {

                View numberFieldView = viewsList.get(question.getGetQuestion().getLabelName());
                EditText etTextField = (EditText) numberFieldView.findViewById(R.id.et_textBox);

                AnswerLine obj = new AnswerLine();
                obj.setLabelName(question.getGetQuestion().getLabelName());

                JsonObject answer = new JsonObject();
                answer.addProperty("number", etTextField.getText().toString());

                obj.setAnswer(answer);
                answerLines.add(obj);

            } else if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("date")) {

                View numberFieldView = viewsList.get(question.getGetQuestion().getLabelName());
                CustomTextViewSemiBold tvDate = (CustomTextViewSemiBold) numberFieldView.findViewById(R.id.tv_date);

                AnswerLine obj = new AnswerLine();
                obj.setLabelName(question.getGetQuestion().getLabelName());

                JsonObject answer = new JsonObject();
                answer.addProperty("date", tvDate.getText().toString());

                obj.setAnswer(answer);
                answerLines.add(obj);

            } else if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("dataGridList")) {

                ServiceOptionGridView gridFieldView = (ServiceOptionGridView) viewsList.get(question.getGetQuestion().getLabelName());

                ArrayList<DataGrid> dataGridList = new ArrayList<>();

                if (gridFieldView != null) {

                    dataGridList = gridFieldView.getGridDataList();
                }
                if (dataGridList != null && dataGridList.size() > 0) {

                    AnswerLine obj = new AnswerLine();
                    obj.setLabelName(question.getGetQuestion().getLabelName());

                    JsonObject answer = new JsonObject();
                    Gson gson = new Gson();
                    JsonElement element = gson.toJsonTree(dataGridList);
                    answer.add("dataGridList", element);

                    obj.setAnswer(answer);
                    answerLines.add(obj);

                    for (DataGrid d : dataGridList) {

                        //ArrayList<GridColumnAnswerLine> gridColumnAnswerLines = d.getDataGridListColumn();

                        for (GridColumnAnswerLine a : d.getDataGridListColumn()) {

                            JsonObject column = a.getColumn();

                            if (column.has("fileUpload")) {

                                JsonArray fileUploadList = column.getAsJsonArray("fileUpload");

                                for (JsonElement e : fileUploadList) {

                                    JsonObject fileObj = e.getAsJsonObject();
                                    String name = fileObj.get("caption").getAsString();
                                    String fileName = null;
                                    if (fileObj.get("url") != null) {
                                        fileName = fileObj.get("url").getAsString();
                                    }
                                    String filePath = "";
                                    if (fileObj.get("path") != null && !fileObj.get("path").getAsString().trim().equalsIgnoreCase("")) {
                                        filePath = fileObj.get("path").getAsString();
                                    }
                                    String mimeType = Config.getMimeType(filePath);

                                    LabelPath lPath = new LabelPath(labelPaths.size(), question.getGetQuestion().getLabelName(), filePath, fileName, mimeType);
                                    labelPaths.add(lPath);
                                }
                            }
                        }
                    }
                }
            }
        }

        input.setAnswerLines(answerLines);

        SharedPreference.getInstance(mContext).setValue(Constants.SERVICEOPTIONQNR, new Gson().toJson(input));
        SharedPreference.getInstance(mContext).setValue(Constants.SERVICEOPTIONQIMAGES, new Gson().toJson(labelPaths));

        String inputString = SharedPreference.getInstance(mContext).getStringValue(Constants.SERVICEOPTIONQNR, "");
        if (serviceInfo.getType() != null) {
            Intent intent = getIntent();
            if (tv_total_price != null && !tv_total_price.getText().toString().trim().isEmpty()) {
                String prc = tv_total_price.getText().toString();
                prc = prc.replace("₹", "");
                prc = prc.trim();
                intent.putExtra("sQnrPrice", Float.parseFloat(prc));
            }
            if (serviceInfo.getType().equalsIgnoreCase(Constants.CHECKIN)) {
                intent.setClass(this, CheckInActivity.class);
            } else if (serviceInfo.getType().equalsIgnoreCase(Constants.APPOINTMENT)) {
                intent.setClass(this, AppointmentActivity.class);
            }
            startActivity(intent);
        }
    }

    private void calculatePrice() {

        float totalPrice = 0;
        boolean isContainAns = false;
        for (Questions question : questionnaire.getQuestionsList()) {

            if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("dataGridList")) {

                ServiceOptionGridView gridFieldView = (ServiceOptionGridView) viewsList.get(question.getGetQuestion().getLabelName());

                ArrayList<DataGrid> dataGridList = new ArrayList<>();

                if (gridFieldView != null) {

                    dataGridList = gridFieldView.getGridDataList();
                }
                if (dataGridList != null && dataGridList.size() > 0) {

                    for (DataGrid d : dataGridList) {

                        for (GridColumnAnswerLine a : d.getDataGridListColumn()) {
                            totalPrice = totalPrice + a.getPrice();
                            isContainAns = true;
                        }
                    }
                }
            }
        }
        if (totalPrice > 0) {
            String price = Config.getAmountNoOrTwoDecimalPoints(totalPrice);
            ll_total_price.setVisibility(View.VISIBLE);
            tv_total_price.setText("₹ " + price);
        } else {
            ll_total_price.setVisibility(View.INVISIBLE);
        }
        if (isContainAns) {      // for disable and fade submit button
            cvSubmit.setEnabled(true);
            cvSubmit.setAlpha(1f);
        } else {
            cvSubmit.setEnabled(false);
            cvSubmit.setAlpha(.3f);
        }
    }


}