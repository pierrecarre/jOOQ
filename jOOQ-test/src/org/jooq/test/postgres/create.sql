DROP TABLE IF EXISTS t_book CASCADE
/
DROP TABLE IF EXISTS t_author CASCADE
/
DROP TABLE IF EXISTS x_unused CASCADE
/

CREATE TABLE t_author (
  id INTEGER NOT NULL PRIMARY KEY,
  first_name VARCHAR(50) NOT NULL,
  last_name VARCHAR(50) NOT NULL,
  date_of_birth DATE NOT NULL,
  year_of_birth INTEGER NOT NULL
)
/
COMMENT ON TABLE t_author IS 'An entity holding authors of books'
/
COMMENT ON COLUMN t_author.id IS 'The author ID'
/
COMMENT ON COLUMN t_author.first_name IS 'The author''s first name'
/
COMMENT ON COLUMN t_author.last_name IS 'The author''s last name'
/
COMMENT ON COLUMN t_author.date_of_birth IS 'The author''s date of birth'
/
COMMENT ON COLUMN t_author.year_of_birth IS 'The author''s year of birth'
/

CREATE TABLE t_book (
	id INTEGER NOT NULL PRIMARY KEY,
	author_id INTEGER NOT NULL,
	title VARCHAR(400) NOT NULL
)
/
COMMENT ON TABLE t_book IS 'An entity holding books'
/
COMMENT ON COLUMN t_book.id IS 'The book ID'
/
COMMENT ON COLUMN t_book.author_id IS 'The author ID in entity ''author'''
/
COMMENT ON COLUMN t_book.title IS 'The book''s title'
/
ALTER TABLE t_book
  ADD CONSTRAINT book_author_id
  FOREIGN KEY (author_id)
  REFERENCES t_author (id)
/

CREATE TABLE x_unused (
	id INTEGER NOT NULL,
	name VARCHAR(10) NOT NULL,
	id_ref INTEGER,
	name_ref VARCHAR(10)
)
/
COMMENT ON TABLE x_unused IS 'An unused table in the same schema.'
/

ALTER TABLE x_unused
  ADD PRIMARY KEY (id, name)
/
ALTER TABLE x_unused
  ADD CONSTRAINT unused_self_ref
  FOREIGN KEY (id_ref, name_ref)
  REFERENCES x_unused (id, name)
/

CREATE OR REPLACE VIEW v_library (author, title) AS
SELECT a.first_name || ' ' || a.last_name, b.title
FROM t_author a JOIN t_book b ON b.author_id = a.id
/
  
INSERT INTO t_author VALUES (1, 'George', 'Orwell', TO_DATE('1903-06-25', 'YYYY-MM-DD'), 1903)
/
INSERT INTO t_author VALUES (2, 'Paulo', 'Coelho', TO_DATE('1947-08-24', 'YYYY-MM-DD'), 1947)
/

INSERT INTO t_book VALUES (1, 1, '1984')
/
INSERT INTO t_book VALUES (2, 1, 'Animal Farm')
/
INSERT INTO t_book VALUES (3, 2, 'O Alquimista')
/
INSERT INTO t_book VALUES (4, 2, 'Brida')
/
