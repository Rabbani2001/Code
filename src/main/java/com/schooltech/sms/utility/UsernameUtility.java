package com.schooltech.sms.utility;

import com.schooltech.multitenant.TenantContext;
import com.schooltech.security.jwt.service.TenantService;
import com.schooltech.sms.dao.client.sequence.UsernameSequenceRepository;
import com.schooltech.sms.entity.client.sequence.UsernameSequence;
import com.schooltech.sms.entity.master.SchoolTenant;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

@Component
public class UsernameUtility {

    private final UsernameSequenceRepository sequenceRepository;
    private final TenantService tenantService;

    public UsernameUtility(UsernameSequenceRepository sequenceRepository,
                           TenantService tenantService) {
        this.sequenceRepository = sequenceRepository;
        this.tenantService = tenantService;
    }

    /**
     * PUBLIC API
     */
    @Transactional
    public String generateUsername(String role) {

        String tenantId = TenantContext.getCurrentTenant();
        SchoolTenant tenant = tenantService.getSchoolTenantByTenantId(tenantId);
        String tenantCode = tenant.getTenantCode();

        UsernameSequence sequence = sequenceRepository
                .findByTenantCodeAndRole(tenantCode, role)
                .orElseGet(() -> createInitialSequence(tenantCode, role));

        long currentValue = sequence.getNextValue();
        sequence.setNextValue(currentValue + 1);
        sequenceRepository.save(sequence);

        return formatUsername(tenantCode, role, currentValue).toLowerCase();
    }

    /**
     * Create first entry atomically
     */
    private UsernameSequence createInitialSequence(String tenantCode, String role) {
        UsernameSequence seq = new UsernameSequence();
        seq.setTenantCode(tenantCode.toLowerCase());
        seq.setRole(role);
        seq.setNextValue(1L);
        return sequenceRepository.save(seq);
    }

    /**
     * Username format logic
     */
    private String formatUsername(String tenantCode, String role, long value) {
        return tenantCode
                + role
                + value;
    }
}
