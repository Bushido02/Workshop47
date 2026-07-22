package com.solegendary.reignofnether.playerprogression;

import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

// Serverside management of FormixPlayerProgression. Loads/creates a player's progression on
// login and syncs it to their client. Public setBodyLevel/setMindLevel/setFistLevel/setEidos/
// setLevel methods are called from CommandsServerEvents (the temporary /formix-stat command,
// see FORMIX_FACTION_LOG.md for why this is manual-only right now).
// Registered centrally in registrars/ServerEventRegistrar.java, matching every other
// XxxServerEvents class in this project (NOT via @Mod.EventBusSubscriber, which this project
// does not use).
public class FormixPlayerProgressionServerEvents {

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent evt) {
        String playerName = evt.getEntity().getName().getString();
        ServerLevel level = (ServerLevel) evt.getEntity().level();
        FormixPlayerProgression progression = FormixPlayerProgressionSaveData.getInstance(level).getOrCreate(playerName);
        FormixPlayerProgressionClientboundPacket.sync(progression);
    }

    private static FormixPlayerProgression get(ServerLevel level, String playerName) {
        return FormixPlayerProgressionSaveData.getInstance(level).getOrCreate(playerName);
    }

    private static void saveAndSync(ServerLevel level, FormixPlayerProgression progression) {
        FormixPlayerProgressionSaveData.getInstance(level).save();
        FormixPlayerProgressionClientboundPacket.sync(progression);
    }

    public static void setBodyLevel(ServerLevel level, String playerName, int value) {
        FormixPlayerProgression progression = get(level, playerName);
        progression.bodyLevel = value;
        saveAndSync(level, progression);
    }

    public static void setMindLevel(ServerLevel level, String playerName, int value) {
        FormixPlayerProgression progression = get(level, playerName);
        progression.mindLevel = value;
        saveAndSync(level, progression);
    }

    public static void setFistLevel(ServerLevel level, String playerName, int value) {
        FormixPlayerProgression progression = get(level, playerName);
        progression.fistLevel = value;
        saveAndSync(level, progression);
    }

    public static void setEidos(ServerLevel level, String playerName, int value) {
        FormixPlayerProgression progression = get(level, playerName);
        progression.eidos = value;
        saveAndSync(level, progression);
    }

    public static void setLevel(ServerLevel level, String playerName, int value) {
        FormixPlayerProgression progression = get(level, playerName);
        progression.level = value;
        saveAndSync(level, progression);
    }
}
