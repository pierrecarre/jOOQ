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
DROP TABLE IF EXISTS x_test_case_64_69;
/
DROP TABLE IF EXISTS x_test_case_85;
/
DROP TABLE IF EXISTS x_unused;
/
DROP ALIAS IF EXISTS f_arrays1
/
DROP ALIAS IF EXISTS f_arrays2
/
DROP ALIAS IF EXISTS f_arrays3
/
DROP ALIAS IF EXISTS f_author_exists;
/
DROP ALIAS IF EXISTS f_one;
/
DROP ALIAS IF EXISTS f_number;
/
DROP ALIAS IF EXISTS f317;
/
DROP ALIAS IF EXISTS p_create_author;
/
DROP ALIAS IF EXISTS p_create_author_by_name;
/

CREATE TABLE t_language (
  id INTEGER NOT NULL PRIMARY KEY,
  cd CHAR(2) NOT NULL,
  description VARCHAR(50)
);
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
	ID INT NOT NULL PRIMARY KEY,
	FIRST_NAME VARCHAR(50),
	LAST_NAME VARCHAR(50) NOT NULL,
	DATE_OF_BIRTH DATE,
	YEAR_OF_BIRTH INT,
	ADDRESS VARCHAR(50)
);
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
	ID INT NOT NULL PRIMARY KEY,
	AUTHOR_ID INT NOT NULL,
	co_author_id int,
	DETAILS_ID INT,
	TITLE VARCHAR(400) NOT NULL,
	PUBLISHED_IN INT NOT NULL,
	LANGUAGE_ID INT NOT NULL,
	CONTENT_TEXT CLOB,
	CONTENT_PDF BLOB,
    FOREIGN KEY (AUTHOR_ID) REFERENCES T_AUTHOR(ID),
    FOREIGN KEY (CO_AUTHOR_ID) REFERENCES T_AUTHOR(ID),
    FOREIGN KEY (DETAILS_ID) REFERENCES T_BOOK_DETAILS(ID), 
    FOREIGN KEY (LANGUAGE_ID) REFERENCES T_LANGUAGE(ID)
);
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

CREATE TABLE t_book_store (
  name VARCHAR(400) NOT NULL UNIQUE
);
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
);
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
  string_array ARRAY,
  number_array ARRAY,
  date_array ARRAY
);
/

CREATE TABLE x_unused (
	ID INT NOT NULL UNIQUE,
	NAME VARCHAR(10) NOT NULL,
	ID_REF INT,
	NAME_REF VARCHAR(10),
	CLASS INT,
	FIELDS INT,
	TABLE INT,
	CONFIGURATION INT,
	U_D_T INT,
	META_DATA INT,
	VALUES INT,
	TYPE0 INT,
	PRIMARY_KEY INT,
	PRIMARYKEY INT,
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
SELECT CONCAT(T_AUTHOR.FIRST_NAME, ' ', T_AUTHOR.LAST_NAME), T_BOOK.TITLE
FROM T_AUTHOR JOIN T_BOOK ON T_BOOK.AUTHOR_ID = T_AUTHOR.ID;
/
  
CREATE ALIAS f_one AS $$
    Integer fOne() {
        return Integer.valueOf(1);
    }
$$;
/

CREATE ALIAS f_number AS $$
    Integer fNumber(Integer n) {
        return n;
    }
$$;
/

CREATE ALIAS f317 AS $$
    Integer f317(Integer p1, Integer p2, Integer p3, Integer p4) {
        return 1000 * p1 + 100 * p2 + p4;
    }
$$;    
/


CREATE ALIAS f_arrays1 AS $$
    Object[] f_arrays1(Object[] in_array) {
        return in_array;
    }
$$;
/

CREATE ALIAS f_arrays2 AS $$
    Object[] f_arrays2(Object[] in_array) {
        return in_array;
    }
$$;
/

CREATE ALIAS f_arrays3 AS $$
    Object[] f_arrays3(Object[] in_array) {
        return in_array;
    }
$$;
/

CREATE ALIAS f_author_exists AS $$
   Integer fAuthorExists(Connection connection, String authorName) throws SQLException {
        Statement stmt = null;
        ResultSet rs = null;

        try {
           stmt = connection.createStatement();
           rs = stmt.executeQuery(
              "SELECT COUNT(*)" +
                " FROM t_author" +
                " WHERE first_name = '" + authorName + "'" +
                   " OR last_name = '" + authorName + "'");
           if (rs.next()) {
               if (rs.getInt(1) == 0) {
                   return 0;
               } else {
                   return 1;
               }
           } else {
               return 0;
           }
        } finally {
          if (rs != null) {
            rs.close();
          }
          if (stmt != null) {
            stmt.close();
          }
        }
   }
$$;
/
CREATE ALIAS p_create_author_by_name AS $$
	void pCreateAuthorByName(Connection connection, String firstName, String lastName) throws SQLException {
		PreparedStatement stmt = connection.prepareStatement(
			"INSERT INTO T_AUTHOR (ID, FIRST_NAME, LAST_NAME) VALUES ((SELECT MAX(ID)+1 FROM T_AUTHOR), ?, ?)");
		
		stmt.setString(1, firstName);
		stmt.setString(2, lastName);
		stmt.execute();
		stmt.close();
	}
$$;
/
CREATE ALIAS p_create_author AS $$
	void pCreateAuthor(Connection connection) throws SQLException {
		CallableStatement call = connection.prepareCall(
			"CALL p_create_author_by_name('William', 'Shakespeare')");
		call.execute();
		call.close();
	}
$$;