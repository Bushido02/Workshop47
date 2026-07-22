package com.solegendary.reignofnether.playerprogression;

// Clientside cache of the local player's FormixPlayerProgression, updated whenever
// FormixPlayerProgressionClientboundPacket arrives from the server. Read by the custom HUD
// renderer (see hud/HudClientEvents.java) to draw the heart/level/eidos/stat panel.
public class FormixPlayerProgressionClientEvents {

    public static FormixPlayerProgression progression = FormixPlayerProgression.getDefault("");
}
