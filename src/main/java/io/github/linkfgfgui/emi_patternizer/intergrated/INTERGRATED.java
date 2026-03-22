package io.github.linkfgfgui.emi_patternizer.intergrated;

import net.neoforged.fml.ModList;

public class INTERGRATED {
    public static boolean AE2;
    public static boolean RS;

    static {
        ModList list = ModList.get();
        if (list.isLoaded("ae2")) {
            AE2 = true;
        } else if (list.isLoaded("refinedstorage_emi_integration")) {
            RS = true;
        }
    }
}
