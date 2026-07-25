# Reign of Nether (Workshop47) — Архитектура проекта

> Основано на анализе репозитория `github.com/Bushido02/Workshop47`
> (клонирован и разобран напрямую, версия мода `1.3.8d` из `ReignOfNether.java`).
> Это тот же проект, что описан в `PROJECT_NOTES/FORMIX_FACTION_LOG.md` —
> Minecraft-мод Reign of Nether с добавленной фракцией Formix.

## 1. Что это

**Reign of Nether** — RTS-мод для Minecraft (Forge, Minecraft 1.20.1),
превращающий ванильную игру в realtime-стратегию в духе Warcraft/Starcraft/
Age of Empires, но построенную поверх ванильных мобов, блоков и механик,
а не полностью самостоятельного движка.

- **Платформа:** Minecraft Forge 1.20.1
- **Язык:** Java
- **Сборка:** Gradle (`gradlew`), decompile-based ForgeGradle пайплайн
  (`dpanvil_version=1.19.2-4.4.0` в `gradle.properties`)
- **Mod ID:** `reignofnether`
- **Точка входа:** `com.solegendary.reignofnether.ReignOfNether` (аннотация `@Mod("reignofnether")`)
- **Объём:** 852 Java-файла в `src/main/java`
- **Лицензия:** GPLv3

## 2. Точки входа и порядок инициализации

```
ReignOfNether (constructor, FMLJavaModLoadingContext)
 ├─ EnchantmentRegistrar.init()
 ├─ AttributeRegistrar.init()
 ├─ ItemRegistrar.init()
 ├─ EntityRegistrar.init()
 ├─ ContainerRegistrar.init()
 ├─ SoundRegistrar.init()
 ├─ BlockRegistrar.init()
 ├─ BlockEntityRegistrar.init()
 ├─ GameRuleRegistrar.init()
 ├─ Buildings.init()
 ├─ FactionRegistries.register()      ← регистрация зданий по фракциям
 ├─ ProductionItems.init()
 ├─ MobEffectRegistrar.init()
 ├─ ParticleRegistrar.init()
 ├─ CommandArgumentRegistrar.init()
 ├─ BuildingSelectorOptions.bootStrap()
 ├─ ClientEventRegistrar (только Dist.CLIENT)
 ├─ ServerEventRegistrar (только Dist.DEDICATED_SERVER)
 └─ ReignOfNetherCommonConfigs / ClientModConfigs — регистрация конфигов
```

Помимо этого в корневом пакете есть:
- **`CommonModEvents`** (`@Mod.EventBusSubscriber(bus = Bus.MOD)`) — регистрирует
  атрибуты (health/damage/speed) для КАЖДОЙ сущности мода через
  `EntityAttributeCreationEvent`, плюс инициализацию `PacketHandler`.
- **`ClientModEvents`** — регистрация рендереров сущностей и layer definitions
  (клиентская часть графики моделей).

Мод также содержит собственную логику "reset packet" (`S2CReset`) поверх
FML handshake-канала — нестандартный низкоуровневый сетевой механизм для
принудительного сброса состояния клиента, не связанный напрямую с игровой
логикой RTS.

## 3. Архитектурный паттерн: Client/Server Events + Registrar

Практически каждая крупная подсистема мода следует одному и тому же
трёхчастному паттерну:

```
<Subsystem>ClientEvents.java   — рендеринг, ввод, локальное состояние клиента
<Subsystem>ServerEvents.java   — авторитетная игровая логика на сервере
<Subsystem>ServerboundPacket.java / <Subsystem>ClientboundPacket.java — сеть между ними
```

Примеры: `Building{Client,Server}Events` + `BuildingServerboundPacket`/
`BuildingClientboundPacket`, `Resources{Client,Server}Events`, `Survival{Client,Server}Events`,
`Player{Server}Events` + `PlayerServerboundPacket`. Это классический
клиент-серверный паттерн Minecraft/Forge (клиент ничего не решает
авторитетно, только предсказывает/рисует; сервер — источник истины),
применённый последовательно ко всем RTS-механикам.

Единая точка регистрации всех обработчиков — `registrars/`:

