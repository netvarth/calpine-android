package com.jaldeeinc.jaldee.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
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
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import com.jaldeeinc.jaldee.custom.CustomEditTextRegular;
import com.jaldeeinc.jaldee.custom.CustomItalicTextViewNormal;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.KeyPairBoolData;
import com.jaldeeinc.jaldee.custom.MultiSpinnerListener;
import com.jaldeeinc.jaldee.custom.MultiSpinnerSearch;
import com.jaldeeinc.jaldee.custom.QuestionnaireGridView;
import com.jaldeeinc.jaldee.model.AnswerLine;
import com.jaldeeinc.jaldee.model.BookingModel;
import com.jaldeeinc.jaldee.model.DataGrid;
import com.jaldeeinc.jaldee.model.GridColumnAnswerLine;
import com.jaldeeinc.jaldee.model.LabelPath;
import com.jaldeeinc.jaldee.model.QuestionnaireBoolean;
import com.jaldeeinc.jaldee.model.QuestionnaireCheckbox;
import com.jaldeeinc.jaldee.model.QuestionnaireDateField;
import com.jaldeeinc.jaldee.model.QuestionnaireFileUploadModel;
import com.jaldeeinc.jaldee.model.QuestionnaireInput;
import com.jaldeeinc.jaldee.model.QuestionnaireListModel;
import com.jaldeeinc.jaldee.model.QuestionnaireNumberModel;
import com.jaldeeinc.jaldee.model.QuestionnaireTextField;
import com.jaldeeinc.jaldee.model.QuestnnaireSingleFile;
import com.jaldeeinc.jaldee.response.GetQuestion;
import com.jaldeeinc.jaldee.response.ListProperties;
import com.jaldeeinc.jaldee.response.Questionnaire;
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

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

public class UpdateQuestionnaire extends AppCompatActivity implements IFilesInterface, DatePickerDialog.OnDateSetListener {

    @BindView(R.id.ll_mainLayout)
    LinearLayout llParentLayout;

    @BindView(R.id.cv_submit)
    CardView cvSubmit;

    @BindView(R.id.tv_buttonName)
    CustomTextViewBold tvButtonName;

    @BindView(R.id.iv_next)
    ImageView ivNext;

    @BindView(R.id.cv_back)
    CardView cvBack;

    private static Context mContext;
    private IFilesInterface iFilesInterface;
    private KeyPairBoolData fileObject = new KeyPairBoolData();
    private static final String IMAGE_DIRECTORY = "/Jaldee" + "";
    String[] fileExtsSupported = new String[]{"jpg", "jpeg", "png", "pdf", "mp3", "wmv", "mp4", "webm", "flw", "mov", "avi"};
    String[] videoFormats = new String[]{"wmv", "mp4", "webm", "flw", "mov", "avi", ".wmv", ".mp4", ".webm", ".flw", ".mov", ".avi"};
    String[] formats = new String[]{"wmv", "mp4", "webm", "flw", "mov", "avi", ".wmv", ".mp4", ".webm", ".flw", ".mov", ".avi"};
    private int GALLERY_FOR_ONE = 1, CAMERA_FOR_ONE = 2;
    private int GALLERY = 3, CAMERA = 4;

