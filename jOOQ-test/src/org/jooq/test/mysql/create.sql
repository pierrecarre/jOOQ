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

INSERT INTO t_book VALUES (NULL, 1, '1984', 1948);
/
INSERT INTO t_book VALUES (NULL, 1, 'Animal Farm', 1945);
/
INSERT INTO t_book VALUES (NULL, 2, 'O Alquimista', 1988);
/
INSERT INTO t_book VALUES (NULL, 2, 'Brida', 1990);
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