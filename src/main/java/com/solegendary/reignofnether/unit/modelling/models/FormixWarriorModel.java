package com.solegendary.reignofnether.unit.modelling.models;

// Formix Warrior model — geometry taken verbatim from the OFFICIAL Blockbench "Export Java
// Entity" output ("Риг_by_MaksPet.java", Blockbench 5.1.6, source model "Ополченец_1"),
// not hand-converted from the raw .bbmodel JSON. Bone names were renamed from Cyrillic to
// English identifiers for project consistency (formixRoot/bodyRotation/head/etc — see the
// original file for the Cyrillic->English mapping if cross-referencing with Blockbench),
// but every coordinate, rotation, and PartPose value is unchanged from the export.
//
// This replaces an earlier hand-written conversion attempt that had a real bug: it dropped
// the top-level Blockbench group ("Формикс"/formixRoot) as an "unnecessary" wrapper. The
// official export shows that assumption was wrong — formixRoot IS a real bone that must
// stay in the hierarchy and IS what render()/root() should point to, with bodyRotation one
// level below it, not collapsed into it. See FORMIX_FACTION_LOG.md for the session this was
// found in.

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.solegendary.reignofnether.ReignOfNether;
import com.solegendary.reignofnether.unit.modelling.animations.FormixWarriorAnimations;
import com.solegendary.reignofnether.unit.units.formix.FormixWarriorUnit;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class FormixWarriorModel<T extends Entity> extends KeyframeHierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(ReignOfNether.MOD_ID, "formix_warrior_layer"), "main");

    private final ModelPart formixRoot;
    private final ModelPart bodyRotation;
    private final ModelPart head;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public FormixWarriorModel(ModelPart root) {
        this.formixRoot = root.getChild("formixRoot");
        this.bodyRotation = this.formixRoot.getChild("bodyRotation");
        ModelPart bodyUpper = this.bodyRotation.getChild("bodyUpper");
        this.head = bodyUpper.getChild("head");
        ModelPart arms = bodyUpper.getChild("arms").getChild("slim");
        this.rightArm = arms.getChild("rightArm");
        this.leftArm = arms.getChild("leftArm");
        ModelPart legs = this.bodyRotation.getChild("bodyLower").getChild("legs");
        this.rightLeg = legs.getChild("rightLeg");
        this.leftLeg = legs.getChild("leftLeg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition formixRoot = partdefinition.addOrReplaceChild("formixRoot", CubeListBuilder.create(), PartPose.offset(0.0F, 8.0F, 0.0F));

		PartDefinition bodyRotation = formixRoot.addOrReplaceChild("bodyRotation", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition bodyUpper = bodyRotation.addOrReplaceChild("bodyUpper", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 0.0F));

		PartDefinition head = bodyUpper.addOrReplaceChild("head", CubeListBuilder.create().texOffs(24, 15).addBox(-3.1703F, -3.9936F, -2.6673F, 6.0915F, 6.0915F, 6.0915F, new CubeDeformation(0.0F)), PartPose.offset(0.6246F, -10.1741F, -0.8785F));

		PartDefinition spikes = head.addOrReplaceChild("spikes", CubeListBuilder.create(), PartPose.offset(-0.1246F, -0.4403F, 0.3785F));

		PartDefinition spikes_r1 = spikes.addOrReplaceChild("spikes_r1", CubeListBuilder.create().texOffs(30, 10).addBox(-5.0762F, -0.7107F, 0.0F, 8.6296F, 1.0152F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0305F, -0.2031F, 0.0F, 0.0052F, 0.7418F, 1.5308F));

		PartDefinition spikes_r2 = spikes.addOrReplaceChild("spikes_r2", CubeListBuilder.create().texOffs(30, 13).addBox(-4.061F, -0.8122F, 0.0F, 7.1068F, 1.0152F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5229F, -1.0152F, 0.5076F, -0.0892F, 0.7852F, 1.5204F));

		PartDefinition spikes_r3 = spikes.addOrReplaceChild("spikes_r3", CubeListBuilder.create().texOffs(30, 14).addBox(-3.5534F, -0.5076F, 0.0F, 6.5991F, 1.0153F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.5229F, 1.0152F, -0.0516F, 0.7851F, 1.5075F));

		PartDefinition spikes_r4 = spikes.addOrReplaceChild("spikes_r4", CubeListBuilder.create().texOffs(20, 36).addBox(0.5076F, -5.0763F, 1.5229F, 0.0F, 8.122F, 1.0153F, new CubeDeformation(0.0F))
		.texOffs(18, 36).addBox(0.5076F, -5.0763F, -2.5381F, 0.0F, 8.122F, 1.0152F, new CubeDeformation(0.0F))
		.texOffs(16, 36).addBox(0.5076F, -5.5839F, -0.5076F, 0.0F, 8.6296F, 1.0152F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5076F, -0.5076F, 0.0F, 0.0F, 0.0F, 0.7418F));

		PartDefinition spikes_r5 = spikes.addOrReplaceChild("spikes_r5", CubeListBuilder.create().texOffs(16, 57).addBox(0.5076F, -4.5686F, 1.5229F, 0.0F, 7.6144F, 1.0153F, new CubeDeformation(0.0F))
		.texOffs(14, 57).addBox(0.5076F, -4.5686F, -2.5381F, 0.0F, 7.6144F, 1.0152F, new CubeDeformation(0.0F))
		.texOffs(22, 36).addBox(0.5076F, -5.0763F, -0.5076F, 0.0F, 8.122F, 1.0152F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5076F, -0.5076F, 0.0F, 0.0F, 0.0F, -0.829F));

		PartDefinition browRidge_r1 = spikes.addOrReplaceChild("browRidge_r1", CubeListBuilder.create().texOffs(30, 9).addBox(-5.0762F, -0.7107F, 0.0F, 8.6296F, 1.0152F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0305F, -0.2031F, 0.0F, 0.0592F, -0.7409F, 1.5308F));

		PartDefinition spikes_r6 = spikes.addOrReplaceChild("spikes_r6", CubeListBuilder.create().texOffs(30, 11).addBox(-4.5686F, -0.5076F, 0.0F, 7.6144F, 1.0153F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.5229F, -0.5076F, 0.0385F, -0.9146F, 1.5016F));

		PartDefinition spikes6_r1 = spikes.addOrReplaceChild("spikes6_r1", CubeListBuilder.create().texOffs(30, 12).addBox(-4.5686F, -0.8122F, 0.0F, 7.6144F, 1.0153F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5229F, -0.5076F, -0.5076F, -0.0236F, -0.6972F, 1.5243F));

		PartDefinition spikes_r7 = spikes.addOrReplaceChild("spikes_r7", CubeListBuilder.create().texOffs(54, 41).addBox(-5.5839F, -2.7412F, 0.0F, 11.1678F, 1.0152F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(54, 44).addBox(-5.0762F, -4.3656F, 0.0F, 10.1525F, 1.0152F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(54, 43).addBox(-5.0762F, -1.1168F, 0.0F, 10.1525F, 1.0153F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.8274F, 0.0F, 0.0F, -2.3562F, 0.0F));

		PartDefinition spikes_r8 = spikes.addOrReplaceChild("spikes_r8", CubeListBuilder.create().texOffs(54, 42).addBox(-5.0762F, -2.8427F, 0.0F, 10.1525F, 1.0153F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(54, 40).addBox(-5.5839F, -4.4671F, 0.0F, 11.1678F, 1.0152F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(54, 45).addBox(-5.0762F, -6.0915F, 0.0F, 10.1525F, 1.0152F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.5534F, 0.0F, 0.0F, -0.7854F, 0.0F));

		PartDefinition eyes = head.addOrReplaceChild("eyes", CubeListBuilder.create(), PartPose.offset(-0.6246F, 25.1741F, 0.8785F));

		PartDefinition eyeRight = eyes.addOrReplaceChild("eyeRight", CubeListBuilder.create().texOffs(18, 57).addBox(-1.2437F, -1.1422F, -0.0102F, 2.5381F, 2.5381F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(48, 33).addBox(-0.7361F, -0.6345F, -0.0203F, 1.5229F, 1.5229F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(2.1498F, -27.0103F, -3.5559F));

		PartDefinition eyeLeft = eyes.addOrReplaceChild("eyeLeft", CubeListBuilder.create().texOffs(48, 30).addBox(-1.2691F, -1.2691F, 0.0051F, 2.5381F, 2.5381F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(44, 13).addBox(-0.7614F, -0.7614F, -0.0051F, 1.5229F, 1.5229F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.1752F, -26.8834F, -3.5711F));

		PartDefinition torsoUpper = bodyUpper.addOrReplaceChild("torsoUpper", CubeListBuilder.create().texOffs(0, 15).addBox(-3.5F, -9.0F, -2.0F, 8.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition arms = bodyUpper.addOrReplaceChild("arms", CubeListBuilder.create(), PartPose.offset(0.0F, -8.0F, 0.0F));

		PartDefinition slim = arms.addOrReplaceChild("slim", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition rightArm = slim.addOrReplaceChild("rightArm", CubeListBuilder.create(), PartPose.offset(9.0F, 0.0F, 0.0F));

		PartDefinition rightArmUpper = rightArm.addOrReplaceChild("rightArmUpper", CubeListBuilder.create().texOffs(16, 46).addBox(-4.5F, -1.0F, -2.0F, 3.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition rightArmLower = rightArm.addOrReplaceChild("rightArmLower", CubeListBuilder.create().texOffs(30, 56).addBox(-4.5F, 0.0F, -2.0F, 3.0F, 6.0F, 4.0F, new CubeDeformation(-0.01F)), PartPose.offset(0.0F, 5.0F, 0.0F));

		PartDefinition layer2_r1 = rightArmLower.addOrReplaceChild("layer2_r1", CubeListBuilder.create().texOffs(0, 57).addBox(-1.5F, 0.1F, -2.0F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.15F)), PartPose.offsetAndRotation(-3.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3927F));

		PartDefinition leftArm = slim.addOrReplaceChild("leftArm", CubeListBuilder.create(), PartPose.offset(-14.0F, 0.0F, 0.0F));

		PartDefinition leftArmUpper = leftArm.addOrReplaceChild("leftArmUpper", CubeListBuilder.create().texOffs(40, 35).addBox(7.5F, -1.0F, -2.0F, 3.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition leftArmLower = leftArm.addOrReplaceChild("leftArmLower", CubeListBuilder.create().texOffs(54, 30).addBox(7.5F, 0.0F, -2.0F, 3.0F, 6.0F, 4.0F, new CubeDeformation(-0.01F)), PartPose.offset(0.0F, 5.0F, 0.0F));

		PartDefinition layer2_r2 = leftArmLower.addOrReplaceChild("layer2_r2", CubeListBuilder.create().texOffs(44, 56).addBox(-1.5F, 0.1F, -2.0F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.15F)), PartPose.offsetAndRotation(9.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3927F));

		PartDefinition weapon = leftArmLower.addOrReplaceChild("weapon", CubeListBuilder.create().texOffs(30, 0).addBox(-5.0F, -14.5F, -2.2F, 2.0F, 2.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-5.5F, -15.0F, -14.2F, 3.0F, 3.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(13.0F, 18.0F, 0.0F));

		PartDefinition bodyLower = bodyRotation.addOrReplaceChild("bodyLower", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition torsoLower = bodyLower.addOrReplaceChild("torsoLower", CubeListBuilder.create().texOffs(0, 28).addBox(-3.5F, 0.0F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(-0.01F))
		.texOffs(24, 27).addBox(-3.5F, 1.0F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.24F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition legs = bodyLower.addOrReplaceChild("legs", CubeListBuilder.create(), PartPose.offset(0.0F, 6.0F, 0.0F));

		PartDefinition rightLeg = legs.addOrReplaceChild("rightLeg", CubeListBuilder.create(), PartPose.offset(2.0F, -2.0F, 0.0F));

		PartDefinition rightLegUpper = rightLeg.addOrReplaceChild("rightLegUpper", CubeListBuilder.create().texOffs(0, 36).addBox(-1.0F, 0.0F, -2.0F, 3.5F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition layer2_r3 = rightLegUpper.addOrReplaceChild("layer2_r3", CubeListBuilder.create().texOffs(46, 46).addBox(-1.0F, 0.7F, -2.0F, 3.5F, 6.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0F, 0.0F, -0.2618F));

		PartDefinition rightLegLower = rightLeg.addOrReplaceChild("rightLegLower", CubeListBuilder.create().texOffs(48, 0).addBox(-1.0F, 0.0F, -2.0F, 3.5F, 6.0F, 4.0F, new CubeDeformation(-0.01F))
		.texOffs(0, 47).addBox(-1.0F, 0.0F, -2.0F, 3.5F, 6.0F, 4.0F, new CubeDeformation(0.24F)), PartPose.offset(0.0F, 6.0F, 0.0F));

		PartDefinition leftLeg = legs.addOrReplaceChild("leftLeg", CubeListBuilder.create(), PartPose.offset(-8.0F, -2.0F, 0.0F));

		PartDefinition leftLegUpper = leftLeg.addOrReplaceChild("leftLegUpper", CubeListBuilder.create().texOffs(24, 35).addBox(4.5F, 0.0F, -2.0F, 3.5F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition layer2_r4 = leftLegUpper.addOrReplaceChild("layer2_r4", CubeListBuilder.create().texOffs(30, 46).addBox(-2.4199F, 0.7545F, -2.0F, 3.5F, 6.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(7.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.2618F));

		PartDefinition leftLegLower = leftLeg.addOrReplaceChild("leftLegLower", CubeListBuilder.create().texOffs(48, 20).addBox(4.5F, 0.0F, -2.0F, 3.5F, 6.0F, 4.0F, new CubeDeformation(-0.01F)), PartPose.offset(0.0F, 6.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public ModelPart root() {
        return this.formixRoot;
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        FormixWarriorUnit unit = (FormixWarriorUnit) entity;

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
            animateWalk(FormixWarriorAnimations.WALK, limbSwing, limbSwingAmount, speed, speed);
        } else {
            restartThenAnimate(unit, unit.idleAnimState, FormixWarriorAnimations.IDLE, ageInTicks);
        }

        this.head.xRot = headPitch * ((float) Math.PI / 180F);
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        formixRoot.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