| Registrar | Отвечает за |
|---|---|
| `EntityRegistrar` | Регистрация всех `EntityType<>` (юниты, снаряды и т.д.) |
| `AttributeRegistrar` | (наряду с `CommonModEvents`) атрибуты сущностей |
| `BlockRegistrar` / `BlockEntityRegistrar` | Кастомные блоки построек |
| `ItemRegistrar` | Предметы (в т.ч. production-иконки) |
| `SoundRegistrar` / `ParticleRegistrar` / `MobEffectRegistrar` / `EnchantmentRegistrar` | Ресурсы соответствующего типа |
| `GameRuleRegistrar` | Кастомные gamerule |
| `CommandArgumentRegistrar` | Аргументы для `/` команд мода |
| `ContainerRegistrar` | Кастомные UI-контейнеры (инвентари/меню) |
| `ClientEventRegistrar` / `ServerEventRegistrar` | Массовая регистрация всех `*ClientEvents`/`*ServerEvents` классов на Forge event bus, раздельно по `Dist` |
| `PacketHandler` | Регистрация сетевых пакетов в `SimpleChannel` |

## 4. Структура пакетов (по числу файлов)

```
unit/          244   — юниты: классы, ИИ (goals), модели, рендеринг, анимации, пути
building/      113   — здания: все фракции, размещение, производство, addon-постройки
ability/        97   — способности юнитов/зданий + геройские способности
mixin/          66   — низкоуровневые патчи ванильного кода Minecraft (Mixin)
research/       53   — древо исследований/апгрейдов
hud/            27   — весь игровой интерфейс (кнопки, панели, портреты, HUD-ресурсы)
blocks/         23   — кастомные блоки
entities/       17   — общая инфраструктура моделей/рендереров сущностей
resources/      16   — экономика (food/wood/ore), лимиты, синхронизация
registrars/     15   — регистрация всех типов контента Forge (см. §3)
survival/       14   — режим "выживание против волн ИИ"
player/         12   — состояние игрока, старт матча, действия
config/         10   — Forge ModConfig (баланс, клиентские настройки)
scenario/        9   — скриптуемые сценарии/кампании
tutorial/         8   — обучающий режим
fogofwar/         8   — туман войны
startpos/         7   — выбор стартовой позиции на карте
sandbox/          7   — песочница (безлимитные ресурсы, свободное тестирование)
rtsmap/            7   — генерация/работа с RTS-картой
hero/              7   — героические юниты (общая инфраструктура)
util/, enchantments/, debug/, alliance/, sounds/, playerprogression/,
gamerules/, gamemode/, commands/, time/, matchstart/, keybinds/,
guiscreen/, minimap/, healthbars/, faction/, attackwarnings/,
worldborder/, particles/, items/, orthoview/, network/, nether/,
cursor/, api/                                          — вспомогательные, каждый 1-9 файлов
```

## 5. Ключевые подсистемы

### 5.1 Фракции (`faction/`)

Минималистичный слой поверх остальной архитектуры — самой логики фракций
как объектов почти нет, это в основном enum + реестр зданий/производств:

```java
enum Faction { VILLAGERS, MONSTERS, PIGLINS, FORMIX, NEUTRAL, RANDOM, NONE }
```

`FactionRegistries` хранит по одному `FactionRegister` на активную фракцию
(включая `FORMIX`) и связывает здания с клавишами быстрого доступа
(`Keybindings.abilitySlotN`). Реальная логика "что доступно этой фракции"
размазана по множеству `switch (faction)` конструкций по всему проекту
(экраны выбора, sandbox, спавн стартовых юнитов и т.д.) — единой точки
интеграции нет, что явно задокументировано как источник багов в
`FORMIX_FACTION_LOG.md` (см. разделы 1.2 и 1.3 журнала — добавление новой
фракции требует grep по всему проекту, а не правки одного файла).

### 5.2 Юниты (`unit/`)

Крупнейший пакет. Разложен по ролям, а не по фракциям на верхнем уровне:

