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

- [Features](#features)
- [Requirements](#requirements)
- [Building](#building)
- [Contributing](#contributing)
- [Links](#links)
- [License](#license)

## Features

- **ME Smelter** — a network-connected machine that pulls furnace-recipe inputs from ME storage and returns the results, in four tiers (Default, Mk1, Mk2, Mk3) with increasing speed, efficiency, and queue capacity.
- **In-place tier upgrades** — upgrade a placed smelter with a craftable kit chain (Template → Mk1 → Mk2 → Mk3). Upgrading keeps everything the machine was doing: pinned recipe, progress, fuel, cards, and its place in the network queue.
- **ME Smelting Terminal** — network-wide control with a shared recipe queue, live progress, fuel selection from the network or your own inventory, and per-machine status.
- **Flexible power** — each smelter runs on conventional furnace fuel or AE power, your choice per machine.
- **Upgrade cards** — Acceleration, Energy, Fuel Efficiency, Capacity, and Redstone cards, using AE2's standard upgrade slot system.
- **Status glow** — smelters show a live status light, styled after AE2's own machines, so you can see at a glance which are running, idle, blocked, or offline.
- **Configurable balance** — tier stats (speed, power draw, fuel efficiency, upgrade slots, queue capacity) are adjustable through the mod config.

See the [wiki](https://github.com/ColinMoulds/Applied-Smelting/wiki) for detailed usage guides.

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
- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/applied-smelting)
- [Changelog](CHANGELOG.md)
- [Issues](https://github.com/ColinMoulds/Applied-Smelting/issues)
- [Contributing](CONTRIBUTING.md)
<!-- - [Discord](https://discord.gg/INVITE) -->

## License

Applied Smelting code is licensed under the [GNU Lesser General Public License v3.0](LICENSE). The accompanying GNU GPLv3 text is provided in [COPYING](licenses/COPYING).

Original textures, models, logos, and other visual assets are licensed under [Creative Commons Attribution-NonCommercial-ShareAlike 3.0](licenses/LICENSE-ASSETS). See [NOTICE](licenses/NOTICE) for the exact scope and attribution information.
