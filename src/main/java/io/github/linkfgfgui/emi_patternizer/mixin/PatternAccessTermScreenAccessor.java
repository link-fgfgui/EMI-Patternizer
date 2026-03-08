package io.github.linkfgfgui.emi_patternizer.mixin;

import appeng.api.storage.IPatternAccessTermMenuHost;
import appeng.client.gui.me.patternaccess.PatternAccessTermScreen;
import appeng.client.gui.me.patternaccess.PatternContainerRecord;
import appeng.helpers.patternprovider.PatternContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.HashMap;
import java.util.Set;

@Mixin(PatternAccessTermScreen.class)
public interface PatternAccessTermScreenAccessor {
    @Accessor("byId")
    HashMap<Long, PatternContainerRecord> getById();
}