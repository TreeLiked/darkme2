# Migrate an environment to builds

Use this flow when the user wants the current environment to use a prebuilt baseline, including Builds page requests to test that the environment will work with builds. The first goal is to test the existing configuration, not to redesign it. Many environments work with builds without any changes.

Do not enable builds for this environment and do not ask the user to promote, activate, merge, or save a draft. The user enables builds themselves on the environment page after reviewing.

## 1. Inspect the current environment

Call `environment-info` and record:

- the environment ID and dashboard URL;
- whether `environmentJsonPath` is present;
- the full effective environment JSON;
- exact repository URLs and default revisions;
- the build currently used by this run, if any;
- effective egress policy.

A non-empty `environmentJsonPath` means repository-managed. A null or absent path means DB-managed. If the environment ID is absent or build tools are unavailable, explain that builds cannot be tested through this flow.

Read the effective install and start commands plus referenced Dockerfiles and scripts. Inspect enough repository documentation and manifests to understand what those commands are intended to do, but avoid speculative cleanup.

## 2. Check build compatibility without over-editing

Environment builds run `install` while creating the baseline snapshot. New pods boot from that snapshot; they do not rerun `install`. Per-pod runtime initialization belongs in `start` or `terminals`.

The install command should:

- install dependencies and create durable source-derived state needed in the baseline;
- be deterministic, non-interactive, and terminate successfully;
- avoid starting dev servers, watchers, workers, databases, or other processes whose continued lifetime it relies on;
- avoid runtime-only migrations or state that must be refreshed for every new pod.

The start command should:

- perform only initialization required on every new pod;
- start or reconcile required daemons and runtime state;
- tolerate restarts and avoid duplicate processes;
- return after a clear readiness or failure check. Put visible long-running foreground processes in `terminals`.

Make a change before the first build only when the incompatibility is obvious, such as an install command that directly runs a foreground development server. If behavior is ambiguous, do not guess. Trigger a build with the current configuration and use its logs as evidence.

## 3. Trigger the build

Only trigger a build when the user explicitly requested this migration.

For the first no-change attempt, build the environment's current saved/default configuration without inventing an override.

If an obvious issue requires a change:

- **Repository-managed:** edit the authoritative environment file or referenced scripts, commit and push every change, then pass `refs` with the exact repository URL and pushed branch or commit. Do not pass `environmentJson`.
- **DB-managed:** pass the revised `install` and/or `start` through `environmentJson`, plus `snapshot` when the draft should start from a prepared VM base (trigger allowlist). This tests an override but does not save it. Use `refs` only when non-default source is also required.

Save the returned build ID. Poll `list-environment-builds` for that exact build every 30 seconds until it reaches a terminal state, then fetch and inspect its complete logs with `environment-build-logs`.

If the build fails, fix the earliest evidenced failure. Do not make unrelated improvements. Trigger a new build for each changed revision or configuration and repeat until the build succeeds or a definitive external blocker is confirmed.

## 4. Test the successful build in a fresh agent

Launch a first-class cloud subagent when the tool exposes:

- `environment: "cloud"`;
- `cloud_requested_environment_build_id`: the successful build ID;
- `cloud_base_branch`: the pushed branch or ref when a repository-managed change was built, or when non-default source was otherwise required.

Ask the subagent to report:

1. whether it booted successfully from the requested build;
2. expected tool versions, dependencies, and durable install outputs;
3. whether per-pod `start` and `terminals` behavior is healthy;
4. service readiness and a representative non-production smoke test;
5. any setup it unexpectedly had to repeat manually.

Do not claim fresh-agent validation if the requested build argument is absent or rejected.

## Handle blockers

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


## 5. Report the migration state

State which outcome occurred:

- **Works out of the box:** the unchanged environment built and passed fresh-agent smoke tests.
- **Works with changes:** identify the exact repository revision or DB-backed configuration changes that were required and the evidence for each.
- **Blocked:** identify the earliest failing layer and the concrete external action required.

Include linked environment and build IDs, build-log findings, and the subagent's evidence.

When builds work (out of the box or after your fixes) and fresh-agent verification passed, the closing next step is for the user to enable builds on the environment page. Give a markdown link labeled **Enable builds** (or equivalent) to the environment dashboard (default detail URL, no hash). Prefer the environment `url` from environment-info when present; otherwise use `[Enable builds](https://cursor.com/dashboard/cloud-agents/environments/e/<environmentPublicId>)`. Do not append `#builds`. Do not bury this behind promotion, merge, or save instructions.

Do not ask the user to promote, activate, or otherwise publish a draft build. Enabling builds on the environment page is the only required follow-up unless you actually changed configuration:

- Repository-managed fixes still on a non-default branch: mention merging that branch only as a brief caveat after the enable link.
- DB-managed overrides that are not saved yet: include the tested configuration after the enable link so the user can save it if they want those fixes; do not frame this as a promotion step.
