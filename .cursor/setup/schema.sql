-- Local development database for darkme2.
-- Idempotent: safe to run on every boot. Creates the `darkme` schema, a
-- localhost dev user, and the tables the MyBatis mappers expect.
--
-- NOTE: `darkme_dev_pw` is a throwaway credential for the local, container-only
-- MySQL instance used by Cloud Agents. It is not a production secret.

CREATE DATABASE IF NOT EXISTS darkme CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'darkme'@'localhost' IDENTIFIED WITH caching_sha2_password BY 'darkme_dev_pw';
CREATE USER IF NOT EXISTS 'darkme'@'127.0.0.1' IDENTIFIED WITH caching_sha2_password BY 'darkme_dev_pw';
GRANT ALL PRIVILEGES ON darkme.* TO 'darkme'@'localhost';
GRANT ALL PRIVILEGES ON darkme.* TO 'darkme'@'127.0.0.1';
FLUSH PRIVILEGES;

USE darkme;

CREATE TABLE IF NOT EXISTS `User` (
  id          VARCHAR(64) NOT NULL PRIMARY KEY,
  Name        VARCHAR(128),
  Password    VARCHAR(128),
  Email       VARCHAR(128),
  GmtCreated  TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  GmtModified TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `IFile` (
  Id          VARCHAR(64) NOT NULL PRIMARY KEY,
  UserId      VARCHAR(64),
  DestUserId  VARCHAR(64),
  Size        BIGINT,
  No          VARCHAR(32),
  SaveDays    INT,
  BucketId    VARCHAR(64),
  Attach      VARCHAR(512),
  Name        VARCHAR(512),
  Open        BIT(1),
  Password    VARCHAR(128),
  GmtCreated  TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  GmtModified TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `Memo` (
  id          VARCHAR(64) NOT NULL PRIMARY KEY,
  Title       VARCHAR(256),
  Content     TEXT,
  Statue      SMALLINT,
  UserId      VARCHAR(64),
  GmtCreated  TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  GmtModified TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
