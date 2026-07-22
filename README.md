# Applied Smelting

[![Build](https://github.com/ColinMoulds/Applied-Smelting/actions/workflows/build.yml/badge.svg)](https://github.com/ColinMoulds/Applied-Smelting/actions/workflows/build.yml)
[![Latest Release](https://img.shields.io/github/v/release/ColinMoulds/Applied-Smelting?include_prereleases&label=release)](https://github.com/ColinMoulds/Applied-Smelting/releases)
![Minecraft](https://img.shields.io/badge/Minecraft-26.1.2-blue)
![NeoForge](https://img.shields.io/badge/NeoForge-26.1.2.80%2B-orange)
![Applied Energistics 2](https://img.shields.io/badge/AE2-26.1.10--beta%2B-8A2BE2)
[![License](https://img.shields.io/badge/license-LGPL--3.0-informational)](LICENSE)
<!-- [![Modrinth](https://img.shields.io/modrinth/dt/PROJECT_ID?logo=modrinth&label=Modrinth)](https://modrinth.com/mod/PROJECT_ID) -->
[![CurseForge](https://cf.way2muchnoise.eu/PROJECT_ID.svg)](https://www.curseforge.com/minecraft/mc-mods/applied-smelting)

Applied Smelting is an Applied Energistics 2 addon for Minecraft 26.1.2. It adds an **ME Smelter** that processes furnace recipes directly from ME storage and an **ME Smelting Terminal** for network-wide control and status.

> Smelt items directly from your ME network with a dedicated network-powered smelter and control terminal.

## Table of Contents

- [Beta scope](#beta-scope)
- [Requirements](#requirements)
- [Building](#building)
- [Contributing](#contributing)
- [Links](#links)
- [License](#license)

## Beta scope

Version `0.2.0` is an early beta. Connected ME Smelters coordinate work through a capacity-controlled recipe queue, reserve pending outputs against storage targets, and distribute queued recipes across the network. Individual machines can be pinned to a recipe by right-clicking them with a smeltable item, or left on the shared queue.

Each smelter can use conventional furnace items or AE power as its heat source. The machine and terminal interfaces report idle AE use, maximum AE-fuel use, active and connected machines, combined processing speed, and queue capacity. Eight mixed upgrade slots support Acceleration, Energy, Capacity, Redstone, and Fuel Efficiency Cards.

- Acceleration Cards double speed per card, up to 16x.
- Energy Cards reduce idle and AE-fuel consumption.
- Fuel Efficiency Cards extend the operations supplied by each furnace fuel item.
- Capacity Cards add recipes to the shared network queue, up to nine recipes.
- Redstone Cards make the installed smelter operate only while receiving a high signal.

Recipe-browser integration and more advanced per-job controls are planned for later beta releases.

## Requirements

| | |
|---|---|
| Minecraft | 26.1.2 |
| NeoForge | 26.1.2.80 or newer compatible 26.1 build |
| Applied Energistics 2 | 26.1.10-beta or newer compatible 26.1 build |
| Java | 25 |

## Building

Run `./gradlew build` (`gradlew.bat build` on Windows). Release artifacts are written to `build/libs`.

## Contributing

Contributions are welcome. See [CONTRIBUTING.md](CONTRIBUTING.md) for setup, coding guidelines, and the PR process.

## Links

<!-- Fill in and uncomment as these become available. -->
<!-- - [Modrinth](https://modrinth.com/mod/PROJECT_ID) -->
- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/applied-smelting) -->
<!-- - [Discord](https://discord.gg/INVITE) -->
- [Changelog](CHANGELOG.md)
- [Issues](https://github.com/ColinMoulds/Applied-Smelting/issues)
- [Contributing](CONTRIBUTING.md)

## License

Applied Smelting code is licensed under the [GNU Lesser General Public License v3.0](LICENSE). The accompanying GNU GPLv3 text is provided in [COPYING](licenses/COPYING).

Original textures, models, logos, and other visual assets are licensed under [Creative Commons Attribution-NonCommercial-ShareAlike 3.0](licenses/LICENSE-ASSETS). See [NOTICE](licenses/NOTICE) for the exact scope and attribution information.
