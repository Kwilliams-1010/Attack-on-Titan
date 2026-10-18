# Founding Titan — datapack for Danny's AOT

The ninth Titan for a Danny's AOT world. Minecraft Java **1.21.1** (Fabric), works next to Danny's AOT without touching it.

## Install
1. Create the world (or open an existing one) → **Data Packs** → drop `founding-titan.zip` into the folder → move it to the "Selected" side.
2. Turn **Allow Commands** on if you want the op shortcuts below.
3. In-game, run `/function founding:help`.

## Becoming the Founder
Craft a **Founding Titan Serum** and drink it:

|              |                   |              |
|--------------|-------------------|--------------|
|              | Totem of Undying  |              |
| Echo Shard   | Nether Star       | Echo Shard   |
|              | Honey Bottle      |              |

Drinking it makes you the Founder and gives you **the Coordinate** (glowing item).
- Only one Founder exists at a time. If someone else drinks a serum, the old Founder loses the power, even if they were offline.
- When the Founder dies, the power is released as a serum where they fell. It never despawns and can't burn.
- Lost the Coordinate? `/trigger founding.item`.

## Abilities
Hold the Coordinate. **Right-click** = use, **sneak + right-click** = switch.

| Ability | What it does | Recharge |
|---|---|---|
| **Titan Form** | Grow ~12x (about 22 blocks tall) for 2 minutes: +180 health, heavy punches, armor, long reach, steps over walls. Right-click again to return early. | 30 s |
| **Coordinate: Kneel** | Every mob within 64 blocks (Danny's pure titans included) freezes and glows for 30 seconds. Nearby players are staggered. | 45 s |
| **The Rumbling** | Seven ~48-block Wall Titans rise in a line ahead of you and march where you face for 60 seconds, crushing mobs underfoot. | 5 min |

The Founder also regenerates slowly at all times.

## Settings (ops)
`/function founding:config/show` — click to toggle:
- **Wall Titans destroy terrain** (default OFF). Heavy on slower PCs.
- **Wall Titans spare other players** (default ON). Turn off for the true Rumbling.

## Notes
- Wall Titans are giants, so they vanish on **Peaceful** difficulty.
- Use one kind of shifting at a time: if you already hold a Danny's AOT shifter, revert from it before using Titan Form.
- Removing the pack: run `/function founding:uninstall` first, then delete it and `/reload`.
- Op shortcuts: `/function founding:give/serum`, `/function founding:give/power`.
