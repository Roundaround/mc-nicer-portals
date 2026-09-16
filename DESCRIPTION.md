![Nicer Portals](https://imgur.com/KRkBCMD.png)

Various small improvements to portals. There are several features included as part of the mod. Some are client-side and
some server-side. See below for the full list!

## Client-side

The following features only work when installed on the client.

### Dedupe portal break sound

When portals get broken, the entire portal will only make one single break noise, instead of one per block that is
broken. No more headache inducing screeching!

## Server-side or single player

The following features only work when installed on the server (or on your client for single player).

### Prevent zombified piglin spawns

Stop zombified piglins from invading your megabase! Simply prevents them from spawning from portals in the overworld.

### Use crying obsidian in portals

Allows using crying obsidian interchangeably with regular obsidian when constructing portals!

### Custom portal shapes

Allows constructing portals of any enclosed shape! For performance reasons, the portals are limited in size based on the
number of actual portal blocks, so check the config file for the limit!

---

<details>
<summary><b>Configuration — <code>nicerportals.toml</code></b></summary>

You can configure the behavior of the mod from the `nicerportals.toml` files. You will find one in your normal config
folder, and one in a new config folder _inside_ your world's save folder. If you have ModMenu installed, you can also
access the configuration through the UI in ModMenu's mod list!

`dedupeBreakSound`: `true|false` - Whether to makes portals emit only one sound when they break. Client-side only.

`preventPortalSpawns`: `true|false` - Whether to prevent portals from spawning Zombified Piglins in the overworld.
Server-side & single player only.

`anyShape`: `true|false` - Whether to allow portals in any shape and size. Server-side & single player only.

`maxSize`: `Integer` - The maximum allowed portal size to allow. Be warned that setting values that are too large here
could lag or even crash the game. 2304 is the default and seems to cause only a small hiccup in come cases. Note this
value only has any effect if `anyShape` is true. Server-side & single player only.

`enforceMinimum`: `true|false` - Require that portals are at least a 1x2 shape (can walk through them). Set to false to
allow 1x1 portals. Note this value only has any effect if anyShape is true. Server-side & single player only.

### 1.7.0+26.1 and later

`portalFrameTag`: `true|false` - Whether to replace portal frame block checks with the `#nicerportals:portal_frame` tag.
Server-side & single player only.

### 1.6.1+1.21.11 and earlier

`cryingObsidian`: `true|false` - Whether to allow using crying obsidian for portals. Server-side & single player only.

</details>

## Links

<!-- modrinth:only -->
Also on [CurseForge](https://www.curseforge.com/minecraft/mc-mods/nicer-portals) · [Source on GitHub](https://github.com/Roundaround/mc-nicer-portals) · [Report an issue](https://github.com/Roundaround/mc-nicer-portals/issues)
<!-- /modrinth:only -->
<!-- curseforge:only -->
Also on [Modrinth](https://modrinth.com/mod/nicer-portals) · [Source on GitHub](https://github.com/Roundaround/mc-nicer-portals) · [Report an issue](https://github.com/Roundaround/mc-nicer-portals/issues)
<!-- /curseforge:only -->

[![Support me on Ko-fi](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/donate/kofi-singular-alt_vector.svg)](https://ko-fi.com/roundaround)
