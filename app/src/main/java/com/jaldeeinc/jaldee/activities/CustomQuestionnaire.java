
package com.jaldeeinc.jaldee.activities;

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
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jaldeeinc.jaldee.BuildConfig;
import com.jaldeeinc.jaldee.Fragment.DataGridFragment;
import com.jaldeeinc.jaldee.Interface.IDataGridListener;
import com.jaldeeinc.jaldee.Interface.IFilesInterface;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.CheckBoxAdapter;
import com.jaldeeinc.jaldee.adapter.FilesAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.KeyPairBoolData;
import com.jaldeeinc.jaldee.custom.MultiSpinnerListener;
import com.jaldeeinc.jaldee.custom.MultiSpinnerSearch;
import com.jaldeeinc.jaldee.custom.QuestionnaireGridView;
import com.jaldeeinc.jaldee.model.AnswerLine;
import com.jaldeeinc.jaldee.model.BookingModel;
import com.jaldeeinc.jaldee.model.DataGrid;
import com.jaldeeinc.jaldee.model.GridColumnAnswerLine;
import com.jaldeeinc.jaldee.model.LabelPath;
import com.jaldeeinc.jaldee.model.QuestionnairInpt;
import com.jaldeeinc.jaldee.model.QuestionnaireBoolean;
import com.jaldeeinc.jaldee.model.QuestionnaireCheckbox;
import com.jaldeeinc.jaldee.model.QuestionnaireDateField;
import com.jaldeeinc.jaldee.model.QuestionnaireFileUploadModel;
import com.jaldeeinc.jaldee.model.QuestionnaireInput;
import com.jaldeeinc.jaldee.model.QuestionnaireListModel;
import com.jaldeeinc.jaldee.model.QuestionnaireNumberModel;
import com.jaldeeinc.jaldee.model.QuestionnaireTextField;
import com.jaldeeinc.jaldee.model.QuestnnaireSingleFile;
import com.jaldeeinc.jaldee.model.ShoppingListModel;
import com.jaldeeinc.jaldee.response.GetQuestion;
import com.jaldeeinc.jaldee.response.ListProperties;
import com.jaldeeinc.jaldee.response.Questionnaire;
import com.jaldeeinc.jaldee.response.QuestionnaireResponse;
import com.jaldeeinc.jaldee.response.QuestionnaireUrls;
import com.jaldeeinc.jaldee.response.Questions;
import com.jaldeeinc.jaldee.response.SubmitQuestionnaire;
import com.jaldeeinc.jaldee.utils.SharedPreference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
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

    @BindView(R.id.tv_buttonName)
    TextView tvButtonName;

    @BindView(R.id.iv_next)
    ImageView ivNext;

    @BindView(R.id.cv_back)
    CardView cvBack;

    private static Context mContext;
    private IFilesInterface iFilesInterface;
    private KeyPairBoolData fileObject = new KeyPairBoolData();
    private static final String IMAGE_DIRECTORY = "/Jaldee" + "";
    private int GALLERY_FOR_ONE = 1, CAMERA_FOR_ONE = 2;
    private int GALLERY = 3, CAMERA = 4;
    private static final int SELECT_VIDEO = 3;

    private Uri mImageUri;
    File f;
    File file;
    String singleFilePath = "";

    ArrayList<LabelPath> labelPaths = new ArrayList<>();
    ArrayList<String> bookingImagesList = new ArrayList<>();
    ArrayList<Questions> updatedQuestionsList = new ArrayList<>();


    ArrayList<String> list = new ArrayList<>();
    private Questionnaire questionnaire = new Questionnaire();
    private String userNotes;

    private HashMap<String, View> viewsList = new HashMap<>();
    private String qLabelName = "";
    private int accountId;
    private int serviceId;
    public BookingModel bookingModel;
    ArrayList<BookingModel> bookingModels = new ArrayList<>();
    QuestionnaireInput qInput = new QuestionnaireInput();
    private QuestionnaireResponse questionnaireResponse = new QuestionnaireResponse();
    public String from, uid, bookingStatus = "";
    private boolean isEdit = false;
    private int uniqueId;
    ArrayList<ShoppingListModel> itemList = new ArrayList<>();
    public ArrayList<LabelPath> imagePathList = new ArrayList<>();
    public String path;
    public boolean isAftrQNR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_questionnaire);
        ButterKnife.bind(this);
        mContext = CustomQuestionnaire.this;
        iFilesInterface = this;

        requestMultiplePermissions();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Intent intent = getIntent();
        try {
            bookingModel = (BookingModel) intent.getSerializableExtra("data");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bookingModel == null) {// for multiple appt slot booking
            try {
                bookingModels = (ArrayList<BookingModel>) intent.getSerializableExtra("datas");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (bookingModels != null && !bookingModels.isEmpty() && bookingModels.size() > 1) {
                bookingModel = bookingModels.get(bookingModels.size() - 1);
            }
        }
        itemList = (ArrayList<ShoppingListModel>) intent.getSerializableExtra("IMAGESLIST");

        uniqueId = intent.getIntExtra("uniqueId", 0);
        serviceId = intent.getIntExtra("serviceId", 0);
        accountId = intent.getIntExtra("accountId", 0);
        uid = intent.getStringExtra("uid");
        isEdit = intent.getBooleanExtra("isEdit", false);
        from = intent.getStringExtra("from");
        isAftrQNR = intent.getBooleanExtra("isFromAftrQNR", false);
        bookingStatus = intent.getStringExtra("status");
        Gson afterQnrGson = new Gson();
        Questionnaire afterQnr = afterQnrGson.fromJson(intent.getStringExtra("afterQnr"), Questionnaire.class);

        if (bookingStatus == null) {
            bookingStatus = "";
        }
        questionnaireResponse = (QuestionnaireResponse) intent.getSerializableExtra("editQuestionnaire");
        if (bookingModel != null) {
            if (bookingModel.getServiceInfo() != null) {
                serviceId = bookingModel.getServiceInfo().getServiceId();
            } else if (bookingModel.getCheckInInfo() != null) {
                serviceId = bookingModel.getCheckInInfo().getId();
            } else if (bookingModel.getDonationServiceInfo() != null) {
                serviceId = bookingModel.getDonationServiceInfo().getId();
            } else if (bookingModel.getCatalogInfo() != null) {
                serviceId = bookingModel.getCatalogInfo().getCatLogId();
            }
            accountId = bookingModel.getAccountId();
            questionnaire = bookingModel.getQuestionnaire();
            bookingImagesList = bookingModel.getImagesList();
            userNotes = bookingModel.getMessage();
        }

        if (isEdit) {

            tvButtonName.setText("Update");
            ivNext.setVisibility(View.GONE);

            if (!bookingStatus.trim().equalsIgnoreCase("") && !(bookingStatus.equalsIgnoreCase("Confirmed") || bookingStatus.equalsIgnoreCase("Arrived") || bookingStatus.equalsIgnoreCase("checkedIn") || bookingStatus.equalsIgnoreCase("arrived"))) {

                showAlert();
            }

        } else {
            SharedPreference.getInstance(mContext).setValue(Constants.QUESTIONNAIRE, "");
            SharedPreference.getInstance(mContext).setValue(Constants.QIMAGES, "");
            tvButtonName.setText("Save");
            ivNext.setVisibility(View.GONE);
        }

        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        String inputString = SharedPreference.getInstance(mContext).getStringValue(Constants.QUESTIONNAIRE, "");


        if (inputString != null && !inputString.trim().equalsIgnoreCase("")) {

            try {

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                qInput = gson.fromJson(inputString, QuestionnaireInput.class);

            } catch (JsonSyntaxException e) {
                qInput = new QuestionnaireInput();
                e.printStackTrace();
            }

        } else {

            qInput = new QuestionnaireInput();
        }

        String imagesString = SharedPreference.getInstance(mContext).getStringValue(Constants.QIMAGES, "");

        if (imagesString != null && !imagesString.trim().equalsIgnoreCase("")) {

            Type labelPathType = new TypeToken<ArrayList<LabelPath>>() {
            }.getType();

            try {

                labelPaths = new Gson().fromJson(imagesString, labelPathType);

            } catch (Exception e) {
                labelPaths = new ArrayList<>();
                e.printStackTrace();
            }

        } else {

            labelPaths = new ArrayList<>();
        }

        // Api to get questionnaire
        if (isEdit) {

            if (qInput != null && qInput.getQuestions() != null) {

                try {
                    UpdateQuestionnaire(qInput.getQuestions());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } else {
            if (isAftrQNR) {
                //               try {
                questionnaire = afterQnr;
                createQuestionnaire(questionnaire.getQuestionsList());
                //        } catch (JSONException e) {
                //    e.printStackTrace();
                //  }

            } else {
                try {
                    getQuestionnaire();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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

    private void showAlert() {

        Config.showAlertBuilder(mContext, "Service has started", "The given details cannot be edited as the service has started.");
    }


    private void getQuestionnaire() {

        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<Questionnaire> call = null;
        if (from.equalsIgnoreCase(Constants.CHECKIN) || from.equalsIgnoreCase(Constants.TOKEN) || from.equalsIgnoreCase(Constants.APPOINTMENT) || from.equalsIgnoreCase(Constants.BOOKING_APPOINTMENT) || from.equalsIgnoreCase(Constants.BOOKING_CHECKIN)) {
            call = apiService.getQuestions(serviceId, 0, accountId);
        } else if (from.equalsIgnoreCase(Constants.ORDERS)) {
            call = apiService.getOrdersQuestions(serviceId, accountId);
        } else if (from.equalsIgnoreCase(Constants.DONATION)) {
            call = apiService.getDonationQuestions(serviceId, accountId);
        }
        call.enqueue(new Callback<Questionnaire>() {
            @Override
            public void onResponse(Call<Questionnaire> call, Response<Questionnaire> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);
                    //Config.logV("URL------getQNR response---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        questionnaire = response.body();


                        if (questionnaire != null) {

                            if (questionnaire.getQuestionsList() != null) {

                                createQuestionnaire(questionnaire.getQuestionsList());
                            } else {

                            }
                        } else {

                        }
                        int j = 0 + 9;
                        j = j + 8;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Questionnaire> call, Throwable t) {
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
            }
        });
    }


    private void submitQuestionnaire() throws JSONException {

        QuestionnairInpt input = new QuestionnairInpt();
        input.setQuestionnaireId(questionnaire.getId());
        ArrayList<AnswerLine> answerLines = new ArrayList<>();

        if (validForm(questionnaire.getQuestionsList())) {

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

                                if (mimeType != null && (mimeType.toLowerCase().contains("audio") || mimeType.toLowerCase().contains("video"))) {

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

                                    if (mimeType != null && (mimeType.toLowerCase().contains("audio") || mimeType.toLowerCase().contains("video"))) {


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
                    TextView tvDate = numberFieldView.findViewById(R.id.tv_date);

                    AnswerLine obj = new AnswerLine();
                    obj.setLabelName(question.getGetQuestion().getLabelName());

                    JsonObject answer = new JsonObject();
                    answer.addProperty("date", tvDate.getText().toString());

                    obj.setAnswer(answer);
                    answerLines.add(obj);

                } else if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("dataGrid")) {

                    QuestionnaireGridView gridFieldView = (QuestionnaireGridView) viewsList.get(question.getGetQuestion().getLabelName());

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
                        answer.add("dataGrid", element);

                        obj.setAnswer(answer);
                        answerLines.add(obj);

                        for (DataGrid d : dataGridList) {

                            for (GridColumnAnswerLine a : d.getDataGridColumn()) {

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

            SharedPreference.getInstance(mContext).setValue(Constants.QUESTIONNAIRE, new Gson().toJson(input));
            SharedPreference.getInstance(mContext).setValue(Constants.QIMAGES, new Gson().toJson(labelPaths));

            /*if (from.equalsIgnoreCase(Constants.CHECKIN)) {
                Intent intent = new Intent(CustomQuestionnaire.this, CheckInReconfirmation.class);
                intent.putExtra("data", bookingModel);
                startActivity(intent);
            } else if (from.equalsIgnoreCase(Constants.APPOINTMENT)) {
                Intent intent = new Intent(CustomQuestionnaire.this, ReconfirmationActivity.class);
                intent.putExtra("data", bookingModel);
                startActivity(intent);
            } else if (from.equalsIgnoreCase(Constants.DONATION)) {
                Intent intent = new Intent(CustomQuestionnaire.this, DonationReconfirmation.class);
                intent.putExtra("data", bookingModel);
                startActivity(intent);
            } */
            String inputString = SharedPreference.getInstance(mContext).getStringValue(Constants.QUESTIONNAIRE, "");
            if (isAftrQNR) {
                ApiSubmitQuestionnnaire(input, labelPaths, uid);
            } else if (from.equalsIgnoreCase(Constants.CHECKIN) || from.equalsIgnoreCase(Constants.APPOINTMENT) || from.equalsIgnoreCase(Constants.DONATION)) {
                if (bookingModels != null && !bookingModels.isEmpty() && bookingModels.size() > 1) { // for multiple appointment slot booking
                    Intent intent = new Intent(CustomQuestionnaire.this, ReconfirmationMultipleApptActivity.class);
                    intent.putExtra("datas", bookingModels);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(CustomQuestionnaire.this, ReconfirmationActivity.class);
                    intent.putExtra("data", bookingModel);
                    startActivity(intent);
                }
            } else if (from.equalsIgnoreCase(Constants.ORDERS)) {
                Intent intent;
                if (bookingModel.getCatalogInfo() != null && bookingModel.getCatalogInfo().getOrderType().equalsIgnoreCase(Constants.SHOPPINGCART)) {
                    intent = new Intent(CustomQuestionnaire.this, CheckoutItemsActivity.class);
                } else {
                    intent = new Intent(CustomQuestionnaire.this, CheckoutListActivity.class);
                    intent.putExtra("IMAGESLIST", itemList);
                    intent.putExtra("uniqueId", uniqueId);
                    intent.putExtra("accountId", accountId);
                    intent.putExtra("serviceId", serviceId);
                }
                //intent.putExtra("accountId", providerAccountId);
                //intent.putExtra("providerInfo", mBusinessDataList);
                startActivity(intent);
            } else if (from.equalsIgnoreCase(Constants.BOOKING_APPOINTMENT)) {

                if (bookingStatus.trim().equalsIgnoreCase("Confirmed") || bookingStatus.trim().equalsIgnoreCase("Arrived")) {
                    ApiUpdateApptQuestionnaire(input, labelPaths);
                } else {

                    Toast.makeText(mContext, "The given details cannot be edited as the service has started.", Toast.LENGTH_SHORT).show();
                }

            } else if (from.equalsIgnoreCase(Constants.BOOKING_CHECKIN)) {

                if (bookingStatus.trim().equalsIgnoreCase("checkedIn") || bookingStatus.trim().equalsIgnoreCase("arrived")) {

                    ApiUpdateWaitListQuestionnaire(input, labelPaths);
                } else {

                    Toast.makeText(mContext, "The given details cannot be edited as the service has started.", Toast.LENGTH_SHORT).show();

                }

            }

        }

    }

    private void ApiSubmitQuestionnnaire(QuestionnairInpt input, ArrayList<LabelPath> labelPaths, String uid) {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        MediaType type = MediaType.parse("*/*");
        MultipartBody.Builder mBuilder = new MultipartBody.Builder();
        mBuilder.setType(MultipartBody.FORM);
        imagePathList = labelPaths;
        for (int i = 0; i < imagePathList.size(); i++) {

            file = new File(imagePathList.get(i).getPath());

            mBuilder.addFormDataPart("files", file.getName(), RequestBody.create(type, file));
        }

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(input);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json);
        mBuilder.addFormDataPart("question", "blob", body);
        RequestBody requestBody = mBuilder.build();

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<SubmitQuestionnaire> call = null;
        if (from.equalsIgnoreCase(Constants.BOOKING_APPOINTMENT)) {
            call = apiService.submitAppointmentQuestionnaire(uid, requestBody, accountId);
        } else if (from.equalsIgnoreCase(Constants.BOOKING_CHECKIN)) {
            call = apiService.submitWaitListQuestionnaire(uid, requestBody, accountId);
        } else if (from.equalsIgnoreCase(Constants.ORDERS)) {
            call = apiService.submitOrderQuestionnaire(uid, requestBody, accountId);
        }

        call.enqueue(new Callback<SubmitQuestionnaire>() {
            @Override
            public void onResponse(Call<SubmitQuestionnaire> call, Response<SubmitQuestionnaire> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    if (response.code() == 200) {

                        SubmitQuestionnaire result = response.body();

                        if (result != null && result.getUrls().size() > 0) {

                            for (QuestionnaireUrls url : result.getUrls()) {

                                for (LabelPath p : imagePathList) {

                                    if (url.getUrl().contains(p.getFileName())) {

                                        p.setUrl(url.getUrl());
                                    }
                                }
                            }

                            uploadFilesToS3(imagePathList, result);
                        } else {
                            SharedPreference.getInstance(mContext).setValue(Constants.QUESTIONNAIRE, "");
                            SharedPreference.getInstance(mContext).setValue(Constants.QIMAGES, "");
                            Toast.makeText(mContext, "Updated Successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    } else {
                        if (response.code() == 422) {
                            Toast.makeText(mContext, response.errorBody().string(), Toast.LENGTH_SHORT).show();
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<SubmitQuestionnaire> call, Throwable t) {
                // Log error here since request failed
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
                Config.logV("Fail---------------" + t.toString());
            }
        });

    }

    private void uploadFilesToS3(ArrayList<LabelPath> filesList, SubmitQuestionnaire result) {

        try {
            ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
            final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
            mDialog.show();
            List<Observable<?>> requests = new ArrayList<>();
            for (LabelPath l : filesList) {
                if (l.getUrl() != null && !l.getUrl().trim().equalsIgnoreCase("")) {
                    RequestBody requestFile = RequestBody.create(MediaType.parse(l.getType()), new File(l.getPath()));
                    requests.add(apiService.uploadPreSignedS3File(l.getUrl(), requestFile));
                }
            }
            // Zip all requests with the Function, which will receive the results.
            Observable.zip(requests, new Function<Object[], Object>() {
                        @Override
                        public Object apply(Object[] objects) throws Exception {
                            // Objects[] is an array of combined results of completed requests
                            // do something with those results and emit new event
                            return objects;
                        }
                    })
                    // After all requests had been performed the next observer will receive the Object, returned from Function

                    .subscribe(
                            // Will be triggered if all requests will end successfully (4xx and 5xx also are successful requests too)
                            new Consumer<Object>() {
                                @Override
                                public void accept(Object object) throws Exception {
                                    //Do something on successful completion of all requests
                                    Log.e("ListOf Calls", "0");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Stuff that updates the UI
                                            try {
                                                if (mDialog.isShowing())
                                                    Config.closeDialog(getParent(), mDialog);
                                                ApiCheckStatus(uid, accountId, result);

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            },
                            // Will be triggered if any error during requests will happen
                            new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable e) throws Exception {
                                    Log.e("ListOf Calls", "1");

                                    //Do something on error completion of requests
                                }
                            }
                    );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ApiCheckStatus(String uid, int accountId, SubmitQuestionnaire result) throws JSONException {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        JSONObject uploadObj = new JSONObject();
        JSONArray uploadArray = new JSONArray();

        for (int i = 0; i < result.getUrls().size(); i++) {

            JSONObject urlObj = new JSONObject();

            urlObj.put("uid", result.getUrls().get(i).getUid());
            urlObj.put("labelName", result.getUrls().get(i).getLabelName());
            urlObj.put("url", result.getUrls().get(i).getUrl());
            urlObj.put("document", result.getUrls().get(i).getDocument());
            if (result.getUrls().get(i).getColumnId() != null && !result.getUrls().get(i).getColumnId().trim().equalsIgnoreCase("")) {
                urlObj.put("columnId", result.getUrls().get(i).getColumnId());
                urlObj.put("gridOrder", 1);
            }
            uploadArray.put(urlObj);
        }
        uploadObj.putOpt("urls", uploadArray);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), uploadObj.toString());
        Call<ResponseBody> call = null;
        if (from.equalsIgnoreCase(Constants.BOOKING_APPOINTMENT)) {
            call = apiService.checkAppointmentUploadStatus(uid, accountId, body);
        } else if (from.equalsIgnoreCase(Constants.BOOKING_CHECKIN)) {
            call = apiService.checkWaitlistUploadStatus(uid, accountId, body);
        } else if (from.equalsIgnoreCase(Constants.ORDERS)) {
            call = apiService.checkOrderUploadStatus(uid, accountId, body);
        }

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    if (response.code() == 200) {
                        if (isAftrQNR) {
                            SharedPreference.getInstance(mContext).setValue(Constants.QUESTIONNAIRE, "");
                            SharedPreference.getInstance(mContext).setValue(Constants.QIMAGES, "");
                        }
                        Toast.makeText(mContext, "Updated Successfully", Toast.LENGTH_SHORT).show();
                        finish();

                    } else {
                        if (response.code() == 422) {
                            Toast.makeText(mContext, response.errorBody().string(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
                Config.logV("Fail---------------" + t.toString());
            }
        });

    }

    private void ApiUpdateApptQuestionnaire(QuestionnairInpt input, ArrayList<LabelPath> imagePathList) {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        MediaType type = MediaType.parse("*/*");
        MultipartBody.Builder mBuilder = new MultipartBody.Builder();
        mBuilder.setType(MultipartBody.FORM);
        for (int i = 0; i < imagePathList.size(); i++) {

            file = new File(imagePathList.get(i).getPath());


            mBuilder.addFormDataPart("files", file.getName(), RequestBody.create(type, file));
        }

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(input);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json);
        mBuilder.addFormDataPart("question", "blob", body);
        RequestBody requestBody = mBuilder.build();

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<SubmitQuestionnaire> call = apiService.reSubmitAppQuestionnaire(uid, requestBody, accountId);

        call.enqueue(new Callback<SubmitQuestionnaire>() {
            @Override
            public void onResponse(Call<SubmitQuestionnaire> call, Response<SubmitQuestionnaire> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    if (response.code() == 200) {
                        imagePathList.clear();
                        Toast.makeText(mContext, "Updated successfully", Toast.LENGTH_SHORT).show();
                        finish();


                    } else {
                        if (response.code() == 422) {
                            Toast.makeText(mContext, response.errorBody().string(), Toast.LENGTH_SHORT).show();
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<SubmitQuestionnaire> call, Throwable t) {
                // Log error here since request failed
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
                Config.logV("Fail---------------" + t.toString());
            }
        });


    }

    private void ApiUpdateWaitListQuestionnaire(QuestionnairInpt input, ArrayList<LabelPath> imagePathList) {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        MediaType type = MediaType.parse("*/*");
        MultipartBody.Builder mBuilder = new MultipartBody.Builder();
        mBuilder.setType(MultipartBody.FORM);
        for (int i = 0; i < imagePathList.size(); i++) {

                file = new File(imagePathList.get(i).getPath());

                mBuilder.addFormDataPart("files", file.getName(), RequestBody.create(type, file));

        }

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(input);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json);
        mBuilder.addFormDataPart("question", "blob", body);
        RequestBody requestBody = mBuilder.build();

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<SubmitQuestionnaire> call = apiService.reSubmitWlQuestionnaire(uid, requestBody, accountId);

        call.enqueue(new Callback<SubmitQuestionnaire>() {
            @Override
            public void onResponse(Call<SubmitQuestionnaire> call, Response<SubmitQuestionnaire> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    if (response.code() == 200) {
                        imagePathList.clear();
                        Toast.makeText(mContext, "Updated successfully", Toast.LENGTH_SHORT).show();
                        finish();


                    } else {
                        if (response.code() == 422) {
                            Toast.makeText(mContext, response.errorBody().string(), Toast.LENGTH_SHORT).show();
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<SubmitQuestionnaire> call, Throwable t) {
                // Log error here since request failed
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
                Config.logV("Fail---------------" + t.toString());
            }
        });

    }


    private boolean validForm(ArrayList<Questions> questionsList) {

        for (Questions question : questionsList) {

            if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("fileUpload") && question.getGetQuestion().isMandatory()) {

                if (question.getGetQuestion().getFileProperties().getAllowedDocuments().size() > 1) {

                    View fileUploadView = viewsList.get(question.getGetQuestion().getLabelName());
                    RecyclerView rvFiles = (RecyclerView) fileUploadView.findViewById(R.id.rv_files);
                    FilesAdapter filesAdapter = (FilesAdapter) rvFiles.getAdapter();
                    TextView tvError = fileUploadView.findViewById(R.id.tv_error);

                    List<KeyPairBoolData> files = filesAdapter.getFiles();

                    if (files.size() > question.getGetQuestion().getFileProperties().getMaxNoOfFile()) {

                        tvError.setVisibility(View.VISIBLE);
                        tvError.setText("Maximum allowed files " + question.getGetQuestion().getFileProperties().getMaxNoOfFile());
                        return false;

                    } else if (files.size() < question.getGetQuestion().getFileProperties().getMinNoOfFile()) {

                        tvError.setVisibility(View.VISIBLE);
                        tvError.setText("Upload atleast " + question.getGetQuestion().getFileProperties().getMinNoOfFile() + " file(s)");

                        return false;

                    } else if (files.size() > 0 && (files.get(0).getImagePath() == null || files.get(0).getImagePath().trim().equalsIgnoreCase(""))) {

                        tvError.setVisibility(View.VISIBLE);
                        tvError.setText("Please upload file");

                        return false;

                    } else if (files.size() > 1 && (files.get(1).getImagePath() == null || files.get(1).getImagePath().trim().equalsIgnoreCase(""))) {

                        tvError.setVisibility(View.VISIBLE);
                        tvError.setText("Please upload file");

                        return false;
                    }

                } else {

                    View singleFileUploadView = viewsList.get(question.getGetQuestion().getLabelName());
                    ImageView ivSingleFile = (ImageView) singleFileUploadView.findViewById(R.id.iv_file);
                    TextView tvError = singleFileUploadView.findViewById(R.id.tv_error);

                    if (ivSingleFile.getDrawable() == null) {

                        tvError.setVisibility(View.VISIBLE);
                        tvError.setText(R.string.please_upload_file);
                        return false;
                    }

                }

            } else if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("list") && question.getGetQuestion().isMandatory()) {

                View listFieldView = viewsList.get(question.getGetQuestion().getLabelName());
                RecyclerView rvCheckBoxes = (RecyclerView) listFieldView.findViewById(R.id.rv_checkBoxes);
                CheckBoxAdapter checkBoxAdapter = (CheckBoxAdapter) rvCheckBoxes.getAdapter();
                TextView tvError = (TextView) listFieldView.findViewById(R.id.tv_error);

                ListProperties properties = new ListProperties();
                properties = question.getGetQuestion().getListProperties();
                int minAnswers = properties.getMinAnswers();
                int maxAnswers = properties.getMaxAnswers();
                int checkedCount = 0;
                if (checkBoxAdapter != null) {
                    checkedCount = checkBoxAdapter.getCheckedCount();
                }
                if (checkedCount < minAnswers) {

                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText("Please select atleast " + minAnswers + " Checkbox");
                    return false;
                } else if (checkedCount > maxAnswers) {

                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText("You can only select upto " + maxAnswers + " Answers");
                    return false;

                }

            } else if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("bool")) {

                View boolFieldView = viewsList.get(question.getGetQuestion().getLabelName());
                RadioGroup radioGroup = (RadioGroup) boolFieldView.findViewById(R.id.rg_radioGroup);
                RadioButton radioButtonYes = (RadioButton) boolFieldView.findViewById(R.id.rb_yes);
                RadioButton radioButtonNo = (RadioButton) boolFieldView.findViewById(R.id.rb_no);
                TextView tvError = (TextView) boolFieldView.findViewById(R.id.tv_error);

                if (!radioButtonYes.isChecked() && !radioButtonNo.isChecked()) {

                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText("Select atleast one option");
                    return false;
                }

            } else if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("plainText") && question.getGetQuestion().isMandatory()) {

                View textFieldView = viewsList.get(question.getGetQuestion().getLabelName());
                EditText etTextField = (EditText) textFieldView.findViewById(R.id.et_textBox);
                TextView tvError = (TextView) textFieldView.findViewById(R.id.tv_error);

                if (etTextField.getText().toString().trim().equalsIgnoreCase("")) {

                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText("Enter " + question.getGetQuestion().getLabelName());
                    return false;
                }

                if (etTextField.getText().toString().trim().length() < question.getGetQuestion().getPlainTextProperties().getMinNoOfLetter()) {

                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText("Enter minimum " + question.getGetQuestion().getPlainTextProperties().getMinNoOfLetter() + " Characters");
                    return false;
                }

            } else if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("number") && question.getGetQuestion().isMandatory()) {

                View numberFieldView = viewsList.get(question.getGetQuestion().getLabelName());
                EditText etTextField = (EditText) numberFieldView.findViewById(R.id.et_textBox);
                TextView tvError = (TextView) numberFieldView.findViewById(R.id.tv_error);

                if (etTextField.getText().toString().trim().equalsIgnoreCase("")) {

                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText("Enter " + question.getGetQuestion().getLabelName());
                    return false;
                }

            } else if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("date") && question.getGetQuestion().isMandatory()) {

                View dateFieldView = viewsList.get(question.getGetQuestion().getLabelName());
                TextView tvDate = dateFieldView.findViewById(R.id.tv_date);
                TextView tvError = (TextView) dateFieldView.findViewById(R.id.tv_error);

                if (tvDate.getText().toString().trim().equalsIgnoreCase("")) {

                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText("Select " + question.getGetQuestion().getLabelName());
                    return false;
                }

            }
        }


        return true;
    }


    private void createQuestionnaire(ArrayList<Questions> questionsList) {

        try {

            for (Questions question : questionsList) {

                if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("fileUpload")) {

                    if (question.getGetQuestion().getFileProperties() != null && question.getGetQuestion().getFileProperties().getAllowedDocuments() != null && question.getGetQuestion().getFileProperties().getAllowedDocuments().size() > 1) {

                        QuestionnaireFileUploadModel model = new QuestionnaireFileUploadModel();
                        model.setQuestionName(question.getGetQuestion().getLabel());
                        model.setManditory(question.getGetQuestion().isMandatory());
                        model.setLabelName(question.getGetQuestion().getLabelName());
                        model.setHint(question.getGetQuestion().getHint());
                        model.setAllowedTypes(question.getGetQuestion().getFileProperties().getFileTypes());

                        ArrayList<KeyPairBoolData> filesList = new ArrayList<>();

                        for (int i = 0; i < question.getGetQuestion().getFileProperties().getAllowedDocuments().size(); i++) {

                            KeyPairBoolData obj = new KeyPairBoolData();
                            obj.setName(question.getGetQuestion().getFileProperties().getAllowedDocuments().get(i));
                            obj.setId(i);
                            filesList.add(obj);

                        }
                        model.setFileNames(filesList);

                        ArrayList<KeyPairBoolData> dataList = new ArrayList<>();
                        for (LabelPath l : labelPaths) {
                            if (l.getLabelName().equalsIgnoreCase(question.getGetQuestion().getLabelName())) {
                                KeyPairBoolData file = new KeyPairBoolData(l.getFileName(), l.getPath(), l.getType());
                                dataList.add(file);
                            }
                        }
                        model.setFiles(dataList);

                        addFileUploadView(model);

                    } else {

                        QuestnnaireSingleFile singleFile = new QuestnnaireSingleFile();
                        singleFile.setQuestionName(question.getGetQuestion().getLabel());
                        singleFile.setManditory(question.getGetQuestion().isMandatory());
                        singleFile.setLabelName(question.getGetQuestion().getLabelName());
                        singleFile.setHint(question.getGetQuestion().getHint());
                        singleFile.setAllowedTypes(question.getGetQuestion().getFileProperties().getFileTypes());

                        for (LabelPath l : labelPaths) {
                            if (l.getLabelName().equalsIgnoreCase(question.getGetQuestion().getLabelName())) {
                                singleFile.setFilePath(l.getPath());
                                singleFile.setType(l.getType());
                            }
                        }

                        addSingleFileUploadView(singleFile);
                    }

                } else if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("plainText")) {

                    QuestionnaireTextField textField = new QuestionnaireTextField();
                    textField.setQuestionName(question.getGetQuestion().getLabel());
                    textField.setManditory(question.getGetQuestion().isMandatory());
                    textField.setLabelName(question.getGetQuestion().getLabelName());
                    textField.setHint(question.getGetQuestion().getHint());

                    for (AnswerLine answerLine : qInput.getAnswerLines()) {
                        if (answerLine != null && answerLine.getLabelName().equalsIgnoreCase(question.getGetQuestion().getLabelName())) {
                            JSONObject txtObj = new JSONObject(answerLine.getAnswer().toString());
                            String text = txtObj.optString("plainText");
                            if (text.equalsIgnoreCase("05:30 AM")) {
                                textField.setText("");
                            } else {
                                textField.setText(text);
                            }
                        }
                    }
                    addTextFieldView(textField);

                } else if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("date")) {

                    QuestionnaireDateField dateField = new QuestionnaireDateField();
                    dateField.setQuestionName(question.getGetQuestion().getLabel());
                    dateField.setManditory(question.getGetQuestion().isMandatory());
                    dateField.setLabelName(question.getGetQuestion().getLabelName());
                    dateField.setHint(question.getGetQuestion().getHint());

                    for (AnswerLine answerLine : qInput.getAnswerLines()) {
                        if (answerLine != null && answerLine.getLabelName().equalsIgnoreCase(question.getGetQuestion().getLabelName())) {
                            JSONObject txtObj = new JSONObject(answerLine.getAnswer().toString());
                            String text = txtObj.optString("date");
                            if (text.equalsIgnoreCase("05:30 AM")) {
                                dateField.setDate("");
                            } else {
                                dateField.setDate(text);
                            }
                        }
                    }

                    addDateFieldView(dateField);
                } else if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("number")) {

                    QuestionnaireNumberModel numberField = new QuestionnaireNumberModel();
                    numberField.setQuestionName(question.getGetQuestion().getLabel());
                    numberField.setManditory(question.getGetQuestion().isMandatory());
                    numberField.setLabelName(question.getGetQuestion().getLabelName());
                    numberField.setHint(question.getGetQuestion().getHint());

                    for (AnswerLine answerLine : qInput.getAnswerLines()) {
                        if (answerLine != null && answerLine.getLabelName().equalsIgnoreCase(question.getGetQuestion().getLabelName())) {
                            JSONObject txtObj = new JSONObject(answerLine.getAnswer().toString());
                            int number = txtObj.optInt("number");
                            numberField.setNumber(String.valueOf(number));
                        }
                    }

                    addNumberFieldView(numberField);

                } else if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("bool")) {

                    QuestionnaireBoolean boolField = new QuestionnaireBoolean();
                    boolField.setQuestionName(question.getGetQuestion().getLabel());
                    boolField.setManditory(question.getGetQuestion().isMandatory());
                    boolField.setLabelName(question.getGetQuestion().getLabelName());
                    boolField.setHint(question.getGetQuestion().getHint());

                    ArrayList values = (ArrayList) question.getGetQuestion().getLabelValues();
                    boolField.setLabels(values);

                    for (AnswerLine answerLine : qInput.getAnswerLines()) {
                        if (answerLine != null && answerLine.getLabelName().equalsIgnoreCase(question.getGetQuestion().getLabelName())) {
                            JSONObject txtObj = new JSONObject(answerLine.getAnswer().toString());
                            Boolean isSelected = txtObj.optBoolean("bool");
                            boolField.setSelected(isSelected);
                        }
                    }

                    addBooleanField(boolField);

                } else if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("list")) {

                    QuestionnaireListModel listModel = new QuestionnaireListModel();
                    listModel.setQuestionName(question.getGetQuestion().getLabel());
                    listModel.setManditory(question.getGetQuestion().isMandatory());
                    listModel.setLabelName(question.getGetQuestion().getLabelName());
                    listModel.setHint(question.getGetQuestion().getHint());
                    listModel.setMaxAnswerable(question.getGetQuestion().getListProperties().getMaxAnswers());
                    ArrayList values = (ArrayList) question.getGetQuestion().getLabelValues();
                    listModel.setLabels(values);

                    for (AnswerLine answerLine : qInput.getAnswerLines()) {
                        if (answerLine != null && answerLine.getLabelName().equalsIgnoreCase(question.getGetQuestion().getLabelName())) {

                            JSONObject txtObj = new JSONObject(answerLine.getAnswer().toString());
                            JSONArray list = (JSONArray) txtObj.get("list");
                            ArrayList<String> selectedItems = new ArrayList<>();
                            for (int i = 0; i < list.length(); i++) {
                                selectedItems.add(list.optString(i));
                            }
                            listModel.setSelectedItems(selectedItems);
                        }
                    }


                    addListField(listModel);

                } else if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("dataGrid")) {

                    QuestionnaireGridView gridView = new QuestionnaireGridView(this);
                    gridView.setQuestionData(question.getGetQuestion());

                    gridView.getLlAdd().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            SharedPreference.getInstance(CustomQuestionnaire.this).setValue(Constants.QUESTION, "");
                            SharedPreference.getInstance(mContext).setValue(Constants.ANSWER, "");

                            SharedPreference.getInstance(mContext).setValue(Constants.QUESTION, new Gson().toJson(question.getGetQuestion()));

                            DataGridFragment dataGridFragment = DataGridFragment.newInstance("");
                            dataGridFragment.setGridView(gridView);
                            final FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.container, dataGridFragment).addToBackStack("DataGrid")
                                    .commit();
                        }
                    });

                    gridView.setiDataGridListener(new IDataGridListener() {
                        @Override
                        public void onEditClick(DataGrid gridObj, int position) {

                            SharedPreference.getInstance(CustomQuestionnaire.this).setValue(Constants.QUESTION, "");
                            SharedPreference.getInstance(mContext).setValue(Constants.ANSWER, "");

                            SharedPreference.getInstance(mContext).setValue(Constants.QUESTION, new Gson().toJson(question.getGetQuestion()));
                            SharedPreference.getInstance(mContext).setValue(Constants.ANSWER, new Gson().toJson(gridObj));

                            DataGridFragment dataGridFragment = DataGridFragment.newInstance("", "", position);
                            dataGridFragment.setGridView(gridView);
                            final FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.container, dataGridFragment).addToBackStack("DataGrid")
                                    .commit();
                        }
                    });
                    llParentLayout.addView(gridView);
                    viewsList.put(question.getGetQuestion().getLabelName(), gridView);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void UpdateQuestionnaire(ArrayList<GetQuestion> questions) throws JSONException {

        for (GetQuestion question : questions) {

            if (question.getFieldDataType().equalsIgnoreCase("fileUpload")) {

                if (question.getFileProperties() != null && question.getFileProperties().getAllowedDocuments() != null && question.getFileProperties().getAllowedDocuments().size() > 1) {

                    QuestionnaireFileUploadModel model = new QuestionnaireFileUploadModel();
                    model.setQuestionName(question.getLabel());
                    model.setManditory(question.isMandatory());
                    model.setLabelName(question.getLabelName());
                    model.setHint(question.getHint());
                    ArrayList<KeyPairBoolData> filesList = new ArrayList<>();

                    for (int i = 0; i < question.getFileProperties().getAllowedDocuments().size(); i++) {

                        KeyPairBoolData obj = new KeyPairBoolData();
                        obj.setName(question.getFileProperties().getAllowedDocuments().get(i));
                        obj.setId(i);
                        filesList.add(obj);

                    }
                    model.setFileNames(filesList);

                    ArrayList<KeyPairBoolData> dataList = new ArrayList<>();
                    for (LabelPath l : labelPaths) {
                        if (l.getLabelName().equalsIgnoreCase(question.getLabelName())) {
                            KeyPairBoolData file = new KeyPairBoolData(l.getFileName(), l.getPath(), l.getType());
                            dataList.add(file);
                        }
                    }
                    model.setFiles(dataList);

                    addFileUploadView(model);

                } else {

                    QuestnnaireSingleFile singleFile = new QuestnnaireSingleFile();
                    singleFile.setQuestionName(question.getLabel());
                    singleFile.setManditory(question.isMandatory());
                    singleFile.setLabelName(question.getLabelName());
                    singleFile.setHint(question.getHint());

                    for (LabelPath l : labelPaths) {
                        if (l.getLabelName().equalsIgnoreCase(question.getLabelName())) {
                            singleFile.setFilePath(l.getPath());
                            singleFile.setType(l.getType());
                        }
                    }

                    addSingleFileUploadView(singleFile);
                }

            } else if (question.getFieldDataType().equalsIgnoreCase("plainText")) {

                QuestionnaireTextField textField = new QuestionnaireTextField();
                textField.setQuestionName(question.getLabel());
                textField.setManditory(question.isMandatory());
                textField.setLabelName(question.getLabelName());
                textField.setHint(question.getHint());

                for (AnswerLine answerLine : qInput.getAnswerLines()) {
                    if (answerLine != null && answerLine.getLabelName().equalsIgnoreCase(question.getLabelName())) {
                        JSONObject txtObj = new JSONObject(answerLine.getAnswer().toString());
                        String text = txtObj.optString("plainText");
                        if (text.equalsIgnoreCase("05:30 AM")) {
                            textField.setText("");
                        } else {
                            textField.setText(text);
                        }
                    }
                }
                addTextFieldView(textField);

            } else if (question.getFieldDataType().equalsIgnoreCase("date")) {

                QuestionnaireDateField dateField = new QuestionnaireDateField();
                dateField.setQuestionName(question.getLabel());
                dateField.setManditory(question.isMandatory());
                dateField.setLabelName(question.getLabelName());
                dateField.setHint(question.getHint());

                for (AnswerLine answerLine : qInput.getAnswerLines()) {
                    if (answerLine != null && answerLine.getLabelName().equalsIgnoreCase(question.getLabelName())) {
                        JSONObject txtObj = new JSONObject(answerLine.getAnswer().toString());
                        String text = txtObj.optString("date");
                        if (text.equalsIgnoreCase("05:30 AM")) {
                            dateField.setDate("");
                        } else {
                            dateField.setDate(text);
                        }
                    }
                }

                addDateFieldView(dateField);
            } else if (question.getFieldDataType().equalsIgnoreCase("number")) {

                QuestionnaireNumberModel numberField = new QuestionnaireNumberModel();
                numberField.setQuestionName(question.getLabel());
                numberField.setManditory(question.isMandatory());
                numberField.setLabelName(question.getLabelName());
                numberField.setHint(question.getHint());

                for (AnswerLine answerLine : qInput.getAnswerLines()) {
                    if (answerLine != null && answerLine.getLabelName().equalsIgnoreCase(question.getLabelName())) {
                        JSONObject txtObj = new JSONObject(answerLine.getAnswer().toString());
                        int number = txtObj.optInt("number");
                        numberField.setNumber(String.valueOf(number));
                    }
                }

                addNumberFieldView(numberField);

            } else if (question.getFieldDataType().equalsIgnoreCase("bool")) {

                QuestionnaireBoolean boolField = new QuestionnaireBoolean();
                boolField.setQuestionName(question.getLabel());
                boolField.setManditory(question.isMandatory());
                boolField.setLabelName(question.getLabelName());
                boolField.setHint(question.getHint());

                ArrayList values = (ArrayList) question.getLabelValues();
                boolField.setLabels(values);

                for (AnswerLine answerLine : qInput.getAnswerLines()) {
                    if (answerLine != null && answerLine.getLabelName().equalsIgnoreCase(question.getLabelName())) {
                        JSONObject txtObj = new JSONObject(answerLine.getAnswer().toString());
                        Boolean isSelected = txtObj.optBoolean("bool");
                        boolField.setSelected(isSelected);
                    }
                }

                addBooleanField(boolField);

            } else if (question.getFieldDataType().equalsIgnoreCase("list")) {

                QuestionnaireListModel listModel = new QuestionnaireListModel();
                listModel.setQuestionName(question.getLabel());
                listModel.setManditory(question.isMandatory());
                listModel.setLabelName(question.getLabelName());
                listModel.setHint(question.getHint());
                listModel.setMaxAnswerable(question.getListProperties().getMaxAnswers());

                ArrayList values = (ArrayList) question.getLabelValues();
                listModel.setLabels(values);

                for (AnswerLine answerLine : qInput.getAnswerLines()) {
                    if (answerLine != null && answerLine.getLabelName().equalsIgnoreCase(question.getLabelName())) {

                        JSONObject txtObj = new JSONObject(answerLine.getAnswer().toString());
//                        LinkedTreeMap list = (LinkedTreeMap) txtObj.get("list");
                        JSONArray list = (JSONArray) txtObj.get("list");
                        ArrayList<String> selectedItems = new ArrayList<>();
//                        for (Object obj : list.values()) {
//                            selectedItems.add(obj.toString().replace("[", "").replace("]", ""));
//                        }
                        for (int i = 0; i < list.length(); i++) {
                            selectedItems.add(list.optString(i));
                        }
                        listModel.setSelectedItems(selectedItems);
                    }
                }


                addListField(listModel);

            }
        }

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

                    ivSingleFile.setImageDrawable(getResources().getDrawable(R.drawable.pdf));
                } else {

                    Glide.with(mContext).load(singleFile.getFilePath()).into(ivSingleFile);
                }
            } else {
                if (singleFile.getFilePath().substring(singleFile.getFilePath().lastIndexOf(".") + 1).equals("pdf")) {

                    ivSingleFile.setImageDrawable(getResources().getDrawable(R.drawable.pdf));

                } else if (singleFile.getFilePath().substring(singleFile.getFilePath().lastIndexOf(".") + 1).equals("mp3")) {

                    ivSingleFile.setImageDrawable(getResources().getDrawable(R.drawable.audio_icon));

                } else if (Arrays.asList(Constants.videoExtFormats).contains(singleFile.getFilePath().substring(singleFile.getFilePath().lastIndexOf(".") + 1))) {

                    ivSingleFile.setImageDrawable(getResources().getDrawable(R.drawable.video_icon));

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

                        Config.openOnlinePdf(mContext, singleFile.getFilePath());

                    } else {

                        Intent intent = new Intent(CustomQuestionnaire.this, ImageActivity.class);
                        intent.putExtra("urlOrPath", singleFile.getFilePath());
                        startActivity(intent);
                    }

                } else {

                    String extension = "";

                    if (imagePath.contains(".")) {
                        extension = imagePath.substring(imagePath.lastIndexOf(".") + 1);
                    }

                    if (imagePath.substring(imagePath.lastIndexOf(".") + 1).equals("pdf")) {

                        Config.openPdf(getApplicationContext(), imagePath);

                    } else if (Arrays.asList(Constants.videoExtFormats).contains(extension)) {

                        Intent intent = new Intent(CustomQuestionnaire.this, VideoActivity.class);
                        intent.putExtra("urlOrPath", imagePath);
                        startActivity(intent);

                    } else if (extension.contains("mp3")) {

                        playAudio(imagePath);

                    } else {

                        Intent intent = new Intent(CustomQuestionnaire.this, ImageActivity.class);
                        intent.putExtra("urlOrPath", imagePath);
                        startActivity(intent);
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
        i.setDataAndType(FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", new File(imagePath)), "audio/*");
        startActivity(i);
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

        rvFiles.setLayoutManager(new LinearLayoutManager(this));
        FilesAdapter filesAdapter = new FilesAdapter(model.getFiles(), mContext, false, iFilesInterface);
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

                mDatePickerDialogFragment.show(getSupportFragmentManager(), "DATE PICK");

            }
        });


        llParentLayout.addView(dateFieldView);
        viewsList.put(dateField.getLabelName(), dateFieldView);


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

        list = listModel.getLabels();

        ArrayList<QuestionnaireCheckbox> checkBoxList = new ArrayList<>();
        for (String text : list) {

            QuestionnaireCheckbox checkBoxObj = new QuestionnaireCheckbox(false, text);
            checkBoxList.add(checkBoxObj);

        }

        for (QuestionnaireCheckbox checkbox : checkBoxList) {

            for (String item : listModel.getSelectedItems()) {

                if (item.equalsIgnoreCase(checkbox.getText())) {

                    checkbox.setChecked(true);
                }
            }
        }

        rvCheckBoxes.setLayoutManager(new LinearLayoutManager(this));
        CheckBoxAdapter checkBoxAdapter = new CheckBoxAdapter(checkBoxList, listModel.getMaxAnswerable(), mContext);
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

//        View fileUploadView = viewsList.get(question.getGetQuestion().getLabelName());
//        RecyclerView rvFiles = (RecyclerView) fileUploadView.findViewById(R.id.rv_files);
//        FilesAdapter filesAdapter = (FilesAdapter) rvFiles.getAdapter();
//
//        List<KeyPairBoolData> files = filesAdapter.getFiles();


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
                if ((ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, GALLERY_FOR_ONE);

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

                            InputStream inputStream = mContext.getContentResolver().openInputStream(uri);
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

                                InputStream inputStream = mContext.getContentResolver().openInputStream(uri);
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

        } else if (requestCode == CAMERA_FOR_ONE) {
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

                            InputStream inputStream = mContext.getContentResolver().openInputStream(uri);
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

                                ivSingleFile.setImageDrawable(getResources().getDrawable(R.drawable.pdf));

                            } else if (Arrays.asList(Constants.videoExtFormats).contains(extension)) {

                                ivSingleFile.setImageDrawable(mContext.getResources().getDrawable(R.drawable.video_icon));

                            } else if (orgFilePath.substring(orgFilePath.lastIndexOf(".") + 1).equals("mp3")) {

                                ivSingleFile.setImageDrawable(mContext.getResources().getDrawable(R.drawable.audio_icon));

                            } else {
                                ivSingleFile.setImageBitmap(BitmapFactory.decodeFile(orgFilePath));
                            }

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

                                InputStream inputStream = mContext.getContentResolver().openInputStream(uri);
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
                                    ivSingleFile.setImageDrawable(getResources().getDrawable(R.drawable.pdf));

                                } else if (Arrays.asList(Constants.videoExtFormats).contains(extension)) {

                                    ivSingleFile.setImageDrawable(mContext.getResources().getDrawable(R.drawable.video_icon));

                                } else if (orgFilePath.substring(orgFilePath.lastIndexOf(".") + 1).equals("mp3")) {

                                    ivSingleFile.setImageDrawable(mContext.getResources().getDrawable(R.drawable.audio_icon));

                                } else {
                                    ivSingleFile.setImageBitmap(BitmapFactory.decodeFile(orgFilePath));
                                }
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
                if ((ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{
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
}