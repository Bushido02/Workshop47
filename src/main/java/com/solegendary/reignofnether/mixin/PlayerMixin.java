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
    // applies.
    //
    // FIX (26.07.2026): the comment below used to claim "tick() naturally reverses all of this
    // so no explicit reversal is needed when leaving orthoview" - this was wrong. Confirmed live
    // in-game: players got stuck floating/unable to descend (stuck in a flying state) after
    // leaving orthoview in Survival/Adventure, only fixed by re-entering and exiting orthoview
    // again. Root cause: this injection only ever sets noPhysics/flying = true, it never sets
    // them back to false once the player is no longer in orthoview. Added an explicit else
    // branch below that resets both for non-Creative players. Creative players are left alone -
    // flying is their normal expected ability there.
    //
    // NOTE: this block was previously commented out and restricted to Creative-only players by
    // the original mod author, reason unknown (possibly an unfinished feature, possibly this
    // exact bug found in Survival/Adventure and never diagnosed). Re-enabled and extended to all
    // game modes on 2026-07-21 per user request - see PROJECT_NOTES/FORMIX_FACTION_LOG.md for
    // context.
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
        if (entity instanceof Player player) {
            boolean inOrthoview = orthoIds.contains(id) ||
                    (this.level.isClientSide() && OrthoviewClientEvents.isEnabled());
            if (inOrthoview) {
                this.noPhysics = true;
                if (!player.getAbilities().flying) {
                    player.getAbilities().flying = true;
                    player.onUpdateAbilities();
                }
            } else if (this.noPhysics && !player.isCreative()) {
                // player just left orthoview (or never should have had these forced) and isn't
                // in Creative - undo what this mixin forced on, since nothing else in the
                // codebase does. See FIX note above.
                this.noPhysics = false;
                if (player.getAbilities().flying) {
                    player.getAbilities().flying = false;
                    player.onUpdateAbilities();
                }
            }
        }
    }
}