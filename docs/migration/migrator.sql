-- can be invoked in psql with: \i ./docs/migration/migrator.sql;
-- ./ works if execute within root of project, change otherwise;

BEGIN;

CREATE TEMPORARY TABLE temp_monolith_user_ids (
   user_id character varying(255)  NOT NULL
);

CREATE TEMPORARY TABLE temp_unique_courses_modules (
    space_id character varying(255)  NOT NULL,
    env_id character varying(255)  NOT NULL,
    course_id character varying(255)  NOT NULL,
    module_id character varying(255)  NOT NULL
);


\COPY temp_monolith_user_ids(user_id) FROM './monolith-user-ids.csv' DELIMITER ',' CSV HEADER;

\COPY temp_unique_courses_modules(space_id, env_id, course_id, module_id) FROM './contentful-data.csv' DELIMITER ',' CSV HEADER;


INSERT INTO public.course_progress (id, space_id, env_id, course_id, user_id, course_result)
SELECT
    nextval('course_progress_sequence_gen'),
    t2.space_id,
    t2.env_id,
    t2.course_id,
    t1.user_id,
    'COURSE_PASSED'
FROM
    temp_monolith_user_ids AS t1
    CROSS JOIN
    (
        SELECT DISTINCT space_id, env_id, course_id FROM temp_unique_courses_modules
    ) AS t2;

INSERT INTO public.module_progress (id, module_id, course_id, complete, incorrect_attempts)
SELECT
    nextval('module_progress_sequence_gen'),
    t2.module_id,
    t1.id,
    TRUE,
    0
FROM
    (
        SELECT
            id, space_id, env_id, course_id
        FROM
            public.course_progress AS prog
        WHERE
            prog.user_id IN (SELECT user_id from temp_monolith_user_ids)
    ) AS t1
    INNER JOIN
    (
        SELECT
            DISTINCT space_id, env_id, course_id, module_id
        FROM
            temp_unique_courses_modules
    ) AS t2
    ON
        t1.space_id = t2.space_id AND t1.env_id = t2.env_id AND t1.course_id = t2.course_id;


COMMIT;
