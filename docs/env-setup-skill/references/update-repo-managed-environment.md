# Update a repository-managed environment

Use this flow when the user asks to change, improve, build, or test a repository-managed environment, and only after `environment-info` confirms both an environment ID and a non-empty `environmentJsonPath`. The repository file at that path is authoritative for the selected revision.

Happy path: iterate on the Dockerfile / environment files → push a branch → trigger a build from that branch → verify with a fresh-agent subagent → open a PR and share the link. Always finish with a branch handoff even when build tools are unavailable.

## 1. Establish the source of truth

1. Call `environment-info`.
2. Record the environment ID and URL, exact repository URLs, environment JSON path, current environment JSON, build information, and effective egress policy.
3. Read the environment file from the repository plus every referenced Dockerfile and script.
4. Inspect dependency manifests, lockfiles, tool-version files, and repository guidance relevant to the requested outcome.
5. If the environment ID is absent, stop: draft-build tools cannot target a logical environment even though a repository environment file exists.

Do not pass a custom `environmentJson` override for this flow. Repository-managed builds read configuration from the requested Git revision.

## 2. Iterate on the repository change and push a branch

Edit the authoritative environment file and its base files narrowly:

- Dockerfile or stable image configuration for system dependencies;
- `install` or referenced install scripts for idempotent repository bootstrap;
- `start` for per-boot reconciliation;
- `terminals` for visible long-running processes;
- ports and repository dependencies when required;
- existing `AGENTS.md` only for durable, non-obvious operating guidance; do not create the file if it is absent.

Validate JSON against the current public schema and validate changed shell scripts or Dockerfiles with safe static checks. Do not add secrets to tracked files. Do not change egress merely to make a build pass; use the environment network-settings flow.

Commit and push every required file to one branch before triggering a build. The builder cannot see uncommitted work, unpushed commits, or files from another branch. Record the full pushed commit SHA and the branch name.

## 3. Trigger a build from the pushed branch

If build tools are unavailable, skip sections 3–4, state that build verification was skipped, and continue to the branch handoff in section 5.

When build tools are available, trigger a draft build as part of this flow. The user's request to change, improve, build, or test this repository-managed environment through `/env-setup` is the explicit build request for `trigger-environment-build`; do not wait for a second "please build" phrase. Do not trigger speculative rebuilds for unrelated questions. If the tool refuses, ask the user once to confirm a draft build of the pushed revision, then continue with the branch handoff either way.

Call `trigger-environment-build` with `refs` entries containing:

- the exact `repo_url` returned by `environment-info`;
- the pushed branch, tag, or preferably full commit SHA in `ref`.

Include every non-default repository revision the environment needs. Do not pass `environmentJson`.

Save the returned build ID. Poll `list-environment-builds` for that exact ID every 30 seconds until it reaches `SUCCEEDED` or `FAILED`; do not mistake a newer unrelated build for this one. Then call `environment-build-logs` and inspect the complete log.

On failure, fix the earliest failing layer:

1. image build and package availability;
2. checkout, permissions, runtime user, disk, or network;
3. install command and generated state;
4. startup and terminal readiness.

Push the correction and create a new build. A completed build is immutable; do not claim that rebuilding the same ID tested later changes.

## 4. Verify the build with a fresh-agent subagent

When a build succeeded, verify it:

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


The build image and checked-out source must describe the same revision. Focus the subagent on the setup change under test (for example, that the start command completes successfully).

If subagent verification is unavailable or rejected, report that limitation, do not claim fresh-agent validation, and continue to the branch handoff.

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


## 5. Hand off the branch (PR when possible) and report

Always hand off the pushed branch after section 2, whether or not build-and-verify succeeded:

1. Prefer opening a pull request from the pushed branch (or updating an existing one) and sharing the PR link. For this repository-managed environment update flow, the user's request to change the environment is the request to propose that change for review—use the PR management tool rather than `gh` when available.
2. If PR creation is unavailable or refused without a more explicit ask, share the pushed branch name and a compare/branch URL, then ask whether to open a PR.
3. Summarize what was validated: full build + subagent evidence when available; otherwise note that build and/or fresh-agent verification was skipped or failed and what remains for the user.

Also provide:

- the linked environment;
- the linked build and terminal status when a build ran;
- the pushed branch and commit;
- build-log findings when a build ran;
- subagent evidence when verification ran;
- any remaining activation step after merge.

A draft build does not become the build new agents use. Merging the PR (and any separate human-controlled activation) is required before the change is the default for new agents.
