package com.jaldeeinc.customapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jaldeeinc.jaldeebusiness.R;

public class DatabaseHandler extends SQLiteOpenHelper {

    private Context mContext;

    public DatabaseHandler(Context context) {
        super(context, context.getString(R.string.db_name), null, Integer.parseInt(context.getString(R.string.db_version)));
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createTableCacheIndex(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void createTableCacheIndex(SQLiteDatabase db) {
        String tblFields, tblCreateStr;

        tblFields = "(cacheindex INTEGER)";

        //create table
        tblCreateStr = "CREATE TABLE IF NOT EXISTS " + mContext.getString(R.string.db_table_cacheIndex) + tblFields;
        db.execSQL(tblCreateStr);
    }

    public void insertCacheIndexValue(Integer cacheIndex) {

        SQLiteDatabase db = new DatabaseHandler(mContext).getWritableDatabase();
        db.beginTransaction();
        try {

            ContentValues values = new ContentValues();
            values.put("cacheindex", cacheIndex);

            db.insert(mContext.getString(R.string.db_table_cacheIndex), null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public Integer getCacheIndex() {
        SQLiteDatabase db = new DatabaseHandler(mContext).getReadableDatabase();

        Integer cacheIndex = null;

        String table = mContext.getString(R.string.db_table_cacheIndex);
        String[] columns = {"cacheindex"};

        db.beginTransaction();

        Cursor cursor = db.query(table, columns, null, null, null, null, null);
        cursor.moveToFirst();

        if (cursor.moveToFirst()) {
            do {
                cacheIndex = cursor.getInt(0);
            } while (cursor.moveToNext());
        }

        cursor.close();

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

        return cacheIndex;
    }

    public void updateCacheIndex(Integer cacheIndex) {
        try {
            SQLiteDatabase db = new DatabaseHandler(mContext).getWritableDatabase();
            db.execSQL("update  " + mContext.getString(R.string.db_table_cacheIndex) + " set cacheindex = " + cacheIndex);
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
