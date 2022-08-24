package com.jaldeeinc.jaldee.database;

/**
 * Created by sharmila on 10/7/18.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.model.CartItemModel;
import com.jaldeeinc.jaldee.model.FileAttachment;
import com.jaldeeinc.jaldee.model.OrderItem;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;
import com.jaldeeinc.jaldee.response.CatalogItem;
import com.jaldeeinc.jaldee.response.ConsumerDetails;
import com.jaldeeinc.jaldee.response.FavouriteModel;
import com.jaldeeinc.jaldee.response.InboxModel;
import com.jaldeeinc.jaldee.response.JaldeeWaitlistDistanceTime;
import com.jaldeeinc.jaldee.response.LocationDetails;
import com.jaldeeinc.jaldee.response.ProfileModel;
import com.jaldeeinc.jaldee.model.Domain_Spinner;
import com.jaldeeinc.jaldee.model.SearchModel;
import com.jaldeeinc.jaldee.response.ProviderDetails;
import com.jaldeeinc.jaldee.response.RatingResponse;
import com.jaldeeinc.jaldee.response.ServiceDetails;
import com.jaldeeinc.jaldee.response.VirtualServiceDetails;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;

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
        createTablePopularSearchKeyWord(db);
        createTableDomain(db);
        createTableInbox(db);
        createTableCheckin(db);
        createTableSubDomain(db);
        createTableFavour(db);
        createTableMyCheckin(db);
        createTableFavID(db);
        createTableCart(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + mContext.getString(R.string.db_table_userinfo));
        db.execSQL("DROP TABLE IF EXISTS " + mContext.getString(R.string.db_table_inbox));

        db.execSQL("DROP TABLE IF EXISTS " + mContext.getString(R.string.db_table_checkin));

        db.execSQL("DROP TABLE IF EXISTS " + mContext.getString(R.string.db_table_fav));

        db.execSQL("DROP TABLE IF EXISTS " + mContext.getString(R.string.db_table_popularsearch));

        db.execSQL("DROP TABLE IF EXISTS " + mContext.getString(R.string.db_table_domain));
        db.execSQL("DROP TABLE IF EXISTS " + mContext.getString(R.string.db_table_mycheckin));

        db.execSQL("DROP TABLE IF EXISTS " + mContext.getString(R.string.db_table_mfavID));

        db.execSQL("DROP TABLE IF EXISTS " + mContext.getString(R.string.db_table_subdomain));

        db.execSQL("DROP TABLE IF EXISTS " + mContext.getString(R.string.db_table_cart));

        // Create tables again
        onCreate(db);
    }


    private void createTableFavID(SQLiteDatabase db) {
        String tblFields, tblCreateStr;

        tblFields = "( id INTEGER PRIMARY KEY)";

        //create table
        tblCreateStr = "CREATE TABLE IF NOT EXISTS " + mContext.getString(R.string.db_table_mfavID) + tblFields;
        db.execSQL(tblCreateStr);
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
                + "dob TEXT,"
                + "telgrmCountryCode TEXT,"
                + "telgrmNumber TEXT,"
                + "whtsAppCountryCode TEXT,"
                + "whtsAppNumber TEXT)";

        //create table
        tblCreateStr = "CREATE TABLE IF NOT EXISTS " + mContext.getString(R.string.db_table_userinfo) + tblFields;
        db.execSQL(tblCreateStr);
    }

    private void createTablePopularSearchKeyWord(SQLiteDatabase db) {
        String tblFields, tblCreateStr;

        tblFields = "( popularkeyword TEXT,"
                + "query TEXT,"
                + "sector TEXT,"
                + "name TEXT)";

        //create table
        tblCreateStr = "CREATE TABLE IF NOT EXISTS " + mContext.getString(R.string.db_table_popularsearch) + tblFields;
        db.execSQL(tblCreateStr);
    }

    private void createTableDomain(SQLiteDatabase db) {
        String tblFields, tblCreateStr;

        tblFields = "( domain TEXT,"
                + "displayname TEXT)";

        //create table
        tblCreateStr = "CREATE TABLE IF NOT EXISTS " + mContext.getString(R.string.db_table_domain) + tblFields;
        db.execSQL(tblCreateStr);
    }

    private void createTableSubDomain(SQLiteDatabase db) {
        String tblFields, tblCreateStr;

        tblFields = "( sector TEXT,"
                + "displayname TEXT)";

        //create table
        tblCreateStr = "CREATE TABLE IF NOT EXISTS " + mContext.getString(R.string.db_table_subdomain) + tblFields;
        db.execSQL(tblCreateStr);
    }

    private void createTableCart(SQLiteDatabase db) {
        try {

            String tblFields, tblCreateStr;

            tblFields = "( itemId INTEGER,"
                    + "accountId INTEGER,"
                    + "catalogId INTEGER,"
                    + "itemName TEXT,"
                    + "imageUrl TEXT,"
                    + "quantity INTEGER,"
                    + "itemPrice REAL,"
                    + "price REAL,"
                    + "instruction TEXT,"
                    + "discountedPrice REAL,"
                    + "discount REAL,"
                    + "promotionalType TEXT,"
                    + "isPromotional NUMERIC,"
                    + "isExpired NUMERIC,"
                    + "uniqueId INTEGER,"
                    + "tax REAL,"
                    + "isTaxable NUMERIC,"
                    + "maxQuantity INTEGER,"
                    + "itemType TEXT,"
                    + "serviceOptionQnr TEXT,"
                    + "serviceOptioniput TEXT,"
                    + "serviceOptionAtachedImages TEXT,"
                    + "serviceOptionPrice REAL)";


            //create table
            tblCreateStr = "CREATE TABLE IF NOT EXISTS " + mContext.getString(R.string.db_table_cart) + tblFields;
            db.execSQL(tblCreateStr);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public boolean insertItemToCart(CartItemModel cartItemModel) {

        try {

            SQLiteDatabase db = new DatabaseHandler(mContext).getWritableDatabase();
            db.beginTransaction();
            try {

                ContentValues values = new ContentValues();
                values.put("itemId", cartItemModel.getItemId());
                values.put("accountId", cartItemModel.getAccountId());
                values.put("catalogId", cartItemModel.getCatalogId());
                values.put("uniqueId", cartItemModel.getUniqueId());
                values.put("itemName", cartItemModel.getItemName());
                values.put("imageUrl", cartItemModel.getImageUrl());
                values.put("quantity", cartItemModel.getQuantity());
                values.put("itemPrice", cartItemModel.getItemPrice());
                values.put("price", cartItemModel.getQuantity() * cartItemModel.getDiscountedPrice());
                values.put("instruction", cartItemModel.getInstruction());
                values.put("discountedPrice", cartItemModel.getDiscountedPrice());
                values.put("discount", cartItemModel.getDiscountedPrice());
                values.put("promotionalType", cartItemModel.getPromotionalType());
                values.put("isPromotional", cartItemModel.getIsPromotional());
                values.put("isExpired", cartItemModel.isExpired());
                values.put("maxQuantity", cartItemModel.getMaxQuantity());
                values.put("tax", cartItemModel.getTax());
                values.put("isTaxable", cartItemModel.getIsTaxable());
                values.put("itemType", cartItemModel.getItemType());
                if (cartItemModel.getQuestionnaire() != null) {
                    values.put("serviceOptionQnr", cartItemModel.getQuestionnaire());
                }
                if (cartItemModel.getServiceOptioniput() != null) {
                    values.put("serviceOptioniput", cartItemModel.getServiceOptioniput());
                }
                if (cartItemModel.getServiceOptionAtachedImages() != null) {
                    values.put("serviceOptionAtachedImages", cartItemModel.getServiceOptionAtachedImages());
                }
                values.put("serviceOptionPrice", cartItemModel.getServiceOptionPrice());


                db.insert(mContext.getString(R.string.db_table_cart), null, values);

                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public void removeItemFromCart(int itemId) {
        try {
            SQLiteDatabase db = new DatabaseHandler(mContext).getWritableDatabase();
            db.execSQL("delete from " + mContext.getString(R.string.db_table_cart) + " where itemId = " + itemId);
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void addQuantity(int itemId, int quantity) {
        try {
            SQLiteDatabase db = new DatabaseHandler(mContext).getWritableDatabase();
            db.execSQL("update  " + mContext.getString(R.string.db_table_cart) + " set quantity = " + quantity + " where itemId = " + itemId);
            db.execSQL("update  " + mContext.getString(R.string.db_table_cart) + " set price = (discountedPrice * quantity) where itemId = " + itemId);
            db.execSQL("delete from " + mContext.getString(R.string.db_table_cart) + " where quantity < 1 ");
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCartItem(CartItemModel item) {
        try {
            SQLiteDatabase db = new DatabaseHandler(mContext).getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("isExpired", 0);
            values.put("itemPrice", item.getItemPrice());
            values.put("discountedPrice", item.getDiscountedPrice());
            values.put("isPromotional", item.getIsPromotional());
            values.put("maxQuantity", item.getMaxQuantity());
            values.put("price", item.getDiscountedPrice() * item.getQuantity());
            String[] args = new String[]{String.valueOf(item.getItemId())};
            db.update(mContext.getString(R.string.db_table_cart), values, "itemId = ?", args);
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void markItemsAsExpired() {
        try {
            SQLiteDatabase db = new DatabaseHandler(mContext).getWritableDatabase();
            db.execSQL("update " + mContext.getString(R.string.db_table_cart) + " set isExpired = 1 ");
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addInstructions(int itemId, String instruction) {
        try {
            SQLiteDatabase db = new DatabaseHandler(mContext).getWritableDatabase();
            db.execSQL("update  " + mContext.getString(R.string.db_table_cart) + " set instruction = '" + instruction + "' where itemId = " + itemId);
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getCartCount() {

        try {
            int cartSize = 0;
            SQLiteDatabase db = new DatabaseHandler(mContext).getReadableDatabase();
            db.beginTransaction();
            Cursor cursor = db.rawQuery("SELECT sum(quantity) as cartCount FROM " + mContext.getString(R.string.db_table_cart), null);

            if (cursor.moveToFirst()) {
                do {
                    cartSize = cursor.getInt(0);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();

            return cartSize;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }

    public int getExpiredItemsCount() {

        try {
            int expiryCount = 0;
            SQLiteDatabase db = new DatabaseHandler(mContext).getReadableDatabase();
            db.beginTransaction();
            Cursor cursor = db.rawQuery("SELECT sum(isExpired) as expiredCount FROM " + mContext.getString(R.string.db_table_cart), null);

            if (cursor.moveToFirst()) {
                do {
                    expiryCount = cursor.getInt(0);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();

            return expiryCount;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }

    public void removeExpiredItems() {
        SQLiteDatabase db = new DatabaseHandler(mContext).getWritableDatabase();
        db.execSQL("delete from " + mContext.getString(R.string.db_table_cart) + " WHERE isExpired = 1");
        db.close();
    }

    public int getItemQuantity(int itemId) {

        try {
            int quantity = 0;
            SQLiteDatabase db = new DatabaseHandler(mContext).getReadableDatabase();
            db.beginTransaction();
            Cursor cursor = db.rawQuery("SELECT sum(quantity) FROM " + mContext.getString(R.string.db_table_cart) + " WHERE itemId = " + itemId, null);

            if (cursor.moveToFirst()) {
                do {
                    quantity = cursor.getInt(0);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();

            return quantity;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }

    public ArrayList<OrderItem> getOrderItems() {

        try {

            ArrayList<OrderItem> orderItemsList = new ArrayList<>();
            SQLiteDatabase db = new DatabaseHandler(mContext).getReadableDatabase();
            db.beginTransaction();
            Cursor cursor = db.rawQuery("SELECT itemId, sum(quantity), instruction, itemType, serviceOptioniput, serviceOptionAtachedImages FROM " + mContext.getString(R.string.db_table_cart) + " GROUP BY itemId ", null);

            if (cursor.moveToFirst()) {
                do {
                    int itemId = cursor.getInt(0);
                    int quantity = cursor.getInt(1);
                    String instruction = cursor.getString(2);
                    String itemType = cursor.getString(3);
                    orderItemsList.add(new OrderItem(itemId, quantity, instruction, itemType, cursor.getString(4), cursor.getString(5)));

                } while (cursor.moveToNext());
            }
            cursor.close();
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();

            return orderItemsList;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

    }

    public int getAccountId() {

        try {
            int accountId = 0;
            SQLiteDatabase db = new DatabaseHandler(mContext).getReadableDatabase();
            db.beginTransaction();
            Cursor cursor = db.rawQuery("SELECT accountId FROM " + mContext.getString(R.string.db_table_cart) + " LIMIT 1", null);

            if (cursor.moveToFirst()) {
                do {
                    accountId = cursor.getInt(0);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();

            return accountId;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }

    public int getUniqueId() {

        try {
            int uniqueId = 0;
            SQLiteDatabase db = new DatabaseHandler(mContext).getReadableDatabase();
            db.beginTransaction();
            Cursor cursor = db.rawQuery("SELECT uniqueId FROM " + mContext.getString(R.string.db_table_cart) + " LIMIT 1", null);

            if (cursor.moveToFirst()) {
                do {
                    uniqueId = cursor.getInt(0);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();

            return uniqueId;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }

    public int getCatalogId() {

        try {
            int catalogId = 0;
            SQLiteDatabase db = new DatabaseHandler(mContext).getReadableDatabase();
            db.beginTransaction();
            Cursor cursor = db.rawQuery("SELECT catalogId FROM " + mContext.getString(R.string.db_table_cart) + " LIMIT 1", null);

            if (cursor.moveToFirst()) {
                do {
                    catalogId = cursor.getInt(0);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();

            return catalogId;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }

    public double getCartPrice() {
        try {
            double cartPrice = 0;
            SQLiteDatabase db = new DatabaseHandler(mContext).getReadableDatabase();
            db.beginTransaction();
            Cursor cursor = db.rawQuery("SELECT sum((itemPrice * quantity) + serviceOptionPrice) as cartCount FROM " + mContext.getString(R.string.db_table_cart), null);

            if (cursor.moveToFirst()) {
                do {
                    cartPrice = cursor.getDouble(0);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();

            return cartPrice;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }


    public double getCartDiscountedPrice() {

        try {
            double cartDiscountPrice = 0;
            SQLiteDatabase db = new DatabaseHandler(mContext).getReadableDatabase();
            db.beginTransaction();
            Cursor cursor = db.rawQuery("SELECT sum((discountedPrice * quantity) + serviceOptionPrice) as cartCount FROM " + mContext.getString(R.string.db_table_cart), null);

            if (cursor.moveToFirst()) {
                do {
                    cartDiscountPrice = cursor.getDouble(0);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();

            return cartDiscountPrice;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }
    public double getItemDiscountedPrice(int itemId) {

        try {
            int quantity = 0;
            SQLiteDatabase db = new DatabaseHandler(mContext).getReadableDatabase();
            db.beginTransaction();
            Cursor cursor = db.rawQuery("SELECT sum((discountedPrice * quantity) + serviceOptionPrice) FROM " + mContext.getString(R.string.db_table_cart) + " WHERE itemId = " + itemId, null);

            if (cursor.moveToFirst()) {
                do {
                    quantity = cursor.getInt(0);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();

            return quantity;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }

    public double getTaxAmount() {

        try {
            double taxAmount = 0;
            SQLiteDatabase db = new DatabaseHandler(mContext).getReadableDatabase();
            db.beginTransaction();
            Cursor cursor = db.rawQuery("SELECT sum(((discountedPrice * quantity) * tax) / 100) as cartCount FROM " + mContext.getString(R.string.db_table_cart) + " WHERE isTaxable = 1", null);

            if (cursor.moveToFirst()) {
                do {
                    taxAmount = cursor.getDouble(0);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();

            return taxAmount;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }

    public boolean isAddedServiceOption(int itemId) {

        try {
            String serviceOptionQnr, serviceOptioniput;
            int quantity;
            boolean isAddedServiceOption = false;
            SQLiteDatabase db = new DatabaseHandler(mContext).getReadableDatabase();
            db.beginTransaction();
            Cursor cursor = db.rawQuery("SELECT serviceOptionQnr, serviceOptioniput, quantity FROM " + mContext.getString(R.string.db_table_cart) + " WHERE serviceOptionQnr IS NOT NULL AND serviceOptioniput IS NOT NULL AND itemId = " + itemId, null);

            if (cursor.moveToFirst()) {
                do {
                    serviceOptionQnr = cursor.getString(0);
                    serviceOptioniput = cursor.getString(1);
                    quantity = cursor.getInt(2);
                    if (serviceOptionQnr != null && !serviceOptionQnr.trim().isEmpty() && serviceOptioniput != null && !serviceOptioniput.trim().isEmpty() && quantity > 0) {
                        isAddedServiceOption = true;
                    } else {
                        isAddedServiceOption = false;
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();

            return isAddedServiceOption;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public boolean updateServiceOptionInput(int itemId, String serviceOption, String serviceOptionAtachedImages, float serviceOtpionPrice) {
        boolean result = false;

        try {
            SQLiteDatabase db = new DatabaseHandler(mContext).getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("serviceOptioniput", serviceOption);
            values.put("serviceOptionAtachedImages", serviceOptionAtachedImages);
            values.put("serviceOptionPrice", serviceOtpionPrice);


            String[] args = new String[]{String.valueOf(itemId)};
            db.update(mContext.getString(R.string.db_table_cart), values, "itemId = ?", args);
            db.close();
//            db.execSQL("update  " + mContext.getString(R.string.db_table_cart) + " set serviceOptioniput = " + serviceOption + " where itemId = " + itemId);
//            db.execSQL("update  " + mContext.getString(R.string.db_table_cart) + " set serviceOptionAtachedImages =" + serviceOptionAtachedImages + " where itemId = " + itemId);
//            db.close();
            result = true;
        } catch (SQLException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    public String getServiceOptioniput(int itemId) {
        String serviceOptioniput = null;

        try {
            SQLiteDatabase db = new DatabaseHandler(mContext).getReadableDatabase();
            db.beginTransaction();
            Cursor cursor = db.rawQuery("SELECT serviceOptioniput FROM " + mContext.getString(R.string.db_table_cart) + " WHERE serviceOptioniput IS NOT NULL AND itemId = " + itemId, null);

            if (cursor.moveToFirst()) {
                do {
                    serviceOptioniput = cursor.getString(0);

                } while (cursor.moveToNext());
            }
            cursor.close();
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return serviceOptioniput;
    }

    public String getServiceOptioniputImages(int itemId) {
        String serviceOptionAtachedImages = null;

        try {
            SQLiteDatabase db = new DatabaseHandler(mContext).getReadableDatabase();
            db.beginTransaction();
            Cursor cursor = db.rawQuery("SELECT serviceOptionAtachedImages FROM " + mContext.getString(R.string.db_table_cart) + " WHERE serviceOptionAtachedImages IS NOT NULL AND itemId = " + itemId, null);

            if (cursor.moveToFirst()) {
                do {
                    serviceOptionAtachedImages = cursor.getString(0);

                } while (cursor.moveToNext());
            }
            cursor.close();
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return serviceOptionAtachedImages;
    }

    public String getServiceOptionQnr(int itemId) {
        String serviceOptionQnr = null;

        try {
            SQLiteDatabase db = new DatabaseHandler(mContext).getReadableDatabase();
            db.beginTransaction();
            Cursor cursor = db.rawQuery("SELECT serviceOptionQnr FROM " + mContext.getString(R.string.db_table_cart) + " WHERE serviceOptioniput IS NOT NULL AND itemId = " + itemId, null);

            if (cursor.moveToFirst()) {
                do {
                    serviceOptionQnr = cursor.getString(0);

                } while (cursor.moveToNext());
            }
            cursor.close();
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return serviceOptionQnr;
    }

    public ArrayList<CartItemModel> getCartItems() {

        try {
            ArrayList<CartItemModel> cartItemsList = new ArrayList<CartItemModel>();
            SQLiteDatabase db = new DatabaseHandler(mContext).getReadableDatabase();

            String table = mContext.getString(R.string.db_table_cart);
            String[] columns = {"itemId", "accountId", "catalogId", "itemName", "imageUrl", "quantity", "itemPrice", "price", "instruction", "discountedPrice", "discount", "promotionalType", "maxQuantity", "isPromotional", "isExpired", "uniqueId", "tax", "isTaxable", "itemType"};
            String selection = " quantity >?";
            String[] selectionArgs = new String[]{"0"};
            db.beginTransaction();

            Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    CartItemModel cartItem = new CartItemModel();
                    cartItem.setItemId(cursor.getInt(0));
                    cartItem.setAccountId(cursor.getInt(1));
                    cartItem.setCatalogId(cursor.getInt(2));
                    cartItem.setItemName(cursor.getString(3));
                    cartItem.setImageUrl(cursor.getString(4));
                    cartItem.setQuantity(cursor.getInt(5));
                    cartItem.setItemPrice(cursor.getDouble(6));
                    cartItem.setPrice(cursor.getDouble(7));
                    cartItem.setInstruction(cursor.getString(8));
                    cartItem.setDiscountedPrice(cursor.getDouble(9));
                    cartItem.setDiscount(cursor.getDouble(10));
                    cartItem.setPromotionalType(cursor.getString(11));
                    cartItem.setMaxQuantity(cursor.getInt(12));
                    cartItem.setIsPromotional(cursor.getInt(13));
                    cartItem.setExpired(cursor.getInt(14));
                    cartItem.setUniqueId(cursor.getInt(15));
                    cartItem.setTax(cursor.getDouble(16));
                    cartItem.setIsTaxable(cursor.getInt(17));
                    cartItem.setItemType(cursor.getString(18));
                    cartItemsList.add(cartItem);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();

            return cartItemsList;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<CartItemModel>();
        }

    }   // to get list fo items in cart

    private void createTableFavour(SQLiteDatabase db) {
        String tblFields, tblCreateStr;

        tblFields = "( id INTEGER PRIMARY KEY,"
                + "businessname TEXT,"
                + "locid TEXT,"
                + "uniqueid INTEGER,"
                + "isRevelPhoneno boolean,"
                + "place TEXT,"
                + "onlinePresence TEXT,"
                + "donationServiceStatus TEXT,"
                + "googleMapUrl TEXT,"
                + "countryCode TEXT)";


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
            values.put("place", favorite.getPlace());
            values.put("onlinePresence", favorite.getOnlinePresence());
            values.put("donationServiceStatus", favorite.getDonationServiceStatus());
            values.put("googleMapUrl", favorite.getGoogleMapUrl());
            values.put("countryCode", favorite.getCountryCode());

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

    public void insertFavIDInfo(ArrayList<FavouriteModel> favorite) {

        SQLiteDatabase db = new DatabaseHandler(mContext).getWritableDatabase();
        db.beginTransaction();
        try {
            for (FavouriteModel favorite1 : favorite) {

                ContentValues values = new ContentValues();
                values.put("id", favorite1.getId());


                db.insert(mContext.getString(R.string.db_table_mfavID), null, values);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }


    }

    public void insertDomainInfo(List<Domain_Spinner> domainSpinner) {

        SQLiteDatabase db = new DatabaseHandler(mContext).getWritableDatabase();
        db.beginTransaction();
        try {
            for (Domain_Spinner domainSpinnerVal : domainSpinner) {
                ContentValues values = new ContentValues();
                values.put("domain", domainSpinnerVal.getDomain());
                values.put("displayname", domainSpinnerVal.getDisplayName());


                db.insert(mContext.getString(R.string.db_table_domain), null, values);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }


    }

    public void insertSubDomainList(List<SearchModel> subDomainList) {

        SQLiteDatabase db = new DatabaseHandler(mContext).getWritableDatabase();
        db.beginTransaction();
        try {
            for (SearchModel subDomain : subDomainList) {
                ContentValues values = new ContentValues();
                values.put("sector", subDomain.getSector());
                values.put("displayname", subDomain.getDisplayname());


                db.insert(mContext.getString(R.string.db_table_subdomain), null, values);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }


    }

    public void insertPopularSearchInfo(List<SearchModel> popularSearchModel) {

        SQLiteDatabase db = new DatabaseHandler(mContext).getWritableDatabase();
        db.beginTransaction();
        try {
            for (SearchModel searchModel : popularSearchModel) {
                ContentValues values = new ContentValues();
                values.put("popularkeyword", searchModel.getDisplayname());
                values.put("query", searchModel.getQuery());
                values.put("sector", searchModel.getSector());
                values.put("name", searchModel.getName());


                db.insert(mContext.getString(R.string.db_table_popularsearch), null, values);
            }

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
        String[] columns = {"id", "businessname", "locid", "uniqueid", "isRevelPhoneno", "place", "onlinePresence", "donationServiceStatus", "googleMapUrl", "countryCode"};


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
                boolean phone = cursor.getInt(4) > 0;
                fav.setRevealPhoneNumber(phone);
                fav.setPlace(cursor.getString(5));
                fav.setOnlinePresence(cursor.getString(6));
                fav.setDonationServiceStatus(cursor.getString(7));
                fav.setGoogleMapUrl(cursor.getString(8));
                fav.setCountryCode(cursor.getString(9));

                favData.add(fav);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

        return favData;


    }

    public ArrayList<FavouriteModel> getFavouriteID() {
        SQLiteDatabase db = new DatabaseHandler(mContext).getReadableDatabase();
        ArrayList<FavouriteModel> favData = new ArrayList<FavouriteModel>();
        String table = mContext.getString(R.string.db_table_mfavID);
        /* String[] columns = {"provider", "service", "id", "timestamp", "uniqueID","receiverID","message","receiverName", "messageStatus","waitlistId"};*/
        String[] columns = {"id"};


        //String timestamp="timestamp";

        db.beginTransaction();

        Cursor cursor = db.query(table, columns, null, null, null, null, null);
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            do {
                FavouriteModel fav = new FavouriteModel();

                fav.setId(cursor.getInt(0));

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
                + "queueEndTime TEXT,"
                + "firstName TEXT,"
                + "lastName TEXT,"
                + "ynwUuid TEXT,"
                + "paymentStatus TEXT,"
                + "billViewStatus TEXT,"
                + "billStatus TEXT,"
                + "amountPaid DOUBLE,"
                + "amountDue DOUBLE,"
                + "personsAhead INTEGER,"
                + "serviceTime TEXT,"
                + "statusUpdatedTime TEXT,"
                + "distance TEXT,"
                + "jaldeeStartTimeType TEXT,"
                + "rating TEXT,"
                + "checkInTime TEXT,"
                + "token INTEGER,"
                + "batchName TEXT,"
                + "parentUuid TEXT,"
                + "lattitude TEXT,"
                + "longitude TEXT,"
                + "primaryMobileNo TEXT,"
                + "calculationMode TEXT,"
                + "livetrack TEXT,"
                + "showToken TEXT,"
                + "consumer TEXT,"
                + "service TEXT,"
                + "virtualService TEXT,"
                + "provider TEXT,"
                + "checkinEncId TEXT,"
                + "location TEXT,"
                + "email TEXT,"
                + "countryCode TEXT)";

        //create table
        tblCreateStr = "CREATE TABLE IF NOT EXISTS " + mContext.getString(R.string.db_table_checkin) + tblFields;
        db.execSQL(tblCreateStr);
    }

    private void createTableMyCheckin(SQLiteDatabase db) {
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
                + "personsAhead INTEGER,"
                + "serviceTime TEXT,"
                + "statusUpdatedTime TEXT,"
                + "distance TEXT,"
                + "jaldeeStartTimeType TEXT,"
                + "rating TEXT,"
                + "queueEndTime TEXT,"
                + "checkInTime TEXT,"
                + "token INTEGER,"
                + "batchName TEXT,"
                + "parentUuid TEXT,"
                + "lattitude TEXT,"
                + "longitude TEXT,"
                + "primaryMobileNo TEXT,"
                + "calculationMode TEXT,"
                + "livetrack TEXT,"
                + "showToken TEXT,"
                + "consumer TEXT,"
                + "service TEXT,"
                + "virtualService TEXT,"
                + "provider TEXT,"
                + "checkinEncId TEXT,"
                + "location TEXT,"
                + "email TEXT,"
                + "countryCode TEXT)";

        //create table
        tblCreateStr = "CREATE TABLE IF NOT EXISTS " + mContext.getString(R.string.db_table_mycheckin) + tblFields;
        db.execSQL(tblCreateStr);
    }

    public void insertCheckinInfo(List<ActiveCheckIn> activeCheckInModel) {
        Config.logV("InsertCheckinDetails" + activeCheckInModel.size());
        SQLiteDatabase db = new DatabaseHandler(mContext).getWritableDatabase();
        db.beginTransaction();
        try {
            for (ActiveCheckIn activeCheckIn : activeCheckInModel) {
                Config.logV("InsertCheckinDetails" + activeCheckIn.getProviderAccount().getId());
                ContentValues values = new ContentValues();
                values.put("id", activeCheckIn.getProviderAccount().getId());
                values.put("businessName", activeCheckIn.getProviderAccount().getBusinessName());
                values.put("uniqueId", activeCheckIn.getProviderAccount().getUniqueId());
                values.put("date", activeCheckIn.getDate());
                values.put("waitlistStatus", activeCheckIn.getWaitlistStatus());
                values.put("servicename", activeCheckIn.getService().getName());
                values.put("partySize", activeCheckIn.getPartySize());
                values.put("appxWaitingTime", activeCheckIn.getAppxWaitingTime());
                values.put("place", activeCheckIn.getQueue().getLocation().getPlace());
                values.put("googleMapUrl", activeCheckIn.getQueue().getLocation().getGoogleMapUrl());
                values.put("queueStartTime", activeCheckIn.getQueue().getQueueStartTime());
                values.put("queueEndTime", activeCheckIn.getQueue().getQueueEndTime());
                values.put("firstName", activeCheckIn.getWaitlistingFor().get(0).getFirstName());
                values.put("lastName", activeCheckIn.getWaitlistingFor().get(0).getLastName());
                values.put("ynwUuid", activeCheckIn.getYnwUuid());
                values.put("paymentStatus", activeCheckIn.getPaymentStatus());
                values.put("billViewStatus", activeCheckIn.getBillViewStatus());
                values.put("billStatus", activeCheckIn.getBillStatus());
                values.put("amountPaid", activeCheckIn.getAmountPaid());
                values.put("amountDue", activeCheckIn.getAmountDue());
                values.put("personsAhead", activeCheckIn.getPersonsAhead());
                values.put("serviceTime", activeCheckIn.getServiceTime());
                values.put("statusUpdatedTime", activeCheckIn.getStatusUpdatedTime());

                values.put("checkInTime", activeCheckIn.getCheckInTime());
                values.put("token", activeCheckIn.getToken());
                values.put("batchName", activeCheckIn.getBatchName());
                values.put("parentUUid", activeCheckIn.getParentUuid());
                values.put("lattitude", activeCheckIn.getQueue().getLocation().getLattitude());
                values.put("longitude", activeCheckIn.getQueue().getLocation().getLongitude());
                values.put("primaryMobileNo", activeCheckIn.getWaitlistingFor().get(0).getPhoneNo());
                values.put("calculationMode", activeCheckIn.getCalculationMode());
                values.put("livetrack", activeCheckIn.getService().getLivetrack());
                values.put("showToken", activeCheckIn.getShowToken());
                values.put("consumer", (new Gson().toJson(activeCheckIn.getConsumer())));
                values.put("service", (new Gson().toJson(activeCheckIn.getService())));
                values.put("virtualService", (new Gson().toJson(activeCheckIn.getVirtualService())));
                values.put("provider", (new Gson().toJson((activeCheckIn.getProvider()))));
                values.put("checkinEncId", activeCheckIn.getCheckinEncId());
                values.put("location", (new Gson().toJson(activeCheckIn.getQueue().getLocation())));
                values.put("email", activeCheckIn.getWaitlistingFor().get(0).getEmail());
                values.put("countryCode", activeCheckIn.getCountryCode());

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


        String[] columns = {"id", "businessName", "uniqueId", "date", "waitlistStatus", "servicename", "partySize", "appxWaitingTime", "place", "googleMapUrl", "queueStartTime", "firstName", "lastName", "ynwUuid", "paymentStatus", "billViewStatus", "billStatus", "amountPaid", "amountDue", "personsAhead", "serviceTime", "queueEndTime", "statusUpdatedTime", "distance", "jaldeeStartTimeType", "rating", "checkInTime", "token", "batchName", "parentUuid", "lattitude", "longitude", "primaryMobileNo", "calculationMode", "livetrack", "showToken", "consumer", "service", "virtualService", "provider", "checkinEncId", "location", "email", "countryCode"};

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

                activeModel.setPersonsAhead(cursor.getInt(19));

                activeModel.setServiceTime(cursor.getString(20));
                activeModel.setQueueEndTime(cursor.getString(21));
                activeModel.setStatusUpdatedTime(cursor.getString(22));
                activeModel.setJaldeeWaitlistDistanceTime(new Gson().fromJson(cursor.getString(23), JaldeeWaitlistDistanceTime.class));
                activeModel.setJaldeeStartTimeType(cursor.getString(24));
                activeModel.setRating(new Gson().fromJson(cursor.getString(25), RatingResponse.class));
                activeModel.setCheckInTime(cursor.getString(26));
                activeModel.setToken(cursor.getInt(27));
                activeModel.setBatchName(cursor.getString(28));
                activeModel.setParentUuid(cursor.getString(29));
                activeModel.setLattitude(cursor.getString(30));
                activeModel.setLongitude(cursor.getString(31));
                activeModel.setPhoneNo(cursor.getString(32));
                activeModel.setCalculationMode(cursor.getString(33));
                activeModel.setLivetrack((cursor.getString(34)));
                activeModel.setShowToken((cursor.getString(35)));
                activeModel.setConsumer(new Gson().fromJson(cursor.getString(36), ConsumerDetails.class));
                activeModel.setService(new Gson().fromJson(cursor.getString(37), ServiceDetails.class));
                activeModel.setVirtualService(new Gson().fromJson(cursor.getString(38), VirtualServiceDetails.class));
                activeModel.setProvider(new Gson().fromJson(cursor.getString(39), ProviderDetails.class));
                activeModel.setCheckinEncId((cursor.getString(40)));
                activeModel.setLocation(new Gson().fromJson(cursor.getString(41), LocationDetails.class));
                activeModel.setEmail((cursor.getString(42)));
                activeModel.setCountryCode((cursor.getString(43)));


                checkin.add(activeModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

        return checkin;
    }

    public void insertMyCheckinInfo(List<ActiveCheckIn> activeCheckInModel) {

        SQLiteDatabase db = new DatabaseHandler(mContext).getWritableDatabase();
        db.beginTransaction();
        try {
            for (ActiveCheckIn activeCheckIn : activeCheckInModel) {
                ContentValues values = new ContentValues();
                values.put("id", activeCheckIn.getProviderAccount().getId());
                values.put("businessName", activeCheckIn.getProviderAccount().getBusinessName());
                values.put("uniqueId", activeCheckIn.getProviderAccount().getUniqueId());
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
                values.put("personsAhead", activeCheckIn.getPersonsAhead());
                values.put("serviceTime", activeCheckIn.getServiceTime());
                values.put("statusUpdatedTime", activeCheckIn.getStatusUpdatedTime());
                values.put("distance", new Gson().toJson(activeCheckIn.getJaldeeWaitlistDistanceTime()));
                values.put("jaldeeStartTimeType", activeCheckIn.getJaldeeStartTimeType());
                values.put("rating", new Gson().toJson(activeCheckIn.getRating()));
                values.put("queueEndTime", activeCheckIn.getQueue().getQueueEndTime());
                values.put("checkInTime", activeCheckIn.getCheckInTime());
                values.put("token", activeCheckIn.getToken());
                values.put("batchName", activeCheckIn.getBatchName());
                values.put("parentUuid", activeCheckIn.getParentUuid());
                values.put("lattitude", activeCheckIn.getQueue().getLocation().getLattitude());
                values.put("longitude", activeCheckIn.getQueue().getLocation().getLongitude());
                values.put("primaryMobileNo", activeCheckIn.getWaitlistingFor().get(0).getPhoneNo());
                values.put("calculationMode", activeCheckIn.getCalculationMode());
                values.put("livetrack", activeCheckIn.getService().getLivetrack());
                values.put("showToken", activeCheckIn.getShowToken());
                values.put("consumer", new Gson().toJson(activeCheckIn.getConsumer()));
                values.put("service", new Gson().toJson(activeCheckIn.getService()));
                values.put("virtualService", new Gson().toJson(activeCheckIn.getVirtualService()));
                values.put("provider", new Gson().toJson(activeCheckIn.getProvider()));
                values.put("checkinEncId", activeCheckIn.getCheckinEncId());
                values.put("location", new Gson().toJson(activeCheckIn.getQueue().getLocation()));
                values.put("email", activeCheckIn.getWaitlistingFor().get(0).getEmail());
                values.put("countryCode", activeCheckIn.getCountryCode());
                db.insert(mContext.getString(R.string.db_table_mycheckin), null, values);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }


    }

    public ArrayList<ActiveCheckIn> getMyCheckinList(String status) {
        ArrayList<ActiveCheckIn> checkin = new ArrayList<ActiveCheckIn>();

        SQLiteDatabase db = new DatabaseHandler(mContext).getReadableDatabase();

        String table = mContext.getString(R.string.db_table_mycheckin);
        // String[] columns = {"provider", "service", "id", "timestamp", "uniqueID","receiverID","message", "receiverName", "messageStatus","waitlistId"};
        String[] columns = {"id", "businessName", "uniqueId", "date", "waitlistStatus", "servicename", "partySize", "appxWaitingTime", "place", "googleMapUrl", "queueStartTime", "firstName", "lastName", "ynwUuid", "paymentStatus", "billViewStatus", "billStatus", "amountPaid", "amountDue", "personsAhead", "serviceTime", "statusUpdatedTime", "distance", "jaldeeStartTimeType", "rating", "queueEndTime", "checkInTime", "token", "batchName", "parentUuid", "lattitude", "longitude", "primaryMobileNo", "calculationMode", "livetrack", "showToken", "consumer", "service", "virtualService", "provider", "checkinEncId", "location", "email", "countryCode"};
        String selection = "";
        String[] selectionArgs = null;
        selectionArgs = new String[]{Config.getTodaysDateString()};
        if (status.equalsIgnoreCase("today")) {
            selection = "  date(date) =?";
        } else if (status.equalsIgnoreCase("old")) {
            selection = "  date(date) <?";
        } else {
            selection = "  date(date) >?";
        }


        db.beginTransaction();

        Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, null);
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
                activeModel.setPersonsAhead(cursor.getInt(19));
                activeModel.setServiceTime(cursor.getString(20));
                activeModel.setStatusUpdatedTime(cursor.getString(21));
                activeModel.setJaldeeWaitlistDistanceTime(new Gson().fromJson(cursor.getString(22), JaldeeWaitlistDistanceTime.class));
                activeModel.setJaldeeStartTimeType(cursor.getString(23));
                activeModel.setRating(new Gson().fromJson(cursor.getString(24), RatingResponse.class));
                activeModel.setQueueEndTime(cursor.getString(25));
                activeModel.setCheckInTime(cursor.getString(26));
                activeModel.setToken(cursor.getInt(27));
                activeModel.setBatchName(cursor.getString(28));
                activeModel.setParentUuid(cursor.getString(29));
                activeModel.setLattitude(cursor.getString(30));
                activeModel.setLongitude(cursor.getString(31));
                activeModel.setPhoneNo(cursor.getString(32));
                activeModel.setCalculationMode(cursor.getString(33));
                activeModel.setLivetrack((cursor.getString(34)));
                activeModel.setShowToken((cursor.getString(35)));
                activeModel.setConsumer(new Gson().fromJson(cursor.getString(36), ConsumerDetails.class));
                activeModel.setService(new Gson().fromJson(cursor.getString(37), ServiceDetails.class));
                activeModel.setVirtualService(new Gson().fromJson(cursor.getString(38), VirtualServiceDetails.class));
                activeModel.setProvider(new Gson().fromJson(cursor.getString(39), ProviderDetails.class));
                activeModel.setCheckinEncId((cursor.getString(40)));
                activeModel.setLocation(new Gson().fromJson(cursor.getString(41), LocationDetails.class));
                activeModel.setEmail((cursor.getString(42)));
                activeModel.setCountryCode((cursor.getString(43)));

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
            if (profileModel.getTelegramNum() != null) {
                if (profileModel.getTelegramNum().get("countryCode") != null && profileModel.getTelegramNum().get("number") != null) {
                    values.put("telgrmCountryCode", profileModel.getTelegramNum().get("countryCode").getAsString());
                    values.put("telgrmNumber", profileModel.getTelegramNum().get("number").getAsString());
                }
            }
            if (profileModel.getWhatsAppNum() != null) {
                if (profileModel.getWhatsAppNum().get("countryCode") != null && profileModel.getWhatsAppNum().get("number") != null) {
                    values.put("whtsAppCountryCode", profileModel.getWhatsAppNum().get("countryCode").getAsString());
                    values.put("whtsAppNumber", profileModel.getWhatsAppNum().get("number").getAsString());
                }
            }
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
            if (profileModel.getTelegramNum() != null) {
                if (profileModel.getTelegramNum().get("countryCode") != null && profileModel.getTelegramNum().get("number") != null) {
                    values.put("telgrmCountryCode", profileModel.getTelegramNum().get("countryCode").getAsString());
                    values.put("telgrmNumber", profileModel.getTelegramNum().get("number").getAsString());
                }
            }
            if (profileModel.getWhatsAppNum() != null) {
                if (profileModel.getWhatsAppNum().get("countryCode") != null && profileModel.getWhatsAppNum().get("number") != null) {
                    values.put("whtsAppCountryCode", profileModel.getTelegramNum().get("countryCode").getAsString());
                    values.put("whtsAppNumber", profileModel.getTelegramNum().get("number").getAsString());
                }
            }


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
        String[] columns = {"id", "firstname", "lastname", "email", "primaryMobNo", "emailVerified", "phoneVerified", "gender", "dob", "telgrmCountryCode", "telgrmNumber", "whtsAppCountryCode", "whtsAppNumber"};

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
        profile.setTelgrmCountryCode(cursor.getString(9));
        profile.setTelgrmNumber(cursor.getString(10));
        profile.setWhtsAppCountryCode(cursor.getString(11));
        profile.setWhtsAppNumber(cursor.getString(12));

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return profile;
    }

    public ArrayList<SearchModel> getPopularSearch(String sectorName) {
        ArrayList<SearchModel> searchModelList = new ArrayList<SearchModel>();
        SQLiteDatabase db = new DatabaseHandler(mContext).getReadableDatabase();

        String table = mContext.getString(R.string.db_table_popularsearch);
        String[] columns = {"popularkeyword", "query", "sector", "name"};
        String selection = " sector =?";
        String[] selectionArgs = new String[]{sectorName};
        db.beginTransaction();

        Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                SearchModel searchModel = new SearchModel();
                searchModel.setDisplayname(cursor.getString(0));
                searchModel.setQuery(cursor.getString(1));
                searchModel.setSector(cursor.getString(2));
                searchModel.setName(cursor.getString(3));


                searchModelList.add(searchModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return searchModelList;
    }

    public ArrayList<Domain_Spinner> getDomain() {
        ArrayList<Domain_Spinner> domainSpinnerList = new ArrayList<Domain_Spinner>();
        SQLiteDatabase db = new DatabaseHandler(mContext).getReadableDatabase();

        String table = mContext.getString(R.string.db_table_domain);
        String[] columns = {"domain", "displayname"};

        db.beginTransaction();

        Cursor cursor = db.query(table, columns, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Domain_Spinner domain = new Domain_Spinner();
                domain.setDomain(cursor.getString(0));
                domain.setDisplayName(cursor.getString(1));


                domainSpinnerList.add(domain);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

        return domainSpinnerList;


    }

    public ArrayList<SearchModel> getSubDomains() {
        ArrayList<SearchModel> subDomainsList = new ArrayList<SearchModel>();
        SQLiteDatabase db = new DatabaseHandler(mContext).getReadableDatabase();

        String table = mContext.getString(R.string.db_table_subdomain);
        String[] columns = {"sector", "displayname"};

        db.beginTransaction();

        Cursor cursor = db.query(table, columns, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                SearchModel subDomain = new SearchModel();
                subDomain.setSector(cursor.getString(0));
                subDomain.setDisplayname(cursor.getString(1));


                subDomainsList.add(subDomain);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

        return subDomainsList;


    }

    public Domain_Spinner getDomainByPosition(int position) {

        try {

            ArrayList<Domain_Spinner> domainSpinnerList = new ArrayList<Domain_Spinner>();
            SQLiteDatabase db = new DatabaseHandler(mContext).getReadableDatabase();

            String table = mContext.getString(R.string.db_table_domain);
            String[] columns = {"domain", "displayname"};

            db.beginTransaction();

            Cursor cursor = db.query(table, columns, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    Domain_Spinner domain = new Domain_Spinner();
                    domain.setDomain(cursor.getString(0));
                    domain.setDisplayName(cursor.getString(1));


                    domainSpinnerList.add(domain);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();

            return domainSpinnerList.get(position);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public SearchModel getSubDomainsByFilter(String domainName, int position) {
        try {
            position = position - 1;
            ArrayList<SearchModel> subDomainsList = new ArrayList<SearchModel>();
            SQLiteDatabase db = new DatabaseHandler(mContext).getReadableDatabase();

            String table = mContext.getString(R.string.db_table_subdomain);
            String[] columns = {"sector", "displayname"};

            db.beginTransaction();

            Cursor cursor = db.query(table, columns, "lower(sector)=?", new String[]{domainName.toLowerCase()}, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    SearchModel subDomain = new SearchModel();
                    subDomain.setSector(cursor.getString(0));
                    subDomain.setDisplayname(cursor.getString(1));


                    subDomainsList.add(subDomain);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();

            return subDomainsList.get(position);
        } catch (Exception e) {
            return null;

        }

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
                + "unread INT,"
                + "messageStatus TEXT,"
                + "waitlistId TEXT,"
                + "attachements TEXT)";

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
                values.put("provider", inbox.getAccountName());
                values.put("service", inbox.getService());
                values.put("id", inbox.getOwner().getId());
                values.put("timestamp", inbox.getTimeStamp());
                values.put("message", inbox.getMsg());
                values.put("uniqueID", inbox.getUniqueID());
                values.put("waitlistId", inbox.getWaitlistId());



              /*  values.put("receiverID", inbox.getReceiver().getReceiverId());
                values.put("receiverName", inbox.getReceiver().getReceiverName());*/

                values.put("messageStatus", inbox.getMessageStatus());
                values.put("attachements", new Gson().toJson(inbox.getAttachments()));

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
            values.put("provider", inbox.getAccountName());
            values.put("service", inbox.getService());
            values.put("id", inbox.getId());
            values.put("timestamp", inbox.getTimeStamp());
            values.put("message", inbox.getMsg());
            values.put("uniqueID", inbox.getUniqueID());
            values.put("waitlistId", inbox.getWaitlistId());
            values.put("unread", inbox.isRead() ? 0 : 1);


               /* values.put("receiverID", inbox.getReceiver().getReceiverId());
                values.put("receiverName", inbox.getReceiver().getReceiverName());*/

            values.put("messageStatus", inbox.getMessageStatus());
            values.put("attachements", new Gson().toJson(inbox.getAttachments()));
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


    public void DeleteMyCheckin(String status) {
        SQLiteDatabase db = new DatabaseHandler(mContext).getWritableDatabase();
        String selection = "";
        String[] selectionArgs = null;
        selectionArgs = new String[]{Config.getTodaysDateString()};
        if (status.equalsIgnoreCase("today")) {
            selection = "  date(date) =?";
        } else if (status.equalsIgnoreCase("old")) {
            selection = "  date(date) <?";
        } else if (status.equalsIgnoreCase("future")) {
            selection = "  date(date) >?";
        }
        db.delete(mContext.getString(R.string.db_table_mycheckin), selection, selectionArgs);
        db.close();
    }

    public void DeletePopularSearch() {
        SQLiteDatabase db = new DatabaseHandler(mContext).getWritableDatabase();
        db.execSQL("delete from " + mContext.getString(R.string.db_table_popularsearch));
        db.close();
    }

    public void DeleteFav() {
        SQLiteDatabase db = new DatabaseHandler(mContext).getWritableDatabase();
        db.execSQL("delete from " + mContext.getString(R.string.db_table_fav));
        db.close();
    }

    public void DeleteCart() {
        SQLiteDatabase db = new DatabaseHandler(mContext).getWritableDatabase();
        db.execSQL("delete from " + mContext.getString(R.string.db_table_cart));
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

    public void DeleteDomain() {
        SQLiteDatabase db = new DatabaseHandler(mContext).getWritableDatabase();
        db.execSQL("delete from " + mContext.getString(R.string.db_table_domain));
        db.close();
    }

    public void DeleteSubDomain() {
        SQLiteDatabase db = new DatabaseHandler(mContext).getWritableDatabase();
        db.execSQL("delete from " + mContext.getString(R.string.db_table_subdomain));
        db.close();
    }

    public void DeleteFAVID() {
        SQLiteDatabase db = new DatabaseHandler(mContext).getWritableDatabase();
        db.execSQL("delete from " + mContext.getString(R.string.db_table_mfavID));
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
                values.put("provider", inbox.getAccountName());
                values.put("service", inbox.getService());
                values.put("id", inbox.getOwner().getId());
                values.put("timestamp", inbox.getTimeStamp());
                values.put("message", inbox.getMsg());
                values.put("uniqueID", inbox.getUniqueID());
                values.put("waitlistId", inbox.getWaitlistId());
               /* values.put("receiverID", inbox.getReceiver().getReceiverId());
                values.put("receiverName", inbox.getReceiver().getReceiverName());*/
                values.put("messageStatus", inbox.getMessageStatus());
                values.put("attachements", new Gson().toJson(inbox.getAttachments()));


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
        String[] columns = {"provider", "service", "id", "timestamp", "uniqueID", "message", "messageStatus", "waitlistId", "attachements"};
        String selection = " uniqueID =?";
        String[] selectionArgs = new String[]{uniqueID};

        //String timestamp="timestamp";

        db.beginTransaction();

        Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, null);
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            do {
                InboxModel inbox = new InboxModel();
                inbox.setAccountName(cursor.getString(0));
                inbox.setService(cursor.getString(1));
                inbox.setId(cursor.getInt(2));
                inbox.setTimeStamp(cursor.getLong(3));
                inbox.setUniqueID(cursor.getString(4));
                // inbox.setReceiverId(cursor.getInt(5));
                inbox.setMsg(cursor.getString(5));
                //   inbox.setRecevierName(cursor.getString(7));
                inbox.setMessageStatus(cursor.getString(6));
                inbox.setWaitlistId(cursor.getString(7));
                Gson gson = new Gson();
                Type type = new TypeToken<List<FileAttachment>>() {
                }.getType();
                List<FileAttachment> attachments = gson.fromJson(cursor.getString(8), type);
//
//                ChannelSearchEnum[] enums = gson.fromJson(yourJson, ChannelSearchEnum[].class);
//
                inbox.setAttachments(attachments);

//                Type collectionType = new TypeToken<Collection<ChannelSearchEnum>>(){}.getType();
//                Collection<ChannelSearchEnum> enums = gson.fromJson(yourJson, collectionType);

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
        ArrayList<InboxModel> countsList = new ArrayList<InboxModel>();

        SQLiteDatabase db = new DatabaseHandler(mContext).getReadableDatabase();

        String table = mContext.getString(R.string.db_table_inbox);
        // String[] columns = {"provider", "service", "id", "timestamp", "uniqueID","receiverID","message", "receiverName", "messageStatus","waitlistId"};


        String[] columns = {"provider", "service", "id", "timestamp", "uniqueID", "message", "messageStatus", "waitlistId", "attachements"};
        String groupBy = "uniqueID";
        db.beginTransaction();

        Cursor cursor = db.query(table, columns, null, null, groupBy, null, null);

        String query = "SELECT uniqueID, sum(unread) as unreadCount FROM " + mContext.getString(R.string.db_table_inbox) + " GROUP BY uniqueID";
        Cursor cursor1 = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                InboxModel inboxModel = new InboxModel();
                inboxModel.setAccountName(cursor.getString(0));
                inboxModel.setService(cursor.getString(1));
                inboxModel.setId(cursor.getInt(2));
                inboxModel.setTimeStamp(cursor.getLong(3));
                inboxModel.setUniqueID(cursor.getString(4));

                //  inboxModel.setReceiverId(cursor.getInt(5));
                inboxModel.setMsg(cursor.getString(5));

                // inboxModel.setRecevierName(cursor.getString(7));
                inboxModel.setMessageStatus(cursor.getString(6));
                inboxModel.setWaitlistId(cursor.getString(7));
                inboxModel.setUnReadCount(0);
                Gson gson = new Gson();
                Type type = new TypeToken<List<FileAttachment>>() {
                }.getType();
                List<FileAttachment> attachments = gson.fromJson(cursor.getString(8), type);
                inboxModel.setAttachments(attachments);
                inbox.add(inboxModel);
            } while (cursor.moveToNext());
        }
        cursor.close();

        if (cursor1.moveToFirst()) {
            do {
                InboxModel inboxModel = new InboxModel();

                inboxModel.setUniqueID(cursor1.getString(0));
                inboxModel.setUnReadCount(cursor1.getInt(1));

                countsList.add(inboxModel);
            } while (cursor1.moveToNext());
        }
        cursor1.close();

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

        for (InboxModel im : inbox) {

            for (InboxModel i : countsList) {

                if (im.getUniqueID().equalsIgnoreCase(i.getUniqueID())) {

                    im.setUnReadCount(i.getUnReadCount());
                }
            }
        }

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