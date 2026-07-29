package com.solegendary.reignofnether.blocks;

import com.solegendary.reignofnether.util.MyRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

// Small popup shown when the player clicks an inactive FormixControlStation terminal (see
// FormixControlStationInactiveBlock.use()). Purely a confirmation + "can I afford it" UX layer -
// all real validation (does the block still exist, does the player still have enough Eidos)
// happens server-side in FormixControlStationServerboundPacket.handle(), this screen never
// mutates state directly.
//
// Modelled on MatchEndScreen.java (small centered panel, MyRenderer.renderFrameWithBg, plain
// vanilla Button) rather than the heavier MatchStartScreen - this is a one-button confirmation,
// not a full custom UI.
public class FormixControlStationUnlockScreen extends Screen {

    private static final int BG_PANEL = 0x40000000;
    private static final int ACCENT = 0xFFE6C76A;
    private static final int TEXT_NORMAL = 0xFFFFFFFF;
    private static final int TEXT_DIM = 0xFFB0B8C0;

    private static final int PANEL_W = 200;
    private static final int PANEL_H = 70;
    private static final int PAD = 10;

    private final BlockPos pos;
    private final int cost;

    private int panelL, panelT;

    public FormixControlStationUnlockScreen(BlockPos pos, int cost) {
        super(Component.translatable("hud.formix_control_station.reignofnether.unlock_title"));
        this.pos = pos;
        this.cost = cost;
    }

    @Override
    protected void init() {
        panelL = (this.width - PANEL_W) / 2;
        panelT = (this.height - PANEL_H) / 2;

        boolean canAfford = Minecraft.getInstance().player != null
                && Minecraft.getInstance().player.totalExperience >= cost;

        Button unlockButton = Button.builder(
                        Component.translatable("hud.formix_control_station.reignofnether.unlock_button", cost),
                        b -> {
                            FormixControlStationServerboundPacket.requestUnlock(pos);
                            onClose();
                        })
                .bounds(panelL + PAD, panelT + PANEL_H - PAD - 20, PANEL_W - PAD * 2, 20)
                .build();
        unlockButton.active = canAfford;
        addRenderableWidget(unlockButton);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        MyRenderer.renderFrameWithBg(g, panelL, panelT, PANEL_W, PANEL_H, BG_PANEL);

        int cl = panelL + PAD;
        int y = panelT + PAD;

        g.drawString(font, Component.translatable("hud.formix_control_station.reignofnether.unlock_title"), cl, y, ACCENT, true);
        y += 12;

        int currentEidos = Minecraft.getInstance().player != null ? Minecraft.getInstance().player.totalExperience : 0;
        String eidosLine = Component.translatable("hud.formix_control_station.reignofnether.unlock_eidos", currentEidos, cost).getString();
        g.drawString(font, eidosLine, cl, y, currentEidos >= cost ? TEXT_NORMAL : 0xFFE05A5A, true);

        super.render(g, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
