package io.github.linkfgfgui.emi_patternizer;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.client.gui.me.patternaccess.PatternAccessTermScreen;
import appeng.client.gui.me.patternaccess.PatternContainerRecord;
import io.github.linkfgfgui.emi_patternizer.mixin.PatternAccessTermScreenAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.event.ScreenEvent;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static io.github.linkfgfgui.emi_patternizer.Patternize.EncodedItems;

public class ReloadMemory {
    static long delayBeforeRead;

    static long PatternCount = 0;

    public static void onScreenOpening(ScreenEvent.Opening event) {
        if (event.getScreen() instanceof PatternAccessTermScreen<?> screen) {
            EncodedItems.clear();
            Minecraft minecraft = Minecraft.getInstance();
            Level level = minecraft.level;
            delayBeforeRead=Config.DELAY_BEFORE_READ.get();
            CompletableFuture.delayedExecutor(delayBeforeRead, TimeUnit.MILLISECONDS).execute(() -> {
                minecraft.execute(() -> {
                    PatternCount = 0;
                    Collection<PatternContainerRecord> patternContainerRecordSet = ((PatternAccessTermScreenAccessor) screen).getById().values();
                    for (PatternContainerRecord entry : patternContainerRecordSet) {
                        entry.getInventory().toItemContainerContents().stream().forEach((item) -> {
                            IPatternDetails details = PatternDetailsHelper.decodePattern(item, level);
                            PatternCount++;
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
                    if (minecraft.player != null) {
                        minecraft.player.sendSystemMessage(Component.translatable("chat.emi_patternizer.loaded", EncodedItems.size(), PatternCount));
                    }
                });
            });
        }
    }
}
