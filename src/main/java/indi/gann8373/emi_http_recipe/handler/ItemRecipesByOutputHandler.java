package indi.gann8373.emi_http_recipe.handler;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dev.emi.emi.api.stack.EmiStack;
import indi.gann8373.emi_http_recipe.utils.EmiRecipeUtil;
import indi.gann8373.emi_http_recipe.utils.RespUtil;
import lombok.Data;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

@Data
public class ItemRecipesByOutputHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        var path = exchange.getRequestURI().getPath();

        var split = path.split("/");

        if (split.length <= 2) {
            return;
        }

        var name = split[split.length - 1];
        var namespace = split[split.length - 2];

        var parse = ResourceLocation.parse(namespace + ":" + name);

        var item = BuiltInRegistries.ITEM.get(parse);

        var rootNode = new JsonObject();

        EmiRecipeUtil.writeRecipesByOutputItemToNode(EmiStack.of(item), rootNode);

        RespUtil.writeResponse(rootNode,exchange);

    }
}
