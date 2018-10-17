package com.nv.youneverwait.service;

/**
 * Created by sharmila on 13/8/18.
 */

public interface SmsListener {
    public void messageReceived(String messageText);
}