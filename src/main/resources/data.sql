INSERT INTO `role`(`role_name`)
VALUES ('SUPER_ADMIN'),
       ('ADMIN'),
       ('MODERATOR'),
       ('USER');

INSERT INTO `user`
(`id`,
 `created_at`,
 `disabled`,
 `password`,
 `updated_at`,
 `username`,
 `role_id`)
VALUES
    (1,
        NOW(),
        0,
        '$2a$10$4xp4.uiwO9MDIqP6Q.OpG.pdCoksHTD2MjIYLh6r3dfQxr3tgHCym',
        null,
        'doc@gmail.com',
        1);

INSERT INTO `employee`
(`id`,
 `first_name`,
 `last_name`,
 `user_id`)
VALUES
    (1,
     "John",
     "Wayne",
     1);
