package com.schooltech.multitenant;

import com.schooltech.sms.configuration.ClientProperties;
import jakarta.persistence.EntityManagerFactory;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.schooltech.sms.dao.master", // Package where UserRepository resides
        entityManagerFactoryRef = "masterEntityManagerFactory",
        transactionManagerRef = "masterTransactionManager"
)
public class MasterDataSourceConfig {

    @Autowired
    private ClientProperties clientProperties;


//    @Bean(name = "masterDataSource")
//    @ConfigurationProperties(prefix = "spring.datasource.master")
//    public DataSource masterDataSource() {
//        return new DriverManagerDataSource();
//    }

    @Bean(name = "hibernatePropertiesMaster")
    public Properties hibernatePropertiesMaster() {
        Properties properties = new Properties();
        properties.put("hibernate.hbm2ddl.auto", "none");
        properties.put("hibernate.hbm2ddl.schema-generation.scripts.action", "create");
        properties.put("hibernate.hbm2ddl.schema-generation.scripts.create-target", "src/main/resources//db/migrations/generate/masterschema.sql");
        properties.put("hibernate.hbm2ddl.delimiter", ";");
        properties.put("hibernate.hbm2ddl.sql-comments", "true");

        return properties;
    }

    @Bean(initMethod = "migrate")
    public Flyway flyway(@Qualifier("masterDataSource") DataSource masterDataSource) {
        String mysqlHost = System.getenv("MYSQL_HOST") != null ? System.getenv("MYSQL_HOST") : "localhost";
        String mysqlPort = System.getenv("MYSQL_PORT") != null ? System.getenv("MYSQL_PORT") : "3306";
        String mysqlUser = System.getenv("MYSQL_USER") != null ? System.getenv("MYSQL_USER") : "root";
        String mysqlPassword = System.getenv("MYSQL_PASSWORD") != null ? System.getenv("MYSQL_PASSWORD") : "root";
        String mysqlDB = System.getenv("MYSQL_DB") != null ? System.getenv("MYSQL_DB") : "stmaster";
        Map<String, String> envMap = new HashMap<>();
        envMap.put("MYSQL_HOST", mysqlHost);
        envMap.put("MYSQL_PORT", mysqlPort);
        envMap.put("MYSQL_USER", mysqlUser);
        envMap.put("MYSQL_PASSWORD", mysqlPassword);
        envMap.put("MYSQL_DB", mysqlDB);
        envMap.putAll(clientProperties.getDb_details());


        return Flyway.configure()
                .dataSource(masterDataSource)
                .locations("classpath:db/migrations/master") // Path for master DB scripts
                .baselineOnMigrate(true)
                .validateOnMigrate(true)
                .placeholders(envMap)
                .load();
    }

    @Bean(name = "masterEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean masterEntityManagerFactory(
            //EntityManagerFactoryBuilder builder,
            @Qualifier("masterDataSource") DataSource masterDataSource, JpaProperties jpaProperties) {

        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(masterDataSource);
        factoryBean.setPackagesToScan("com.schooltech.sms.entity.master"); // Package where User entity resides
        factoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        //factoryBean.setJpaProperties(jpaProperties.getProperties());
        factoryBean.setJpaProperties(hibernatePropertiesMaster());
        return factoryBean;
    }

    @Bean(name = "masterTransactionManager")
    @Primary
    public PlatformTransactionManager masterTransactionManager(
            @Qualifier("masterEntityManagerFactory") EntityManagerFactory masterEntityManagerFactory) {
        return new JpaTransactionManager(masterEntityManagerFactory);
    }


    @Bean(name = "masterDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.master")
    public DataSource masterDataSource() {

        String mysqlHost = System.getenv("MYSQL_HOST") != null ? System.getenv("MYSQL_HOST") : "localhost";
        String mysqlPort = System.getenv("MYSQL_PORT") != null ? System.getenv("MYSQL_PORT") : "3306";
        String mysqlUser = System.getenv("MYSQL_USER") != null ? System.getenv("MYSQL_USER") : "root";
        String mysqlPassword = System.getenv("MYSQL_PASSWORD") != null ? System.getenv("MYSQL_PASSWORD") : "root";
        String mysqlDB = System.getenv("MYSQL_DB") != null ? System.getenv("MYSQL_DB") : "stmaster";


        String jdbcUrl = "jdbc:mysql://" + mysqlHost + ":" + mysqlPort + "/";
        String dbName = mysqlDB;

        try (Connection connection = DriverManager.getConnection(jdbcUrl, mysqlUser, mysqlPassword)) {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
        } catch (SQLException e) {
            throw new RuntimeException("Error creating database " + dbName, e);
        }

        return new DriverManagerDataSource(jdbcUrl + dbName, mysqlUser, mysqlPassword);
    }


}
