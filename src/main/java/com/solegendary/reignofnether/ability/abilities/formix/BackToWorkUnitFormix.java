package com.solegendary.reignofnether.ability.abilities.formix;

import com.solegendary.reignofnether.ability.Ability;
import com.solegendary.reignofnether.building.buildings.formix.FormixHive;
import com.solegendary.reignofnether.hud.AbilityButton;
import com.solegendary.reignofnether.keybinds.Keybinding;
import com.solegendary.reignofnether.unit.UnitAction;
import com.solegendary.reignofnether.unit.interfaces.Unit;
import com.solegendary.reignofnether.unit.units.formix.FormixWarriorUnit;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.Level;

import java.util.List;

import static com.solegendary.reignofnether.unit.UnitClientEvents.sendUnitCommand;

// Formix equivalent of BackToWorkUnit (Villagers): converts this warrior back into a
// FormixWorkerUnit, restoring its previous gathering assignment if it had one.
public class BackToWorkUnitFormix extends Ability {

    private static final int RANGE = FormixHive.WARRIOR_RANGE;

    public BackToWorkUnitFormix() {
        super(
                UnitAction.BACK_TO_WORK_UNIT,
                0,
                RANGE,
                0,
                false,
                false
        );
    }

    @Override
    public AbilityButton getButton(Keybinding hotkey, Unit unit) {
        if (!(unit instanceof FormixWarriorUnit)) return null;
        return new AbilityButton(
                "Back to Work (Building)",
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/item/iron_pickaxe.png"),
                hotkey,
                () -> false,
                () -> false,
                () -> true,
                () -> sendUnitCommand(UnitAction.BACK_TO_WORK_UNIT),
                null,
                List.of(
                        FormattedCharSequence.forward(I18n.get("abilities.reignofnether.back_to_work_unit_formix"), Style.EMPTY.withBold(true)),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("abilities.reignofnether.back_to_work_unit_formix.tooltip1"), Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("abilities.reignofnether.back_to_work_unit_formix.tooltip2"), Style.EMPTY)
                ),
                this,
                unit
        );
    }

    @Override
    public void use(Level level, Unit unitUsing, BlockPos targetBp) {
        if (unitUsing instanceof FormixWarriorUnit warrior && !level.isClientSide())
            warrior.convertToWorker();
    }
}
