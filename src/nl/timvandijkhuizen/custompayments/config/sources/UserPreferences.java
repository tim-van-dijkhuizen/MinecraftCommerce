package nl.timvandijkhuizen.custompayments.config.sources;

import com.google.gson.JsonObject;

import nl.timvandijkhuizen.spigotutils.config.sources.JsonConfig;

public class UserPreferences extends JsonConfig {

    public UserPreferences(JsonObject json) {
        super(json);
    }
    
    public UserPreferences() {
        super();
    }

}
