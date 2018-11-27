package com.nv.youneverwait.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nv.youneverwait.Fragment.SearchDetailViewFragment;
import com.nv.youneverwait.R;
import com.nv.youneverwait.callback.FavAdapterOnCallback;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.response.FavouriteModel;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by sharmila on 22/8/18.
 */

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.MyViewHolder> {

    private List<FavouriteModel> mFavList;
    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_provider, tv_removefav, tv_privacy, tv_message, tv_view;
        LinearLayout Lfavlisiting,Layout_fav;
        RecyclerView mrRecylce_fav;
        ImageView imgarrow;

        public MyViewHolder(View view) {
            super(view);
            tv_provider = (TextView) view.findViewById(R.id.txt_provider);
            mrRecylce_fav = (RecyclerView) view.findViewById(R.id.recylce_favloc);
            tv_removefav = (TextView) view.findViewById(R.id.txtremovefav);
            tv_privacy = (TextView) view.findViewById(R.id.txtprivacy);
            tv_message = (TextView) view.findViewById(R.id.txtmessage);
            tv_view = (TextView) view.findViewById(R.id.txtview);
            Lfavlisiting = (LinearLayout) view.findViewById(R.id.favlisiting);
            imgarrow=(ImageView) view.findViewById(R.id.imgarrow);
            Layout_fav=(LinearLayout) view.findViewById(R.id.layout_fav);


        }
    }

    Activity activity;
    FavAdapterOnCallback callback;
    ArrayList<Integer> ids = new ArrayList<>();

    public FavouriteAdapter(List<FavouriteModel> mFAVList, Context mContext, Activity mActivity, FavAdapterOnCallback callback) {
        this.mContext = mContext;
        this.mFavList = mFAVList;
        this.activity = mActivity;
        this.callback = callback;

    }

    @Override
    public FavouriteAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favlist_row, parent, false);


        return new FavouriteAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FavouriteAdapter.MyViewHolder myViewHolder, final int position) {
        final FavouriteModel favList = mFavList.get(position);

        Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        myViewHolder.tv_provider.setTypeface(tyface);

        myViewHolder.tv_provider.setText(favList.getBusinessName());

        myViewHolder.imgarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!favList.isExpandFlag()) {
                    favList.setExpandFlag(true);
                    myViewHolder.Lfavlisiting.setVisibility(View.VISIBLE);
                    ids.clear();
                    for (int i = 0; i < favList.getLocations().size(); i++) {
                        ids.add(favList.getLocations().get(i).getLocId());
                    }


                    Config.logV("Ids------------" + ids.size());
                    for (int i = 0; i < ids.size(); i++) {

                        Config.logV("Ids---1111---------" + ids.get(i));
                    }
                    callback.onMethodViewCallback(favList.getId(), ids, myViewHolder.mrRecylce_fav, favList.getUniqueId(), favList.getBusinessName());
                    myViewHolder.imgarrow.setImageResource( R.drawable.icon_up_light);
                    myViewHolder.Layout_fav.setBackground(mContext.getResources().getDrawable(R.drawable.input_border_top_white));
                } else {
                    favList.setExpandFlag(false);
                    myViewHolder.Lfavlisiting.setVisibility(View.GONE);
                    myViewHolder.imgarrow.setImageResource( R.drawable.icon_down_light);
                    myViewHolder.Layout_fav.setBackground(mContext.getResources().getDrawable(R.drawable.input_background_white_round));

                }
            }
        });
        myViewHolder.tv_provider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onMethodSearchDetailCallback(favList.getUniqueId());
            }


        });

        myViewHolder.tv_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog dialog = new BottomSheetDialog(mContext, R.style.DialogStyle);
                dialog.setContentView(R.layout.reply);
                dialog.show();

                final Button btn_send = (Button) dialog.findViewById(R.id.btn_send);
                Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
                final EditText edt_message = (EditText) dialog.findViewById(R.id.edt_message);
                TextView txtsendmsg = (TextView) dialog.findViewById(R.id.txtsendmsg);
                txtsendmsg.setVisibility(View.VISIBLE);
                txtsendmsg.setText("Message to " + favList.getBusinessName());
                btn_send.setText("SEND");

                edt_message.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable arg0) {
                        if(edt_message.getText().toString().length()>1){
                            btn_send.setEnabled(true);
                            btn_send.setClickable(true);
                            btn_send.setBackground(mContext.getResources().getDrawable(R.drawable.roundedrect_blue));
                        }else{
                            btn_send.setEnabled(false);
                            btn_send.setClickable(false);
                            btn_send.setBackground(mContext.getResources().getDrawable(R.drawable.btn_checkin_grey));
                        }
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                });

                btn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String modifyAccountID = String.valueOf(favList.getId());
                        callback.onMethodMessageCallback(modifyAccountID, edt_message.getText().toString(), dialog);
                        // ApiSearchViewTerminology(modifyAccountID);
                        //dialog.dismiss();

                    }
                });

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });


            }
        });


        myViewHolder.tv_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onMethodSearchDetailCallback(favList.getUniqueId());
            }
        });


        myViewHolder.tv_removefav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog myQuittingDialogBox =new AlertDialog.Builder(mContext)
                        //set message, title, and icon
                        //.setTitle("Delete")
                        .setMessage("Do you want to remove "+favList.getBusinessName()+" from favourite list?")


                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                //your deleting code
                                dialog.dismiss();
                              callback.onMethodDeleteFavourite(favList.getId());
                            }

                        })



                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();

                            }
                        })
                        .create();
                 myQuittingDialogBox.show();
            }
        });



        myViewHolder.tv_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog dialog = new BottomSheetDialog(mContext, R.style.DialogStyle);
                dialog.setContentView(R.layout.privacy);
                dialog.show();

                Button btn_send = (Button) dialog.findViewById(R.id.btn_send);
                Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
                TextView txtmangeprivacy = (TextView) dialog.findViewById(R.id.txtmangeprivacy);
                final CheckBox chkeprivacy = (CheckBox) dialog.findViewById(R.id.chkphone);
                Config.logV("Revel Phone--------------"+favList.isRevealPhoneNumber());
                chkeprivacy.setChecked(favList.isRevealPhoneNumber());
                btn_send.setText("SAVE");
                Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                        "fonts/Montserrat_Bold.otf");
                txtmangeprivacy.setTypeface(tyface);

                btn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        callback.onMethodPrivacy(favList.getId(), chkeprivacy.isChecked(), dialog);
                        favList.setRevealPhoneNumber(chkeprivacy.isChecked());


                    }
                });

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

    }



    @Override
    public int getItemCount() {
        return mFavList.size();
    }


}