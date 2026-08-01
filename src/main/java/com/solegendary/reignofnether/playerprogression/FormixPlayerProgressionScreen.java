package com.solegendary.reignofnether.playerprogression;

import com.solegendary.reignofnether.ReignOfNether;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

// "Познание/Мир/Я" screen - opened by clicking the upper zone (relativeY > 0.85) of an ACTIVE
// FormixControlStation terminal (see FormixControlStationBlock.use(), which previously left
// that zone as a bare InteractionResult.PASS with a TODO comment pointing here).
//
// SCOPE OF THIS PASS (29.07.2026): shell only - full-screen background art + a close button.
// Skill tree nodes/progress bars/unlock logic (see the drawio mockup the project owner shared
// this session, and PROJECT_NOTES/FORMIX_FACTION_LOG.md section 0/15 for the current plan) are
// an explicitly separate, larger task - new SaveData fields, a serverbound spend/unlock packet,
// per-node cost balancing, etc. Do NOT assume the mockup's node positions/labels are final; the
// project owner said node names/text are still being decided. This screen exists so it opens
// cleanly and is ready to receive that content without needing another screen-plumbing pass.
public class FormixPlayerProgressionScreen extends Screen {

    // See FormixControlStationUnlockScreen for the same texture-size caveat: these are only the
    // UV source-texture dimensions for blit(), update them if the real PNG's resolution differs.
    // Update alongside formix_progression_bg.png whenever the real art file changes size.
    private static final int BG_TEXTURE_W = 1920;
    private static final int BG_TEXTURE_H = 1080;

    private static final ResourceLocation BACKGROUND_LOCATION =
            ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "textures/gui/formix_progression_bg.png");

    public FormixPlayerProgressionScreen() {
        super(Component.translatable("hud.formix_control_station.reignofnether.progression_title"));
    }

    @Override
    protected void init() {
        addRenderableWidget(Button.builder(Component.literal("✕"), b -> onClose())
                .bounds(this.width - 30, 10, 20, 20)
                .build());
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        // Full-screen stretched background - same blit() pattern as FormixControlStationUnlockScreen,
        // see that class's javadoc for why the 9-arg overload (not tiled Screen.BACKGROUND_LOCATION)
        // is used here.
        g.blit(BACKGROUND_LOCATION, 0, 0, 0, 0, this.width, this.height, BG_TEXTURE_W, BG_TEXTURE_H);

        // TODO (future session): skill tree nodes go here, once FormixPlayerProgressionSaveData
        // has real unlock-state fields and the project owner has finalised node names/positions
        // from the drawio mockup.

        super.render(g, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
