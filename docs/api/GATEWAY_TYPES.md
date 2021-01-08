# API - Gateway types

Using gateway types you can add more ways for your players to pay.

### Create the gateway type
A gateway type is created once and then reused by all gateways.

```java
import java.util.Arrays;
import java.util.Collection;

import com.cryptomorin.xseries.XMaterial;

import nl.timvandijkhuizen.commerce.base.GatewayClient;
import nl.timvandijkhuizen.commerce.base.GatewayType;
import nl.timvandijkhuizen.commerce.elements.Gateway;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.ConfigTypes;

public class GatewayTest implements GatewayType {

    @Override
    public String getHandle() {
        // This value must be globally unique, only one gateway type can use this.
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
    public Collection<ConfigOption<?>> getOptions() {
        ConfigOption<String> test = new ConfigOption<>("test", "Test", XMaterial.APPLE, ConfigTypes.STRING);
        return Arrays.asList(test);
    }

    @Override
    public GatewayClient createClient(Gateway gateway) {
        return new ClientTest();
    }

}
```

### Create the gateway client
A gateway client is created for each gateway. This allows you to have different variable values for each gateway, which is useful when dealing configuration values.

```java
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import nl.timvandijkhuizen.commerce.base.GatewayClient;
import nl.timvandijkhuizen.commerce.elements.Order;
import nl.timvandijkhuizen.commerce.helpers.WebHelper;

public class ClientTest implements GatewayClient {

    @Override
    public String createPaymentUrl(Order order) throws Throwable {
        // Do something to generate a URL that the player uses to pay.
        return "https://test.com/pay";
    }

    @Override
    public FullHttpResponse handleWebRequest(Order order, FullHttpRequest request) throws Throwable {
        // Handle webhooks or user requests
        return WebHelper.createResponse("Test 123");
    }

}
```

### Register the gateway type
```java
import nl.timvandijkhuizen.commerce.CommerceApi;

@Override
public void onLoad() {
    CommerceApi.registerGatewayType(new GatewayTest());
}
```