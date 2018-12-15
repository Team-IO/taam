Tech and Accessory Mod : TAAM
[![Build Status (master branch)](https://img.shields.io/travis/Team-IO/taam/master.svg?label=build%3Amaster)](https://travis-ci.org/Team-IO/taam/branches)
[![Build Status (1.8 branch)](https://img.shields.io/travis/Team-IO/taam/1.8.svg?label=build%3A1.8)](https://travis-ci.org/Team-IO/taam/branches)
[![Build Status (1.9 branch)](https://img.shields.io/travis/Team-IO/taam/1.9.svg?label=build%3A1.9)](https://travis-ci.org/Team-IO/taam/branches)
[![Build Status (1.10 branch)](https://img.shields.io/travis/Team-IO/taam/1.10.svg?label=build%3A1.10)](https://travis-ci.org/Team-IO/taam/branches)
[![Build Status (1.12 branch)](https://img.shields.io/travis/Team-IO/taam/1.12.svg?label=build%3A1.12)](https://travis-ci.org/Team-IO/taam/branches)
[![Join the chat at https://gitter.im/Team-IO/taam](https://badges.gitter.im/Team-IO/taam.svg)](https://gitter.im/Team-IO/taam?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
====

For details on how to use this mod, check the [Wiki](https://github.com/Team-IO/taam/wiki).

Also find this mod on [CurseForge](http://minecraft.curseforge.com/projects/taam) and [on our homepage](https://team-io.net/taam.php).

## Contributing
If you want to contribute, you can do so [by reporting bugs](https://github.com/Team-IO/taam/issues), [by helping fix the bugs](https://github.com/Team-IO/taam/pulls) or by spreading the word!

You are also welcome to [support us on Patreon](https://www.patreon.com/Team_IO?ty=h)!

## Building the mod
Taam uses a fairly simple implementation of ForgeGradle. To build a ready-to-use jar, you can use the gradle wrapper delivered with the rest of the source code.

For Windows systems, run this in the console:

```
gradlew.bat build
```

For *nix systems, run this in the terminal:

```
./gradlew build
```

Installed Gradle versions should also work fine, but we require at least Gradle 4.6 for the JUnit integration.

## Some info on the internal structure:
### Branches
The master branch currently points to the latest released version for MC 1.12.2. Every supported minecraft version has their own branch.

### Packages
Mod & Dependency versions are controlled in the `build.gradle` and according `build.properties` file. All mod metadata is done in code, with the version replaced by gradle on compile time.

Item/Block names are recorded in the class `net.teamio.taam.Taam`. Main mod class is `net.teamio.taam.TaamMain`. Anything else in that package is somewhat related to global registration with Minecraft or config stuff.

All Item & block classes (and related stuff) are located in the `net.teamio.taam.content.*` packages, clustered by area.
Supporting classes for the themes (Utils, API, etc.) are located in the corresponding `net.teamio.taam.*` packages.

Integrations with other mods belong in a corresponding package in `net.teamio.taam.integration`. GUI, rendering and network sit in their respective package in `net.teamio.taam`.

### Tests
This mod uses [MinecraftJUnit](https://github.com/BuiltBrokenModding/MinecraftJUnit) to run automated tests. It is pulled in via a maven dependency.
These tests are run automatically for every commit.
