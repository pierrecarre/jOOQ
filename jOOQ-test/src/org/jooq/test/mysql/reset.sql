TRUNCATE t_book_to_book_store
/
TRUNCATE t_book_store
/
TRUNCATE t_book
/
TRUNCATE t_author
/
TRUNCATE t_language
/

INSERT INTO t_language VALUES (1, 'en', 'English')
/
INSERT INTO t_language VALUES (2, 'de', 'Deutsch')
/
INSERT INTO t_language VALUES (3, 'fr', 'Français')
/
INSERT INTO t_language VALUES (4, 'pt', NULL)
/

INSERT INTO t_author VALUES (1, 'George', 'Orwell', '1903-06-25', 1903, null);
/
INSERT INTO t_author VALUES (2, 'Paulo', 'Coelho', '1947-08-24', 1947, null);
/

INSERT INTO t_book VALUES (1, 1, null, null, '1984', 1948, 1, 'To know and not to know, to be conscious of complete truthfulness while telling carefully constructed lies, to hold simultaneously two opinions which cancelled out, knowing them to be contradictory and believing in both of them, to use logic against logic, to repudiate morality while laying claim to it, to believe that democracy was impossible and that the Party was the guardian of democracy, to forget, whatever it was necessary to forget, then to draw it back into memory again at the moment when it was needed, and then promptly to forget it again, and above all, to apply the same process to the process itself -- that was the ultimate subtlety; consciously to induce unconsciousness, and then, once again, to become unconscious of the act of hypnosis you had just performed. Even to understand the word ''doublethink'' involved the use of doublethink..', null, 'ORDERED')
/
INSERT INTO t_book VALUES (2, 1, null, null, 'Animal Farm', 1945, 1, null, null, 'ON STOCK')
/
INSERT INTO t_book VALUES (3, 2, null, null, 'O Alquimista', 1988, 4, null, null, 'ON STOCK')
/
INSERT INTO t_book VALUES (4, 2, null, null, 'Brida', 1990, 2, null, null, 'SOLD OUT')
/

INSERT INTO t_book_store VALUES 
	('Orell Füssli'),
	('Ex Libris'),
	('Buchhandlung im Volkshaus')
/

INSERT INTO t_book_to_book_store VALUES 
	('Orell Füssli', 1, 10),
	('Orell Füssli', 2, 10),
	('Orell Füssli', 3, 10),
	('Ex Libris', 1, 1),
	('Ex Libris', 3, 2),
	('Buchhandlung im Volkshaus', 3, 1)
/

