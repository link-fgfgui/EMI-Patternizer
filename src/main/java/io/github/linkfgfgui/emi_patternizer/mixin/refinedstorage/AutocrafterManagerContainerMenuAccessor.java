package io.github.linkfgfgui.emi_patternizer.mixin.refinedstorage;

import com.refinedmods.refinedstorage.common.autocrafting.autocraftermanager.AutocrafterManagerContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;


@Mixin(AutocrafterManagerContainerMenu.class)
public interface AutocrafterManagerContainerMenuAccessor {
    @Invoker("getAutocrafterSlots")
    List<Object> invokeGetAutocrafterSlots();
}
