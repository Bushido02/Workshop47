package com.solegendary.reignofnether.blocks;

import com.solegendary.reignofnether.ReignOfNether;
import com.solegendary.reignofnether.playerprogression.FormixPlayerProgressionServerEvents;
import com.solegendary.reignofnether.registrars.BlockRegistrar;
import com.solegendary.reignofnether.registrars.PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

// Sent client -> server when the player presses "Unlock (10 Eidos)" on
// FormixControlStationUnlockScreen (opened from FormixControlStationInactiveBlock.use()).
// Server re-validates everything authoritatively (position still holds the inactive block,
// player still has enough experience) rather than trusting the client - the client only shows
// the button as enabled/disabled as a UX hint, this packet is the actual gate.
//
// Cost: EIDOS_UNLOCK_COST Eidos, where "Eidos" is real vanilla player XP (see
// FormixPlayerProgression.java / FORMIX_FACTION_LOG.md 22.07.2026 - Eidos is a relabelled
// player.totalExperience, not a separate currency field), spent via
// player.giveExperiencePoints(-cost), same primitive CommandsServerEvents already uses
// elsewhere in this project (rtsapi-change-eidos-like command) to move totalExperience to an
// exact value.
public class FormixControlStationServerboundPacket {

    // Cost to unlock an inactive Control Station terminal. Kept here (not in a config) since
    // this is a fixed one-time gameplay cost, not something expected to need per-session tuning
    // like unit/building resource costs.
    public static final int EIDOS_UNLOCK_COST = 10;

    private final BlockPos pos;

    public static void requestUnlock(BlockPos pos) {
        PacketHandler.INSTANCE.sendToServer(new FormixControlStationServerboundPacket(pos));
    }

    public FormixControlStationServerboundPacket(BlockPos pos) {
        this.pos = pos;
    }

    public FormixControlStationServerboundPacket(FriendlyByteBuf buffer) {
        this.pos = buffer.readBlockPos();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(this.pos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        final var success = new AtomicBoolean(false);
        ctx.get().enqueueWork(() -> {

            ServerPlayer player = ctx.get().getSender();
            if (player == null) {
                ReignOfNether.LOGGER.warn("FormixControlStationServerboundPacket: Sender was null");
                success.set(false);
                return;
            }

            ServerLevel level = player.serverLevel();
            BlockState state = level.getBlockState(pos);

            // Re-check the block is still the inactive terminal - guards against the block
            // having been broken/replaced/already-unlocked between the screen opening and the
            // button being pressed (e.g. two players clicking the same terminal, or lag).
            if (!state.is(BlockRegistrar.FORMIX_CONTROL_STATION_INACTIVE_BLOCK.get())) {
                success.set(true);
                return;
            }

            // player.totalExperience is the authoritative Eidos value read/written elsewhere in
            // this project (see HudClientEvents "Эйдос: " + MC.player.totalExperience and
            // CommandsServerEvents' existing giveExperiencePoints(amount - totalExperience)
            // usage) - re-check server-side, never trust a client-computed "can afford" flag.
            if (player.totalExperience < EIDOS_UNLOCK_COST) {
                success.set(true);
                return;
            }

            player.giveExperiencePoints(-EIDOS_UNLOCK_COST);
            level.setBlock(pos, BlockRegistrar.FORMIX_CONTROL_STATION_BLOCK.get().defaultBlockState(), 3);

            success.set(true);
        });
        ctx.get().setPacketHandled(true);
        return success.get();
    }
}
