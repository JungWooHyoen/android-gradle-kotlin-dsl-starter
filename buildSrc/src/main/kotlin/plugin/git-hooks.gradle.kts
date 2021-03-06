package plugin

import BuildTasksGroup
import util.isLinuxOrMacOs

tasks {
  register<Copy>("copyGitHooks") {
    description = "Copies the git hooks from scripts/git-hooks to the .git folder."
    group = BuildTasksGroup.GIT_HOOKS
    from("$rootDir/scripts/git-hooks/") {
      include("**/*.sh")
      rename("(.*).sh", "$1")
    }
    into("$rootDir/.git/hooks")
  }

  register<Exec>("installGitHooks") {
    description = "Installs the pre-push git hooks from scripts/git-hooks."
    group = BuildTasksGroup.GIT_HOOKS
    workingDir(rootDir)
    commandLine("chmod")
    args("-R", "+x", ".git/hooks/")
    dependsOn(named("copyGitHooks"))
    onlyIf {
      isLinuxOrMacOs()
    }
    doLast {
      logger.info("Git hooks installed successfully.")
    }
  }

  register<Delete>("deleteGitHooks") {
    description = "Delete the pre-push git hooks."
    group = BuildTasksGroup.GIT_HOOKS
    delete(fileTree(".git/hooks/"))
  }

  afterEvaluate {
    tasks["clean"].dependsOn(tasks.named("installGitHooks"))
  }
}
