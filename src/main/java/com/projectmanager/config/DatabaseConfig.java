package com.projectmanager.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("prod")
public class DatabaseConfig {

    @Bean
    public DataSource dataSource() {
        String rawUrl = System.getenv("DATABASE_URL");
        System.out.println("=== RAW DATABASE_URL: " + rawUrl);

        // Parse postgresql://user:password@host/dbname
        // into jdbc:postgresql://host/dbname?user=user&password=password
        String jdbcUrl;
        String username = null;
        String password = null;

        if (rawUrl.startsWith("jdbc:")) {
            jdbcUrl = rawUrl;
        } else {
            // Remove protocol prefix
            String withoutPrefix = rawUrl
                .replace("postgresql://", "")
                .replace("postgres://", "");

            // Split user:password@host/db
            String[] atSplit = withoutPrefix.split("@");
            String userInfo = atSplit[0]; // user:password
            String hostAndDb = atSplit[1]; // host/db

            String[] userSplit = userInfo.split(":");
            username = userSplit[0];
            password = userSplit[1];

            jdbcUrl = "jdbc:postgresql://" + hostAndDb + "?sslmode=require";
        }

        System.out.println("=== JDBC URL: " + jdbcUrl);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setDriverClassName("org.postgresql.Driver");
        if (username != null) config.setUsername(username);
        if (password != null) config.setPassword(password);
        config.setMaximumPoolSize(3);
        config.setConnectionTimeout(30000);

        return new HikariDataSource(config);
    }
}
