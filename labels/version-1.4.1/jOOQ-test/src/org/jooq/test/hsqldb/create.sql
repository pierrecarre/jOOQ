DROP VIEW IF EXISTS v_library;
/
DROP TABLE IF EXISTS t_book;
/
DROP TABLE IF EXISTS t_author;
/
DROP TABLE IF EXISTS x_unused;
/
DROP PROCEDURE IF EXISTS p_author_exists;
/
DROP FUNCTION IF EXISTS f_author_exists;
/

CREATE TABLE t_author (
	ID INT NOT NULL PRIMARY KEY,
	FIRST_NAME VARCHAR(50) NOT NULL,
	LAST_NAME VARCHAR(50) NOT NULL,
	DATE_OF_BIRTH DATE NOT NULL,
	YEAR_OF_BIRTH INT NOT NULL
);
/

CREATE TABLE t_book (
	ID INT NOT NULL PRIMARY KEY,
	AUTHOR_ID INT NOT NULL,
	TITLE VARCHAR(400) NOT NULL,
	PUBLISHED_IN INT NOT NULL,
	CONTENT_TEXT LONGVARCHAR,
	CONTENT_PDF LONGVARBINARY,
	FOREIGN KEY (AUTHOR_ID) REFERENCES T_AUTHOR(ID)
);
/

CREATE TABLE x_unused (
	ID INT NOT NULL,
	NAME VARCHAR(10) NOT NULL,
	ID_REF INT,
	NAME_REF VARCHAR(10),
	PRIMARY KEY(ID, NAME),
	FOREIGN KEY(ID_REF, NAME_REF) REFERENCES X_UNUSED(ID, NAME)
);
/

CREATE VIEW V_LIBRARY (AUTHOR, TITLE) AS
SELECT T_AUTHOR.FIRST_NAME + ' ' + T_AUTHOR.LAST_NAME, T_BOOK.TITLE
FROM T_AUTHOR JOIN T_BOOK ON T_BOOK.AUTHOR_ID = T_AUTHOR.ID;
/
  
INSERT INTO t_author VALUES (1, 'George', 'Orwell', '1903-06-25', 1903);
/
INSERT INTO t_author VALUES (2, 'Paulo', 'Coelho', '1947-08-24', 1947);
/

INSERT INTO t_book VALUES (1, 1, '1984', 1984, 'To know and not to know, to be conscious of complete truthfulness while telling carefully constructed lies, to hold simultaneously two opinions which cancelled out, knowing them to be contradictory and believing in both of them, to use logic against logic, to repudiate morality while laying claim to it, to believe that democracy was impossible and that the Party was the guardian of democracy, to forget, whatever it was necessary to forget, then to draw it back into memory again at the moment when it was needed, and then promptly to forget it again, and above all, to apply the same process to the process itself -- that was the ultimate subtlety; consciously to induce unconsciousness, and then, once again, to become unconscious of the act of hypnosis you had just performed. Even to understand the word ''doublethink'' involved the use of doublethink..', null);
/
INSERT INTO t_book VALUES (2, 1, 'Animal Farm', 1945, null, null);
/
INSERT INTO t_book VALUES (3, 2, 'O Alquimista', 1988, null, null);
/
INSERT INTO t_book VALUES (4, 2, 'Brida', 1990, null, null);
/
