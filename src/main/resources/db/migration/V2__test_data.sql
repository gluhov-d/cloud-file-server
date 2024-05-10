INSERT INTO users (username, password, role, first_name, last_name, enabled, created_at, updated_at, status, created_by, modified_by) VALUES
        ('admin_user', 'WzjFMN3iIOsAGIu8SXaM53KqiJakyJ+YBXwAwUTZ9BE=', 'ADMIN', 'Admin', 'User', TRUE, NOW(), NOW(), 'ACTIVE', NULL, NULL),
        ('user_one', 'WzjFMN3iIOsAGIu8SXaM53KqiJakyJ+YBXwAwUTZ9BE=', 'USER', 'User', 'One', TRUE, NOW(), NOW(), 'ACTIVE', '1', NULL),
        ('moderator_user', 'WzjFMN3iIOsAGIu8SXaM53KqiJakyJ+YBXwAwUTZ9BE=', 'MODERATOR', 'Moderator', 'User', TRUE, NOW(), NOW(), 'ACTIVE', '1', NULL);
INSERT INTO files (name, location, user_id, status, created_at, updated_at, created_by, modified_by) VALUES
        ('2023-11-03 10.52.36.jpg', 'https://gluhov-file-storage.s3.amazonaws.com/2023-11-03%2010.52.36.jpg', 1, 'ACTIVE', NOW(), NOW(), '1','1'),
        ('2023-11-03 10.52.36.jpg', 'https://gluhov-file-storage.s3.amazonaws.com/2023-11-03%2010.52.36.jpg', 2, 'ACTIVE', NOW(), NOW(), '2','2'),
        ('2023-11-03 10.52.36.jpg', 'https://gluhov-file-storage.s3.amazonaws.com/2023-11-03%2010.52.36.jpg', 3, 'ACTIVE', NOW(), NOW(), '3','3');
INSERT INTO events (user_id, file_id, status, created_at, updated_at, created_by, modified_by) VALUES
        (1, 1, 'ACTIVE', NOW(), NOW(), '1', '1'),
        (2, 2, 'ACTIVE', NOW(), NOW(), '2', '2'),
        (3, 3, 'ACTIVE', NOW(), NOW(), '3', '3');