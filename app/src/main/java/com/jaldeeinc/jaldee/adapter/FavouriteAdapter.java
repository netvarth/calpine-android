package com.jaldeeinc.jaldee.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.ChatActivity;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.activities.ProviderDetailActivity;
import com.jaldeeinc.jaldee.callback.FavAdapterOnCallback;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.response.FavouriteModel;
import com.jaldeeinc.jaldee.response.SearchLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by sharmila on 22/8/18.
 */

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.MyViewHolder> {

    private List<FavouriteModel> mFavList;
    Context mContext;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        CustomTextViewSemiBold tv_removefav, tv_privacy, tv_message, tv_view;
        LinearLayout Lfavlisiting, Layout_fav, llfav, llLocation;
        RecyclerView mrRecylce_fav;
        ImageView imgarrow, ivPrivacy, ivMessage,ivUnfav;
        ArrayList<SearchLocation> mSearchLocList;
        CustomTextViewBold tv_provider;
        CustomTextViewMedium tvLocationName;


        public MyViewHolder(View view) {
            super(view);
            tv_provider = view.findViewById(R.id.txt_provider);
            mrRecylce_fav = (RecyclerView) view.findViewById(R.id.recylce_favloc);
            tv_removefav = view.findViewById(R.id.txtremovefav);
            tv_privacy = view.findViewById(R.id.txtprivacy);
            tv_message = view.findViewById(R.id.txtmessage);
            tv_view = view.findViewById(R.id.txtview);
            llfav = view.findViewById(R.id.ll_fav);
            llLocation = view.findViewById(R.id.ll_location);
            Lfavlisiting = (LinearLayout) view.findViewById(R.id.favlisiting);
            imgarrow = (ImageView) view.findViewById(R.id.imgarrow);
            Layout_fav = (LinearLayout) view.findViewById(R.id.layout_fav);
            tvLocationName = view.findViewById(R.id.tv_locationName);
            ivPrivacy = view.findViewById(R.id.iv_privacy);
            ivMessage = view.findViewById(R.id.iv_message);
            ivUnfav = view.findViewById(R.id.iv_unFav);

            mSearchLocList = new ArrayList<>();


        }
    }

    Activity activity;
    FavAdapterOnCallback callback;
    ArrayList<String> ids = new ArrayList<>();
    ArrayList<String> places = new ArrayList<>();


    public FavouriteAdapter(List<FavouriteModel> mFAVList, Context mContext, Activity mActivity, FavAdapterOnCallback callback) {
        this.mContext = mContext;
        this.mFavList = mFAVList;
        this.activity = mActivity;
        this.callback = callback;


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favlist_row, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int position) {
        final FavouriteModel favList = mFavList.get(position);


        myViewHolder.tv_provider.setText(favList.getBusinessName());

        if (favList.getPlace() != null && !favList.getPlace().trim().equalsIgnoreCase("")) {
            myViewHolder.llLocation.setVisibility(View.VISIBLE);
            myViewHolder.tvLocationName.setText(favList.getPlace());
        } else {
            myViewHolder.llLocation.setVisibility(View.GONE);
        }

        myViewHolder.llLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (favList.getGoogleMapUrl() != null) {
                    Uri uri = Uri.parse(favList.getGoogleMapUrl());
                    Intent in = new Intent(Intent.ACTION_VIEW, uri);
                    mContext.startActivity(in);
                }
            }
        });

        myViewHolder.imgarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Config.isOnline(mContext)) {
                    if (!favList.isExpandFlag()) {
                        favList.setExpandFlag(true);
                        myViewHolder.Lfavlisiting.setVisibility(View.VISIBLE);
                        ids.clear();

                        if (favList.getLocationId() != null) {
                            ids = new ArrayList<String>(Arrays.asList(favList.getLocationId().split(" , ")));
                        }
                        places = new ArrayList<String>(Arrays.asList(favList.getPlace().split(" , ")));

                        Config.logV("Ids------------" + ids.size());
                        for (int i = 0; i < ids.size(); i++) {

                            Config.logV("Ids---1111---------" + ids.get(i));
                        }
                        callback.onMethodViewCallback(favList.getId(), ids, myViewHolder.mrRecylce_fav, favList.getUniqueId(), favList.getBusinessName());
                        myViewHolder.imgarrow.setImageResource(R.drawable.icon_up_light);
                        myViewHolder.Layout_fav.setBackground(mContext.getResources().getDrawable(R.drawable.input_border_top_white));
                    } else {
                        favList.setExpandFlag(false);
                        myViewHolder.Lfavlisiting.setVisibility(View.GONE);
                        myViewHolder.imgarrow.setImageResource(R.drawable.icon_down_light);
                        myViewHolder.Layout_fav.setBackground(mContext.getResources().getDrawable(R.drawable.input_background_white_round));

                    }
                }
            }
        });

        myViewHolder.llfav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailIntent = new Intent(mContext, ProviderDetailActivity.class);
                detailIntent.putExtra("uniqueID", favList.getUniqueId());
                detailIntent.putExtra("from", "fav");
                mContext.startActivity(detailIntent);
                // callback.onMethodSearchDetailCallback(favList.getUniqueId());
            }


        });

        myViewHolder.ivMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("name",favList.getBusinessName());
                intent.putExtra("accountId",favList.getId());
                intent.putExtra("from", Constants.PROVIDER);
                mContext.startActivity(intent);

            }
        });


        myViewHolder.tv_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailIntent = new Intent(mContext, ProviderDetailActivity.class);
                detailIntent.putExtra("uniqueID", favList.getUniqueId());
                detailIntent.putExtra("from", "fav");
                mContext.startActivity(detailIntent);
                //  callback.onMethodSearchDetailCallback(favList.getUniqueId());
            }
        });


        myViewHolder.ivUnfav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callback.onMethodDeleteFavourite(favList.getId());

            }
        });


        myViewHolder.ivPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog dialog = new BottomSheetDialog(mContext, R.style.DialogStyle);
                dialog.setContentView(R.layout.privacy);
                dialog.show();

                Button btn_send = (Button) dialog.findViewById(R.id.btn_send);
                Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
                TextView txtmangeprivacy = (TextView) dialog.findViewById(R.id.txtmangeprivacy);
                final CheckBox chkeprivacy = (CheckBox) dialog.findViewById(R.id.chkphone);
                Config.logV("Revel Phone--------------" + favList.isRevealPhoneNumber() + "Title" + favList.getBusinessName());
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