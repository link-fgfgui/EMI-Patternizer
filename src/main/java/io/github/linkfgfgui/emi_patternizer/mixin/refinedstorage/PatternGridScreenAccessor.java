package io.github.linkfgfgui.emi_patternizer.mixin.refinedstorage;

import com.refinedmods.refinedstorage.common.autocrafting.patterngrid.PatternGridScreen;
import net.minecraft.client.gui.components.Button;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;


@Mixin(PatternGridScreen.class)
public interface PatternGridScreenAccessor {
    @Accessor("createPatternButton")
    Button getCreatePatternButton();
}
