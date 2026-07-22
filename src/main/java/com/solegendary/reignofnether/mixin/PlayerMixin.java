package com.solegendary.reignofnether.mixin;

import com.solegendary.reignofnether.orthoview.OrthoviewClientEvents;
import com.solegendary.reignofnether.player.PlayerServerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(Entity.class)
public abstract class PlayerMixin {

    @Shadow public boolean noPhysics;
    @Shadow public abstract int getId();
    @Shadow public Level level;

    // noclip + flight for orthoview players regardless of game mode (Creative/Survival/Adventure).
    // Needed because entering orthoview teleports the player up to orthoviewPlayerBaseY; without
    // this, non-Creative players would immediately start falling/taking fall damage once gravity
    // applies. tick() naturally reverses all of this so no explicit reversal is needed when
    // leaving orthoview - the ability flags are only forced on while the player is in the
    // orthoview list/has it locally enabled.
    //
    // NOTE: this block was previously commented out and restricted to Creative-only players by
    // the original mod author, reason unknown (possibly an unfinished feature, possibly a bug
    // found in Survival/Adventure). Re-enabled and extended to all game modes on 2026-07-21 per
    // user request - see PROJECT_NOTES/FORMIX_FACTION_LOG.md section on RTS-in-Survival for
    // context. If this causes issues (players stuck flying after leaving orthoview, clipping
    // through terrain unexpectedly, etc), the safest revert is to re-add the player.isCreative()
    // check removed below.
    @Inject(
            method = "tick()V",
            at = @At("HEAD")
    )
    private void tick(CallbackInfo ci) {
        int id = this.getId();
        Entity entity = this.level.getEntity(id);
        List<Integer> orthoIds = new ArrayList<>();
        for (ServerPlayer orthoviewPlayer : PlayerServerEvents.orthoviewPlayers) {
            Integer serverPlayerId = orthoviewPlayer.getId();
            orthoIds.add(serverPlayerId);
        }
        if (entity instanceof Player player &&
            (orthoIds.contains(id) || (this.level.isClientSide() && OrthoviewClientEvents.isEnabled()))) {
            this.noPhysics = true;
            if (!player.getAbilities().flying) {
                player.getAbilities().flying = true;
                player.onUpdateAbilities();
            }
        }
    }
}
