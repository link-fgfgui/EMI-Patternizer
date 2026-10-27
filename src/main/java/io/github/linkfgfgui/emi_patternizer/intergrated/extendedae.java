package io.github.linkfgfgui.emi_patternizer.intergrated;

import appeng.client.gui.me.patternaccess.PatternContainerRecord;
import io.github.linkfgfgui.emi_patternizer.mixin.extendedae.GuiExPatternTerminalAccessor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.level.Level;

import java.util.Collection;

/**
 * ExtendedAE "Extended Pattern Management Terminal" adapter.
 * <p>
 * Its screen is not an AE2 {@code PatternAccessTermScreen}, but it keeps the same
 * {@code PatternContainerRecord} map, so the records are read through an accessor mixin.
 * The wireless variant ({@code GuiWirelessExPAT}) extends this screen and is covered by it.
 */
public class extendedae extends appliedenergistics2 {
    public extendedae(AbstractContainerScreen<?> screen) {
        super(screen);
    }

    @Override
    public long getPatternCount(Level level) {
        Collection<PatternContainerRecord> records = ((GuiExPatternTerminalAccessor) screen).getById().values();
        return countPatterns(records, level);
    }
}
