package com.example.mrrobot.concurrent.Utils;

import android.util.ArrayMap;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Utils {

    public static JSONObject toJsonObject(HashMap<String,String> arrayMap){
        JSONObject jsonObject = new JSONObject();
        try {

            for(Map.Entry<String, String> entry: arrayMap.entrySet()){
                String key=entry.getKey();
                String value= entry.getValue();
                jsonObject.put(key,value);
            }
        } catch (JSONException e) {
            Log.d("JSONException", e.getMessage());
        }
        return jsonObject;
    }
    public static JSONObject toJsonObject(String key,String value){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(key,value);

        } catch (JSONException e) {
            Log.d("JSONException", e.getMessage());
        }
        return jsonObject;
    }
    public static JSONObject toJsonObject(String ... kv){
        JSONObject jsonObject = new JSONObject();
        try {
            for(int i=0;i<kv.length;i=i+2){
                String key=kv[i];
                String value=kv[i+1];
                jsonObject.put(key,value);
            }
        } catch (JSONException e) {
            Log.d("JSONException", e.getMessage());
        }
        return jsonObject;
    }
}
