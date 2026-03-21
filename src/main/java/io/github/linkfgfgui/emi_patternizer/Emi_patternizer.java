package io.github.linkfgfgui.emi_patternizer;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;


@Mod(value = Emi_patternizer.MODID)
public class Emi_patternizer {
    public static final String MODID = "emi_patternizer";
    public static final Lazy<KeyMapping> PATTERNIZE_MAPPING;
    private static final Logger LOGGER = LogUtils.getLogger();

    static {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            PATTERNIZE_MAPPING = Lazy.of(() -> new KeyMapping(
                    "key.emi_patternizer.patternize",
                    KeyConflictContext.GUI,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_N,
                    "key.categories.emi_patternizer.category"
            ));
        } else {
            PATTERNIZE_MAPPING = Lazy.of(() -> null);
        }
    }

    public Emi_patternizer() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
            modEventBus.addListener(this::clientSetup);
            modEventBus.addListener(this::registerKeyMappings);
            MinecraftForge.EVENT_BUS.addListener(Patternize::onKeyPressed);
            MinecraftForge.EVENT_BUS.addListener(ReloadMemory::onScreenOpening);
            ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
        }
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.debug(Emi_patternizer.MODID + " has been initialized");
    }

    private void registerKeyMappings(final RegisterKeyMappingsEvent event) {
        event.register(PATTERNIZE_MAPPING.get());
    }
}
