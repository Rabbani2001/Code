package com.schooltech.sms.dao.master;

import com.schooltech.sms.entity.master.SchoolTenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TenantRepository extends JpaRepository<SchoolTenant, String> {

    //@Query("SELECT e FROM User e WHERE e.username= :usernameOrEmail OR e.email=: usernameOrEmail OR e.phoneNo=: usernameOrEmail")
    //public Optional<User> findByUsernameOrEmail(String usernameOrEmail);

    public Optional<SchoolTenant> findByTenantCode(String tenantCode);

    public Optional<SchoolTenant> findByTenantId(String tenantId);

    //public Optional<User> findByUserId(String id);
    @Query("SELECT s FROM SchoolTenant s WHERE s.isActive = true")
    List<SchoolTenant> findAllActiveTenants();

}
