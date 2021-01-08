# API - Template resolvers

Using template resolvers you can render your own templates. You need this if you want to render custom templates with your plugin.

For more information check the thymeleaf documentation:
https://www.thymeleaf.org/apidocs/thymeleaf/2.0.2/org/thymeleaf/templateresolver/TemplateResolver.html

### Register the template resolver
```java
import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import nl.timvandijkhuizen.commerce.CommerceApi;

@Override
public void onLoad() {
    ClassLoaderTemplateResolver resourceResolver = new ClassLoaderTemplateResolver(getClassLoader());

    resourceResolver.setCheckExistence(true);
    resourceResolver.setPrefix("templates" + File.separatorChar);
    resourceResolver.setSuffix(".html");
    resourceResolver.setCharacterEncoding("UTF-8");
    resourceResolver.setTemplateMode(TemplateMode.HTML);
    resourceResolver.setOrder(3);

    CommerceApi.registerTemplateResolver(resourceResolver);
}
```