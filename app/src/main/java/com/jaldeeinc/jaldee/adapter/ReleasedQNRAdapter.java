package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.activities.CustomQuestionnaire;
import com.jaldeeinc.jaldee.activities.UpdateQuestionnaire;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.model.LabelPath;
import com.jaldeeinc.jaldee.model.QuestionnaireResponseInput;
import com.jaldeeinc.jaldee.model.RlsdQnr;
import com.jaldeeinc.jaldee.response.AnswerLineResponse;
import com.jaldeeinc.jaldee.response.GetQuestion;
import com.jaldeeinc.jaldee.response.QuestionAnswers;
import com.jaldeeinc.jaldee.response.Questionnaire;
import com.jaldeeinc.jaldee.response.QuestionnaireResponse;
import com.jaldeeinc.jaldee.utils.SharedPreference;

import java.util.ArrayList;

public class ReleasedQNRAdapter extends RecyclerView.Adapter<ReleasedQNRAdapter.MyViewHolder> {

    ArrayList<RlsdQnr> rlsdQnrs;
    Context mContext;
    ArrayList<Questionnaire> afterQuestionnaire;

    public int serviceId;
    public int pAccountId;
    public String uid;
    public Integer uniqueId;
    public String status;
    public String from;
    public ArrayList<QuestionnaireResponse> questionnaires;

    public ReleasedQNRAdapter(Context mContext, ArrayList<RlsdQnr> rlsdQnrs, int serviceId, int pAccountId, String uid, String uniqueIdNumber, String status, String from, ArrayList<QuestionnaireResponse> questionnaireResponses) {
        this.rlsdQnrs = rlsdQnrs;
        this.mContext = mContext;
        this.serviceId = serviceId;
        this.pAccountId = pAccountId;
        this.uid = uid;
        this.uniqueId = Integer.parseInt(uniqueIdNumber);
        this.status = status;
        this.from = from;
        this.questionnaires = questionnaireResponses;

    }

    public ReleasedQNRAdapter(Context mContext, ArrayList<RlsdQnr> rlsdQnrs, ArrayList<Questionnaire> afterQuestionnaire, int serviceId, int pAccountId, String uid, String uniqueIdNumber, String status, String from, ArrayList<QuestionnaireResponse> questionnaireResponses) {
        this.rlsdQnrs = rlsdQnrs;
        this.mContext = mContext;
        this.afterQuestionnaire = afterQuestionnaire;
        this.serviceId = serviceId;
        this.pAccountId = pAccountId;
        this.uid = uid;
        this.uniqueId = Integer.parseInt(uniqueIdNumber);
        this.status = status;
        this.from = from;
        this.questionnaires = questionnaireResponses;
    }

    @Override
    public ReleasedQNRAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.released_qnr_item, parent, false);


        return new ReleasedQNRAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final ReleasedQNRAdapter.MyViewHolder myViewHolder, final int position) {

        final RlsdQnr rlsdQnr = rlsdQnrs.get(position);
        try {
            String upperTagString = rlsdQnr.getStatus().substring(0, 1).toUpperCase() + rlsdQnr.getStatus().substring(1).toLowerCase();

            myViewHolder.tv_qnr_name.setText(rlsdQnr.getQnrName());
            myViewHolder.tv_qnr_tag.setText(upperTagString);
            if (rlsdQnr.getStatus().equalsIgnoreCase("released")) {
                myViewHolder.tv_qnr_tag.setTextColor(ContextCompat.getColor(mContext, R.color.red));
            } else if (rlsdQnr.getStatus().equalsIgnoreCase("submitted")) {
                myViewHolder.tv_qnr_tag.setTextColor(ContextCompat.getColor(mContext, R.color.green));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        myViewHolder.cv_qnr.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (rlsdQnr.getStatus().equalsIgnoreCase("released")) {
                    if (afterQuestionnaire != null) {
                        for (Questionnaire questionnaire : afterQuestionnaire) {
                            if (questionnaire != null && questionnaire.getQuestionsList() != null && questionnaire.getId() == rlsdQnr.getId()) {
                                Gson gson = new Gson();
                                String myJson = gson.toJson(questionnaire);
                                Intent intent = new Intent(mContext, CustomQuestionnaire.class);
                                intent.putExtra("serviceId", serviceId);
                                intent.putExtra("accountId", pAccountId);
                                intent.putExtra("uid", uid);
                                intent.putExtra("isEdit", false);
                                intent.putExtra("uniqueId", uniqueId);
                                intent.putExtra("status", status);
                                intent.putExtra("from", from);
                                intent.putExtra("afterQnr", myJson);
                                intent.putExtra("isFromAftrQNR", true);

                                mContext.startActivity(intent);
                            }
                        }
                    }
                } else if (rlsdQnr.getStatus().equalsIgnoreCase("submitted")) {
                    for (QuestionnaireResponse questionnaireResponse : questionnaires) {
                        if (questionnaireResponse.getQuestionnaireId() == rlsdQnr.getId()) {
                            QuestionnaireResponseInput input = buildQuestionnaireInput(questionnaireResponse);
                            ArrayList<LabelPath> labelPaths = buildQuestionnaireLabelPaths(questionnaireResponse);

                            SharedPreference.getInstance(mContext).setValue(Constants.QUESTIONNAIRE, new Gson().toJson(input));
                            SharedPreference.getInstance(mContext).setValue(Constants.QIMAGES, new Gson().toJson(labelPaths));

                            Intent intent = new Intent(mContext, UpdateQuestionnaire.class);
                            intent.putExtra("serviceId", serviceId);
                            intent.putExtra("accountId", pAccountId);
                            intent.putExtra("uid", uid);
                            intent.putExtra("isEdit", true);
                            intent.putExtra("from", from);
                            intent.putExtra("status", status);

                            mContext.startActivity(intent);
                        }
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {

        return rlsdQnrs.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CustomTextViewMedium tv_qnr_name, tv_qnr_tag;
        CardView cv_qnr;

        public MyViewHolder(View view) {
            super(view);
            tv_qnr_name = view.findViewById(R.id.tv_qnr_name);
            tv_qnr_tag = view.findViewById(R.id.tv_qnr_tag);
            cv_qnr = view.findViewById(R.id.cv_qnr);

        }
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
}
