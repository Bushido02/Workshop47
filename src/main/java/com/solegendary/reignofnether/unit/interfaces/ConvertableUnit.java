package com.solegendary.reignofnether.unit.interfaces;

import com.solegendary.reignofnether.unit.packets.UnitSyncClientboundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface ConvertableUnit {

    public boolean shouldDiscard();
    public void setShouldDiscard(boolean shouldDiscard);

    // returns the new unit's id
    public default LivingEntity convertToUnit(EntityType<? extends Unit> entityType) {
        Unit oldUnit = (Unit) this;
        LivingEntity oldEntity = (LivingEntity) this;
        if (oldEntity.level().isClientSide())
            return null;

        ServerLevel level = (ServerLevel) oldEntity.level();
        LivingEntity newEntity = (LivingEntity) entityType.create(level);

        if (newEntity == null)
            return null;

        float maxHealthDiff = newEntity.getMaxHealth() - oldEntity.getMaxHealth();

        newEntity.setHealth(Math.max(1, oldEntity.getHealth() + maxHealthDiff));
        for (MobEffectInstance effect : oldEntity.getActiveEffects())
            newEntity.addEffect(effect);

        newEntity.copyPosition(oldEntity);
        ((Unit) newEntity).setOwnerName(oldUnit.getOwnerName());
        level.addFreshEntity(newEntity);

        for (ItemStack item : oldUnit.getItems())
            ((Unit) newEntity).getItems().add(item);

        UnitSyncClientboundPacket.sendSyncResourcesPacket((Unit) newEntity);

        Entity vehicle = oldEntity.getVehicle();
        if (vehicle != null) {
            oldEntity.stopRiding();
            newEntity.startRiding(vehicle, true);
        }
        if (oldEntity.isVehicle()) {
            Entity passenger = oldEntity.getFirstPassenger();
            if (passenger != null) {
                passenger.stopRiding();
                passenger.startRiding(newEntity, true);
            }
        }
        newEntity.setYRot(oldEntity.getYRot());

        // Discard the old entity immediately on the server, instead of waiting for the client
        // round-trip (UnitClientboundPacket DISCARD). The old approach left a window of a few ticks
        // where both the old and new entities existed simultaneously, which made population counters
        // flicker (e.g. 3 -> 6 -> 3) during worker<->warrior conversion. `this` is already statically
        // known to be a ConvertableUnit here (we're inside its own default method), so no instanceof
        // check/cast is needed - just call setShouldDiscard directly.
        this.setShouldDiscard(true);
        oldEntity.discard();

        return newEntity;
    }
}