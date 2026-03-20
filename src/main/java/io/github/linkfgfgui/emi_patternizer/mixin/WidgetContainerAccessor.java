package io.github.linkfgfgui.emi_patternizer.mixin;

import appeng.client.gui.WidgetContainer;
import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(WidgetContainer.class)
public interface WidgetContainerAccessor {
    @Accessor("widgets")
    Map<String, AbstractWidget> getWidgets();
}
