package io.github.linkfgfgui.emi_patternizer.client;


import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.client.gui.me.interfaceterminal.InterfaceRecord;
import appeng.client.gui.me.interfaceterminal.InterfaceTerminalScreen;
import io.github.linkfgfgui.emi_patternizer.client.mixin.InterfaceTerminalScreenAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static io.github.linkfgfgui.emi_patternizer.client.Patternize.EncodedItems;

public class ReloadMemory {
    static long delayBeforeRead;

    static long PatternCount = 0;

    public static void onScreenOpening(InterfaceTerminalScreen<?> screen) {
        if (true) {
            EncodedItems.clear();
            Minecraft minecraft = Minecraft.getInstance();
            Level level = minecraft.level;
            delayBeforeRead = Config.DELAY_BEFORE_READ.get();
            CompletableFuture.delayedExecutor(delayBeforeRead, TimeUnit.MILLISECONDS).execute(() -> {
                minecraft.execute(() -> {
                    PatternCount = 0;
                    Collection<InterfaceRecord> patternContainerRecordSet = ((InterfaceTerminalScreenAccessor) screen).getById().values();
                    for (InterfaceRecord entry : patternContainerRecordSet) {
                        entry.getInventory().forEach((item) -> {
                            IPatternDetails details = PatternDetailsHelper.decodePattern(item, level);
                            PatternCount++;
                            if (details == null) {
                            } else {
                                Arrays.stream(details.getOutputs()).forEach(
                                        genericStack -> {
                                            EncodedItems.add(genericStack.what().getId().toString());
                                        }
                                );
                            }
                        });
                    }
                    if (minecraft.player != null) {
                        minecraft.player.displayClientMessage(new TranslatableComponent("chat.emi_patternizer.loaded", EncodedItems.size(), PatternCount), false);
                    }
                });
            });
        }
    }
}
