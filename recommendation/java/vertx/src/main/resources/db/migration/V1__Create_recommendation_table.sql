CREATE TABLE recommendation (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

INSERT INTO recommendation(name) 
    VALUES ('Star Wars: A New Hope');

INSERT INTO recommendation(name) 
    VALUES ('Star Trek: First Contact');
