lazy val `docs-sandbox` = (project in file("."))
  .enablePlugins(
    ParadoxMaterialThemePlugin,
    ParadoxPlugin,
    ParadoxSitePlugin,
    GhpagesPlugin
  )
  .settings(
    organization := "com.thatdot",
    name := "connect-docs",
    git.remoteRepo := "git@github.com:thatdot/docs-sandbox.git",
    Compile / paradoxMaterialTheme ~= {
      _.withColor("white", "blue")
        .withoutFont()
        .withRepository(uri("https://github.com/thatdot/docs-sandbox"))
        .withCopyright("Copyright © thatDot")
    },
    Compile / paradoxProperties ++= Map(
        "project.name" -> "thatDot Test Project",
        "github.base_url" -> "https://github.com/thatdot/docs-sandbox"
      )
  )

