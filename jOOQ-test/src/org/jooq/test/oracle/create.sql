DROP TABLE t_arrays
/
DROP TABLE t_book_to_book_store
/
DROP TABLE t_book_store
/
DROP TABLE t_book
/
DROP TABLE t_book_details
/
DROP TABLE t_author
/
DROP TABLE t_language
/
DROP TABLE x_test_case_71
/
DROP TABLE x_test_case_64_69
/
DROP TABLE x_test_case_85
/
DROP TABLE x_unused
/
DROP TABLE x_many_fields
/
DROP PROCEDURE p_arrays1
/
DROP PROCEDURE p_arrays2
/
DROP PROCEDURE p_arrays3
/
DROP PROCEDURE p_many_parameters
/
DROP FUNCTION f_arrays1
/
DROP FUNCTION f_arrays2
/
DROP FUNCTION f_arrays3
/
DROP PROCEDURE p_enhance_address1
/
DROP PROCEDURE p_enhance_address2
/
DROP PROCEDURE p_enhance_address3
/
DROP PROCEDURE p_unused
/
DROP PROCEDURE p_create_author 
/ 
DROP PROCEDURE p_create_author_by_name 
/ 
DROP PROCEDURE p_author_exists
/
DROP FUNCTION f_author_exists
/
DROP FUNCTION f_one
/
DROP FUNCTION f_number
/
DROP FUNCTION f317
/
DROP PACKAGE library
/
DROP TYPE u_address_type
/
DROP TYPE u_street_type 
/
DROP TYPE u_string_array
/
DROP TYPE u_number_array
/
DROP TYPE u_number_long_array
/
DROP TYPE u_date_array
/


CREATE TYPE u_street_type AS OBJECT (
  street VARCHAR2(100),
  no VARCHAR2(30)
)
/

CREATE TYPE u_address_type AS OBJECT (
  street u_street_type,
  zip VARCHAR2(50),
  city VARCHAR2(50),
  country VARCHAR2(50),
  since DATE,
  code NUMBER(7)
)
/

CREATE TABLE t_language (
  id NUMBER(7) NOT NULL PRIMARY KEY,
  cd CHAR(2) NOT NULL,
  description VARCHAR2(50)
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
  id NUMBER(7) NOT NULL PRIMARY KEY,
  first_name VARCHAR2(50),
  last_name VARCHAR2(50) NOT NULL,
  date_of_birth DATE,
  year_of_birth NUMBER(7),
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
    ID NUMBER(7) PRIMARY KEY
)
/
COMMENT ON TABLE t_book_details IS 'An unused details table'
/

