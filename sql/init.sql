use melon;

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `id`           VARCHAR(255),
    `username`     VARCHAR(255) NOT NULL UNIQUE,
    `password`     VARCHAR(255) NOT NULL,
    `nickname`     VARCHAR(255),
    `avatar_url`   VARCHAR(255),
    `signature`    VARCHAR(255),
    `introduction` TEXT,
    `residence`    CHAR(50),
    `interest`     VARCHAR(255),
    `gender`       char(2),
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `video`;
CREATE TABLE `video`
(
    `id`           VARCHAR(255),
    `user_id`      VARCHAR(255) NOT NULL,
    `video_path`   VARCHAR(255) NOT NULL,
    `picture_path` VARCHAR(255) NOT NULL,
    `title`        VARCHAR(255),
    `description`  VARCHAR(255),
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `subscription`;
CREATE TABLE `subscription`
(
    `id`         VARCHAR(255),
    `subscriber` VARCHAR(255) NOT NULL,
    `target`     VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `like`;
CREATE TABLE `like`
(
    `id`       VARCHAR(255),
    `user_id`  VARCHAR(255) NOT NULL,
    `video_id` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `collect`;
CREATE TABLE `collect`
(
    `id`       VARCHAR(255),
    `user_id`  VARCHAR(255) NOT NULL,
    `video_id` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`
(
    `id`           VARCHAR(255),
    `user_id`      VARCHAR(255) NOT NULL,
    `video_id`     VARCHAR(255) NOT NULL,
    `content`      TEXT         NOT NULL,
    `created_time` DATETIME     NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `comment_like`;
CREATE TABLE `comment_like`
(
    `id`         varchar(255),
    `user_id`    varchar(255) NOT NULL,
    `comment_id` varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `reply`;
CREATE TABLE `reply`
(
    `id`             VARCHAR(255),
    `user_id`        VARCHAR(255) NOT NULL,
    `target_id`      VARCHAR(255) NOT NULL,
    `target_user_id` VARCHAR(255) NOT NULL,
    `comment_id`     VARCHAR(255) NOT NULL,
    `type`           CHAR(1)      NOT NULL,
    `content`        TEXT         NOT NULL,
    `created_time`   DATETIME     NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `post`;
CREATE TABLE `post`
(
    `id`           VARCHAR(255),
    `content`      VARCHAR(255) NOT NULL,
    `user_id`      VARCHAR(255) NOT NULL,
    `images`       TEXT,
    `created_time` DATETIME     NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `post_like`;
CREATE TABLE `post_like`
(
    `id`      varchar(255),
    `user_id` varchar(255),
    `post_id` varchar(255),
    PRIMARY KEY (`id`)
);