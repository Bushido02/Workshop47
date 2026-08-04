package com.solegendary.reignofnether.blocks;

import com.solegendary.reignofnether.registrars.BlockEntityRegistrar;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class FormixControlStationBlockEntity extends BlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public FormixControlStationBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistrar.FORMIX_CONTROL_STATION_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "idle_controller", 0,
                state -> state.setAndContinue(RawAnimation.begin().thenLoop("idle"))));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    // ИСПРАВЛЕНИЕ (см. FORMIX_FACTION_LOG.md — "видна только 1 часть модели"):
    // модель физически занимает несколько блоков в каждую сторону от блока
    // размещения, но Minecraft по умолчанию считает render bounding box
    // BlockEntity равным области самого блока 1x1x1 (collision shape) —
    // см. IForgeBlockEntity.getRenderBoundingBox() javadoc. Из-за этого view
    // frustum culling обрезает бОльшую часть модели при многих углах камеры —
    // видна только та часть, что физически ближе всего к позиции блока.
    // Расширяем bounding box с запасом (5 блоков), чтобы вся модель считалась
    // видимой, пока в кадре виден хотя бы сам блок размещения.
    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(this.getBlockPos()).inflate(5);
    }
}