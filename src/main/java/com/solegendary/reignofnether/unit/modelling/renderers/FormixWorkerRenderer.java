package com.solegendary.reignofnether.unit.modelling.renderers;

import com.solegendary.reignofnether.ReignOfNether;
import com.solegendary.reignofnether.unit.modelling.models.FormixWorkerModel;
import com.solegendary.reignofnether.unit.units.formix.FormixWorkerUnit;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

// PLACEHOLDER renderer/texture path. Once the real Blockbench texture PNG is added at
// src/main/resources/assets/reignofnether/textures/entities/formix_worker_unit.png this class
// needs no further changes.
@OnlyIn(Dist.CLIENT)
public class FormixWorkerRenderer extends MobRenderer<FormixWorkerUnit, FormixWorkerModel<FormixWorkerUnit>> {

    private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "textures/entities/formix_worker_unit.png");

    public FormixWorkerRenderer(EntityRendererProvider.Context context) {
        super(context, new FormixWorkerModel<>(context.bakeLayer(FormixWorkerModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull FormixWorkerUnit entity) {
        return TEXTURE_LOCATION;
    }
}
