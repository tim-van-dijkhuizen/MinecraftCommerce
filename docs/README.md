# MinecraftCommerce
A Spigot plugin for creating in-game donation stores.

More information: 
https://www.spigotmc.org/resources/minecraft-commerce.86973

### Documentation

* [Configuration](setup/CONFIGURATION.md)
* [SSL with CloudFlare](setup/SSL_WITH_CLOUDFLARE.md)
* [Categories](admin/CATEGORIES.md)
* [Products](admin/PRODUCTS.md)
* [Fields](admin/FIELDS.md)
* [Gateways](admin/GATEWAYS.md)
* [Orders](admin/ORDERS.md)

### Developer API
The developer API allows you to extend MinecraftCommerce. You should only interact with the plugin through its public API: `nl.timvandijkhuizen.commerce.CommerceApi`. I recommend that you use my [SpigotUtils](https://www.spigotmc.org/resources/spigotutils.86830/) plugin API. This utility plugin helps you develop better plugins with less code.

##### Where should I place my code?
All extensions should be registered before MinecraftCommerce is initiated. To do this you must either register them from `onLoad()` or `setup()` if you're using the plugin API from SpigotUtils.

##### What can I do with the API?
* [Custom storage types](api/STORAGE_TYPES.md)
* [Custom field types](api/FIELD_TYPES.md)
* [Custom gateway types](api/GATEWAY_TYPES.md)
* [Custom order variables](api/ORDER_VARIABLES.md)
* [Custom order effects](api/ORDER_EFFECTS.md)
* [Custom template resolvers](api/TEMPLATE_RESOLVERS.md)
