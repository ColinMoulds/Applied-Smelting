# Applied Smelting

[![Build](https://github.com/ColinMoulds/Applied-Smelting/actions/workflows/build.yml/badge.svg)](https://github.com/ColinMoulds/Applied-Smelting/actions/workflows/build.yml)
[![Latest Release](https://img.shields.io/github/v/release/ColinMoulds/Applied-Smelting?include_prereleases&label=release)](https://github.com/ColinMoulds/Applied-Smelting/releases)
![Minecraft](https://img.shields.io/badge/Minecraft-26.1.2-blue)
![NeoForge](https://img.shields.io/badge/NeoForge-26.1.2.80%2B-orange)
![Applied Energistics 2](https://img.shields.io/badge/AE2-26.1.10--beta%2B-8A2BE2)
[![Modrinth](https://img.shields.io/modrinth/dt/WF9YE7g7?logo=modrinth&label=Modrinth)](https://modrinth.com/mod/WF9YE7g7)
[![License](https://img.shields.io/badge/license-LGPL--3.0-informational)](LICENSE)

Applied Smelting is an Applied Energistics 2 addon for Minecraft 26.1.2. It adds network-connected machines for smelting, blasting, smoking, and melting metals directly from ME storage, plus an **ME Smelting Terminal** for network-wide control and status.

> Turn your ME network into a configurable, tiered processing system without managing machine input and output inventories.

## Table of Contents

- [Features](#features)
- [Requirements](#requirements)
- [Building](#building)
- [Contributing](#contributing)
- [Links](#links)
- [License](#license)

## Features

- **Four machine families** — the ME Smelter processes furnace recipes, the ME Blast Furnace handles blasting recipes, the ME Smoker cooks smoking recipes, and the ME Crucible turns supported ores and raw metals into molten fluids.
- **Four tiers per machine** — Default, Mk1, Mk2, and Mk3 variants provide increasing speed, efficiency, and queue capacity.
- **In-place tier upgrades** — use the universal upgrade-kit chain (Template → Mk1 → Mk2 → Mk3) on a placed machine. Pinned recipes, progress, fuel, cards, power mode, and network assignment are preserved.
- **ME Smelting Terminal** — control four independent Smelting, Blasting, Smoking, and Crucible queues from one terminal, with live progress, output targets, fuel selection, and aggregate machine status.
- **Three power modes** — each machine can use conventional furnace fuel, AE power, or lava drawn from ME fluid storage.
- **Upgrade cards** — Acceleration, Energy, Fuel Efficiency, Capacity, and Redstone cards, using AE2's standard upgrade slot system.
- **Pinned processing** — right-click a machine with a valid input to dedicate it to that recipe instead of the shared queue.
- **Status glow and Jade support** — machines show an AE2-style status light, while optional Jade tooltips expose status, input, progress, and fuel without opening a menu.
- **JEI integration** — optional JEI support exposes Crucible recipes and registers every machine tier as a recipe catalyst.
- **Molten-metal compatibility** — the Crucible uses fluids from common `c:molten_<metal>` tags when another mod provides them and falls back to its own network-only fluids.
- **Configurable balance** — tier stats (speed, power draw, fuel efficiency, upgrade slots, queue capacity) are adjustable through the mod config.

See the [wiki](https://github.com/ColinMoulds/Applied-Smelting/wiki) for detailed usage guides.

## Requirements

| | |
|---|---|
| Minecraft | 26.1.2 |
| NeoForge | 26.1.2.80 or newer compatible 26.1 build |
| Applied Energistics 2 | 26.1.10-beta or newer compatible 26.1 build |
| Java | 25 |
| Optional integrations | JEI 29.16.0.47+, Jade 26.1.0+ |

## Building

Run `./gradlew build` (`gradlew.bat build` on Windows). Release artifacts are written to `build/libs`.

## Contributing

Contributions are welcome. See [CONTRIBUTING.md](CONTRIBUTING.md) for setup, coding guidelines, and the PR process.

## Links

- [Modrinth](https://modrinth.com/mod/WF9YE7g7)
- [Changelog](CHANGELOG.md)
- [Issues](https://github.com/ColinMoulds/Applied-Smelting/issues)
- [Contributing](CONTRIBUTING.md)
<!-- - [Discord](https://discord.gg/INVITE) -->

## License

Applied Smelting code is licensed under the [GNU Lesser General Public License v3.0](LICENSE). The accompanying GNU GPLv3 text is provided in [COPYING](licenses/COPYING).

Original textures, models, logos, and other visual assets are licensed under [Creative Commons Attribution-NonCommercial-ShareAlike 3.0](licenses/LICENSE-ASSETS). See [NOTICE](licenses/NOTICE) for the exact scope and attribution information.
