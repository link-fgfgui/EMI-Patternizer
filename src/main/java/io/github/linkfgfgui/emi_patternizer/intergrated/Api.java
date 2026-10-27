package io.github.linkfgfgui.emi_patternizer.intergrated;

import dev.emi.emi.api.recipe.EmiRecipe;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;

import java.util.HashMap;
import java.util.Map;


public interface Api {
    Map<String, Class<?>> CLASS_CACHE = new HashMap<>();
    String WCWT_SCREEN = "com.lhy.wcwt.client.WirelessComprehensiveWorkTerminalScreen";
    String EXTENDEDAE_ACCESS_SCREEN = "com.glodblock.github.extendedae.client.gui.GuiExPatternTerminal";

    static boolean isInstanceOf(Object obj, String className) {
        if (obj == null) return false;
        Class<?> clazz = CLASS_CACHE.get(className);
        if (clazz == null) {
            try {
                clazz = Class.forName(className);
                CLASS_CACHE.put(className, clazz);
            } catch (ClassNotFoundException e) {
                CLASS_CACHE.put(className, Void.class);
                return false;
            }
        }
        if (clazz == Void.class) return false;
        return clazz.isInstance(obj);
    }

     class INTEGRATED {
        public static boolean AE2;
        public static boolean RS;
        public static boolean WCWT;
        public static boolean EXTENDEDAE;

        static {
            ModList list = ModList.get();
            if (list.isLoaded("ae2")) {
                AE2 = true;
            } else if (list.isLoaded("refinedstorage_emi_integration")) {
                RS = true;
            }
            WCWT = list.isLoaded("wcwt");
            EXTENDEDAE = list.isLoaded("extendedae");
        }
    }


    static Api getApi(AbstractContainerScreen<?> screen) {
        if (INTEGRATED.WCWT && isInstanceOf(screen, WCWT_SCREEN)) {
            return new wcwt(screen);
        } else if (INTEGRATED.EXTENDEDAE && isInstanceOf(screen, EXTENDEDAE_ACCESS_SCREEN)) {
            return new extendedae(screen);
        } else if (INTEGRATED.AE2) {
            return new appliedenergistics2(screen);
        } else if (INTEGRATED.RS) {
            return new refinedstorage(screen);
        } else {
            return null;
        }
    }

    static boolean isValidEncodingScreen(Screen screen) {
        if (INTEGRATED.WCWT && isInstanceOf(screen, WCWT_SCREEN)) {
            return true;
        } else if (INTEGRATED.AE2) {
            return isInstanceOf(screen, "appeng.client.gui.me.items.PatternEncodingTermScreen");
        } else if (INTEGRATED.RS) {
            return isInstanceOf(screen, "com.refinedmods.refinedstorage.common.autocrafting.patterngrid.PatternGridScreen");
        }
        return false;
    }
    static boolean isValidAccessScreen(Screen screen) {
        if (INTEGRATED.EXTENDEDAE && isInstanceOf(screen, EXTENDEDAE_ACCESS_SCREEN)) {
            return true;
        } else if (INTEGRATED.AE2) {
            return isInstanceOf(screen, "appeng.client.gui.me.patternaccess.PatternAccessTermScreen");
        } else if (INTEGRATED.RS) {
            return isInstanceOf(screen, "com.refinedmods.refinedstorage.common.autocrafting.autocraftermanager.AutocrafterManagerScreen");
        }
        return false;
    }

    void encode(boolean isSimulateClick);

    default void encode(boolean isSimulateClick, EmiRecipe recipe) {
        encode(isSimulateClick);
    }

    int getEncodedPatternSlot();

    long getPatternCount(Level level);


}
