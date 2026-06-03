plugins {
  id("me.roundaround.allay")
}

allay {
  displayName.set("Nicer Portals")
  description.set("Various small improvements to portals.")
  authors.set(listOf("Roundaround"))
  license.set("MIT")
  homepage.set("https://modrinth.com/mod/nicer-portals")
  repository.set("https://github.com/Roundaround/mc-fabric-nicer-portals")
  issues.set("https://github.com/Roundaround/mc-fabric-nicer-portals/issues")
  logoFile.set("assets/nicerportals/banner.png")

  modrinth {
    projectId.set("nicer-portals")
  }

  curseforge {
    projectId.set(1501935)
  }

  release {
    versionType.set("release")
    sourcesJar.set(true)
  }
}
