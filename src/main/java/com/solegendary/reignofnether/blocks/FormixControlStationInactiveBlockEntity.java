package com.solegendary.reignofnether.blocks;

import com.solegendary.reignofnether.registrars.BlockEntityRegistrar;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

// Неактивная модель (control_station_diactive.bbmodel) не содержит анимаций
// в исходном .bbmodel - registerControllers() намеренно пустой (GeckoLib
// требует implements GeoBlockEntity даже без анимаций, чтобы модель вообще
// рендерилась через GeoBlockRenderer).
public class FormixControlStationInactiveBlockEntity extends BlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public FormixControlStationInactiveBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistrar.FORMIX_CONTROL_STATION_INACTIVE_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // нет анимаций для этой модели - контроллер не регистрируется
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    // См. комментарий в FormixControlStationBlockEntity.java (активная версия) -
    // тот же фикс frustum culling, та же геометрия габаритов.
    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(this.getBlockPos()).inflate(5);
    }
}