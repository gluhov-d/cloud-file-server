INSERT INTO users (username, password, role, first_name, last_name, enabled, created_at, updated_at, status, created_by, modified_by) VALUES
        ('admin_user', 'WzjFMN3iIOsAGIu8SXaM53KqiJakyJ+YBXwAwUTZ9BE=', 'ADMIN', 'Admin', 'User', TRUE, NOW(), NOW(), 'ACTIVE', NULL, NULL),
        ('user_one', 'WzjFMN3iIOsAGIu8SXaM53KqiJakyJ+YBXwAwUTZ9BE=', 'USER', 'User', 'One', TRUE, NOW(), NOW(), 'ACTIVE', '1', NULL),
        ('user_two', 'WzjFMN3iIOsAGIu8SXaM53KqiJakyJ+YBXwAwUTZ9BE=', 'USER', 'User', 'Two', TRUE, NOW(), NOW(), 'ACTIVE', '1', NULL);