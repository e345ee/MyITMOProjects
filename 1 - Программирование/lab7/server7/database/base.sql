DROP TYPE IF EXISTS UNIT_OF_MEASURE_ENUM CASCADE;
DROP TYPE IF EXISTS ORGANIZATION_TYPE_ENUM CASCADE;

DROP TABLE IF EXISTS Product CASCADE;
DROP TABLE IF EXISTS Coordinates CASCADE;
DROP TABLE IF EXISTS Organization CASCADE;
DROP TABLE IF EXISTS Address CASCADE;
DROP TABLE IF EXISTS "User" CASCADE;
DROP TABLE IF EXISTS Creator CASCADE;

-- Создание перечислений
CREATE TYPE UNIT_OF_MEASURE_ENUM AS ENUM ('METERS', 'CENTIMETERS', 'MILLIGRAMS');
CREATE TYPE ORGANIZATION_TYPE_ENUM AS ENUM ('GOVERNMENT', 'PRIVATE_LIMITED_COMPANY', 'OPEN_JOINT_STOCK_COMPANY');

-- Таблица для Coordinates
CREATE TABLE IF NOT EXISTS Coordinates
(
    id
    SERIAL
    PRIMARY
    KEY,
    x
    FLOAT
    NOT
    NULL,
    y
    INTEGER
    NOT
    NULL
);

-- Таблица для Address
CREATE TABLE IF NOT EXISTS Address
(
    id
    SERIAL
    PRIMARY
    KEY,
    street
    VARCHAR
(
    148
), -- строка может быть NULL
    zip_code VARCHAR
(
    14
) NOT NULL -- строка не может быть NULL
    );

-- Таблица для Organization
CREATE TABLE IF NOT EXISTS Organization
(
    id
    BIGSERIAL
    PRIMARY
    KEY, -- генерируется автоматически
    name
    TEXT
    NOT
    NULL
    CHECK (
    LENGTH
(
    name
) > 0), -- не может быть NULL и не может быть пустой
    employees_count BIGINT NOT NULL CHECK
(
    employees_count >
    0
), -- должно быть больше 0
    type ORGANIZATION_TYPE_ENUM, -- может быть NULL
    official_address_id INTEGER REFERENCES Address
(
    id
) ON DELETE SET NULL -- может быть NULL
    );

CREATE TABLE IF NOT EXISTS Product
(
    id SERIAL PRIMARY KEY, -- генерируется автоматически
    name TEXT NOT NULL CHECK (LENGTH(name) > 0), -- не может быть NULL и не может быть пустой
    coordinates_id INTEGER REFERENCES Coordinates(id) ON DELETE RESTRICT NOT NULL, -- ссылка на координаты
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- генерируется автоматически
    price INTEGER NOT NULL CHECK (price > 0), -- не может быть NULL, должно быть больше 0
    part_number VARCHAR(69) CHECK (LENGTH(part_number) >= 20 OR part_number IS NULL), -- длина строки 20-69 символов, может быть NULL
    manufacture_cost FLOAT, -- может быть NULL
    unit_of_measure UNIT_OF_MEASURE_ENUM, -- может быть NULL
    manufacturer_id BIGINT REFERENCES Organization(id) ON DELETE RESTRICT NOT NULL -- ссылка на производителя
    );

-- Создание частичного индекса для уникальных значений part_number, исключая NULL
CREATE UNIQUE INDEX product_part_number_unique_idx ON Product(part_number) WHERE part_number IS NOT NULL;

-- Таблица для пользователей
CREATE TABLE IF NOT EXISTS "User"
(
    id
    SERIAL
    PRIMARY
    KEY,  -- уникальный идентификатор, генерируется автоматически
    name
    TEXT
    NOT
    NULL, -- имя пользователя, не может быть NULL
    passwd_hash
    TEXT
    NOT
    NULL, -- хэш пароля, не может быть NULL
    passwd_salt
    TEXT
    NOT
    NULL  -- соль для пароля, не может быть NULL
);

-- Таблица для отслеживания авторов объектов
CREATE TABLE IF NOT EXISTS Creator
(
    user_id
    BIGINT
    REFERENCES
    "User"
(
    id
) ON DELETE CASCADE NOT NULL, -- при удалении пользователя удаляются и созданные им продукты
    product_id BIGINT REFERENCES Product
(
    id
)
  ON DELETE CASCADE NOT NULL UNIQUE, -- при удалении продукта удаляются записи о его создании
    PRIMARY KEY
(
    user_id,
    product_id
) -- составной первичный ключ
    );