package io.github.linkfgfgui.emi_patternizer.client;

import appeng.client.gui.me.items.PatternEncodingTermScreen;
import com.mojang.blaze3d.platform.InputConstants;
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.fml.config.ModConfig;
import org.lwjgl.glfw.GLFW;

public class Emi_patternizer implements ClientModInitializer {

    public static String MOD_ID = "emi_patternizer";
    private static KeyMapping encodeKeyBinding;

    @Override
    public void onInitializeClient() {
        encodeKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.emi_patternizer.patternize",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_N,
                "key.categories.emi_patternizer.category"
        ));

        ScreenEvents.BEFORE_INIT.register((client, anyScreen, scaledWidth, scaledHeight) -> {
            if (anyScreen instanceof PatternEncodingTermScreen<?>) {
                ScreenKeyboardEvents.beforeKeyPress(anyScreen).register((screen, key, scancode, modifiers) -> {
                    if (screen.getFocused() instanceof net.minecraft.client.gui.components.EditBox) {
                        return;
                    }
                    if (encodeKeyBinding.matches(key, scancode)) {
                        Patternize.onKeyPressed((PatternEncodingTermScreen<?>) screen);
                    }
                });
            }
        });
        ForgeConfigRegistry.INSTANCE.register(MOD_ID, ModConfig.Type.CLIENT, Config.SPEC);
    }
}
