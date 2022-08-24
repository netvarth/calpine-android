package com.jaldeeinc.jaldee.custom;

import android.Manifest;
import android.app.Activity;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.jaldeeinc.jaldee.Interface.IItemInterface;
import com.jaldeeinc.jaldee.Interface.IServiceOption;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.ItemsActivity;
import com.jaldeeinc.jaldee.adapter.CheckBoxAdapter;
import com.jaldeeinc.jaldee.adapter.FilesAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.model.AnswerLine;
import com.jaldeeinc.jaldee.model.CartItemModel;
import com.jaldeeinc.jaldee.model.DataGrid;
import com.jaldeeinc.jaldee.model.GridColumnAnswerLine;
import com.jaldeeinc.jaldee.model.LabelPath;
import com.jaldeeinc.jaldee.model.QuestionnairInpt;
import com.jaldeeinc.jaldee.model.QuestionnaireCheckbox;
import com.jaldeeinc.jaldee.response.CatalogItem;
import com.jaldeeinc.jaldee.response.DataGridColumns;
import com.jaldeeinc.jaldee.response.Item;
import com.jaldeeinc.jaldee.response.Questionnaire;
import com.jaldeeinc.jaldee.response.Questions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OrderitemServiceoptionadditemDialog extends Fragment implements IServiceOption {
    Context mContext;
    Dialog itemOptionsDialog;
    LinearLayout llParentLayout;
    private HashMap<String, View> viewsList = new HashMap<>();
    private HashMap<String, View> mainViewsList = new HashMap<>();
    private CardView cv_submit, cv_cover;
    private TextView tv_totalprice;
    boolean isEdit;
    IServiceOption iServiceOptionListOptionChange, iSrvcOptionListOptionChange;
    int position = -1;

    private static final String GRID_QUESTION = "gridQuestion";
    private static final String GRID_ANSWERS = "gridAnswers";
    private static final String ISEDIT = "isEdit";


    private static final String ITEM_DETAILS = "itemDetails";
    private static final String ITEM_DETAILSCATALOGMODEL = "itemDetailsCatalogModel";

    private static final String POSITION = "position";
    private static final String ISERVICEOPTION = "iserviceOption";

    private static final String FROMADDEDASNEW = "fromAddedAsNew";

    Questionnaire questionnaire;
    CatalogItem itemDetails;
    QuestionnairInpt answer;
    CartItemModel itemDetailsCatalogModel;
    IItemInterface iItemInterface;
    boolean fromAddedAsNew;
    private DatabaseHandler db;
    public OrderitemServiceoptionadditemDialog(IServiceOption iSrvcOptionListOptionChange) {
        this.iSrvcOptionListOptionChange = iSrvcOptionListOptionChange;
    }
    public OrderitemServiceoptionadditemDialog(IServiceOption iSrvcOptionListOptionChange, IItemInterface iItemInterface) {
        this.iSrvcOptionListOptionChange = iSrvcOptionListOptionChange;
        this.iItemInterface = iItemInterface;
    }

    public static OrderitemServiceoptionadditemDialog newInstance(Questionnaire qnr, CatalogItem itemDetails, CartItemModel itemDetailsModel, IServiceOption iSrvcOptionListOptionChange, IItemInterface iItemInterface, boolean fromAddedAsNew) {
        OrderitemServiceoptionadditemDialog fragment = new OrderitemServiceoptionadditemDialog(iSrvcOptionListOptionChange, iItemInterface);
        Bundle args = new Bundle();
        String sQnr = new Gson().toJson(qnr);
        args.putString(GRID_QUESTION, sQnr);
        args.putSerializable(ITEM_DETAILS, (Serializable) itemDetails);
        args.putSerializable(ITEM_DETAILSCATALOGMODEL, (Serializable) itemDetailsModel);
        //args.putString(GRID_ANSWERS, null);
        args.putInt(POSITION, -1);
        args.putBoolean(FROMADDEDASNEW, fromAddedAsNew);
        fragment.setArguments(args);

        return fragment;
    }

    public static OrderitemServiceoptionadditemDialog newInstance(String qnr, String qnrInput, int position, CartItemModel itemDetails, boolean isEdit, IServiceOption iSrvcOptionListOptionChange) {
        OrderitemServiceoptionadditemDialog fragment = new OrderitemServiceoptionadditemDialog(iSrvcOptionListOptionChange);
        Bundle args = new Bundle();
        try {
            System.out.println("Console ----" + qnr);
            //Questionnaire qnrs = new Questionnaire();
            args.putString(GRID_QUESTION, qnr);
            args.putString(GRID_ANSWERS, qnrInput);

        } catch (Exception e){
            e.printStackTrace();
        }
        args.putSerializable(ITEM_DETAILSCATALOGMODEL, (Serializable) itemDetails);
        args.putInt(POSITION, position);
        args.putBoolean(ISEDIT, isEdit);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        iServiceOptionListOptionChange = this;
        db = new DatabaseHandler(mContext);

        if (getArguments() != null) {
            try {
                String qnr = getArguments().getString(GRID_QUESTION);
                Gson gson = new Gson();
                questionnaire = gson.fromJson(qnr, Questionnaire.class);
                String qnrInput = getArguments().getString(GRID_ANSWERS);
                answer = gson.fromJson(qnrInput, QuestionnairInpt.class);

            } catch (Exception e){
                e.printStackTrace();
            }
            itemDetails = (CatalogItem) getArguments().getSerializable(ITEM_DETAILS);
            itemDetailsCatalogModel = (CartItemModel) getArguments().getSerializable(ITEM_DETAILSCATALOGMODEL);
            position = getArguments().getInt(POSITION, -1);
            isEdit = getArguments().getBoolean(ISEDIT, true);
            fromAddedAsNew = getArguments().getBoolean(FROMADDEDASNEW, false);
            if (itemDetails == null) {
                itemDetails = new CatalogItem();
                itemDetails.setId(itemDetailsCatalogModel.getItemId());
                itemDetails.setCatalogId(itemDetailsCatalogModel.getCatalogId());
                Item item = new Item();
                item.setItemId(itemDetailsCatalogModel.getItemId());
                item.setDisplayName(itemDetailsCatalogModel.getItemName());
                itemDetails.setItems(item);
            }
        }
        createDialog(questionnaire);


    }

    private void
    createDialog(Questionnaire questionnaire) {

//        if (selectedItemNme != null && !selectedItemNme.trim().isEmpty()) {
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
        //tv_item_name.setText(selectedItemNme);
        if (!isEdit) {
            cv_submit.setVisibility(View.GONE);
            cv_cover.setVisibility(View.VISIBLE);
        } else {
            cv_submit.setVisibility(View.VISIBLE);
            cv_cover.setVisibility(View.GONE);
        }
        tv_item_price.setVisibility(View.GONE);
        tv_item_name.setText(itemDetails.getItems().getDisplayName());
            /*
            if (selectedItemPrice != null && !selectedItemPrice.equals("")) {
                tv_item_price.setText("RS:" + Config.getAmountNoOrTwoDecimalPoints(selectedItemPrice) + "/-");
                tv_item_price.setVisibility(View.VISIBLE);
            } else {
                tv_item_price.setVisibility(View.GONE);
            }*/
        itemOptionsDialog.show();
        try {
            //if()
            createQuestionnaire(questionnaire.getQuestionsList());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemOptionsDialog.cancel();
            }
        });
        cv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addDataToQuestionnaireGrid();
