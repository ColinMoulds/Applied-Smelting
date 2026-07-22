<!--
  Banner: drop a 1280x320-ish banner (mod name, ME Smelter render, ME network aesthetic)
  at docs/images/banner.png and uncomment the line below.
-->
<!-- ![Applied Smelting banner](docs/images/banner.png) -->

# Applied Smelting

<!--
  Badge notes:
  - Build badge is live once .github/workflows/build.yml runs on this repo.
  - Release badge is live once a GitHub Release/tag exists (currently none — see status below).
  - Swap OWNER/REPO if the repo is ever renamed or transferred.
  - Modrinth/CurseForge badges: uncomment once the mod has a project page there.
-->
[![Build](https://github.com/ColinMoulds/Applied-Smelting/actions/workflows/build.yml/badge.svg)](https://github.com/ColinMoulds/Applied-Smelting/actions/workflows/build.yml)
[![Latest Release](https://img.shields.io/github/v/release/ColinMoulds/Applied-Smelting?include_prereleases&label=release)](https://github.com/ColinMoulds/Applied-Smelting/releases)
![Minecraft](https://img.shields.io/badge/Minecraft-26.1.2-blue)
![NeoForge](https://img.shields.io/badge/NeoForge-26.1.2.80%2B-orange)
![Applied Energistics 2](https://img.shields.io/badge/AE2-26.1.10--beta%2B-8A2BE2)
[![License](https://img.shields.io/badge/license-LGPL--3.0-informational)](LICENSE)
<!-- [![Modrinth](https://img.shields.io/modrinth/dt/PROJECT_ID?logo=modrinth&label=Modrinth)](https://modrinth.com/mod/PROJECT_ID) -->
<!-- [![CurseForge](https://cf.way2muchnoise.eu/PROJECT_ID.svg)](https://www.curseforge.com/minecraft/mc-mods/PROJECT_ID) -->

Applied Smelting is an Applied Energistics 2 addon for Minecraft 26.1.2. It adds an **ME Smelter** that processes furnace recipes directly from ME storage and an **ME Smelting Terminal** for network-wide control and status.

> Smelt items directly from your ME network with a dedicated network-powered smelter and control terminal.

<!--
  Screenshots/GIFs: drop images in docs/images/ and uncomment the rows you fill in.
  Good candidates: ME Smelter block in-world, ME Smelter GUI (status panel + upgrades),
  ME Smelting Terminal in queue mode, settings screen.
-->
<!--
| ME Smelter | ME Smelting Terminal |
|:---:|:---:|
| ![ME Smelter in-world](docs/images/me-smelter-inworld.png) | ![ME Smelting Terminal](docs/images/terminal-queue.png) |
-->

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

> **Release status:** `0.2.0` is committed on `main` but not yet tagged or released while multi-smelter scheduling, power modes, and the new queue UI go through in-game testing. The latest published release is `0.1.2` (prerelease).

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
<!-- - [CurseForge](https://www.curseforge.com/minecraft/mc-mods/PROJECT_ID) -->
<!-- - [Discord](https://discord.gg/INVITE) -->
- [Changelog](CHANGELOG.md)
- [Issues](https://github.com/ColinMoulds/Applied-Smelting/issues)
- [Contributing](CONTRIBUTING.md)

## License

Applied Smelting code is licensed under the [GNU Lesser General Public License v3.0](LICENSE). The accompanying GNU GPLv3 text is provided in [COPYING](COPYING).

Original textures, models, logos, and other visual assets are licensed under [Creative Commons Attribution-NonCommercial-ShareAlike 3.0](LICENSE-ASSETS). See [NOTICE](NOTICE) for the exact scope and attribution information.
