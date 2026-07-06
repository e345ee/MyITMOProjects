DROP TABLE IF EXISTS location CASCADE;
DROP TABLE IF EXISTS human CASCADE;
DROP TABLE IF EXISTS way_document CASCADE;
DROP TABLE IF EXISTS abstract_road CASCADE;
DROP TABLE IF EXISTS wall CASCADE;
DROP TABLE IF EXISTS human_eye_contact CASCADE;
DROP TABLE IF EXISTS eye_contact CASCADE;

CREATE TABLE location
(
id SERIAL PRIMARY KEY,
name TEXT NOT NULL,
area TEXT NOT NULL,
timezone INT NOT NULL
CHECK (timezone IN (-12, -11, -10, -9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1))
);

CREATE TABLE human
(
id SERIAL PRIMARY KEY,
name TEXT NOT NULL,
surname TEXT NOT NULL,
age INT DEFAULT 0,
location_id INT REFERENCES location(id)
);


CREATE TABLE way_document
(
id SERIAL PRIMARY KEY,
human_id INT REFERENCES human(id),
date DATE NOT NULL
);

CREATE TABLE wall
(
id SERIAL PRIMARY KEY,
color TEXT NOT NULL,
material TEXT NOT NULL
);

CREATE TABLE eye_contact
(
id SERIAL PRIMARY KEY,
wall_id INT REFERENCES wall(id),
time TIME NOT NULL,
date DATE NOT NULL
);

CREATE TABLE human_eye_contact
(
id SERIAL PRIMARY KEY,
id_human INT REFERENCES human(id),
id_eye_contact INT REFERENCES eye_contact(id)
);

CREATE TABLE abstract_road
(
id SERIAL PRIMARY KEY,
length INT NOT NULL,
location_A_id INT REFERENCES location(id),
location_B_id INT REFERENCES location(id),
document_id INT REFERENCES way_document(id),
direction TEXT NOT NULL, CHECK ( direction IN ('up', 'down', 'forward')),
number_of_lanterns INT DEFAULT 0,
wall_id INT REFERENCES wall(id),
wall_type CHAR(1) NOT NULL, CHECK (wall_type IN ('R', 'T'))
);

INSERT INTO location (name, area, timezone) VALUES ('озеро', 'Америка', 5),
 ('болото', 'Америка', 3),
 ('поле', 'Грузия', 9),
 ('озеро', 'Россия', -7);

INSERT INTO human (name, surname, age, location_id) VALUES ('Григорий', 'Садовой', 5, 1),
 ('Гена', 'Букин', 56, 1),
 ('Лупа', 'Пупа', 90, 3);

INSERT INTO way_document (date, human_id) VALUES ('2016-11-23', 2),
('2236-11-13', 1),
('1999-11-8', 3);

INSERT INTO wall (color, material) VALUES ('green', 'silk'),
('red', 'brick'),
('green', 'brick');

INSERT INTO eye_contact (wall_id, time, date)
 VALUES (1, '11:13:44+01', '2016-11-23'),
(2, '11:16:44+01', '2017-11-23'),
(3, '11:19:44+01', '2019-11-23');

INSERT INTO human_eye_contact (id_human, id_eye_contact) VALUES (1, 3),
(2, 2),
(3, 1);


INSERT INTO abstract_road (length, location_A_id, location_B_id, document_id, direction, number_of_lanterns, wall_id, wall_type) VALUES (344, 1, 4, 2, 'up', 34, 1, 'R'),
 (516, 3, 2, 3, 'up', 47, 2, 'T'),
 (47, 1, 1, 1, 'down', 2, 3, 'R');

