package com.nv.youneverwait.database;

/**
 * Created by sharmila on 10/7/18.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nv.youneverwait.R;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.response.ActiveCheckIn;
import com.nv.youneverwait.response.FavouriteModel;
import com.nv.youneverwait.response.InboxModel;
import com.nv.youneverwait.response.ProfileModel;

import java.util.ArrayList;
import java.util.List;

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
        createTableCheckin(db);

        createTableFavour(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + mContext.getString(R.string.db_table_userinfo));
        db.execSQL("DROP TABLE IF EXISTS " + mContext.getString(R.string.db_table_inbox));

        db.execSQL("DROP TABLE IF EXISTS " + mContext.getString(R.string.db_table_checkin));

        db.execSQL("DROP TABLE IF EXISTS " + mContext.getString(R.string.db_table_fav));
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

    private void createTableFavour(SQLiteDatabase db) {
        String tblFields, tblCreateStr;

        tblFields = "( id INTEGER PRIMARY KEY,"
                + "businessname TEXT,"
                + "locid TEXT,"
                + "uniqueid INTEGER,"
                + "isRevelPhoneno boolean)";


        //create table
        tblCreateStr = "CREATE TABLE IF NOT EXISTS " + mContext.getString(R.string.db_table_fav) + tblFields;
        db.execSQL(tblCreateStr);
    }

    public void insertFavInfo(FavouriteModel favorite) {

        SQLiteDatabase db = new DatabaseHandler(mContext).getWritableDatabase();
        db.beginTransaction();
        try {
           // for (FavouriteModel favorite : favouriteModel) {

                ContentValues values = new ContentValues();
                values.put("id", favorite.getId());
                values.put("businessname", favorite.getBusinessName());
                values.put("uniqueId", favorite.getUniqueId());
                values.put("locid", favorite.getLocationId());
                values.put("isRevelPhoneno", favorite.isRevealPhoneNumber());


                db.insert(mContext.getString(R.string.db_table_fav), null, values);
           // }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }


    }


    public ArrayList<FavouriteModel> getFavourites() {
        SQLiteDatabase db = new DatabaseHandler(mContext).getReadableDatabase();
        ArrayList<FavouriteModel> favData = new ArrayList<FavouriteModel>();
        String table = mContext.getString(R.string.db_table_fav);
       /* String[] columns = {"provider", "service", "id", "timestamp", "uniqueID","receiverID","message","receiverName", "messageStatus","waitlistId"};*/
        String[] columns = {"id","businessname","locid","uniqueid","isRevelPhoneno"};



        //String timestamp="timestamp";

        db.beginTransaction();

        Cursor cursor = db.query(table, columns, null, null, null, null, null);
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            do {
                FavouriteModel fav = new FavouriteModel();

                fav.setId(cursor.getInt(0));
                fav.setBusinessName(cursor.getString(1));
                fav.setLocationId(cursor.getString(2));
                fav.setUniqueId(cursor.getInt(3));
                boolean phone=cursor.getInt(4)>0;
                fav.setRevealPhoneNumber(phone);




                favData.add(fav);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

        return favData;


    }


    /*Table userInfo*/
    private void createTableCheckin(SQLiteDatabase db) {
        String tblFields, tblCreateStr;

        tblFields = "( id INTEGER ,"
                + "businessName TEXT,"
                + "uniqueId TEXT,"
                + "date TEXT,"
                + "waitlistStatus TEXT,"
                + "servicename TEXT,"
                + "partySize INTEGER,"
                + "appxWaitingTime INTEGER,"
                + "place TEXT,"
                + "googleMapUrl TEXT,"
                + "queueStartTime TEXT,"
                + "firstName TEXT,"
                + "lastName TEXT,"
                + "ynwUuid TEXT,"
                + "paymentStatus TEXT,"
                + "billViewStatus TEXT,"
                + "billStatus TEXT,"
                + "amountPaid DOUBLE,"
                + "amountDue DOUBLE,"
                + "serviceTime TEXT)";

        //create table
        tblCreateStr = "CREATE TABLE IF NOT EXISTS " + mContext.getString(R.string.db_table_checkin) + tblFields;
        db.execSQL(tblCreateStr);
    }


    public void insertCheckinInfo(List<ActiveCheckIn> activeCheckInModel) {
        Config.logV("InsertCheckinDetails"+activeCheckInModel.size());
        SQLiteDatabase db = new DatabaseHandler(mContext).getWritableDatabase();
        db.beginTransaction();
        try {
            for (ActiveCheckIn activeCheckIn : activeCheckInModel) {
                Config.logV("InsertCheckinDetails"+activeCheckIn.getProvider().getId());
                ContentValues values = new ContentValues();
                values.put("id", activeCheckIn.getProvider().getId());
                values.put("businessName", activeCheckIn.getProvider().getBusinessName());
                values.put("uniqueId", activeCheckIn.getProvider().getUniqueId());
                values.put("date", activeCheckIn.getDate());
                values.put("waitlistStatus", activeCheckIn.getWaitlistStatus());
                values.put("servicename", activeCheckIn.getService().getName());
                values.put("partySize", activeCheckIn.getPartySize());
                values.put("appxWaitingTime", activeCheckIn.getAppxWaitingTime());
                values.put("place", activeCheckIn.getQueue().getLocation().getPlace());
                values.put("googleMapUrl", activeCheckIn.getQueue().getLocation().getGoogleMapUrl());
                values.put("queueStartTime", activeCheckIn.getQueue().getQueueStartTime());
                values.put("firstName", activeCheckIn.getWaitlistingFor().get(0).getFirstName());
                values.put("lastName", activeCheckIn.getWaitlistingFor().get(0).getLastName());

                values.put("ynwUuid", activeCheckIn.getYnwUuid());
                values.put("paymentStatus", activeCheckIn.getPaymentStatus());
                values.put("billViewStatus", activeCheckIn.getBillViewStatus());
                values.put("billStatus", activeCheckIn.getBillStatus());
                values.put("amountPaid", activeCheckIn.getAmountPaid());
                values.put("amountDue", activeCheckIn.getAmountDue());
                values.put("serviceTime", activeCheckIn.getServiceTime());

                db.insert(mContext.getString(R.string.db_table_checkin), null, values);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }


    }

    public List<ActiveCheckIn> getAllCheckinList() {
        List<ActiveCheckIn> checkin = new ArrayList<ActiveCheckIn>();

        SQLiteDatabase db = new DatabaseHandler(mContext).getReadableDatabase();

        String table = mContext.getString(R.string.db_table_checkin);
        // String[] columns = {"provider", "service", "id", "timestamp", "uniqueID","receiverID","message", "receiverName", "messageStatus","waitlistId"};

        String[] columns = {"id", "businessName", "uniqueId", "date", "waitlistStatus", "servicename", "partySize", "appxWaitingTime", "place", "googleMapUrl", "queueStartTime", "firstName", "lastName", "ynwUuid", "paymentStatus", "billViewStatus", "billStatus", "amountPaid", "amountDue","serviceTime"};

        db.beginTransaction();

        Cursor cursor = db.query(table, columns, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                ActiveCheckIn activeModel = new ActiveCheckIn();
                activeModel.setId(cursor.getInt(0));
                activeModel.setBusinessName(cursor.getString(1));
                activeModel.setUniqueId(cursor.getString(2));
                activeModel.setDate(cursor.getString(3));
                activeModel.setWaitlistStatus(cursor.getString(4));
                activeModel.setName(cursor.getString(5));
                activeModel.setPartySize(cursor.getInt(6));
                activeModel.setAppxWaitingTime(cursor.getInt(7));

                activeModel.setPlace(cursor.getString(8));
                activeModel.setGoogleMapUrl(cursor.getString(9));
                activeModel.setQueueStartTime(cursor.getString(10));

                activeModel.setFirstName(cursor.getString(11));
                activeModel.setLastName(cursor.getString(12));
                activeModel.setYnwUuid(cursor.getString(13));
                activeModel.setPaymentStatus(cursor.getString(14));

                activeModel.setBillViewStatus(cursor.getString(15));
                activeModel.setBillStatus(cursor.getString(16));
                activeModel.setAmountPaid(cursor.getDouble(17));
                activeModel.setAmountDue(cursor.getDouble(18));

                activeModel.setServiceTime(cursor.getString(19));



                checkin.add(activeModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

        return checkin;
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


            db.update(mContext.getString(R.string.db_table_userinfo), values, "id=" + profileModel.getId(), null);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }


    }

    public boolean checkForTables() {
        boolean hasTables = false;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + mContext.getString(R.string.db_table_userinfo), null);

        if (cursor != null && cursor.getCount() > 0) {
            hasTables = true;
            cursor.close();
        }

        return hasTables;
    }

    public ProfileModel getProfileDetail(int consumerID) {
        SQLiteDatabase db = new DatabaseHandler(mContext).getReadableDatabase();

        String table = mContext.getString(R.string.db_table_userinfo);
        String[] columns = {"id", "firstname", "lastname", "email", "primaryMobNo", "emailVerified", "phoneVerified", "gender", "dob"};

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
                + "message TEXT,"
                + "uniqueID TEXT,"
              /*  + "receiverID INT,"
                + "receiverName TEXT,"*/
                + "messageStatus TEXT,"
                + "waitlistId TEXT)";

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
                values.put("uniqueID", inbox.getUniqueID());
                values.put("waitlistId", inbox.getWaitlistId());

              /*  values.put("receiverID", inbox.getReceiver().getReceiverId());
                values.put("receiverName", inbox.getReceiver().getReceiverName());*/

                values.put("messageStatus", inbox.getMessageStatus());

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


    public void insertInboxModel(InboxModel inbox) {
        // Config.logV("Insert Inbox");
        SQLiteDatabase db = new DatabaseHandler(mContext).getWritableDatabase();
        db.beginTransaction();

        try {

            ContentValues values = new ContentValues();
            values.put("provider", inbox.getUserName());
            values.put("service", inbox.getService());
            values.put("id", inbox.getId());
            values.put("timestamp", inbox.getTimeStamp());
            values.put("message", inbox.getMsg());
            values.put("uniqueID", inbox.getUniqueID());
            values.put("waitlistId", inbox.getWaitlistId());

               /* values.put("receiverID", inbox.getReceiver().getReceiverId());
                values.put("receiverName", inbox.getReceiver().getReceiverName());*/

            values.put("messageStatus", inbox.getMessageStatus());

            db.insert(mContext.getString(R.string.db_table_inbox), null, values);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }


    }

    public void DeleteCheckin() {
        SQLiteDatabase db = new DatabaseHandler(mContext).getWritableDatabase();
        db.execSQL("delete from " + mContext.getString(R.string.db_table_checkin));
        db.close();
    }

    public void DeleteFav() {
        SQLiteDatabase db = new DatabaseHandler(mContext).getWritableDatabase();
        db.execSQL("delete from " + mContext.getString(R.string.db_table_fav));
        db.close();
    }


    public void DeleteInbox() {
        SQLiteDatabase db = new DatabaseHandler(mContext).getWritableDatabase();
        db.execSQL("delete from " + mContext.getString(R.string.db_table_inbox));
        db.close();
    }

    public void DeleteProfile() {
        SQLiteDatabase db = new DatabaseHandler(mContext).getWritableDatabase();
        db.execSQL("delete from " + mContext.getString(R.string.db_table_userinfo));
        db.close();
    }

    public void deleteDatabase() {
        boolean succes = mContext.deleteDatabase(mContext.getResources().getString(R.string.db_name));
        Config.logV("data base deleted........" + succes);
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
                values.put("uniqueID", inbox.getUniqueID());
                values.put("waitlistId", inbox.getWaitlistId());
               /* values.put("receiverID", inbox.getReceiver().getReceiverId());
                values.put("receiverName", inbox.getReceiver().getReceiverName());*/
                values.put("messageStatus", inbox.getMessageStatus());


                db.update(mContext.getString(R.string.db_table_inbox), values, "timestamp=" + inbox.getTimeStamp(), null);

            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }


    }

    public ArrayList<InboxModel> getInboxDetail(String uniqueID) {
        SQLiteDatabase db = new DatabaseHandler(mContext).getReadableDatabase();
        ArrayList<InboxModel> inboxData = new ArrayList<InboxModel>();
        String table = mContext.getString(R.string.db_table_inbox);
       /* String[] columns = {"provider", "service", "id", "timestamp", "uniqueID","receiverID","message","receiverName", "messageStatus","waitlistId"};*/
        String[] columns = {"provider", "service", "id", "timestamp", "uniqueID", "message", "messageStatus", "waitlistId"};
        String selection = " uniqueID =?";
        String[] selectionArgs = new String[]{uniqueID};

        //String timestamp="timestamp";

        db.beginTransaction();

        Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, null);
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            do {
                InboxModel inbox = new InboxModel();
                inbox.setUserName(cursor.getString(0));
                inbox.setService(cursor.getString(1));
                inbox.setId(cursor.getInt(2));
                inbox.setTimeStamp(cursor.getLong(3));
                inbox.setUniqueID(cursor.getString(4));
                // inbox.setReceiverId(cursor.getInt(5));
                inbox.setMsg(cursor.getString(5));
                //   inbox.setRecevierName(cursor.getString(7));
                inbox.setMessageStatus(cursor.getString(6));
                inbox.setWaitlistId(cursor.getString(7));


                inboxData.add(inbox);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

        return inboxData;


    }


    public ArrayList<InboxModel> getAllInboxDetail() {
        ArrayList<InboxModel> inbox = new ArrayList<InboxModel>();

        SQLiteDatabase db = new DatabaseHandler(mContext).getReadableDatabase();

        String table = mContext.getString(R.string.db_table_inbox);
        // String[] columns = {"provider", "service", "id", "timestamp", "uniqueID","receiverID","message", "receiverName", "messageStatus","waitlistId"};

        String[] columns = {"provider", "service", "id", "timestamp", "uniqueID", "message", "messageStatus", "waitlistId"};
        String groupBy = "uniqueID";
        db.beginTransaction();

        Cursor cursor = db.query(table, columns, null, null, groupBy, null, null);
        if (cursor.moveToFirst()) {
            do {
                InboxModel inboxModel = new InboxModel();
                inboxModel.setUserName(cursor.getString(0));
                inboxModel.setService(cursor.getString(1));
                inboxModel.setId(cursor.getInt(2));
                inboxModel.setTimeStamp(cursor.getLong(3));
                inboxModel.setUniqueID(cursor.getString(4));

                //  inboxModel.setReceiverId(cursor.getInt(5));
                inboxModel.setMsg(cursor.getString(5));

                // inboxModel.setRecevierName(cursor.getString(7));
                inboxModel.setMessageStatus(cursor.getString(6));
                inboxModel.setWaitlistId(cursor.getString(7));


                inbox.add(inboxModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

        return inbox;
    }


    public boolean CheckIsInboxTBDataorNot() {

        String TableName = mContext.getString(R.string.db_table_inbox);
        SQLiteDatabase db = new DatabaseHandler(mContext).getReadableDatabase();
        String Query = "Select * from " + TableName;
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public boolean CheckIsDataAlreadyInDBorNot(String fieldValue) {

        String TableName = mContext.getString(R.string.db_table_inbox);
        SQLiteDatabase db = new DatabaseHandler(mContext).getReadableDatabase();
        String Query = "Select * from " + TableName + " where timestamp = " + fieldValue;
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
}
