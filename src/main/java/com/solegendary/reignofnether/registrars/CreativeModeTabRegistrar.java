package com.solegendary.reignofnether.registrars;

import com.solegendary.reignofnether.ReignOfNether;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

// Отдельная вкладка творческого режима для фракции Formix - все Item/BlockItem
// фракции удобно собраны в одном месте, не разбросаны по ваниль-вкладкам
// (FUNCTIONAL_BLOCKS и т.п.), как было раньше (см. FORMIX_CONTROL_STATION_BLOCK_ITEM
// в ItemRegistrar.java до 26.07.2026 - лежал в FUNCTIONAL_BLOCKS).
//
// КАК ДОБАВИТЬ НОВЫЙ FORMIX-ПРЕДМЕТ/БЛОК В ЭТУ ВКЛАДКУ (для будущих сессий):
// Просто добавь RegistryObject в список FORMIX_TAB_ITEMS ниже (одна строка),
// НЕ нужно трогать ItemRegistrar.java/BlockRegistrar.java/CommonModEvents.java -
// та ручная "добавь event.accept(...) в CommonModEvents.creativeTabSetup()"
// разводка, которая понадобилась для FORMIX_CONTROL_STATION_BLOCK_ITEM (т.к. он
// не создаётся через авто-BlockItem, см. комментарий в BlockRegistrar.java),
// здесь больше не нужна - список ниже читается один раз в displayItems() и
// покрывает все перечисленные RegistryObject независимо от того, как они были
// зарегистрированы (авто-BlockItem, ручной GeoItem, обычный Item, спавн-яйцо).
public class CreativeModeTabRegistrar {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ReignOfNether.MOD_ID);

    // Единый список всех Formix-предметов/блоков, показываемых в вкладке.
    // Порядок в списке = порядок отображения в инвентаре.
    private static final List<Supplier<? extends RegistryObject<? extends Item>>> FORMIX_TAB_ITEMS = new ArrayList<>();

    static {
        FORMIX_TAB_ITEMS.add(() -> ItemRegistrar.FORMIX_CONTROL_STATION_BLOCK_ITEM);
        FORMIX_TAB_ITEMS.add(() -> ItemRegistrar.FORMIX_CONTROL_STATION_INACTIVE_BLOCK_ITEM);
        // Добавляй сюда новые Formix RegistryObject<Item>/<Block Item> по мере
        // их появления - например когда у worker/warrior/Hive появятся свои
        // предметы (спавн-яйца или blockitem), или у будущих зданий фракции.
    }

    public static final RegistryObject<CreativeModeTab> FORMIX_TAB = CREATIVE_MODE_TABS.register("formix",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + ReignOfNether.MOD_ID + ".formix"))
                    .icon(() -> new ItemStack(ItemRegistrar.FORMIX_CONTROL_STATION_BLOCK_ITEM.get()))
                    .displayItems((params, output) -> {
                        for (Supplier<? extends RegistryObject<? extends Item>> supplier : FORMIX_TAB_ITEMS) {
                            output.accept(supplier.get().get());
                        }
                    })
                    .build()
    );

    public static void init(FMLJavaModLoadingContext context) {
        CREATIVE_MODE_TABS.register(context.getModEventBus());
    }
}
