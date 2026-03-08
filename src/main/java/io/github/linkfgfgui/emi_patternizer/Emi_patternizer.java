package io.github.linkfgfgui.emi_patternizer;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(value = Emi_patternizer.MODID, dist = Dist.CLIENT)
public class Emi_patternizer {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "emi_patternizer";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Lazy<KeyMapping> PATTERNIZE_MAPPING = Lazy.of(() -> new KeyMapping(
            "key.emi_patternizer.patternize",
            KeyConflictContext.GUI,
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            "key.categories.emi_patternizer.category"
    ));
    public Emi_patternizer(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::registerKeyMappings);
        NeoForge.EVENT_BUS.register(new Patternize());
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info(Emi_patternizer.MODID + " has been initialized");
    }

    private void registerKeyMappings(final RegisterKeyMappingsEvent event) {
        event.register(PATTERNIZE_MAPPING.get());
    }
}
