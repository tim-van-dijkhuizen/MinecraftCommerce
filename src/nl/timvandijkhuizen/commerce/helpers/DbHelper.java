package nl.timvandijkhuizen.commerce.helpers;

import org.bukkit.Material;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import nl.timvandijkhuizen.commerce.base.GatewayType;
import nl.timvandijkhuizen.commerce.config.sources.GatewayConfig;
import nl.timvandijkhuizen.commerce.config.sources.UserPreferences;
import nl.timvandijkhuizen.spigotutils.config.sources.JsonConfig;

public class DbHelper {

    public static final Gson GSON = new Gson();
    
    public static String prepareMaterial(Material material) {
        return material.name();
    }

    public static Material parseMaterial(String raw) {
        try {
            return Material.valueOf(raw);
        } catch (IllegalArgumentException e) {
            return Material.CHEST;
        }
    }
    
    public static String prepareJson(JsonObject json) {
        return GSON.toJson(json);
    }

    public static JsonObject parseJson(String json) {
        return GSON.fromJson(json, JsonObject.class);
    }
    
    public static String prepareJsonConfig(JsonConfig config) {
        return prepareJson(config.getJson());
    }

    public static UserPreferences parseUserPreferences(String json) {
        return new UserPreferences(parseJson(json));
    }
    
    public static GatewayConfig parseGatewayConfig(String json, GatewayType type) {
        return new GatewayConfig(type, parseJson(json));
    }

}
