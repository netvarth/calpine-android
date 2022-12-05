package com.jaldeeinc.jaldee.custom;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaldeeinc.jaldee.Interface.IDataGrid;
import com.jaldeeinc.jaldee.Interface.IFilesInterface;
import com.jaldeeinc.jaldee.Interface.IServiceOption;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.DetailPageItemsAdapter;
import com.jaldeeinc.jaldee.adapter.ItemsAdapter;
import com.jaldeeinc.jaldee.adapter.SelectedItemsAdapter;
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

}
