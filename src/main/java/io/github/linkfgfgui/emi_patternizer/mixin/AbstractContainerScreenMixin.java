package io.github.linkfgfgui.emi_patternizer.mixin;

import appeng.client.gui.me.items.PatternEncodingTermScreen;
import io.github.linkfgfgui.emi_patternizer.Patternize;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {
    @Inject(method = "onClose", at = @At("HEAD"), cancellable = true)
    private void preventScreenClose(CallbackInfo ci) {
        Screen currentScreen = (Screen) (Object) this;
        if (currentScreen instanceof PatternEncodingTermScreen<?> && Patternize.operating) {
            ci.cancel();
        }
    }
}
