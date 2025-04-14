\c gymdb
CREATE SCHEMA IF NOT EXISTS gym_keycloak;
CREATE SCHEMA IF NOT EXISTS gym_app;

-- Grant permissions to the admin user
GRANT USAGE ON SCHEMA gym_keycloak TO admin;
GRANT CREATE ON SCHEMA gym_keycloak TO admin;
GRANT USAGE ON SCHEMA gym_app TO admin;
GRANT CREATE ON SCHEMA gym_app TO admin;
GRANT ALL PRIVILEGES ON DATABASE gymdb TO admin;

-- Create the 'keycloak' user with password
CREATE USER keycloak WITH PASSWORD 'admin';

-- Create the 'keycloak' database
CREATE DATABASE keycloak;

-- Grant all privileges on the database to the 'keycloak' user
GRANT ALL PRIVILEGES ON DATABASE keycloak TO keycloak;
