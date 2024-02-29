INSERT INTO categories (id, name, description, is_deleted) VALUES (1, 'Fantasy', null, false);
commit;

INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES (1, 'The Lord of the Rings', 'J. R. R. Tolkien', '000000000003', 199.0, null, null, false);
commit;
INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES (2, 'The golden compass', 'Phillip Pullman', '000000000004', 155.0, null, null, false);
commit;

INSERT INTO books_categories (book_id, category_id) VALUES (1, 1);
commit;
INSERT INTO books_categories (book_id, category_id) VALUES (2, 1);
commit;

INSERT INTO shopping_carts (id, user_id) VALUES (1, 1);
commit;

INSERT INTO cart_items (id, shopping_cart_id, book_id, quantity)
VALUES (1, 1, 1, 3);
commit;
INSERT INTO cart_items (id, shopping_cart_id, book_id, quantity)
VALUES (2, 1, 2, 2);
commit;