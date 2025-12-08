# Repository Guidelines

## Project Structure & Module Organization
- Backend code lives in `src/main/java/com/github/toran`; domain abstractions sit in `common`, cross-cutting infra in `framework`, and business modules under `module` (for example `module/system` for users/config, `module/infra` for storage adapters).
- Configuration and assets are in `src/main/resources` (edit `application.yml`, DB DDL in `db/schema.sql`, static/templates reserved for views); integration tests reside in `src/test/java`.
- `target/` is build output; do not commit it.

## Build, Test, and Development Commands
- `mvn clean install` - full build with tests.
- `mvn spring-boot:run` - run the app locally with dev config from `application.yml`.
- `mvn test` - execute the Spring Boot test suite.
- `mvn -DskipTests package` - package quickly when tests are unchanged.

## Coding Style & Naming Conventions
- Java 21, Spring Boot 3.5.x; prefer 4-space indentation and names aligned to the package (e.g., `*Controller`, `*Service`, `*Mapper`).
- Use Lombok annotations already present (e.g., `@Data`, `@Builder`) instead of manual boilerplate; keep DTOs/VOs lightweight.
- Controller responses should use the `Result` wrapper; reuse `ResultCode` and `CommonConstant` values.
- Keep MyBatis-Plus mappers under `module/**/mapper`; place configs/aspects under `framework` rather than feature modules.

## Testing Guidelines
- Default to `@SpringBootTest` with JUnit 5; name classes `*Tests` (see `PersonalBlogApplicationTests`).
- Cover service-layer rules, security filters, and configuration toggles; mock external I/O (file storage, DB writes) when possible.
- Run `mvn test` before pushing; add regression cases for bugs and include negative paths.

## Commit & Pull Request Guidelines
- Use present-tense, concise commits; Conventional Commit prefixes (`feat`, `fix`, `chore`, `test`, `docs`) are preferred.
- PRs should describe scope, rationale, and test evidence (`mvn test` output or screenshots for API docs/UI); link issues if applicable.
- Keep changes scoped: one feature or fix per PR; update relevant docs (README, API guides) alongside code.

## Security & Configuration Tips
- Never commit secrets; keep database and token values in environment overrides rather than `application.yml`.
- Run `mysql -u root -p < src/main/resources/db/schema.sql` to seed local DB; adjust JDBC settings in `src/main/resources/application.yml` and mirror overrides via env vars for deployments.
- JWT and security settings live in `framework/security/config`; review filters when adding public endpoints.
