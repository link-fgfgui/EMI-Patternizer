package io.github.linkfgfgui.emi_patternizer.client;


import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.client.gui.me.patternaccess.PatternAccessTermScreen;
import appeng.client.gui.me.patternaccess.PatternContainerRecord;
import io.github.linkfgfgui.emi_patternizer.client.mixin.PatternAccessTermScreenAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static io.github.linkfgfgui.emi_patternizer.client.Patternize.EncodedItems;

public class ReloadMemory {
    static long delayBeforeRead;

    static long PatternCount = 0;

    public static void onScreenOpening(PatternAccessTermScreen<?> screen) {
        if (true) {
            EncodedItems.clear();
            Minecraft minecraft = Minecraft.getInstance();
            Level level = minecraft.level;
            delayBeforeRead = Config.DELAY_BEFORE_READ.get();
            CompletableFuture.delayedExecutor(delayBeforeRead, TimeUnit.MILLISECONDS).execute(() -> {
                minecraft.execute(() -> {
                    PatternCount = 0;
                    Collection<PatternContainerRecord> patternContainerRecordSet = ((PatternAccessTermScreenAccessor) screen).getById().values();
                    for (PatternContainerRecord entry : patternContainerRecordSet) {
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
                        minecraft.player.sendSystemMessage(Component.translatable("chat.emi_patternizer.loaded", EncodedItems.size(), PatternCount));
                    }
                });
            });
        }
    }
}
