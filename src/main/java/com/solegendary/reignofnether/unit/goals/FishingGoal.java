package com.solegendary.reignofnether.unit.goals;

import com.solegendary.reignofnether.resources.ResourceName;
import com.solegendary.reignofnether.unit.interfaces.Unit;
import com.solegendary.reignofnether.unit.interfaces.WorkerUnit;
import com.solegendary.reignofnether.unit.packets.UnitSyncClientboundPacket;
import com.solegendary.reignofnether.util.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

// Worker стоит на кромке воды и ловит рыбу удочкой (не разрушает никакой блок -
// в отличие от GatherResourcesGoal, который построен вокруг MoveToTargetBlockGoal
// + mob.level().destroyBlock(...)). Наследуется от MoveToTargetBlockGoal только
// ради переиспользования готовой RTS-инфраструктуры движения/pathfinding к точке
// (moveTarget/setMoveTarget), tick() полностью свой, никогда не вызывает
// destroyBlock. Только Formix Worker (решение пользователя 29.07.2026) - НЕ
// добавлено в общий WorkerUnit интерфейс/GatherResourcesGoal, чтобы не задеть
// Villagers.
//
// Улов - самописная таблица шансов в Java (НЕ настоящий ваниль LootTable API,
// решение пользователя 29.07.2026: "надёжнее, в будущем можем поменять").
// Если позже понадобится точная ваниль-механика (BuiltInLootTables.FISHING) -
// см. FORMIX_FACTION_LOG.md для контекста этого решения.
public class FishingGoal extends MoveToTargetBlockGoal {

    private static final int REACH_RANGE = 5;
    // Ваниль-рыбалка ждёт 5-30 секунд (100-600 тиков) без Lure; берём тот же
    // диапазон для узнаваемости механики.
    private static final int MIN_BITE_TICKS = 100;
    private static final int MAX_BITE_TICKS = 600;
    private int ticksUntilBite = 0;
    private boolean waitingForBite = false;

    @Nullable private BlockPos fishingSpot = null;

    public FishingGoal(Mob mob) {
        super(mob, true, REACH_RANGE - 1);
    }

    @Override
    public double getMinDistToRecalculateSqr() {
        return Math.max(super.getMinDistToRecalculateSqr(), REACH_RANGE * REACH_RANGE);
    }

    // проверяет, что позиция реально соприкасается с водой (сам worker стоит на
    // суше/берегу рядом, не заходит в воду - см. hasAdjacentWater)
    private boolean isValidFishingSpot(BlockPos bp) {
        if (!mob.level().getWorldBorder().isWithinBounds(bp))
            return false;
        return hasAdjacentWater(bp);
    }

    private boolean hasAdjacentWater(BlockPos bp) {
        for (BlockPos adj : List.of(bp.north(), bp.south(), bp.east(), bp.west(), bp)) {
            BlockState bs = mob.level().getBlockState(adj);
            if (bs.getFluidState().is(FluidTags.WATER))
                return true;
        }
        return false;
    }

    public void setFishingSpot(BlockPos bp) {
        if (bp != null && isValidFishingSpot(bp)) {
            MiscUtil.addUnitCheckpoint((Unit) mob, bp, true);
            this.fishingSpot = bp;
            super.setMoveTarget(bp);
        }
    }

    @Nullable
    public BlockPos getFishingSpot() {
        return fishingSpot;
    }

    public boolean isFishing() {
        return fishingSpot != null;
    }

    // в зоне ловли (та же дистанция, что и обычный сбор ресурсов - REACH_RANGE)
    private boolean isAtFishingSpot() {
        if (fishingSpot == null)
            return false;
        return fishingSpot.distToCenterSqr(mob.getX(), mob.getEyeY(), mob.getZ()) <= REACH_RANGE * REACH_RANGE;
    }

    @Override
    public void tick() {
        if (this.mob.level().isClientSide())
            return;

        if (fishingSpot == null)
            return;

        // цель стала невалидной (воду засыпали блоками) - прекращаем рыбачить
        if (!isValidFishingSpot(fishingSpot)) {
            stopFishing();
            return;
        }

        if (!isAtFishingSpot()) {
            super.setMoveTarget(fishingSpot);
            return;
        }

        if (!waitingForBite) {
            waitingForBite = true;
            RandomSource random = mob.level().getRandom();
            ticksUntilBite = MIN_BITE_TICKS + random.nextInt(MAX_BITE_TICKS - MIN_BITE_TICKS);
            return;
        }

        ticksUntilBite -= 1;
        if (ticksUntilBite <= 0) {
            waitingForBite = false;
            catchSomething();

            // при заполнении лимита ресурсов - вернуться на склад, как обычный сбор
            Unit unit = (Unit) mob;
            if (Unit.atThresholdResources(unit) && unit.getReturnResourcesGoal() != null) {
                if (mob instanceof WorkerUnit workerUnit) {
                    unit.resetBehaviours();
                    WorkerUnit.resetBehaviours(workerUnit);
                }
                unit.getReturnResourcesGoal().returnToClosestBuilding();
            }
        }
    }

    // Простая самописная таблица улова (не настоящий ваниль LootTable API -
    // решение пользователя 29.07.2026, см. класс-комментарий выше). Проценты
    // ориентировочно соответствуют духу ваниль-рыбалки (в основном рыба,
    // изредка мусор), без точного повторения официальных весов/предметов
    // сокровищ (лук/удочка/зачарованная книга с NBT-зачарованиями сюда
    // намеренно не включены - усложнили бы без надёжного способа проверить
    // компиляцию/баланс без живого теста).
    private void catchSomething() {
        RandomSource random = mob.level().getRandom();
        float roll = random.nextFloat();

        ItemStack caught;
        if (roll < 0.60f) {
            caught = new ItemStack(Items.COD);
        } else if (roll < 0.85f) {
            caught = new ItemStack(Items.SALMON);
        } else if (roll < 0.92f) {
            caught = new ItemStack(Items.PUFFERFISH);
        } else if (roll < 0.97f) {
            caught = new ItemStack(Items.BONE);
        } else {
            caught = new ItemStack(Items.STRING);
        }

        Unit unit = (Unit) mob;
        unit.getItems().add(caught);
        UnitSyncClientboundPacket.sendSyncResourcesPacket(unit);
    }

    public void stopFishing() {
        fishingSpot = null;
        waitingForBite = false;
        ticksUntilBite = 0;
        super.stopMoving();
    }

    public ResourceName getTargetResourceName() {
        // рыба всегда считается food-ресурсом (Items.COD/SALMON уже
        // распознаются как FOOD в ResourceSources) - используется только для
        // совместимости с местами кода, ожидающими ResourceName у worker'а
        // (например переключение инструмента в руке, см. WorkerUnit.tick()).
        return isFishing() ? ResourceName.FOOD : ResourceName.NONE;
    }
}
