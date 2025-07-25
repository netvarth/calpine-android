package com.nv.youneverwait.response;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import com.nv.youneverwait.common.Config;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sharmila on 21/11/18.
 */

public class SearchVirtualFields {
    String name;
    String displayName;

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDataType() {
        return dataType;
    }

    public ArrayList getDomain() {
        return domain;
    }

    public ArrayList getSubdomain() {
        return subdomain;
    }

    String dataType;
    @SerializedName("domain")
    ArrayList domain;
    @SerializedName("subdomain")
    ArrayList subdomain;
/*
  *//*  public Object getValue() {
        return value;
    }*//*

   *//* @SerializedName("value")
    private Object value;*//*
    public static class DataStateDeserializer implements JsonDeserializer<SearchVirtualFields> {

        @Override
        public SearchVirtualFields deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            SearchVirtualFields userResponse = new Gson().fromJson(json, SearchVirtualFields.class);
            JsonObject jsonObject = json.getAsJsonObject();

            if (jsonObject.has("value")) {
                JsonElement elem = jsonObject.get("value");
                if (elem != null && !elem.isJsonNull()) {
                    if(elem.isJsonPrimitive()){
                        Config.logV("Value @@@@@@@@@@@");
                    }else{
                        Config.logV("Value @@@@@@@@@@@!!!!!");

                    }
                }
            }
            return userResponse ;
        }
    }

  */




    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    String head;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    String result;
    String type;
}
