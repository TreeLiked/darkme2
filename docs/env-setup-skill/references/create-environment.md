# Set up a new environment

Use this flow when the user wants a repository to become fully usable in Cloud Agents, or when no effective environment exists yet. Respect an explicit scope such as one service or package; otherwise target the repository's complete development experience.

## 1. Discover the development experience

Before changing configuration, investigate in parallel:

1. Identify the products and services in the repository. Separate services required for an end-to-end development flow from optional services.
2. Find setup entry points such as `README.md`, `CONTRIBUTING.md`, `AGENTS.md`, package-manager files, tool-version files, Makefiles, setup scripts, devcontainer files, Docker Compose files, and development documentation.
3. Find the canonical lint, type-check, test, build, and development-server commands. Inspect pre-commit and pre-push hooks because they often encode required checks.
4. Inspect `.cursor/environment.json`, if present, plus every Dockerfile or script it references. A committed file is already the highest-precedence environment source; do not create a competing dashboard configuration.
5. Identify required secrets, test accounts, network domains, databases, queues, browser dependencies, and other external prerequisites. Check which secrets are already available before reporting a blocker.
6. When the repository depends on a third-party service, hosted platform, or a stack you are not deeply familiar with (for example Supabase or Firebase), search its current official documentation with web search and fetch tools when available instead of relying on memorized knowledge. If web search or network access is unavailable in the current egress mode, say so and prefer commands the repository itself pins or documents.

Use subagents for broad, independent repository analysis when available, but review the files and commands yourself before adopting their recommendations. Create a concrete checklist containing:

- dependencies and generated state the automatic install phase must prepare;
- system packages or stable toolchains the base image must contain;
- runtime services that must start on every boot;
- commands and user flows that will prove the environment works.

## 2. Design the environment

Prefer Cursor's default base image unless a stable system dependency or toolchain is missing. When a custom Dockerfile is justified:

- install operating-system packages and stable tools there;
- keep it deterministic and non-interactive;
- install `git` and `curl` in fully custom images;
- target x86_64 Debian or Ubuntu unless current product documentation says otherwise;
- do not copy the whole repository into the image, because Cursor checks out the requested revision separately;
- use a repository-relative Dockerfile for repo-managed environments and inline `dockerfileContents` only when the supported dashboard/setup flow requires it.

If Docker must run inside the Cloud Agent VM, account for the nested-container environment. Docker may require `fuse-overlayfs`, legacy iptables, and explicit daemon startup. Validate the current platform rather than pasting a stale package-version recipe.

If Tailscale is required, use userspace networking mode. Standard TUN-based networking does not work in Cloud Agent VMs. Start `tailscaled` with userspace SOCKS/HTTP proxy listeners and set the proxy variables only for processes that need tailnet access.

Divide commands by lifecycle:

- `install`: minimal, idempotent dependency refresh and source-derived generation after checkout. It must terminate. Do not put dev servers, migrations, tests, or fragile production builds here.
- `start`: per-boot reconciliation for daemons or runtime state required by every agent. It must tolerate restarts, prevent duplicates, check readiness, and then return.
- `terminals`: named long-running foreground servers, workers, and watchers whose logs and lifecycle should remain visible.
- `AGENTS.md`: only if the repository already has one. You may add durable, non-obvious operating guidance for future agents and point to existing canonical commands instead of duplicating ordinary README content. Do not create `AGENTS.md` when it does not already exist.

Use the repository's pinned tool versions, package manager, and lockfile. Do not broadly upgrade dependencies or rewrite lockfiles unless the user asks. Avoid interactive commands and shell-profile mutation. An install command must also remain safe if an unmerged setup PR is not present; guard references to files introduced only by that PR when necessary.

## 3. Implement and validate locally

Set up the current VM first. Work from the smallest failing layer:

1. Install system dependencies and toolchains.
2. Run the proposed install command to completion, then run it a second time to prove idempotence.
3. Start required infrastructure and development services through the intended `start` or `terminals` path.
4. Run the repository's lint, type-check, automated test, and development build commands for the agreed scope.
5. Exercise a representative hello-world flow through the product's core functionality. Merely loading a page or starting a process is insufficient; perform a real action such as creating a record, sending a request, or completing the primary interaction.
6. Capture concise evidence: successful command output and, for a GUI product, a screenshot or short recording of the working flow.

