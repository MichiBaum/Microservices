# Git Version and Release

This handbook explains how we manage versions and publish releases using the Git Flow branching model and the gitflow-maven-plugin. Follow these steps to create features, releases, and hotfixes consistently across all modules in this repository.

## Why Git Flow + Maven

- Clear branch roles: production (master), integration (develop), short-lived feature and hotfix branches.
- Automated version bumping: The plugin updates all Maven POMs for you during release and hotfix lifecycles.
- Consistent tagging: Releases are tagged in Git so CI/CD can build immutable artifacts.

## Branch Model (summary)

- master: production-ready history (only releases and hotfixes are merged here)
- develop: next release integration branch
- feature/name: branches from develop, merged back into develop
- hotfix/x.y.z: branches from master to quickly patch production

We follow Semantic Versioning (SemVer): MAJOR.MINOR.PATCH
- MAJOR: incompatible changes
- MINOR: new functionality
- PATCH: bug fixes

## Plugin Configuration

The root pom.xml configures the plugin for the whole multi-module build.

Notes:
- installProject=false means the plugin will not run mvn install during finish goals, which keeps local repositories clean. If CI needs artifacts, the pipeline will build them.
- The default release branch prefix (release/) is used.

## Prerequisites

- Git installed. Maven Wrapper (mvnw) is included in this repo; a system Maven is optional.
- Clean working tree (commit or stash changes).
- Up-to-date local branches tracking origin/master and origin/develop.
- Run commands from the repository root (where the root pom.xml is located).

Tip: use the Maven Wrapper
- Linux/macOS: ./mvnw
- Windows (PowerShell): .\\mvnw.cmd

## Quick Start Reference

- Initialize Git Flow (only once per clone; our pom already defines names/prefixes):
  - ./mvnw -B gitflow:init
- Start a feature:
  - ./mvnw gitflow:feature-start -DfeatureName=MY_FEATURE
- Finish a feature (merges to develop):
  - ./mvnw -B gitflow:feature-finish -DfeatureName=MY_FEATURE
- Run a release (start and finish in one step):
  - ./mvnw -B gitflow:release -DreleaseVersion=1.4.0 -DdevelopmentVersion=1.5.0-SNAPSHOT
- Start a hotfix from master:
  - ./mvnw -B gitflow:hotfix-start -DhotfixVersion=1.4.1
- Finish a hotfix (merges to master and develop, creates tag):
  - ./mvnw -B gitflow:hotfix-finish

## Typical Workflows

### 1) Feature development
1. Start:
   - ./mvnw gitflow:feature-start -DfeatureName=US-123-login
   - The plugin creates and checks out feature/US-123-login from develop.
2. Commit code normally. Rebase/sync with develop as needed.
3. Finish:
   - ./mvnw -B gitflow:feature-finish -DfeatureName=US-123-login
   - Merges into develop and pushes to origin by default. Override with -DpushRemote=false to avoid pushing. Feature branches are deleted by default (keepBranch=false); use -DkeepBranch=true to keep them.

### 2) Prepare a release
1. Ensure develop is green and all features are merged.
2. Run a release with explicit versions:
   - ./mvnw -B gitflow:release -DreleaseVersion=1.4.0 -DdevelopmentVersion=1.5.0-SNAPSHOT
   - By default: pushes to origin. Override with -DpushRemote=false if needed.
   - Actions performed by the plugin:
     - Merge release/1.4.0 into master, tag the release (e.g., 1.4.0)
     - Merge release/1.4.0 back into develop
     - Update develop to 1.5.0-SNAPSHOT (or whatever you set at release)

### 3) Emergency hotfix
1. Start from master:
   - ./mvnw -B gitflow:hotfix-start -DhotfixVersion=1.4.1
   - Versions across modules are set to 1.4.1 on hotfix/1.4.1.
2. Commit the fix, verify tests.
3. Finish and push:
   - ./mvnw -B gitflow:hotfix-finish
   - By default: pushes to origin. Override with -DpushRemote=false if needed.
   - Actions performed by the plugin:
     - Merge hotfix/1.4.1 into master and tag 1.4.1
     - Merge hotfix/1.4.1 back into develop and bump to next SNAPSHOT

## CI/CD notes

- Creating a tag on master typically triggers release pipelines. Check our GitHub Actions configuration for the exact behavior (see Github → Actions in the docs).

## Best Practices

- Keep master protected; only releases and hotfixes should reach master via the plugin.
- Start releases only when develop is stable and all required features are merged.
- Choose versions according to SemVer:
  - Breaking changes: bump MAJOR
  - New features (backward compatible): bump MINOR
  - Bug fixes only: bump PATCH
- Always run with -B (batch mode) in CI to avoid prompts.
- Pushing to origin is enabled by default; use -DpushRemote=false on finish goals to avoid pushing when needed.
- Feature branches are deleted by default at feature-finish (keepBranch=false); release branches are kept by default (keepBranch=true). Override with -DkeepBranch=true/false as needed.
- Run from the repo root so the plugin updates all modules’ pom.xml files consistently.

## Troubleshooting

- Merge conflicts during finish:
  - The plugin will stop on conflict. Resolve conflicts with Git, commit, and re-run the same finish goal.
- Local is behind origin:
  - Run git fetch --all and update your master/develop branches before starting.
- Wrong version chosen:
  - If you accidentally started with a wrong version, you can delete the release/hotfix branch (if not pushed), then start again with the correct -D…Version parameters.
- Tag naming:
  - By default, the plugin creates a Git tag equal to the release version (e.g., 1.4.0). Protect tags in the remote if necessary.

## Notes for this repository

- The plugin is configured at the root, so it manages versions for all services and libraries in one go.
- productionBranch=master, developmentBranch=develop, featureBranchPrefix=feature/, hotfixBranchPrefix=hotfix/ (release/ is default).
- Use the Maven Wrapper: Linux/macOS: ./mvnw; Windows (PowerShell): .\\mvnw.cmd
- Defaults: pushRemote=true on finish goals; keepBranch=false for feature-finish; keepBranch=true for releases.

## See also

- Maven project configuration (root pom.xml)
- Docs → Github → Actions for CI behavior on tags and branch merges

