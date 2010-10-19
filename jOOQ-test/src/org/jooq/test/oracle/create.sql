DROP TABLE t_book
/
DROP TABLE t_author
/
DROP TABLE x_unused_2
/
DROP TABLE x_unused
/

CREATE TABLE t_author (
  id NUMBER(7) NOT NULL PRIMARY KEY,
  first_name VARCHAR2(50) NOT NULL,
  last_name VARCHAR2(50) NOT NULL,
  date_of_birth DATE NOT NULL,
  year_of_birth NUMBER(7) NOT NULL
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
	id NUMBER(7) NOT NULL PRIMARY KEY,
	author_id NUMBER(7) NOT NULL,
	title VARCHAR2(400) NOT NULL,
  published_in NUMBER(7) NOT NULL,
  content_text CLOB,
  content_pdf BLOB
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
COMMENT ON COLUMN t_book.published_in IS  'The year the book was published in'
/
COMMENT ON COLUMN t_book.content_text IS 'Some textual content of the book'
/
COMMENT ON COLUMN t_book.content_pdf IS 'Some binary content of the book'
/

ALTER TABLE t_book
  ADD CONSTRAINT book_author_id
  FOREIGN KEY (author_id)
  REFERENCES t_author (id)
/

CREATE TABLE x_unused (
	id NUMBER(7) NOT NULL UNIQUE,
	name VARCHAR(10) NOT NULL,
	id_ref NUMBER(7),
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


CREATE TABLE x_unused_2 (
  id NUMBER(7) NOT NULL,
  unused_id NUMBER(7)
)
/
ALTER TABLE x_unused_2
  ADD PRIMARY KEY (id)
/
ALTER TABLE x_unused_2
  ADD CONSTRAINT unused_2_unused_ref
  FOREIGN KEY (unused_id)
  REFERENCES x_unused (id)
/

CREATE OR REPLACE VIEW v_library (author, title) AS
SELECT a.first_name || ' ' || a.last_name, b.title
FROM t_author a JOIN t_book b ON b.author_id = a.id
/
  
INSERT INTO t_author VALUES (1, 'George', 'Orwell', TO_DATE('1903-06-25', 'YYYY-MM-DD'), 1903)
/
INSERT INTO t_author VALUES (2, 'Paulo', 'Coelho', TO_DATE('1947-08-24', 'YYYY-MM-DD'), 1947)
/

INSERT INTO t_book VALUES (1, 1, '1984', 1948, 'To know and not to know, to be conscious of complete truthfulness while telling carefully constructed lies, to hold simultaneously two opinions which cancelled out, knowing them to be contradictory and believing in both of them, to use logic against logic, to repudiate morality while laying claim to it, to believe that democracy was impossible and that the Party was the guardian of democracy, to forget, whatever it was necessary to forget, then to draw it back into memory again at the moment when it was needed, and then promptly to forget it again, and above all, to apply the same process to the process itself -- that was the ultimate subtlety; consciously to induce unconsciousness, and then, once again, to become unconscious of the act of hypnosis you had just performed. Even to understand the word ''doublethink'' involved the use of doublethink..', null)
/
INSERT INTO t_book VALUES (2, 1, 'Animal Farm', 1945, null, null)
/
INSERT INTO t_book VALUES (3, 2, 'O Alquimista', 1988, null, null)
/
INSERT INTO t_book VALUES (4, 2, 'Brida', 1990, null, null)
/