Do not change application code merely to hide an environment problem. Environment configuration, referenced setup files, and narrowly necessary edits to an existing `AGENTS.md` are in scope. Do not create `AGENTS.md` if it is missing.

## 4. Unblock, snapshot, build-test, then propose

Get the current VM working before recording anything. If setup is blocked on a missing secret, network allowlist entry, or other external prerequisite, request that information and continue once unblocked. Do not draft, snapshot, or trigger a build while the local setup still fails for reasons you can fix inside the VM.

### Handle blockers during setup

Do everything possible inside the VM before requesting user action. A missing secret, test login, network allowlist entry, external account permission, or unavoidable provider-console step can be a blocker; installing packages, editing files, or starting local services is not.

The order of operations is mandatory:

1. As soon as a required secret, test login, network/egress change, or external action is confirmed missing, call `cursor-cloud-request-environment-setup-actions` in that turn. If the tool is unavailable, explain the blocker in normal prose.
2. Continue only independent pre-snapshot setup work. Do not snapshot, trigger a build, verify a build, propose the configuration, or ask the user to Save.
3. End the turn with a concise explanation of what remains and wait until every required blocker is resolved. If the user declines a required action, report that setup remains incomplete; do not propose or request Save.
4. After every required blocker is resolved, resume the matching workflow's remaining validation steps, including snapshot, build, and build verification when those capabilities are available.
5. Only after validation succeeds, propose the configuration and ask the user to Save.

Confirm that required secrets are missing or invalid before requesting them. The tool accepts only three action types: `add_secrets`, `add_egress_allowlist_domain`, and `external_action`. Do not use `add_test_login` — put test-login username/password/OTP secret names in one `add_secrets` action instead. Each secret is `{ "name": "SECRET_NAME", "optional": false }` (`optional` is required). External actions are only for work the user must perform outside the repository or VM; use `instructions` (not `description`) on `external_action`. You may call the tool more than once as new blockers appear; successful calls in the completed turn are combined.

Example arguments:

```json
{
  "actions": [
    {
      "type": "add_secrets",
      "secrets": [
        { "name": "TEST_LOGIN_USERNAME", "optional": false },
        { "name": "TEST_LOGIN_PASSWORD", "optional": false },
        { "name": "DB_CONNECT_STRING", "optional": false }
      ],
      "reason": "Needed for API startup and e2e login tests."
    },
    {
      "type": "add_egress_allowlist_domain",
      "domain": "api.example.com",
      "reason": "Install downloads packages from this host."
    },
    {
      "type": "external_action",
      "id": "create_oauth_app",
      "title": "Create OAuth app",
      "instructions": "Create an OAuth app in the provider dashboard and add the callback URL."
    }
  ]
}
```


When local validation succeeds, inspect the available tools and follow this order:

1. Snapshot the working VM and wait until it is READY.
2. When environment-build tools are available, trigger a draft build with `environmentJson` containing that READY `snapshot` plus the install/start scripts under test (including greenfield creates with no linked environment yet), wait for it to finish, and verify it with a fresh-agent subagent. Asking the user to set up an environment through this skill is the explicit request to run that build-test path; do not trigger builds for unrelated questions.
3. Propose the final install and start scripts for the user to review and Save, including the successful `buildId` when a build was tested (not the raw snapshot id).
4. If build tools are unavailable, skip the build-test step and finalize with snapshot then proposal instead.

### Snapshot the working VM

If `cursor-cloud-take-environment-snapshot` is available, take a fresh snapshot after the VM reflects the final install state:

1. Run the final install command successfully.
2. Call `cursor-cloud-take-environment-snapshot` and record the returned snapshot ID.
3. Poll `cursor-cloud-check-environment-snapshot` every 30 seconds until it reports ready.
4. Do not continue while the snapshot is creating or after it fails.
5. When the snapshot is READY, pass it as `environmentJson.snapshot` on `trigger-environment-build` together with the install/start scripts under test. Draft-build overrides replace saved config wholesale, so the snapshot pins the base image for that build. Do **not** put `snapshot` on `propose-environment-json` — after a successful draft build, pass that build's id as top-level `buildId` so Portal Save reuses the snapshot the build validated.

