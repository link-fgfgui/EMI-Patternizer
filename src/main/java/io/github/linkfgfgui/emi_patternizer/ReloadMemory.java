package io.github.linkfgfgui.emi_patternizer;

import io.github.linkfgfgui.emi_patternizer.intergrated.Api;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.event.ScreenEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static io.github.linkfgfgui.emi_patternizer.Patternize.EncodedItems;

public class ReloadMemory {
    static long delayBeforeRead;

    static long PatternCount = 0;

    public static void onScreenOpening(ScreenEvent.Opening event) {
        if (Api.isValidAccessScreen(event.getScreen())) {
            AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) event.getScreen();
            Api api = Api.getApi(screen);
            EncodedItems.clear();
            Minecraft minecraft = Minecraft.getInstance();
            Level level = minecraft.level;
            delayBeforeRead = Config.DELAY_BEFORE_READ.get();
            CompletableFuture.delayedExecutor(delayBeforeRead, TimeUnit.MILLISECONDS).execute(() -> {
                minecraft.execute(() -> {
                    PatternCount = api.getPatternCount(level);
                    if (minecraft.player != null) {
                        minecraft.player.sendSystemMessage(Component.translatable("chat.emi_patternizer.loaded", EncodedItems.size(), PatternCount));
                    }
                });
            });
        }
    }
}
