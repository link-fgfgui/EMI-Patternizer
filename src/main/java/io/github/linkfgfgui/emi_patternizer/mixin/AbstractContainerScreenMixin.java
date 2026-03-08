package io.github.linkfgfgui.emi_patternizer.mixin;

import appeng.client.gui.me.patternaccess.PatternAccessTermScreen;
import com.mojang.logging.LogUtils;
import io.github.linkfgfgui.emi_patternizer.Patternize;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {
    private static final Logger LOGGER = LogUtils.getLogger();
    @Inject(method = "onClose", at = @At("HEAD"), cancellable = true)
    private void preventScreenClose(CallbackInfo ci) {
        Screen currentScreen = (Screen) (Object) this;
        if (currentScreen instanceof PatternAccessTermScreen<?> && Patternize.operating) {
            ci.cancel();
            LOGGER.info("try to cancel");

        }else{
            LOGGER.info(currentScreen.toString());
        }
    }
}
