package io.github.linkfgfgui.emi_patternizer.intergrated;

import appeng.parts.encoding.EncodingMode;
import com.mojang.logging.LogUtils;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

import java.lang.reflect.Method;

/**
 * WCWT (AE2 Wireless Comprehensive Work Terminal) adapter.
 * <p>
 * Its terminal screen is a {@code CraftingTermScreen}, not an AE2 {@code PatternEncodingTermScreen},
 * so filling is delegated to the EMI recipe handler WCWT registers for its own menu type and
 * encoding goes through {@code WirelessComprehensiveWorkTerminalMenu#encodePattern(EncodingMode)}.
 */
public class wcwt extends appliedenergistics2 {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final Object wcwtMenu;

    public wcwt(AbstractContainerScreen<?> screen) {
        super(screen);
        this.wcwtMenu = screen.getMenu();
    }

    @Override
    public void encode(boolean isSimulateClick) {
        encode(isSimulateClick, null);
    }

    @Override
    public void encode(boolean isSimulateClick, EmiRecipe recipe) {
        EncodingMode mode = recipe != null ? transferMode(recipe) : syncedMode();
        try {
            Method encodePattern = wcwtMenu.getClass().getMethod("encodePattern", EncodingMode.class);
            encodePattern.invoke(wcwtMenu, mode);
        } catch (ReflectiveOperationException e) {
            LOGGER.error("Failed to encode pattern in WCWT terminal", e);
        }
    }

    /**
     * Fallback for callers without a recipe: the mode WCWT last synced to the client.
     */
    private EncodingMode syncedMode() {
        try {
            Object mode = wcwtMenu.getClass().getMethod("getPatternEncodingMode").invoke(wcwtMenu);
            return mode instanceof EncodingMode encodingMode ? encodingMode : EncodingMode.PROCESSING;
        } catch (ReflectiveOperationException e) {
            LOGGER.error("Failed to read WCWT pattern encoding mode", e);
            return EncodingMode.PROCESSING;
        }
    }

    /**
     * Mirrors WCWT's own recipe-to-mode mapping so the server encodes with the same mode
     * that the EMI fill used, without waiting for the mode data slot to sync back.
     */
    private static EncodingMode transferMode(EmiRecipe recipe) {
        if (recipe.getCategory() == VanillaEmiRecipeCategories.CRAFTING && recipe.supportsRecipeTree()) {
            return EncodingMode.CRAFTING;
        }
        if (recipe.getCategory() == VanillaEmiRecipeCategories.SMITHING) {
            return EncodingMode.SMITHING_TABLE;
        }
        if (recipe.getCategory() == VanillaEmiRecipeCategories.STONECUTTING) {
            return EncodingMode.STONECUTTING;
        }
        return EncodingMode.PROCESSING;
    }

    @Override
    public long getPatternCount(Level level) {
        // WCWT's work terminal is not an AE2 pattern access terminal.
        return 0;
    }
}
