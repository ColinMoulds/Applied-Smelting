# Contributing to Applied Smelting

Thanks for your interest in contributing. Applied Smelting is an early-beta Applied Energistics 2 addon, so expect APIs, recipes, and NBT layouts to change between releases.

## Before you start

- For anything beyond a small fix, open an issue first (or comment on an existing one) so the change can be discussed before you put work into it.
- Check [CHANGELOG.md](CHANGELOG.md) and open issues/PRs to avoid duplicating work already in progress.
- This project targets **Minecraft 26.1.2**, **NeoForge 26.1.2.80+**, and **Applied Energistics 2 26.1.10-beta+**. PRs that require bumping any of these should call that out explicitly and explain why.

## Development setup

- Requires **Java 25**.
- Build with `./gradlew build` (`gradlew.bat build` on Windows). Artifacts are written to `build/libs`.
- Launch a client/server test instance with the `client` / `server` Gradle runs (see `build.gradle.kts`), or via your IDE's NeoForge run configurations.
- Run `./gradlew test` before opening a PR. Pure logic (tier math, etc.) belongs in a plain JUnit test under `src/test`; anything that needs real registries, a `Level`, or a `RecipeManager` should use NeoForge's test framework (`@ExtendWith(EphemeralTestServerProvider.class)`, see `ModRegistriesTest`) instead of hand-waving it with mocks. Automated tests don't replace in-game testing for anything UI- or timing-related — please still describe what you tested manually (and how) in your PR description.
- Run `./gradlew spotlessApply` before committing; CI runs `spotlessCheck` and will fail the build on unformatted code.

## Making changes

- Keep PRs focused. Prefer several small, reviewable PRs over one large one that mixes unrelated changes.
- Follow the existing code style and package layout under `src/main/java/dev/excal1bur/appliedsmelting/`.
- Validate any JSON resources you touch (recipes, models, screen layouts, lang files) and run `git diff --check` before opening a PR.
- Update `CHANGELOG.md` under an "Unreleased" heading (create one if it doesn't exist) describing your change from a player's perspective.
- If your change affects saved data (block entities, upgrade inventories, queue state, etc.), make sure existing worlds still load correctly — add migration handling if the on-disk shape changes.

## Submitting a pull request

1. Fork the repo and create a branch from `main`.
2. Make your changes and confirm `./gradlew build` succeeds.
3. Open a PR describing the change, the motivation, and how you tested it (include screenshots/GIFs for UI changes where possible).
4. Be responsive to review feedback — this is a small, actively developed project and maintainers may ask for adjustments before merging.

## Reporting bugs / requesting features

Please use the issue templates under **New Issue** rather than opening a blank issue — they collect the details (version info, reproduction steps, logs) needed to act on a report quickly.

## License

By contributing, you agree that your contributions will be licensed under the same terms as the project: [LGPL-3.0](LICENSE) for code, and [CC BY-NC-SA 3.0](licenses/LICENSE-ASSETS) for original textures, models, and other visual assets (see [NOTICE](licenses/NOTICE)).
