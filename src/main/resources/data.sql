INSERT IGNORE INTO authority VALUE (1, 'ROLE_ADMIN');
INSERT IGNORE INTO authority VALUE (2, 'ROLE_EDITOR');
INSERT IGNORE INTO authority VALUE (3, 'ROLE_READER');
INSERT IGNORE INTO authority VALUE (4, 'ROLE_MODERATOR');
INSERT IGNORE INTO authority VALUE (5, 'ROLE_GUEST');

-- tabelle di dominio, che non hanno ForeignKey

-- viste
CREATE OR REPLACE VIEW visible_post AS SELECT * FROM post WHERE published = true;