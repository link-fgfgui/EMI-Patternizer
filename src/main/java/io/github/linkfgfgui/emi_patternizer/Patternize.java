package io.github.linkfgfgui.emi_patternizer;

import appeng.client.gui.me.items.PatternEncodingTermScreen;
import appeng.client.gui.me.patternaccess.PatternAccessTermScreen;
import appeng.menu.SlotSemantics;
import appeng.menu.me.items.PatternEncodingTermMenu;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.bom.BoM;
import dev.emi.emi.bom.MaterialNode;
import dev.emi.emi.registry.EmiRecipeFiller;
import io.github.linkfgfgui.emi_patternizer.mixin.AEBaseMenuAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.inventory.ClickType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


public class Patternize {
    static long per = 60;
    public static HashSet<String> EncodedItems = new HashSet<>();
    public static volatile boolean operating = false;
    private static final Logger LOGGER = LogUtils.getLogger();

    public static boolean containsAllItems(EmiRecipe r) {
        return containsAllItems(r.getOutputs());
    }

    public static boolean containsAllItems(List<EmiStack> stackList) {
        return (stackList.stream().allMatch(emiStack -> EncodedItems.contains(emiStack.getId().toString())));
    }

    public static void Encode(long initDelay, Minecraft minecraft, EmiRecipe recipe, PatternEncodingTermScreen<?> screen, PatternEncodingTermMenu menu, LocalPlayer player, MultiPlayerGameMode gameMode, int encodedPatternSlot) {
        CompletableFuture.delayedExecutor(initDelay, TimeUnit.MILLISECONDS).execute(() -> {
            minecraft.execute(() -> {
//                if (containsAllItems(recipe)) {
//                    return;
//                }
                EmiRecipeFiller.performFill(recipe, screen, EmiCraftContext.Type.FILL_BUTTON, EmiCraftContext.Destination.NONE, 1);
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            });
            CompletableFuture.delayedExecutor(per, TimeUnit.MILLISECONDS).execute(() -> {
                minecraft.execute(() -> {
                    if (containsAllItems(recipe)) {
                        return;
                    }
                    menu.encode();
                    recipe.getOutputs().forEach(emiStack -> EncodedItems.add(emiStack.getId().toString()));
                    LOGGER.debug("Operating: {}, EncodedItems: {}", operating, EncodedItems);
                });
                CompletableFuture.delayedExecutor(per, TimeUnit.MILLISECONDS).execute(() ->
                        minecraft.execute(() ->
                                gameMode.handleInventoryMouseClick(
                                        menu.containerId,
                                        encodedPatternSlot,
                                        0, // 0 = Left, 1 = Right
                                        ClickType.QUICK_MOVE,
                                        player
                                )
                        ));
            });
        });
    }

    public static long CreateTasks(long delay, Minecraft minecraft, @Nullable List<MaterialNode> nodes, PatternEncodingTermScreen<?> screen, PatternEncodingTermMenu menu, LocalPlayer player, MultiPlayerGameMode gameMode, int encodedPatternSlot) {
        if (nodes == null) {
            return delay;
        }
        for (MaterialNode node : nodes) {
            if (node.recipe != null && node.recipe.getId() != null && !containsAllItems(node.recipe)) {
                Encode(delay, minecraft, node.recipe, screen, menu, player, gameMode, encodedPatternSlot);
                delay += (3 * per + 20);
            }
            if (node.children != null) {
                delay = CreateTasks(delay, minecraft, node.children, screen, menu, player, gameMode, encodedPatternSlot);
            }
        }
        return delay;
    }

    public static void onKeyPressed(ScreenEvent.KeyPressed.Post event) {
        if (!operating && Emi_patternizer.PATTERNIZE_MAPPING.get().isActiveAndMatches(InputConstants.getKey(event.getKeyCode(), event.getScanCode()))) {
            if (event.getScreen() instanceof PatternEncodingTermScreen<?> screen) {
                LOGGER.debug("Catched");
                PatternEncodingTermMenu menu = screen.getMenu();
                int blankPatternSlot = ((AEBaseMenuAccessor) menu).getSlotsBySemantic().get(SlotSemantics.BLANK_PATTERN).getFirst().index;
                int encodedPatternSlot = ((AEBaseMenuAccessor) menu).getSlotsBySemantic().get(SlotSemantics.ENCODED_PATTERN).getFirst().index;
                if (BoM.craftingMode) {
                    LOGGER.debug("BoM crafting");
                    Minecraft minecraft = Minecraft.getInstance();
                    LocalPlayer player = minecraft.player;
                    MultiPlayerGameMode gameMode = minecraft.gameMode;


                    MaterialNode goal = BoM.tree.goal;
                    operating = true;
                    LOGGER.debug(String.valueOf(operating));
                    long maxDelay = CreateTasks(0, minecraft, List.of(goal), screen, menu, player, gameMode, encodedPatternSlot);
                    CompletableFuture.delayedExecutor(maxDelay + per, TimeUnit.MILLISECONDS).execute(() -> {
                        operating = false;
                        BoM.craftingMode=false;
                        LOGGER.debug(String.valueOf(operating));
                    });
                }
            } else {
                LOGGER.debug(event.getScreen().toString());
            }
        }
    }
}
