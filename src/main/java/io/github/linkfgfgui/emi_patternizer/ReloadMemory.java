package io.github.linkfgfgui.emi_patternizer;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.storage.IPatternAccessTermMenuHost;
import appeng.client.gui.me.patternaccess.PatternAccessTermScreen;
import appeng.client.gui.me.patternaccess.PatternContainerRecord;
import appeng.menu.implementations.PatternAccessTermMenu;
import com.mojang.logging.LogUtils;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.registry.EmiRecipeFiller;
import io.github.linkfgfgui.emi_patternizer.mixin.PatternAccessTermScreenAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.event.ScreenEvent;

import static io.github.linkfgfgui.emi_patternizer.Patternize.EncodedItems;

import org.slf4j.Logger;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ReloadMemory {
    static long PatternCount = 0;
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void onScreenOpening(ScreenEvent.Opening event) {
        if (event.getScreen() instanceof PatternAccessTermScreen<?> screen) {
            EncodedItems.clear();
            Minecraft minecraft = Minecraft.getInstance();
            Level level = minecraft.level;
            CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS).execute(() -> {
                minecraft.execute(() -> {
                    PatternCount = 0;
                    Collection<PatternContainerRecord> patternContainerRecordSet = ((PatternAccessTermScreenAccessor) screen).getById().values();
                    LOGGER.debug(patternContainerRecordSet.toString());
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
