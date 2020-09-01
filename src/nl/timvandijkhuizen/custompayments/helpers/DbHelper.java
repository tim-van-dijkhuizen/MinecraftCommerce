package nl.timvandijkhuizen.custompayments.helpers;

import org.bukkit.Material;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import nl.timvandijkhuizen.custompayments.base.GatewayType;
import nl.timvandijkhuizen.custompayments.config.sources.GatewayConfig;
import nl.timvandijkhuizen.custompayments.config.sources.UserPreferences;
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
    
    public static String prepareJsonConfig(JsonConfig config) {
        return GSON.toJson(config.getJson());
    }

    public static UserPreferences parseUserPreferences(String raw) {
        JsonObject json = GSON.fromJson(raw, JsonObject.class);
        return new UserPreferences(json);
    }
    
    public static GatewayConfig parseGatewayConfig(String raw, GatewayType type) {
        JsonObject json = GSON.fromJson(raw, JsonObject.class);
        return new GatewayConfig(type, json);
    }

}
