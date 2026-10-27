package io.github.linkfgfgui.emi_patternizer.intergrated;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;
import appeng.api.parts.SelectedPart;
import appeng.api.storage.IPatternAccessTermMenuHost;
import appeng.helpers.IPatternTerminalMenuHost;
import com.mojang.logging.LogUtils;
import io.github.linkfgfgui.emi_patternizer.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.slf4j.Logger;

/**
 * Opens and closes a nearby pattern access terminal before a pattern encoding terminal is opened,
 * so {@code ReloadMemory} can load the encoded pattern list without the player having to open the
 * access terminal manually.
 * <p>
 * The original interaction is cancelled and replayed after the access terminal has been closed.
 * The access terminal must be within the player's block interaction range to be openable.
 */
public final class PatternAccessPreloader {
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Wireless pattern encoding terminals, which open from {@code useItem} instead of a block part.
     */
    private static final String[] ENCODING_TERMINAL_ITEMS = {
            "de.mari_023.ae2wtlib.wet.ItemWET",
            "com.lhy.wcwt.item.WirelessComprehensiveWorkTerminalItem"
    };

    /**
     * How long the access terminal stays open. The pattern list is only accepted by the client
     * while that screen is current ({@code PatternAccessTerminalPacket#handleOnClient}), and the
     * server sends it on the first tick after the menu opens, so a few ticks are enough.
     */
    private static final int HOLD_TICKS = 10;

    /**
     * Give up waiting for the access screen and replay the original interaction.
     */
    private static final int OPEN_TIMEOUT_TICKS = 40;

    private enum Phase {
        IDLE,
        WAITING_FOR_SCREEN,
        SCREEN_OPEN
    }

    private static Phase phase = Phase.IDLE;
    private static LocalPlayer target;
    private static Runnable pendingReplay;
    private static int ticks;

    /**
     * Set while the original interaction is being replayed, so it is not intercepted again.
     */
    private static boolean replaying;

    private PatternAccessPreloader() {
    }

    /**
     * @return true if the interaction was taken over and must not be sent yet
     */
    public static boolean onUseItemOn(LocalPlayer player, InteractionHand hand, BlockHitResult hit) {
        if (!Config.PRELOAD_PATTERN_ACCESS.getAsBoolean() || replaying) return false;
        if (!isEncodingTerminalPart(player.level(), hit)) return false;
        return phase != Phase.IDLE || start(player, () -> Minecraft.getInstance().gameMode.useItemOn(player, hand, hit));
    }

    /**
     * @return true if the interaction was taken over and must not be sent yet
     */
    public static boolean onUseItem(Player player, InteractionHand hand) {
        if (!Config.PRELOAD_PATTERN_ACCESS.getAsBoolean() || replaying) return false;
        if (!(player instanceof LocalPlayer localPlayer)) return false;
        if (!holdsEncodingTerminal(player, hand)) return false;
        return phase != Phase.IDLE || start(localPlayer, () -> Minecraft.getInstance().gameMode.useItem(player, hand));
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        if (phase == Phase.IDLE) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != target || minecraft.level == null) {
            reset();
            return;
        }

        Screen screen = minecraft.screen;
        if (phase == Phase.WAITING_FOR_SCREEN) {
            if (screen != null) {
                if (!Api.isValidAccessScreen(screen)) {
                    // The player opened something else instead, leave them alone.
                    reset();
                } else {
                    phase = Phase.SCREEN_OPEN;
                    ticks = 0;
                }
            } else if (++ticks > OPEN_TIMEOUT_TICKS) {
                // The access terminal never opened (out of range, protected, ...), continue anyway.
                finish(false);
            }
            return;
        }

