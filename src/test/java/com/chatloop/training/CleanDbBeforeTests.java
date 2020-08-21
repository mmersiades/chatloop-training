package com.chatloop.training;

import org.springframework.test.context.jdbc.Sql;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Inherited
@Target(TYPE)
@Retention(RUNTIME)
@Sql(value = "classpath:db_scripts/drop_all.sql")
public @interface CleanDbBeforeTests {
}
