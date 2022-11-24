package com.jaldeeinc.jaldee.activities;

import android.os.Build;

import java.util.Arrays;
import java.util.List;

import okhttp3.MediaType;

public class Constants {


    // Must change based on release type
    //public static final String URL = "https://www.jaldee.com/";
    public static final String URL = "https://scale.jaldee.com/";
    //public static final String URL = "https://test.jaldee.com/";
    //public static final String URL = "http://103.70.197.233/";


    // Testing Payment Gateway
    public static final String MONEY_HASH = "https://debajyotibasak.000webhostapp.com/PayUMoneyHash.php";
    public static final String SURL = "https://www.payumoney.com/mobileapp/payumoney/success.php";
    public static final String FURL = "https://www.payumoney.com/mobileapp/payumoney/failure.php";
    public static final String MERCHANT_KEY = "rjQUPktU";
    public static final String MERCHANT_ID = "4934580";
    public static final boolean DEBUG = true;

    public static final List<String> razorpayMethods = Arrays.asList("dc", "cc", "netbanking", "NB", "wallet", "emi", "upi");
    //public static final List<String> paytmMethods = Arrays.asList( "BALANCE", "PPBL", "UPI", "CREDIT_CARD" "DEBIT_CARD" NET_BANKINGFor Net Banking EMI PAYTM_DIGITAL_CREDIT);

    public static final String PREFS = "com.jaldeeinc.jaldee";


    public static final String FIRST_NAME = "Debajyoti";
    public static final String MOBILE = "7204342561";
    public static final String EMAIL = "d.basak.db@gmail.com";
    public static final String PURPOSE_PREPAYMENT = "prePayment";
    public static final String PURPOSE_BILLPAYMENT = "billPayment";
    public static final String PURPOSE_DONATIONPAYMENT = "donation";
    public static final String SOURCE_PAYMENT = "Android";

    public static final String[] fileExtFormats = new String[]{"mpeg", "jpg", "jpeg", "png", "pdf", "mp3", "wmv", "mp4", "webm", "flw", "mov", "avi", "doc",
            "msword", "xls", "ms-excel", "vnd.ms-excel", "odt", "ods", "odp", "odg", "presentation", "vnd.oasis.opendocument.presentation", "vnd.oasis.opendocument.text", "txt",
            "plain", "vnd.oasis.opendocument.spreadsheet", "spreadsheet", "document", "vnd.openxmlformats-officedocument.wordprocessingml.document", "ogg", "opus", "wav", "weba",
            "vnd.openxmlformats-officedocument.spreadsheetml.sheet", "sheet", "aac", "3gp", "3gpp", "3g2", "3gpp2", "x-msvideo", "ogv", "rtf", "text", "vnd.ms-powerpoint",
            "ms-powerpoint"};
    public static final String[] videoExtFormats = new String[]{"wmv", "mp4", "webm", "flw", "mov", "avi", ".wmv", ".mp4", ".webm", ".flw", ".mov",
            ".avi", "x-msvideo", "mpeg", "ogv", "ogg", "3gpp" };
    public static final String[] imgExtFormats = new String[]{"jpg", "jpeg", "png"};
    public static final String[] docExtFormats = new String[]{"pdf", "txt", "sheet", "document", "doc", "presentation", "msword", "ms-excel", "vnd.ms-excel",
            "odt", "ods", "odp", "odg", "spreadsheet", "xls", "vnd.oasis.opendocument.presentation", "vnd.oasis.opendocument.text", "text",
            "vnd.oasis.opendocument.spreadsheet", "vnd.openxmlformats-officedocument.wordprocessingml.document",
            "vnd.openxmlformats-officedocument.spreadsheetml.sheet", "rtf", "vnd.ms-powerpoint", "ms-powerpoint"};
    public static final String[] audioExtFormats = new String[]{"mp3", "aac", "oga", "opus", "wav", "weba", "3gp", "3gpp", "mpeg", "ogg", "opus",  "3g2", "3gpp2"};
    public static final String[] textExtFormats = new String[]{"plain", "txt", "xml", "css", "csv", "htm", "html", "ics", "js", "mjs"};



    public static final MediaType docType = MediaType.parse("application/*");
    public static final MediaType pdfType = MediaType.parse("application/pdf");
    public static final MediaType imgType = MediaType.parse("image/*");
    public static final MediaType audioType = MediaType.parse("audio/*");
    public static final MediaType videoType = MediaType.parse("video/*");
    public static final MediaType txtType = MediaType.parse("text/*");







    public static final String APPOINTMENT = "Appointments";
    public static final String CHECKIN = "CheckIns";
    public static final String TOKEN = "Tokens";
    public static final String DONATION = "Donation";
    public static final String PROVIDER = "Provider";
    public static final String ORDERS = "Orders";
    public static final String REQUEST = "request";
    public static final String RESCHEDULE = "Reschedule";
    public static final String MULTIPLE_APPOINTMENT = "Multiple_Appointments";
    public static final String SINGLE_APPOINTMENT = "Single_Appointments";

    public static final String SERVICE_BOOKING_TYPE_BOOKING = "booking";
    public static final String SERVICE_BOOKING_TYPE_REQUEST = "request";




    public static final String BOOKING_CHECKIN = "bCheckIn";

    public static final String BOOKING_APPOINTMENT = "bAppointment";


    public static final String CONFIRMED = "Confirmed";
    public static final String ARRIVED = "Arrived";
    public static final String COMPLETED = "Completed";
    public static final String CANCELLED = "cancelled";
    public static final String CHECKEDIN = "Checkedin";
    public static final String REQUESTREJECTED = "RequestRejected";
    public static final String REQUESTED = "Requested";

    public static final String DONE = "done";
    public static final String CHAT = "ChatSection";
    public static final String COUNTRY_CODE = "countryCode";
    public static final String INBOX = "inbox";

    public static final String SHOPPINGCART = "SHOPPINGCART";
    public static final String SHOPPINGLIST = "SHOPPINGLIST";


    public static final String DEVICE_NAME = "deviceName";

    public static final String NONE = "NONE";
    public static final String FULLAMOUNT = "FULLAMOUNT";
    public static final String FIXED = "FIXED";
    public static final String SERVICEOPTIONQNR = "serviceoptionqnr";
    public static final String SERVICEOPTIONQIMAGES = "serviceoptionQImages";

    public static final String QUESTIONNAIRE = "questionnaire";
    public static final String QIMAGES = "qImages";
    public static final String QUESTION = "question";
    public static final String ANSWER = "answer";


    // notification purpose
    public static final String PUSH_NOTIFICATION = "pushNotification";

    //Jcash log details
    public static final String SPENT = "SPENT";
    public static final String REFUNDED = "REFUNDED";

    public static int getViewId(int id) {

        switch (id) {

//            case 1:  return R.id.id1;
        }

        return 0;

    }

    public static final String getDeviceName() {

        String deviceName = Build.MANUFACTURER
                + " " + Build.MODEL + " " + Build.VERSION.RELEASE
                + " " + Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName();


        return deviceName;
    }


}
