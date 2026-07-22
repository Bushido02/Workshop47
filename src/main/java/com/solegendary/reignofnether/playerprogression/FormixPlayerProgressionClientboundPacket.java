package com.solegendary.reignofnether.playerprogression;

import com.solegendary.reignofnether.registrars.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

// Syncs FormixPlayerProgression server -> client so the custom HUD (heart/level/eidos/stats)
// can render it. Sent to ALL clients like ResourcesClientboundPacket in this project already
// does for personal per-player data - each client filters by ownerName on the receiving end,
// matching the existing pattern rather than introducing a new per-player-only distribution
// style.
public class FormixPlayerProgressionClientboundPacket {

    String ownerName;
    int bodyLevel;
    int mindLevel;
    int fistLevel;
    int eidos;
    int level;

    public static void sync(FormixPlayerProgression progression) {
        PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(),
                new FormixPlayerProgressionClientboundPacket(progression));
    }

    public FormixPlayerProgressionClientboundPacket(FormixPlayerProgression progression) {
        this.ownerName = progression.ownerName;
        this.bodyLevel = progression.bodyLevel;
        this.mindLevel = progression.mindLevel;
        this.fistLevel = progression.fistLevel;
        this.eidos = progression.eidos;
        this.level = progression.level;
    }

    public FormixPlayerProgressionClientboundPacket(FriendlyByteBuf buffer) {
        this.ownerName = buffer.readUtf();
        this.bodyLevel = buffer.readInt();
        this.mindLevel = buffer.readInt();
        this.fistLevel = buffer.readInt();
        this.eidos = buffer.readInt();
        this.level = buffer.readInt();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.ownerName);
        buffer.writeInt(this.bodyLevel);
        buffer.writeInt(this.mindLevel);
        buffer.writeInt(this.fistLevel);
        buffer.writeInt(this.eidos);
        buffer.writeInt(this.level);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        final var success = new AtomicBoolean(false);

        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> {
                    if (Minecraft.getInstance().player != null &&
                            Minecraft.getInstance().player.getName().getString().equals(ownerName)) {
                        FormixPlayerProgressionClientEvents.progression =
                                new FormixPlayerProgression(ownerName, bodyLevel, mindLevel, fistLevel, eidos, level);
                    }
                    success.set(true);
                });
        });
        ctx.get().setPacketHandled(true);
        return success.get();
    }
}
