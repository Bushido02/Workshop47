package com.solegendary.reignofnether.blocks;

import com.solegendary.reignofnether.ReignOfNether;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FormixControlStationInactiveItemModel extends GeoModel<FormixControlStationInactiveBlockItem> {

    private static final ResourceLocation MODEL =
            ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "geo/formix_control_station_diactive.geo.json");
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "textures/block/formix_control_station_diactive.png");
    private static final ResourceLocation ANIMATIONS =
            ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "animations/formix_control_station.animation.json");

    @Override
    public ResourceLocation getModelResource(FormixControlStationInactiveBlockItem animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(FormixControlStationInactiveBlockItem animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(FormixControlStationInactiveBlockItem animatable) {
        return ANIMATIONS;
    }
}
