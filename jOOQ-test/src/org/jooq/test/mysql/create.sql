DROP TABLE IF EXISTS t_book_to_book_store;
/
DROP TABLE IF EXISTS t_book_store;
/
DROP TABLE IF EXISTS t_book;
/
DROP TABLE IF EXISTS t_book_details;
/
DROP TABLE IF EXISTS t_author;
/
DROP TABLE IF EXISTS t_language;
/
DROP TABLE IF EXISTS x_test_case_71;
/
DROP TABLE IF EXISTS x_test_case_64_69;
/
DROP TABLE IF EXISTS x_test_case_85;
/
DROP TABLE IF EXISTS x_unused;
/
DROP PROCEDURE IF EXISTS p_unused;
/
DROP PROCEDURE IF EXISTS p_author_exists;
/
DROP PROCEDURE IF EXISTS p_create_author 
/ 
DROP PROCEDURE IF EXISTS p_create_author_by_name 
/ 
DROP FUNCTION IF EXISTS f_author_exists;
/
DROP FUNCTION IF EXISTS f_one;
/
DROP FUNCTION IF EXISTS f_number;
/
DROP FUNCTION IF EXISTS f317;
/

CREATE TABLE t_language (
  ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT 'The language ID',
  CD CHAR(2) NOT NULL COMMENT 'The language ISO code',
  DESCRIPTION VARCHAR(50) COMMENT 'The language description'
) ENGINE = InnoDB
  COMMENT 'An entity holding language master data'
/

CREATE TABLE t_author (
	ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT 'The author ID',
	FIRST_NAME VARCHAR(50) COMMENT 'The author''s first name',
	LAST_NAME VARCHAR(50) NOT NULL COMMENT 'The author''s last name',
	DATE_OF_BIRTH DATE COMMENT 'The author''s date of birth',
	YEAR_OF_BIRTH INT COMMENT 'The author''s year of birth',
	ADDRESS VARCHAR(200) COMMENT 'The author''s address'
) ENGINE = InnoDB
  COMMENT = 'An entity holding authors of books';
/

CREATE TABLE t_book_details (
    ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT 'The details ID'
) ENGINE = InnoDB
  COMMENT = 'An unused details table'
/

CREATE TABLE t_book (
	ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT 'The book ID',
	AUTHOR_ID INT NOT NULL COMMENT 'The author ID in entity ''author''',
	co_author_id int,
	DETAILS_ID INT COMMENT 'Some more details about the book',
	TITLE TEXT NOT NULL COMMENT 'The book''s title',
	PUBLISHED_IN INT NOT NULL COMMENT 'The year the book was published in',
	LANGUAGE_ID INT NOT NULL COMMENT 'The language of the book',
	CONTENT_TEXT LONGTEXT COMMENT 'Some textual content of the book',
	CONTENT_PDF LONGBLOB COMMENT 'Some binary content of the book',
	STATUS enum('SOLD OUT','ORDERED','ON STOCK') COMMENT 'The book''s stock status',
	INDEX (AUTHOR_ID),
	INDEX (LANGUAGE_ID),
	FOREIGN KEY (AUTHOR_ID) REFERENCES T_AUTHOR(ID),
	FOREIGN KEY (CO_AUTHOR_ID) REFERENCES T_AUTHOR(ID),
	FOREIGN KEY (DETAILS_ID) REFERENCES T_BOOK_DETAILS(ID),
	FOREIGN KEY (LANGUAGE_ID) REFERENCES T_LANGUAGE(ID)
) ENGINE = InnoDB
  COMMENT = 'An entity holding books';
/

CREATE TABLE t_book_store (
  name VARCHAR(400) NOT NULL UNIQUE COMMENT 'The books store name'
) ENGINE = InnoDB
  COMMENT = 'A book store'
/

CREATE TABLE t_book_to_book_store (
  book_store_name VARCHAR(400) NOT NULL COMMENT 'The book store name',
  book_id INTEGER NOT NULL COMMENT 'The book ID',
  stock INTEGER COMMENT 'The number of books on stock',
  PRIMARY KEY(book_store_name, book_id),
  CONSTRAINT b2bs_book_store_id
    FOREIGN KEY (book_store_name)
    REFERENCES t_book_store (name)
    ON DELETE CASCADE,
  CONSTRAINT b2bs_book_id
    FOREIGN KEY (book_id)
    REFERENCES t_book (id)
    ON DELETE CASCADE
) ENGINE = InnoDB
  COMMENT = 'An m:n relation between books and book stores'
