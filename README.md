# Attack on Titan — Minecraft Modpack

An Attack on Titan modpack for **Minecraft 1.21.1 (Fabric)**, built around [Danny's AoT](https://www.curseforge.com/minecraft/mc-mods/dannys-aot), plus two custom pieces made for it:

| Folder | What it is |
|---|---|
| [`titan-mastery/`](titan-mastery) | **Titan Mastery**, a Fabric add-on mod for Danny's AoT. New shifters can't control their Titan and it rages on its own; control grows the more you use each Titan, unlocking a mastery ability and form for all nine. |
| [`founding-titan-datapack/`](founding-titan-datapack) | **Founding Titan** datapack: the Coordinate, Titan Form, Kneel and the Rumbling. |
| [`modpack/`](modpack) | The CurseForge modpack manifest. |
| [`releases/`](releases) | Ready-to-use files: **`AttackOnTitan-BeyondTheWalls-2.0.1.zip`** (the modpack), `titanmastery-2.0.1.jar`, and the standalone `founding-titan.zip`. |

## Install (one click, CurseForge app)

1. Download [`releases/AttackOnTitan-BeyondTheWalls-2.0.1.zip`](releases/AttackOnTitan-BeyondTheWalls-2.0.1.zip).
2. In CurseForge: **Minecraft → Create Custom Profile → Import** → pick the zip.
3. Set memory to **6–8 GB** (**Settings → Minecraft → Java Settings**) and play.

The modpack downloads Danny's AoT and its libraries from CurseForge unchanged, adds performance and quality-of-life mods (Sodium, Lithium, FerriteCore, Entity Culling, Mod Menu, Jade, JEI), and includes Titan Mastery with the **Founding Titan datapack built in**: it's active in every world, with nothing to install separately.

**Keys:** Titan Mastery uses **J** (mastery ability) and **U** (mastery form), chosen so they don't clash with Danny's controls (G/H are the ODM hooks). Rebind them under *Controls → Titan Mastery*.

### Optional extras

The modpack keeps to a lean, tested core. These fit well on top (add them with **Add More Content**):

**Core**: Danny's AoT · Fabric API · GeckoLib · Player Animation Library · AAA Particles · Titan Mastery (this repo)

**Combat & content (optional)**: SpringDAOT: All Features (don't combine with other SpringDAOT add-ons) · Better Combat

**World**: ChoiceTheorem's Overhauled Village · Towns and Towers · YUNG's Better Dungeons / Mineshafts · Terralith · Farmer's Delight Refabricated · Supplementaries · Carry On · Waystones · Traveler's Backpack

**Performance**: Sodium · Lithium · FerriteCore · ModernFix · Entity Culling · ImmediatelyFast

**Visuals & sound**: Iris + Complementary Reimagined · Distant Horizons · Sound Physics Remastered · AmbientSounds · Simple Voice Chat

**Tools**: JEI · Jade · Mod Menu · AppleSkin · Xaero's Minimap & World Map

## Titan Mastery

Each Titan has its own mastery, earned by using it: **1 XP per second shifted, 10 per kill, 40 per player or shifter kill.**

| Stage | XP | Control |
|---|---|---|
| Feral | 0 | None. Every shift the Titan rampages for 30 s, attacking the nearest anything (friends too), dragging your camera, refusing to revert, then collapses and ejects you exhausted. |
| Unstable | 200 | You steer, but rage can take over on shifting (40%) or at low health. Danny's Titan abilities stay locked. |
| Awakened | 600 | Abilities unlocked; rage is rare. |
| Disciplined | 1500 | Full control. **Mastery ability (J).** |
| Mastered | 3000 | **Mastery form (U).** |
| Perfected | 6000 | Forms last twice as long; peak stats. |

Every stage also adds ~8% damage dealt, ~5% damage resisted, and Titan regeneration from Awakened on.

| Titan | Ability (J) | Form (U) |
|---|---|---|
| Attack | War Cry | Hardened Attack Titan |
| Colossal | Steam Burst | Scalding Steam |
| Armored | Battering Ram | Full Plating |
| Female | Crystal Strike | Crystal Cocoon |
| Beast | Rock Barrage | Pitcher's Stance |
| War Hammer | Spike Eruption | Crystal Core |
| Jaw | Lunge Bite | Frenzy |
| Cart | Panzer Fire | Endurance March |
| Founding | Coordinate | Paths of Ymir |

**Commands:** `/mastery` · `/mastery show <player>` · ops: `/mastery set|add <players> <titan> <xp>`, `/mastery fury <players> <seconds>`, `/mastery founding <players>`.

### The Founding Titan

Danny's AoT has a Founding Titan, but its code only lets his dev team grant it. Titan Mastery adds its own:

- **Born with it:** a player with royal blood has a 25% chance to be born the Founder, if nobody holds it. Only one Founder exists. When they die, a serum drops where they fell; drinking it passes the power on. Ops: `/mastery founding <player>`, `/mastery founding release`, `/mastery founding chance <0-100>`.
- **Transform with O**, like any shifter. With **SpringDAOT** installed you become its 70-block **Ymir Founding Titan** through Danny's own shift; without it (or with `/mastery founding model original`, or as a Marleyan, whom Danny never grants Titan powers) you become Titan Mastery's own 20-block Founding Titan.
- **Powers** (unlocked by Founding mastery): **J Coordinate** stops every titan and shifter within 96 blocks (Commanded: no moving, punching, abilities or reverting) and freezes pure titans; **U Paths of Ymir** heals and strengthens everyone near you; **P The Rumbling** sends Wall Titans marching where you face.
- **Rage:** a new Founder's Titan runs wild until mastery grows. With SpringDAOT, its own "uncontrolled first transformation" game rule is switched off once per world so the two don't stack (`/gamerule uncontrolledFirstTransformation true` brings it back).
- All keys can be rebound in *Options → Controls → Key Binds → Titan Mastery*.
Mastery XP is stored in scoreboard objectives `tm_attack`, `tm_colossal`, … so it saves with the world.

### Building from source

The source is written against Fabric **intermediary** names (`class_1297`, `method_5643`, …) and compiles directly against the jars Fabric runs, so Loom uses intermediary as its mappings.

```
cd titan-mastery
gradle build      # output: build/libs/titanmastery-<version>.jar
```

Hooks into Danny's AoT 2.5.1.1 (`AttackTitanEntity`, `ColossalTitanEntity`, `ArmoredTitanEntity`, `FemaleTitanEntity`, `BeastTitanEntity`, `WarhammerTitanEntity`, plus the Jaw, Cart and Founding subclasses). If Danny renames those methods in a future update, the mixins in `titanmastery/mixin` need updating.

## Credits

- **Danny's AoT** by Danny7, the base mod everything here builds on.
- Attack on Titan is © Hajime Isayama / Kodansha. This is a non-commercial fan project.
