package com.schooltech.multitenant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Service
public class TenantDataSourceProvider {
    private final Map<String, DataSource> tenantDataSources = new HashMap<>();
    @Autowired
    private DataSource masterDataSource;

    /*
    This method is providing DataSource from master table "stclient_db_config"
     */
    public DataSource getDataSource(String tenantId) {
        if (!tenantDataSources.containsKey(tenantId)) { //loading only when it is not found in the map
            loadTenantDataSource(tenantId);
        }
        return tenantDataSources.get(tenantId);
    }

    private void loadTenantDataSource(String tenantId) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(masterDataSource);
        String query = "SELECT db_url, db_username, db_password, db_driver FROM stclient_db_config WHERE tenant_id = ?";
        Map<String, Object> dbConfig = jdbcTemplate.queryForMap(query, tenantId);

        DataSource dataSource = createDataSource(
                (String) dbConfig.get("db_url"),
                (String) dbConfig.get("db_username"),
                (String) dbConfig.get("db_password"),
                (String) dbConfig.get("db_driver")
        );

        tenantDataSources.put(tenantId, dataSource);
    }

    private DataSource createDataSource(String url, String username, String password, String driver) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driver);
        return dataSource;
    }

//    private DataSource getDefaultDataSource() {
//        // Replace with your default tenant's database configuration
//        return createDataSource(
//                "jdbc:mysql://localhost:3306/academic_mgmt",
//                "springstudent",
//                "springstudent",
//                "com.mysql.cj.jdbc.Driver"
//        );
//    }
}
