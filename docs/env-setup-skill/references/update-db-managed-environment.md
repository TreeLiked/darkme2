# Update a DB-managed environment

Use this flow when the user asks to change or improve a DB-managed (dashboard-backed) environment, and only after `environment-info` confirms an environment ID and a null or absent `environmentJsonPath`. The saved dashboard configuration is authoritative; a repository patch alone does not update it.

Happy path: read the current config with `environment-info` → iterate on the install/start scripts on this VM → request and resolve any required secret, test-login, network/egress, or external-action blockers → snapshot this VM → trigger a draft build with `environmentJson` snapshot + install/start → verify with a fresh-agent subagent → call `propose-environment-json` with the successful build's `buildId` → require the user to press Save.

## 1. Read the current environment configuration

1. Call `environment-info` (the environment-config lookup tool) and treat its result as the current saved configuration.
2. Record the environment ID and URL, exact repository URLs and default revisions, full current environment JSON, build information, and effective egress policy.
3. Inspect the repository's manifests, lockfiles, setup scripts, devcontainer files, service commands, and relevant guidance for the requested change.

If the environment ID is absent, stop: draft-build tools cannot target a logical environment.

## 2. Iterate on the install script on this VM

Apply the requested change on the current agent VM first—usually the install script, plus `start` / `terminals` when the change needs per-boot or long-running behavior.

Decide the install and start scripts from the working result. Use each tool's `environmentJson` schema for draft-build and propose arguments; other env.json knobs stay on the saved environment or a repo-file branch. Validate lifecycle placement: dependency refresh in `install`, per-boot reconciliation in `start`, and visible long-running processes in `terminals`.

Do not place secrets in the proposal. Do not use `environmentJson` to change egress; network policy must be updated through environment settings.

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


## 3. Snapshot this VM

### Snapshot the working VM

If `cursor-cloud-take-environment-snapshot` is available, take a fresh snapshot after the VM reflects the final install state:

1. Run the final install command successfully.
2. Call `cursor-cloud-take-environment-snapshot` and record the returned snapshot ID.
3. Poll `cursor-cloud-check-environment-snapshot` every 30 seconds until it reports ready.
4. Do not continue while the snapshot is creating or after it fails.
5. When the snapshot is READY, pass it as `environmentJson.snapshot` on `trigger-environment-build` together with the install/start scripts under test. Draft-build overrides replace saved config wholesale, so the snapshot pins the base image for that build. Do **not** put `snapshot` on `propose-environment-json` — after a successful draft build, pass that build's id as top-level `buildId` so Portal Save reuses the snapshot the build validated.

Any later VM change or install-script change requires running the final install command again and taking a new snapshot. If the snapshot tool is unavailable, do not attempt to snapshot the VM another way.


## 4. Trigger a draft build with the proposed scripts

If build tools are unavailable or the tool cannot accept a DB-managed `environmentJson` override, skip sections 4–5, state that build verification was skipped, and continue to section 6. Propose the working scripts without a `buildId`; do not claim that a build was tested. The user must still review and Save the proposal.

When the user asked to change, improve, build, or test this environment and the tool supports DB-managed overrides, trigger a build as part of this flow.

Call `trigger-environment-build` with `environmentJson` per the tool schema, including the READY `snapshot` from section 3 and the proposed install/start scripts.

- Omit `refs` to build every repository's default branch. This is the preferred path when the resulting build may later be promoted.
- Add `refs` only when testing source from a non-default branch is necessary. Use exact repository URLs from `environment-info`.
- A DB-managed build from a non-default ref can be tested but may not be promotable.

Save the returned build ID. Poll `list-environment-builds` for that exact ID every 30 seconds until terminal, then fetch its complete log with `environment-build-logs`.

On failure, correct the earliest failing image, checkout, install, or startup layer, re-run the final install on this VM if the scripts changed, take a fresh snapshot, and trigger a new build with that new READY snapshot plus the revised install/start. Do not treat one successful late command as evidence that earlier setup phases succeeded.

## 5. Verify the build with a fresh-agent subagent

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


Focus the subagent on the setup change under test (for example, that the start command completes successfully).

## 6. Propose the configuration and require Save

A custom `environmentJson` draft build does not modify the saved DB-backed environment. State that directly. The update is incomplete until the user presses Save on the environment configuration. When a draft build succeeded, the proposal must include that build's `buildId` so Portal Save reuses the validated snapshot.

### Propose install and start scripts

If `cursor-cloud-propose-environment-json` is available, call it after validation succeeds. Use the tool's `environmentJson` and optional `buildId` parameters (see the tool schema). When a draft build was tested, pass that build's id as `buildId` so Portal Save reuses the snapshot the build validated.

If the proposal or selected build changes later, call the tool again with the updated final values.

A proposal does not modify the current VM or save the environment. Do not call `cursor-cloud-propose-environment-json` or ask the user to Save while any required secret, test login, network/egress change, or external action is outstanding. Record those blockers first and end the turn without proposing or requesting Save. After the user resolves every required blocker, finish validation and only then propose the configuration for review and Save. If the user declines a required action, report that setup remains incomplete; do not propose or request Save. For now users cannot edit scripts in the environment panel. If they want script changes, they can either ask for another build-and-test cycle or Save using the already-validated (possibly outdated) build.


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


Do not claim the dashboard environment was updated until the user Saves.
