package com.jaldeeinc.jaldee.custom;

import static com.jaldeeinc.jaldee.connection.ApiClient.context;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jaldeeinc.jaldee.Interface.IActions;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.BillActivity;
import com.jaldeeinc.jaldee.activities.ChatActivity;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.activities.OrderDetailActivity;
import com.jaldeeinc.jaldee.activities.ReleasedQNRActivity;
import com.jaldeeinc.jaldee.activities.ShowCartServiceOption;
import com.jaldeeinc.jaldee.activities.UpdateQuestionnaire;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.model.LabelPath;
import com.jaldeeinc.jaldee.model.QuestionnairInpt;
import com.jaldeeinc.jaldee.model.QuestionnaireResponseInput;
import com.jaldeeinc.jaldee.model.RlsdQnr;
import com.jaldeeinc.jaldee.response.ActiveOrders;
import com.jaldeeinc.jaldee.response.AnswerLineResponse;
import com.jaldeeinc.jaldee.response.GetQuestion;
import com.jaldeeinc.jaldee.response.ItemDetails;
import com.jaldeeinc.jaldee.response.QuestionAnswers;
import com.jaldeeinc.jaldee.response.QuestionnaireResponse;
import com.jaldeeinc.jaldee.response.RatingResponse;
import com.jaldeeinc.jaldee.utils.SharedPreference;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActionsDialog extends Dialog {

    private Context mContext;
    private boolean isActive = false;
    private ActiveOrders orderInfo = new ActiveOrders();
    private IActions iActions;
    boolean firstTimeRating = false;

    @BindView(R.id.ll_cancel)
    LinearLayout llCancel;

    @BindView(R.id.ll_bill)
    LinearLayout llBill;

    @BindView(R.id.ll_details)
    LinearLayout llDetails;

    @BindView(R.id.ll_message)
    LinearLayout llMessage;

    @BindView(R.id.ll_rating)
    LinearLayout llRating;

    @BindView(R.id.tv_bill)
    CustomTextViewMedium tvBill;

    @BindView(R.id.ll_questionnaire)
    LinearLayout llQuestionnaire;

    @BindView(R.id.ll_service_option_qnr)
    LinearLayout ll_service_option_qnr;

    public OrderActionsDialog(@NonNull Context context, boolean isActive, ActiveOrders activeOrder, IActions iActions) {
        super(context);
        this.mContext = context;
        this.isActive = isActive;
        this.orderInfo = activeOrder;
        this.iActions = iActions;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_actions);
        ButterKnife.bind(this);

        if (isActive) {

            if (orderInfo.getOrderStatus().equalsIgnoreCase("Order Received") || orderInfo.getOrderStatus().equalsIgnoreCase("Order Acknowledged") || orderInfo.getOrderStatus().equalsIgnoreCase("Order Confirmed")) {

                llCancel.setVisibility(View.VISIBLE);
                llCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        showAlertDialog();
                    }
                });
            } else {
                hideView(llCancel);
                llCancel.setVisibility(View.GONE);
            }

        } else {
            llCancel.setVisibility(View.GONE);
            hideView(llCancel);
            llRating.setVisibility(View.VISIBLE);
        }
        // to show questionnaire option
        if (orderInfo != null) {
            if (orderInfo.getReleasedQnr() != null) {
                List<RlsdQnr> fReleasedQNR = orderInfo.getReleasedQnr().stream()
                        .filter(p -> !p.getStatus().equalsIgnoreCase("unReleased")).collect(Collectors.toList());
                orderInfo.getReleasedQnr().clear();
                orderInfo.setReleasedQnr((ArrayList<RlsdQnr>) fReleasedQNR); // remove releasedqnr response and add rlsdqnr with remove "unReleased" status
            }
        }
        if (orderInfo.getReleasedQnr() != null && orderInfo.getQuestionnaire() != null && orderInfo.getQuestionnaire().getQuestionAnswers() != null && orderInfo.getQuestionnaire().getQuestionAnswers().size() > 0) {
            llQuestionnaire.setVisibility(View.VISIBLE);
        } else if (orderInfo.getReleasedQnr() != null && orderInfo.getQuestionnaire() != null && orderInfo.getReleasedQnr().size() > 0) {
            llQuestionnaire.setVisibility(View.VISIBLE);
        } else {
            hideView(llQuestionnaire);
        }
        // To show ServiceOption details
        if(orderInfo.getItemsList() != null && orderInfo.getItemsList().size() > 0){
            ArrayList<ItemDetails> itemsDetails = orderInfo.getItemsList();
            for(ItemDetails i : itemsDetails){
                if(i.getSrvAnswers() != null ){
                    QuestionnairInpt answerLine;
                    JsonObject jsonObject = i.getSrvAnswers();
                    Gson gson = new Gson();
                    answerLine = gson.fromJson(jsonObject, QuestionnairInpt.class);
                    if(answerLine.getAnswerLines().size() > 0){
                        ll_service_option_qnr.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
        // To show Bill details
        if (orderInfo.getBill() != null) {
            if (orderInfo.getBill().getBillPaymentStatus().equalsIgnoreCase("FullyPaid") || orderInfo.getBill().getBillPaymentStatus().equalsIgnoreCase("Refund")) {
                tvBill.setText("Receipt");
            } else {
                tvBill.setText("Pay Bill");
            }

            if (orderInfo.getBill().getBillViewStatus() != null && !orderInfo.getOrderStatus().equalsIgnoreCase("Cancelled")) {
                if (orderInfo.getBill().getBillViewStatus().equalsIgnoreCase("Show")) {
                    llBill.setVisibility(View.VISIBLE);
                } else {
                    hideView(llBill);
                }

            } else {
                if (!orderInfo.getBill().getBillPaymentStatus().equalsIgnoreCase("NotPaid")) {
                    llBill.setVisibility(View.VISIBLE);
                } else {
                    hideView(llBill);
                }
            }
            /**26-3-21*/
            if (orderInfo.getBill().getBillViewStatus() == null || orderInfo.getBill().getBillViewStatus().equalsIgnoreCase("NotShow") || orderInfo.getBill().getBillStatus().equals("Settled") || orderInfo.getOrderStatus().equals("Rejected") || orderInfo.getOrderStatus().equals("Cancelled")) {
                hideView(llBill);
            }
            /***/

            llBill.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        if (orderInfo != null) {
                            Intent iBill = new Intent(mContext, BillActivity.class);
                            iBill.putExtra("ynwUUID", orderInfo.getUid());
                            iBill.putExtra("provider", orderInfo.getProviderAccount().getBusinessName());
                            iBill.putExtra("accountID", String.valueOf(orderInfo.getProviderAccount().getId()));
                            if (orderInfo.getBill() != null) {
                                iBill.putExtra("payStatus", orderInfo.getBill().getBillPaymentStatus());
                            }
                            iBill.putExtra("purpose", Constants.PURPOSE_BILLPAYMENT);
                            iBill.putExtra("consumer", orderInfo.getOrderFor().getFirstName() + " " + orderInfo.getOrderFor().getLastName());
                            iBill.putExtra("uniqueId", String.valueOf(orderInfo.getProviderAccount().getUniqueId()));
                            iBill.putExtra("encId", orderInfo.getOrderNumber());
                            iBill.putExtra("bookingStatus", orderInfo.getOrderStatus());
                            mContext.startActivity(iBill);
                            dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {

            hideView(llBill);
        }

        llDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, OrderDetailActivity.class);
                //intent.putExtra("orderInfo", orderInfo);
                intent.putExtra("uuid", orderInfo.getUid());
                intent.putExtra("account", String.valueOf(orderInfo.getProviderAccount().getId()));
                mContext.startActivity(intent);
                dismiss();
            }
        });

        llRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ApiRating(String.valueOf(orderInfo.getProviderAccount().getId()), orderInfo.getUid());
            }
        });

        llMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (orderInfo != null) {
                        Intent intent = new Intent(mContext, ChatActivity.class);
                        intent.putExtra("uuid", orderInfo.getUid());
                        intent.putExtra("accountId", orderInfo.getProviderAccount().getId());
                        intent.putExtra("name", orderInfo.getProviderAccount().getBusinessName());
                        intent.putExtra("from", Constants.ORDERS);
                        mContext.startActivity(intent);
                    }
                    dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        llQuestionnaire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (orderInfo != null) {
                    if (orderInfo.getReleasedQnr() != null) {
                        if (orderInfo.getReleasedQnr().size() == 1 && orderInfo.getReleasedQnr().get(0).getStatus().equalsIgnoreCase("submitted")) {

                            QuestionnaireResponseInput input = buildQuestionnaireInput(orderInfo.getQuestionnaire());
                            ArrayList<LabelPath> labelPaths = buildQuestionnaireLabelPaths(orderInfo.getQuestionnaire());

                            SharedPreference.getInstance(mContext).setValue(Constants.QUESTIONNAIRE, new Gson().toJson(input));
                            SharedPreference.getInstance(mContext).setValue(Constants.QIMAGES, new Gson().toJson(labelPaths));

                            Intent intent = new Intent(mContext, UpdateQuestionnaire.class);
                            intent.putExtra("serviceId", orderInfo.getCatalog().getCatLogId());
                            intent.putExtra("accountId", orderInfo.getProviderAccount().getId());
                            intent.putExtra("uid", orderInfo.getUid());
                            intent.putExtra("isEdit", true);
                            intent.putExtra("from", Constants.ORDERS);
                            if (orderInfo != null && orderInfo.getOrderStatus() != null) {
                                intent.putExtra("status", orderInfo.getOrderStatus());
                            }
                            mContext.startActivity(intent);
                        } else {
                            Gson gson = new Gson();
                            String myJson = gson.toJson(orderInfo);

                            Intent intent = new Intent(mContext, ReleasedQNRActivity.class);
                            intent.putExtra("bookingInfo", myJson);
                            intent.putExtra("from", Constants.ORDERS);
                            mContext.startActivity(intent);
                        }
                    }
                }
                dismiss();
            }
        });
        ll_service_option_qnr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (orderInfo != null) {
                    if (orderInfo.getItemsList() != null && orderInfo.getItemsList().size() > 0) {

                            Intent intent = new Intent(mContext, ShowCartServiceOption.class);
                            //intent.putExtra("serviceId", orderInfo.getCatalog().getCatLogId());
                           // intent.putExtra("accountId", orderInfo.getProviderAccount().getId());
                           // intent.putExtra("uid", orderInfo.getUid());
                            intent.putExtra("orderInfo", new Gson().toJson(orderInfo));
                            intent.putExtra("from", Constants.ORDERS);

                            mContext.startActivity(intent);

                    }
                }
                dismiss();
            }
        });
    }

    private QuestionnaireResponseInput buildQuestionnaireInput(QuestionnaireResponse questionnaire) {

        QuestionnaireResponseInput responseInput = new QuestionnaireResponseInput();
        responseInput.setQuestionnaireId(questionnaire.getQuestionnaireId());
        ArrayList<AnswerLineResponse> answerLineResponse = new ArrayList<>();
        ArrayList<GetQuestion> questions = new ArrayList<>();

        for (QuestionAnswers qAnswers : questionnaire.getQuestionAnswers()) {

            answerLineResponse.add(qAnswers.getAnswerLine());
            questions.add(qAnswers.getGetQuestion());

        }

        responseInput.setAnswerLines(answerLineResponse);
        responseInput.setQuestions(questions);

        return responseInput;

    }

    private ArrayList<LabelPath> buildQuestionnaireLabelPaths(QuestionnaireResponse questionnaire) {

        ArrayList<LabelPath> labelPaths = new ArrayList<>();

        for (QuestionAnswers qAnswers : questionnaire.getQuestionAnswers()) {

            if (qAnswers.getGetQuestion().getFieldDataType().equalsIgnoreCase("fileUpload")) {

                JsonArray jsonArray = new JsonArray();
                jsonArray = qAnswers.getAnswerLine().getAnswer().get("fileUpload").getAsJsonArray();
                for (int i = 0; i < jsonArray.size(); i++) {

                    LabelPath path = new LabelPath();
                    path.setId(labelPaths.size());
                    path.setFileName(jsonArray.get(i).getAsJsonObject().get("caption").getAsString());
                    path.setLabelName(qAnswers.getAnswerLine().getLabelName());
                    path.setPath(jsonArray.get(i).getAsJsonObject().get("s3path").getAsString());
                    path.setType(jsonArray.get(i).getAsJsonObject().get("type").getAsString());

                    labelPaths.add(path);
                }

            }

        }

        return labelPaths;

    }

    BottomSheetDialog dialog;
    float rate = 0;
    String comment = "";

    private void ApiRating(final String accountID, final String UUID) {
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Map<String, String> query = new HashMap<>();
        query.put("account-eq", accountID);
        query.put("uId-eq", UUID);
        Call<ArrayList<RatingResponse>> call = apiService.getOrderRating(query);
        Config.logV("Location-----###########@@@@@@" + query);
        call.enqueue(new Callback<ArrayList<RatingResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<RatingResponse>> call, final Response<ArrayList<RatingResponse>> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getOwnerActivity(), mDialog);
                    Config.logV("URL----------Location-----###########@@@@@@-----" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code--------Message-----------------" + response.code());
                    if (response.code() == 200) {
                        final ArrayList<RatingResponse> mRatingDATA = response.body();
                        Config.logV("Response--code--------BottomSheetDialog-----------------" + response.code());
                        dialog = new BottomSheetDialog(mContext);
                        dialog.setContentView(R.layout.rating);
                        dialog.setCancelable(true);
                        dialog.show();
                        final EditText edt_message = (EditText) dialog.findViewById(R.id.edt_message);
                        final RatingBar rating = (RatingBar) dialog.findViewById(R.id.rRatingBar);
                        final Button btn_close = (Button) dialog.findViewById(R.id.btn_cancel);
                        final Button btn_rate = (Button) dialog.findViewById(R.id.btn_send);
                        btn_rate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                rate = rating.getRating();
                                comment = edt_message.getText().toString();
                                if (response.body().size() == 0) {
                                    firstTimeRating = true;
                                } else {
                                    firstTimeRating = false;
                                }
                                ApiPUTRating(Math.round(rate), UUID, comment, accountID, dialog, firstTimeRating);

                            }
                        });
                        edt_message.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void afterTextChanged(Editable arg0) {
                                if (edt_message.getText().toString().length() >= 1 && !edt_message.getText().toString().trim().isEmpty() && rating != null && rating.getRating() != 0) {
                                    btn_rate.setEnabled(true);
                                    btn_rate.setClickable(true);
                                    btn_rate.setBackground(mContext.getResources().getDrawable(R.drawable.curved_save));
                                } else {
                                    btn_rate.setEnabled(false);
                                    btn_rate.setClickable(false);
                                    btn_rate.setBackground(mContext.getResources().getDrawable(R.drawable.btn_checkin_grey));
                                }
                            }

                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }
                        });

                        if (rating != null) {
                            rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                                @Override
                                public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                                    if (edt_message.getText().toString().length() >= 1 && !edt_message.getText().toString().trim().isEmpty() && rating.getRating() != 0) {
                                        btn_rate.setEnabled(true);
                                        btn_rate.setClickable(true);
                                        btn_rate.setBackground(mContext.getResources().getDrawable(R.drawable.curved_save));
                                    } else {
                                        btn_rate.setEnabled(false);
                                        btn_rate.setClickable(false);
                                        btn_rate.setBackground(mContext.getResources().getDrawable(R.drawable.btn_checkin_grey));
                                    }
                                }
                            });
                        }


                        btn_close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        if (response.body().size() > 0) {
                            if (mRatingDATA.get(0).getStars() != 0) {
                                rating.setRating(Float.valueOf(mRatingDATA.get(0).getStars()));
                            }
                            if (mRatingDATA.get(0).getFeedback() != null) {
                                Config.logV("Comments---------" + mRatingDATA.get(0).getFeedback().get(mRatingDATA.get(0).getFeedback().size() - 1).getComments());
                                edt_message.setText(mRatingDATA.get(0).getFeedback().get(mRatingDATA.get(0).getFeedback().size() - 1).getComments());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<RatingResponse>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Location-----###########@@@@@@-------Fail--------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getOwnerActivity(), mDialog);
            }
        });
    }

    private void ApiPUTRating(final int stars, final String UUID, String feedback, String accountID, final BottomSheetDialog dialog, boolean firstTimerate) {
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("uId", UUID);
            jsonObj.put("stars", String.valueOf(stars));
            jsonObj.put("feedback", feedback);
            Config.logV("Feedback--------------" + feedback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<ResponseBody> call;
        if (firstTimerate) {
            call = apiService.putOrderRating(accountID, body);
        } else {
            call = apiService.updateOrderRating(accountID, body);
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Config.logV("URL-------Request---" + response.raw().request().url().toString().trim());
                    if (mDialog.isShowing())
                        Config.closeDialog(getOwnerActivity(), mDialog);
                    dialog.dismiss();
                    Config.logV("Put Rating#########################" + response.code());
                    if (response.code() == 200) {
                        if (response.body().string().equalsIgnoreCase("true")) {
                            DynamicToast.make(context, "Rated successfully", AppCompatResources.getDrawable(
                                    context, R.drawable.icon_tickmark),
                                    ContextCompat.getColor(context, R.color.white), ContextCompat.getColor(context, R.color.green), Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Location-----###########@@@@@@-------Fail--------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getOwnerActivity(), mDialog);
            }
        });
    }


    private void showAlertDialog() {

        final BottomSheetDialog dialog = new BottomSheetDialog(mContext);
        dialog.setContentView(R.layout.cancelcheckin);
        dialog.show();
        Button btSend = (Button) dialog.findViewById(R.id.btn_send);
        Button btCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/JosefinSans-SemiBold.ttf");
        btSend.setTypeface(tyface1);
        btCancel.setTypeface(tyface1);
        final EditText edt_message = (EditText) dialog.findViewById(R.id.edt_message);
        TextView txtsendmsg = (TextView) dialog.findViewById(R.id.txtsendmsg);
        txtsendmsg.setText("Do you want to cancel this Order?");
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //api call
                if (orderInfo != null && orderInfo.getUid() != null && orderInfo.getProviderAccount() != null) {
                    cancelOrder(orderInfo, dialog);
                }
            }
        });
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void cancelOrder(ActiveOrders orderInfo, BottomSheetDialog dialog) {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ResponseBody> call = apiService.cancelOrder(orderInfo.getUid(), orderInfo.getProviderAccount().getId());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getOwnerActivity(), mDialog);
                    if (response.code() == 200) {

                        DynamicToast.make(mContext, "Order cancelled successfully",
                                ContextCompat.getColor(mContext, R.color.white), ContextCompat.getColor(mContext, R.color.appoint_theme), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        dismiss();
                        iActions.onCancel();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getOwnerActivity(), mDialog);

            }
        });

    }

    private void hideView(View view) {
        GridLayout gridLayout = (GridLayout) view.getParent();
        if (gridLayout != null) {
            for (int i = 0; i < gridLayout.getChildCount(); i++) {
                if (view == gridLayout.getChildAt(i)) {
                    gridLayout.removeViewAt(i);
                    break;
                }
            }
        }
    }

}
