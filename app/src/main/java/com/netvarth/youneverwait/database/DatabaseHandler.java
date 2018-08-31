package com.netvarth.youneverwait.database;

/**
 * Created by sharmila on 10/7/18.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

import com.netvarth.youneverwait.R;
import com.netvarth.youneverwait.common.Config;
import com.netvarth.youneverwait.response.InboxModel;
import com.netvarth.youneverwait.response.ProfileModel;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

    private Context mContext;
    private static DatabaseHandler mInstance = null;

    public DatabaseHandler(Context context) {
        super(context, context.getString(R.string.db_name), null, Integer.parseInt(context.getString(R.string.db_version)));
        mContext = context;
    }

    public DatabaseHandler(Context context, int version) {
        super(context, context.getResources().getString(R.string.db_name), null, version);
        mContext = context;
    }

    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
        mContext = context;
    }

    public static DatabaseHandler getInstance(Context context) {
        // Use the application context, which will ensure that you don't accidentally leak an Context's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance == null) {
            mInstance = new DatabaseHandler(context);
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //createTableDoctorDetails(db);
        createTableUserInfo(db);
        createTableInbox(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + mContext.getString(R.string.db_table_userinfo));
        db.execSQL("DROP TABLE IF EXISTS " + mContext.getString(R.string.db_table_inbox));

        // Create tables again
        onCreate(db);
    }

    /*Table userInfo*/
    private void createTableUserInfo(SQLiteDatabase db) {
        String tblFields, tblCreateStr;

        tblFields = "( id INTEGER PRIMARY KEY,"
                + "firstname TEXT,"
                + "lastname TEXT,"
                + "email TEXT,"
                + "primaryMobNo TEXT,"
                + "emailVerified boolean,"
                + "phoneVerified boolean,"
                + "gender TEXT,"
                + "dob TEXT)";

        //create table
        tblCreateStr = "CREATE TABLE IF NOT EXISTS " + mContext.getString(R.string.db_table_userinfo) + tblFields;
        db.execSQL(tblCreateStr);
    }


    public void insertUserInfo(ProfileModel profileModel) {
        Config.logV("InsertProfileDetails");
        SQLiteDatabase db = new DatabaseHandler(mContext).getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put("id", profileModel.getId());
            values.put("firstname", profileModel.getFirstName());
            values.put("lastname", profileModel.getLastName());
            values.put("email", profileModel.getEmail());
            values.put("primaryMobNo", profileModel.getPrimaryMobileNo());
            values.put("emailVerified", profileModel.getEmailVerified());
            values.put("phoneVerified", profileModel.getPhoneVerified());
            values.put("gender", profileModel.getGender());
            values.put("dob", profileModel.getDob());


            db.insert(mContext.getString(R.string.db_table_userinfo), null, values);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }


    }

    public void updateUserInfo(ProfileModel profileModel) {
        Config.logV("InsertProfileDetails");
        SQLiteDatabase db = new DatabaseHandler(mContext).getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put("id", profileModel.getId());
            values.put("firstname", profileModel.getFirstName());
            values.put("lastname", profileModel.getLastName());
            values.put("email", profileModel.getEmail());
            values.put("primaryMobNo", profileModel.getPrimaryMobileNo());
            values.put("emailVerified", profileModel.getEmailVerified());
            values.put("phoneVerified", profileModel.getPhoneVerified());
            values.put("gender", profileModel.getGender());
            values.put("dob", profileModel.getDob());


            db.update(mContext.getString(R.string.db_table_userinfo), values, "id="+profileModel.getId(), null);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }


    }

    public ProfileModel getProfileDetail(int consumerID) {
        SQLiteDatabase db = new DatabaseHandler(mContext).getReadableDatabase();

        String table = mContext.getString(R.string.db_table_userinfo);
        String[] columns = {"id", "firstname", "lastname", "email", "primaryMobNo", "emailVerified", "phoneVerified","gender","dob"};

        String selection = " id =?";
        String[] selectionArgs = new String[]{String.valueOf(consumerID)};

        db.beginTransaction();

        Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, null);
        cursor.moveToFirst();

        ProfileModel profile = new ProfileModel();
        profile.setId(cursor.getInt(0));
        profile.setFirstName(cursor.getString(1));
        profile.setLastName(cursor.getString(2));
        profile.setEmail(cursor.getString(3));
        profile.setPrimaryMobileNo(cursor.getString(4));
        profile.setEmailVerified(Boolean.parseBoolean(cursor.getString(5)));
        profile.setPhoneVerified(Boolean.parseBoolean(cursor.getString(6)));
        profile.setGender(cursor.getString(7));
        profile.setDob(cursor.getString(8));


        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

        return profile;


    }


    //Table Inbox

    /*Table userInfo*/
    private void createTableInbox(SQLiteDatabase db) {
        String tblFields, tblCreateStr;

        tblFields = "( provider TEXT,"
                + "service TEXT,"
                + "id INT,"
                + "timestamp TEXT,"
                + "message TEXT)";

        //create table
        tblCreateStr = "CREATE TABLE IF NOT EXISTS " + mContext.getString(R.string.db_table_inbox) + tblFields;
        db.execSQL(tblCreateStr);
    }




    public void insertInbox(ArrayList<InboxModel> inboxModel) {
        Config.logV("Insert Inbox");
        SQLiteDatabase db = new DatabaseHandler(mContext).getWritableDatabase();
        db.beginTransaction();

        try {
            for (InboxModel inbox : inboxModel) {
                ContentValues values = new ContentValues();
                values.put("provider", inbox.getOwner().getUserName());
                values.put("service", inbox.getService());
                values.put("id", inbox.getOwner().getId());
                values.put("timestamp", inbox.getTimeStamp());
                values.put("message", inbox.getMsg());

                db.insert(mContext.getString(R.string.db_table_inbox), null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }


    }




    public void updateInboxInfo(ArrayList<InboxModel> inboxModel) {
        Config.logV("UpdateInboxDetails");
        SQLiteDatabase db = new DatabaseHandler(mContext).getWritableDatabase();
        db.beginTransaction();

        try {
            for (InboxModel inbox : inboxModel) {
            ContentValues values = new ContentValues();
            values.put("provider", inbox.getOwner().getUserName());
            values.put("service", inbox.getService());
            values.put("id", inbox.getOwner().getId());
            values.put("timestamp", inbox.getTimeStamp());
            values.put("message", inbox.getMsg());



            db.update(mContext.getString(R.string.db_table_inbox), values, "timestamp="+inbox.getTimeStamp(), null);

            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }


    }

    public InboxModel getInboxDetail(String provider) {
        SQLiteDatabase db = new DatabaseHandler(mContext).getReadableDatabase();

        String table = mContext.getString(R.string.db_table_inbox);
        String[] columns = {"provider", "service", "id","timestamp", "message"};

        String selection = " provider =?";
        String[] selectionArgs = new String[]{provider};

        db.beginTransaction();

        Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, null);
        cursor.moveToFirst();

        InboxModel inbox = new InboxModel();
        inbox.setUserName(cursor.getString(0));
        inbox.setService(cursor.getString(1));
        inbox.setId(cursor.getInt(2));
        inbox.setTimeStamp(cursor.getLong(3));
        inbox.setMsg(cursor.getString(4));



        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

        return inbox;


    }




    public ArrayList<InboxModel> getAllInboxDetail() {
        ArrayList<InboxModel> inbox = new ArrayList<InboxModel>();

        SQLiteDatabase db = new DatabaseHandler(mContext).getReadableDatabase();

        String table = mContext.getString(R.string.db_table_inbox);
        String[] columns = {"provider", "service", "id","timestamp", "message"};

        db.beginTransaction();

        Cursor cursor = db.query(table, columns, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                InboxModel inboxModel = new InboxModel();
                inboxModel.setUserName(cursor.getString(0));
                inboxModel.setService(cursor.getString(1));
                inboxModel.setId(cursor.getInt(2));
                inboxModel.setTimeStamp(cursor.getLong(3));
                inboxModel.setMsg(cursor.getString(4));


                inbox.add(inboxModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

        return inbox;
    }


    public  boolean CheckIsInboxTBDataorNot() {

        String TableName = mContext.getString(R.string.db_table_inbox);
        SQLiteDatabase db = new DatabaseHandler(mContext).getReadableDatabase();
        String Query = "Select * from " + TableName;
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
}
