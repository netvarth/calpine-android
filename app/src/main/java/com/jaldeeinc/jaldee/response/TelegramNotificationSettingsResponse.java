package com.jaldeeinc.jaldee.response;

public class TelegramNotificationSettingsResponse {
    boolean status;
    String botUrl;

    public boolean isStatus() {
        return status;
    }

    public String getBotUrl() {
        return botUrl;
    }
}
