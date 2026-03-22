package io.github.linkfgfgui.emi_patternizer.intergrated;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.WidgetContainer;
import appeng.client.gui.me.items.PatternEncodingTermScreen;
import appeng.client.gui.me.patternaccess.PatternContainerRecord;
import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;
import appeng.menu.me.items.PatternEncodingTermMenu;
import io.github.linkfgfgui.emi_patternizer.mixin.WidgetContainerAccessor;
import io.github.linkfgfgui.emi_patternizer.mixin.ae2.AEBaseMenuAccessor;
import io.github.linkfgfgui.emi_patternizer.mixin.ae2.AEBaseScreenAccessor;
import io.github.linkfgfgui.emi_patternizer.mixin.ae2.PatternAccessTermScreenAccessor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;

import static io.github.linkfgfgui.emi_patternizer.Patternize.EncodedItems;

public class appliedenergistics2 implements Api {
    AEBaseScreen<?> screen;
    AEBaseMenu menu;
    PatternEncodingTermMenu encodeMenu;

    public appliedenergistics2(AbstractContainerScreen<?> screen) {
        this.screen = (AEBaseScreen<?>) screen;
        if (screen instanceof PatternEncodingTermScreen<?> screen1) {
            this.encodeMenu = screen1.getMenu();
        }
        this.menu = (AEBaseMenu) screen.getMenu();
    }


    @Override
    public void encode(boolean isSimulateClick) {
        if (isSimulateClick) {
            WidgetContainer widgets = ((AEBaseScreenAccessor) screen).getWidgets();
            AbstractWidget widget = ((WidgetContainerAccessor) widgets).getWidgets().get("encodePattern");
            if (widget instanceof Button but) {
                but.onPress();
            }
        } else {
            encodeMenu.encode();
        }
    }

    @Override
    public int getEncodedPatternSlot() {
        return ((AEBaseMenuAccessor) menu).getSlotsBySemantic().get(SlotSemantics.ENCODED_PATTERN).getFirst().index;
    }

    @Override
    public long getPatternCount(Level level) {
        AtomicLong PatternCount = new AtomicLong();
        Collection<PatternContainerRecord> patternContainerRecordSet = ((PatternAccessTermScreenAccessor) screen).getById().values();
        for (PatternContainerRecord entry : patternContainerRecordSet) {
            entry.getInventory().toItemContainerContents().stream().forEach((item) -> {
                IPatternDetails details = PatternDetailsHelper.decodePattern(item, level);
                PatternCount.getAndIncrement();
                if (details == null) {
                } else {
                    details.getOutputs().forEach(
                            genericStack -> {
                                EncodedItems.add(genericStack.what().getId().toString());
                            }
                    );
                }
            });
        }
        return PatternCount.get();
    }

}
