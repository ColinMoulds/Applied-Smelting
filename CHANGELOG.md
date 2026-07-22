# Changelog

## 0.2.0

- Added a capacity-controlled network smelting queue with round-robin, least-assigned recipe scheduling across connected smelters.
- Added pending-output reservations so parallel smelters respect storage targets without overproducing.
- Added optional per-smelter recipe pinning by right-clicking an ME Smelter with a smeltable item; repeat the action to return it to the network queue.
- Added Active/Connected counts, combined processing speed, queue usage, and combined AE consumption reporting.
- Split smelting heat into Item Fuel and AE Power modes. Item Fuel mode uses furnace fuels; AE Power mode consumes network energy directly.
- Added support for Energy Cards, Capacity Cards, Redstone Cards, and the new Fuel Efficiency Card alongside Acceleration Cards.
- Energy Cards reduce idle and AE-fuel consumption by 15% each, down to 40% of the base cost.
- Fuel Efficiency Cards extend furnace fuel duration by 25% each, up to 200%.
- Capacity Cards add network recipe queue slots, up to nine total queued recipes.
- Redstone Cards require a high redstone signal for the installed smelter to operate.
- Expanded ME Smelters to eight mixed upgrade slots and redesigned their interface with power, recipe, upgrade, and energy telemetry.

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
