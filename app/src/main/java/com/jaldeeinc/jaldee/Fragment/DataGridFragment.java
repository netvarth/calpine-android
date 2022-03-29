package com.jaldeeinc.jaldee.Fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.jaldeeinc.jaldee.Interface.IFilesInterface;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.adapter.FilesAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.KeyPairBoolData;
import com.jaldeeinc.jaldee.custom.QuestionnaireBoolView;
import com.jaldeeinc.jaldee.custom.QuestionnaireDateView;
import com.jaldeeinc.jaldee.custom.QuestionnaireFileUploadView;
import com.jaldeeinc.jaldee.custom.QuestionnaireGridView;
import com.jaldeeinc.jaldee.custom.QuestionnaireListView;
import com.jaldeeinc.jaldee.custom.QuestionnaireNumberView;
import com.jaldeeinc.jaldee.custom.QuestionnaireTextView;
import com.jaldeeinc.jaldee.model.DataGrid;
import com.jaldeeinc.jaldee.model.GridColumnAnswerLine;
import com.jaldeeinc.jaldee.response.DataGridColumns;
import com.jaldeeinc.jaldee.response.GetQuestion;
import com.jaldeeinc.jaldee.utils.SharedPreference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DataGridFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DataGridFragment extends Fragment {

    private static final String GRID_QUESTION = "gridQuestion";
    private static final String GRID_ANSWERS = "gridAnswers";
    private static final String POSITION = "position";

    private LinearLayout llParentLayout;
    private CardView cvBack, cvAdd;
    QuestionnaireGridView gridView;
    int position = -1;

    private int GALLERY = 3, CAMERA = 4;
    private static final String IMAGE_DIRECTORY = "/Jaldee" + "";
    String[] videoFormats = new String[]{"wmv", "mp4", "webm", "flw", "mov", "avi", ".wmv", ".mp4", ".webm", ".flw", ".mov", ".avi"};

    private GetQuestion mQuestion = new GetQuestion();
    private DataGrid mAnswer = new DataGrid();
    private KeyPairBoolData fileObject = new KeyPairBoolData();
    private String qLabelName = "";
    private HashMap<String, View> viewsList = new HashMap<>();

    private Uri mImageUri;
    File f;
    File file;
    String singleFilePath = "";
    Bitmap bitmap;


    public DataGridFragment() {
        // Required empty public constructor
    }


    public static DataGridFragment newInstance(String question) {
        DataGridFragment fragment = new DataGridFragment();
        Bundle args = new Bundle();
        args.putString(GRID_QUESTION, question);
        args.putString(GRID_ANSWERS, null);
        args.putInt(POSITION, -1);
        fragment.setArguments(args);
        return fragment;
    }

    public static DataGridFragment newInstance(String question, String answer, int position) {
        DataGridFragment fragment = new DataGridFragment();
        Bundle args = new Bundle();
        args.putString(GRID_QUESTION, question);
        args.putString(GRID_ANSWERS, answer);
        args.putInt(POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(POSITION, -1);
        }

        String question = SharedPreference.getInstance(getContext()).getStringValue(Constants.QUESTION, "");
        String answer = SharedPreference.getInstance(getContext()).getStringValue(Constants.ANSWER, null);

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_data_grid, container, false);

        llParentLayout = view.findViewById(R.id.ll_parentLayout);
        cvBack = view.findViewById(R.id.cv_back);
        cvAdd = view.findViewById(R.id.cv_add);

        if (mQuestion != null) {

            if (mAnswer == null) {

                for (DataGridColumns question : mQuestion.getDataGridProperties().getGridList()) {

                    question.setAnswer(null);
                }
                createQuestionnaire(mQuestion);
            } else {

                for (DataGridColumns question : mQuestion.getDataGridProperties().getGridList()) {

                    for (GridColumnAnswerLine answer : mAnswer.getDataGridColumn()) {

                        if (question.getColumnId().equalsIgnoreCase(answer.getColumnId())) {

                            question.setAnswer(answer);
                        }
                    }

                }
                createQuestionnaire(mQuestion);
            }
        }

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    SharedPreference.getInstance(getContext()).setValue(Constants.QUESTION, "");
                    SharedPreference.getInstance(getContext()).setValue(Constants.ANSWER, "");

                    getFragmentManager().popBackStack("DataGrid", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    return true;
                }
                return false;
            }
        });

        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreference.getInstance(getContext()).setValue(Constants.QUESTION, "");
                SharedPreference.getInstance(getContext()).setValue(Constants.ANSWER, "");

                getFragmentManager().popBackStack("DataGrid", FragmentManager.POP_BACK_STACK_INCLUSIVE);

            }
        });

        cvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addDataToQuestionnaireGrid();
            }
        });


        return view;
    }

    public void setGridView(QuestionnaireGridView gView) {

        gridView = gView;

    }

    private void createQuestionnaire(GetQuestion objQuestion) {

        try {

            ArrayList<DataGridColumns> questionsList = objQuestion.getDataGridProperties().getGridList();

            for (DataGridColumns question : questionsList) {

                if (question.getDataType().equalsIgnoreCase("fileUpload")) {

                    QuestionnaireFileUploadView fileUploadView = new QuestionnaireFileUploadView(getContext());
                    fileUploadView.setGridQuestionData(question);

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

                    QuestionnaireTextView textView = new QuestionnaireTextView(getContext());
                    textView.setGridQuestionData(question);

                    llParentLayout.addView(textView);
                    viewsList.put(question.getColumnId(), textView);

                } else if (question.getDataType().equalsIgnoreCase("date")) {

                    QuestionnaireDateView dateView = new QuestionnaireDateView(getContext());
                    dateView.setGridQuestionData(question);

                    llParentLayout.addView(dateView);
                    viewsList.put(question.getColumnId(), dateView);


                } else if (question.getDataType().equalsIgnoreCase("number")) {

                    QuestionnaireNumberView numberView = new QuestionnaireNumberView(getContext());
                    numberView.setGridQuestionData(question);

                    llParentLayout.addView(numberView);
                    viewsList.put(question.getColumnId(), numberView);


                } else if (question.getDataType().equalsIgnoreCase("bool")) {

                    QuestionnaireBoolView boolView = new QuestionnaireBoolView(getContext());
                    boolView.setGridQuestionData(question);

                    llParentLayout.addView(boolView);
                    viewsList.put(question.getColumnId(), boolView);


                } else if (question.getDataType().equalsIgnoreCase("list")) {

                    QuestionnaireListView listView = new QuestionnaireListView(getContext());
                    listView.setGridQuestionData(question);

                    llParentLayout.addView(listView);
                    viewsList.put(question.getColumnId(), listView);


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void addDataToQuestionnaireGrid() {

        ArrayList<Boolean> validList = new ArrayList<>();

        cvAdd.setClickable(false);

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

        dataGridObj.setDataGridColumn(columnAnswersList);

        if (validList.contains(false)) {

            cvAdd.setClickable(true);

        } else {

            gridView.updateDataGrid(dataGridObj, position);

            getFragmentManager().popBackStack("DataGrid", FragmentManager.POP_BACK_STACK_INCLUSIVE);

        }

    }

    private void openImageOptions() {

        try {

            Dialog dialog = new Dialog(getContext());
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.camera_options);
            dialog.getWindow().getAttributes().windowAnimations = R.style.slidingUpAndDown;
            DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
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
                if ((ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {


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


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY) {
            if (data != null) {
                try {
                    if (data.getData() != null) {
                        Uri uri = data.getData();

                        String mimeType = this.getActivity().getContentResolver().getType(uri);
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
                                photoFile = Config.createFile(getActivity(), extension, true);
                            } catch (IOException ex) {
                                Toast.makeText(getActivity(), "Error occurred while creating the file", Toast.LENGTH_SHORT).show();

                                // Log.d(TAG, "Error occurred while creating the file");
                            }

                            InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                            FileOutputStream fileOutputStream = new FileOutputStream(photoFile);
                            // Copying
                            Config.copyStream(inputStream, fileOutputStream);
                            fileOutputStream.close();
                            inputStream.close();
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "onActivityResult: " + e.toString(), Toast.LENGTH_SHORT).show();

                            //Log.d(TAG, "onActivityResult: " + e.toString());
                        }
                        String orgFilePath = photoFile.getAbsolutePath();

                        if (orgFilePath == null) {
                            orgFilePath = Config.getFilePathFromURI(getActivity(), uri, extension);
                        }
                        /*Uri uri = data.getData();
                        String orgFilePath = getRealPathFromURI(uri, getActivity());
                        String filepath = "";//default fileName

                        String mimeType = getActivity().getContentResolver().getType(uri);
                        String uriString = uri.toString();
                        String extension = "";
                        if (uriString.contains(".")) {
                            extension = uriString.substring(uriString.lastIndexOf(".") + 1);
                        }

                        if (mimeType != null) {
                            extension = mimeType.substring(mimeType.lastIndexOf("/") + 1);
                        }
                        if (orgFilePath == null) {
                            orgFilePath = getFilePathFromURI(getContext(), uri, extension);
                        }*/

                        View fileUploadView = viewsList.get(qLabelName);
                        RecyclerView rvFiles = (RecyclerView) fileUploadView.findViewById(R.id.rv_files);
                        CustomTextViewMedium tvSupportedTypes = (CustomTextViewMedium) fileUploadView.findViewById(R.id.tv_supportedTypes);
                        FilesAdapter filesAdapter = (FilesAdapter) rvFiles.getAdapter();

                        if (tvSupportedTypes.getText().toString().contains(extension)) {
                            fileObject.setImagePath(orgFilePath);
                            filesAdapter.updateFileObject(fileObject);
                        } else {

                            Toast.makeText(getContext(), "File type not supported", Toast.LENGTH_SHORT).show();
                        }


                    } else if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            String mimeType = this.getActivity().getContentResolver().getType(uri);
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
                                    photoFile = Config.createFile(getActivity(), extension, true);
                                } catch (IOException ex) {
                                    Toast.makeText(getActivity(), "Error occurred while creating the file", Toast.LENGTH_SHORT).show();

                                    // Log.d(TAG, "Error occurred while creating the file");
                                }

                                InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                                FileOutputStream fileOutputStream = new FileOutputStream(photoFile);
                                // Copying
                                Config.copyStream(inputStream, fileOutputStream);
                                fileOutputStream.close();
                                inputStream.close();
                            } catch (Exception e) {
                                Toast.makeText(getActivity(), "onActivityResult: " + e.toString(), Toast.LENGTH_SHORT).show();

                                //Log.d(TAG, "onActivityResult: " + e.toString());
                            }
                            String orgFilePath = photoFile.getAbsolutePath();

                            if (orgFilePath == null) {
                                orgFilePath = Config.getFilePathFromURI(getActivity(), uri, extension);
                            }

                            View fileUploadView = viewsList.get(qLabelName);
                            RecyclerView rvFiles = (RecyclerView) fileUploadView.findViewById(R.id.rv_files);
                            CustomTextViewMedium tvSupportedTypes = (CustomTextViewMedium) fileUploadView.findViewById(R.id.tv_supportedTypes);
                            FilesAdapter filesAdapter = (FilesAdapter) rvFiles.getAdapter();

                            if (tvSupportedTypes.getText().toString().contains(extension)) {
                                fileObject.setImagePath(orgFilePath);
                                filesAdapter.updateFileObject(fileObject);
                            } else {
                                Toast.makeText(getContext(), "File type not supported", Toast.LENGTH_SHORT).show();
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
                    photoFile = Config.createFile(getActivity(), "png", true);//////////
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
                /*try {
                    bytes.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }
        }
    }
}