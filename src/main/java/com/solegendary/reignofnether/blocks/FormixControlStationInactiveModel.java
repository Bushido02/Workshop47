package com.solegendary.reignofnether.blocks;

import com.solegendary.reignofnether.ReignOfNether;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FormixControlStationInactiveModel extends GeoModel<FormixControlStationInactiveBlockEntity> {

    private static final ResourceLocation MODEL =
            ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "geo/formix_control_station_diactive.geo.json");
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "textures/block/formix_control_station_diactive.png");
    // нет анимаций у неактивной модели - файл не создавался (см. FORMIX_FACTION_LOG.md).
    // GeoModel.getAnimationResource() требует не-null ResourceLocation в API GeckoLib4,
    // поэтому указываем на тот же animation.json, что и активная модель, но
    // FormixControlStationInactiveBlockEntity.registerControllers() не регистрирует
    // ни одного AnimationController - имена анимаций оттуда никогда не запрашиваются.
    private static final ResourceLocation ANIMATIONS =
            ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "animations/formix_control_station.animation.json");

    @Override
    public ResourceLocation getModelResource(FormixControlStationInactiveBlockEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(FormixControlStationInactiveBlockEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(FormixControlStationInactiveBlockEntity animatable) {
        return ANIMATIONS;
    }
}
