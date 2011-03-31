DROP PROCEDURE IF EXISTS p_arrays1
/
DROP PROCEDURE IF EXISTS p_arrays2
/
DROP PROCEDURE IF EXISTS p_arrays3
/
DROP FUNCTION IF EXISTS f_arrays1
/
DROP FUNCTION IF EXISTS f_arrays2
/
DROP FUNCTION IF EXISTS f_arrays3
/
DROP PROCEDURE IF EXISTS p_author_exists;
/
DROP PROCEDURE IF EXISTS p_unused;
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
DROP VIEW IF EXISTS v_library;
/
DROP TABLE IF EXISTS t_arrays
/
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
DROP TABLE IF EXISTS x_test_case_85;
/
DROP TABLE IF EXISTS x_test_case_64_69;
/
DROP TABLE IF EXISTS x_unused;
/

CREATE TABLE t_language (
  id INTEGER NOT NULL PRIMARY KEY,
  cd CHAR(2) NOT NULL,
  description VARCHAR(50)
);
/

CREATE TABLE t_author (
	ID INT NOT NULL PRIMARY KEY,
	FIRST_NAME VARCHAR(50),
	LAST_NAME VARCHAR(50) NOT NULL,
	DATE_OF_BIRTH DATE,
	YEAR_OF_BIRTH INT,
	ADDRESS VARCHAR(50)
);
/

CREATE TABLE t_book_details (
    ID INT PRIMARY KEY
);
/

CREATE TABLE t_book (
	ID INT NOT NULL PRIMARY KEY,
	AUTHOR_ID INT NOT NULL,
	co_author_id int,
	DETAILS_ID INT,
	TITLE VARCHAR(400) NOT NULL,
	PUBLISHED_IN INT NOT NULL,
	LANGUAGE_ID INT NOT NULL,
	CONTENT_TEXT LONGVARCHAR,
	CONTENT_PDF LONGVARBINARY,
    FOREIGN KEY (AUTHOR_ID) REFERENCES T_AUTHOR(ID),
    FOREIGN KEY (CO_AUTHOR_ID) REFERENCES T_AUTHOR(ID),
    FOREIGN KEY (DETAILS_ID) REFERENCES T_BOOK_DETAILS(ID), 
    FOREIGN KEY (LANGUAGE_ID) REFERENCES T_LANGUAGE(ID)
);
/

CREATE TABLE t_book_store (
  name VARCHAR(400) NOT NULL UNIQUE
);
/

CREATE TABLE t_book_to_book_store (
  book_store_name VARCHAR(400) NOT NULL,
  book_id INTEGER NOT NULL,
  stock INTEGER,
  PRIMARY KEY(book_store_name, book_id),
  CONSTRAINT b2bs_book_store_id
    FOREIGN KEY (book_store_name)
    REFERENCES t_book_store (name)
    ON DELETE CASCADE,
  CONSTRAINT b2bs_book_id
    FOREIGN KEY (book_id)
    REFERENCES t_book (id)
    ON DELETE CASCADE
);
/


CREATE TABLE t_arrays (
  id integer not null primary key,
  string_array VARCHAR(20) ARRAY,
  number_array INTEGER ARRAY,
  date_array DATE ARRAY
);
/

CREATE TABLE x_unused (
	ID INT NOT NULL UNIQUE,
	NAME VARCHAR(10) NOT NULL,
	ID_REF INT,
	CLASS INT,
	FIELDS INT,
	CONFIGURATION INT,
	U_D_T INT,
	META_DATA INT,
	TYPE0 INT,
	PRIMARY_KEY INT,
	PRIMARYKEY INT,	NAME_REF VARCHAR(10),
	PRIMARY KEY(ID, NAME),
	FOREIGN KEY(ID_REF, NAME_REF) REFERENCES X_UNUSED(ID, NAME)
);
/