//                try {
//                    submitServiceOptionsQNR();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        });
        if (!isEdit) {
            cv_cover.setOnClickListener(new View.OnClickListener() {   //this empty click listner is important for in case of not editable service qnr
                @Override
                public void onClick(View view) {

                }
            });
        }

//        } else {
//            DynamicToast.make(mContext, "Please select an item",
//                    ContextCompat.getColor(mContext, R.color.white), ContextCompat.getColor(mContext, R.color.red), Toast.LENGTH_SHORT).show();
//
//        }
    }


    private void createQuestionnaire(ArrayList<Questions> questionsList) throws JSONException {
        int count = 0;
        for (Questions question : questionsList) {
            if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("dataGridList")) {
                String selectedItemName = null;
                if (answer != null) {
                    AnswerLine al = answer.getAnswerLines().get(count);
                    ArrayList<DataGrid> dataGridList = new ArrayList<>();
                    dataGridList = al.getDataGridListList();
                    DataGrid dataGrid = dataGridList.get(position);
                    JsonArray ja = dataGrid.getDataGridListColumn().get(0).getColumn().getAsJsonArray("list");
                    if (ja.get(0).toString().trim() != null && !ja.get(0).toString().trim().isEmpty()) {
                        selectedItemName = ja.get(0).getAsString();
                    }
                    for (DataGridColumns q : question.getGetQuestion().getDataGridListProperties().getGridListColumns()) {

                        for (GridColumnAnswerLine answer : dataGrid.getDataGridListColumn()) {

                            if (q.getColumnId().equalsIgnoreCase(answer.getColumnId())) {

                                q.setAnswer(answer);
                            }
                        }
                    }
                } else {
                    ArrayList<DataGridColumns> questionsLst = question.getGetQuestion().getDataGridListProperties().getGridListColumns();

                    String ja = questionsLst.get(0).getListProperties().getValues().get(0).toString();
                    if (ja.trim() != null && !ja.trim().isEmpty()) {
                        selectedItemName = ja.trim();
                    }
                    for (DataGridColumns q : question.getGetQuestion().getDataGridListProperties().getGridListColumns()) {
                        q.setAnswer(null);
                    }
                }
                OrderItemServiceOptionAddItemGridView orderItemServiceOptionAddItemGridView = new OrderItemServiceOptionAddItemGridView(getContext(), question, selectedItemName, iServiceOptionListOptionChange);
                llParentLayout.addView(orderItemServiceOptionAddItemGridView);
                viewsList.put(question.getGetQuestion().getLabelName(), orderItemServiceOptionAddItemGridView);
            }
            count++;
        }
        calculatePrice();
    }

    ArrayList<LabelPath> labelPaths = new ArrayList<>();
    public String path;
    OrderItemServiceOptionAddItemGridView gridFieldView;

    private void submitServiceOptionsQNR() throws JSONException {
        if (answer == null || answer.getAnswerLines().size() == 0) {
            QuestionnairInpt input = new QuestionnairInpt();
            input.setQuestionnaireId(questionnaire.getId());
            ArrayList<AnswerLine> answerLines = new ArrayList<>();

            //if (validForm(questionnaire.getQuestionsList())) {

            labelPaths = new ArrayList<>();
            if (fromAddedAsNew) { // from service option add as a new repeatsame dialog
                QuestionnairInpt questionnairInpt = new QuestionnairInpt();
                String sInput = db.getServiceOptioniput(itemDetails.getItems().getItemId());
                String sInputImages = db.getServiceOptioniputImages(itemDetails.getItems().getItemId());
                if (sInput != null && !sInput.trim().isEmpty()) {
                    Gson gson = new Gson();
                    questionnairInpt = gson.fromJson(sInput, QuestionnairInpt.class);
                }
                ArrayList<AnswerLine> als = questionnairInpt.getAnswerLines();

                int i = 0;
                for (Questions question : questionnaire.getQuestionsList()) {

                    if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("dataGridList")) {

                        gridFieldView = (OrderItemServiceOptionAddItemGridView) viewsList.get(question.getGetQuestion().getLabelName());

                        ArrayList<DataGrid> dataGridList = new ArrayList<>();

                        if (gridFieldView != null) {

                            dataGridList = gridFieldView.getGridDataList();
                        }
                        if (dataGridList != null && dataGridList.size() > 0) {
                            AnswerLine al = als.get(i);
                            DataGrid dataGrid = new DataGrid();
                            ArrayList<DataGrid> dataGridList1 = new ArrayList<>();
                            dataGridList1 = al.getDataGridListList();
                            dataGrid = dataGridList.get(0);
                            dataGridList1.add(dataGrid);
                            JsonObject answer = new JsonObject();
                            Gson gson = new Gson();
                            JsonElement element = gson.toJsonTree(dataGridList1);
                            answer.add("dataGridList", element);
                            questionnairInpt.getAnswerLines().get(i).setAnswer(answer);


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
                    i++;
                }


                //SharedPreference.getInstance(mContext).setValue(Constants.SERVICEOPTIONQNR, new Gson().toJson(input));
                //SharedPreference.getInstance(mContext).setValue(Constants.SERVICEOPTIONQIMAGES, new Gson().toJson(labelPaths));

                String inputString = new Gson().toJson(questionnairInpt);
                itemOptionsDialog.cancel(); // close the dialogbox
                String qnr = db.getServiceOptionQnr(itemDetails.getItems().getItemId());

                float serviceOtpionPrice = 0;
                if (qnr.trim() != null && questionnairInpt != null && !questionnairInpt.getAnswerLines().isEmpty() ) {
                    Gson gson = new Gson();
                    try {
                        ItemsActivity itemsActivity = new ItemsActivity();
                        serviceOtpionPrice = itemsActivity.calculateTotalPrice(itemDetails.getItems().getItemId(), qnr, gson.toJson(questionnairInpt));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                int quantity = db.getItemQuantity(itemDetails.getItems().getItemId());
                db.addQuantity(itemDetails.getItems().getItemId(), quantity + 1);
                db.updateServiceOptionInput(itemDetails.getItems().getItemId(), new Gson().toJson(questionnairInpt), sInputImages, serviceOtpionPrice);
                iItemInterface.checkItemQuantity();
            } else {
                for (Questions question : questionnaire.getQuestionsList()) {

                    if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("dataGridList")) {

                        gridFieldView = (OrderItemServiceOptionAddItemGridView) viewsList.get(question.getGetQuestion().getLabelName());

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

                //SharedPreference.getInstance(mContext).setValue(Constants.SERVICEOPTIONQNR, new Gson().toJson(input));
                //SharedPreference.getInstance(mContext).setValue(Constants.SERVICEOPTIONQIMAGES, new Gson().toJson(labelPaths));

                String inputString = new Gson().toJson(input);
                itemOptionsDialog.cancel(); // close the dialogbox
                float price = calculatePrice();
                iSrvcOptionListOptionChange.savetoDataBase(itemDetails, new Gson().toJson(input), new Gson().toJson(labelPaths));
            }
        } else {
            try {


            int k = 0;
            //ArrayList<AnswerLine> als = answer.getAnswerLines();
            for (Questions question : questionnaire.getQuestionsList()) {
                if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("dataGridList")) {

                    gridFieldView = (OrderItemServiceOptionAddItemGridView) viewsList.get(question.getGetQuestion().getLabelName());

                    ArrayList<DataGrid> dataGridList = new ArrayList<>();

                    if (gridFieldView != null) {

                        dataGridList = gridFieldView.getGridDataList();
                    }
                    if (dataGridList != null && dataGridList.size() > 0) {

                        AnswerLine obj = new AnswerLine();
                        obj.setLabelName(question.getGetQuestion().getLabelName());

                        JsonObject answe = new JsonObject();
                        Gson gson = new Gson();


                        ArrayList<DataGrid> dt = answer.getAnswerLines().get(k).getDataGridListList();
                        dt.set(position, dataGridList.get(0));
                        JsonElement element = gson.toJsonTree(dt);
                        answe.add("dataGridList", element);
                        answer.getAnswerLines().get(k).setAnswer(answe);

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
                k++;
            }
            //String inputString = new Gson().toJson(answer);
            itemOptionsDialog.cancel(); // close the dialogbox
            //float price = calculatePrice();
            iSrvcOptionListOptionChange.savetoDataBase(itemDetails, new Gson().toJson(answer), new Gson().toJson(labelPaths));
        } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private float calculatePrice() {
        ArrayList<GridColumnAnswerLine> columnAnswersList = new ArrayList<>();
        float totalPrice = 0;
        int aCount = llParentLayout.getChildCount();
        for (int j = 0; j < aCount; j++) {
            View l = llParentLayout.getChildAt(j);

            if (l instanceof OrderItemServiceOptionAddItemGridView) {
                LinearLayout ll = ((OrderItemServiceOptionAddItemGridView) l).getllParentLayout();
                int count = ll.getChildCount();
                for (int i = 0; i < count; i++) {
                    View view = ll.getChildAt(i);
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
            }
        }

        tv_totalprice.setText("Price ₹" + Config.getAmountNoOrTwoDecimalPoints(totalPrice));
        return totalPrice;
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
    boolean mIsActivityDone = false;
    @Override
    public KeyPairBoolData openImageOptions(KeyPairBoolData fileObject, String qLabelName, HashMap<String, View> viewsList) {
        this.fileObject1 = fileObject;
        this.viewsList1 = viewsList;
        this.qLabelName1 = qLabelName;
        openImageOptions();
//        while (mIsActivityDone == false)
//        {
//            try {
//                TimeUnit.SECONDS.sleep(2);  // sleeping for 2 seconds
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
        return fileObject1;
    }


    private void addDataToQuestionnaireGrid() {

        ArrayList<Boolean> validList = new ArrayList<>();

        cv_submit.setClickable(false);

        int aCount = llParentLayout.getChildCount();
        for (int j = 0; j < aCount; j++) {
            View l = llParentLayout.getChildAt(j);
            if (l instanceof OrderItemServiceOptionAddItemGridView) {
                OrderItemServiceOptionAddItemGridView gridView = (OrderItemServiceOptionAddItemGridView) l;
                ArrayList<GridColumnAnswerLine> columnAnswersList = new ArrayList<>();
                LinearLayout ll = ((OrderItemServiceOptionAddItemGridView) l).getllParentLayout();
                int count = ll.getChildCount();
                DataGrid dataGridObj = new DataGrid();

                for (int i = 0; i < count; i++) {
                    View view = ll.getChildAt(i);

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
                    // gridFieldView.updateDataGrid(dataGridObj, j);
                    gridView.updateDataGrid(dataGridObj, position);
                    //itemOptionsDialog.cancel();

                }
            }
        }

        try {
            if (!validList.contains(false)) {
                submitServiceOptionsQNR();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }













    private int GALLERY = 3, CAMERA = 4;
    private KeyPairBoolData fileObject1 = new KeyPairBoolData();
    private String qLabelName1 = "";
    private Uri mImageUri;
    private HashMap<String, View> viewsList1 = new HashMap<>();

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
                    //someActivityResultLauncher.launch(intent.createChooser(intent, "Select Picture"));


                }
            } else {

                Intent intent = new Intent();
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY);
                //someActivityResultLauncher.launch(intent.createChooser(intent, "Select Picture"));

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

                        View fileUploadView = viewsList1.get(qLabelName1);
                        RecyclerView rvFiles = (RecyclerView) fileUploadView.findViewById(R.id.rv_files);
                        TextView tvSupportedTypes = fileUploadView.findViewById(R.id.tv_supportedTypes);
                        FilesAdapter filesAdapter = (FilesAdapter) rvFiles.getAdapter();

                        if (tvSupportedTypes.getText().toString().contains(extension)) {
                            fileObject1.setImagePath(orgFilePath);
                            filesAdapter.updateFileObject(fileObject1);
                        } else {

                            Toast.makeText(mContext, "File type not supported", Toast.LENGTH_SHORT).show();
                        }

                        mIsActivityDone = true;
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

                            View fileUploadView = viewsList1.get(qLabelName1);
                            RecyclerView rvFiles = (RecyclerView) fileUploadView.findViewById(R.id.rv_files);
                            TextView tvSupportedTypes = fileUploadView.findViewById(R.id.tv_supportedTypes);
                            FilesAdapter filesAdapter = (FilesAdapter) rvFiles.getAdapter();

                            if (tvSupportedTypes.getText().toString().contains(extension)) {
                                fileObject1.setImagePath(orgFilePath);
                                filesAdapter.updateFileObject(fileObject1);
                            } else {
                                Toast.makeText(mContext, "File type not supported", Toast.LENGTH_SHORT).show();
                            }
                        }
                        mIsActivityDone = true;
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
                /*Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                String path = saveImage(bitmap);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);*/
                if (path != null) {
                    mImageUri = Uri.parse(path);

                    View fileUploadView = viewsList1.get(qLabelName1);
                    RecyclerView rvFiles = (RecyclerView) fileUploadView.findViewById(R.id.rv_files);
                    FilesAdapter filesAdapter = (FilesAdapter) rvFiles.getAdapter();

                    fileObject1.setImagePath(path);
                    filesAdapter.updateFileObject(fileObject1);
                }
                /*try {
                    bytes.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                mIsActivityDone = true;

            }
        }
    }
//    public void openSomeActivityForResult() {
//        Intent intent = new Intent(this, SomeActivity.class);
//        someActivityResultLauncher.launch(intent);
//    }

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
//    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            new ActivityResultCallback<ActivityResult>() {
//                @Override
//                public void onActivityResult(ActivityResult result) {
//                    if (result.getResultCode() == Activity.RESULT_OK) {
//                        // There are no request codes
//                        Intent data = result.getData();
//                       // doSomeOperations();
//                    }
//                }
//            });
}
