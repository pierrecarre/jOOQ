DROP TABLE IF EXISTS t_arrays CASCADE
/
DROP TABLE IF EXISTS t_book_to_book_store CASCADE
/
DROP TABLE IF EXISTS t_book_store CASCADE
/
DROP TABLE IF EXISTS t_book CASCADE
/
DROP TABLE IF EXISTS t_book_details CASCADE
/
DROP TABLE IF EXISTS t_author CASCADE
/
DROP TABLE IF EXISTS t_language CASCADE
/
DROP TABLE IF EXISTS x_test_case_71 CASCADE
/
DROP TABLE IF EXISTS x_test_case_64_69 CASCADE
/
DROP TABLE IF EXISTS x_test_case_85 CASCADE
/
DROP TABLE IF EXISTS x_unused CASCADE
/
DROP FUNCTION f_arrays(in_array IN integer[])
/
DROP FUNCTION f_arrays(in_array IN bigint[])
/
DROP FUNCTION f_arrays(in_array IN text[])
/
DROP FUNCTION p_arrays(in_array IN integer[], out_array OUT integer[])
/
DROP FUNCTION p_arrays(in_array IN bigint[], out_array OUT bigint[])
/
DROP FUNCTION p_arrays(in_array IN text[], out_array OUT text[])
/
DROP FUNCTION p_enhance_address1(address IN u_address_type, no OUT VARCHAR)
/
DROP FUNCTION p_enhance_address2(address OUT u_address_type)
/
DROP FUNCTION p_enhance_address3(address IN OUT u_address_type)
/
DROP FUNCTION p_unused(in1 VARCHAR, out1 OUT INTEGER, out2 IN OUT INTEGER)
/
DROP FUNCTION p_create_author()
/ 
DROP FUNCTION p_create_author_by_name(first_name VARCHAR, last_name VARCHAR)
/ 
DROP FUNCTION p_author_exists(author_name VARCHAR, result OUT INTEGER)
/
DROP FUNCTION f_author_exists(author_name VARCHAR);
/
DROP FUNCTION f_one();
/
DROP FUNCTION f_number(n int);
/
DROP FUNCTION f317(p1 int, p2 int, p3 int, p4 int);
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
  no VARCHAR(30)
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
  description VARCHAR(50)
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
  date_of_birth DATE,
  year_of_birth INTEGER,
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

CREATE TABLE t_book_details (
    ID INT PRIMARY KEY
)
/
COMMENT ON TABLE t_book_details IS 'An unused details table'
/

CREATE TABLE t_book (
  id INTEGER NOT NULL PRIMARY KEY,
  author_id INTEGER NOT NULL,
  co_author_id INTEGER,
  details_id INT,
  title VARCHAR(400) NOT NULL,
  published_in INTEGER NOT NULL,
  language_id INTEGER NOT NULL,
  content_text TEXT,
  content_pdf BYTEA,
  status u_book_status,
  FOREIGN KEY (AUTHOR_ID) REFERENCES T_AUTHOR(ID),
  FOREIGN KEY (CO_AUTHOR_ID) REFERENCES T_AUTHOR(ID),
  FOREIGN KEY (DETAILS_ID) REFERENCES T_BOOK_DETAILS(ID), 
  FOREIGN KEY (LANGUAGE_ID) REFERENCES T_LANGUAGE(ID)
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


CREATE TABLE t_book_store (
  name VARCHAR(400) NOT NULL UNIQUE
)
/
COMMENT ON TABLE t_book_store IS 'A book store'
/
COMMENT ON COLUMN t_book_store.name IS 'The books store name'
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
)
/
COMMENT ON TABLE t_book_to_book_store IS 'An m:n relation between books and book stores'
/
COMMENT ON COLUMN t_book_to_book_store.book_store_name IS 'The book store name'
/
COMMENT ON COLUMN t_book_to_book_store.book_id IS 'The book ID'
/
COMMENT ON COLUMN t_book_to_book_store.stock IS 'The number of books on stock'
/


CREATE TABLE t_arrays (
  id integer not null primary key,
  string_array VARCHAR(20)[],
  number_array INTEGER[],
  date_array DATE[],
  udt_array u_street_type[],
  enum_array u_country[],
  array_array INTEGER[][]
)
/

