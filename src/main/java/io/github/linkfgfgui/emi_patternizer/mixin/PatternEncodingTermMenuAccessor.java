package io.github.linkfgfgui.emi_patternizer.mixin;

import appeng.menu.SlotSemantic;
import appeng.menu.me.items.PatternEncodingTermMenu;
import appeng.menu.slot.RestrictedInputSlot;
import com.google.common.collect.ArrayListMultimap;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PatternEncodingTermMenu.class)
public interface PatternEncodingTermMenuAccessor {
    @Accessor("blankPatternSlot")
    RestrictedInputSlot getBlankPatternSlot();

    @Accessor("encodedPatternSlot")
    RestrictedInputSlot getEncodedPatternSlot();
}