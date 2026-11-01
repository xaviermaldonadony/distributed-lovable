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

## Communication

- Be concise: short answers, bullets, no filler, no restating the request.
- Show only relevant code and diffs.
- Ask one focused question when something is ambiguous.

## Project facts

- Java 21, Spring Boot 4.1.x, Spring Cloud 2025.1.x, Maven.
- No root pom; each module is standalone.
- Install common-lib first: `cd common-lib && ./mvnw install`.
- Never commit secrets; use environment variables.