/


CREATE TABLE x_unused (
	ID INT NOT NULL AUTO_INCREMENT UNIQUE,
	NAME VARCHAR(10) NOT NULL,
	ID_REF INT,
	NAME_REF VARCHAR(10),
	CLASS INT,
	FIELDS INT,
	CONFIGURATION INT,
	U_D_T INT,
	META_DATA INT,
	TYPE0 INT,
	PRIMARY_KEY INT,
	PRIMARYKEY INT,	
	PRIMARY KEY(ID, NAME),
	INDEX (ID_REF, NAME_REF),
	FOREIGN KEY(ID_REF, NAME_REF) REFERENCES X_UNUSED(ID, NAME)
) ENGINE = InnoDB
  COMMENT = 'An unused table in the same schema.';
/

CREATE TABLE x_test_case_64_69 (
	ID INT NOT NULL AUTO_INCREMENT,
	UNUSED_ID INT,
	PRIMARY KEY(ID),
	INDEX (UNUSED_ID),
	FOREIGN KEY(UNUSED_ID) REFERENCES X_UNUSED(ID)
) ENGINE = InnoDB
  COMMENT = 'An unused table in the same schema.';
/

CREATE TABLE x_test_case_71 (
	ID INT NOT NULL AUTO_INCREMENT,
	TEST_CASE_64_69_ID INT,
	PRIMARY KEY(ID),
	FOREIGN KEY(TEST_CASE_64_69_ID) REFERENCES X_TEST_CASE_64_69(ID)
) ENGINE = InnoDB
  COMMENT = 'An unused table in the same schema.';
/

CREATE TABLE x_test_case_85 (
	id int NOT NULL,
	x_unused_id int,
	x_unused_name VARCHAR(10),
	PRIMARY KEY(ID),
	FOREIGN KEY(x_unused_id, x_unused_name) REFERENCES X_UNUSED(id, name)
) ENGINE = InnoDB
  COMMENT = 'An unused table in the same schema.';
/

CREATE OR REPLACE VIEW V_LIBRARY (AUTHOR, TITLE) AS
SELECT CONCAT(A.FIRST_NAME, ' ', A.LAST_NAME), B.TITLE
FROM T_AUTHOR A JOIN T_BOOK B ON B.AUTHOR_ID = A.ID;
/
  

CREATE PROCEDURE p_unused (in1 VARCHAR(50), OUT out1 BOOL, INOUT out2 BOOL)
BEGIN
END
/

CREATE PROCEDURE p_create_author_by_name (IN first_name VARCHAR(50), IN last_name VARCHAR(50))
  COMMENT 'Create a new author'
BEGIN
	INSERT INTO T_AUTHOR (FIRST_NAME, LAST_NAME)
	VALUES (first_name, last_name);
END
/

CREATE PROCEDURE p_create_author()
  COMMENT 'Create a new author'
BEGIN
	call {jdbc.Schema}.p_create_author_by_name('William', 'Shakespeare');
END
/

CREATE PROCEDURE p_author_exists (author_name VARCHAR(50), OUT result INT)
  COMMENT 'Check existence of an author'
BEGIN
  SELECT COUNT(*) > 0 INTO result
    FROM t_author 
   WHERE first_name LIKE author_name 
      OR last_name LIKE author_name;
END
/

CREATE FUNCTION f_author_exists (author_name VARCHAR(50))
  RETURNS INT
  COMMENT 'Check existence of an author'
BEGIN
  RETURN (SELECT COUNT(*) > 0
    FROM t_author 
   WHERE first_name LIKE author_name 
      OR last_name LIKE author_name);
END
/

CREATE FUNCTION f_one ()
  RETURNS INT
  COMMENT '1 constant value'
BEGIN
  RETURN 1;
END
/

CREATE FUNCTION f_number (n int)
  RETURNS INT
  COMMENT 'echo n'
BEGIN
  RETURN n;
END
/

CREATE FUNCTION f317 (p1 int, p2 int, p3 int, p4 int)
  RETURNS INT
  COMMENT 'integration test for #317'
BEGIN
  RETURN 1000 * p1 + 100 * p2 + p4;
END
/