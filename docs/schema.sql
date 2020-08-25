-- select * from information_schema.constraints

-- Contentful ID (sys.id) is unique to each environment. TODO check if some guards are needed;
CREATE TABLE course_progress (
	id bigint NOT NULL,

	space_id varchar(255) NOT NULL,
	env_id varchar(255) NOT NULL,
	user_id varchar(255) NOT NULL,
	course_id varchar(255) NOT NULL,
	
	course_status varchar(255) NOT NULL,
	
	primary key (id)
);

CREATE TABLE module_progress (
	id bigint NOT NULL,
	
	course_id bigint NOT NULL,
	module_id varchar(255) NOT NULL,
	
	incorrect_attempts integer NOT NULL,
	complete boolean not null,
	
	primary key (id)
);

CREATE TABLE training_event (
	id bigint NOT NULL,

	space_id varchar(255) NOT NULL,
	env_id varchar(255) NOT NULL,

	user_id varchar(255) NOT NULL,
	program_id varchar(255) NOT NULL,
	course_id varchar(255) NOT NULL,
	module_id varchar(255) NOT NULL,

	event_type varchar(255) NOT NULL

	created timestamp NOT NULL,
	primary key (id)
);
