package com.solegendary.reignofnether.playerprogression;

import com.solegendary.reignofnether.ReignOfNether;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nonnull;
import java.util.ArrayList;

// Persists FormixPlayerProgression per player permanently (survives relogs/server restarts),
// per explicit user decision (22.07.2026) that this progression should carry over between
// matches like a normal RPG, unlike Resources which resets every RTS match.
// Modelled directly on unit/HeroUnitSaveData.java - same SavedData pattern already proven in
// this project, just keyed by ownerName instead of a per-unit uuid.
public class FormixPlayerProgressionSaveData extends SavedData {

    public final ArrayList<FormixPlayerProgression> players = new ArrayList<>();

    private static FormixPlayerProgressionSaveData create() {
        return new FormixPlayerProgressionSaveData();
    }

    @Nonnull
    public static FormixPlayerProgressionSaveData getInstance(LevelAccessor level) {
        MinecraftServer server = level.getServer();
        if (server == null) {
            return create();
        }
        return server.overworld()
            .getDataStorage()
            .computeIfAbsent(FormixPlayerProgressionSaveData::load, FormixPlayerProgressionSaveData::create, "saved-formix-player-progression-data");
    }

    public static FormixPlayerProgressionSaveData load(CompoundTag tag) {
        ReignOfNether.LOGGER.info("FormixPlayerProgressionSaveData.load");

        FormixPlayerProgressionSaveData data = create();
        ListTag ltag = (ListTag) tag.get("players");

        if (ltag != null) {
            for (Tag ctag : ltag) {
                CompoundTag ptag = (CompoundTag) ctag;

                String ownerName = ptag.getString("ownerName");
                int bodyLevel = ptag.getInt("bodyLevel");
                int mindLevel = ptag.getInt("mindLevel");
                int fistLevel = ptag.getInt("fistLevel");
                int eidos = ptag.getInt("eidos");
                int level = ptag.getInt("level");

                data.players.add(new FormixPlayerProgression(ownerName, bodyLevel, mindLevel, fistLevel, eidos, level));
            }
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        this.players.forEach(p -> {
            CompoundTag cTag = new CompoundTag();
            cTag.putString("ownerName", p.ownerName);
            cTag.putInt("bodyLevel", p.bodyLevel);
            cTag.putInt("mindLevel", p.mindLevel);
            cTag.putInt("fistLevel", p.fistLevel);
            cTag.putInt("eidos", p.eidos);
            cTag.putInt("level", p.level);
            list.add(cTag);
        });
        tag.put("players", list);
        return tag;
    }

    public void save() {
        this.setDirty();
    }

    public FormixPlayerProgression getOrCreate(String ownerName) {
        for (FormixPlayerProgression p : players)
            if (p.ownerName.equals(ownerName))
                return p;
        FormixPlayerProgression created = FormixPlayerProgression.getDefault(ownerName);
        players.add(created);
        return created;
    }
}
