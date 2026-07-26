package com.solegendary.reignofnether.blocks;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class FormixControlStationInactiveBlockRenderer extends GeoBlockRenderer<FormixControlStationInactiveBlockEntity> {
    public FormixControlStationInactiveBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new FormixControlStationInactiveModel());
    }
}
