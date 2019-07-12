package com.jaldeeinc.jaldee.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.jaldeeinc.jaldee.common.Config;

import android.provider.Telephony;
import android.util.Log;

/**
 * A broadcast receiver who listens for incoming SMS
 */
public class SmsBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "SmsBroadcastReceiver";
    public static final String SMS_BUNDLE = "pdus";
    //interface
    private static SmsListener mListener;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            String smsSender = "";
            String smsBody = "";
            String smsAddress = "";
           /* for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                smsBody += smsMessage.getMessageBody();
                smsAddress = smsMessage.getOriginatingAddress();
            }*/

            Bundle intentExtras = intent.getExtras();
            if(intentExtras != null) {
                Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
                for (int i = 0; i < sms.length; i++) {
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);
                     smsBody = smsMessage.getMessageBody();
                     smsAddress = smsMessage.getOriginatingAddress();
                }
                Config.logV("Address----------" + smsAddress);

                if (smsAddress.toLowerCase().contains(SmsHelper.SMS_CONDITION.toLowerCase())) {
                    Log.d(TAG, "Sms with condition detected");
                    mListener.messageReceived(getOTPFromMessage(smsBody));
                    // Toast.makeText(context, "BroadcastReceiver caught conditional SMS: " + smsBody, Toast.LENGTH_LONG).show();
                }
                Log.d(TAG, "SMS detected: From " + smsSender + " With text " + smsBody);
            }

        }
    }


    private String getOTPFromMessage(String smsBody) {
        // TODO Auto-generated method stub
        String OTP = "";

        if (smsBody != null) {
            for (int i = 0; i < smsBody.length(); i++) {
                if (Character.isDigit(smsBody.charAt(i))) {
                    OTP += smsBody.charAt(i);
                }
            }
        }
        return OTP;
    }
    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }
}
