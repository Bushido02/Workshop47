package com.solegendary.reignofnether.blocks;

import com.solegendary.reignofnether.ReignOfNether;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

// Small popup shown when the player clicks an inactive FormixControlStation terminal (see
// FormixControlStationInactiveBlock.use()). Purely a confirmation + "can I afford it" UX layer -
// all real validation (does the block still exist, does the player still have enough Eidos)
// happens server-side in FormixControlStationServerboundPacket.handle(), this screen never
// mutates state directly.
//
// Background is a full-screen custom PNG supplied by the project owner (see
// textures/gui/formix_unlock_bg.png) rather than the earlier programmatic
// MyRenderer.renderFrameWithBg panel - stretched to the current window size via the 9-arg
// GuiGraphics.blit(...) overload (same call shape already used in
// tutorial/TutorialRendering.java for non-tiled textures in this project), so it scales cleanly
// with any resolution/GUI Scale without needing a fixed logical panel size.
public class FormixControlStationUnlockScreen extends Screen {

    // Actual pixel dimensions of formix_unlock_bg.png. MUST be updated to match the real file
    // if/when the art is replaced with a different resolution - these are only used as the UV
    // source-texture size in blit(), not as the on-screen size (the image is always stretched to
    // fill the current window regardless of these values).
    private static final int BG_TEXTURE_W = 1920;
    private static final int BG_TEXTURE_H = 1080;

    private static final ResourceLocation BACKGROUND_LOCATION =
            ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "textures/gui/formix_unlock_bg.png");

    private static final int TEXT_NORMAL = 0xFFFFFFFF;
    private static final int TEXT_DEFICIT = 0xFFE05A5A;
    private static final int ACCENT = 0xFFE6C76A;

    private static final int BUTTON_W = 220;
    private static final int BUTTON_H = 20;

    private final BlockPos pos;
    private final int cost;

    public FormixControlStationUnlockScreen(BlockPos pos, int cost) {
        super(Component.translatable("hud.formix_control_station.reignofnether.unlock_title"));
        this.pos = pos;
        this.cost = cost;
    }

    @Override
    protected void init() {
        boolean canAfford = Minecraft.getInstance().player != null
                && Minecraft.getInstance().player.totalExperience >= cost;

        Button unlockButton = Button.builder(
                        Component.translatable("hud.formix_control_station.reignofnether.unlock_button", cost),
                        b -> {
                            FormixControlStationServerboundPacket.requestUnlock(pos);
                            onClose();
                        })
                .bounds((this.width - BUTTON_W) / 2, this.height - 60, BUTTON_W, BUTTON_H)
                .build();
        unlockButton.active = canAfford;
        addRenderableWidget(unlockButton);

        addRenderableWidget(Button.builder(Component.literal("✕"), b -> onClose())
                .bounds(this.width - 30, 10, 20, 20)
                .build());
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        // Stretched full-screen background - see class javadoc for why the 9-arg blit overload
        // (not the tiled Screen.BACKGROUND_LOCATION pattern MatchEndScreen uses elsewhere in
        // this project) is the correct call here.
        g.blit(BACKGROUND_LOCATION, 0, 0, 0, 0, this.width, this.height, BG_TEXTURE_W, BG_TEXTURE_H);

        g.drawCenteredString(font, Component.translatable("hud.formix_control_station.reignofnether.unlock_title"),
                this.width / 2, 30, ACCENT);

        int currentEidos = Minecraft.getInstance().player != null ? Minecraft.getInstance().player.totalExperience : 0;
        String eidosLine = Component.translatable("hud.formix_control_station.reignofnether.unlock_eidos", currentEidos, cost).getString();
        g.drawCenteredString(font, eidosLine, this.width / 2, this.height - 80, currentEidos >= cost ? TEXT_NORMAL : TEXT_DEFICIT);

        super.render(g, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
