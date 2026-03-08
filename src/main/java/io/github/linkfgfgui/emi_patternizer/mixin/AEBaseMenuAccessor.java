package io.github.linkfgfgui.emi_patternizer.mixin;

import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantic;
import com.google.common.collect.ArrayListMultimap;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AEBaseMenu.class)
public interface AEBaseMenuAccessor {
    @Accessor("slotsBySemantic")
    ArrayListMultimap<SlotSemantic, Slot> getSlotsBySemantic();
}
