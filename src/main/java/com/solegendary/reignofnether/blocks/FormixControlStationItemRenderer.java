package com.solegendary.reignofnether.blocks;

import software.bernie.geckolib.renderer.GeoItemRenderer;

// Модель терминала физически большая (3x4x3 блока как блок в мире), поэтому
// в руке/инвентаре по умолчанию она рендерится в том же гигантском масштабе,
// перекрывая весь экран (см. FORMIX_FACTION_LOG.md, "видна огромная модель
// в руке"). scaleWidth/scaleHeight — публичные поля базового GeoRenderer
// (унаследованы от GeckoLib4 через GeoItemRenderer), единственный
// официально задокументированный способ пре-масштабировать GeckoLib-модель
// при рендере (НЕ через "display"/scale в models/item/*.json — тот путь не
// работает с "parent": "builtin/entity", который GeckoLib-предметы обязаны
// использовать). Значение 0.2 подобрано так, чтобы модель в руке была
// сопоставима по видимому размеру с обычным ваниль-предметом (габариты
// модели ~4-5 блоков x 0.2 ≈ размер чуть больше 1 блока — комфортно для
// руки/инвентаря, не перекрывает экран). Если после сборки всё ещё слишком
// крупная/мелкая — подстрой это единственное число.
public class FormixControlStationItemRenderer extends GeoItemRenderer<FormixControlStationBlockItem> {
    public FormixControlStationItemRenderer() {
        super(new FormixControlStationItemModel());
        this.scaleWidth = 0.2f;
        this.scaleHeight = 0.2f;
    }
}

