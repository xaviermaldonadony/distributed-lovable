# Project instructions

## Before changing anything

- Explain what you plan to change and why, then wait for explicit confirmation.
- Do not edit, create, delete, or rename files until the user confirms.
- Read-only investigation is always fine.
- After approval, make only the agreed changes.

### What counts as confirmation

- A request to fix, debug, or investigate something is NOT confirmation to change files.
  It authorizes read-only investigation and a proposed change only.
- Show the exact diff (or the full new file for new files), then stop and wait.
- Apply only after an explicit go-ahead: "apply", "go ahead", "yes", "commit it".
- One approval covers one change set. Do not reuse it for later edits.
- If the change would differ from what was approved, stop and re-confirm.

## Filesystem scope

- Stay inside the project root for every command, read, and write.
- Anything outside the project root requires permission first: name the exact path and why, then wait.
  This includes sibling repos (e.g. `../lovable-config-server`), `~`, `/tmp`, and other projects.
- Read-only access is not an exception. Ask before reading outside the project root too.
- Permission covers one path for one task; it does not carry over to later paths or tasks.
- Exception: the harness-approved temp directory may be used without asking.

## Communication

- Be concise: short answers, bullets, no filler, no restating the request.
- Show only relevant code and diffs.
- Ask one focused question when something is ambiguous.

## Project facts

- Java 21, Spring Boot 4.1.x, Spring Cloud 2025.1.x, Maven.
- No root pom; each module is standalone.
- Install common-lib first: `cd common-lib && ./mvnw install`.
- Never commit secrets; use environment variables.
