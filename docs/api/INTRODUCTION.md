# API - Introduction

The developer API allows you to extend MinecraftCommerce. You should only interact with the plugin through its public API: `nl.timvandijkhuizen.commerce.CommerceApi`. I recommend that you use my [SpigotUtils](https://www.spigotmc.org/resources/spigotutils.86830/) plugin API. This utility plugin helps you develop better plugins with less code.

### Where should I place my code?
All extensions should be registered before MinecraftCommerce is initiated. To do this you must either register them from `onLoad()` or `setup()` if you're using the plugin API from SpigotUtils.

### What can I do with the API?
* [Custom storage types](STORAGE_TYPES.md)
* [Custom field types](FIELD_TYPES.md)
* [Custom gateway types](GATEWAY_TYPES.md)
* [Custom order variables](FIELD_TYPES.md)
* [Custom order effects](FIELD_TYPES.md)
* [Custom template resolvers](FIELD_TYPES.md)