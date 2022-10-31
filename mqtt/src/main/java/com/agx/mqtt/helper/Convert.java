package com.agx.mqtt.helper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

public class Convert {
    private static Type type = new TypeToken<HashMap>() {}.getType();

    public static String HashMapToJson(HashMap hashMap){
        Gson gson = new Gson();

        return gson.toJson(hashMap,type);
    }
    public static HashMap HashMapFromJson(String json){
        Gson gson = new Gson();
        return gson.fromJson(json,type);

    }
}
