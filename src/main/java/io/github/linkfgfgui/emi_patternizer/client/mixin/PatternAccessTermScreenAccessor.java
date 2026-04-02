package io.github.linkfgfgui.emi_patternizer.client.mixin;

import appeng.client.gui.me.patternaccess.PatternAccessTermScreen;
import appeng.client.gui.me.patternaccess.PatternContainerRecord;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.HashMap;

@Mixin(value = PatternAccessTermScreen.class, remap = false)
public interface PatternAccessTermScreenAccessor {
    @Accessor("byId")
    HashMap<Long, PatternContainerRecord> getById();
}
