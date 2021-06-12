package com.jaldeeinc.jaldee.response;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class AnswerResponse implements Serializable {

    private Map<String,JSONObject> object = new HashMap<>();


    public Map<String, JSONObject> getObject() {
        return object;
    }

    public void setObject(Map<String, JSONObject> object) {
        this.object = object;
    }
}
