package indi.gann8373.emi_http_recipe.utils;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import lombok.Data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Data
public class RespUtil {

    /**
     * write http response
     */
    public static void writeResponse(JsonObject jsonNode, HttpExchange exchange) throws IOException {
        var bytes = jsonNode.toString().getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, bytes.length);
        exchange.getResponseHeaders().set("Content-Type", "application/json");

         try (var responseBody = exchange.getResponseBody()) {
            responseBody.write(bytes);
            responseBody.flush();
        }
    }
}
