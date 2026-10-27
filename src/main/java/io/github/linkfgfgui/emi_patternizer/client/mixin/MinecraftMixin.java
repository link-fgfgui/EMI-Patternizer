package io.github.linkfgfgui.emi_patternizer.client.mixin;

import appeng.client.gui.me.interfaceterminal.InterfaceTerminalScreen;
import io.github.linkfgfgui.emi_patternizer.client.ReloadMemory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "setScreen", at = @At("HEAD"))
    private void onOpenScreen(Screen screen, CallbackInfo ci) {
        if (screen instanceof InterfaceTerminalScreen<?> interfaceTerminalScreen) {
            ReloadMemory.onScreenOpening(interfaceTerminalScreen);
        }
    }
}
