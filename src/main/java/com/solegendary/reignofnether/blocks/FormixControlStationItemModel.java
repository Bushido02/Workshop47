package com.solegendary.reignofnether.blocks;

import com.solegendary.reignofnether.ReignOfNether;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

// Та же геометрия/текстура/анимация, что у FormixControlStationModel
// (блочная версия) - отдельный класс требуется, т.к. GeoModel<T> типизирован
// по конкретному animatable (тут FormixControlStationBlockItem, не
// FormixControlStationBlockEntity).
public class FormixControlStationItemModel extends GeoModel<FormixControlStationBlockItem> {

    private static final ResourceLocation MODEL =
            ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "geo/formix_control_station.geo.json");
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "textures/block/formix_control_station.png");
    private static final ResourceLocation ANIMATIONS =
            ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "animations/formix_control_station.animation.json");

    @Override
    public ResourceLocation getModelResource(FormixControlStationBlockItem animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(FormixControlStationBlockItem animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(FormixControlStationBlockItem animatable) {
        return ANIMATIONS;
    }
}
