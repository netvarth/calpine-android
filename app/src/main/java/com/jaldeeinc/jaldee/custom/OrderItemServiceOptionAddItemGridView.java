package com.jaldeeinc.jaldee.custom;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaldeeinc.jaldee.Interface.IDataGrid;
import com.jaldeeinc.jaldee.Interface.IFilesInterface;
import com.jaldeeinc.jaldee.Interface.IServiceOption;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.DetailPageItemsAdapter;
import com.jaldeeinc.jaldee.adapter.FilesAdapter;
import com.jaldeeinc.jaldee.adapter.ItemsAdapter;
import com.jaldeeinc.jaldee.adapter.SelectedItemsAdapter;
import com.jaldeeinc.jaldee.adapter.ServiceOptionDataGridAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.model.CartItemModel;
import com.jaldeeinc.jaldee.model.DataGrid;
import com.jaldeeinc.jaldee.model.QuestionnairInpt;
import com.jaldeeinc.jaldee.model.QuestionnaireCheckbox;
import com.jaldeeinc.jaldee.response.CatalogItem;
import com.jaldeeinc.jaldee.response.DataGridColumns;
import com.jaldeeinc.jaldee.response.GetQuestion;
import com.jaldeeinc.jaldee.response.Questionnaire;
import com.jaldeeinc.jaldee.response.Questions;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderItemServiceOptionAddItemGridView extends LinearLayout implements IDataGrid, IServiceOption {
    private Context mContext;
    IDataGrid iDataGrid;
    TextView tvQuestionName, tvHint;
    TextView tvError;
    TextView tvManditory;
    LinearLayout llParentLayout;
    private HashMap<String, View> viewsList = new HashMap<>();
    public ArrayList<DataGrid> gridDataList = new ArrayList<>();

    IServiceOption iServiceOptionListOptionChange;
    Questions question;
    String selectedItemName;
    private int GALLERY = 3, CAMERA = 4;

    public OrderItemServiceOptionAddItemGridView(Context context, Questions questn, String selectedItemName, IServiceOption iServiceOptionListOptionChange) {
        super(context);
        this.mContext = context;
        iDataGrid = this;
        this.question = questn;
        this.selectedItemName = selectedItemName;
        this.iServiceOptionListOptionChange = iServiceOptionListOptionChange;
        initView();

    }
    private void initView() {

        inflate(mContext, R.layout.order_service_option_gridview, this);
        tvQuestionName = findViewById(R.id.tv_questionName);
        tvManditory = findViewById(R.id.tv_manditory);
        tvHint = findViewById(R.id.tv_item_Hint);
        llParentLayout = findViewById(R.id.ll_mainLayout);
        if (tvQuestionName != null && question.getGetQuestion().getLabel() != null) {
            tvQuestionName.setText(question.getGetQuestion().getLabel());
        }
        if (tvManditory != null) {
            //tvManditory.setText(question.getGetQuestion().isMandatory());
        }
        if (question.getGetQuestion().getHint() != null && !question.getGetQuestion().getHint().trim().isEmpty()) {
            String hint = "";
            hint = (hint == null) ? "" : hint;
            tvHint.setText(hint);
            if (hint.trim().equalsIgnoreCase("")) {
                tvHint.setVisibility(View.GONE);
            } else {
                tvHint.setVisibility(View.VISIBLE);
            }
        }
        createDataGridListQuestionnaire(question.getGetQuestion());
    }
    private KeyPairBoolData fileObject = new KeyPairBoolData();
    private String qLabelName = "";
    private void createDataGridListQuestionnaire(GetQuestion objQuestion) {

        try {

            ArrayList<DataGridColumns> questionsList = objQuestion.getDataGridListProperties().getGridListColumns();

            /*String ja = questionsList.get(0).getListProperties().getValues().get(0).toString();
            if (ja.trim() != null && !ja.trim().isEmpty()) {
                selectedItemName = ja.trim();
            }*/

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
                            fileObject = iServiceOptionListOptionChange.openImageOptions(fileObject, qLabelName, viewsList);
                            int j = 0+8;
                            j=j+8;

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
                    }
                    QuestionnaireListView listView = new QuestionnaireListView(mContext, iServiceOptionListOptionChange);
                    listView.setServiceOptionGridQuestionData(question, questionnaireCheckboxes, question.getListProperties().getMaxAnswerable());

                    llParentLayout.addView(listView);
                    viewsList.put(question.getColumnId(), listView);
                }
            }
            //aLLayouts.add(llParentLayout);
            //llParentLayout.addView(llParentLayout);
            //viewsList.put(objQuestion.getLabelName(), llParentLayout);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //calculatePrice();
    }
    public void updateDataGrid(DataGrid dataGridObj, int position) {

        //rvDataTable.setVisibility(VISIBLE);

        if (position >= 0) {
            //gridDataList.set(position, dataGridObj);
            gridDataList.add(dataGridObj);
        } else {
            gridDataList.add(dataGridObj);
        }

//        rvDataTable.setLayoutManager(new LinearLayoutManager(context));
//        serviceOptionDataGridAdapter = new ServiceOptionDataGridAdapter(gridDataList, context, iDataGrid, isEdit);
//        rvDataTable.setAdapter(serviceOptionDataGridAdapter);

    }
    public ArrayList<DataGrid> getGridDataList() {

        return (gridDataList == null) ? new ArrayList<>() : gridDataList;
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
    public LinearLayout getllParentLayout() {
        return llParentLayout;
    }
        @Override
    public void onEditClick(DataGrid gridObj, int position) {

    }

    @Override
    public void onEditClick(Questionnaire qnr, QuestionnairInpt answerGridObj, int position, CartItemModel itemDetails, boolean isEdit) {

    }

    @Override
    public void onDeleteClick(int position) {

    }

    @Override
    public void onAddClick(int position) {

    }

    @Override
    public void onAddClick(CatalogItem catalogItem, ItemsAdapter.ViewHolder viewHolder, boolean isDecreaseQty, int newValue) {

    }

    @Override
    public void onAddClick(CartItemModel cartItemModel, SelectedItemsAdapter.ViewHolder viewHolder, boolean isDecreaseQty, int newValue) {

    }

    @Override
    public void onAddClick(CatalogItem catalogItem, DetailPageItemsAdapter.ViewHolder viewHolder, boolean isDecreaseQty, int newValue) {

    }

    @Override
    public void onRemoveClick(int position, Questionnaire questionnaire, QuestionnairInpt answerLine, CartItemModel itemDetails) {

    }

    @Override
    public void updateTotalPrice() {
        iServiceOptionListOptionChange.updateTotalPrice();
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
//    private void openImageOptions() {
//
//        try {
//
//            Dialog dialog = new Dialog(mContext);
//            dialog.setCancelable(true);
//            dialog.setContentView(R.layout.camera_options);
//            dialog.getWindow().getAttributes().windowAnimations = R.style.slidingUpAndDown;
//            DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
//            int width = (int) (metrics.widthPixels * 1);
//            dialog.getWindow().setGravity(Gravity.BOTTOM);
//            dialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
//            LinearLayout llGallery = dialog.findViewById(R.id.ll_gallery);
//            LinearLayout llCamera = dialog.findViewById(R.id.ll_camera);
//
//            llCamera.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    openCamera();
//                    dialog.dismiss();
//                }
//            });
//
//            llGallery.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    openGallery();
//                    dialog.dismiss();
//                }
//            });
//
//            dialog.show();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void openCamera() {
//
//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//
//
//                    ((AppCompatActivity) mContext).requestPermissions(new String[]{
//                            Manifest.permission.CAMERA}, CAMERA);
//
//                    return;
//                } else {
//                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                    Intent cameraIntent = new Intent();
//                    cameraIntent.setType("image/*");
//                    cameraIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
//                    cameraIntent.setAction(Intent.ACTION_GET_CONTENT);
//                    startActivityForResult(intent, CAMERA);
//                }
//            } else {
//
//                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                Intent cameraIntent = new Intent();
//                cameraIntent.setType("image/*");
//                cameraIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
//                cameraIntent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(intent, CAMERA);
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    private void openGallery() {
//
//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if ((ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                    ((AppCompatActivity) mContext).requestPermissions(new String[]{
//                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, GALLERY);
//
//                    return;
//                } else {
//                    Intent intent = new Intent();
//                    intent.setType("*/*");
//                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
//                    intent.setAction(Intent.ACTION_GET_CONTENT);
//                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY);
//                }
//            } else {
//
//                Intent intent = new Intent();
//                intent.setType("*/*");
//                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY);
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == GALLERY) {
//            if (data != null) {
//                try {
//                    if (data.getData() != null) {
//                        Uri uri = data.getData();
//
//                        String mimeType = this.mContext.getContentResolver().getType(uri);
//                        String uriString = uri.toString();
//                        String extension = "";
//                        if (uriString.contains(".")) {
//                            extension = uriString.substring(uriString.lastIndexOf(".") + 1);
//                        }
//
//                        if (mimeType != null) {
//                            extension = mimeType.substring(mimeType.lastIndexOf("/") + 1);
//                        }
//                        File photoFile = null;
//
//                        try {
//                            // Creating file
//                            try {
//                                photoFile = Config.createFile(mContext, extension, true);
//                            } catch (IOException ex) {
//                                Toast.makeText(mContext, "Error occurred while creating the file", Toast.LENGTH_SHORT).show();
//
//                                // Log.d(TAG, "Error occurred while creating the file");
//                            }
//
//                            InputStream inputStream = mContext.getContentResolver().openInputStream(data.getData());
//                            FileOutputStream fileOutputStream = new FileOutputStream(photoFile);
//                            // Copying
//                            Config.copyStream(inputStream, fileOutputStream);
//                            fileOutputStream.close();
//                            inputStream.close();
//                        } catch (Exception e) {
//                            Toast.makeText(mContext, "onActivityResult: " + e.toString(), Toast.LENGTH_SHORT).show();
//
//                            //Log.d(TAG, "onActivityResult: " + e.toString());
//                        }
//                        String orgFilePath = photoFile.getAbsolutePath();
//
//                        if (orgFilePath == null) {
//                            orgFilePath = Config.getFilePathFromURI(mContext, uri, extension);
//                        }
//                        /*Uri uri = data.getData();
//                        String orgFilePath = getRealPathFromURI(uri, getActivity());
//                        String filepath = "";//default fileName
//
//                        String mimeType = getActivity().getContentResolver().getType(uri);
//                        String uriString = uri.toString();
//                        String extension = "";
//                        if (uriString.contains(".")) {
//                            extension = uriString.substring(uriString.lastIndexOf(".") + 1);
//                        }
//
//                        if (mimeType != null) {
//                            extension = mimeType.substring(mimeType.lastIndexOf("/") + 1);
//                        }
//                        if (orgFilePath == null) {
//                            orgFilePath = getFilePathFromURI(getContext(), uri, extension);
//                        }*/
//
//                        View fileUploadView = viewsList.get(qLabelName);
//                        RecyclerView rvFiles = (RecyclerView) fileUploadView.findViewById(R.id.rv_files);
//                        TextView tvSupportedTypes = fileUploadView.findViewById(R.id.tv_supportedTypes);
//                        FilesAdapter filesAdapter = (FilesAdapter) rvFiles.getAdapter();
//
//                        if (tvSupportedTypes.getText().toString().contains(extension)) {
//                            fileObject.setImagePath(orgFilePath);
//                            filesAdapter.updateFileObject(fileObject);
//                        } else {
//
//                            Toast.makeText(mContext, "File type not supported", Toast.LENGTH_SHORT).show();
//                        }
//
//
//                    } else if (data.getClipData() != null) {
//                        ClipData mClipData = data.getClipData();
//                        for (int i = 0; i < mClipData.getItemCount(); i++) {
//                            ClipData.Item item = mClipData.getItemAt(i);
//                            Uri uri = item.getUri();
//                            String mimeType = this.mContext.getContentResolver().getType(uri);
//                            String uriString = uri.toString();
//                            String extension = "";
//                            if (uriString.contains(".")) {
//                                extension = uriString.substring(uriString.lastIndexOf(".") + 1);
//                            }
//
//                            if (mimeType != null) {
//                                extension = mimeType.substring(mimeType.lastIndexOf("/") + 1);
//                            }
//                            File photoFile = null;
//
//                            try {
//                                // Creating file
//                                try {
//                                    photoFile = Config.createFile(mContext, extension, true);
//                                } catch (IOException ex) {
//                                    Toast.makeText(mContext, "Error occurred while creating the file", Toast.LENGTH_SHORT).show();
//
//                                    // Log.d(TAG, "Error occurred while creating the file");
//                                }
//
//                                InputStream inputStream = mContext.getContentResolver().openInputStream(data.getData());
//                                FileOutputStream fileOutputStream = new FileOutputStream(photoFile);
//                                // Copying
//                                Config.copyStream(inputStream, fileOutputStream);
//                                fileOutputStream.close();
//                                inputStream.close();
//                            } catch (Exception e) {
//                                Toast.makeText(mContext, "onActivityResult: " + e.toString(), Toast.LENGTH_SHORT).show();
//
//                                //Log.d(TAG, "onActivityResult: " + e.toString());
//                            }
//                            String orgFilePath = photoFile.getAbsolutePath();
//
//                            if (orgFilePath == null) {
//                                orgFilePath = Config.getFilePathFromURI(mContext, uri, extension);
//                            }
//
//                            View fileUploadView = viewsList.get(qLabelName);
//                            RecyclerView rvFiles = (RecyclerView) fileUploadView.findViewById(R.id.rv_files);
//                            TextView tvSupportedTypes = fileUploadView.findViewById(R.id.tv_supportedTypes);
//                            FilesAdapter filesAdapter = (FilesAdapter) rvFiles.getAdapter();
//
//                            if (tvSupportedTypes.getText().toString().contains(extension)) {
//                                fileObject.setImagePath(orgFilePath);
//                                filesAdapter.updateFileObject(fileObject);
//                            } else {
//                                Toast.makeText(mContext, "File type not supported", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//        } else if (requestCode == CAMERA) {
//            if (data != null && data.getExtras() != null) {
//                File photoFile = null;/////////
//                // ///////
//                try {//////////
//                    photoFile = Config.createFile(mContext, "png", true);//////////
//                } catch (IOException e) {/////////////
//                    e.printStackTrace();///////////
//                }///////////
//                Bitmap bitmap = (Bitmap) data.getExtras().get("data");/////////
//                try (FileOutputStream out = new FileOutputStream(photoFile)) {////////////
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance////////////
//                    // PNG is a lossless format, the compression factor (100) is ignored/////////
//                } catch (IOException e) {////////////
//                    e.printStackTrace();///////////
//                }////////
//                String path = photoFile.getAbsolutePath();////////
//                /*Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//                String path = saveImage(bitmap);
//                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);*/
//                if (path != null) {
//                    mImageUri = Uri.parse(path);
//
//                    View fileUploadView = viewsList.get(qLabelName);
//                    RecyclerView rvFiles = (RecyclerView) fileUploadView.findViewById(R.id.rv_files);
//                    FilesAdapter filesAdapter = (FilesAdapter) rvFiles.getAdapter();
//
//                    fileObject.setImagePath(path);
//                    filesAdapter.updateFileObject(fileObject);
//                }
//                /*try {
//                    bytes.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }*/
//            }
//        }
//    }
//public void openSomeActivityForResult() {
//    Intent intent = new Intent(this, SomeActivity.class);
//    someActivityResultLauncher.launch(intent);
//}
//
//    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
//    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            new ActivityResultCallback<ActivityResult>() {
//                @Override
//                public void onActivityResult(ActivityResult result) {
//                    if (result.getResultCode() == Activity.RESULT_OK) {
//                        // There are no request codes
//                        Intent data = result.getData();
//                        doSomeOperations();
//                    }
//                }
//            });
}
