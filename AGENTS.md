# Project instructions

## Before changing anything

- Explain what you plan to change and why, then wait for explicit confirmation.
- Do not edit, create, delete, or rename files until the user confirms.
- Read-only investigation is always fine.
- After approval, make only the agreed changes.

## Communication

- Be concise: short answers, bullets, no filler, no restating the request.
- Show only relevant code and diffs.
- Ask one focused question when something is ambiguous.

## Project facts

- Java 21, Spring Boot 4.1.x, Spring Cloud 2025.1.x, Maven.
- No root pom; each module is standalone.
- Install common-lib first: `cd common-lib && ./mvnw install`.
- Never commit secrets; use environment variables.
