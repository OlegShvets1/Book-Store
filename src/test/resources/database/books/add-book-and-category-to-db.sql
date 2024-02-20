INSERT INTO categories (id, name, description, is_deleted) VALUES (1, 'History', "Book about histiry", false);

INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES (1, 'Shantaram', 'Gregory David Roberts', '000000000001', 139.0,
        'about the life of an escaped Australian convict with the alias Lin who comes to India in order to evade his torturous fate in the prison', null, false);

INSERT INTO books_categories (book_id, category_id) VALUES (1, 1);
