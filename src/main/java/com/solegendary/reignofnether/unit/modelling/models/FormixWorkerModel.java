package com.solegendary.reignofnether.unit.modelling.models;

// PLACEHOLDER model for the Formix worker unit.
// Replace the body of createBodyLayer()/the part fields below with the class Blockbench exports
// for the real worker model (Blockbench > File > Export > Export Java Entity), keeping the
// class name and constructor signature the same so FormixWorkerRenderer/FormixWorkerUnit compile
// unchanged. See WraithModel.java for what a real Blockbench export looks like.

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.solegendary.reignofnether.ReignOfNether;
import com.solegendary.reignofnether.unit.modelling.animations.FormixAnimations;
import com.solegendary.reignofnether.unit.units.formix.FormixWorkerUnit;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class FormixWorkerModel<T extends Entity> extends KeyframeHierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(ReignOfNether.MOD_ID, "formix_worker_layer"), "main");

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart left_arm;
    private final ModelPart right_arm;
    private final ModelPart left_leg;
    private final ModelPart right_leg;

    public FormixWorkerModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.head = this.body.getChild("head");
        this.left_arm = this.body.getChild("left_arm");
        this.right_arm = this.body.getChild("right_arm");
        this.left_leg = this.body.getChild("left_leg");
        this.right_leg = this.body.getChild("right_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 6.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create()
                .texOffs(32, 16).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 12.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offset(5.5F, 0.0F, 0.0F));

        PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create()
                .texOffs(40, 16).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 12.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-5.5F, 0.0F, 0.0F));

        PartDefinition left_leg = body.addOrReplaceChild("left_leg", CubeListBuilder.create()
                .texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(2.0F, 12.0F, 0.0F));

        PartDefinition right_leg = body.addOrReplaceChild("right_leg", CubeListBuilder.create()
                .texOffs(8, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-2.0F, 12.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        FormixWorkerUnit unit = (FormixWorkerUnit) entity;

        if (unit.animateScale > 0 && unit.animateScaleReducing) {
            unit.animateScale -= 0.02f;
        }
        if (unit.animateScale <= 0) {
            unit.animateScale = 1.0f;
            unit.activeAnimDef = null;
            unit.activeAnimState = null;
            unit.animateScaleReducing = false;
            unit.stopAllAnimations();
        }

        AttributeInstance ms = unit.getAttribute(Attributes.MOVEMENT_SPEED);
        if (ms == null)
            return;
        float speed = (float) ms.getValue() * 10;

        if (unit.activeAnimDef != null && unit.activeAnimState != null && unit.animateTicks > 0) {
            restartThenAnimate(unit, unit.activeAnimState, unit.activeAnimDef, ageInTicks, unit.animateScale, unit.getAnimationSpeed());
        } else if (!entity.isInWaterOrBubble() && limbSwingAmount > 0.001f) {
            restart(unit, unit.walkAnimState, ageInTicks);
            animateWalk(FormixAnimations.WALK, limbSwing, limbSwingAmount, speed, speed);
        } else {
            restartThenAnimate(unit, unit.idleAnimState, FormixAnimations.IDLE, ageInTicks);
        }

        this.head.xRot = headPitch * ((float) Math.PI / 180F);
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
