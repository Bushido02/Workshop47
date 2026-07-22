package com.solegendary.reignofnether.building.buildings.formix;

import com.solegendary.reignofnether.ability.Ability;
import com.solegendary.reignofnether.ability.abilities.formix.BackToWorkBuildingFormix;
import com.solegendary.reignofnether.ability.abilities.formix.CallToArmsBuildingFormix;
import com.solegendary.reignofnether.api.ReignOfNetherRegistries;
import com.solegendary.reignofnether.blocks.BlockClientEvents;
import com.solegendary.reignofnether.building.*;
import com.solegendary.reignofnether.building.addon.NightSourceAddon;
import com.solegendary.reignofnether.building.addon.RangeIndicatorAddon;
import com.solegendary.reignofnether.building.production.ProductionBuilding;
import com.solegendary.reignofnether.building.production.ProductionItems;
import com.solegendary.reignofnether.keybinds.Keybinding;
import com.solegendary.reignofnether.keybinds.Keybindings;
import com.solegendary.reignofnether.resources.ResourceCost;
import com.solegendary.reignofnether.resources.ResourceCosts;
import com.solegendary.reignofnether.faction.Faction;
import com.solegendary.reignofnether.util.MiscUtil;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

// Main building of the Formix faction. Produces FormixWorkerUnits and is the anchor point
// workers walk to when converting into FormixWarriorUnits (Call to Arms) and vice versa
// (Back to Work) -- direct parallel of TownCentre for Villagers.
//
// TEMPORARY: structureName currently points at the existing "town_centre" NBT schematic so this
// compiles and is placeable in-game before a dedicated Formix structure/texture set exists.
// Once a real Formix Hive structure is ready:
//   1. Save it as src/main/resources/{assets,data}/reignofnether/structures/formix_hive.nbt
//   2. Change structureName below from "town_centre" to "formix_hive"
//   3. Update portraitBlock/icon to a Formix-appropriate block/texture
public class FormixHive extends ProductionBuilding implements RangeIndicatorAddon {

    public final static String buildingName = "Formix Hive";
    public final static String structureName = "town_centre"; // TODO: replace with "formix_hive" once its NBT exists
    public final static ResourceCost cost = ResourceCosts.FORMIX_HIVE;

    // distance a warrior can move away from the hive before reverting to a worker
    public static final int WARRIOR_RANGE = 60;

    public FormixHive() {
        super(structureName, cost, true);
        this.name = buildingName;
        this.portraitBlock = Blocks.HONEYCOMB_BLOCK;
        this.icon = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/honeycomb_block.png");

        this.maxHealth = 380d;
        this.buildTimeModifier = 0.328f; // 60s total build time with 3 workers
        this.canAcceptResources = true;

        this.startingBlockTypes.add(Blocks.STONE_BRICK_STAIRS);
        this.startingBlockTypes.add(Blocks.GRASS_BLOCK);
        this.startingBlockTypes.add(Blocks.POLISHED_ANDESITE_STAIRS);

        Ability callToArms = new CallToArmsBuildingFormix();
        this.abilities.add(callToArms, Keybindings.hotkey1);
        BackToWorkBuildingFormix backToWork = new BackToWorkBuildingFormix();
        this.abilities.add(backToWork, Keybindings.build);

        this.productions.add(ProductionItems.FORMIX_WORKER, Keybindings.abilitySlot1);

        setActiveAddon(RangeIndicatorAddon.class, this, true);
    }

    public Faction getFaction() {return Faction.FORMIX;}

    public BuildingPlaceButton getBuildButton(Keybinding hotkey) {
        ResourceLocation key = ReignOfNetherRegistries.BUILDING.getKey(this);
        String name = I18n.get("buildings." + getFaction().name().toLowerCase() + "." + key.getNamespace() + "." + key.getPath());
        return new BuildingPlaceButton(
               name,
                ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/honeycomb_block.png"),
                hotkey,
                () -> BuildingClientEvents.getBuildingToPlace() == Buildings.FORMIX_HIVE,
                () -> false,
                () -> true,
                List.of(
                        FormattedCharSequence.forward(I18n.get("buildings.reignofnether.formix_hive"), Style.EMPTY.withBold(true)),
                        ResourceCosts.getFormattedCost(cost),
                        ResourceCosts.getFormattedPop(cost),
                        FormattedCharSequence.forward("", Style.EMPTY),
                        FormattedCharSequence.forward(I18n.get("buildings.reignofnether.formix_hive.tooltip1"), Style.EMPTY)
                ),
                this
        );
    }

    @Override
    public void tick(Level tickLevel, BuildingPlacement buildingPlacement) {
        super.tick(tickLevel, buildingPlacement);
        if (tickLevel.isClientSide && buildingPlacement.getTickAgeAfterBuilt() > 0 && buildingPlacement.getTickAgeAfterBuilt() % 100 == 0)
            updateHighlightBps(buildingPlacement);
    }

    @Override
    public int getRange(BuildingPlacement placement) {
        return (placement.isBuilt) ? WARRIOR_RANGE : 0;
    }

    @Override
    public void updateHighlightBps(BuildingPlacement placement) {
        if (!placement.level.isClientSide())
            return;
        placement.getDataStorage().getData(RangeIndicatorAddon.HIGHLIGHT_BPS_CACHE).clear();
        placement.getDataStorage().getData(RangeIndicatorAddon.HIGHLIGHT_BPS_CACHE).addAll(MiscUtil.getRangeIndicatorCircleBlocks(placement.centrePos,
                getRange(placement) - BlockClientEvents.VISIBLE_BORDER_ADJ, placement.level, hasActiveAddon(NightSourceAddon.class)));
    }

    @Override
    public boolean showOnlyWhenSelected(BuildingPlacement placement) {
        return true;
    }
}