        if (screen == null) {
            // The player closed the access terminal, continue with the original interaction.
            finish(false);
        } else if (!Api.isValidAccessScreen(screen)) {
            reset();
        } else if (++ticks >= HOLD_TICKS) {
            finish(true);
        }
    }

    private static boolean start(LocalPlayer player, Runnable replay) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen != null || minecraft.gameMode == null) return false;

        BlockHitResult accessHit = findAccessTerminal(player);
        if (accessHit == null) {
            LOGGER.debug("No openable pattern access terminal near {}", player.blockPosition());
            return false;
        }
        LOGGER.debug("Opening pattern access terminal at {} before the pattern encoding terminal", accessHit.getBlockPos());

        target = player;
        pendingReplay = replay;
        phase = Phase.WAITING_FOR_SCREEN;
        ticks = 0;
        try {
            // Must be the main hand: the vanilla fallback from useItemOn to useWithoutItem (which is
            // what opens AE2 terminal parts) only happens for InteractionHand.MAIN_HAND.
            InteractionResult result = minecraft.gameMode.useItemOn(player, InteractionHand.MAIN_HAND, accessHit);
            if (!result.consumesAction()) {
                // The click missed the part, so no screen will ever open. Do not stall the player.
                LOGGER.debug("Pattern access terminal at {} did not accept the click", accessHit.getBlockPos());
                reset();
                return false;
            }
        } catch (RuntimeException e) {
            LOGGER.warn("Failed to open the nearby pattern access terminal", e);
            reset();
            return false;
        }
        return true;
    }

    private static void finish(boolean closeScreen) {
        LocalPlayer player = target;
        Runnable replay = pendingReplay;
        reset();
        Minecraft minecraft = Minecraft.getInstance();
        if (player == null || minecraft.player != player || minecraft.level == null) return;

        if (closeScreen) {
            Screen screen = minecraft.screen;
            if (screen != null) {
                if (!Api.isValidAccessScreen(screen)) return;
                player.closeContainer();
            }
        }

        if (replay == null) return;
        replaying = true;
        try {
            replay.run();
        } finally {
            replaying = false;
        }
    }

    private static void reset() {
        phase = Phase.IDLE;
        target = null;
        pendingReplay = null;
        ticks = 0;
    }

    private static boolean isEncodingTerminalPart(Level level, BlockHitResult hit) {
        BlockEntity blockEntity = level.getBlockEntity(hit.getBlockPos());
        if (!(blockEntity instanceof IPartHost partHost)) return false;
        BlockPos pos = hit.getBlockPos();
        Vec3 local = hit.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
        SelectedPart selected = partHost.selectPartLocal(local);
        IPart part = selected != null ? selected.part : null;
        LOGGER.debug("Clicked part at {}: {}", pos, part == null ? "none" : part.getClass().getName());
        return part instanceof IPatternTerminalMenuHost;
    }

    private static boolean holdsEncodingTerminal(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.isEmpty()) return false;
        for (String className : ENCODING_TERMINAL_ITEMS) {
            if (Api.isInstanceOf(stack.getItem(), className)) return true;
        }
        return false;
    }

    private static BlockHitResult findAccessTerminal(LocalPlayer player) {
        Level level = player.level();
        Vec3 eye = player.getEyePosition();
        // Same bound the server uses to accept the interaction.
        double range = player.blockInteractionRange() + 1.0;
        double rangeSq = range * range;
        int radius = (int) Math.ceil(range);
        BlockPos origin = player.blockPosition();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        BlockHitResult best = null;
        double bestDistSq = Double.MAX_VALUE;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    cursor.set(origin.getX() + dx, origin.getY() + dy, origin.getZ() + dz);
                    if (!level.isLoaded(cursor)) continue;
                    double distSq = eye.distanceToSqr(Vec3.atCenterOf(cursor));
                    if (distSq > rangeSq || distSq >= bestDistSq) continue;
                    if (!(level.getBlockEntity(cursor) instanceof IPartHost partHost)) continue;
                    BlockHitResult hit = findAccessHit(partHost, cursor.immutable());
                    if (hit != null) {
                        best = hit;
                        bestDistSq = distSq;
                    }
                }
            }
        }
        return best;
    }

    private static BlockHitResult findAccessHit(IPartHost partHost, BlockPos pos) {
        for (Direction side : Direction.values()) {
            if (isAccessTerminal(partHost.getPart(side))) return hitAt(pos, side);
        }
        if (isAccessTerminal(partHost.getPart(null))) return hitAt(pos, null);
        return null;
    }

    private static boolean isAccessTerminal(IPart part) {
        return part instanceof IPatternAccessTermMenuHost;
    }

    /**
     * A point inside the part's collision box, which is what {@code selectPartLocal} uses.
     */
    private static BlockHitResult hitAt(BlockPos pos, Direction side) {
        Vec3 center = Vec3.atCenterOf(pos);
        if (side == null) return new BlockHitResult(center, Direction.UP, pos, false);
        Vec3 hit = center.add(side.getStepX() * 0.45, side.getStepY() * 0.45, side.getStepZ() * 0.45);
        return new BlockHitResult(hit, side, pos, false);
    }
}
