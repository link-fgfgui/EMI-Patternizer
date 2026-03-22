package io.github.linkfgfgui.emi_patternizer.mixin.ae2;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.WidgetContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AEBaseScreen.class)
public interface AEBaseScreenAccessor {
    @Accessor("widgets")
    WidgetContainer getWidgets();
}
