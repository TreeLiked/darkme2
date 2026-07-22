# AGENTS.md

## Cursor Cloud specific instructions

### What this project is
`darkme2` is a Spring Boot **2.1.1** (Java **8**) website: a file transfer/网盘 station (Tencent COS backed),
user register/login, notes (memo), and a one-stroke-puzzle solver. It serves **HTTPS on port 30001** and an
**HTTP port 80 connector that redirects to HTTPS**. Build tool is the bundled Maven wrapper (`./mvnw`).

### Java version (important)
This project compiles at source/target **8**. The VM default JDK has been set to **Java 8**
(`/usr/lib/jvm/java-8-openjdk-amd64`) via `update-alternatives`, so `./mvnw` works without extra env setup.
Do **not** build with JDK 21 — it rejects `-source 8` and the build fails.

### Database (important)
The committed `src/main/resources/jdbc.properties` points at a **dead remote MySQL** (Tencent CloudDB), so the
app/tests cannot use it. A **local MySQL 8** is installed with a `darkme` database, a `darkme`@`localhost` user
(password `darkme_dev_pw`), and the `User`, `IFile`, `Memo` tables. Start it before running anything:

```
sudo service mysql start
```

`DataSourceConfig` reads datasource values via `@Value` from `jdbc.properties` (`@PropertySource`, lowest
precedence), so override them at runtime **without editing files** by passing Spring args/`-D` properties.
`allowPublicKeyRetrieval=true` is required for MySQL 8 `caching_sha2_password` over a non-SSL connection.

If the local DB is ever missing, recreate it with:
```
sudo mysql -e "CREATE DATABASE IF NOT EXISTS darkme CHARACTER SET utf8mb4;
CREATE USER IF NOT EXISTS 'darkme'@'localhost' IDENTIFIED WITH caching_sha2_password BY 'darkme_dev_pw';
GRANT ALL PRIVILEGES ON darkme.* TO 'darkme'@'localhost'; FLUSH PRIVILEGES;"
sudo mysql darkme -e "
CREATE TABLE IF NOT EXISTS \`User\` (id VARCHAR(64) PRIMARY KEY, Name VARCHAR(128), Password VARCHAR(128), Email VARCHAR(128), GmtCreated TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP, GmtModified TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE IF NOT EXISTS \`IFile\` (Id VARCHAR(64) PRIMARY KEY, UserId VARCHAR(64), DestUserId VARCHAR(64), Size BIGINT, No VARCHAR(32), SaveDays INT, BucketId VARCHAR(64), Attach VARCHAR(512), Name VARCHAR(512), Open BIT(1), Password VARCHAR(128), GmtCreated TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP, GmtModified TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE IF NOT EXISTS \`Memo\` (id VARCHAR(64) PRIMARY KEY, Title VARCHAR(256), Content TEXT, Statue SMALLINT, UserId VARCHAR(64), GmtCreated TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP, GmtModified TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP);"
```

### Build / lint / test
No lint plugin is configured; `./mvnw compile` is the effective compile check.
Tests use `@SpringBootTest` (full context load), so they need a running DB — pass the same overrides:

```
./mvnw test \
  "-Dspring.datasource.url=jdbc:mysql://localhost:3306/darkme?characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=GMT%2B8&allowMultiQueries=true" \
  "-Dspring.datasource.username=darkme" "-Dspring.datasource.password=darkme_dev_pw"
```

### Running the app
The HTTP :80 redirect connector requires **root**, so run with `sudo`. `spring-boot:run` does **not** work
out of the box: the local `lib/shared-all-1.0-SNAPSHOT.jar` (used by controllers/services) is only bundled into
the packaged fat jar via a `<resource>` into `BOOT-INF/lib`, not placed on the plugin runtime classpath. Use the
packaged jar instead:

```
./mvnw package -DskipTests
sudo java -jar target/darkme2-0.0.1-SNAPSHOT.jar \
  --spring.datasource.url='jdbc:mysql://localhost:3306/darkme?characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=GMT%2B8&allowMultiQueries=true' \
  --spring.datasource.username=darkme --spring.datasource.password=darkme_dev_pw
```

Then browse `https://localhost:30001/` (self-signed cert → accept the warning). The landing page auto-redirects
to `/index`, whose front end pulls Vue/Element-UI/jQuery from public CDNs.

### Feature notes
- User register/login (`/api/user/register`, `/api/user/login`) and the one-stroke solver work with just the
  local DB — no external services.
- File upload uses **Tencent COS** with hardcoded credentials in `application.yml` that are likely invalid, so
  the upload flow cannot be fully exercised here.
- The `MemoService` implementation is commented out, so the notes feature is not wired up in this revision.
