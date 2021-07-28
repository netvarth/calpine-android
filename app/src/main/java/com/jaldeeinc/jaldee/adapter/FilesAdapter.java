package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jaldeeinc.jaldee.BuildConfig;
import com.jaldeeinc.jaldee.Interface.IEditAddress;
import com.jaldeeinc.jaldee.Interface.IFilesInterface;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.AudioActivity;
import com.jaldeeinc.jaldee.activities.CustomQuestionnaire;
import com.jaldeeinc.jaldee.activities.ImageActivity;
import com.jaldeeinc.jaldee.activities.UpdateQuestionnaire;
import com.jaldeeinc.jaldee.activities.VideoActivity;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.KeyPairBoolData;
import com.jaldeeinc.jaldee.model.Address;

import java.io.File;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.ViewHolder> {

    List<KeyPairBoolData> filesList = new ArrayList<>();
    public Context context;
    private boolean isLoading = true;
    private int lastPosition = -1;
    private int selectedPosition = 0;
    private IFilesInterface iFilesInterface;
    private String labelName = "";
    String[] videoFormats = new String[]{"wmv", "mp4", "webm", "flw", "mov", "avi",".wmv", ".mp4", ".webm", ".flw", ".mov", ".avi"};
    String[] formats = new String[]{"wmv", "mp4", "webm", "flw", "mov", "avi",".wmv", ".mp4", ".webm", ".flw", ".mov", ".avi"};



    public FilesAdapter(ArrayList<KeyPairBoolData> fList, Context context, boolean isLoading, IFilesInterface iFilesInterface) {
        this.filesList = fList;
        this.context = context;
        this.isLoading = isLoading;
        this.iFilesInterface = iFilesInterface;
    }

    @NonNull
    @Override
    public FilesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (isLoading) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_line_shimmer, parent, false);
            return new FilesAdapter.ViewHolder(v, true);

        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item, parent, false);
            return new FilesAdapter.ViewHolder(v, false);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull FilesAdapter.ViewHolder viewHolder, int position) {

        if (!isLoading) {

            final KeyPairBoolData data = filesList.get(position);

            viewHolder.tvFileText.setText(data.getName());

            if (data.getImagePath() != null && !data.getImagePath().trim().equalsIgnoreCase("")) {

                if (data.getImagePath().contains("http://") || data.getImagePath().contains("https://")) {

                    String extension = "";

                    if (data.getType() != null) {
                        extension = data.getType().substring(data.getType().lastIndexOf("/") + 1);
                    }

                    if (data.getType() != null && data.getType().equalsIgnoreCase(".pdf")) {

                        viewHolder.ivFile.setImageDrawable(context.getResources().getDrawable(R.drawable.pdfs));

                    } else if (data.getType()!= null && data.getType().contains("audio")) {

                        viewHolder.ivFile.setImageDrawable(context.getResources().getDrawable(R.drawable.audio_icon));

                    } else if (Arrays.asList(formats).contains(extension)) {

                        viewHolder.ivFile.setImageDrawable(context.getResources().getDrawable(R.drawable.video_icon));

                    }
                    else {
                        Glide.with(context).load(data.getImagePath()).into(viewHolder.ivFile);
                    }
                } else {

                    String extension = "";

                    if (data.getImagePath().contains(".")) {
                        extension = data.getImagePath().substring(data.getImagePath().lastIndexOf(".") + 1);
                    }

                    if (data.getImagePath().substring(data.getImagePath().lastIndexOf(".") + 1).equals("pdf")) {

                        viewHolder.ivFile.setImageDrawable(context.getResources().getDrawable(R.drawable.pdfs));

                    } else if (Arrays.asList(formats).contains(extension)) {

                        viewHolder.ivFile.setImageDrawable(context.getResources().getDrawable(R.drawable.video_icon));

                    } else if (data.getImagePath().substring(data.getImagePath().lastIndexOf(".") + 1).equals("mp3")) {

                        viewHolder.ivFile.setImageDrawable(context.getResources().getDrawable(R.drawable.audio_icon));

                    } else {

                        viewHolder.ivFile.setImageBitmap(BitmapFactory.decodeFile(data.getImagePath()));
                    }
                }
                viewHolder.llFileLayout.setVisibility(View.VISIBLE);
            } else {

                viewHolder.ivFile.setImageDrawable(null);
                viewHolder.llFileLayout.setVisibility(View.VISIBLE);

            }

            viewHolder.ivFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    if (data.getImagePath().contains("http://") || data.getImagePath().contains("https://")) {

                        String extension = "";

                        if (data.getType() != null) {
                            extension = data.getType().substring(data.getType().lastIndexOf("/") + 1);
                        }

                        if (data.getType() != null && data.getType().equalsIgnoreCase(".pdf")) {

                            openOnlinePdf(context, data.getImagePath());

                        } else if (Arrays.asList(formats).contains(extension)) {

                            Intent intent = new Intent(context, VideoActivity.class);
                            intent.putExtra("urlOrPath", data.getImagePath());
                            context.startActivity(intent);

                        } else if (data.getType()!= null && data.getType().contains("audio")) {

                            Intent viewMediaIntent = new Intent();
                            viewMediaIntent.setAction(android.content.Intent.ACTION_VIEW);
                            viewMediaIntent.setDataAndType(Uri.parse(data.getImagePath()), "audio/*");
                            viewMediaIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            context.startActivity(viewMediaIntent);

                        } else {

                            Intent intent = new Intent(context, ImageActivity.class);
                            intent.putExtra("urlOrPath", data.getImagePath());
                            context.startActivity(intent);
                        }

                    } else {

                        String extension = "";

                        if (data.getImagePath().contains(".")) {
                            extension = data.getImagePath().substring(data.getImagePath().lastIndexOf(".") + 1);
                        }

                        if (data.getImagePath().substring(data.getImagePath().lastIndexOf(".") + 1).equals("pdf")) {

                            openPdf(context, data.getImagePath());

                        } else if (Arrays.asList(formats).contains(extension)) {

                            Intent intent = new Intent(context, VideoActivity.class);
                            intent.putExtra("urlOrPath", data.getImagePath());
                            context.startActivity(intent);

                        } else if (extension.contains("mp3")) {

                           playAudio(data.getImagePath());


                        } else {
                            Intent intent = new Intent(context, ImageActivity.class);
                            intent.putExtra("urlOrPath", data.getImagePath());
                            context.startActivity(intent);
                        }
                    }

                }
            });

            viewHolder.llUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    iFilesInterface.onFileUploadClick(data, labelName);
                }
            });

            if (viewHolder.ivFile.getDrawable() != null) {

                viewHolder.ivClose.setVisibility(View.VISIBLE);
            } else {
                viewHolder.ivClose.setVisibility(View.GONE);
            }

            viewHolder.ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    iFilesInterface.onCloseClick(data);

                    viewHolder.ivFile.setImageDrawable(null);
                    viewHolder.ivFile.setImageBitmap(null);
                    filesList.get(position).setImagePath(null);
                    filesList.remove(position);
                    notifyDataSetChanged();

