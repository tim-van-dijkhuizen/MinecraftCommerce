package nl.timvandijkhuizen.custompayments.helpers;

import org.bukkit.Material;

public class DbHelper {

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

}
