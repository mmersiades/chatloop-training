CREATE TABLE IF NOT EXISTS public.training_event
(
    id bigint NOT NULL,
    course_id character varying(255)  NOT NULL,
    created timestamp without time zone NOT NULL,
    env_id character varying(255)  NOT NULL,
    module_id character varying(255) ,
    space_id character varying(255)  NOT NULL,
    training_event_type character varying(255)  NOT NULL,
    user_id character varying(255)  NOT NULL,
    CONSTRAINT training_event_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.course_progress
(
    id bigint NOT NULL,
    course_id character varying(255)  NOT NULL,
    course_result character varying(255)  NOT NULL,
    env_id character varying(255)  NOT NULL,
    space_id character varying(255)  NOT NULL,
    user_id character varying(255)  NOT NULL,
    CONSTRAINT course_progress_pkey PRIMARY KEY (id),
    CONSTRAINT course_progress_unique_constraint UNIQUE (space_id, env_id, user_id, course_id)
);

CREATE TABLE IF NOT EXISTS public.earned_qualifications
(
    user_id character varying(255) PRIMARY KEY,
    qualifications text NULL
);

CREATE TABLE IF NOT EXISTS public.module_progress
(
    id bigint NOT NULL,
    complete boolean NOT NULL,
    incorrect_attempts integer NOT NULL,
    module_id character varying(255)  NOT NULL,
    course_id bigint NOT NULL,
    CONSTRAINT module_progress_pkey PRIMARY KEY (id),
    CONSTRAINT module_progress_fkey FOREIGN KEY (course_id)
        REFERENCES public.course_progress (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

-- INCREMENT should match SequenceGenerator.allocationSize in POJOs to avoid negative primary keys or conflicts;
CREATE SEQUENCE IF NOT EXISTS training_event_sequence_gen INCREMENT 100 START 1;
CREATE SEQUENCE IF NOT EXISTS course_progress_sequence_gen INCREMENT 100 START 1;
CREATE SEQUENCE IF NOT EXISTS module_progress_sequence_gen INCREMENT 100 START 1;