```
unit/
├── units/<фракция>/     — конкретные классы юнитов + *Prod (кнопки производства)
├── interfaces/          — Unit, WorkerUnit, AttackerUnit, RangedAttackerUnit,
│                          ConvertableUnit, HeroUnit, KeyframeAnimated, ArmSwingingUnit
├── goals/                — кастомные AI-goals (32 файла), включая "ручные"
│                          goal-объекты, тикаемые вручную из tick() юнита,
│                          а не через стандартный goalSelector
├── modelling/
│   ├── models/           — геометрия (Blockbench-экспорт или placeholder)
│   ├── renderers/        — связка модель+текстура
│   ├── animations/       — keyframe-анимации
│   └── layers/           — доп. рендер-слои (броня, эффекты)
├── controls/             — выделение/управление юнитами игроком
├── pathfinding/          — RTS-специфичный pathfinding поверх ваниль-навигации
└── packets/               — сетевые пакеты команд юнитам
```

**Иерархия юнита:** каждый боевой/рабочий юнит — это `extends <ваниль-класс
или Monster> implements Unit, <RoleInterfaces...>`. Два подхода к базовому
классу сосуществуют в проекте:
- Наследование от ваниль-моба (`GruntUnit extends Piglin`) — когда юнит
  визуально ре-текстур существующего моба.
- `extends Monster` (например `WraithUnit`, `FormixWorkerUnit`,
  `FormixWarriorUnit`) — когда юнит полностью кастомный (своя модель).

Интерфейс `Unit` (в `unit/interfaces/`) — центральная точка общей логики
(тик ресурсов/подбора лута через `checkAndPickupResources`, и т.п.),
реализуется практически всеми юнитами вне зависимости от базового класса.

### 5.3 Здания (`building/`)

```
building/
├── buildings/<фракция>/   — конкретные здания (у Formix — только FormixHive)
├── buildings/shared/       — общие абстрактные типы (AbstractFarm, AbstractMarket,
│                            AbstractBridge, AbstractStockpile) — переиспользуются
│                            между фракциями через наследование
├── buildings/placements/   — логика превью/размещения конкретных типов зданий
├── production/             — очередь производства юнитов (ProductionItems)
├── addon/                   — здания-надстройки/пристройки
├── custombuilding/          — пользовательские постройки (не для всех фракций)
└── data/                    — сохранение состояния зданий
```

Здание хранится как NBT-структура (`resources/structures/*.nbt`, дублируется
в `assets/` и `data/`), а не процедурная геометрия — HP здания
пропорционален количеству физически размещённых блоков (заявлено в README
как уникальная механика мода). `FormixHive` временно переиспользует
структуру `town_centre` от Villagers до появления собственного арта.

### 5.4 Способности (`ability/`)

```
ability/
├── Ability.java, Abilities.java        — базовая инфраструктура + реестр
├── abilities/<фракция>/                 — конкретные способности юнитов/зданий
├── heroAbilities/<герой>/               — способности конкретных геройских юнитов
│                                          (enchanter, necromancer, piglinmerchant,
│                                          royalguard, wildfire, wretchedwraith)
└── heroAbilities/shared/                — общая инфраструктура геройских способностей
```

Важная архитектурная деталь (задокументирована в проектном журнале
Formix): `UnitAction` enum-константы **переиспользуются между фракциями**
(например `CALL_TO_ARMS_UNIT` используется и Villagers, и Formix) — это
безопасно, потому что диспетчеризация идёт не через центральный switch по
`UnitAction`, а через конкретный объект `Ability`, прикреплённый к юниту.
`UnitAction` играет роль ID кнопки/сетевого пакета, не диспетчера логики.

### 5.5 Ресурсы и экономика (`resources/`)

Простая экономика на 3 ресурса (food/wood/ore), централизованная в классе
`Resources`. Два метода (`changeInstantly` — серверное начисление,
`changeOverTime` — клиентская анимация счётчика) — единственные точки,
где ресурсы физически меняются во всём проекте, что делает их удобным
местом для сквозной логики (например `MAX_RESOURCE_AMOUNT`-клэмпа,
добавленного для Formix). Sandbox-игроки (`sandbox/`) обходят лимиты через
явную проверку `SandboxServer.isSandboxPlayer(...)`.

