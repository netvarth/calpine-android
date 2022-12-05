package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.model.BillModel;

import java.util.List;

public class BillDemandDisplayNotesAdapter extends RecyclerView.Adapter<BillDemandDisplayNotesAdapter.MyViewHolder> {

        List<BillModel> billDisplayNotes;
        Context mContext;
        BillModel mBillData;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tv_billNotes,tv_displayNotes;
            LinearLayout displayNotesList;

            public MyViewHolder(View view) {
                super(view);
                tv_billNotes = (TextView) view.findViewById(R.id.txtbill_notes);
                tv_displayNotes = (TextView) view.findViewById(R.id.txtdisplay_notes);
                displayNotesList=(LinearLayout)view.findViewById(R.id.displayNotesList);

            }
        }

        public BillDemandDisplayNotesAdapter(List<BillModel> billDisplayNotes, Context mContext, BillModel mBillData) {
            this.mContext = mContext;
            this.billDisplayNotes = billDisplayNotes;
            this.mBillData = mBillData;

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.bill_displaynotes_listrow, parent, false);

            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder myViewHolder, final int position) {
            final BillModel displayNotesList = billDisplayNotes.get(position);
            if(mBillData.getDisplayNotes()!=null && displayNotesList.getDisplayNote()!=null){
                myViewHolder.tv_billNotes.setText("Bill Notes");
                myViewHolder.tv_billNotes.setVisibility(View.GONE);
            }
            else{
                myViewHolder.tv_billNotes.setText("Bill Notes");
                myViewHolder.tv_billNotes.setVisibility(View.VISIBLE);
            }
            String displayNotes = displayNotesList.getDisplayNote();
            if (displayNotesList.getDisplayNote() != null ) {
                myViewHolder.tv_displayNotes.setText("On Demand Discount " + "-" + " " + displayNotes);
            } else {
                myViewHolder.tv_billNotes.setVisibility(View.GONE);
                myViewHolder.tv_displayNotes.setVisibility(View.GONE);
            }
        }
        @Override
        public int getItemCount() {
            return billDisplayNotes.size();
        }
    }


