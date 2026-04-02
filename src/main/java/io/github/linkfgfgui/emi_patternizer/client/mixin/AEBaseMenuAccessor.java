package io.github.linkfgfgui.emi_patternizer.client.mixin;

import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantic;
import com.google.common.collect.ArrayListMultimap;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = AEBaseMenu.class, remap = false)
public interface AEBaseMenuAccessor {
    @Accessor("slotsBySemantic")
    ArrayListMultimap<SlotSemantic, Slot> getSlotsBySemantic();
}
