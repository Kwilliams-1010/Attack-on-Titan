# Attack on Titan — Minecraft Modpack

An Attack on Titan modpack for **Minecraft 1.21.1 (Fabric)**, built around [Danny's AoT](https://www.curseforge.com/minecraft/mc-mods/dannys-aot), plus two custom pieces made for it:

| Folder | What it is |
|---|---|
| [`titan-mastery/`](titan-mastery) | **Titan Mastery**, a Fabric add-on mod for Danny's AoT. New shifters can't control their Titan and it rages on its own; control grows the more you use each Titan, unlocking a mastery ability and form for all nine. |
| [`founding-titan-datapack/`](founding-titan-datapack) | **Founding Titan** datapack: the Coordinate, Titan Form, Kneel and the Rumbling. |
| [`releases/`](releases) | Ready-to-use builds: `titanmastery-1.0.0.jar` and `founding-titan.zip`. |

## Install (CurseForge app)

1. **Create Custom Profile** → Fabric, Minecraft **1.21.1**. Set memory to 8 GB.
2. Add the mods below with **Add More Content**.
3. Drop `releases/titanmastery-1.0.0.jar` into the profile's `mods` folder (**⋯ → Open Folder**).
4. Optional: put `releases/founding-titan.zip` in `saves/<world>/datapacks`.

### Mod list

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
| Disciplined | 1500 | Full control. **Mastery ability (G).** |
| Mastered | 3000 | **Mastery form (H).** |
| Perfected | 6000 | Forms last twice as long; peak stats. |

Every stage also adds ~8% damage dealt, ~5% damage resisted, and Titan regeneration from Awakened on.

| Titan | Ability (G) | Form (H) |
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

**Commands:** `/mastery` · `/mastery show <player>` · ops: `/mastery set|add <players> <titan> <xp>`, `/mastery fury <players> <seconds>`.
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
