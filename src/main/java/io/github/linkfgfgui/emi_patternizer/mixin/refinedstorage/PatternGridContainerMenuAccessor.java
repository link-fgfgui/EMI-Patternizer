package io.github.linkfgfgui.emi_patternizer.mixin.refinedstorage;

import com.refinedmods.refinedstorage.common.autocrafting.patterngrid.PatternGridContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;


@Mixin(PatternGridContainerMenu.class)
public interface PatternGridContainerMenuAccessor {
    @Invoker("sendCreatePattern")
    void invokeSendCreatePattern();

    @Accessor("patternOutputSlot")
    Slot getPatternOutputSlot();
}
