package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.util.AtomicFile;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jaldeeinc.jaldee.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class DetailFileImageAdapter extends RecyclerView.Adapter<DetailFileImageAdapter.MyViewHolder> {
    private final ArrayList<String> imagePathList;
    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_file_attach;
        LinearLayout fileList;
        ImageView delete_file;
        public MyViewHolder(View view) {
            super(view);
            iv_file_attach = view.findViewById(R.id.file);
            fileList = view.findViewById(R.id.fileList);
            delete_file = view.findViewById(R.id.deletefile);

        }
    }


    public DetailFileImageAdapter(ArrayList<String> mfileList, Context mContext) {
        this.imagePathList = mfileList;
        this.mContext = mContext;
    }

    @Override
    public DetailFileImageAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.file_attach_list_row, parent, false);

        return new DetailFileImageAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final DetailFileImageAdapter.MyViewHolder myViewHolder, final int position) {
        // final  fileList = mfileList.get(position);
        Log.i("path", this.imagePathList.get(position));
        String imagePath = this.imagePathList.get(position);
        if (imagePath.substring(imagePath.lastIndexOf(".") + 1).equals("pdf")) {
            myViewHolder.iv_file_attach.setVisibility(View.VISIBLE);
        } else {
//           Bitmap bitmap = BitmapFactory.decodeFile(imagePathList.get(position));
//            myViewHolder.iv_file_attach.setImageBitmap(bitmap);
//            Bitmap bitmap = BitmapFactory.decodeFile(new File(imagePathList.get(position)).getAbsolutePath()); //bitmap is always null
//            OutputStream os = null;
//            try {
//                os = new BufferedOutputStream(new FileOutputStream(new File(imagePathList.get(position))));
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//            bitmap.compress(Bitmap.CompressFormat.JPEG,100,os);
//            myViewHolder.iv_file_attach.setImageBitmap(bitmap);


            Uri imgUri=Uri.parse(this.imagePathList.get(position));
            myViewHolder.iv_file_attach.setImageURI(null);
            myViewHolder.iv_file_attach.setImageURI(imgUri);
//           try {
////
//                Bitmap bitmaps = MediaStore.Images.Media.getBitmap(this.mContext.getContentResolver(), Uri.fromFile(new File(this.imagePathList.get(position))));
//                final ByteArrayOutputStream stream = new ByteArrayOutputStream();
////                    bitmaps.compress();
//                bitmaps.compress(Bitmap.CompressFormat.JPEG, 90, stream);
//                byte[] byteArray = stream.toByteArray();
//                String encodeded = Base64.encodeToString(byteArray, Base64.DEFAULT);
//                byte[] decodedString = Base64.decode(encodeded, Base64.DEFAULT);
//                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//                myViewHolder.iv_file_attach.setImageBitmap(decodedByte);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            myViewHolder.iv_file_attach.setImageURI(null);
//            myViewHolder.iv_file_attach.setImageURI(imgUri);


//            File file = new File(imagePath);
//            AtomicFile atomicFile =  new AtomicFile(file);
//            FileOutputStream fos = null;
//            try {
//                // read the current image
//                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//                // open the stream (backup the current content)
//                // from now on (and until finishWrite/failWrite) we cannot read the file directly
//                fos = atomicFile.startWrite();
////                Log.d("showmethebitmap", bitmap.toString()); //Error: bitmap is null !
//                OutputStream oos = new BufferedOutputStream(fos);
//                bitmap.compress(Bitmap.CompressFormat.JPEG,100,oos);
//                // flush but do not close the stream (@see AtomicFile doc)
//                oos.flush();
//                // close the stream, remove the backup
//                atomicFile.finishWrite(fos);
//              if(bitmap!=null){
//              myViewHolder.iv_file_attach.setImageBitmap(bitmap);}
//            } catch (IOException e) {
//                // recover the content from the backup
//                atomicFile.failWrite(fos);
//                try {
//                    throw e;
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                }
//            }




//            BitmapFactory.Options options;
//
//            try {
//                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
//                myViewHolder.iv_file_attach.setImageBitmap(bitmap);
//
//            } catch (OutOfMemoryError e) {
//                try {
//
//                    options = new BitmapFactory.Options();
//                    options.inSampleSize = 2;
//                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath,options);
//                    myViewHolder.iv_file_attach.setImageBitmap(bitmap);
//
//                } catch(Exception excepetion) {
//                    Log.i("hujv",excepetion.toString());
//                }
//
//            }



        }

        myViewHolder.delete_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePathList.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.i("count",String.valueOf(imagePathList.size()));
        return imagePathList.size();
    }
}

