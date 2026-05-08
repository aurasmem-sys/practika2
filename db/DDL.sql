-- DDL скрипт для бази даних "Ательє" (SQLite)

/*
  Таблиця: clients
  Тип: Сутність (Entity) / Довідник
  Нормальна форма: 3НФ. Усі атрибути атомарні (1НФ), залежать від первинного ключа (2НФ) і не залежать від інших неключових атрибутів (3НФ).
*/
CREATE TABLE clients (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone_number VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE,
    password VARCHAR(255)
);

/*
  Таблиця: employees
  Тип: Сутність (Entity) / Довідник
  Нормальна форма: 3НФ. Усі атрибути (ім'я, посада, телефон) залежать тільки від id співробітника.
*/
CREATE TABLE employees (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    position VARCHAR(50) NOT NULL,
    phone_number VARCHAR(20) UNIQUE NOT NULL
);

/*
  Таблиця: services
  Тип: Довідник (Reference table)
  Нормальна форма: 3НФ. Зберігає довідкову інформацію про послуги та їх базову вартість.
*/
CREATE TABLE services (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    service_name VARCHAR(100) NOT NULL,
    description TEXT,
    base_price DECIMAL(10, 2) NOT NULL CHECK (base_price >= 0)
);

/*
  Таблиця: materials
  Тип: Довідник (Reference table)
  Нормальна форма: 3НФ. Зберігає довідкову інформацію про матеріали.
*/
CREATE TABLE materials (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    material_name VARCHAR(100) NOT NULL,
    unit VARCHAR(20) NOT NULL,
    price_per_unit DECIMAL(10, 2) NOT NULL CHECK (price_per_unit >= 0)
);

/*
  Таблиця: orders
  Тип: Сутність (Entity) / Операційна таблиця
  Нормальна форма: 3НФ. Зв'язок з клієнтом та співробітником через зовнішні ключі (забезпечення 1:N). Усі інші поля (дата, статус, сума) залежать тільки від первинного ключа id замовлення.
*/
CREATE TABLE orders (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    client_id INTEGER NOT NULL,
    employee_id INTEGER NOT NULL,
    order_date DATE NOT NULL DEFAULT CURRENT_DATE,
    status VARCHAR(50) NOT NULL DEFAULT 'Нове',
    total_price DECIMAL(10, 2) DEFAULT 0 CHECK (total_price >= 0),
    FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE RESTRICT,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE RESTRICT
);

/*
  Таблиця: order_services
  Тип: Таблиця зв'язку (Junction/Bridge table)
  Нормальна форма: 3НФ. Реалізує зв'язок багато-до-багатьох (N:M) між замовленнями і послугами. 
  Атрибут actual_price залежить від складеного первинного ключа, тому що ціна фіксується на момент створення замовлення і не залежить від зміни базової ціни в довіднику послуг.
*/
CREATE TABLE order_services (
    order_id INTEGER NOT NULL,
    service_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1 CHECK (quantity > 0),
    actual_price DECIMAL(10, 2) NOT NULL CHECK (actual_price >= 0),
    PRIMARY KEY (order_id, service_id),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES services(id) ON DELETE RESTRICT
);

/*
  Таблиця: order_materials
  Тип: Таблиця зв'язку (Junction/Bridge table)
  Нормальна форма: 3НФ. Реалізує зв'язок багато-до-багатьох (N:M) між замовленнями і матеріалами.
*/
CREATE TABLE order_materials (
    order_id INTEGER NOT NULL,
    material_id INTEGER NOT NULL,
    quantity DECIMAL(10, 2) NOT NULL CHECK (quantity > 0),
    PRIMARY KEY (order_id, material_id),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (material_id) REFERENCES materials(id) ON DELETE RESTRICT
);
