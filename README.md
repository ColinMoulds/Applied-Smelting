# Applied Smelting

Applied Smelting is an Applied Energistics 2 addon for Minecraft 26.1.2. It adds an ME Smelter that processes furnace recipes directly from ME storage and an ME Smelting Terminal for network-wide control and status.

> Smelt items directly from your ME network with a dedicated network-powered smelter and control terminal.

## Beta scope

Version `0.1.2` is an early beta. Smelters process selected furnace inputs from their AE2 network, consume selected fuel and AE power, and return completed outputs directly to storage. The terminal supports input and fuel selection, priority-style target stock controls, stored-output reporting, network-wide power controls, active-machine status, and live aggregate progress. ME Smelters accept up to four acceleration cards for speeds up to 16x.

Recipe-browser integration, expanded upgrades, and advanced parallel job controls are planned for later beta releases.

## Requirements

- Minecraft 26.1.2
- NeoForge 26.1.2.80 or newer compatible 26.1 build
- Applied Energistics 2 26.1.10-beta or newer compatible 26.1 build
- Java 25

## Building

Run `./gradlew build` (`gradlew.bat build` on Windows). Release artifacts are written to `build/libs`.

## License

Applied Smelting code is licensed under the [GNU Lesser General Public License v3.0](LICENSE). The accompanying GNU GPLv3 text is provided in [COPYING](COPYING).

Original textures, models, logos, and other visual assets are licensed under [Creative Commons Attribution-NonCommercial-ShareAlike 3.0](LICENSE-ASSETS). See [NOTICE](NOTICE) for the exact scope and attribution information.
