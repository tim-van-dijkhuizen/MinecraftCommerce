package nl.timvandijkhuizen.commerce.helpers;

import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Material;

import com.cryptomorin.xseries.XMaterial;
import com.google.gson.JsonObject;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.GatewayType;
import nl.timvandijkhuizen.commerce.config.objects.StoreCurrency;
import nl.timvandijkhuizen.commerce.config.sources.GatewayConfig;
import nl.timvandijkhuizen.commerce.config.sources.OrderFieldData;
import nl.timvandijkhuizen.commerce.config.sources.UserPreferences;
import nl.timvandijkhuizen.spigotutils.config.sources.JsonConfig;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;

public class DbHelper {

    public static Integer getInteger(ResultSet result, int column) throws SQLException {
        int value = result.getInt(column);
        return result.wasNull() ? null : value;
    }
    
    public static byte[] prepareUniqueId(UUID uuid) {
        byte[] bytes = new byte[16];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        
        return bytes;
    }

    public static UUID parseUniqueId(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        long high = buffer.getLong();
        long low = buffer.getLong();
        
        return new UUID(high, low);
    }
    
    public static String prepareMaterial(Material material) {
        return material.name();
    }

    public static Material parseMaterial(String raw) {
        try {
            return Material.valueOf(raw);
        } catch (IllegalArgumentException e) {
            return XMaterial.BARRIER.parseMaterial(true);
        }
    }

    public static String prepareJson(JsonObject object) {
        return JsonHelper.toJson(object);
    }

    public static JsonObject parseJson(String json) {
        return JsonHelper.fromJson(json);
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

    public static OrderFieldData parseOrderFields(String json) {
        return new OrderFieldData(parseJson(json));
    }

    public static String prepareCurrency(StoreCurrency currency) {
        return currency.getCode().getCurrencyCode();
    }

    public static StoreCurrency parseCurrency(String code) {
        YamlConfig pluginConfig = Commerce.getInstance().getConfig();
        
        // Get all currencies
        List<StoreCurrency> currencies = pluginConfig.getOptionValue("general.currencies");
    
        Optional<StoreCurrency> currency = currencies.stream()
            .filter(i -> i.getCode().getCurrencyCode().equals(code))
            .findFirst();
    
        return currency.orElse(null);
    }

}
