DROP TABLE IF EXISTS t_book CASCADE
/
DROP TABLE IF EXISTS t_author CASCADE
/
DROP TABLE IF EXISTS t_language CASCADE
/
DROP TABLE IF EXISTS x_test_case_71 CASCADE
/
DROP TABLE IF EXISTS x_test_case_64_69 CASCADE
/
DROP TABLE IF EXISTS x_unused CASCADE
/
DROP TYPE IF EXISTS u_address_type CASCADE
/
DROP TYPE IF EXISTS u_street_type CASCADE
/
DROP TYPE IF EXISTS u_book_status CASCADE
/
DROP TYPE IF EXISTS u_country CASCADE
/

CREATE TYPE u_book_status AS ENUM ('SOLD OUT', 'ON STOCK', 'ORDERED')
/
CREATE TYPE u_country AS ENUM ('Brazil', 'England', 'Germany')
/

CREATE TYPE u_street_type AS (
  street VARCHAR(100),
  number VARCHAR(30)
)
/

CREATE TYPE u_address_type AS (
  street u_street_type,
  zip VARCHAR(50),
  city VARCHAR(50),
  country u_country,
  since DATE,
  code INTEGER
)
/

CREATE TABLE t_language (
  id INTEGER NOT NULL PRIMARY KEY,
  cd CHAR(2) NOT NULL,
  description VARCHAR(50) NOT NULL
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
  id INTEGER NOT NULL PRIMARY KEY,
  first_name VARCHAR(50),
  last_name VARCHAR(50) NOT NULL,
  date_of_birth DATE NOT NULL,
  year_of_birth INTEGER NOT NULL,
  address u_address_type
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
COMMENT ON COLUMN t_author.address IS 'The author''s address'
/

CREATE TABLE t_book (
  id INTEGER NOT NULL PRIMARY KEY,
  author_id INTEGER NOT NULL,
  title VARCHAR(400) NOT NULL,
  published_in INTEGER NOT NULL,
  language_id INTEGER NOT NULL,
  content_text TEXT,
  content_pdf BYTEA,
  status u_book_status
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
COMMENT ON COLUMN t_book.status IS 'The book''s stock status'
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
  id INTEGER NOT NULL UNIQUE,
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

CREATE TABLE x_test_case_64_69 (
    id INTEGER NOT NULL,
    unused_id INTEGER
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
  id INTEGER NOT NULL,
  test_case_64_69_id SMALLINT
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

INSERT INTO t_author VALUES (1, 'George', 'Orwell', TO_DATE('1903-06-25', 'YYYY-MM-DD'), 1903, ROW(ROW('Parliament Hill', '77'), 'NW31A9', 'Hampstead', 'England', '1980-01-01', null))
/
INSERT INTO t_author VALUES (2, 'Paulo', 'Coelho', TO_DATE('1947-08-24', 'YYYY-MM-DD'), 1947, ROW(ROW('Caixa Postal', '43.003'), null, 'Rio de Janeiro', 'Brazil', '1940-01-01', 2))
/

INSERT INTO t_book VALUES (1, 1, '1984', 1948, 1, 'To know and not to know, to be conscious of complete truthfulness while telling carefully constructed lies, to hold simultaneously two opinions which cancelled out, knowing them to be contradictory and believing in both of them, to use logic against logic, to repudiate morality while laying claim to it, to believe that democracy was impossible and that the Party was the guardian of democracy, to forget, whatever it was necessary to forget, then to draw it back into memory again at the moment when it was needed, and then promptly to forget it again, and above all, to apply the same process to the process itself -- that was the ultimate subtlety; consciously to induce unconsciousness, and then, once again, to become unconscious of the act of hypnosis you had just performed. Even to understand the word ''doublethink'' involved the use of doublethink..', null, 'ORDERED')
/
INSERT INTO t_book VALUES (2, 1, 'Animal Farm', 1945, 1, null, null, 'ON STOCK')
/
INSERT INTO t_book VALUES (3, 2, 'O Alquimista', 1988, 4, null, null, 'ON STOCK')
/
INSERT INTO t_book VALUES (4, 2, 'Brida', 1990, 2, null, null, 'SOLD OUT')
/
