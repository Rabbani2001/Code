package com.schooltech.multitenant;

import com.schooltech.sms.configuration.ClientProperties;
import jakarta.persistence.EntityManagerFactory;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
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
        basePackages = "com.schooltech.sms.dao.client", // Package where client repositories reside
        entityManagerFactoryRef = "clientEntityManagerFactory",
        transactionManagerRef = "clientTransactionManager"
)
public class ClientMultiTenantDataSourceConfig {

    private static final Logger logger = LoggerFactory.getLogger(ClientMultiTenantDataSourceConfig.class);


    @Autowired
    private ClientProperties clientProperties;
    @Value("${stclient.db_details.stclient_default}")
    private String stclient_default; //This property can also be taken from clientProperties but to avod iterating over map i used this method to collect the stclient_default key
    @Autowired
    private TenantDataSourceProvider tenantDataSourceProvider;

    @Bean(name = "clientEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(JpaProperties jpaProperties) {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(multiTenantDataSource());
        factoryBean.setPackagesToScan("com.schooltech.sms.entity.client");// Package where client entities reside
        factoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        //factoryBean.setJpaProperties(jpaProperties.getProperties());
        factoryBean.setJpaProperties(hibernatePropertiesClient());
        return factoryBean;
    }

    @Bean(name = "hibernatePropertiesClient")
    public Properties hibernatePropertiesClient() {
        Properties properties = new Properties();
//        properties.put("hibernate.ddl-auto", "none");
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.hbm2ddl.schema-generation.scripts.action", "update");
        properties.put("hibernate.hbm2ddl.schema-generation.scripts.create-target", "src/main/resources/db/migrations/generate/clientschema.sql");

        //properties.put("hibernate.hbm2ddl.delimiter", ";");
        //properties.put("hibernate.hbm2ddl.sql-comments", "true");
        return properties;
    }

    @Bean(name = "clientTransactionManager")
    public JpaTransactionManager transactionManager(@Qualifier("clientEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

//    @Bean
//    public DataSource multiTenantDataSource() {
//        MultiTenantRoutingDataSource dataSource = new MultiTenantRoutingDataSource();
//
//        // Create a map of tenant data sources
//        Map<Object, Object> targetDataSources = new HashMap<>();
//
//        clientProperties.getDb_details().forEach((key, value) -> {
//            //System.out.println("================="+key + " : " + value);
//            targetDataSources.put(value, tenantDataSourceProvider.getDataSource(value));
//        });
//
//        // Add a default tenant data source
////        DataSource defaultDataSource = tenantDataSourceProvider.getDataSource("rtclient_default");
////        targetDataSources.put("rtclient_default", defaultDataSource);
////        //targetDataSources.put("iqraa", tenantDataSourceProvider.getDataSource("iqraa"));
////        //targetDataSources.put("rtmaster", tenantDataSourceProvider.getDataSource("rtmaster"));
////        targetDataSources.put("rtclient_iqraa", tenantDataSourceProvider.getDataSource("rtclient_iqraa"));
////        targetDataSources.put("rtclient_kanchan", tenantDataSourceProvider.getDataSource("rtclient_kanchan"));
//
//
//        //System.out.println("=================stclient_default : "+stclient_default);
//        DataSource defaultDataSource = tenantDataSourceProvider.getDataSource(stclient_default);
//        targetDataSources.put(stclient_default, defaultDataSource);
//
//        // Set the map to the routing data source
//        dataSource.setTargetDataSources(targetDataSources);
//        // Set the default target data source
//        dataSource.setDefaultTargetDataSource(defaultDataSource);
//        handleFlyway(targetDataSources);
//
//        dataSource.afterPropertiesSet();
//        logger.info("RESOURCTECH........Target DataSources: {}", targetDataSources.keySet());
//        return dataSource;
//    }

    @Bean
    public DataSource multiTenantDataSource() {
        MultiTenantRoutingDataSource dataSource = new MultiTenantRoutingDataSource();
        Map<Object, Object> targetDataSources = new HashMap<>();

        clientProperties.getDb_details().forEach((key, value) -> {
            createDatabaseIfNotExists(value); // Ensure DB exists before connecting
            targetDataSources.put(value, tenantDataSourceProvider.getDataSource(value));
        });

        DataSource defaultDataSource = tenantDataSourceProvider.getDataSource(stclient_default);
        targetDataSources.put(stclient_default, defaultDataSource);

        dataSource.setTargetDataSources(targetDataSources);
        dataSource.setDefaultTargetDataSource(defaultDataSource);//Need to check why it is mandatory
        handleFlyway(targetDataSources);
        dataSource.afterPropertiesSet();

        return dataSource;
    }

    // ✅ Create Database If It Doesn't Exist
    private void createDatabaseIfNotExists(String dbName) {

        String mysqlHost = System.getenv("MYSQL_HOST") != null ? System.getenv("MYSQL_HOST") : "localhost";
        String mysqlPort = System.getenv("MYSQL_PORT") != null ? System.getenv("MYSQL_PORT") : "3306";
        String mysqlUser = System.getenv("MYSQL_USER") != null ? System.getenv("MYSQL_USER") : "root";
        String mysqlPassword = System.getenv("MYSQL_PASSWORD") != null ? System.getenv("MYSQL_PASSWORD") : "root";
        String mysqlDB = System.getenv("MYSQL_DB") != null ? System.getenv("MYSQL_DB") : "stmaster";


        String jdbcUrl = "jdbc:mysql://" + mysqlHost + ":" + mysqlPort + "/";
        try (Connection connection = DriverManager.getConnection(jdbcUrl, mysqlUser, mysqlPassword)) {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
        } catch (SQLException e) {
            throw new RuntimeException("Error creating tenant database " + dbName, e);
        }
    }

    public void handleFlyway(Map<Object, Object> targetDataSources) {
        for (Map.Entry<Object, Object> entry : targetDataSources.entrySet()) {
            String tenantId = entry.getKey().toString();
            DataSource tenantDataSource = (DataSource) entry.getValue();

            logger.info("Applying Flyway migrations for tenant: {}", tenantId);

            Flyway flyway = Flyway.configure()
                    .dataSource(tenantDataSource) // Set the tenant-specific data source
                    .locations("classpath:db/migrations/client") // Path to migration scripts
                    .baselineOnMigrate(true)
                    .validateOnMigrate(true)
                    .load();

            try {
                flyway.migrate();
                logger.info("Flyway migrations completed for tenant: {}", tenantId);
            } catch (Exception e) {
                logger.error("Flyway migration failed for tenant: {}", tenantId, e);
            }
        }
    }

}
