/* ---------------------------------------------------- */
/*  Generated by Enterprise Architect Version 15.2 		*/
/*  Created On : 13-дек-2024 10:04:36 				*/
/*  DBMS       : PostgreSQL 						*/
/* ---------------------------------------------------- */

/* Drop Tables */

--DROP TABLE IF EXISTS school CASCADE--
;

/* Create Tables */

CREATE TABLE school
(
	id bigint NOT NULL,
	name varchar(100) NOT NULL,
	address varchar(200) NOT NULL,
	created_at timestamp without time zone NOT NULL,
	update_at timestamp without time zone NOT NULL
)
;

/* Create Primary Keys, Indexes, Uniques, Checks */

ALTER TABLE school ADD CONSTRAINT "PK_school"
	PRIMARY KEY (id)
;