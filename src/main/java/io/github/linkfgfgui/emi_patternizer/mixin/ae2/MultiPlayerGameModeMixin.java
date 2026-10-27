package io.github.linkfgfgui.emi_patternizer.mixin.ae2;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.core.network.serverbound.MEInteractionPacket;
import appeng.helpers.InventoryAction;
import io.github.linkfgfgui.emi_patternizer.Patternize;
import io.github.linkfgfgui.emi_patternizer.intergrated.PatternAccessPreloader;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Client-side hooks on {@link MultiPlayerGameMode}:
 * <ul>
 *     <li>delays the interaction that would open a pattern encoding terminal until a nearby
 *     pattern access terminal has been opened and closed once;</li>
 *     <li>redirects shift-clicks on encoded patterns in the player inventory to the ME network
 *     while the patternizer is running, so AE2's quick-move cannot fill the encoded pattern slot.</li>
 * </ul>
 * Lives in the {@code ae2} package so {@code MixinPlugin} only applies it when AE2 is present,
 * which is required by {@link PatternAccessPreloader} and the AE2 API used by the redirect.
 */
@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void emi_patternizer$preloadPatternAccess(LocalPlayer player, InteractionHand hand, BlockHitResult hit,
                                                       CallbackInfoReturnable<InteractionResult> cir) {
        if (PatternAccessPreloader.onUseItemOn(player, hand, hit)) {
            cir.setReturnValue(InteractionResult.CONSUME);
        }
    }

    @Inject(method = "useItem", at = @At("HEAD"), cancellable = true)
    private void emi_patternizer$preloadPatternAccessItem(Player player, InteractionHand hand,
                                                          CallbackInfoReturnable<InteractionResult> cir) {
        if (PatternAccessPreloader.onUseItem(player, hand)) {
            cir.setReturnValue(InteractionResult.CONSUME);
        }
    }

    /**
     * While batch encoding is running, shift-clicking encoded patterns in the player inventory
     * would normally go through AE2's PatternEncodingTermMenu#transferStackToMenu, which tries
     * the encoded pattern slot before the ME network. Redirect the click so the pattern is
     * inserted into the ME network directly, leaving the encoded pattern slot untouched.
     */
    @Inject(method = "handleInventoryMouseClick", at = @At("HEAD"), cancellable = true)
    private void emi_patternizer$redirectEncodedPatternQuickMove(
            int containerId, int slotId, int mouseButton, ClickType clickType,
            Player player, CallbackInfo ci) {
        if (!Patternize.operating) return;
        if (clickType != ClickType.QUICK_MOVE || mouseButton != 0) return;

        AbstractContainerMenu menu = player.containerMenu;
        if (menu == null || menu.containerId != containerId) return;
        if (slotId < 0 || slotId >= menu.slots.size()) return;

        Slot slot = menu.slots.get(slotId);
        if (slot.container != player.getInventory()) return;

        ItemStack stack = slot.getItem();
        if (stack.isEmpty() || !PatternDetailsHelper.isEncodedPattern(stack)) return;

        // Cancel the vanilla QUICK_MOVE; the server would try the encoded pattern slot first.
        ci.cancel();

        MultiPlayerGameMode gameMode = (MultiPlayerGameMode) (Object) this;

        // 1. Pick up the stack onto the cursor.
        gameMode.handleInventoryMouseClick(containerId, slotId, 0, ClickType.PICKUP, player);

        // 2. Insert the carried stack directly into the ME network. serial -1 means
        //    "no clicked network key", which makes MEStorageMenu#handleNetworkInteraction
        //    call putCarriedItemIntoNetwork(false).
        PacketDistributor.sendToServer(
                new MEInteractionPacket(containerId, -1L, InventoryAction.PICKUP_OR_SET_DOWN));

        // 3. Put any remainder (network full/offline) back into the source slot.
        gameMode.handleInventoryMouseClick(containerId, slotId, 0, ClickType.PICKUP, player);
    }
}
