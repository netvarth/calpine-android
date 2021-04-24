package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomEditTextRegular;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.MultipleSelectionSpinner;
import com.jaldeeinc.jaldee.response.ActiveAppointment;
import com.jaldeeinc.jaldee.response.GetQuestion;
import com.jaldeeinc.jaldee.response.Questionnaire;
import com.jaldeeinc.jaldee.response.Questions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuestionareActivity extends AppCompatActivity {


    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionare);
        mContext = QuestionareActivity.this;

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
                txtView.setId(1);//need for better use
                txtView.setTextColor(mContext.getResources().getColor(R.color.title_color));
                txtView.setTextSize(15);
                LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                textParams.setMargins(35, 35, 35, 0);
                txtView.setLayoutParams(textParams);
                linearLayout.addView(txtView);

                if (question.getGetQuestion().getFileProperties() != null && question.getGetQuestion().getFileProperties().getAllowedDocuments() != null && question.getGetQuestion().getFileProperties().getAllowedDocuments().size() > 1) {

                    MultipleSelectionSpinner multipleSelectionSpinner = new MultipleSelectionSpinner(this);
                    multipleSelectionSpinner.setItems(question.getGetQuestion().getFileProperties().getAllowedDocuments());
                    LinearLayout.LayoutParams spinnerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    spinnerParams.setMargins(35, 15, 35, 0);
                    multipleSelectionSpinner.setLayoutParams(spinnerParams);
                    multipleSelectionSpinner.setId(2);
                    multipleSelectionSpinner.setBackground(mContext.getResources().getDrawable(R.drawable.new_spinner_bg));
                    linearLayout.addView(multipleSelectionSpinner);

                } else if (question.getGetQuestion().getFileProperties() != null && question.getGetQuestion().getFileProperties().getAllowedDocuments() != null && question.getGetQuestion().getFileProperties().getAllowedDocuments().size() == 1) {

                    CardView cardView = new CardView(this);
                    CustomTextViewSemiBold tvUpload = new CustomTextViewSemiBold(this);
                    tvUpload.setText("Upload file");
                    tvUpload.setTextSize(15);
                    tvUpload.setTextColor(mContext.getResources().getColor(R.color.title_color));
                    tvUpload.setId(3);
                    LinearLayout.LayoutParams upload = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    upload.setMargins(35, 15, 35, 15);
                    tvUpload.setLayoutParams(upload);
                    tvUpload.setGravity(Gravity.CENTER);
                    LinearLayout.LayoutParams cardUpload = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    cardUpload.setMargins(35, 15, 35, 15);
                    cardView.setLayoutParams(cardUpload);
                    cardView.setCardBackgroundColor(getResources().getColor(R.color.spinnerbg));
                    cardView.addView(tvUpload);
                    linearLayout.addView(cardView);

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
                text.setId(6);
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
                dateLayout.setLayoutParams(calenderLayout);

                linearLayout.addView(dateLayout);

                // underline
                LinearLayout dividerOne = new LinearLayout(this);
                dividerOne.setBackgroundColor(getResources().getColor(R.color.black));
                LinearLayout.LayoutParams dividerOneParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
                dividerOneParams.setMargins(35, 15, 35, 0);
                dividerOne.setLayoutParams(dividerOneParams);
                linearLayout.addView(dividerOne);


            } else if (question.getGetQuestion().getFieldDataType().equalsIgnoreCase("plainText")) {

                CustomTextViewSemiBold txtView = new CustomTextViewSemiBold(this);
                txtView.setText(question.getGetQuestion().getLabel());
                txtView.setId(7);//need for better use
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
                editText.setId(8);
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

// Create RadioButton Dynamically
                RadioButton radioButton1 = new RadioButton(this);
                radioButton1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                radioButton1.setText("");
                radioButton1.setId(0);

                RadioButton radioButton2 = new RadioButton(this);
                radioButton2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                radioButton2.setText("");
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
}