package indi.gann8373.emi_http_recipe;

import com.mojang.logging.LogUtils;
import com.sun.net.httpserver.HttpServer;
import indi.gann8373.emi_http_recipe.handler.FluidRecipesByOutputHandler;
import indi.gann8373.emi_http_recipe.handler.ItemRecipesByOutputHandler;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(value = EmiHttpRecipe.MODID, dist = Dist.CLIENT)
public class EmiHttpRecipe {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "emi_http_recipe";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public EmiHttpRecipe(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }


    private HttpServer httpServer;
    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        try {
            httpServer = HttpServer.create(new InetSocketAddress(Config.PORT.get()), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        httpServer.createContext("/registry/item", new ItemRecipesByOutputHandler());
        httpServer.createContext("/registry/fluid", new FluidRecipesByOutputHandler());
        httpServer.setExecutor(null);
        httpServer.start();
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code

        }
    }
}
