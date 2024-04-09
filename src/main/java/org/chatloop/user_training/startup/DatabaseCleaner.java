package org.chatloop.user_training.startup;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DatabaseCleaner {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void clean() {
        final Query allTablesQuery = this.em.createNativeQuery("SELECT\n" +
                " tablename\n" +
                "FROM\n" +
                " pg_catalog.pg_tables\n" +
                "WHERE\n" +
                " schemaname != 'pg_catalog'\n" +
                "AND schemaname != 'information_schema'");

       final List<Object> tablesList = allTablesQuery.getResultList();
       final List<String> tablesToClear = tablesList
               .stream()
               .map(t -> (String) t)
               .collect(Collectors.toList());

       tablesToClear.forEach(this::deleteAllRows);
    }

    private void deleteAllRows(String table) {
        final String deleteSql = "TRUNCATE TABLE " + table + " RESTART IDENTITY CASCADE";
        this.em.createNativeQuery(deleteSql).executeUpdate();
    }
}
