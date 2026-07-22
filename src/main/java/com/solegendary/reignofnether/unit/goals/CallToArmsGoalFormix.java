package com.solegendary.reignofnether.unit.goals;

import com.solegendary.reignofnether.building.BuildingPlacement;
import com.solegendary.reignofnether.building.BuildingServerEvents;
import com.solegendary.reignofnether.building.BuildingUtils;
import com.solegendary.reignofnether.building.buildings.formix.FormixHive;
import com.solegendary.reignofnether.unit.Relationship;
import com.solegendary.reignofnether.unit.interfaces.Unit;
import com.solegendary.reignofnether.unit.units.formix.FormixWorkerUnit;
import com.solegendary.reignofnether.util.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;

import javax.annotation.Nullable;

// Move a Formix worker towards the faction's main building (FormixHive) to become a warrior.
// Mirrors CallToArmsGoal (Villagers) but targets FormixHive/FormixWorkerUnit instead.
public class CallToArmsGoalFormix extends MoveToTargetBlockGoal {

    private BuildingPlacement buildingTarget;

    public CallToArmsGoalFormix(Mob mob) {
        super(mob, true, 0);
    }

    public void tick() {
        if (buildingTarget == null)
            return;
        calcMoveTarget();
        if (this.mob.tickCount % 20 == 0)
            start();

        if (isInRange() && buildingTarget != null && !this.mob.level().isClientSide())
            if (this.mob instanceof FormixWorkerUnit workerUnit)
                workerUnit.convertToWarrior();
    }

    private void calcMoveTarget() {
        if (this.buildingTarget != null)
            this.moveTarget = this.buildingTarget.getClosestGroundPos(mob.getOnPos(), 1);
    }

    public boolean isInRange() {
        if (buildingTarget != null && this.moveTarget != null)
            if (BuildingServerEvents.getUnitToBuildingRelationship((Unit) this.mob, buildingTarget) == Relationship.OWNED)
                return MiscUtil.isMobInRangeOfPos(moveTarget, mob, 2);
        return false;
    }

    public void setNearestFormixHiveAsTarget() {
        BuildingPlacement building = BuildingUtils.findClosestBuilding(mob.level().isClientSide(), this.mob.getEyePosition(),
                (b) -> b.isBuilt && b.ownerName.equals(((Unit) mob).getOwnerName()) && b.getBuilding() instanceof FormixHive);
        if (building != null && building.getBuilding() instanceof FormixHive)
            setBuildingTarget(building);
    }

    private void setBuildingTarget(@Nullable BuildingPlacement target) {
        if (target != null) {
            MiscUtil.addUnitCheckpoint((Unit) mob, new BlockPos(
                    target.centrePos.getX(),
                    target.originPos.getY() + 1,
                    target.centrePos.getZ()),
                    true
            );
        }
        this.buildingTarget = target;
        calcMoveTarget();
        this.start();
    }

    public BuildingPlacement getBuildingTarget() { return buildingTarget; }

    @Override
    protected boolean useRtsPathfinding() {
        return false;
    }

    @Override
    public void stop() {
        buildingTarget = null;
        super.stop();
    }
}
