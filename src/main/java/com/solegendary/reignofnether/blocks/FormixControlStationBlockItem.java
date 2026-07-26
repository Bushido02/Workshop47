package com.solegendary.reignofnether.blocks;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

// BlockItem-эквивалент для FORMIX_CONTROL_STATION_BLOCK, показывающий
// полноценную 3D GeckoLib-модель в руке/инвентаре (не плоскую 2D-иконку,
// как у большинства блоков проекта, включая GarrisonEntryBlock). Решение
// пользователя 26.07.2026 - "нетипично для проекта, но полноценная модель".
//
// Регистрируется вручную в BlockRegistrar вместо авто-генерируемого
// BlockItem (registerBlock(name, block, tab) создаёт обычный BlockItem,
// без GeckoLib-рендера в руке - для терминала он заменён на этот класс).
//
// Требует parent: "builtin/entity" в models/item/formix_control_station_block.json
// (см. официальную GeckoLib4 wiki "Geckolib Items") - НЕ block/cross,
// как у обычных предметов блоков в проекте.
public class FormixControlStationBlockItem extends BlockItem implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public FormixControlStationBlockItem(Properties properties) {
        super(com.solegendary.reignofnether.registrars.BlockRegistrar.FORMIX_CONTROL_STATION_BLOCK.get(), properties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer = null;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new FormixControlStationItemRenderer();
                }
                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // статичный вид в руке - модель не анимируется вне мира (та же
        // idle-анимация проигрывается только у реально размещённого блока
        // через FormixControlStationBlockEntity). Пустой контроллер здесь
        // достаточен, т.к. GeoItemRenderer всё равно требует, чтобы Item
        // implements GeoItem, даже без активных анимаций.
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
