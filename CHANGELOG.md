# Changelog

## 0.1.2

- Centred the furnace input, flame, and fuel layout with additional vertical spacing.
- Replaced the custom flame artwork with Minecraft's standard animated furnace flame sprite.
- Fixed the furnace panel rendering order so status and progress information remain visible.
- Added current stored output reporting and configurable output targets.
- Moved power and target controls to a dedicated AE2-style settings screen inspired by the ME Drive priority screen.
- Replaced Pause/Run with AE2's native redstone ON/OFF toggle and added priority-style target adjustment controls.

## 0.1.1

- Added live smelting progress to the ME Smelting Terminal.
- Added a furnace-style fuel flame that drains with the smelters' current fuel charge.
- Added machine status reporting for paused, offline, missing selection, missing input, missing fuel, missing AE power, full storage, target reached, invalid recipe, and active smelting states.
- Changed the active-machine count to report machines that are actually processing.
- Added aggregate progress reporting for networks with multiple ME Smelters.

## 0.1.0

- Added the ME Smelter with AE2 grid connectivity and channel usage.
- Added automatic furnace-recipe discovery from items in ME storage.
- Added AE-powered processing and safe output insertion back into ME storage.
- Added the ME Smelting Terminal with network-wide pause/resume control and status.
- Added original 16x16 block and item artwork for both machines.
- Added Minecraft 26.1.2 block, item, loot-table, recipe, model, and language resources.
