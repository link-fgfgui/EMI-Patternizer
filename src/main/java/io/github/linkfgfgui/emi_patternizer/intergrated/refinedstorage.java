package io.github.linkfgfgui.emi_patternizer.intergrated;

import com.refinedmods.refinedstorage.api.autocrafting.Pattern;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.autocrafting.patterngrid.PatternGridContainerMenu;
import com.refinedmods.refinedstorage.common.autocrafting.patterngrid.PatternGridScreen;
import com.refinedmods.refinedstorage.common.support.AbstractBaseContainerMenu;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
import com.refinedmods.refinedstorage.common.support.stretching.AbstractStretchingScreen;
import io.github.linkfgfgui.emi_patternizer.mixin.refinedstorage.PatternGridContainerMenuAccessor;
import io.github.linkfgfgui.emi_patternizer.mixin.refinedstorage.PatternGridScreenAccessor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static io.github.linkfgfgui.emi_patternizer.Patternize.EncodedItems;

public class refinedstorage implements Api {

    AbstractStretchingScreen<?> screen;
    AbstractBaseContainerMenu menu;
    PatternGridContainerMenu encodeMenu;

    public refinedstorage(AbstractContainerScreen<?> screen) {
        this.screen = (AbstractStretchingScreen<?>) screen;
        if (screen instanceof PatternGridScreen screen1) {
            this.encodeMenu = screen1.getMenu();
        }
        this.menu = (AbstractBaseContainerMenu) screen.getMenu();
    }

    @Override
    public void encode(boolean isSimulateClick) {
        if (isSimulateClick) {
            ((PatternGridScreenAccessor) screen).getCreatePatternButton().onPress();
        } else {
            ((PatternGridContainerMenuAccessor) encodeMenu).invokeSendCreatePattern();
//            C2SPackets.sendPatternGridCreatePattern(); // ???
        }
    }

    @Override
    public int getEncodedPatternSlot() {
        return ((PatternGridContainerMenuAccessor) encodeMenu).getPatternOutputSlot().index;
    }

    @Override
    public long getPatternCount(Level level) {
        AtomicLong PatternCount = new AtomicLong();
        List<ItemStack> patternSlots = menu.getItems();
        patternSlots.forEach((item) -> {
            if (item.isEmpty()) return;
            Optional<Pattern> details = RefinedStorageApi.INSTANCE.getPattern(item, level);
            details.ifPresent(pattern -> pattern.layout().outputs().forEach(
                    resourceAmount -> {
                        ResourceKey resource = resourceAmount.resource();
                        if (resource instanceof ItemResource itemResource) {
                            EncodedItems.add(itemResource.item().toString());
                            PatternCount.getAndIncrement();
                        }
                    }
            ));
        });
        return PatternCount.get();
    }
}
