package com.solegendary.reignofnether.playerprogression;

// Simple data holder for one player's Formix character progression.
// TEMPORARY/SCAFFOLDING (per user request 22.07.2026): right now these values are only ever set
// manually via the /reignofnether formix-stat command while the real skill-tree UI (still being
// designed by the user in draw.io) and real eidos-gain sources (kills/minigames/eidos farm - see
// FORMIX_FACTION_LOG.md for what's still undecided about those) are built out. Nothing here
// currently grants gameplay bonuses - the three stats are meant to only gate what content is
// unlockable in the future skill tree, per explicit user decision.
public class FormixPlayerProgression {

    public String ownerName;

    // "тело" / "разум" / "кулак" from the reference screenshot - currently free-standing ints
    // set by command, not yet tied to any real stat-point spending system.
    public int bodyLevel;
    public int mindLevel;
    public int fistLevel;

    // Eidos - DEPRECATED as of 22.07.2026: originally a standalone field, now unused by the HUD
    // (see hud/HudClientEvents.onRenderCustomPlayerHud). Eidos is just relabelled vanilla
    // Minecraft XP per user request ("просто текстуру и название поменяем") - the HUD reads
    // player.totalExperience/experienceLevel directly instead. Kept here only so existing saved
    // data doesn't break; not read anywhere in current code. Safe to remove entirely in a future
    // cleanup pass once confirmed nothing references it.
    public int eidos;

    // overall character level - DEPRECATED as of 22.07.2026, see eidos comment above. HUD now
    // reads player.experienceLevel directly instead of this field.
    public int level;

    public FormixPlayerProgression(String ownerName, int bodyLevel, int mindLevel, int fistLevel, int eidos, int level) {
        this.ownerName = ownerName;
        this.bodyLevel = bodyLevel;
        this.mindLevel = mindLevel;
        this.fistLevel = fistLevel;
        this.eidos = eidos;
        this.level = level;
    }

    public static FormixPlayerProgression getDefault(String ownerName) {
        return new FormixPlayerProgression(ownerName, 0, 0, 0, 0, 0);
    }
}
