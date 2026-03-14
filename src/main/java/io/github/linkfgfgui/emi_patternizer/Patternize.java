package io.github.linkfgfgui.emi_patternizer;

import appeng.client.gui.me.items.PatternEncodingTermScreen;
import appeng.menu.SlotSemantics;
import appeng.menu.me.items.PatternEncodingTermMenu;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
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
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.slf4j.Logger;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;


public class Patternize {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static HashSet<String> EncodedItems = new HashSet<>();
    public static volatile boolean operating = false;
    static long delayPerOperation;
    static long delayAdditionalPerPattern;
    static boolean isPlaySound;

    public static boolean containsAllItems(EmiRecipe r) {
        return containsAllItems(r.getOutputs());
    }

    public static boolean containsAllItems(List<EmiStack> stackList) {
        return (stackList.stream().allMatch(emiStack -> EncodedItems.contains(emiStack.getId().toString())));
    }

    public static void Encode(long initDelay, Minecraft minecraft, EmiRecipe recipe, PatternEncodingTermScreen<?> screen, PatternEncodingTermMenu menu, LocalPlayer player, MultiPlayerGameMode gameMode, int encodedPatternSlot) {
        CompletableFuture.delayedExecutor(initDelay, TimeUnit.MILLISECONDS).execute(() -> {
            minecraft.execute(() -> {
                EmiRecipeFiller.performFill(recipe, screen, EmiCraftContext.Type.FILL_BUTTON, EmiCraftContext.Destination.NONE, 1);
                if (isPlaySound) {
                    minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                }
            });
            CompletableFuture.delayedExecutor(delayPerOperation, TimeUnit.MILLISECONDS).execute(() -> {
                minecraft.execute(() -> {
                    menu.encode();
                });
                CompletableFuture.delayedExecutor(delayPerOperation, TimeUnit.MILLISECONDS).execute(() ->
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

    public static Stream<MaterialNode> streamTree(MaterialNode node) {
        if (node == null) return Stream.empty();

        return Stream.concat(
                Stream.of(node),
                node.children == null ? Stream.empty() : node.children.stream().flatMap(Patternize::streamTree)
        );
    }

    public static void LoadConfig() {
        delayPerOperation = Config.DELAY_PER_OPERATION.get();
        delayAdditionalPerPattern = Config.DELAY_ADDITIONAL_PER_PATTERN.get();
        isPlaySound = Config.IS_PLAY_SOUND.get();
    }

    public static void onKeyPressed(ScreenEvent.KeyPressed.Post event) {
        if (!operating && Emi_patternizer.PATTERNIZE_MAPPING.get().isActiveAndMatches(InputConstants.getKey(event.getKeyCode(), event.getScanCode()))) {
            if (event.getScreen() instanceof PatternEncodingTermScreen<?> screen) {
                PatternEncodingTermMenu menu = screen.getMenu();
//                int blankPatternSlot = ((AEBaseMenuAccessor) menu).getSlotsBySemantic().get(SlotSemantics.BLANK_PATTERN).getFirst().index;
                int encodedPatternSlot = ((AEBaseMenuAccessor) menu).getSlotsBySemantic().get(SlotSemantics.ENCODED_PATTERN).getFirst().index;
                if (BoM.craftingMode && !operating) {
                    LoadConfig();
                    Minecraft minecraft = Minecraft.getInstance();
                    LocalPlayer player = minecraft.player;
                    MultiPlayerGameMode gameMode = minecraft.gameMode;
                    MaterialNode goal = BoM.tree.goal;
                    operating = true;
                    LOGGER.debug("operating started");
                    AtomicLong maxDelay = new AtomicLong(0);
                    Stream.of(goal)
                            .flatMap(Patternize::streamTree)
                            .sorted(Comparator.comparing(node -> {
                                if (node.recipe != null) {
                                    return node.recipe.getCategory().id.toString();
                                } else {
                                    return "null";
                                }
                            }))
                            .forEachOrdered(node -> {
                                if (node.recipe != null && node.recipe.getId() != null && !containsAllItems(node.recipe)) {
                                    Encode(maxDelay.get(), minecraft, node.recipe, screen, menu, player, gameMode, encodedPatternSlot);
                                    node.recipe.getOutputs().forEach(emiStack -> EncodedItems.add(emiStack.getId().toString()));
                                    maxDelay.addAndGet(3 * delayPerOperation + delayAdditionalPerPattern);
                                }
                            });
                    CompletableFuture.delayedExecutor(maxDelay.get() + delayPerOperation, TimeUnit.MILLISECONDS).execute(() -> {
                        operating = false;
                        BoM.craftingMode = false;
                        LOGGER.debug("operating finished");
                    });
                }
            } else {
                LOGGER.debug(event.getScreen().toString());
            }
        }
    }
}
