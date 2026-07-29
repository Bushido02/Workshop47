package com.solegendary.reignofnether.blocks;

import software.bernie.geckolib.renderer.GeoItemRenderer;

// См. комментарий в FormixControlStationItemRenderer.java (активная версия) -
// то же масштабирование, та же причина.
public class FormixControlStationInactiveItemRenderer extends GeoItemRenderer<FormixControlStationInactiveBlockItem> {
    public FormixControlStationInactiveItemRenderer() {
        super(new FormixControlStationInactiveItemModel());
        this.scaleWidth = 0.2f;
        this.scaleHeight = 0.2f;
    }
}