CREATE TABLE t_book (
  id NUMBER(7) NOT NULL PRIMARY KEY,
  author_id NUMBER(7) NOT NULL,
  co_author_id NUMBER(7),
  details_id NUMBER(7),
  title VARCHAR2(400) NOT NULL,
  published_in NUMBER(7) NOT NULL,
  language_id NUMBER(7) NOT NULL,
  content_text CLOB,
  content_pdf BLOB,
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


CREATE TABLE t_book_store (
  name VARCHAR2(400) NOT NULL UNIQUE
)
/
COMMENT ON TABLE t_book_store IS 'A book store'
/
COMMENT ON COLUMN t_book_store.name IS 'The books store name'
/


CREATE TABLE t_book_to_book_store (
  book_store_name VARCHAR2(400) NOT NULL,
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


CREATE TYPE u_string_array AS VARRAY(4) OF VARCHAR2(20)
/
CREATE TYPE u_number_array AS VARRAY(4) OF NUMBER(7)
/
CREATE TYPE u_number_long_array AS VARRAY(4) OF NUMBER(11)
/
CREATE TYPE u_date_array AS VARRAY(4) OF DATE
/

CREATE TABLE t_arrays (
  id NUMBER(7) not null primary key,
  string_array u_string_array,
  number_array u_number_array,
  number_long_array u_number_long_array,
  date_array u_date_array
)
/

CREATE TABLE x_unused (
	id NUMBER(7) NOT NULL UNIQUE,
	name VARCHAR2(10) NOT NULL,
	id_ref NUMBER(7),
	CLASS NUMBER(7),
	FIELDS NUMBER(7),
	CONFIGURATION NUMBER(7),
	U_D_T NUMBER(7),
	META_DATA NUMBER(7),
	TYPE0 NUMBER(7),
	PRIMARY_KEY NUMBER(7),
	PRIMARYKEY NUMBER(7),	
	name_ref VARCHAR2(10)
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
  id NUMBER(7) NOT NULL,
  unused_id NUMBER(7)
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
	id NUMBER(7) NOT NULL,
	test_case_64_69_id NUMBER(4)
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
	id NUMBER(7) NOT NULL,
	x_unused_id NUMBER(7),
	x_unused_name VARCHAR2(10)
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
  

CREATE OR REPLACE PROCEDURE p_unused (in1 VARCHAR2, out1 OUT NUMBER, out2 IN OUT NUMBER)
IS
BEGIN
	NULL;
END p_unused;
/

CREATE OR REPLACE PROCEDURE p_enhance_address1 (address IN u_address_type, no OUT VARCHAR2)
IS
BEGIN
	no := address.street.no;
END p_enhance_address1;
/

CREATE OR REPLACE PROCEDURE p_enhance_address2 (address OUT u_address_type)
IS
BEGIN
	SELECT address 
	INTO address
	FROM t_author
	WHERE first_name = 'George';
END p_enhance_address2;
/

CREATE OR REPLACE PROCEDURE p_enhance_address3 (address IN OUT u_address_type)
IS
BEGIN
	address.street := u_street_type('Zwinglistrasse', '17');
END p_enhance_address3;
/

CREATE OR REPLACE PROCEDURE p_create_author_by_name (first_name VARCHAR2, last_name VARCHAR2)
IS
BEGIN
	INSERT INTO T_AUTHOR (ID, FIRST_NAME, LAST_NAME)
	VALUES ((SELECT MAX(ID)+1 FROM T_AUTHOR), first_name, last_name);
END p_create_author_by_name;
/

CREATE OR REPLACE PROCEDURE p_create_author
IS
BEGIN
	{jdbc.Schema}.p_create_author_by_name('William', 'Shakespeare');
END p_create_author;
/

CREATE OR REPLACE PROCEDURE p_arrays1 (in_array u_number_array, out_array OUT u_number_array)
IS
BEGIN 
    out_array := in_array;  
END p_arrays1;
/

CREATE OR REPLACE PROCEDURE p_arrays2 (in_array u_number_long_array, out_array OUT u_number_long_array)
IS
BEGIN 
    out_array := in_array;  
END p_arrays2;
/

CREATE OR REPLACE PROCEDURE p_arrays3 (in_array u_string_array, out_array OUT u_string_array)
IS
BEGIN 
    out_array := in_array;  
END p_arrays3;
/

CREATE OR REPLACE PROCEDURE p_many_parameters (
  f000 number, f001 number, f002 number, f003 number, f004 number,
  f005 number, f006 number, f007 number, f008 number, f009 number,
  f010 number, f011 number, f012 number, f013 number, f014 number,
  f015 number, f016 number, f017 number, f018 number, f019 number,
  f020 number, f021 number, f022 number, f023 number, f024 number,
  f025 number, f026 number, f027 number, f028 number, f029 number,
  f030 number, f031 number, f032 number, f033 number, f034 number,
  f035 number, f036 number, f037 number, f038 number, f039 number,
  f040 number, f041 number, f042 number, f043 number, f044 number,
  f045 number, f046 number, f047 number, f048 number, f049 number,
  f050 number, f051 number, f052 number, f053 number, f054 number,
  f055 number, f056 number, f057 number, f058 number, f059 number,
  f060 number, f061 number, f062 number, f063 number, f064 number,
  f065 number, f066 number, f067 number, f068 number, f069 number,
  f070 number, f071 number, f072 number, f073 number, f074 number,
  f075 number, f076 number, f077 number, f078 number, f079 number,
  f080 number, f081 number, f082 number, f083 number, f084 number,
  f085 number, f086 number, f087 number, f088 number, f089 number,
  f090 number, f091 number, f092 number, f093 number, f094 number,
  f095 number, f096 number, f097 number, f098 number, f099 number,

  f100 number, f101 number, f102 number, f103 number, f104 number,
  f105 number, f106 number, f107 number, f108 number, f109 number,
  f110 number, f111 number, f112 number, f113 number, f114 number,
  f115 number, f116 number, f117 number, f118 number, f119 number,
  f120 number, f121 number, f122 number, f123 number, f124 number,
  f125 number, f126 number, f127 number, f128 number, f129 number,
  f130 number, f131 number, f132 number, f133 number, f134 number,
  f135 number, f136 number, f137 number, f138 number, f139 number,
  f140 number, f141 number, f142 number, f143 number, f144 number,
  f145 number, f146 number, f147 number, f148 number, f149 number,
  f150 number, f151 number, f152 number, f153 number, f154 number,
  f155 number, f156 number, f157 number, f158 number, f159 number,
  f160 number, f161 number, f162 number, f163 number, f164 number,
  f165 number, f166 number, f167 number, f168 number, f169 number,
  f170 number, f171 number, f172 number, f173 number, f174 number,
  f175 number, f176 number, f177 number, f178 number, f179 number,
  f180 number, f181 number, f182 number, f183 number, f184 number,
  f185 number, f186 number, f187 number, f188 number, f189 number,
  f190 number, f191 number, f192 number, f193 number, f194 number,
  f195 number, f196 number, f197 number, f198 number, f199 number,

  f200 number, f201 number, f202 number, f203 number, f204 number,
  f205 number, f206 number, f207 number, f208 number, f209 number,
  f210 number, f211 number, f212 number, f213 number, f214 number,
  f215 number, f216 number, f217 number, f218 number, f219 number,
  f220 number, f221 number, f222 number, f223 number, f224 number,
  f225 number, f226 number, f227 number, f228 number, f229 number,
  f230 number, f231 number, f232 number, f233 number, f234 number,
  f235 number, f236 number, f237 number, f238 number, f239 number,
  f240 number, f241 number, f242 number, f243 number, f244 number,
  f245 number, f246 number, f247 number, f248 number, f249 number,
  f250 number, f251 number, f252 number, f253 number, f254 number,
  f255 number, f256 number, f257 number, f258 number, f259 number,
  f260 number, f261 number, f262 number, f263 number, f264 number,
  f265 number, f266 number, f267 number, f268 number, f269 number,
  f270 number, f271 number, f272 number, f273 number, f274 number,
  f275 number, f276 number, f277 number, f278 number, f279 number,
  f280 number, f281 number, f282 number, f283 number, f284 number,
  f285 number, f286 number, f287 number, f288 number, f289 number,
  f290 number, f291 number, f292 number, f293 number, f294 number,
  f295 number, f296 number, f297 number, f298 number, f299 number,

  f300 number, f301 number, f302 number, f303 number, f304 number,
  f305 number, f306 number, f307 number, f308 number, f309 number,
  f310 number, f311 number, f312 number, f313 number, f314 number,
  f315 number, f316 number, f317 number, f318 number, f319 number,
  f320 number, f321 number, f322 number, f323 number, f324 number,
  f325 number, f326 number, f327 number, f328 number, f329 number,
  f330 number, f331 number, f332 number, f333 number, f334 number,
  f335 number, f336 number, f337 number, f338 number, f339 number,
  f340 number, f341 number, f342 number, f343 number, f344 number,
  f345 number, f346 number, f347 number, f348 number, f349 number,
  f350 number, f351 number, f352 number, f353 number, f354 number,
  f355 number, f356 number, f357 number, f358 number, f359 number,
  f360 number, f361 number, f362 number, f363 number, f364 number,
  f365 number, f366 number, f367 number, f368 number, f369 number,
  f370 number, f371 number, f372 number, f373 number, f374 number,
  f375 number, f376 number, f377 number, f378 number, f379 number,
  f380 number, f381 number, f382 number, f383 number, f384 number,
  f385 number, f386 number, f387 number, f388 number, f389 number,
  f390 number, f391 number, f392 number, f393 number, f394 number,
  f395 number, f396 number, f397 number, f398 number, f399 number
)
IS
BEGIN
	NULL;
END p_many_parameters;
/

CREATE OR REPLACE FUNCTION f_many_parameters (
  f000 number, f001 number, f002 number, f003 number, f004 number,
  f005 number, f006 number, f007 number, f008 number, f009 number,
  f010 number, f011 number, f012 number, f013 number, f014 number,
  f015 number, f016 number, f017 number, f018 number, f019 number,
  f020 number, f021 number, f022 number, f023 number, f024 number,
  f025 number, f026 number, f027 number, f028 number, f029 number,
  f030 number, f031 number, f032 number, f033 number, f034 number,
  f035 number, f036 number, f037 number, f038 number, f039 number,
  f040 number, f041 number, f042 number, f043 number, f044 number,
  f045 number, f046 number, f047 number, f048 number, f049 number,
  f050 number, f051 number, f052 number, f053 number, f054 number,
  f055 number, f056 number, f057 number, f058 number, f059 number,
  f060 number, f061 number, f062 number, f063 number, f064 number,
  f065 number, f066 number, f067 number, f068 number, f069 number,
  f070 number, f071 number, f072 number, f073 number, f074 number,
  f075 number, f076 number, f077 number, f078 number, f079 number,
  f080 number, f081 number, f082 number, f083 number, f084 number,
  f085 number, f086 number, f087 number, f088 number, f089 number,
  f090 number, f091 number, f092 number, f093 number, f094 number,
  f095 number, f096 number, f097 number, f098 number, f099 number,

  f100 number, f101 number, f102 number, f103 number, f104 number,
  f105 number, f106 number, f107 number, f108 number, f109 number,
  f110 number, f111 number, f112 number, f113 number, f114 number,
  f115 number, f116 number, f117 number, f118 number, f119 number,
  f120 number, f121 number, f122 number, f123 number, f124 number,
  f125 number, f126 number, f127 number, f128 number, f129 number,
  f130 number, f131 number, f132 number, f133 number, f134 number,
  f135 number, f136 number, f137 number, f138 number, f139 number,
  f140 number, f141 number, f142 number, f143 number, f144 number,
  f145 number, f146 number, f147 number, f148 number, f149 number,
  f150 number, f151 number, f152 number, f153 number, f154 number,
  f155 number, f156 number, f157 number, f158 number, f159 number,
  f160 number, f161 number, f162 number, f163 number, f164 number,
  f165 number, f166 number, f167 number, f168 number, f169 number,
  f170 number, f171 number, f172 number, f173 number, f174 number,
  f175 number, f176 number, f177 number, f178 number, f179 number,
  f180 number, f181 number, f182 number, f183 number, f184 number,
  f185 number, f186 number, f187 number, f188 number, f189 number,
  f190 number, f191 number, f192 number, f193 number, f194 number,
  f195 number, f196 number, f197 number, f198 number, f199 number,

  f200 number, f201 number, f202 number, f203 number, f204 number,
  f205 number, f206 number, f207 number, f208 number, f209 number,
  f210 number, f211 number, f212 number, f213 number, f214 number,
  f215 number, f216 number, f217 number, f218 number, f219 number,
  f220 number, f221 number, f222 number, f223 number, f224 number,
  f225 number, f226 number, f227 number, f228 number, f229 number,
  f230 number, f231 number, f232 number, f233 number, f234 number,
  f235 number, f236 number, f237 number, f238 number, f239 number,
  f240 number, f241 number, f242 number, f243 number, f244 number,
  f245 number, f246 number, f247 number, f248 number, f249 number,
  f250 number, f251 number, f252 number, f253 number, f254 number,
  f255 number, f256 number, f257 number, f258 number, f259 number,
  f260 number, f261 number, f262 number, f263 number, f264 number,
  f265 number, f266 number, f267 number, f268 number, f269 number,
  f270 number, f271 number, f272 number, f273 number, f274 number,
  f275 number, f276 number, f277 number, f278 number, f279 number,
  f280 number, f281 number, f282 number, f283 number, f284 number,
  f285 number, f286 number, f287 number, f288 number, f289 number,
  f290 number, f291 number, f292 number, f293 number, f294 number,
  f295 number, f296 number, f297 number, f298 number, f299 number,

  f300 number, f301 number, f302 number, f303 number, f304 number,
  f305 number, f306 number, f307 number, f308 number, f309 number,
  f310 number, f311 number, f312 number, f313 number, f314 number,
  f315 number, f316 number, f317 number, f318 number, f319 number,
  f320 number, f321 number, f322 number, f323 number, f324 number,
  f325 number, f326 number, f327 number, f328 number, f329 number,
  f330 number, f331 number, f332 number, f333 number, f334 number,
  f335 number, f336 number, f337 number, f338 number, f339 number,
  f340 number, f341 number, f342 number, f343 number, f344 number,
  f345 number, f346 number, f347 number, f348 number, f349 number,
  f350 number, f351 number, f352 number, f353 number, f354 number,
  f355 number, f356 number, f357 number, f358 number, f359 number,
  f360 number, f361 number, f362 number, f363 number, f364 number,
  f365 number, f366 number, f367 number, f368 number, f369 number,
  f370 number, f371 number, f372 number, f373 number, f374 number,
  f375 number, f376 number, f377 number, f378 number, f379 number,
  f380 number, f381 number, f382 number, f383 number, f384 number,
  f385 number, f386 number, f387 number, f388 number, f389 number,
  f390 number, f391 number, f392 number, f393 number, f394 number,
  f395 number, f396 number, f397 number, f398 number, f399 number
)
RETURN number
IS
BEGIN
	return NULL;
END f_many_parameters;
/

CREATE OR REPLACE FUNCTION f_arrays1 (in_array u_number_array)
RETURN u_number_array
IS
BEGIN 
    return in_array;
END f_arrays1;
/

CREATE OR REPLACE FUNCTION f_arrays2 (in_array u_number_long_array)
RETURN u_number_long_array
IS
BEGIN 
    return in_array;
END f_arrays2;
/

CREATE OR REPLACE FUNCTION f_arrays3 (in_array u_string_array)
RETURN u_string_array
IS
BEGIN 
    return in_array;
END f_arrays3;
/

CREATE OR REPLACE PROCEDURE p_author_exists (author_name VARCHAR2, result OUT NUMBER)
IS
  v_result NUMBER(1);
BEGIN
  SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END
    INTO v_result
    FROM t_author 
   WHERE first_name LIKE author_name 
      OR last_name LIKE author_name;
      
  result := v_result;
END p_author_exists;
/

CREATE OR REPLACE FUNCTION f_author_exists (author_name VARCHAR2)
RETURN NUMBER
IS
  v_result NUMBER(1);
BEGIN
  SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END
    INTO v_result
    FROM t_author 
   WHERE first_name LIKE author_name 
      OR last_name LIKE author_name;
      
  return v_result;
END f_author_exists;
/

CREATE OR REPLACE FUNCTION f_one
RETURN NUMBER
IS
BEGIN
	RETURN 1;
END f_one;
/

CREATE OR REPLACE FUNCTION f_number(n NUMBER)
RETURN NUMBER
IS
BEGIN
	RETURN n;
END f_number;
/

CREATE OR REPLACE FUNCTION f317 (p1 INTEGER, p2 INTEGER, p3 INTEGER, p4 INTEGER) RETURN Integer deterministic IS
BEGIN
  return 1000 * p1 + 100 * p2 + p4;
END f317;
/

CREATE OR REPLACE PACKAGE library AS
	PROCEDURE pkg_p_author_exists (author_name VARCHAR2, result OUT NUMBER);
	PROCEDURE pkg_p_author_exists (author_name VARCHAR2, result OUT NUMBER, unused IN OUT NUMBER);
	FUNCTION pkg_f_author_exists (author_name VARCHAR2) RETURN NUMBER;
	FUNCTION pkg_f_author_exists (author_name VARCHAR2, unused NUMBER) RETURN NUMBER;
	FUNCTION pkg_f_unused RETURN NUMBER;
	
	PROCEDURE pkg_p_many_parameters (
	  f000 number, f001 number, f002 number, f003 number, f004 number,
	  f005 number, f006 number, f007 number, f008 number, f009 number,
	  f010 number, f011 number, f012 number, f013 number, f014 number,
	  f015 number, f016 number, f017 number, f018 number, f019 number,
	  f020 number, f021 number, f022 number, f023 number, f024 number,
	  f025 number, f026 number, f027 number, f028 number, f029 number,
	  f030 number, f031 number, f032 number, f033 number, f034 number,
	  f035 number, f036 number, f037 number, f038 number, f039 number,
	  f040 number, f041 number, f042 number, f043 number, f044 number,
	  f045 number, f046 number, f047 number, f048 number, f049 number,
	  f050 number, f051 number, f052 number, f053 number, f054 number,
	  f055 number, f056 number, f057 number, f058 number, f059 number,
	  f060 number, f061 number, f062 number, f063 number, f064 number,
	  f065 number, f066 number, f067 number, f068 number, f069 number,
	  f070 number, f071 number, f072 number, f073 number, f074 number,
	  f075 number, f076 number, f077 number, f078 number, f079 number,
	  f080 number, f081 number, f082 number, f083 number, f084 number,
	  f085 number, f086 number, f087 number, f088 number, f089 number,
	  f090 number, f091 number, f092 number, f093 number, f094 number,
	  f095 number, f096 number, f097 number, f098 number, f099 number,
	
	  f100 number, f101 number, f102 number, f103 number, f104 number,
	  f105 number, f106 number, f107 number, f108 number, f109 number,
	  f110 number, f111 number, f112 number, f113 number, f114 number,
	  f115 number, f116 number, f117 number, f118 number, f119 number,
	  f120 number, f121 number, f122 number, f123 number, f124 number,
	  f125 number, f126 number, f127 number, f128 number, f129 number,
	  f130 number, f131 number, f132 number, f133 number, f134 number,
	  f135 number, f136 number, f137 number, f138 number, f139 number,
	  f140 number, f141 number, f142 number, f143 number, f144 number,
	  f145 number, f146 number, f147 number, f148 number, f149 number,
	  f150 number, f151 number, f152 number, f153 number, f154 number,
	  f155 number, f156 number, f157 number, f158 number, f159 number,
	  f160 number, f161 number, f162 number, f163 number, f164 number,
	  f165 number, f166 number, f167 number, f168 number, f169 number,
	  f170 number, f171 number, f172 number, f173 number, f174 number,
	  f175 number, f176 number, f177 number, f178 number, f179 number,
	  f180 number, f181 number, f182 number, f183 number, f184 number,
	  f185 number, f186 number, f187 number, f188 number, f189 number,
	  f190 number, f191 number, f192 number, f193 number, f194 number,
	  f195 number, f196 number, f197 number, f198 number, f199 number,
	
	  f200 number, f201 number, f202 number, f203 number, f204 number,
	  f205 number, f206 number, f207 number, f208 number, f209 number,
	  f210 number, f211 number, f212 number, f213 number, f214 number,
	  f215 number, f216 number, f217 number, f218 number, f219 number,
	  f220 number, f221 number, f222 number, f223 number, f224 number,
	  f225 number, f226 number, f227 number, f228 number, f229 number,
	  f230 number, f231 number, f232 number, f233 number, f234 number,
	  f235 number, f236 number, f237 number, f238 number, f239 number,
	  f240 number, f241 number, f242 number, f243 number, f244 number,
	  f245 number, f246 number, f247 number, f248 number, f249 number,
	  f250 number, f251 number, f252 number, f253 number, f254 number,
	  f255 number, f256 number, f257 number, f258 number, f259 number,
	  f260 number, f261 number, f262 number, f263 number, f264 number,
	  f265 number, f266 number, f267 number, f268 number, f269 number,
	  f270 number, f271 number, f272 number, f273 number, f274 number,
	  f275 number, f276 number, f277 number, f278 number, f279 number,
	  f280 number, f281 number, f282 number, f283 number, f284 number,
	  f285 number, f286 number, f287 number, f288 number, f289 number,
	  f290 number, f291 number, f292 number, f293 number, f294 number,
	  f295 number, f296 number, f297 number, f298 number, f299 number,
	
	  f300 number, f301 number, f302 number, f303 number, f304 number,
	  f305 number, f306 number, f307 number, f308 number, f309 number,
	  f310 number, f311 number, f312 number, f313 number, f314 number,
	  f315 number, f316 number, f317 number, f318 number, f319 number,
	  f320 number, f321 number, f322 number, f323 number, f324 number,
	  f325 number, f326 number, f327 number, f328 number, f329 number,
	  f330 number, f331 number, f332 number, f333 number, f334 number,
	  f335 number, f336 number, f337 number, f338 number, f339 number,
	  f340 number, f341 number, f342 number, f343 number, f344 number,
	  f345 number, f346 number, f347 number, f348 number, f349 number,
	  f350 number, f351 number, f352 number, f353 number, f354 number,
	  f355 number, f356 number, f357 number, f358 number, f359 number,
	  f360 number, f361 number, f362 number, f363 number, f364 number,
	  f365 number, f366 number, f367 number, f368 number, f369 number,
	  f370 number, f371 number, f372 number, f373 number, f374 number,
	  f375 number, f376 number, f377 number, f378 number, f379 number,
	  f380 number, f381 number, f382 number, f383 number, f384 number,
	  f385 number, f386 number, f387 number, f388 number, f389 number,
	  f390 number, f391 number, f392 number, f393 number, f394 number,
	  f395 number, f396 number, f397 number, f398 number, f399 number
	);

	FUNCTION pkg_f_many_parameters (
	  f000 number, f001 number, f002 number, f003 number, f004 number,
	  f005 number, f006 number, f007 number, f008 number, f009 number,
	  f010 number, f011 number, f012 number, f013 number, f014 number,
	  f015 number, f016 number, f017 number, f018 number, f019 number,
	  f020 number, f021 number, f022 number, f023 number, f024 number,
	  f025 number, f026 number, f027 number, f028 number, f029 number,
	  f030 number, f031 number, f032 number, f033 number, f034 number,
	  f035 number, f036 number, f037 number, f038 number, f039 number,
	  f040 number, f041 number, f042 number, f043 number, f044 number,
	  f045 number, f046 number, f047 number, f048 number, f049 number,
	  f050 number, f051 number, f052 number, f053 number, f054 number,
	  f055 number, f056 number, f057 number, f058 number, f059 number,
	  f060 number, f061 number, f062 number, f063 number, f064 number,
	  f065 number, f066 number, f067 number, f068 number, f069 number,
	  f070 number, f071 number, f072 number, f073 number, f074 number,
	  f075 number, f076 number, f077 number, f078 number, f079 number,
	  f080 number, f081 number, f082 number, f083 number, f084 number,
	  f085 number, f086 number, f087 number, f088 number, f089 number,
	  f090 number, f091 number, f092 number, f093 number, f094 number,
	  f095 number, f096 number, f097 number, f098 number, f099 number,
	
	  f100 number, f101 number, f102 number, f103 number, f104 number,
	  f105 number, f106 number, f107 number, f108 number, f109 number,
	  f110 number, f111 number, f112 number, f113 number, f114 number,
	  f115 number, f116 number, f117 number, f118 number, f119 number,
	  f120 number, f121 number, f122 number, f123 number, f124 number,
	  f125 number, f126 number, f127 number, f128 number, f129 number,
	  f130 number, f131 number, f132 number, f133 number, f134 number,
	  f135 number, f136 number, f137 number, f138 number, f139 number,
	  f140 number, f141 number, f142 number, f143 number, f144 number,
	  f145 number, f146 number, f147 number, f148 number, f149 number,
	  f150 number, f151 number, f152 number, f153 number, f154 number,
	  f155 number, f156 number, f157 number, f158 number, f159 number,
	  f160 number, f161 number, f162 number, f163 number, f164 number,
	  f165 number, f166 number, f167 number, f168 number, f169 number,
	  f170 number, f171 number, f172 number, f173 number, f174 number,
	  f175 number, f176 number, f177 number, f178 number, f179 number,
	  f180 number, f181 number, f182 number, f183 number, f184 number,
	  f185 number, f186 number, f187 number, f188 number, f189 number,
	  f190 number, f191 number, f192 number, f193 number, f194 number,
	  f195 number, f196 number, f197 number, f198 number, f199 number,
	
	  f200 number, f201 number, f202 number, f203 number, f204 number,
	  f205 number, f206 number, f207 number, f208 number, f209 number,
	  f210 number, f211 number, f212 number, f213 number, f214 number,
	  f215 number, f216 number, f217 number, f218 number, f219 number,
	  f220 number, f221 number, f222 number, f223 number, f224 number,
	  f225 number, f226 number, f227 number, f228 number, f229 number,
	  f230 number, f231 number, f232 number, f233 number, f234 number,
	  f235 number, f236 number, f237 number, f238 number, f239 number,
	  f240 number, f241 number, f242 number, f243 number, f244 number,
	  f245 number, f246 number, f247 number, f248 number, f249 number,
	  f250 number, f251 number, f252 number, f253 number, f254 number,
	  f255 number, f256 number, f257 number, f258 number, f259 number,
	  f260 number, f261 number, f262 number, f263 number, f264 number,
	  f265 number, f266 number, f267 number, f268 number, f269 number,
	  f270 number, f271 number, f272 number, f273 number, f274 number,
	  f275 number, f276 number, f277 number, f278 number, f279 number,
	  f280 number, f281 number, f282 number, f283 number, f284 number,
	  f285 number, f286 number, f287 number, f288 number, f289 number,
	  f290 number, f291 number, f292 number, f293 number, f294 number,
	  f295 number, f296 number, f297 number, f298 number, f299 number,
	
	  f300 number, f301 number, f302 number, f303 number, f304 number,
	  f305 number, f306 number, f307 number, f308 number, f309 number,
	  f310 number, f311 number, f312 number, f313 number, f314 number,
	  f315 number, f316 number, f317 number, f318 number, f319 number,
	  f320 number, f321 number, f322 number, f323 number, f324 number,
	  f325 number, f326 number, f327 number, f328 number, f329 number,
	  f330 number, f331 number, f332 number, f333 number, f334 number,
	  f335 number, f336 number, f337 number, f338 number, f339 number,
	  f340 number, f341 number, f342 number, f343 number, f344 number,
	  f345 number, f346 number, f347 number, f348 number, f349 number,
	  f350 number, f351 number, f352 number, f353 number, f354 number,
	  f355 number, f356 number, f357 number, f358 number, f359 number,
	  f360 number, f361 number, f362 number, f363 number, f364 number,
	  f365 number, f366 number, f367 number, f368 number, f369 number,
	  f370 number, f371 number, f372 number, f373 number, f374 number,
	  f375 number, f376 number, f377 number, f378 number, f379 number,
	  f380 number, f381 number, f382 number, f383 number, f384 number,
	  f385 number, f386 number, f387 number, f388 number, f389 number,
	  f390 number, f391 number, f392 number, f393 number, f394 number,
	  f395 number, f396 number, f397 number, f398 number, f399 number
	) return number;
	
END library;
/

CREATE OR REPLACE PACKAGE BODY library AS
	PROCEDURE pkg_p_author_exists (author_name VARCHAR2, result OUT NUMBER) IS
	BEGIN
		test.p_author_exists(author_name, result);
	END pkg_p_author_exists;

	PROCEDURE pkg_p_author_exists (author_name VARCHAR2, result OUT NUMBER, unused IN OUT NUMBER) IS
	BEGIN
		test.p_author_exists(author_name, result);
	END pkg_p_author_exists;

	FUNCTION pkg_f_author_exists (author_name VARCHAR2) RETURN NUMBER IS
	BEGIN
		return test.f_author_exists(author_name);
	END pkg_f_author_exists;

	FUNCTION pkg_f_author_exists (author_name VARCHAR2, unused NUMBER) RETURN NUMBER IS
	BEGIN
		return test.f_author_exists(author_name);
	END pkg_f_author_exists;
	
	FUNCTION pkg_f_unused RETURN NUMBER IS
	BEGIN
		return 0;
	END pkg_f_unused;
	
	PROCEDURE pkg_p_many_parameters (
	  f000 number, f001 number, f002 number, f003 number, f004 number,
	  f005 number, f006 number, f007 number, f008 number, f009 number,
	  f010 number, f011 number, f012 number, f013 number, f014 number,
	  f015 number, f016 number, f017 number, f018 number, f019 number,
	  f020 number, f021 number, f022 number, f023 number, f024 number,
	  f025 number, f026 number, f027 number, f028 number, f029 number,
	  f030 number, f031 number, f032 number, f033 number, f034 number,
	  f035 number, f036 number, f037 number, f038 number, f039 number,
	  f040 number, f041 number, f042 number, f043 number, f044 number,
	  f045 number, f046 number, f047 number, f048 number, f049 number,
	  f050 number, f051 number, f052 number, f053 number, f054 number,
	  f055 number, f056 number, f057 number, f058 number, f059 number,
	  f060 number, f061 number, f062 number, f063 number, f064 number,
	  f065 number, f066 number, f067 number, f068 number, f069 number,
	  f070 number, f071 number, f072 number, f073 number, f074 number,
	  f075 number, f076 number, f077 number, f078 number, f079 number,
	  f080 number, f081 number, f082 number, f083 number, f084 number,
	  f085 number, f086 number, f087 number, f088 number, f089 number,
	  f090 number, f091 number, f092 number, f093 number, f094 number,
	  f095 number, f096 number, f097 number, f098 number, f099 number,
	
	  f100 number, f101 number, f102 number, f103 number, f104 number,
	  f105 number, f106 number, f107 number, f108 number, f109 number,
	  f110 number, f111 number, f112 number, f113 number, f114 number,
	  f115 number, f116 number, f117 number, f118 number, f119 number,
	  f120 number, f121 number, f122 number, f123 number, f124 number,
	  f125 number, f126 number, f127 number, f128 number, f129 number,
	  f130 number, f131 number, f132 number, f133 number, f134 number,
	  f135 number, f136 number, f137 number, f138 number, f139 number,
	  f140 number, f141 number, f142 number, f143 number, f144 number,
	  f145 number, f146 number, f147 number, f148 number, f149 number,
	  f150 number, f151 number, f152 number, f153 number, f154 number,
	  f155 number, f156 number, f157 number, f158 number, f159 number,
	  f160 number, f161 number, f162 number, f163 number, f164 number,
	  f165 number, f166 number, f167 number, f168 number, f169 number,
	  f170 number, f171 number, f172 number, f173 number, f174 number,
	  f175 number, f176 number, f177 number, f178 number, f179 number,
	  f180 number, f181 number, f182 number, f183 number, f184 number,
	  f185 number, f186 number, f187 number, f188 number, f189 number,
	  f190 number, f191 number, f192 number, f193 number, f194 number,
	  f195 number, f196 number, f197 number, f198 number, f199 number,
	
	  f200 number, f201 number, f202 number, f203 number, f204 number,
	  f205 number, f206 number, f207 number, f208 number, f209 number,
	  f210 number, f211 number, f212 number, f213 number, f214 number,
	  f215 number, f216 number, f217 number, f218 number, f219 number,
	  f220 number, f221 number, f222 number, f223 number, f224 number,
	  f225 number, f226 number, f227 number, f228 number, f229 number,
	  f230 number, f231 number, f232 number, f233 number, f234 number,
	  f235 number, f236 number, f237 number, f238 number, f239 number,
	  f240 number, f241 number, f242 number, f243 number, f244 number,
	  f245 number, f246 number, f247 number, f248 number, f249 number,
	  f250 number, f251 number, f252 number, f253 number, f254 number,
	  f255 number, f256 number, f257 number, f258 number, f259 number,
	  f260 number, f261 number, f262 number, f263 number, f264 number,
	  f265 number, f266 number, f267 number, f268 number, f269 number,
	  f270 number, f271 number, f272 number, f273 number, f274 number,
	  f275 number, f276 number, f277 number, f278 number, f279 number,
	  f280 number, f281 number, f282 number, f283 number, f284 number,
	  f285 number, f286 number, f287 number, f288 number, f289 number,
	  f290 number, f291 number, f292 number, f293 number, f294 number,
	  f295 number, f296 number, f297 number, f298 number, f299 number,
	
	  f300 number, f301 number, f302 number, f303 number, f304 number,
	  f305 number, f306 number, f307 number, f308 number, f309 number,
	  f310 number, f311 number, f312 number, f313 number, f314 number,
	  f315 number, f316 number, f317 number, f318 number, f319 number,
	  f320 number, f321 number, f322 number, f323 number, f324 number,
	  f325 number, f326 number, f327 number, f328 number, f329 number,
	  f330 number, f331 number, f332 number, f333 number, f334 number,
	  f335 number, f336 number, f337 number, f338 number, f339 number,
	  f340 number, f341 number, f342 number, f343 number, f344 number,
	  f345 number, f346 number, f347 number, f348 number, f349 number,
	  f350 number, f351 number, f352 number, f353 number, f354 number,
	  f355 number, f356 number, f357 number, f358 number, f359 number,
	  f360 number, f361 number, f362 number, f363 number, f364 number,
	  f365 number, f366 number, f367 number, f368 number, f369 number,
	  f370 number, f371 number, f372 number, f373 number, f374 number,
	  f375 number, f376 number, f377 number, f378 number, f379 number,
	  f380 number, f381 number, f382 number, f383 number, f384 number,
	  f385 number, f386 number, f387 number, f388 number, f389 number,
	  f390 number, f391 number, f392 number, f393 number, f394 number,
	  f395 number, f396 number, f397 number, f398 number, f399 number
	)
	IS
	BEGIN
		NULL;
	END pkg_p_many_parameters;

	FUNCTION pkg_f_many_parameters (
	  f000 number, f001 number, f002 number, f003 number, f004 number,
	  f005 number, f006 number, f007 number, f008 number, f009 number,
	  f010 number, f011 number, f012 number, f013 number, f014 number,
	  f015 number, f016 number, f017 number, f018 number, f019 number,
	  f020 number, f021 number, f022 number, f023 number, f024 number,
	  f025 number, f026 number, f027 number, f028 number, f029 number,
	  f030 number, f031 number, f032 number, f033 number, f034 number,
	  f035 number, f036 number, f037 number, f038 number, f039 number,
	  f040 number, f041 number, f042 number, f043 number, f044 number,
	  f045 number, f046 number, f047 number, f048 number, f049 number,
	  f050 number, f051 number, f052 number, f053 number, f054 number,
	  f055 number, f056 number, f057 number, f058 number, f059 number,
	  f060 number, f061 number, f062 number, f063 number, f064 number,
	  f065 number, f066 number, f067 number, f068 number, f069 number,
	  f070 number, f071 number, f072 number, f073 number, f074 number,
	  f075 number, f076 number, f077 number, f078 number, f079 number,
	  f080 number, f081 number, f082 number, f083 number, f084 number,
	  f085 number, f086 number, f087 number, f088 number, f089 number,
	  f090 number, f091 number, f092 number, f093 number, f094 number,
	  f095 number, f096 number, f097 number, f098 number, f099 number,
	
	  f100 number, f101 number, f102 number, f103 number, f104 number,
	  f105 number, f106 number, f107 number, f108 number, f109 number,
	  f110 number, f111 number, f112 number, f113 number, f114 number,
	  f115 number, f116 number, f117 number, f118 number, f119 number,
	  f120 number, f121 number, f122 number, f123 number, f124 number,
	  f125 number, f126 number, f127 number, f128 number, f129 number,
	  f130 number, f131 number, f132 number, f133 number, f134 number,
	  f135 number, f136 number, f137 number, f138 number, f139 number,
	  f140 number, f141 number, f142 number, f143 number, f144 number,
	  f145 number, f146 number, f147 number, f148 number, f149 number,
	  f150 number, f151 number, f152 number, f153 number, f154 number,
	  f155 number, f156 number, f157 number, f158 number, f159 number,
	  f160 number, f161 number, f162 number, f163 number, f164 number,
	  f165 number, f166 number, f167 number, f168 number, f169 number,
	  f170 number, f171 number, f172 number, f173 number, f174 number,
	  f175 number, f176 number, f177 number, f178 number, f179 number,
	  f180 number, f181 number, f182 number, f183 number, f184 number,
	  f185 number, f186 number, f187 number, f188 number, f189 number,
	  f190 number, f191 number, f192 number, f193 number, f194 number,
	  f195 number, f196 number, f197 number, f198 number, f199 number,
	
	  f200 number, f201 number, f202 number, f203 number, f204 number,
	  f205 number, f206 number, f207 number, f208 number, f209 number,
	  f210 number, f211 number, f212 number, f213 number, f214 number,
	  f215 number, f216 number, f217 number, f218 number, f219 number,
	  f220 number, f221 number, f222 number, f223 number, f224 number,
	  f225 number, f226 number, f227 number, f228 number, f229 number,
	  f230 number, f231 number, f232 number, f233 number, f234 number,
	  f235 number, f236 number, f237 number, f238 number, f239 number,
	  f240 number, f241 number, f242 number, f243 number, f244 number,
	  f245 number, f246 number, f247 number, f248 number, f249 number,
	  f250 number, f251 number, f252 number, f253 number, f254 number,
	  f255 number, f256 number, f257 number, f258 number, f259 number,
	  f260 number, f261 number, f262 number, f263 number, f264 number,
	  f265 number, f266 number, f267 number, f268 number, f269 number,
	  f270 number, f271 number, f272 number, f273 number, f274 number,
	  f275 number, f276 number, f277 number, f278 number, f279 number,
	  f280 number, f281 number, f282 number, f283 number, f284 number,
	  f285 number, f286 number, f287 number, f288 number, f289 number,
	  f290 number, f291 number, f292 number, f293 number, f294 number,
	  f295 number, f296 number, f297 number, f298 number, f299 number,
	
	  f300 number, f301 number, f302 number, f303 number, f304 number,
	  f305 number, f306 number, f307 number, f308 number, f309 number,
	  f310 number, f311 number, f312 number, f313 number, f314 number,
	  f315 number, f316 number, f317 number, f318 number, f319 number,
	  f320 number, f321 number, f322 number, f323 number, f324 number,
	  f325 number, f326 number, f327 number, f328 number, f329 number,
	  f330 number, f331 number, f332 number, f333 number, f334 number,
	  f335 number, f336 number, f337 number, f338 number, f339 number,
	  f340 number, f341 number, f342 number, f343 number, f344 number,
	  f345 number, f346 number, f347 number, f348 number, f349 number,
	  f350 number, f351 number, f352 number, f353 number, f354 number,
	  f355 number, f356 number, f357 number, f358 number, f359 number,
	  f360 number, f361 number, f362 number, f363 number, f364 number,
	  f365 number, f366 number, f367 number, f368 number, f369 number,
	  f370 number, f371 number, f372 number, f373 number, f374 number,
	  f375 number, f376 number, f377 number, f378 number, f379 number,
	  f380 number, f381 number, f382 number, f383 number, f384 number,
	  f385 number, f386 number, f387 number, f388 number, f389 number,
	  f390 number, f391 number, f392 number, f393 number, f394 number,
	  f395 number, f396 number, f397 number, f398 number, f399 number
	) 
	return number
	IS
	BEGIN
		return null;
	END pkg_f_many_parameters;
END library;
/
