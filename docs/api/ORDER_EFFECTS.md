# API - Order effects

Using order effects you can change what happens when an order is completed.

### Create the order effect
```java
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;

import nl.timvandijkhuizen.commerce.base.OrderEffect;
import nl.timvandijkhuizen.commerce.elements.Order;

public class OrderEffectTest implements OrderEffect {

    @Override
    public String getHandle() {
        // This value must be globally unique, only one order effect can use this.
        return "test";
    }

    @Override
    public String getDisplayName() {
        return "Test";
    }

    @Override
    public XMaterial getIcon() {
        return XMaterial.APPLE;
    }

    @Override
    public void playEffect(Player player, Order order) {
        player.sendMessage(ChatColor.GREEN + "Thanks for your order " + player.getName());
        player.playSound(player.getLocation(), XSound.ENTITY_PLAYER_LEVELUP.parseSound(), 1, 1);
    }

}
```

### Register the gateway effect
```java
import nl.timvandijkhuizen.commerce.CommerceApi;

@Override
public void onLoad() {
    CommerceApi.registerOrderEffect(new OrderEffectTest());
}
```