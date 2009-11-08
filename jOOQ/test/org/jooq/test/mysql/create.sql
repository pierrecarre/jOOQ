DROP TABLE IF EXISTS t_book;
/
DROP TABLE IF EXISTS t_author;
/
DROP TABLE IF EXISTS x_unused;
/
DROP PROCEDURE IF EXISTS p_author_exists;
/

CREATE TABLE t_author (
	ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT 'The author ID',
	FIRST_NAME VARCHAR(50) NOT NULL COMMENT 'The author''s first name',
	LAST_NAME VARCHAR(50) NOT NULL COMMENT 'The author''s last name',
	DATE_OF_BIRTH DATE NOT NULL COMMENT 'The author''s date of birth'
) ENGINE = InnoDB
  COMMENT = 'An entity holding authors of books';
/
  
CREATE TABLE t_book (
	ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT 'The book ID',
	AUTHOR_ID INT NOT NULL COMMENT 'The author ID in entity ''author''',
	TITLE TEXT NOT NULL COMMENT 'The book''s title',
	INDEX (AUTHOR_ID),
	FOREIGN KEY (AUTHOR_ID) REFERENCES T_AUTHOR(ID)
) ENGINE = InnoDB
  COMMENT = 'An entity holding books';
/

CREATE TABLE x_unused (
	ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY
) ENGINE = InnoDB
  COMMENT = 'An unused table in the same schema.';
/

CREATE OR REPLACE VIEW V_LIBRARY (AUTHOR, TITLE) AS
SELECT CONCAT(A.FIRST_NAME, ' ', A.LAST_NAME), B.TITLE
FROM T_AUTHOR A JOIN T_BOOK B ON B.AUTHOR_ID = A.ID;
/
  
INSERT INTO t_author VALUES (NULL, 'George', 'Orwell', '1903-06-25');
/
INSERT INTO t_author VALUES (NULL, 'Paulo', 'Coelho', '1947-08-24');
/

INSERT INTO t_book VALUES (NULL, 1, '1984');
/
INSERT INTO t_book VALUES (NULL, 1, 'Animal Farm');
/
INSERT INTO t_book VALUES (NULL, 2, 'O Alquimista');
/
INSERT INTO t_book VALUES (NULL, 2, 'Brida');
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