Any later VM change or install-script change requires running the final install command again and taking a new snapshot. If the snapshot tool is unavailable, do not attempt to snapshot the VM another way.


### Build and verify when builds are available

Call `environment-info` when available and record any environment ID and URL. Decide the install and start scripts that describe the working setup.
When `trigger-environment-build` is available, call it with `environmentJson` per the tool schema (READY `snapshot` from the snapshot step plus install/start under test). A missing environment ID is fine: passing `environmentJson` creates and links a personal transitional draft automatically. Prefer omitting `refs` so the build uses each repository's default revision unless a non-default source is required for the test.

Save the returned build ID. Poll `list-environment-builds` for that exact ID every 30 seconds until it reaches `SUCCEEDED` or `FAILED`, then inspect the complete log with `environment-build-logs`. On failure, fix the earliest failing layer, update the scripts, and trigger a new build. A completed build is immutable.

### Test the successful build in a fresh agent

After a successful build, use a first-class cloud subagent when its tool exposes the required arguments:

- `environment: "cloud"`;
- `cloud_requested_environment_build_id`: the successful build ID;
- `cloud_base_branch`: the pushed branch or non-default ref when the build used one.

Omit `cloud_base_branch` when the build used the default branch. Ask the subagent to:

1. report the requested build identity if visible;
2. verify required tool versions and install outputs;
3. verify generated files and dependency state;
4. start or inspect required services through the configured startup path;
5. run representative health checks, tests, or a small product action;
6. avoid production endpoints and mutations.

If build selection is unavailable or rejected, do not claim that this build was tested.


### Propose install and start scripts

If `cursor-cloud-propose-environment-json` is available, call it after validation succeeds. Use the tool's `environmentJson` and optional `buildId` parameters (see the tool schema). When a draft build was tested, pass that build's id as `buildId` so Portal Save reuses the snapshot the build validated.

If the proposal or selected build changes later, call the tool again with the updated final values.

A proposal does not modify the current VM or save the environment. Do not call `cursor-cloud-propose-environment-json` or ask the user to Save while any required secret, test login, network/egress change, or external action is outstanding. Record those blockers first and end the turn without proposing or requesting Save. After the user resolves every required blocker, finish validation and only then propose the configuration for review and Save. If the user declines a required action, report that setup remains incomplete; do not propose or request Save. For now users cannot edit scripts in the environment panel. If they want script changes, they can either ask for another build-and-test cycle or Save using the already-validated (possibly outdated) build.


Never imply that a proposed or draft configuration was saved. Saving is a user action in the environment panel.

If build tools are unavailable, skip the build-and-verify step entirely. Still snapshot the working VM (above) and then propose scripts without a `buildId`. Do not claim a build was tested.

## 5. Report the result

### Report a successful dashboard Save flow

After validation succeeds, the proposal is created, and Save is the only remaining action, respond using this structure:

<project-name>’s Cloud Agent environment is tested and ready to save.

#### Validation
| Check | Result |
| --- | --- |
| <core product action> | <result> |
| <relevant checks> | <result> |
| <representative tests> | <result> |
| Install idempotence | Passed twice |
| Fresh Cloud Agent | <versions and checks passed> |

Include only successful checks that actually ran, and omit unavailable rows. Include Fresh Cloud Agent only when a subagent booted from the exact tested build and passed verification.

When `environment-info` returns a non-empty environment `url`, add:

#### Setup details
| Resource | Link |
| --- | --- |
| Environment | [<full-id>](<confirmed-url>) |
| Tested build | [<full-id>](<confirmed-url>) |

Use the returned `url` for the environment link; builds belonging to that environment may use their build-detail URLs. Omit unavailable rows. If `url` is absent—including for a transitional greenfield draft—omit Setup details and do not construct links from IDs.

#### Action required
**Click Save in the Environment panel on the right.**

This makes the tested setup available to future Cloud Agents. Nothing else is required.

Do not repeat configuration shown in the Environment panel or expose internal setup mechanics. If Save is not the only remaining action, do not use this template; report the actual blockers or use the workflow-specific handoff.


When an existing `AGENTS.md` changed, explain after the standard response that merging those changes preserves the discovered guidance for future agents.
