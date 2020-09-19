package com.jaldeeinc.jaldee.response;

import java.io.Serializable;

public class VirtualServiceDetails implements Serializable {


    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    String Phone;


    public String getWhatsApp() {
        return WhatsApp;
    }

    public void setWhatsApp(String whatsApp) {
        WhatsApp = whatsApp;
    }

    String WhatsApp;
}
