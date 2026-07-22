# Changelog

## 0.2.0

- Multiple ME Smelters can now work together from one shared list of recipes, so several machines can smelt different items from your ME network at the same time.
- Added a network smelting queue to the ME Smelting Terminal — queue up recipes and watch them get worked on live.
- Redesigned the terminal with a simple grid: see everything queued, what each one produces, and which ones are actively being smelted at a glance.
- Added a dedicated Fuel tab for picking your smelting fuel, right from ME storage or straight out of your own inventory — works with any pack's custom fuels, not just vanilla.
- You can now pin a specific recipe to one ME Smelter by right-clicking it while holding a smeltable item, so that machine always works that recipe instead of pulling from the shared queue.
- Smelters can now run on AE power instead of furnace fuel, your choice per machine.
- Added Energy Cards (less power use), Fuel Efficiency Cards (fuel lasts longer), Capacity Cards (bigger shared queue), and Redstone Cards (only run on a redstone signal), joining the existing Acceleration Cards.
- ME Smelters now have 8 upgrade slots instead of 4, with a cleaner status display.
- Fixed smelters occasionally hopping between queued recipes instead of sticking with one until it runs out or hits its target.

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
