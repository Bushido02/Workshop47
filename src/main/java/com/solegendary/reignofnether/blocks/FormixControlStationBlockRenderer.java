package com.solegendary.reignofnether.blocks;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class FormixControlStationBlockRenderer extends GeoBlockRenderer<FormixControlStationBlockEntity> {
    public FormixControlStationBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new FormixControlStationModel());
    }
}
