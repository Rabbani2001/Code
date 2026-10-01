package com.schooltech.multitenant;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

@Getter
public class MultiTenantRoutingDataSource extends AbstractRoutingDataSource {

    private static final Logger logger = LoggerFactory.getLogger(MultiTenantRoutingDataSource.class);

    @Override
    protected Object determineCurrentLookupKey() {
        String currentTenant = TenantContext.getCurrentTenant();
        logger.info("SCHOOLTECH... Resolving tenant: {}", currentTenant);
        return currentTenant;
    }
}
