/* ---------------------------------------------------- */
/*  Generated by Enterprise Architect Version 15.2 		*/
/*  Created On : 13-дек-2024 10:05:27 				*/
/*  DBMS       : PostgreSQL 						*/
/* ---------------------------------------------------- */

/* Drop Tables */

--DROP TABLE IF EXISTS subscribers CASCADE--
;

/* Create Tables */

CREATE TABLE subscribers
(
	id bigint NOT NULL,
	entity varchar(255) NOT NULL,
	event_type varchar(255) NOT NULL,
	url varchar(255) NOT NULL,
	create_at timestamp without time zone NOT NULL
)
;

/* Create Primary Keys, Indexes, Uniques, Checks */

ALTER TABLE subscribers ADD CONSTRAINT "PK_subscribers"
	PRIMARY KEY (id)
;
