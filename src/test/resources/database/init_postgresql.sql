CREATE TABLE accounts (
                          user_id serial PRIMARY KEY,
                          username VARCHAR ( 50 ) UNIQUE NOT NULL,
                          password VARCHAR ( 50 ) NOT NULL,
                          email VARCHAR ( 255 ) UNIQUE NOT NULL,
                          created_on TIMESTAMP NOT NULL,
                          last_login TIMESTAMP
);

CREATE TABLE roles(
                      role_id serial PRIMARY KEY,
                      role_name VARCHAR (255) UNIQUE NOT NULL
);

CREATE TABLE account_roles (
                               user_id INT NOT NULL,
                               role_id INT NOT NULL,
                               grant_date TIMESTAMP,
                               PRIMARY KEY (user_id, role_id),
                               FOREIGN KEY (role_id)
                                   REFERENCES roles (role_id),
                               FOREIGN KEY (user_id)
                                   REFERENCES accounts (user_id)
);

-- Test data: two roles
INSERT INTO roles VALUES (0, 'admin');
INSERT INTO roles VALUES (1, 'user');

-- Test data: two users
INSERT INTO accounts VALUES (1, 'user1', 'password1', 'user1@email.com', NOW(), NULL);
INSERT INTO account_roles VALUES (1, 1, NOW());
INSERT INTO accounts VALUES (2, 'user2', 'password2', 'user2@email.com', NOW(), NULL);
INSERT INTO account_roles VALUES (2, 1, NOW());
