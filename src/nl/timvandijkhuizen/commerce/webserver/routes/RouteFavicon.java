package nl.timvandijkhuizen.commerce.webserver.routes;

import java.io.File;
import java.io.FileInputStream;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.helpers.WebHelper;
import nl.timvandijkhuizen.commerce.webserver.ContentType;
import nl.timvandijkhuizen.commerce.webserver.StaticRoute;
import nl.timvandijkhuizen.commerce.webserver.errors.NotFoundHttpException;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;

public class RouteFavicon implements StaticRoute {

    @Override
    public void handleRequest(ChannelHandlerContext ctx, FullHttpRequest request) throws Throwable {
        YamlConfig config = Commerce.getInstance().getConfig();
        ConfigOption<File> option = config.getOption("webserver.favicon");
        File favicon = option.getValue(config);

        if (favicon == null) {
            throw new NotFoundHttpException("");
        }

        // Send file
        WebHelper.sendFileResponse(ctx, request, ContentType.IMAGE_ICO, new FileInputStream(favicon));
    }

}
