DROP TABLE t_book
/
DROP TABLE t_author
/
DROP TABLE t_language
/
DROP TABLE x_test_case_71
/
DROP TABLE x_test_case_64_69
/
DROP TABLE x_unused
/
DROP PROCEDURE p_author_exists
/
DROP FUNCTION f_author_exists
/
DROP PACKAGE library
/


CREATE TABLE t_language (
  id NUMBER(7) NOT NULL PRIMARY KEY,
  cd CHAR(2) NOT NULL,
  description VARCHAR2(50) NOT NULL
)
/
COMMENT ON TABLE t_language IS 'An entity holding language master data'
/
COMMENT ON COLUMN t_language.id IS 'The language ID'
/
COMMENT ON COLUMN t_language.cd IS 'The language ISO code'
/
COMMENT ON COLUMN t_language.description IS 'The language description'
/


CREATE TABLE t_author (
  id NUMBER(7) NOT NULL PRIMARY KEY,
  first_name VARCHAR2(50),
  last_name VARCHAR2(50) NOT NULL,
  date_of_birth DATE NOT NULL,
  year_of_birth NUMBER(7) NOT NULL,
  address VARCHAR2(200)
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
  language_id INTEGER NOT NULL,
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
COMMENT ON COLUMN t_book.language_id IS  'The language of the book'
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
ALTER TABLE t_book
  ADD CONSTRAINT book_language_id
  FOREIGN KEY (language_id)
  REFERENCES t_language (id)
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


CREATE TABLE x_test_case_64_69 (
  id NUMBER(7) NOT NULL,
  unused_id NUMBER(7)
)
/
ALTER TABLE x_test_case_64_69
  ADD PRIMARY KEY (id)
/
ALTER TABLE x_test_case_64_69
  ADD CONSTRAINT x_test_case_64_69_ref
  FOREIGN KEY (unused_id)
  REFERENCES x_unused (id)
/

CREATE TABLE x_test_case_71 (
	id NUMBER(7) NOT NULL,
	test_case_64_69_id NUMBER(4)
)
/

ALTER TABLE x_test_case_71
  ADD PRIMARY KEY (id)
/
ALTER TABLE x_test_case_71
  ADD CONSTRAINT x_test_case_71_ref
  FOREIGN KEY (test_case_64_69_id)
  REFERENCES x_test_case_64_69 (id)
/

CREATE OR REPLACE VIEW v_library (author, title) AS
SELECT a.first_name || ' ' || a.last_name, b.title
FROM t_author a JOIN t_book b ON b.author_id = a.id
/
  
INSERT INTO t_language VALUES (1, 'en', 'English')
/
INSERT INTO t_language VALUES (2, 'de', 'Deutsch')
/
INSERT INTO t_language VALUES (3, 'fr', 'Français')
/
INSERT INTO t_language VALUES (4, 'pt', 'Português')
/

INSERT INTO t_author VALUES (1, 'George', 'Orwell', TO_DATE('1903-06-25', 'YYYY-MM-DD'), 1903, null)
/
INSERT INTO t_author VALUES (2, 'Paulo', 'Coelho', TO_DATE('1947-08-24', 'YYYY-MM-DD'), 1947, null)
/

INSERT INTO t_book VALUES (1, 1, '1984', 1948, 1, 'To know and not to know, to be conscious of complete truthfulness while telling carefully constructed lies, to hold simultaneously two opinions which cancelled out, knowing them to be contradictory and believing in both of them, to use logic against logic, to repudiate morality while laying claim to it, to believe that democracy was impossible and that the Party was the guardian of democracy, to forget, whatever it was necessary to forget, then to draw it back into memory again at the moment when it was needed, and then promptly to forget it again, and above all, to apply the same process to the process itself -- that was the ultimate subtlety; consciously to induce unconsciousness, and then, once again, to become unconscious of the act of hypnosis you had just performed. Even to understand the word ''doublethink'' involved the use of doublethink..', null)
/
INSERT INTO t_book VALUES (2, 1, 'Animal Farm', 1945, 1, null, null)
/
INSERT INTO t_book VALUES (3, 2, 'O Alquimista', 1988, 4, null, null)
/
INSERT INTO t_book VALUES (4, 2, 'Brida', 1990, 2, null, null)
/

CREATE OR REPLACE PROCEDURE p_author_exists (author_name VARCHAR2, result OUT NUMBER)
IS
  v_result NUMBER(1);
BEGIN
  SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END
    INTO v_result
    FROM t_author 
   WHERE first_name LIKE author_name 
      OR last_name LIKE author_name;
      
  result := v_result;
END p_author_exists;
/

CREATE OR REPLACE FUNCTION f_author_exists (author_name VARCHAR2)
RETURN NUMBER
IS
  v_result NUMBER(1);
BEGIN
  SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END
    INTO v_result
    FROM t_author 
   WHERE first_name LIKE author_name 
      OR last_name LIKE author_name;
      
  return v_result;
END f_author_exists;
/

CREATE OR REPLACE PACKAGE library AS
	PROCEDURE p_author_exists (author_name VARCHAR2, result OUT NUMBER);
	FUNCTION f_author_exists (author_name VARCHAR2) RETURN NUMBER;
END library;
/

CREATE OR REPLACE PACKAGE BODY library AS
	PROCEDURE p_author_exists (author_name VARCHAR2, result OUT NUMBER) IS
	BEGIN
		test.p_author_exists(author_name, result);
	END p_author_exists;
	
	FUNCTION f_author_exists (author_name VARCHAR2) RETURN NUMBER IS
	BEGIN
		return test.f_author_exists(author_name);
	END f_author_exists;
END library;
/
