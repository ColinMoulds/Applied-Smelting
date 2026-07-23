# Changelog

## 0.4.2

- Added Mk1, Mk2, and Mk3 tiers for the ME Smoker, upgradeable in place the same way as the ME Smelter and ME Blast Furnace.

## 0.4.1

- Added Mk1, Mk2, and Mk3 tiers for the ME Blast Furnace, upgradeable in place the same way as the ME Smelter (craft an ME Machine Upgrade Template, then Mk1/Mk2/Mk3 Upgrade Kits, shift-right-click to apply).
- The Upgrade Template and Mk1-3 Upgrade Kits are now universal - the same items work on any upgradeable machine, not just the ME Smelter.
- Rebalanced the ME Smelter and ME Blast Furnace tier progression so Mk1 is a real step up in speed/efficiency instead of matching the base tier's stats.

## 0.4.0

- Added the ME Blast Furnace, a Blast Furnace-recipe machine with its own independent network queue — twice as fast as the ME Smelter for ore-style recipes.
- Added the ME Smoker, a Smoker-recipe machine with its own independent network queue, for fast food cooking.
- The ME Smelting Terminal now has separate tabs for Smelting, Blasting, and Smoking, so you can manage all three machines' queues from one terminal.
- Sky Dust and Sky Stone Block can now be processed in the ME Blast Furnace, not just a regular furnace.
- Added a third power option: machines can now draw lava straight from your ME network's fluid storage, alongside furnace fuel and AE power.
- Added the ME Crucible, a new machine that melts ore and raw metal into molten metal and stores it directly in your ME network's fluid storage. Uses Productive Metalworks's fluids if that mod is installed, or its own molten iron/copper/gold otherwise.
- Fixed the status light on all machines showing on the wrong face when placed facing east or west.
- Fixed machines showing "waiting for a recipe" instead of "missing fuel" when they had something queued but no power or fuel available.

## 0.3.0

- Added three higher tiers of ME Smelter — Mk1, Mk2, and Mk3 — sitting alongside the original as separate blocks with higher base speed, more upgrade slots, lower power draw, better fuel efficiency, and more network queue capacity the further you go.
- Added a crafting chain to upgrade a placed ME Smelter in place: craft a Smelter Upgrade Template, then a Mk1 Upgrade Kit, then a Mk2 Upgrade Kit, then a Mk3 Upgrade Kit, each built from the previous one. Shift-right-click a smelter with the matching kit to upgrade it.
- Upgrading a smelter keeps everything it was doing — installed cards, pinned recipe, in-progress smelting, fuel, and its place in the network queue all carry over, nothing resets.
- ME Smelters (all tiers) now glow with AE2-style cyan conduit lines and status lights, brighter and more pronounced on higher tiers.
- Tier stats (speed, power draw, fuel efficiency, upgrade slots, queue capacity) are now configurable, for modpack authors who want to rebalance them.

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
