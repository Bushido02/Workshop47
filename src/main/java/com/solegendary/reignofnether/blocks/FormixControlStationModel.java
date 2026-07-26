package com.solegendary.reignofnether.blocks;

import com.solegendary.reignofnether.ReignOfNether;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FormixControlStationModel extends GeoModel<FormixControlStationBlockEntity> {

    private static final ResourceLocation MODEL =
            ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "geo/formix_control_station.geo.json");
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "textures/block/formix_control_station.png");
    private static final ResourceLocation ANIMATIONS =
            ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "animations/formix_control_station.animation.json");

    @Override
    public ResourceLocation getModelResource(FormixControlStationBlockEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(FormixControlStationBlockEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(FormixControlStationBlockEntity animatable) {
        return ANIMATIONS;
    }
}
