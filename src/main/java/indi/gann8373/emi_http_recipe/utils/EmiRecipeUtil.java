package indi.gann8373.emi_http_recipe.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.serializer.EmiIngredientSerializer;
import indi.gann8373.emi_http_recipe.Config;
import org.slf4j.Logger;

public class EmiRecipeUtil {
    private static Class<?> _emiApiClz = null;
    private static boolean _checkEmi = false;
    private static final Logger LOGGER = LogUtils.getLogger();


    private static boolean isEmiLoaded() {
        if (!_checkEmi) {
            return _emiApiClz != null;
        }

        try {
            _emiApiClz = Config.class.getClassLoader().loadClass("dev.emi.emi.api.EmiApi");
        } catch (ClassNotFoundException e) {
            LOGGER.info(e.getMessage());
        }
        _checkEmi = true;
        return _emiApiClz != null;
    }

    public static boolean writeRecipesByOutputItemToNode(EmiStack item, JsonObject rootNode) {
        // skip air
        var id = item.getId();
        var idStr = id.toString();
        if (isEmiLoaded()
                || "minecraft:air".equals(idStr)
                || "emi:empty".equals(idStr)
        ) {
            return false;
        }

        var recipeManager = EmiApi.getRecipeManager();
        var recipesByOutput = recipeManager.getRecipesByOutput((item));

        var arrNode = new JsonArray();
        for (EmiRecipe emiRecipe : recipesByOutput) {
            var categoryId = emiRecipe.getCategory().getId();
            // skip tag
            if (categoryId.toString().startsWith("minecraft:tag_recipes")) {
                continue;
            }

            var node = new JsonObject();
            node.addProperty("id", idStr);

            node.addProperty("category", categoryId.toString());

            var catalystsArrNode = new JsonArray();
            emiRecipe.getCatalysts().stream().map(x -> {
                var jsonNode = new JsonObject();
                var stackArrNode = new JsonArray();
                x.getEmiStacks().stream().map(EmiIngredientSerializer::getSerialized).forEach(stackArrNode::add);
                jsonNode.add("inputs", stackArrNode);

                jsonNode.addProperty("amount", x.getAmount());
                jsonNode.addProperty("chance", x.getChance());

                return jsonNode;
            }).forEach(catalystsArrNode::add);
            node.add("catalysts", catalystsArrNode);

            var inputNode = new JsonArray();
            emiRecipe.getInputs().stream().map(EmiIngredientSerializer::getSerialized).forEach(inputNode::add);
            node.add("inputs", inputNode);

            arrNode.add(node);
        }
        rootNode.add("recipesByOutput", arrNode);
        return true;
    }
}