CREATE TABLE x_test_case_64_69 (
    ID INT NOT NULL,
    UNUSED_ID INT,
    PRIMARY KEY(ID),
    FOREIGN KEY(UNUSED_ID) REFERENCES X_UNUSED(ID)
);
/

CREATE TABLE x_test_case_71 (
	ID INT NOT NULL,
	TEST_CASE_64_69_ID SMALLINT,
	PRIMARY KEY(ID),
	FOREIGN KEY(TEST_CASE_64_69_ID) REFERENCES X_TEST_CASE_64_69(ID)
);
/

CREATE TABLE x_test_case_85 (
	id int NOT NULL,
	x_unused_id int,
	x_unused_name VARCHAR(10),
	PRIMARY KEY(ID),
	FOREIGN KEY(x_unused_id, x_unused_name) REFERENCES X_UNUSED(id, name)
);
/

CREATE VIEW V_LIBRARY (AUTHOR, TITLE) AS
SELECT T_AUTHOR.FIRST_NAME + ' ' + T_AUTHOR.LAST_NAME, T_BOOK.TITLE
FROM T_AUTHOR JOIN T_BOOK ON T_BOOK.AUTHOR_ID = T_AUTHOR.ID;
/
  

CREATE FUNCTION f_author_exists (author_name varchar(50)) 
     RETURNS INTEGER 
     RETURN ( 
        SELECT COUNT(*) 
          FROM t_author  
   	  WHERE first_name = author_name  
      	  OR last_name = author_name
     ) 
/

CREATE FUNCTION f_one ()
RETURNS INTEGER
RETURN 1
/

CREATE FUNCTION f_number (n integer)
RETURNS INTEGER
RETURN n
/

CREATE FUNCTION f317 (p1 INTEGER, p2 INTEGER, p3 INTEGER, p4 INTEGER)
RETURNS INTEGER
return 1000 * p1 + 100 * p2 + p4
/

CREATE PROCEDURE p_arrays1(IN in_array int array, OUT out_array int array)
BEGIN ATOMIC
	SET out_array = in_array;  
END
/

CREATE PROCEDURE p_arrays2(IN in_array bigint array, OUT out_array bigint array)
BEGIN ATOMIC
	SET out_array = in_array;  
END
/

CREATE PROCEDURE p_arrays3(IN in_array longvarchar array, OUT out_array longvarchar array)
BEGIN ATOMIC
	SET out_array = in_array;  
END
/

CREATE FUNCTION f_arrays1(IN in_array int array)
RETURNS INTEGER array
RETURN in_array
/

CREATE FUNCTION f_arrays2(IN in_array bigint array)
RETURNS bigint array
RETURN in_array
/

CREATE FUNCTION f_arrays3(IN in_array longvarchar array)
RETURNS longvarchar array
RETURN in_array
/

CREATE PROCEDURE p_author_exists (IN author_name VARCHAR(50), OUT result INT) 
READS SQL DATA
BEGIN ATOMIC
  SELECT COUNT(*) INTO result
    FROM t_author
   WHERE first_name LIKE author_name
      OR last_name LIKE author_name;
END
/

CREATE PROCEDURE p_create_author_by_name (IN first_name VARCHAR(50), IN last_name VARCHAR(50))
MODIFIES SQL DATA
BEGIN ATOMIC
	INSERT INTO T_AUTHOR (ID, FIRST_NAME, LAST_NAME)
	VALUES ((SELECT MAX(ID)+1 FROM T_AUTHOR), first_name, last_name);
END
/

CREATE PROCEDURE p_create_author()
MODIFIES SQL DATA
BEGIN ATOMIC
	call {jdbc.Schema}.p_create_author_by_name('William', 'Shakespeare');
END
/

CREATE PROCEDURE p_unused (IN in1 VARCHAR(50), OUT out1 INT, INOUT out2 INT)
BEGIN ATOMIC
	SET out1 = 0;
	SET out2 = 0;
END
/
