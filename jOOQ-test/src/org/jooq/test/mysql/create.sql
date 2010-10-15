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
	ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT 'The author ID',
	FIRST_NAME VARCHAR(50) NOT NULL COMMENT 'The author''s first name',
	LAST_NAME VARCHAR(50) NOT NULL COMMENT 'The author''s last name',
	DATE_OF_BIRTH DATE NOT NULL COMMENT 'The author''s date of birth',
	YEAR_OF_BIRTH INT NOT NULL COMMENT 'The author''s year of birth'
) ENGINE = InnoDB
  COMMENT = 'An entity holding authors of books';
/
  
CREATE TABLE t_book (
	ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT 'The book ID',
	AUTHOR_ID INT NOT NULL COMMENT 'The author ID in entity ''author''',
	TITLE TEXT NOT NULL COMMENT 'The book''s title',
	PUBLISHED_IN INT NOT NULL COMMENT 'The year the book was published in',
	CONTENT_TEXT LONGTEXT COMMENT 'Some textual content of the book',
	CONTENT_PDF LONGBLOB COMMENT 'Some binary content of the book',
	INDEX (AUTHOR_ID),
	FOREIGN KEY (AUTHOR_ID) REFERENCES T_AUTHOR(ID)
) ENGINE = InnoDB
  COMMENT = 'An entity holding books';
/

CREATE TABLE x_unused (
	ID INT NOT NULL AUTO_INCREMENT,
	NAME VARCHAR(10) NOT NULL,
	ID_REF INT,
	NAME_REF VARCHAR(10),
	PRIMARY KEY(ID, NAME),
	INDEX (ID_REF, NAME_REF),
	FOREIGN KEY(ID_REF, NAME_REF) REFERENCES X_UNUSED(ID, NAME)
) ENGINE = InnoDB
  COMMENT = 'An unused table in the same schema.';
/

CREATE OR REPLACE VIEW V_LIBRARY (AUTHOR, TITLE) AS
SELECT CONCAT(A.FIRST_NAME, ' ', A.LAST_NAME), B.TITLE
FROM T_AUTHOR A JOIN T_BOOK B ON B.AUTHOR_ID = A.ID;
/
  
INSERT INTO t_author VALUES (NULL, 'George', 'Orwell', '1903-06-25', 1903);
/
INSERT INTO t_author VALUES (NULL, 'Paulo', 'Coelho', '1947-08-24', 1947);
/

INSERT INTO t_book VALUES (NULL, 1, '1984', 1948, 'To know and not to know, to be conscious of complete truthfulness while telling carefully constructed lies, to hold simultaneously two opinions which cancelled out, knowing them to be contradictory and believing in both of them, to use logic against logic, to repudiate morality while laying claim to it, to believe that democracy was impossible and that the Party was the guardian of democracy, to forget, whatever it was necessary to forget, then to draw it back into memory again at the moment when it was needed, and then promptly to forget it again, and above all, to apply the same process to the process itself -- that was the ultimate subtlety; consciously to induce unconsciousness, and then, once again, to become unconscious of the act of hypnosis you had just performed. Even to understand the word ''doublethink'' involved the use of doublethink..', null);
/
INSERT INTO t_book VALUES (NULL, 1, 'Animal Farm', 1945, null, null);
/
INSERT INTO t_book VALUES (NULL, 2, 'O Alquimista', 1988, null, null);
/
INSERT INTO t_book VALUES (NULL, 2, 'Brida', 1990, null, null);
/

CREATE PROCEDURE p_author_exists (author_name VARCHAR(50), OUT result BOOL)
  COMMENT 'Check existence of an author'
BEGIN
  SELECT COUNT(*) > 0 INTO result
    FROM t_author 
   WHERE first_name LIKE author_name 
      OR last_name LIKE author_name;
END
/

CREATE FUNCTION f_author_exists (author_name VARCHAR(50))
  RETURNS BOOL
  COMMENT 'Check existence of an author'
BEGIN
  RETURN (SELECT COUNT(*) > 0
    FROM t_author 
   WHERE first_name LIKE author_name 
      OR last_name LIKE author_name);
END
/