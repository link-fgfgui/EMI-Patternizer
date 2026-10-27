package io.github.linkfgfgui.emi_patternizer.mixin.extendedae;

import appeng.client.gui.me.patternaccess.PatternContainerRecord;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.HashMap;

@Mixin(targets = "com.glodblock.github.extendedae.client.gui.GuiExPatternTerminal")
public interface GuiExPatternTerminalAccessor {
    @Accessor("byId")
    HashMap<Long, PatternContainerRecord> getById();
}
