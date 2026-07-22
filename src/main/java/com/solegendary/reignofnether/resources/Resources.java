package com.solegendary.reignofnether.resources;

import com.solegendary.reignofnether.sandbox.SandboxServer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

// RTS resources used for buildings, units, etc.
// usually tied to a ServerPlayer's NBT data so it is retained between relogs and server restarts
// but with a clientside copy too for the HUD
public class Resources {

    // storage cap per resource type - once reached, further gains are discarded (workers still
    // walk back and forth as normal, they just stop contributing once the player is capped)
    public static final int MAX_RESOURCE_AMOUNT = 1000;

    public String ownerName;

    // present amounts of each resource
    public int food;
    public int wood;
    public int ore;

    // balances of each resource to add (or remove if < 0)
    // shown as a +-X amount beside the resource on the HUD that is ticked in over time
    public int foodToAdd = 0;
    public int woodToAdd = 0;
    public int oreToAdd = 0;

    public Resources(String ownerName, int food, int wood, int ore) {
        this.ownerName = ownerName;
        this.food = food;
        this.wood = wood;
        this.ore = ore;
    }

    public int getTotalValue() {
        return this.food + this.foodToAdd + this.wood + this.woodToAdd + this.ore + this.oreToAdd;
    }

    // usually used clientside
    public void changeOverTime(int food, int wood, int ore) {
        this.foodToAdd += food;
        this.woodToAdd += wood;
        this.oreToAdd += ore;
        clampToAddFields();
    }

    // usually used serverside
    public void changeInstantly(int food, int wood, int ore) {
        this.food += food;
        this.wood += wood;
        this.ore += ore;
        clampTotals();
    }

    // clamp present totals to [0, MAX_RESOURCE_AMOUNT] - negative changes (spending resources)
    // are never blocked, only positive gains past the cap are discarded. Sandbox players are
    // exempt (they start with 999999 of each resource specifically to build/spawn without
    // worrying about cost - clamping them to 1000 would break that).
    private void clampTotals() {
        if (SandboxServer.isSandboxPlayer(this.ownerName))
            return;
        this.food = Math.min(this.food, MAX_RESOURCE_AMOUNT);
        this.wood = Math.min(this.wood, MAX_RESOURCE_AMOUNT);
        this.ore = Math.min(this.ore, MAX_RESOURCE_AMOUNT);
    }

    // clamp the pending "ToAdd" amounts so the clientside ticker never animates past the cap -
    // spending (negative ToAdd) is left untouched, only positive gains are capped based on how
    // much headroom is left before MAX_RESOURCE_AMOUNT. Sandbox players are exempt, see
    // clampTotals() above for why.
    private void clampToAddFields() {
        if (SandboxServer.isSandboxPlayer(this.ownerName))
            return;
        if (this.foodToAdd > 0)
            this.foodToAdd = Math.min(this.foodToAdd, Math.max(0, MAX_RESOURCE_AMOUNT - this.food));
        if (this.woodToAdd > 0)
            this.woodToAdd = Math.min(this.woodToAdd, Math.max(0, MAX_RESOURCE_AMOUNT - this.wood));
        if (this.oreToAdd > 0)
            this.oreToAdd = Math.min(this.oreToAdd, Math.max(0, MAX_RESOURCE_AMOUNT - this.ore));
    }

    // drain ToAdd fields into totals so that we get the appearance of change over time on the HUD
    public void tick() {
        this.food += getDrainPerTick(this.foodToAdd);
        this.foodToAdd -= getDrainPerTick(this.foodToAdd);
        this.wood += getDrainPerTick(this.woodToAdd);
        this.woodToAdd -= getDrainPerTick(this.woodToAdd);
        this.ore += getDrainPerTick(this.oreToAdd);
        this.oreToAdd -= getDrainPerTick(this.oreToAdd);
    }

    private int getDrainPerTick(int totalToAdd) {
        int absVal = Math.abs(totalToAdd);
        int retVal = 0;

        if (absVal > 10)
            retVal = Math.round((float) absVal / 10);
        else if (absVal > 0)
            retVal = 1;

        return (int) Math.signum(totalToAdd) * retVal;
    }

    public static Resources getTotalResourcesFromItems(List<ItemStack> itemStacks) {
        Resources resources = new Resources("", 0,0,0);
        for (ItemStack itemStack : itemStacks) {
            ResourceSource source = ResourceSources.getFromItem(itemStack.getItem());
            if (source != null) {
                int value = source.resourceValue * itemStack.getCount();
                switch (source.resourceName) {
                    case FOOD -> resources.changeInstantly(value, 0, 0);
                    case WOOD -> resources.changeInstantly(0, value, 0);
                    case ORE -> resources.changeInstantly(0, 0, value);
                }
            }
        }
        return resources;
    }


}
