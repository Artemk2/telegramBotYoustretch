CREATE DATABASE youstretch;
--\c youstretch

CREATE TABLE logs (
    id SERIAL PRIMARY KEY,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    message TEXT,
    status TEXT,
    "user" TEXT
);