    private Uri mImageUri;
    File f;
    File file;
    String singleFilePath = "";
    Bitmap bitmap;

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
    QuestionnaireInput qInput = new QuestionnaireInput();
    public String from, uid, bookingStatus = "";
    private boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_questionnaire);
        ButterKnife.bind(this);
        mContext = UpdateQuestionnaire.this;
        iFilesInterface = this;

        requestMultiplePermissions();

        Intent intent = getIntent();
        bookingModel = (BookingModel) intent.getSerializableExtra("data");
        serviceId = intent.getIntExtra("serviceId", 0);
        accountId = intent.getIntExtra("accountId", 0);
        uid = intent.getStringExtra("uid");
        isEdit = intent.getBooleanExtra("isEdit", false);
        from = intent.getStringExtra("from");
        bookingStatus = intent.getStringExtra("status");
        if (bookingStatus == null) {
            bookingStatus = "";
        }

        if (!bookingStatus.trim().equalsIgnoreCase("") && !(bookingStatus.equalsIgnoreCase("Confirmed") || bookingStatus.equalsIgnoreCase("Arrived") || bookingStatus.equalsIgnoreCase("checkedIn") || bookingStatus.equalsIgnoreCase("arrived") || bookingStatus.equalsIgnoreCase(Constants.DONATION))) {

            showAlert("Service has started", "The given details cannot be edited as the service has started");
        }

        // to show alert in Donation case

        if (bookingStatus.equalsIgnoreCase(Constants.DONATION)) {

            Toast.makeText(mContext, "Details cannot be edited as the donation has been made", Toast.LENGTH_SHORT).show();
            cvSubmit.setVisibility(View.GONE);

        } else {

            cvSubmit.setVisibility(View.VISIBLE);
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

        if (qInput != null && qInput.getQuestions() != null) {

            try {
                UpdateQuestionnaire(qInput.getQuestions());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void showAlert(String title, String message) {

        Config.showAlertBuilder(mContext, title, message);
    }

    private void submitQuestionnaire() throws JSONException {

        if (from.equalsIgnoreCase(Constants.DONATION)) {

            Toast.makeText(mContext, "The given details cannot be edited.", Toast.LENGTH_SHORT).show();
            return;
        }

        QuestionnaireInput input = new QuestionnaireInput();
        input.setQuestionnaireId(qInput.getQuestionnaireId());
        ArrayList<AnswerLine> answerLines = new ArrayList<>();

        if (validForm(qInput.getQuestions())) {

            labelPaths = new ArrayList<>();

            for (GetQuestion question : qInput.getQuestions()) {

                if (question.getFieldDataType().equalsIgnoreCase("fileUpload")) {

                    if (question.getFileProperties().getAllowedDocuments().size() == 1) {

                        View singleFileUploadView = viewsList.get(question.getLabelName());
                        ImageView ivSingleFile = (ImageView) singleFileUploadView.findViewById(R.id.iv_file);
                        CustomTextViewMedium tvPath = (CustomTextViewMedium) singleFileUploadView.findViewById(R.id.tv_path);

                        String path = null;
                        if (!tvPath.getText().toString().trim().equalsIgnoreCase("")) {

                            path = tvPath.getText().toString();
                        }


                        if (path != null && !path.trim().equalsIgnoreCase("")) {

                            if (!(path.contains("http://") || path.contains("https://"))) {

                                AnswerLine obj = new AnswerLine();
                                obj.setLabelName(question.getLabelName());
                                JsonObject answer = new JsonObject();
                                JsonArray uploadList = new JsonArray();
                                JsonObject fileInfo = new JsonObject();
                                String filename = null;
                                String mimeType = mContext.getContentResolver().getType(Uri.fromFile(new File(path)));


                                if (mimeType != null && (mimeType.toLowerCase().contains("audio") || mimeType.toLowerCase().contains("video"))) {

                                    filename = path.substring(path.lastIndexOf("/") + 1);

                                    fileInfo.addProperty("mimeType", mimeType);
                                    fileInfo.addProperty("url", filename);

                                } else {

                                    fileInfo.addProperty("index", labelPaths.size());
                                }
                                fileInfo.addProperty("caption", question.getFileProperties().getAllowedDocuments().get(0));
                                fileInfo.addProperty("action", isEdit ? "update" : "add");
                                uploadList.add(fileInfo);
                                answer.add("fileUpload", uploadList);
                                obj.setAnswer(answer);
                                answerLines.add(obj);

                                LabelPath lPath = new LabelPath(labelPaths.size(), question.getLabelName(), path, filename, mimeType);
                                labelPaths.add(lPath);
                            }

                        }

                    } else {

                        AnswerLine obj = new AnswerLine();
                        obj.setLabelName(question.getLabelName());
                        JsonObject answer = new JsonObject();
                        JsonArray uploadList = new JsonArray();

                        View fileUploadView = viewsList.get(question.getLabelName());
                        RecyclerView rvFiles = (RecyclerView) fileUploadView.findViewById(R.id.rv_files);
                        FilesAdapter filesAdapter = (FilesAdapter) rvFiles.getAdapter();

                        List<KeyPairBoolData> files = filesAdapter.getFiles();

                        for (int i = 0; i < files.size(); i++) {

                            if (files.get(i).getImagePath() != null && !files.get(i).getImagePath().trim().equalsIgnoreCase("")) {

                                if (!(files.get(i).getImagePath().contains("http://") || files.get(i).getImagePath().contains("https://"))) {

                                    JsonObject fileInfo = new JsonObject();

                                    String path = files.get(i).getImagePath();
                                    String mimeType = mContext.getContentResolver().getType(Uri.fromFile(new File(path)));

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

                                    LabelPath lPath = new LabelPath(labelPaths.size(), question.getLabelName(), files.get(i).getImagePath(), filename, mimeType);
                                    labelPaths.add(lPath);
                                }
                            }
                        }


                    }

                } else if (question.getFieldDataType().equalsIgnoreCase("list")) {

                    View listFieldView = viewsList.get(question.getLabelName());
                    RecyclerView rvCheckBoxes = (RecyclerView) listFieldView.findViewById(R.id.rv_checkBoxes);
                    CheckBoxAdapter checkBoxAdapter = (CheckBoxAdapter) rvCheckBoxes.getAdapter();
                    ArrayList<QuestionnaireCheckbox> selectedCheckboxes = new ArrayList<>();
                    if (checkBoxAdapter != null) {

                        selectedCheckboxes = checkBoxAdapter.getSelectedCheckboxes();
                    }
                    AnswerLine obj = new AnswerLine();
                    obj.setLabelName(question.getLabelName());
                    JsonObject answer = new JsonObject();
                    JsonArray list = new JsonArray();
                    for (QuestionnaireCheckbox item : selectedCheckboxes) {

                        list.add(item.getText());
                    }
                    answer.add("list", list);

                    obj.setAnswer(answer);
                    answerLines.add(obj);


                } else if (question.getFieldDataType().equalsIgnoreCase("bool")) {

                    View boolFieldView = viewsList.get(question.getLabelName());
                    RadioButton radioButtonYes = (RadioButton) boolFieldView.findViewById(R.id.rb_yes);

                    AnswerLine obj = new AnswerLine();
                    obj.setLabelName(question.getLabelName());

                    JsonObject answer = new JsonObject();
                    answer.addProperty("bool", radioButtonYes.isSelected());

                    obj.setAnswer(answer);
                    answerLines.add(obj);

                } else if (question.getFieldDataType().equalsIgnoreCase("plainText")) {

                    View textFieldView = viewsList.get(question.getLabelName());
                    CustomEditTextRegular etTextField = (CustomEditTextRegular) textFieldView.findViewById(R.id.et_textBox);

                    AnswerLine obj = new AnswerLine();
                    obj.setLabelName(question.getLabelName());

                    JsonObject answer = new JsonObject();
                    answer.addProperty("plainText", etTextField.getText().toString());

                    obj.setAnswer(answer);
                    answerLines.add(obj);

                } else if (question.getFieldDataType().equalsIgnoreCase("number")) {

                    View numberFieldView = viewsList.get(question.getLabelName());
                    CustomEditTextRegular etTextField = (CustomEditTextRegular) numberFieldView.findViewById(R.id.et_textBox);

                    AnswerLine obj = new AnswerLine();
                    obj.setLabelName(question.getLabelName());

                    JsonObject answer = new JsonObject();
                    answer.addProperty("number", etTextField.getText().toString());

                    obj.setAnswer(answer);
                    answerLines.add(obj);

                } else if (question.getFieldDataType().equalsIgnoreCase("date")) {

                    View numberFieldView = viewsList.get(question.getLabelName());
                    CustomTextViewSemiBold tvDate = (CustomTextViewSemiBold) numberFieldView.findViewById(R.id.tv_date);

                    AnswerLine obj = new AnswerLine();
                    obj.setLabelName(question.getLabelName());

                    JsonObject answer = new JsonObject();
                    answer.addProperty("date", tvDate.getText().toString());

                    obj.setAnswer(answer);
                    answerLines.add(obj);

                } else if (question.getFieldDataType().equalsIgnoreCase("dataGrid")) {

                    QuestionnaireGridView gridFieldView = (QuestionnaireGridView) viewsList.get(question.getLabelName());

                    ArrayList<DataGrid> dataGridList = new ArrayList<>();

                    if (gridFieldView != null) {

                        dataGridList = gridFieldView.getGridDataList();
                    }

                    AnswerLine obj = new AnswerLine();
                    obj.setLabelName(question.getLabelName());

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
                                    String mimeType = getMimeType(filePath);

                                    LabelPath lPath = new LabelPath(labelPaths.size(), question.getLabelName(), filePath, fileName, mimeType);
                                    labelPaths.add(lPath);
                                }

                            }
                        }
                    }

                }
            }

            input.setAnswerLines(answerLines);


            SharedPreference.getInstance(mContext).setValue(Constants.QUESTIONNAIRE, new Gson().toJson(input));
            SharedPreference.getInstance(mContext).setValue(Constants.QIMAGES, new Gson().toJson(labelPaths));

            if (from.equalsIgnoreCase(Constants.BOOKING_APPOINTMENT)) {

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

            } else if (from.equalsIgnoreCase(Constants.DONATION)) {

                Toast.makeText(mContext, "The given details cannot be edited.", Toast.LENGTH_SHORT).show();

            }
        }

    }


    private void ApiUpdateApptQuestionnaire(QuestionnaireInput input, ArrayList<LabelPath> imagePathList) {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        MediaType type = MediaType.parse("*/*");
        MultipartBody.Builder mBuilder = new MultipartBody.Builder();
        mBuilder.setType(MultipartBody.FORM);
        for (int i = 0; i < imagePathList.size(); i++) {

            try {
                if (imagePathList.get(i).getPath().contains("http://") || imagePathList.get(i).getPath().contains("https://")) {


                } else {
                    bitmap = MediaStore.Images.Media.getBitmap(mContext.getApplicationContext().getContentResolver(), Uri.fromFile(new File(imagePathList.get(i).getPath())));
                    if (bitmap != null) {
                        String path = saveImage(bitmap);
                        file = new File(path);
                    } else {
                        file = new File(imagePathList.get(i).getPath());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

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

                        SubmitQuestionnaire result = response.body();

                        if (result != null && result.getUrls().size() > 0) {

                            for (QuestionnaireUrls url : result.getUrls()) {

                                for (LabelPath p : imagePathList) {

                                    String fileName = URLEncoder.encode(p.getFileName(), StandardCharsets.UTF_8.toString());

                                    if (fileName != null && fileName.trim().length() > 0 && url.getUrl().contains(fileName)) {

                                        p.setUrl(url.getUrl());
                                    }
                                }
                            }

                            uploadFilesToS3(imagePathList, result);
                        } else {

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
                                                if (from.equalsIgnoreCase(Constants.BOOKING_APPOINTMENT)) {

                                                    ApiCheckStatus(uid, accountId, result);

                                                } else if (from.equalsIgnoreCase(Constants.BOOKING_CHECKIN)) {

                                                    ApiCheckWaitlistUploadStatus(uid, accountId, result);
                                                }
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

        Call<ResponseBody> call = apiService.checkAppointmentUploadStatus(uid, accountId, body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    if (response.code() == 200) {

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

    private void ApiCheckWaitlistUploadStatus(String uid, int accountId, SubmitQuestionnaire result) throws JSONException {

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
        Call<ResponseBody> call = apiService.checkWaitlistUploadStatus(uid, accountId, body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    if (response.code() == 200) {

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


    private void ApiUpdateWaitListQuestionnaire(QuestionnaireInput input, ArrayList<LabelPath> imagePathList) {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        MediaType type = MediaType.parse("*/*");
        MultipartBody.Builder mBuilder = new MultipartBody.Builder();
        mBuilder.setType(MultipartBody.FORM);
        for (int i = 0; i < imagePathList.size(); i++) {

            try {
                if (imagePathList.get(i).getPath().contains("http://") || imagePathList.get(i).getPath().contains("https://")) {


                } else {
                    bitmap = MediaStore.Images.Media.getBitmap(mContext.getApplicationContext().getContentResolver(), Uri.fromFile(new File(imagePathList.get(i).getPath())));
                    if (bitmap != null) {
                        String path = saveImage(bitmap);
                        file = new File(path);
                    } else {
                        file = new File(imagePathList.get(i).getPath());
                    }

                    mBuilder.addFormDataPart("files", file.getName(), RequestBody.create(type, file));

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

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

    private boolean validForm(ArrayList<GetQuestion> questions) {

        for (GetQuestion question : questions) {

            if (question.getFieldDataType().equalsIgnoreCase("fileUpload") && question.isMandatory()) {

                if (question.getFileProperties().getAllowedDocuments().size() > 1) {

                    View fileUploadView = viewsList.get(question.getLabelName());
                    RecyclerView rvFiles = (RecyclerView) fileUploadView.findViewById(R.id.rv_files);
                    FilesAdapter filesAdapter = (FilesAdapter) rvFiles.getAdapter();
                    CustomItalicTextViewNormal tvError = (CustomItalicTextViewNormal) fileUploadView.findViewById(R.id.tv_error);

                    List<KeyPairBoolData> files = filesAdapter.getFiles();

                    if (files.size() > question.getFileProperties().getMaxNoOfFile()) {

                        tvError.setVisibility(View.VISIBLE);
                        tvError.setText("Maximum allowed files " + question.getFileProperties().getMaxNoOfFile());
                        return false;

                    } else if (files.size() < question.getFileProperties().getMinNoOfFile()) {

                        tvError.setVisibility(View.VISIBLE);
                        tvError.setText("Upload atleast " + question.getFileProperties().getMinNoOfFile() + " file(s)");

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

                    View singleFileUploadView = viewsList.get(question.getLabelName());
                    ImageView ivSingleFile = (ImageView) singleFileUploadView.findViewById(R.id.iv_file);
                    CustomItalicTextViewNormal tvError = (CustomItalicTextViewNormal) singleFileUploadView.findViewById(R.id.tv_error);


                    if (ivSingleFile.getDrawable() == null) {

                        tvError.setVisibility(View.VISIBLE);
                        tvError.setText(R.string.please_upload_file);
                        return false;
                    }


                }

            } else if (question.getFieldDataType().equalsIgnoreCase("list") && question.isMandatory()) {

                View listFieldView = viewsList.get(question.getLabelName());
                RecyclerView rvCheckBoxes = (RecyclerView) listFieldView.findViewById(R.id.rv_checkBoxes);
                CheckBoxAdapter checkBoxAdapter = (CheckBoxAdapter) rvCheckBoxes.getAdapter();
                CustomItalicTextViewNormal tvError = (CustomItalicTextViewNormal) listFieldView.findViewById(R.id.tv_error);

                ListProperties properties = new ListProperties();
                properties = question.getListProperties();
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

            } else if (question.getFieldDataType().equalsIgnoreCase("bool") && question.isMandatory()) {

                View boolFieldView = viewsList.get(question.getLabelName());
                RadioGroup radioGroup = (RadioGroup) boolFieldView.findViewById(R.id.rg_radioGroup);
                RadioButton radioButtonYes = (RadioButton) boolFieldView.findViewById(R.id.rb_yes);
                RadioButton radioButtonNo = (RadioButton) boolFieldView.findViewById(R.id.rb_no);
                CustomItalicTextViewNormal tvError = (CustomItalicTextViewNormal) boolFieldView.findViewById(R.id.tv_error);

                if (!radioButtonYes.isSelected() && !radioButtonNo.isSelected()) {

                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText("Select atleast one option");
                    return false;
                }

            } else if (question.getFieldDataType().equalsIgnoreCase("plainText") && question.isMandatory()) {

                View textFieldView = viewsList.get(question.getLabelName());
                CustomEditTextRegular etTextField = (CustomEditTextRegular) textFieldView.findViewById(R.id.et_textBox);
                CustomItalicTextViewNormal tvError = (CustomItalicTextViewNormal) textFieldView.findViewById(R.id.tv_error);

                if (etTextField.getText().toString().trim().equalsIgnoreCase("")) {

                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText("Enter " + question.getLabelName());
                    return false;
                }

            } else if (question.getFieldDataType().equalsIgnoreCase("number") && question.isMandatory()) {

                View numberFieldView = viewsList.get(question.getLabelName());
                CustomEditTextRegular etTextField = (CustomEditTextRegular) numberFieldView.findViewById(R.id.et_textBox);
                CustomItalicTextViewNormal tvError = (CustomItalicTextViewNormal) numberFieldView.findViewById(R.id.tv_error);

                if (etTextField.getText().toString().trim().equalsIgnoreCase("")) {

                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText("Enter " + question.getLabelName());
                    return false;
                }

            } else if (question.getFieldDataType().equalsIgnoreCase("date") && question.isMandatory()) {

                View dateFieldView = viewsList.get(question.getLabelName());
                CustomTextViewSemiBold tvDate = (CustomTextViewSemiBold) dateFieldView.findViewById(R.id.tv_date);
                CustomItalicTextViewNormal tvError = (CustomItalicTextViewNormal) dateFieldView.findViewById(R.id.tv_error);

                if (tvDate.getText().toString().trim().equalsIgnoreCase("")) {

                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText("Select " + question.getLabelName());
                    return false;
                }

            }
        }


        return true;
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
                    model.setAllowedTypes(question.getFileProperties().getFileTypes());
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
                    singleFile.setAllowedTypes(question.getFileProperties().getFileTypes());

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
                    if (answerLine.getLabelName().equalsIgnoreCase(question.getLabelName())) {
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
                    if (answerLine.getLabelName().equalsIgnoreCase(question.getLabelName())) {
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
                    if (answerLine.getLabelName().equalsIgnoreCase(question.getLabelName())) {
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
                    if (answerLine.getLabelName().equalsIgnoreCase(question.getLabelName())) {
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
                ArrayList values = (ArrayList) question.getLabelValues();
                listModel.setLabels(values);

                for (AnswerLine answerLine : qInput.getAnswerLines()) {
                    if (answerLine.getLabelName().equalsIgnoreCase(question.getLabelName())) {

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

            } else if (question.getFieldDataType().equalsIgnoreCase("dataGrid")) {

                QuestionnaireGridView gridView = new QuestionnaireGridView(this);
                gridView.setQuestionData(question);

                ArrayList<DataGrid> dataGridList = new ArrayList<>();

                for (AnswerLine answerLine : qInput.getAnswerLines()) {
                    if (answerLine.getLabelName().equalsIgnoreCase(question.getLabelName())) {

                        dataGridList = answerLine.getDataGridList();
                    }
                }
                gridView.setGridDataList(dataGridList);

                gridView.getLlAdd().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        SharedPreference.getInstance(UpdateQuestionnaire.this).setValue(Constants.QUESTION, "");
                        SharedPreference.getInstance(mContext).setValue(Constants.QUESTION, new Gson().toJson(question));

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

                        SharedPreference.getInstance(UpdateQuestionnaire.this).setValue(Constants.QUESTION, "");
                        SharedPreference.getInstance(mContext).setValue(Constants.ANSWER, "");

                        SharedPreference.getInstance(mContext).setValue(Constants.QUESTION, new Gson().toJson(question));
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
                viewsList.put(question.getLabelName(), gridView);

            }
        }

    }


    private void addSingleFileUploadView(QuestnnaireSingleFile singleFile) {

        View fileUploadView = getLayoutInflater().inflate(R.layout.singlefile_upload, null, false);

        CustomTextViewSemiBold tvQuestionName = (CustomTextViewSemiBold) fileUploadView.findViewById(R.id.tv_questionName);
        CustomTextViewBold tvMutipleFileManditory = (CustomTextViewBold) fileUploadView.findViewById(R.id.tv_singleFileManditory);
        CustomTextViewMedium tvSupportedTypes = (CustomTextViewMedium) fileUploadView.findViewById(R.id.tv_supportedTypes);
        LinearLayout llUpload = (LinearLayout) fileUploadView.findViewById(R.id.ll_upload);
        ImageView ivSingleFile = (ImageView) fileUploadView.findViewById(R.id.iv_file);
        ImageView ivClose = (ImageView) fileUploadView.findViewById(R.id.iv_close);
        CustomTextViewMedium tvHint = (CustomTextViewMedium) fileUploadView.findViewById(R.id.tv_hint);
        CustomTextViewMedium tvPath = (CustomTextViewMedium) fileUploadView.findViewById(R.id.tv_path);


        tvQuestionName.setText(singleFile.getQuestionName());
        tvSupportedTypes.setText(singleFile.getAllowedTypes().toString());

        if (singleFile.getFilePath() != null && !singleFile.getFilePath().trim().equalsIgnoreCase("")) {

            tvPath.setText(singleFile.getFilePath());
            if (singleFile.getFilePath().contains("http://") || singleFile.getFilePath().contains("https://")) {

                String extension = "";

                if (singleFile.getType() != null) {
                    extension = singleFile.getType().substring(singleFile.getType().lastIndexOf("/") + 1);
                }

                if (singleFile.getType() != null && singleFile.getType().equalsIgnoreCase(".pdf")) {

                    ivSingleFile.setImageDrawable(getResources().getDrawable(R.drawable.pdfs));
                } else if (singleFile.getType() != null && singleFile.getType().contains("audio")) {

                    ivSingleFile.setImageDrawable(getResources().getDrawable(R.drawable.audio_icon));

                } else if (Arrays.asList(videoFormats).contains(extension)) {

                    ivSingleFile.setImageDrawable(getResources().getDrawable(R.drawable.video_icon));

                } else {

                    Glide.with(mContext).load(singleFile.getFilePath()).into(ivSingleFile);
                }
            } else {
                if (singleFile.getFilePath().substring(singleFile.getFilePath().lastIndexOf(".") + 1).equals("pdf")) {

                    ivSingleFile.setImageDrawable(getResources().getDrawable(R.drawable.pdfs));

                } else if (singleFile.getFilePath().substring(singleFile.getFilePath().lastIndexOf(".") + 1).equals("mp3")) {

                    ivSingleFile.setImageDrawable(getResources().getDrawable(R.drawable.audio_icon));

                } else if (Arrays.asList(formats).contains(singleFile.getFilePath().substring(singleFile.getFilePath().lastIndexOf(".") + 1))) {

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

                String imagePath = singleFile.getFilePath();

                if (imagePath.contains("http://") || imagePath.contains("https://")) {

                    String extension = "";

                    if (singleFile.getType() != null) {
                        extension = singleFile.getType().substring(singleFile.getType().lastIndexOf("/") + 1);
                    }

                    if (singleFile.getType() != null && singleFile.getType().equalsIgnoreCase(".pdf")) {

                        openOnlinePdf(mContext, singleFile.getFilePath());

                    } else if (Arrays.asList(videoFormats).contains(extension)) {

                        Intent intent = new Intent(UpdateQuestionnaire.this, VideoActivity.class);
                        intent.putExtra("urlOrPath", imagePath);
                        startActivity(intent);

                    } else if (singleFile.getType().contains("audio")) {

                        Intent viewMediaIntent = new Intent();
                        viewMediaIntent.setAction(android.content.Intent.ACTION_VIEW);
                        viewMediaIntent.setDataAndType(Uri.parse(singleFile.getFilePath()), "audio/*");
                        viewMediaIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(viewMediaIntent);

                    } else {

                        Intent intent = new Intent(UpdateQuestionnaire.this, ImageActivity.class);
                        intent.putExtra("urlOrPath", singleFile.getFilePath());
                        startActivity(intent);
                    }

                } else {

                    String extension = "";

                    if (imagePath.contains(".")) {
                        extension = imagePath.substring(imagePath.lastIndexOf(".") + 1);
                    }

                    if (imagePath.substring(imagePath.lastIndexOf(".") + 1).equals("pdf")) {

                        openPdf(getApplicationContext(), imagePath);

                    } else if (Arrays.asList(formats).contains(extension)) {

                        Intent intent = new Intent(UpdateQuestionnaire.this, VideoActivity.class);
                        intent.putExtra("urlOrPath", imagePath);
                        startActivity(intent);

                    } else if (extension.contains("mp3")) {

                        playAudio(imagePath);

                    } else {

                        Intent intent = new Intent(UpdateQuestionnaire.this, ImageActivity.class);
                        intent.putExtra("urlOrPath", singleFile.getFilePath());
                        startActivity(intent);
                    }
                }
            }
        });
        if (from.equalsIgnoreCase(Constants.DONATION) && !isEdit) {  // if it from Donation, its not need to editable,there for use a masking layout above the parent layout
            llUpload.setOnClickListener(null);
            ivClose.setOnClickListener(null);
        }

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

        CustomTextViewSemiBold tvQuestionName = (CustomTextViewSemiBold) fileUploadView.findViewById(R.id.tv_questionName);
        MultiSpinnerSearch filesSpinner = (MultiSpinnerSearch) fileUploadView.findViewById(R.id.mfilesSpinner);
        RecyclerView rvFiles = (RecyclerView) fileUploadView.findViewById(R.id.rv_files);
        CustomTextViewBold tvMutipleFileManditory = (CustomTextViewBold) fileUploadView.findViewById(R.id.tv_multipleFileManditory);
        CustomTextViewMedium tvSupportedTypes = (CustomTextViewMedium) fileUploadView.findViewById(R.id.tv_supportedTypes);
        CustomTextViewMedium tvHint = (CustomTextViewMedium) fileUploadView.findViewById(R.id.tv_hint);

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
        if (from.equalsIgnoreCase(Constants.DONATION) && !isEdit) {  // if it from Donation, its not need to editable,there for use a masking layout above the parent layout
            filesSpinner.setVisibility(View.GONE);
        }

        llParentLayout.addView(fileUploadView);
        viewsList.put(model.getLabelName(), fileUploadView);

    }

    private void addTextFieldView(QuestionnaireTextField textField) {

        View textFieldView = getLayoutInflater().inflate(R.layout.edittext_field, null, false);

        LinearLayout ll_mask = (LinearLayout) textFieldView.findViewById(R.id.ll_mask);
        CustomTextViewSemiBold tvQuestionName = (CustomTextViewSemiBold) textFieldView.findViewById(R.id.tv_questionName);
        CustomTextViewBold tvTextFieldManditory = (CustomTextViewBold) textFieldView.findViewById(R.id.tv_manditory);
        CustomEditTextRegular etTextField = (CustomEditTextRegular) textFieldView.findViewById(R.id.et_textBox);
        CustomTextViewMedium tvHint = (CustomTextViewMedium) textFieldView.findViewById(R.id.tv_hint);


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
        if (from.equalsIgnoreCase(Constants.DONATION) && !isEdit) {  // if it from Donation, its not need to editable,there for use a masking layout above the parent layout
            ll_mask.setVisibility(View.VISIBLE);
        } else {
            ll_mask.setVisibility(View.GONE);
        }
        llParentLayout.addView(textFieldView);
        viewsList.put(textField.getLabelName(), textFieldView);


    }

    private void addDateFieldView(QuestionnaireDateField dateField) {

        View dateFieldView = getLayoutInflater().inflate(R.layout.date_item, null, false);

        LinearLayout ll_mask = (LinearLayout) dateFieldView.findViewById(R.id.ll_mask);
        CustomTextViewSemiBold tvQuestionName = (CustomTextViewSemiBold) dateFieldView.findViewById(R.id.tv_questionName);
        CustomTextViewBold tvTextFieldManditory = (CustomTextViewBold) dateFieldView.findViewById(R.id.tv_manditory);
        RelativeLayout rlCalender = (RelativeLayout) dateFieldView.findViewById(R.id.rl_calender);
        CustomTextViewSemiBold tvDate = (CustomTextViewSemiBold) dateFieldView.findViewById(R.id.tv_date);
        CustomTextViewMedium tvHint = (CustomTextViewMedium) dateFieldView.findViewById(R.id.tv_hint);

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
        if (from.equalsIgnoreCase(Constants.DONATION) && !isEdit) {  // if it from Donation, its not need to editable,there for use a masking layout above the parent layout
            ll_mask.setVisibility(View.VISIBLE);
        } else {
            ll_mask.setVisibility(View.GONE);
        }

        llParentLayout.addView(dateFieldView);
        viewsList.put(dateField.getLabelName(), dateFieldView);

    }

    private void addNumberFieldView(QuestionnaireNumberModel numberField) {

        View dateFieldView = getLayoutInflater().inflate(R.layout.numberfield_item, null, false);

        LinearLayout ll_mask = (LinearLayout) dateFieldView.findViewById(R.id.ll_mask);
        CustomTextViewSemiBold tvQuestionName = (CustomTextViewSemiBold) dateFieldView.findViewById(R.id.tv_questionName);
        CustomTextViewBold tvTextFieldManditory = (CustomTextViewBold) dateFieldView.findViewById(R.id.tv_manditory);
        CustomEditTextRegular etTextField = (CustomEditTextRegular) dateFieldView.findViewById(R.id.et_textBox);
        CustomTextViewMedium tvHint = (CustomTextViewMedium) dateFieldView.findViewById(R.id.tv_hint);

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
        if (from.equalsIgnoreCase(Constants.DONATION) && !isEdit) {  // if it from Donation, its not need to editable,there for use a masking layout above the parent layout
            ll_mask.setVisibility(View.VISIBLE);
        } else {
            ll_mask.setVisibility(View.GONE);
        }

        llParentLayout.addView(dateFieldView);
        viewsList.put(numberField.getLabelName(), dateFieldView);

    }

    private void addBooleanField(QuestionnaireBoolean boolField) {

        View boolFieldView = getLayoutInflater().inflate(R.layout.boolean_item, null, false);

        LinearLayout ll_mask = (LinearLayout) boolFieldView.findViewById(R.id.ll_mask);
        CustomTextViewSemiBold tvQuestionName = (CustomTextViewSemiBold) boolFieldView.findViewById(R.id.tv_questionName);
        CustomTextViewBold tvTextFieldManditory = (CustomTextViewBold) boolFieldView.findViewById(R.id.tv_manditory);
        RadioGroup radioGroup = (RadioGroup) boolFieldView.findViewById(R.id.rg_radioGroup);
        RadioButton radioButtonYes = (RadioButton) boolFieldView.findViewById(R.id.rb_yes);
        RadioButton radioButtonNo = (RadioButton) boolFieldView.findViewById(R.id.rb_no);
        CustomTextViewMedium tvHint = (CustomTextViewMedium) boolFieldView.findViewById(R.id.tv_hint);

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
        if (from.equalsIgnoreCase(Constants.DONATION) && !isEdit) {  // if it from Donation, its not need to editable,there for use a masking layout above the parent layout
            ll_mask.setVisibility(View.VISIBLE);
        } else {
            ll_mask.setVisibility(View.GONE);
        }
        llParentLayout.addView(boolFieldView);
        viewsList.put(boolField.getLabelName(), boolFieldView);

    }

    private void addListField(QuestionnaireListModel listModel) {

        View listFieldView = getLayoutInflater().inflate(R.layout.list_fielditem, null, false);

        LinearLayout ll_mask = (LinearLayout) listFieldView.findViewById(R.id.ll_mask);
        CustomTextViewSemiBold tvQuestionName = (CustomTextViewSemiBold) listFieldView.findViewById(R.id.tv_questionName);
        CustomTextViewBold tvTextFieldManditory = (CustomTextViewBold) listFieldView.findViewById(R.id.tv_manditory);
        RecyclerView rvCheckBoxes = (RecyclerView) listFieldView.findViewById(R.id.rv_checkBoxes);
        CustomTextViewMedium tvHint = (CustomTextViewMedium) listFieldView.findViewById(R.id.tv_hint);

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
        CheckBoxAdapter checkBoxAdapter = new CheckBoxAdapter(checkBoxList, mContext);
        rvCheckBoxes.setAdapter(checkBoxAdapter);
        if (from.equalsIgnoreCase(Constants.DONATION) && !isEdit) {  // if it from Donation, its not need to editable,there for use a masking layout above the parent layout
            ll_mask.setVisibility(View.VISIBLE);
        } else {
            ll_mask.setVisibility(View.GONE);
        }

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

            Dialog dialog = new Dialog(UpdateQuestionnaire.this);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.camera_options);
            dialog.getWindow().getAttributes().windowAnimations = R.style.slidingUpAndDown;
            DisplayMetrics metrics = UpdateQuestionnaire.this.getResources().getDisplayMetrics();
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
                        String orgFilePath = getRealPathFromURI(uri, this);
                        String filepath = "";//default fileName

                        String mimeType = mContext.getContentResolver().getType(uri);
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
                        }

                        View fileUploadView = viewsList.get(qLabelName);
                        RecyclerView rvFiles = (RecyclerView) fileUploadView.findViewById(R.id.rv_files);
                        CustomTextViewMedium tvSupportedTypes = (CustomTextViewMedium) fileUploadView.findViewById(R.id.tv_supportedTypes);
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
                            if (orgFilePath == null) {
                                orgFilePath = getFilePathFromURI(mContext, imageUri, extension);
                            }

                            View fileUploadView = viewsList.get(qLabelName);
                            RecyclerView rvFiles = (RecyclerView) fileUploadView.findViewById(R.id.rv_files);
                            CustomTextViewMedium tvSupportedTypes = (CustomTextViewMedium) fileUploadView.findViewById(R.id.tv_supportedTypes);
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
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                String path = saveImage(bitmap);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                if (path != null) {
                    mImageUri = Uri.parse(path);

                    View fileUploadView = viewsList.get(qLabelName);
                    RecyclerView rvFiles = (RecyclerView) fileUploadView.findViewById(R.id.rv_files);
                    FilesAdapter filesAdapter = (FilesAdapter) rvFiles.getAdapter();

                    fileObject.setImagePath(path);
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
                        if (orgFilePath == null) {
                            orgFilePath = getFilePathFromURI(mContext, uri, extension);
                        }

                        singleFilePath = orgFilePath;
                        View fileUploadView = viewsList.get(qLabelName);
                        ImageView ivSingleFile = (ImageView) fileUploadView.findViewById(R.id.iv_file);
                        CustomTextViewMedium tvPath = (CustomTextViewMedium) fileUploadView.findViewById(R.id.tv_path);
                        CustomTextViewMedium tvSupportedTypes = (CustomTextViewMedium) fileUploadView.findViewById(R.id.tv_supportedTypes);
                        ImageView ivClose = (ImageView) fileUploadView.findViewById(R.id.iv_close);
                        ivClose.setVisibility(View.VISIBLE);
                        tvPath.setText(orgFilePath);

                        if (tvSupportedTypes.getText().toString().contains(extension)) {

                            if (orgFilePath != null) {

                                if (orgFilePath.substring(orgFilePath.lastIndexOf(".") + 1).equals("pdf")) {

                                    ivSingleFile.setImageDrawable(getResources().getDrawable(R.drawable.pdfs));

                                } else if (Arrays.asList(videoFormats).contains(extension)) {

                                    ivSingleFile.setImageDrawable(mContext.getResources().getDrawable(R.drawable.video_icon));

                                } else if (orgFilePath.substring(orgFilePath.lastIndexOf(".") + 1).equals("mp3")) {

                                    ivSingleFile.setImageDrawable(mContext.getResources().getDrawable(R.drawable.audio_icon));

                                } else {
                                    ivSingleFile.setImageBitmap(BitmapFactory.decodeFile(orgFilePath));
                                }

                            }
                        } else {

                            Toast.makeText(mContext, "File type not supported", Toast.LENGTH_SHORT).show();
                        }


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
                            if (orgFilePath == null) {
                                orgFilePath = getFilePathFromURI(mContext, imageUri, extension);
                            }

                            singleFilePath = orgFilePath;
                            View fileUploadView = viewsList.get(qLabelName);
                            ImageView ivSingleFile = (ImageView) fileUploadView.findViewById(R.id.iv_file);
                            CustomTextViewMedium tvPath = (CustomTextViewMedium) fileUploadView.findViewById(R.id.tv_path);
                            CustomTextViewMedium tvSupportedTypes = (CustomTextViewMedium) fileUploadView.findViewById(R.id.tv_supportedTypes);
                            ImageView ivClose = (ImageView) fileUploadView.findViewById(R.id.iv_close);
                            ivClose.setVisibility(View.VISIBLE);
                            tvPath.setText(orgFilePath);

                            if (tvSupportedTypes.getText().toString().contains(extension)) {

                                if (orgFilePath != null) {

                                    if (orgFilePath.substring(orgFilePath.lastIndexOf(".") + 1).equals("pdf")) {

                                        ivSingleFile.setImageDrawable(getResources().getDrawable(R.drawable.pdfs));

                                    } else if (Arrays.asList(videoFormats).contains(extension)) {

                                        ivSingleFile.setImageDrawable(mContext.getResources().getDrawable(R.drawable.video_icon));

                                    } else if (orgFilePath.substring(orgFilePath.lastIndexOf(".") + 1).equals("mp3")) {

                                        ivSingleFile.setImageDrawable(mContext.getResources().getDrawable(R.drawable.audio_icon));

                                    } else {
                                        ivSingleFile.setImageBitmap(BitmapFactory.decodeFile(orgFilePath));
                                    }
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
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                String path = saveImage(bitmap);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                if (path != null) {
                    mImageUri = Uri.parse(path);

                    singleFilePath = path;
                    View fileUploadView = viewsList.get(qLabelName);
                    ImageView ivSingleFile = (ImageView) fileUploadView.findViewById(R.id.iv_file);
                    ImageView ivClose = (ImageView) fileUploadView.findViewById(R.id.iv_close);
                    CustomTextViewMedium tvPath = (CustomTextViewMedium) fileUploadView.findViewById(R.id.tv_path);
                    ivSingleFile.setImageBitmap(BitmapFactory.decodeFile(path));
                    ivClose.setVisibility(View.VISIBLE);
                    tvPath.setText(path);

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

            Dialog dialog = new Dialog(UpdateQuestionnaire.this);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.camera_options);
            dialog.getWindow().getAttributes().windowAnimations = R.style.slidingUpAndDown;
            DisplayMetrics metrics = UpdateQuestionnaire.this.getResources().getDisplayMetrics();
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

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "IMG_" + System.currentTimeMillis(), null);
        return Uri.parse(path);
    }

    public String getPathFromUri(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
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
        Cursor cursor = mContext.getContentResolver().query(contentUri, projection, null, null, null);
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

    public static float getImageSize(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
            cursor.moveToFirst();
            float imageSize = cursor.getLong(sizeIndex);
            cursor.close();
            return imageSize / (1024f * 1024f); // returns size in bytes
        }
        return 0;
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
        CustomTextViewSemiBold tvDate = (CustomTextViewSemiBold) numberFieldView.findViewById(R.id.tv_date);
        String selectedDate = dateParser.format(mCalender.getTime());
        tvDate.setText(selectedDate);
    }

    public void openPdf(Context context, String path) {
        File file = new File(path);
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/pdf");
            PackageManager pm = context.getPackageManager();
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setType("application/pdf");
            Intent openInChooser = Intent.createChooser(intent, "Choose");
            List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
            if (resInfo.size() > 0) {
                try {
                    context.startActivity(openInChooser);
                } catch (Throwable throwable) {
                    Toast.makeText(context, "PDF apps are not installed", Toast.LENGTH_SHORT).show();
                    // PDF apps are not installed
                }
            } else {
                Toast.makeText(context, "PDF apps are not installed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openOnlinePdf(Context mContext, String filePath) {

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(filePath));
        startActivity(browserIntent);
    }

    public static String getMimeType(String path) {
        String extension = path.substring(path.lastIndexOf("."));
        String mimeTypeMap = MimeTypeMap.getFileExtensionFromUrl(extension);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mimeTypeMap);
        return mimeType;
    }
}