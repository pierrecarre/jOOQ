DROP VIEW v_library
/
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

CREATE TABLE t_language (
  ID INT NOT NULL PRIMARY KEY,
  CD CHAR(2) NOT NULL,
  DESCRIPTION VARCHAR(50) NOT NULL
)
/

CREATE TABLE t_author (
  ID INT NOT NULL PRIMARY KEY,
  FIRST_NAME VARCHAR(50),
  LAST_NAME VARCHAR(50) NOT NULL,
  DATE_OF_BIRTH DATE NOT NULL,
  YEAR_OF_BIRTH INT NOT NULL,
  ADDRESS VARCHAR(200)
)
/

CREATE TABLE t_book (
  ID INT NOT NULL PRIMARY KEY,
  AUTHOR_ID INT NOT NULL,
  TITLE VARCHAR(400) NOT NULL,
  PUBLISHED_IN INT NOT NULL,
  LANGUAGE_ID INT NOT NULL,
  CONTENT_TEXT LONG VARCHAR,
  CONTENT_PDF BLOB,
  FOREIGN KEY (AUTHOR_ID) REFERENCES T_AUTHOR(ID),
  FOREIGN KEY (LANGUAGE_ID) REFERENCES T_LANGUAGE(ID)
)
/

CREATE TABLE x_unused (
  ID INT NOT NULL UNIQUE,
  NAME VARCHAR(10) NOT NULL,
  ID_REF INT,
  NAME_REF VARCHAR(10),
  PRIMARY KEY(ID, NAME),
  FOREIGN KEY(ID_REF, NAME_REF) REFERENCES X_UNUSED(ID, NAME)
)
/

CREATE TABLE x_test_case_64_69 (
    ID INT NOT NULL,
    UNUSED_ID INT,
    PRIMARY KEY(ID),
    FOREIGN KEY(UNUSED_ID) REFERENCES X_UNUSED(ID)
)
/

CREATE TABLE x_test_case_71 (
  ID INT NOT NULL,
  TEST_CASE_64_69_ID SMALLINT,
  PRIMARY KEY(ID),
  FOREIGN KEY(TEST_CASE_64_69_ID) REFERENCES X_TEST_CASE_64_69(ID)
)
/


CREATE VIEW V_LIBRARY (AUTHOR, TITLE) AS
SELECT T_AUTHOR.FIRST_NAME || ' ' || T_AUTHOR.LAST_NAME, T_BOOK.TITLE
FROM T_AUTHOR JOIN T_BOOK ON T_BOOK.AUTHOR_ID = T_AUTHOR.ID
/
  
INSERT INTO t_language VALUES (1, 'en', 'English')
/
INSERT INTO t_language VALUES (2, 'de', 'Deutsch')
/
INSERT INTO t_language VALUES (3, 'fr', 'Français')
/
INSERT INTO t_language VALUES (4, 'pt', 'Português')
/

INSERT INTO t_author VALUES (1, 'George', 'Orwell', '1903-06-25', 1903, null)
/
INSERT INTO t_author VALUES (2, 'Paulo', 'Coelho', '1947-08-24', 1947, null)
/

INSERT INTO t_book VALUES (1, 1, '1984', 1948, 1, 'To know and not to know, to be conscious of complete truthfulness while telling carefully constructed lies, to hold simultaneously two opinions which cancelled out, knowing them to be contradictory and believing in both of them, to use logic against logic, to repudiate morality while laying claim to it, to believe that democracy was impossible and that the Party was the guardian of democracy, to forget, whatever it was necessary to forget, then to draw it back into memory again at the moment when it was needed, and then promptly to forget it again, and above all, to apply the same process to the process itself -- that was the ultimate subtlety; consciously to induce unconsciousness, and then, once again, to become unconscious of the act of hypnosis you had just performed. Even to understand the word ''doublethink'' involved the use of doublethink..', null)
/
INSERT INTO t_book VALUES (2, 1, 'Animal Farm', 1945, 1, null, null)
/
INSERT INTO t_book VALUES (3, 2, 'O Alquimista', 1988, 4, null, null)
/
INSERT INTO t_book VALUES (4, 2, 'Brida', 1990, 2, null, null)
/
