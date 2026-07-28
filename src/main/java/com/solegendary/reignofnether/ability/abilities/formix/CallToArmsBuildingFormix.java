package com.solegendary.reignofnether.ability.abilities.formix;

import com.solegendary.reignofnether.ability.Ability;
import com.solegendary.reignofnether.building.BuildingPlacement;
import com.solegendary.reignofnether.building.buildings.formix.FormixHive;
import com.solegendary.reignofnether.hud.AbilityButton;
import com.solegendary.reignofnether.keybinds.Keybinding;
import com.solegendary.reignofnether.keybinds.Keybindings;
import com.solegendary.reignofnether.sounds.SoundAction;
import com.solegendary.reignofnether.sounds.SoundClientboundPacket;
import com.solegendary.reignofnether.unit.UnitAction;
import com.solegendary.reignofnether.unit.interfaces.Unit;
import com.solegendary.reignofnether.unit.interfaces.WorkerUnit;
import com.solegendary.reignofnether.unit.units.formix.FormixWorkerUnit;
import com.solegendary.reignofnether.util.MiscUtil;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.Level;
import org.joml.Vector3d;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.solegendary.reignofnether.unit.UnitClientEvents.sendUnitCommand;

// Formix equivalent of CallToArmsBuilding (Villagers): calls every owned FormixWorkerUnit
// within range of this FormixHive to walk to it and convert into a warrior.
public class CallToArmsBuildingFormix extends Ability {

    private static final int RANGE = FormixHive.WARRIOR_RANGE;

    public CallToArmsBuildingFormix() {
        super(
                UnitAction.CALL_TO_ARMS_BUILDING,
                0,
                RANGE,
                0,
                false,
                false
        );
        this.defaultHotkey = Keybindings.hotkey1;
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, BuildingPlacement placement) {
        return new AbilityButton(
                "Call To Arms",
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/item/bell.png"),
                hotkey,
                () -> false,
                () -> false,
                () -> true,
                () -> sendUnitCommand(UnitAction.CALL_TO_ARMS_BUILDING),
                null,
                List.of(
                        FormattedCharSequence.forward(I18n.get("abilities.reignofnether.call_to_arms_building_formix"), Style.EMPTY.withBold(true)),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("abilities.reignofnether.call_to_arms_building_formix.tooltip1"), Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("abilities.reignofnether.call_to_arms_building_formix.tooltip2"), Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("abilities.reignofnether.call_to_arms_building_formix.tooltip3", RANGE), Style.EMPTY)
                ),
                this,
                placement
        );
    }

    @Override
    public void use(Level level, BuildingPlacement buildingUsing, BlockPos targetBp) {
        List<FormixWorkerUnit> units = MiscUtil.getEntitiesWithinRange(
                new Vector3d(buildingUsing.centrePos.getX(), buildingUsing.centrePos.getY(), buildingUsing.centrePos.getZ()),
                range, FormixWorkerUnit.class, buildingUsing.getLevel());
        for (FormixWorkerUnit unit : units) {
            if (unit.getOwnerName().equals(buildingUsing.ownerName)) {
                Unit.resetBehaviours(unit);
                WorkerUnit.resetBehaviours(unit);
                unit.callToArmsGoal.setNearestFormixHiveAsTarget();
            }
        }

        if (!level.isClientSide()) {
            // TEMP DEBUG (Formix population investigation, remove after diagnosis):
            // fires every time the building's Call to Arms is used
            try {
                String ownerName = buildingUsing.ownerName;
                int supply = com.solegendary.reignofnether.building.BuildingServerEvents.getTotalPopulationSupply(ownerName);
                String debugMsg = "[FORMIX-DEBUG-CALLTOARMS-BUILDING] owner='" + ownerName + "' supply=" + supply
                        + " thisBuilding.isBuilt=" + buildingUsing.isBuilt
                        + " thisBuilding.pop=" + buildingUsing.getBuilding().cost.population
                        + " workersInRange=" + units.size();
                System.out.println(debugMsg);
                net.minecraft.server.level.ServerPlayer sp = level.getServer().getPlayerList().getPlayerByName(ownerName);
                if (sp != null)
                    sp.sendSystemMessage(net.minecraft.network.chat.Component.literal(debugMsg));
            } catch (Exception e) {
                System.out.println("[FORMIX-DEBUG-CALLTOARMS-BUILDING] exception: " + e);
            }

            SoundClientboundPacket.playSoundAtPos(SoundAction.BELL, buildingUsing.centrePos);
            CompletableFuture.delayedExecutor(300, TimeUnit.MILLISECONDS).execute(() -> {
                SoundClientboundPacket.playSoundAtPos(SoundAction.BELL, buildingUsing.centrePos);
            });
        }
    }
}