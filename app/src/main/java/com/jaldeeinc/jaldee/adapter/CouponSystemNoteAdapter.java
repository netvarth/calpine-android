package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.enums.CouponSystemNotes;

import java.util.ArrayList;

public class CouponSystemNoteAdapter extends RecyclerView.Adapter<CouponSystemNoteAdapter.MyViewHolder> {
    Context mContext;
    ArrayList<String> sytemNotes;

    public CouponSystemNoteAdapter(Context mContext, ArrayList<String> sytemNotes) {
        this.mContext = mContext;
        this.sytemNotes = sytemNotes;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mSystemNote;

        public MyViewHolder(View view) {
            super(view);
            mSystemNote = (TextView) view.findViewById(R.id.system_note);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupon_systemnote_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int position) {
        final String systemNote = sytemNotes.get(position);
        myViewHolder.mSystemNote.setText(CouponSystemNotes.valueOf(systemNote).getSystemMsg());
    }


    @Override
    public int getItemCount() {
        return sytemNotes.size();
    }
}
