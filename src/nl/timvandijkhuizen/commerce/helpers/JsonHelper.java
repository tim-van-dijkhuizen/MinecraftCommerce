package nl.timvandijkhuizen.commerce.helpers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class JsonHelper {

    public static final Gson GSON = new Gson();
    
    public static String toJson(JsonObject object) {
        return GSON.toJson(object);
    }
    
    public static JsonObject fromJson(String json) {
        return GSON.fromJson(json, JsonObject.class);
    }
    
}
