CREATE TABLE IF NOT EXISTS clients_lab (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    balance INT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS operations_lab (
    id SERIAL PRIMARY KEY,
    client_id INT REFERENCES clients_lab(id),
    amount INT NOT NULL,
    operation_type TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

INSERT INTO clients_lab(name, balance)
SELECT 'Ivan', 1000
WHERE NOT EXISTS (SELECT 1 FROM clients_lab WHERE name = 'Ivan');

INSERT INTO clients_lab(name, balance)
SELECT 'Maria', 1500
WHERE NOT EXISTS (SELECT 1 FROM clients_lab WHERE name = 'Maria');

INSERT INTO operations_lab(client_id, amount, operation_type)
SELECT c.id, 100, 'initial'
FROM clients_lab c
WHERE c.name = 'Ivan'
  AND NOT EXISTS (SELECT 1 FROM operations_lab WHERE operation_type = 'initial' AND client_id = c.id);

INSERT INTO operations_lab(client_id, amount, operation_type)
SELECT c.id, 200, 'initial'
FROM clients_lab c
WHERE c.name = 'Maria'
  AND NOT EXISTS (SELECT 1 FROM operations_lab WHERE operation_type = 'initial' AND client_id = c.id);
