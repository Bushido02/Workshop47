package com.solegendary.reignofnether.ability.abilities.formix;

import com.solegendary.reignofnether.ability.Ability;
import com.solegendary.reignofnether.building.BuildingPlacement;
import com.solegendary.reignofnether.building.buildings.formix.FormixHive;
import com.solegendary.reignofnether.hud.AbilityButton;
import com.solegendary.reignofnether.keybinds.Keybinding;
import com.solegendary.reignofnether.keybinds.Keybindings;
import com.solegendary.reignofnether.unit.UnitAction;
import com.solegendary.reignofnether.unit.units.formix.FormixWarriorUnit;
import com.solegendary.reignofnether.util.MiscUtil;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.Level;
import org.joml.Vector3d;

import java.util.List;

import static com.solegendary.reignofnether.unit.UnitClientEvents.sendUnitCommand;

// Formix equivalent of BackToWorkBuilding (Villagers): converts every owned FormixWarriorUnit
// within range of this FormixHive back into a worker.
public class BackToWorkBuildingFormix extends Ability {

    private static final int RANGE = FormixHive.WARRIOR_RANGE + 5;

    public BackToWorkBuildingFormix() {
        super(
                UnitAction.BACK_TO_WORK_BUILDING,
                0,
                RANGE,
                0,
                false,
                false
        );
        this.defaultHotkey = Keybindings.hotkey2;
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, BuildingPlacement placement) {
        return new AbilityButton(
                "Back to Work",
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/item/iron_pickaxe.png"),
                hotkey,
                () -> false,
                () -> false,
                () -> true,
                () -> sendUnitCommand(UnitAction.BACK_TO_WORK_BUILDING),
                null,
                List.of(
                        FormattedCharSequence.forward(I18n.get("abilities.reignofnether.back_to_work_building_formix"), Style.EMPTY.withBold(true)),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("abilities.reignofnether.back_to_work_building_formix.tooltip1"), Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("abilities.reignofnether.back_to_work_building_formix.tooltip2"), Style.EMPTY)
                ),
                this,
                placement
        );
    }

    @Override
    public void use(Level level, BuildingPlacement buildingUsing, BlockPos targetBp) {
        List<FormixWarriorUnit> units = MiscUtil.getEntitiesWithinRange(
                new Vector3d(buildingUsing.centrePos.getX(), buildingUsing.centrePos.getY(), buildingUsing.centrePos.getZ()),
                range, FormixWarriorUnit.class, buildingUsing.getLevel());
        for (FormixWarriorUnit unit : units) {
            if (unit.getOwnerName().equals(buildingUsing.ownerName) && !level.isClientSide()) {
                unit.convertToWorker();
            }
        }
    }
}