CREATE TABLE x_unused (
  id INTEGER NOT NULL UNIQUE,
  name VARCHAR(10) NOT NULL,
  id_ref INTEGER,
  CLASS INT,
  FIELDS INT,
  CONFIGURATION INT,
  U_D_T INT,
  META_DATA INT,
  VALUES INT,
  TYPE0 INT,
  PRIMARY_KEY INT,
  PRIMARYKEY INT,	
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

CREATE TABLE x_test_case_85 (
	id INTEGER NOT NULL,
	x_unused_id INTEGER,
	x_unused_name VARCHAR(10)
)
/

ALTER TABLE x_test_case_85
  ADD PRIMARY KEY (id)
/
ALTER TABLE x_test_case_85
  ADD CONSTRAINT x_test_case_85_ref
  FOREIGN KEY (x_unused_id, x_unused_name)
  REFERENCES x_unused (id, name)
/


CREATE OR REPLACE VIEW v_library (author, title) AS
SELECT a.first_name || ' ' || a.last_name, b.title
FROM t_author a JOIN t_book b ON b.author_id = a.id
/
  

CREATE FUNCTION p_unused (in1 VARCHAR, out1 OUT INT, out2 IN OUT INT)
AS $$
BEGIN
	NULL;
END;
$$ LANGUAGE plpgsql;
/

CREATE FUNCTION p_enhance_address1 (address IN u_address_type, no OUT VARCHAR)
AS $$
BEGIN
	no := $1.street.no;
END;
$$ LANGUAGE plpgsql;
/

CREATE FUNCTION p_enhance_address2 (address OUT u_address_type)
AS $$
BEGIN
	address := (
		SELECT t_author.address 
		FROM t_author
		WHERE first_name = 'George'
	);
END;
$$ LANGUAGE plpgsql;
/

CREATE FUNCTION p_enhance_address3 (address IN OUT u_address_type)
AS $$
BEGIN
	address.street := row('Zwinglistrasse', '17');
END;
$$ LANGUAGE plpgsql;
/

CREATE FUNCTION p_create_author_by_name (first_name VARCHAR, last_name VARCHAR)
RETURNS VOID
AS $$
BEGIN
	INSERT INTO T_AUTHOR (ID, FIRST_NAME, LAST_NAME)
	VALUES ((SELECT MAX(ID)+1 FROM T_AUTHOR), first_name, last_name);
END;
$$ LANGUAGE plpgsql;
/

CREATE FUNCTION p_create_author()
RETURNS VOID
AS $$
BEGIN
	PERFORM {jdbc.Schema}.p_create_author_by_name('William', 'Shakespeare');
END;
$$ LANGUAGE plpgsql;
/

CREATE FUNCTION p_author_exists (author_name VARCHAR, result OUT INTEGER)
AS $$
DECLARE
  v_result INT;
BEGIN
  SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END
    INTO v_result
    FROM t_author 
   WHERE first_name LIKE author_name 
      OR last_name LIKE author_name;
      
  result := v_result;
END;
$$ LANGUAGE plpgsql;
/

CREATE FUNCTION p_arrays(in_array IN integer[], out_array OUT integer[])
AS $$
BEGIN
	out_array := in_array;
END;
$$ LANGUAGE plpgsql;
/

CREATE FUNCTION p_arrays(in_array IN bigint[], out_array OUT bigint[])
AS $$
BEGIN
	out_array := in_array;
END;
$$ LANGUAGE plpgsql;
/

CREATE FUNCTION p_arrays(in_array IN text[], out_array OUT text[])
AS $$
BEGIN
	out_array := in_array;
END;
$$ LANGUAGE plpgsql;
/

CREATE FUNCTION f_arrays(in_array IN integer[])
RETURNS integer[]
AS $$
BEGIN
	return in_array;
END;
$$ LANGUAGE plpgsql;
/

CREATE FUNCTION f_arrays(in_array IN bigint[])
RETURNS bigint[]
AS $$
BEGIN
	return in_array;
END;
$$ LANGUAGE plpgsql;
/

CREATE FUNCTION f_arrays(in_array IN text[])
RETURNS text[]
AS $$
BEGIN
	return in_array;
END;
$$ LANGUAGE plpgsql;
/

CREATE FUNCTION f_author_exists (author_name VARCHAR)
RETURNS INT
AS $$
DECLARE
	v_result INT;
BEGIN
  SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END
    INTO v_result
    FROM t_author 
   WHERE first_name LIKE author_name 
      OR last_name LIKE author_name;
      
  return v_result;
END;
$$ LANGUAGE plpgsql;
/

CREATE FUNCTION f_one ()
RETURNS INT
AS $$
BEGIN
	RETURN 1;
END;
$$ LANGUAGE plpgsql;
/

CREATE FUNCTION f_number (n int)
RETURNS INT
AS $$
BEGIN
	RETURN n;
END;
$$ LANGUAGE plpgsql;
/

CREATE FUNCTION f317 (p1 int, p2 int, p3 int, p4 int)
RETURNS INT
AS $$
BEGIN
	RETURN 1000 * p1 + 100 * p2 + p4;
END;
$$ LANGUAGE plpgsql;
/