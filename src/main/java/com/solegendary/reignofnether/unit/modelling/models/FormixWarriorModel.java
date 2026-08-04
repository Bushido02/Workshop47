package com.solegendary.reignofnether.unit.modelling.models;

// Formix Warrior model — converted from Blockbench source "Ополченец_1 - Converted.bbmodel"
// (box_uv geometry, 42 cubes / 27 named bones + per-cube-rotation synthetic child bones,
// static geometry only — no baked animation channels were usable in the source file,
// see FORMIX_FACTION_LOG.md for details on this session).
// Texture resolution is 128x128 (stretched up from a 64x64 placeholder PNG per user request —
// replace with a proper 128x128 texture later for full detail, current one is upscaled/blurry
// on close inspection).
//
// IMPORTANT for any future manual .bbmodel -> Java conversion in this project:
// Blockbench cube 'from'/'to' fields are the UNROTATED axis-aligned box; 'rotation' + 'origin'
// on the cube itself describe a rotation applied on top, pivoting around 'origin'. The real
// Blockbench "Export Java Entity" feature handles this by wrapping each individually-rotated
// cube in its own synthetic child PartDefinition with PartPose.offsetAndRotation() — that is
// what this file does too (see e.g. the "spikes1".."spikes8" bones under "spikes", or
// "layer2"/"layer2_1" etc under the arm/leg armor plates). Cubes with rotation=[0,0,0] are
// still just added directly via .addBox() on their owning bone's CubeListBuilder, no wrapper
// needed. Verified against the source file with an independent rotation-matrix simulation
// (rotate cube corners around cube origin, compare visually) before writing this out — see the
// chat session where this was generated for the verification script if this ever needs redoing.

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

    private final ModelPart root;
    private final ModelPart bodyRotation;
    private final ModelPart head;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public FormixWarriorModel(ModelPart root) {
        this.root = root;
        // NOTE: context.bakeLayer() returns the synthetic Minecraft mesh root, not the
        // Blockbench top-level group ("Формикс") — bodyRotation ("Teло") is a direct child
        // of that synthetic root in createBodyLayer() below (the old intermediate "root"
        // group from Blockbench was dropped to keep this a 1:1 match).
        this.bodyRotation = root.getChild("bodyRotation");
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

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(),
						PartPose.offset(0.0F, 16.0F, 0.0F));

		PartDefinition bodyRotation = root.addOrReplaceChild("bodyRotation", CubeListBuilder.create(),
						PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition bodyUpper = bodyRotation.addOrReplaceChild("bodyUpper", CubeListBuilder.create(),
						PartPose.offset(0.0F, -1.0F, 0.0F));

		PartDefinition head = bodyUpper.addOrReplaceChild("head", CubeListBuilder.create()
						.texOffs(24, 15).addBox(-2.92116F, -2.09787F, -2.66729F, 6.0915F, 6.0915F, 6.0915F, new CubeDeformation(0.0F)),
						PartPose.offset(-0.62459F, 10.17412F, -0.87846F));

		PartDefinition spikes = head.addOrReplaceChild("spikes", CubeListBuilder.create(),
						PartPose.offset(0.12459F, 0.44026F, 0.37846F));

		PartDefinition spikes1 = spikes.addOrReplaceChild("spikes1", CubeListBuilder.create().texOffs(54, 45).addBox(-5.07625F, 1.52287F, 0.0F, 10.1525F, 1.01525F, 0.0F, new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

		PartDefinition spikes2 = spikes.addOrReplaceChild("spikes2", CubeListBuilder.create().texOffs(54, 40).addBox(-5.58388F, 1.72592F, 0.0F, 11.16776F, 1.01525F, 0.0F, new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.0F, -1.82745F, 0.0F, 0.0F, 0.7854F, 0.0F));

		PartDefinition spikes3 = spikes.addOrReplaceChild("spikes3", CubeListBuilder.create().texOffs(54, 42).addBox(-5.07625F, 1.82745F, 0.0F, 10.1525F, 1.01525F, 0.0F, new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.0F, -3.55338F, 0.0F, 0.0F, 0.7854F, 0.0F));

		PartDefinition spikes4 = spikes.addOrReplaceChild("spikes4", CubeListBuilder.create().texOffs(54, 43).addBox(-5.07625F, 1.82745F, 0.0F, 10.1525F, 1.01525F, 0.0F, new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.0F, -3.55338F, 0.0F, 0.0F, 2.35619F, 0.0F));

		PartDefinition spikes5 = spikes.addOrReplaceChild("spikes5", CubeListBuilder.create().texOffs(54, 44).addBox(-5.07625F, 1.52287F, 0.0F, 10.1525F, 1.01525F, 0.0F, new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 2.35619F, 0.0F));

		PartDefinition spikes6 = spikes.addOrReplaceChild("spikes6", CubeListBuilder.create().texOffs(54, 41).addBox(-5.58388F, 1.72592F, 0.0F, 11.16776F, 1.01525F, 0.0F, new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.0F, -1.82745F, 0.0F, 0.0F, 2.35619F, 0.0F));

		PartDefinition spikes7 = spikes.addOrReplaceChild("spikes7", CubeListBuilder.create().texOffs(30, 12).addBox(-3.04576F, -0.20305F, 0.0F, 7.61438F, 1.01525F, 0.0F, new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(-1.52287F, 0.50762F, -0.50762F, 0.02363F, 0.69721F, 1.52429F));

		PartDefinition spikes8 = spikes.addOrReplaceChild("spikes8", CubeListBuilder.create().texOffs(30, 11).addBox(-3.04575F, -0.50763F, 0.0F, 7.61438F, 1.01526F, 0.0F, new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.0F, 1.52287F, -0.50762F, -0.03846F, 0.91464F, 1.50161F));

		PartDefinition browRidge = spikes.addOrReplaceChild("browRidge", CubeListBuilder.create().texOffs(30, 9).addBox(-3.55337F, -0.30458F, 0.0F, 8.62962F, 1.01525F, 0.0F, new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(2.0305F, 0.20305F, 0.0F, -0.05915F, 0.74089F, 1.53085F));

		PartDefinition mandibleTop = spikes.addOrReplaceChild("mandibleTop", CubeListBuilder.create().texOffs(22, 36).addBox(-0.50762F, -3.04575F, -0.50762F, 0.0F, 8.122F, 1.01524F, new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.50762F, 0.50762F, 0.0F, 0.0F, 0.0F, -0.82903F));

		PartDefinition mandibleSpike = spikes.addOrReplaceChild("mandibleSpike", CubeListBuilder.create().texOffs(14, 57).addBox(-0.50762F, -3.04575F, -2.53812F, 0.0F, 7.61437F, 1.01525F, new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.50762F, 0.50762F, 0.0F, 0.0F, 0.0F, -0.82903F));

		PartDefinition spikes9 = spikes.addOrReplaceChild("spikes9", CubeListBuilder.create().texOffs(16, 57).addBox(-0.50762F, -3.04575F, 1.52288F, 0.0F, 7.61437F, 1.01525F, new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.50762F, 0.50762F, 0.0F, 0.0F, 0.0F, -0.82903F));

		PartDefinition spikes10 = spikes.addOrReplaceChild("spikes10", CubeListBuilder.create().texOffs(16, 36).addBox(-0.50762F, -3.04575F, -0.50762F, 0.0F, 8.62963F, 1.01524F, new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.50762F, 0.50762F, 0.0F, 0.0F, 0.0F, 0.74176F));

		PartDefinition spikes11 = spikes.addOrReplaceChild("spikes11", CubeListBuilder.create().texOffs(18, 36).addBox(-0.50762F, -3.04575F, -2.53812F, 0.0F, 8.122F, 1.01525F, new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.50762F, 0.50762F, 0.0F, 0.0F, 0.0F, 0.74176F));

		PartDefinition spikes12 = spikes.addOrReplaceChild("spikes12", CubeListBuilder.create().texOffs(20, 36).addBox(-0.50762F, -3.04575F, 1.52288F, 0.0F, 8.122F, 1.01525F, new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.50762F, 0.50762F, 0.0F, 0.0F, 0.0F, 0.74176F));

		PartDefinition spikes13 = spikes.addOrReplaceChild("spikes13", CubeListBuilder.create().texOffs(30, 14).addBox(-3.04575F, -0.50763F, 0.0F, 6.59913F, 1.01526F, 0.0F, new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(0.0F, 1.52287F, 1.01525F, 0.05162F, -0.78509F, 1.50753F));

		PartDefinition spikes14 = spikes.addOrReplaceChild("spikes14", CubeListBuilder.create().texOffs(30, 13).addBox(-3.04576F, -0.20305F, 0.0F, 7.10676F, 1.01525F, 0.0F, new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(-1.52287F, 1.01524F, 0.50762F, 0.08915F, -0.78516F, 1.52039F));

		PartDefinition spikes15 = spikes.addOrReplaceChild("spikes15", CubeListBuilder.create().texOffs(30, 10).addBox(-3.55337F, -0.30458F, 0.0F, 8.62962F, 1.01525F, 0.0F, new CubeDeformation(0.0F)),
						PartPose.offsetAndRotation(2.0305F, 0.20305F, 0.0F, -0.00516F, -0.74184F, 1.53081F));

		PartDefinition eyes = head.addOrReplaceChild("eyes", CubeListBuilder.create(),
						PartPose.offset(0.62459F, -25.17412F, 0.87846F));

		PartDefinition eyeRight = eyes.addOrReplaceChild("eyeRight", CubeListBuilder.create()
						.texOffs(18, 57).addBox(-1.29445F, -1.39596F, -0.01015F, 2.53813F, 2.53812F, 0.0F, new CubeDeformation(0.0F))
						.texOffs(48, 33).addBox(-0.78682F, -0.88834F, -0.02031F, 1.52288F, 1.52288F, 0.0F, new CubeDeformation(0.0F)),
						PartPose.offset(-2.14978F, 27.01034F, -3.5559F));

		PartDefinition eyeLeft = eyes.addOrReplaceChild("eyeLeft", CubeListBuilder.create()
						.texOffs(48, 30).addBox(-1.26906F, -1.26906F, 0.00508F, 2.53813F, 2.53812F, 0.0F, new CubeDeformation(0.0F))
						.texOffs(44, 13).addBox(-0.76144F, -0.76144F, -0.00508F, 1.52288F, 1.52288F, 0.0F, new CubeDeformation(0.0F)),
						PartPose.offset(1.17516F, 26.88344F, -3.57113F));

		PartDefinition torsoUpper = bodyUpper.addOrReplaceChild("torsoUpper", CubeListBuilder.create()
						.texOffs(0, 15).addBox(-4.5F, 0.0F, -2.0F, 8.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)),
						PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition arms = bodyUpper.addOrReplaceChild("arms", CubeListBuilder.create(),
						PartPose.offset(0.0F, 8.0F, 0.0F));

		PartDefinition slim = arms.addOrReplaceChild("slim", CubeListBuilder.create(),
						PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition rightArm = slim.addOrReplaceChild("rightArm", CubeListBuilder.create(),
						PartPose.offset(-9.0F, 0.0F, 0.0F));

		PartDefinition rightArmUpper = rightArm.addOrReplaceChild("rightArmUpper", CubeListBuilder.create()
						.texOffs(16, 46).addBox(1.5F, -6.0F, -2.0F, 3.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)),
						PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition rightArmLower = rightArm.addOrReplaceChild("rightArmLower", CubeListBuilder.create()
						.texOffs(30, 56).addBox(1.5F, -6.0F, -2.0F, 3.0F, 6.0F, 4.0F, new CubeDeformation(-0.01F)),
						PartPose.offset(0.0F, -5.0F, 0.0F));

		PartDefinition layer2 = rightArmLower.addOrReplaceChild("layer2", CubeListBuilder.create().texOffs(0, 57).addBox(-1.5F, -3.1F, -2.0F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.15F)),
						PartPose.offsetAndRotation(3.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3927F));

		PartDefinition leftArm = slim.addOrReplaceChild("leftArm", CubeListBuilder.create(),
						PartPose.offset(14.0F, 0.0F, 0.0F));

		PartDefinition leftArmUpper = leftArm.addOrReplaceChild("leftArmUpper", CubeListBuilder.create()
						.texOffs(40, 35).addBox(-10.5F, -6.0F, -2.0F, 3.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)),
						PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition leftArmLower = leftArm.addOrReplaceChild("leftArmLower", CubeListBuilder.create()
						.texOffs(54, 30).addBox(-10.5F, -6.0F, -2.0F, 3.0F, 6.0F, 4.0F, new CubeDeformation(-0.01F)),
						PartPose.offset(0.0F, -5.0F, 0.0F));

		PartDefinition layer21 = leftArmLower.addOrReplaceChild("layer21", CubeListBuilder.create().texOffs(44, 56).addBox(-1.5F, -3.1F, -2.0F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.15F)),
						PartPose.offsetAndRotation(-9.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3927F));

		PartDefinition weapon = leftArmLower.addOrReplaceChild("weapon", CubeListBuilder.create()
						.texOffs(30, 0).addBox(3.0F, 12.5F, -2.2F, 2.0F, 2.0F, 7.0F, new CubeDeformation(0.0F))
						.texOffs(0, 0).addBox(2.5F, 12.0F, -14.2F, 3.0F, 3.0F, 12.0F, new CubeDeformation(0.0F)),
						PartPose.offset(-13.0F, -18.0F, 0.0F));

		PartDefinition bodyLower = bodyRotation.addOrReplaceChild("bodyLower", CubeListBuilder.create(),
						PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition torsoLower = bodyLower.addOrReplaceChild("torsoLower", CubeListBuilder.create()
						.texOffs(0, 28).addBox(-4.5F, -4.0F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(-0.01F))
						.texOffs(24, 27).addBox(-4.5F, -5.0F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.24F)),
						PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition legs = bodyLower.addOrReplaceChild("legs", CubeListBuilder.create(),
						PartPose.offset(0.0F, -6.0F, 0.0F));

		PartDefinition rightLeg = legs.addOrReplaceChild("rightLeg", CubeListBuilder.create(),
						PartPose.offset(-2.0F, 2.0F, 0.0F));

		PartDefinition rightLegUpper = rightLeg.addOrReplaceChild("rightLegUpper", CubeListBuilder.create()
						.texOffs(0, 36).addBox(-2.5F, -7.0F, -2.0F, 3.5F, 7.0F, 4.0F, new CubeDeformation(0.0F)),
						PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition layer22 = rightLegUpper.addOrReplaceChild("layer22", CubeListBuilder.create().texOffs(46, 46).addBox(-2.5F, -6.7F, -2.0F, 3.5F, 6.0F, 4.0F, new CubeDeformation(0.1F)),
						PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.0F, -0.2618F));

		PartDefinition rightLegLower = rightLeg.addOrReplaceChild("rightLegLower", CubeListBuilder.create()
						.texOffs(48, 0).addBox(-2.5F, -6.0F, -2.0F, 3.5F, 6.0F, 4.0F, new CubeDeformation(-0.01F))
						.texOffs(0, 47).addBox(-2.5F, -6.0F, -2.0F, 3.5F, 6.0F, 4.0F, new CubeDeformation(0.24F)),
						PartPose.offset(0.0F, -6.0F, 0.0F));

		PartDefinition leftLeg = legs.addOrReplaceChild("leftLeg", CubeListBuilder.create(),
						PartPose.offset(8.0F, 2.0F, 0.0F));

		PartDefinition leftLegUpper = leftLeg.addOrReplaceChild("leftLegUpper", CubeListBuilder.create()
						.texOffs(24, 35).addBox(-8.0F, -7.0F, -2.0F, 3.5F, 7.0F, 4.0F, new CubeDeformation(0.0F)),
						PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition layer23 = leftLegUpper.addOrReplaceChild("layer23", CubeListBuilder.create().texOffs(30, 46).addBox(-1.08013F, -6.75445F, -2.0F, 3.5F, 6.0F, 4.0F, new CubeDeformation(0.1F)),
						PartPose.offsetAndRotation(-7.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.2618F));

		PartDefinition leftLegLower = leftLeg.addOrReplaceChild("leftLegLower", CubeListBuilder.create()
						.texOffs(48, 20).addBox(-8.0F, -6.0F, -2.0F, 3.5F, 6.0F, 4.0F, new CubeDeformation(-0.01F))
						.texOffs(48, 10).addBox(-8.0F, -6.0F, -2.0F, 3.5F, 6.0F, 4.0F, new CubeDeformation(0.24F)),
						PartPose.offset(0.0F, -6.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public ModelPart root() {
        return this.root;
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
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
