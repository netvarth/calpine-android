package com.jaldeeinc.jaldee.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
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
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.jaldeeinc.jaldee.Interface.IDocumentType;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.DetailFileImageAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomEditTextRegular;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.MultipleSelectionSpinner;
import com.jaldeeinc.jaldee.model.ShoppingListModel;
import com.jaldeeinc.jaldee.response.Questionnaire;
import com.jaldeeinc.jaldee.response.Questions;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuestionareActivity extends AppCompatActivity implements IDocumentType, DatePickerDialog.OnDateSetListener {


    private Context mContext;
    RadioGroup mRgAllButtons;
    private IDocumentType iDocumentType;
    //files related
    private static final String IMAGE_DIRECTORY = "/Jaldee" + "";
    String[] imgExtsSupported = new String[]{"jpg", "jpeg", "png"};
    String[] fileExtsSupported = new String[]{"jpg", "jpeg", "png", "pdf"};
    private int GALLERY_FOR_ONE = 1, CAMERA_FOR_ONE = 2;
    private Uri mImageUri;
    File f;
    String singleFilePath, fileName;
    Bitmap bitmap;
    LinearLayout llFileName;
    CustomTextViewMedium tvSingleFileName;
    ImageView ivSingleFileClose;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionare);
        mContext = QuestionareActivity.this;
        iDocumentType = this;

        getQuestionnaire();
    }

    private void getQuestionnaire() {

        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<Questionnaire> call = apiService.getAppointmentQuestions("36091", "0", 125976);
        call.enqueue(new Callback<Questionnaire>() {
            @Override
            public void onResponse(Call<Questionnaire> call, Response<Questionnaire> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);
                    Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        Questionnaire questionnaire = response.body();

                        if (questionnaire != null) {

                            Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();

                            if (questionnaire.getQuestionsList() != null) {
                                updateUI(questionnaire.getQuestionsList());
                            }
                        }
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

    private void updateUI(ArrayList<Questions> questionsList) {

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.container);

        CustomTextViewSemiBold tvOne = new CustomTextViewSemiBold(this);
        tvOne.setText("Please provide us with a little more information to complete your booking");
        tvOne.setGravity(Gravity.CENTER);
        tvOne.setTextColor(mContext.getResources().getColor(R.color.dark_black));
        tvOne.setTextSize(15);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(15, 60, 15, 50);
        tvOne.setLayoutParams(params);
        linearLayout.addView(tvOne);

        for (Questions question : questionsList) {

            if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("fileUpload")) {

                CustomTextViewSemiBold txtView = new CustomTextViewSemiBold(this);
                txtView.setText(question.getGetQuestion().getLabel());
                txtView.setId(R.id.id1);//need for better use
                txtView.setTextColor(mContext.getResources().getColor(R.color.title_color));
                txtView.setTextSize(15);
                LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                textParams.setMargins(35, 35, 35, 0);
                txtView.setLayoutParams(textParams);
                linearLayout.addView(txtView);

                if (question.getGetQuestion().getFileProperties() != null && question.getGetQuestion().getFileProperties().getAllowedDocuments() != null && question.getGetQuestion().getFileProperties().getAllowedDocuments().size() > 1) {

                    MultipleSelectionSpinner multipleSelectionSpinner = new MultipleSelectionSpinner(this, iDocumentType, R.id.id2);
                    multipleSelectionSpinner.setItems(question.getGetQuestion().getFileProperties().getAllowedDocuments());
                    LinearLayout.LayoutParams spinnerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    spinnerParams.setMargins(35, 15, 35, 0);
                    multipleSelectionSpinner.setLayoutParams(spinnerParams);
                    multipleSelectionSpinner.setId(R.id.id2);
                    multipleSelectionSpinner.setBackground(mContext.getResources().getDrawable(R.drawable.new_spinner_bg));
                    linearLayout.addView(multipleSelectionSpinner);


//                    multipleSelectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//
//                            if (i > 0) {
//                                CardView cvFile = (CardView) findViewById(30 + (i -1));
//                                cvFile.setVisibility(View.VISIBLE);
//                            }
//                        }
//
//                        @Override
//                        public void onNothingSelected(AdapterView<?> adapterView) {
//
//                        }
//                    });

                    for (int i = 0; i < question.getGetQuestion().getFileProperties().getAllowedDocuments().size(); i++) {

                        CardView cardView = new CardView(this);
                        CustomTextViewSemiBold tvUpload = new CustomTextViewSemiBold(this);
                        String fileName = question.getGetQuestion().getFileProperties().getAllowedDocuments().get(i);
                        tvUpload.setText("Upload " + fileName);
                        tvUpload.setTextSize(15);
                        tvUpload.setTextColor(mContext.getResources().getColor(R.color.title_color));
                        tvUpload.setId(i + 20);
                        LinearLayout.LayoutParams upload = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        upload.setMargins(35, 15, 35, 15);
                        tvUpload.setLayoutParams(upload);
                        tvUpload.setGravity(Gravity.CENTER);
                        LinearLayout.LayoutParams cardUpload = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        cardUpload.setMargins(35, 15, 35, 15);
                        cardView.setLayoutParams(cardUpload);
                        cardView.setCardBackgroundColor(getResources().getColor(R.color.spinnerbg));
                        cardView.addView(tvUpload);
                        cardView.setId(i + 30);
                        cardView.setVisibility(View.GONE);
                        linearLayout.addView(cardView);

                    }


                } else if (question.getGetQuestion().getFileProperties() != null && question.getGetQuestion().getFileProperties().getAllowedDocuments() != null && question.getGetQuestion().getFileProperties().getAllowedDocuments().size() == 1) {

                    LinearLayout fileLayout = new LinearLayout(this);
                    fileLayout.setOrientation(LinearLayout.HORIZONTAL);
//                    LinearLayout.LayoutParams Horizontallinearlayoutlayoutparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//
//                    setContentView(fileLayout, Horizontallinearlayoutlayoutparams);
                    CardView cardView = new CardView(this);
                    CustomTextViewSemiBold tvUpload = new CustomTextViewSemiBold(this);
                    tvUpload.setText("Upload file");
                    tvUpload.setTextSize(15);
                    tvUpload.setTextColor(mContext.getResources().getColor(R.color.title_color));
                    tvUpload.setId(R.id.id3);
                    LinearLayout.LayoutParams upload = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    upload.setMargins(35, 15, 35, 15);
                    tvUpload.setLayoutParams(upload);
                    tvUpload.setGravity(Gravity.CENTER);
                    LinearLayout.LayoutParams cardUpload = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    cardUpload.setMargins(35, 15, 35, 15);
                    cardView.setLayoutParams(cardUpload);
                    cardView.setCardBackgroundColor(getResources().getColor(R.color.spinnerbg));
                    cardView.addView(tvUpload);
                    fileLayout.addView(cardView);

                    cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            openGalleryForOneImage();
                        }
                    });


                    LinearLayout fileName = new LinearLayout(this);
                    fileName.setId(R.id.id13);
                    CustomTextViewMedium tvFileName = new CustomTextViewMedium(this);
                    tvFileName.setText("fileName");
                    tvFileName.setId(R.id.id11);
                    LinearLayout.LayoutParams fileNameUpload = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    fileNameUpload.setMargins(35, 15, 35, 15);
                    tvFileName.setLayoutParams(fileNameUpload);
                    tvFileName.setGravity(Gravity.CENTER);
                    fileName.addView(tvFileName);

                    ImageView ivClose = new ImageView(this);
                    ivClose.setImageDrawable(getResources().getDrawable(R.drawable.close));
                    LinearLayout.LayoutParams closeIcon = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    closeIcon.setMargins(35, 15, 35, 15);
                    ivClose.setLayoutParams(closeIcon);
                    ivClose.setId(R.id.id12);

                    fileName.addView(ivClose);
                    fileLayout.addView(fileName);
                    linearLayout.addView(fileLayout);

                    llFileName = (LinearLayout) findViewById(R.id.id13);
                    llFileName.setVisibility(View.GONE);
                    tvSingleFileName = (CustomTextViewMedium) findViewById(R.id.id11);
                    ivSingleFileClose = (ImageView) findViewById(R.id.id12);

                    ivSingleFileClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            singleFilePath = "";
                            tvSingleFileName.setText("");
                            llFileName.setVisibility(View.GONE);
                        }
                    });

                }

                LinearLayout dividerOne = new LinearLayout(this);
                dividerOne.setBackgroundColor(getResources().getColor(R.color.black));
                LinearLayout.LayoutParams dividerOneParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
                dividerOneParams.setMargins(35, 15, 35, 0);
                dividerOne.setLayoutParams(dividerOneParams);
                linearLayout.addView(dividerOne);

            } else if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("list")) {

                CustomTextViewSemiBold txtView = new CustomTextViewSemiBold(this);
                txtView.setText(question.getGetQuestion().getLabel());
                txtView.setId(4);//need for better use
                txtView.setTextColor(mContext.getResources().getColor(R.color.title_color));
                txtView.setTextSize(15);
                LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                textParams.setMargins(35, 35, 35, 0);
                txtView.setLayoutParams(textParams);
                linearLayout.addView(txtView);

                ArrayList values = (ArrayList) question.getGetQuestion().getLabelValues();

                for (int i = 0; i < values.size(); i++) {
                    CheckBox rdbtn = new CheckBox(this);
                    rdbtn.setId(40 + i);
                    rdbtn.setText(values.get(i).toString());
                    rdbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton cb, boolean b) {

//                            if (data.selectedLabels.size() > question.getGetQuestion().getListProperties().getMaxAnswers()) {

                            Log.e("$#$$#$$$", " Name " + ((CheckBox) cb).getText() + " Id is " + cb.getId());
//                            }
                        }
                    });
                    linearLayout.addView(rdbtn);
                }


                // underline
                LinearLayout dividerOne = new LinearLayout(this);
                dividerOne.setBackgroundColor(getResources().getColor(R.color.black));
                LinearLayout.LayoutParams dividerOneParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
                dividerOneParams.setMargins(35, 15, 35, 0);
                dividerOne.setLayoutParams(dividerOneParams);
                linearLayout.addView(dividerOne);

            } else if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("date")) {

                CustomTextViewSemiBold txtView = new CustomTextViewSemiBold(this);
                txtView.setText(question.getGetQuestion().getLabel());
                txtView.setId(5);//need for better use
                txtView.setTextColor(mContext.getResources().getColor(R.color.title_color));
                txtView.setTextSize(15);
                LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                textParams.setMargins(35, 35, 35, 0);
                txtView.setLayoutParams(textParams);
                linearLayout.addView(txtView);

                LinearLayout dateLayout = new LinearLayout(this);
                CustomTextViewSemiBold text = new CustomTextViewSemiBold(this);
                text.setText("Select Date");
                text.setTextSize(15);
                text.setId(R.id.id6);
                text.setTextColor(mContext.getResources().getColor(R.color.title_color));
                LinearLayout.LayoutParams calenderText = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                calenderText.setMargins(35, 25, 25, 25);
                text.setLayoutParams(calenderText);
                ImageView imageView = new ImageView(this);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.icon_calendar));
                imageView.setPadding(50, 0, 35, 0);

                dateLayout.addView(text);
                dateLayout.addView(imageView);
                dateLayout.setBackgroundColor(getResources().getColor(R.color.spinnerbg));
                LinearLayout.LayoutParams calenderLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                calenderLayout.setMargins(35, 35, 35, 0);
                dateLayout.setGravity(Gravity.CENTER);
                dateLayout.setId(R.id.id10);
                dateLayout.setLayoutParams(calenderLayout);
                linearLayout.addView(dateLayout);

                // underline
                LinearLayout dividerOne = new LinearLayout(this);
                dividerOne.setBackgroundColor(getResources().getColor(R.color.black));
                LinearLayout.LayoutParams dividerOneParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
                dividerOneParams.setMargins(35, 15, 35, 0);
                dividerOne.setLayoutParams(dividerOneParams);
                linearLayout.addView(dividerOne);

                dateLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        com.jaldeeinc.jaldee.custom.DatePicker mDatePickerDialogFragment;
                        mDatePickerDialogFragment = new com.jaldeeinc.jaldee.custom.DatePicker();
                        mDatePickerDialogFragment.show(getSupportFragmentManager(), "DATE PICK");
                    }
                });


            } else if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("plainText")) {

                CustomTextViewSemiBold txtView = new CustomTextViewSemiBold(this);
                txtView.setText(question.getGetQuestion().getLabel());
                txtView.setId(R.id.id7);//need for better use
                txtView.setTextColor(mContext.getResources().getColor(R.color.title_color));
                txtView.setTextSize(15);
                LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                textParams.setMargins(35, 35, 35, 0);
                txtView.setLayoutParams(textParams);
                linearLayout.addView(txtView);

                CustomEditTextRegular editText = new CustomEditTextRegular(this);
                editText.setBackground(getResources().getDrawable(R.drawable.edittext));
                editText.setHeight(120);
                editText.setPadding(15, 0, 0, 0);
                editText.setId(R.id.id8);
                LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                editTextParams.setMargins(35, 35, 35, 0);
                editText.setLayoutParams(editTextParams);
                linearLayout.addView(editText);


                // underline
                LinearLayout dividerOne = new LinearLayout(this);
                dividerOne.setBackgroundColor(getResources().getColor(R.color.black));
                LinearLayout.LayoutParams dividerOneParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
                dividerOneParams.setMargins(35, 15, 35, 0);
                dividerOne.setLayoutParams(dividerOneParams);
                linearLayout.addView(dividerOne);

            } else if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("bool")) {

                CustomTextViewSemiBold txtView = new CustomTextViewSemiBold(this);
                txtView.setText(question.getGetQuestion().getLabel());
                txtView.setId(9);//need for better use
                txtView.setTextColor(mContext.getResources().getColor(R.color.title_color));
                txtView.setTextSize(15);
                LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                textParams.setMargins(35, 35, 35, 0);
                txtView.setLayoutParams(textParams);
                linearLayout.addView(txtView);

                ArrayList values = (ArrayList) question.getGetQuestion().getLabelValues();


// Create RadioButton Dynamically
                RadioButton radioButton1 = new RadioButton(this);
                radioButton1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                radioButton1.setText(values.get(0).toString());
                radioButton1.setId(0);

                RadioButton radioButton2 = new RadioButton(this);
                radioButton2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                radioButton2.setText(values.get(1).toString());
                radioButton2.setId(1);

                RadioGroup radioGroup = new RadioGroup(this);
                LinearLayout.LayoutParams radioParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                radioParams.setMargins(35, 0, 0, 0);
                radioGroup.setLayoutParams(radioParams);

                radioGroup.addView(radioButton1);
                radioGroup.addView(radioButton2);
                linearLayout.addView(radioGroup);

                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
//                            String text = getString(R.string.you_selected);
//                            text += " " + getString((checkedId == 0) ? R.string.male : R.string.female);
//                            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                    }
                });


                // underline
                LinearLayout dividerOne = new LinearLayout(this);
                dividerOne.setBackgroundColor(getResources().getColor(R.color.black));
                LinearLayout.LayoutParams dividerOneParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
                dividerOneParams.setMargins(35, 15, 35, 0);
                dividerOne.setLayoutParams(dividerOneParams);
                linearLayout.addView(dividerOne);


            }
        }

    }

    private void openGalleryForOneImage() {

        try {

            Dialog dialog = new Dialog(QuestionareActivity.this);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.camera_options);
            dialog.getWindow().getAttributes().windowAnimations = R.style.slidingUpAndDown;
            DisplayMetrics metrics = QuestionareActivity.this.getResources().getDisplayMetrics();
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
        //   mTxvBuy.setEnabled(true);

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

                        singleFilePath = orgFilePath;
                        fileName = data.getData().getLastPathSegment();
                        llFileName.setVisibility(View.VISIBLE);
                        tvSingleFileName.setText(fileName);


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
                            fileName = data.getData().getLastPathSegment();
                            llFileName.setVisibility(View.VISIBLE);
                            tvSingleFileName.setText(fileName);
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

                    singleFilePath = path;
                    fileName = getFileName(mImageUri);
                    llFileName.setVisibility(View.VISIBLE);
                    tvSingleFileName.setText(fileName);

                }
                try {
                    bytes.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
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
    public void onOkClick(int spinnerId) {

//        MultipleSelectionSpinner spinner = (MultipleSelectionSpinner) findViewById(spinnerId);
//        spinner.getSelectedItems()
//
//        CardView cvFile = (CardView) findViewById(30 + (i -1));
//        cvFile.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        SimpleDateFormat dateParser = new SimpleDateFormat("dd-MM-yyyy",
                Locale.ENGLISH);
        Calendar mCalender = Calendar.getInstance();
        mCalender.set(Calendar.YEAR, year);
        mCalender.set(Calendar.MONTH, month);
        mCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String selectedDate = dateParser.format(mCalender.getTime()).toString();

        CustomTextViewSemiBold tvDate = (CustomTextViewSemiBold) findViewById(R.id.id6);
        tvDate.setText(selectedDate);

    }
}