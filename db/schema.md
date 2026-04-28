# Схеми Бази Даних Ательє

## 1. Концептуальна схема (Нотація Пітера Чена)

Нижче наведена концептуальна модель бази даних у нотації Пітера Чена, реалізована через Mermaid (прямокутники — сутності, ромби — зв'язки, овали — атрибути).

```mermaid
graph TD
    %% Сутності (Entities)
    C[Клієнт]
    E[Співробітник]
    O[Замовлення]
    S[Послуга]
    M[Матеріал]

    %% Зв'язки (Relationships)
    Pl{Розміщує}
    Man{Оформлює}
    Inc{Включає}
    Use{Використовує}

    %% З'єднання сутностей та зв'язків
    C ---|1| Pl ---|N| O
    E ---|1| Man ---|N| O
    O ---|N| Inc ---|M| S
    O ---|N| Use ---|M| M

    %% Атрибути для Клієнта (Овали)
    C_id([ID Клієнта]) --- C
    C_fn([Ім'я]) --- C
    C_ln([Прізвище]) --- C
    C_ph([Телефон]) --- C

    %% Атрибути для Замовлення
    O_id([ID Замовлення]) --- O
    O_date([Дата]) --- O
    O_stat([Статус]) --- O
    O_tot([Сума]) --- O

    %% Атрибути для Співробітника
    E_id([ID Співробітника]) --- E
    E_fn([Ім'я]) --- E
    E_pos([Посада]) --- E

    %% Атрибути для Послуги
    S_id([ID Послуги]) --- S
    S_name([Назва]) --- S
    S_price([Ціна]) --- S

    %% Атрибути для Матеріалу
    M_id([ID Матеріалу]) --- M
    M_name([Назва]) --- M
    M_unit([Одиниця]) --- M
```

## 2. Логічна схема (Нотація Crow's Foot)

Нижче наведена логічна модель, що відображає таблиці в реляційній базі даних (включаючи проміжні таблиці для зв'язків багато-до-багатьох) у нотації Crow's Foot.

```mermaid
erDiagram
    clients ||--o{ orders : "розміщує (places)"
    employees ||--o{ orders : "оформлює/виконує (manages)"
    
    orders ||--|{ order_services : "містить (contains)"
    services ||--o{ order_services : "входить до (included_in)"
    
    orders ||--o{ order_materials : "використовує (uses)"
    materials ||--o{ order_materials : "використовується_в (used_in)"

    clients {
        int id PK
        string first_name
        string last_name
        string phone_number
        string email
    }
    employees {
        int id PK
        string first_name
        string last_name
        string position
        string phone_number
    }
    services {
        int id PK
        string service_name
        text description
        decimal base_price
    }
    materials {
        int id PK
        string material_name
        string unit
        decimal price_per_unit
    }
    orders {
        int id PK
        int client_id FK
        int employee_id FK
        date order_date
        string status
        decimal total_price
    }
    order_services {
        int order_id PK, FK
        int service_id PK, FK
        int quantity
        decimal actual_price
    }
    order_materials {
        int order_id PK, FK
        int material_id PK, FK
        decimal quantity
    }
```