//                    for (KeyPairBoolData obj : filesList) {
//
//                        if (data.getId() == obj.getId()) {
//
////                            viewHolder.ivFile.setImageDrawable(null);
//                            filesList.remove(data);
//                            notifyDataSetChanged();
//                        }
//                    }

                }
            });


        } else {

            FilesAdapter.ViewHolder skeletonViewHolder = (FilesAdapter.ViewHolder) viewHolder;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            skeletonViewHolder.itemView.setLayoutParams(params);

        }
    }

    private void playAudio(String imagePath) {

        Intent i = new Intent(android.content.Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        i.setDataAndType(FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider",new File(imagePath)), "audio/*");
        context.startActivity(i);
    }

    @Override
    public int getItemCount() {
        return isLoading ? 10 : filesList.size();
    }

    public void setLabelName(String label) {

        this.labelName = label;

    }

    public List<KeyPairBoolData> getFiles() {


        return filesList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CustomTextViewSemiBold tvFileText;
        private ImageView ivFile;
        private LinearLayout llFileLayout, llUpload;
        private ImageView ivClose;

        public ViewHolder(@NonNull View itemView, boolean isLoading) {

            super(itemView);

            if (!isLoading) {

                ivFile = itemView.findViewById(R.id.iv_file);
                tvFileText = itemView.findViewById(R.id.tv_fileText);
                ivClose = itemView.findViewById(R.id.iv_close);
                llFileLayout = itemView.findViewById(R.id.ll_fileLayout);
                llUpload = itemView.findViewById(R.id.ll_upload);


            }
        }
    }

    public void updateData(List<KeyPairBoolData> list) {

        list = list == null ? new ArrayList<>() : list;
        this.filesList = list;
        notifyDataSetChanged();
    }

    public void updateFileObject(KeyPairBoolData obj) {

        for (KeyPairBoolData file : filesList) {

            if (file.getName() == obj.getName()) {

                file.setName(obj.getName());
                file.setImagePath(obj.getImagePath());
            }
        }
        notifyDataSetChanged();
    }


    public void openPdf(Context context, String path) {
        File file = new File(path);
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/pdf");
            PackageManager pm = context.getPackageManager();
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setType("application/pdf");
            Intent openInChooser = Intent.createChooser(intent, "Choose");
            List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
            if (resInfo.size() > 0) {
                try {
                    context.startActivity(openInChooser);
                } catch (Throwable throwable) {
                    Toast.makeText(context, "PDF apps are not installed", Toast.LENGTH_SHORT).show();
                    // PDF apps are not installed
                }
            } else {
                Toast.makeText(context, "PDF apps are not installed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openOnlinePdf(Context mContext, String filePath) {

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(filePath));
        context.startActivity(browserIntent);
    }

}