### 5.6 Mixin-слой (`mixin/`, 66 файлов)

Отдельная от Forge-events категория интеграции — прямые байткод-патчи
ванильных классов Minecraft через MixinExtras/SpongePowered Mixin, когда
Forge-события недостаточны (например `PlayerMixin` — управление
`flying`/`noPhysics` для RTS top-down режима; `LivingEntityMixin`/`MobMixin`
— поведенческие правки; `mixin/fogofwar/*` — 8 файлов, патчащих рендер-
пайплайн клиента для тумана войны; `mixin/fire/*` — правки поведения огня).
Подключаются через `reignofnether.mixins.json`.

### 5.7 Survival-режим (`survival/`)

Отдельный игровой режим "выживание против волн монстров", со своей
парой Client/ServerEvents, `Wave`/`WaveDifficulty`/`WaveEnemy` — описание
волн атакующих, и `spawners/` — конкретная логика спавна. Formix пока
сознательно не подключён к системе волн (см. проектный журнал раздел 3.3).

### 5.8 HUD / UI (`hud/`, 27 файлов)

Вся отрисовка интерфейса поверх игры: кнопки способностей/производства
(`hud/buttons/`), портреты юнитов/зданий через реальный 3D-рендер модели
(`PortraitRendererUnit`/`PortraitRendererBuilding`), очередь производства,
дипломатия/наблюдатели (`hud/playerdisplay/`). `HudClientEvents` — самый
насыщенный класс здесь, диспетчеризует отрисовку разных панелей в
зависимости от того, какой Screen сейчас открыт у игрока (в т.ч. кастомная
логика для показа компактного счётчика ресурсов только в инвентаре, см.
Formix-журнал раздел 1.8/1.9).

### 5.9 Прочие геймплейные системы

- **`research/`** (53 файла) — древо апгрейдов, `researchItems/` — конкретные
  исследования по фракциям.
- **`fogofwar/`** — туман войны, завязан на mixin-патчи рендер-пайплайна.
- **`orthoview/`** — переключение в RTS top-down вид ("режим сверху").
- **`scenario/`** — скриптуемые кампании/миссии поверх RTS-движка.
- **`alliance/`** — союзы между игроками в матче.
- **`playerprogression/`** — прогрессия/уровни игрока между матчами.
- **`matchstart/`** — экран лобби выбора фракции перед стартом (`MatchStartScreen`).

## 6. Ассеты (`src/main/resources/assets/reignofnether/`)

```
textures/entities/     — текстуры 3D-моделей юнитов
textures/mobheads/      — маленькие иконки-портреты для кнопок производства
textures/icons/         — иконки ресурсов/UI
structures/              — .nbt-структуры зданий (форма из блоков)
lang/en_us.json          — все игровые строки (Crowdin используется для остальных языков)
```

## 7. Заметки для дальнейшей работы

- **Нет единой точки "фракция → всё, что для неё нужно".** Интеграция
  новой фракции (как Formix) требует правок в 15+ разрозненных местах
  (registrars, HUD, sandbox, лобби, стартовые события) — это
  подтверждённый источник багов в истории проекта (см.
  `FORMIX_FACTION_LOG.md`). При любой работе с фракциями — грепать по
  всему проекту, не полагаться на "очевидные" точки интеграции.
- **`unit/goals/`** содержит как обычные Minecraft `Goal` (через
  `goalSelector.addGoal`), так и "ручные" goal-объекты, тикаемые прямым
  вызовом внутри `tick()` юнита. Не смешивать паттерны при копировании.
- **Компиляция недоступна в песочнице Claude** — нет сетевого доступа к
  Maven-репозиториям Forge. Любые правки в этом проекте проверяются
  статически (сверка сигнатур через `grep`/`view`), не живым `./gradlew build`.
- Полная историческая детализация по конкретной фракции Formix (что
  сделано, что временное/placeholder, известные риски) — в
  `PROJECT_NOTES/FORMIX_FACTION_LOG.md`, раздел "0. АКТУАЛЬНОЕ СОСТОЯНИЕ".
