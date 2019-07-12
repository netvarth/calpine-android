package com.jaldeeinc.jaldee.connection.rest;

/**
 * Created by sharmila on 2/7/18.
 */

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.jaldeeinc.jaldee.common.Config;

import java.io.IOException;

public class ItemTypeAdapterFactory implements TypeAdapterFactory {

    public <T> TypeAdapter<T> create(Gson gson, final TypeToken<T> type) {

        Config.logV("Item TYpe");
        final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
        final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);

        return new TypeAdapter<T>() {

            public void write(JsonWriter out, T value) throws IOException
            {
                delegate.write(out, value);
            }

            public T read(JsonReader in) throws IOException
            {
                Config.logV("Item TYpe11");

                JsonElement jsonElement = elementAdapter.read(in);
                if (jsonElement.isJsonObject())
                {

                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    Config.logV("Item TYpe22"+jsonObject.get("code").getAsInt());
                    if (jsonObject.has("code") && jsonObject.get("code").getAsInt() == 404)
                    {
                        throw new IllegalArgumentException(jsonObject.get("message").getAsString());
                    }
                    if (jsonObject.has("code") && jsonObject.get("code").getAsInt() == 200)
                    {
                        Config.logV("Item TYpe333");
                        Config.logV("Sucesss-------------------------");
                        throw new IllegalArgumentException(jsonObject.get("message").getAsString());
                    }
                }

                return delegate.fromJsonTree(jsonElement);
            }
        }.nullSafe();
    }
}