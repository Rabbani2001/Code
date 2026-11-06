package com.schooltech.sms.utility;

import com.schooltech.multitenant.TenantContext;
import com.schooltech.security.jwt.service.TenantService;
import com.schooltech.sms.dao.client.sequence.UsernameSequenceRepository;
import com.schooltech.sms.dao.client.student.StudentRepository;
import com.schooltech.sms.dao.client.tenant_data.TenantDataRepository;
import com.schooltech.sms.entity.client.sequence.UsernameSequence;
import com.schooltech.sms.entity.client.tenant_data.TenantData;
import com.schooltech.sms.entity.master.SchoolTenant;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.stereotype.Component;

@Component
public class UsernameUtility {

    private final TenantDataRepository tenantDataRepository;
    private final StudentRepository studentRepository;
    private final UsernameSequenceRepository sequenceRepository;
    private final TenantService tenantService;

    public UsernameUtility(TenantDataRepository tenantDataRepository, StudentRepository studentRepository, UsernameSequenceRepository sequenceRepository,
                           TenantService tenantService) {
        this.tenantDataRepository = tenantDataRepository;
        this.studentRepository = studentRepository;
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


    @Transactional(transactionManager = "clientTransactionManager")
    public String getAdmissionNoSequence(String role) {

        // Fetch custom admission pattern from tenant_data
        String admissionPattern = getAdmissionPattern();
        String tenantId = TenantContext.getCurrentTenant();
        SchoolTenant tenant = tenantService.getSchoolTenantByTenantId(tenantId);
        if (admissionPattern == null) {
            admissionPattern= tenant.getTenantCode();
        }
        UsernameSequence sequence = sequenceRepository
                .findByTenantCodeAndRole(tenant.getTenantCode(), role)
                .orElseGet(() -> createInitialAdmissionNoSequence(tenant.getTenantCode(), role));

        long nextValue = sequence.getNextValue();

        // Fetch custom admission pattern from tenant_data
        String admissionNo = formatAdmissionNoWithPattern(admissionPattern, nextValue);

        // Skip admission numbers already assigned to students
        while (studentRepository.findByAdmissionNo(admissionNo).isPresent()) {
            nextValue++;
            admissionNo = formatAdmissionNoWithPattern( admissionPattern, nextValue);
        }

        // Update counter only when existing numbers were skipped
        if (nextValue != sequence.getNextValue()) {
            sequence.setNextValue(nextValue);
            sequenceRepository.save(sequence);
        }

        return admissionNo;
    }

    private String getAdmissionPattern() {
        TenantData tenantData = tenantDataRepository
                .findAll()
                .stream()
                .findFirst()
                .orElse(null);

        if (tenantData == null || tenantData.getPattern() == null) {
            return null;
        }

        return tenantData.getPattern().get("admissionNo");
    }

    private String formatAdmissionNoWithPattern(
            String admissionPattern,
            long nextValue) {

        return (admissionPattern + nextValue).toLowerCase();
    }


    @Transactional(transactionManager = "clientTransactionManager", propagation = Propagation.MANDATORY)
    public void incrementAdmissionNoSequence(String role) {

        String tenantId = TenantContext.getCurrentTenant();

        SchoolTenant tenant =
                tenantService.getSchoolTenantByTenantId(tenantId);

        String tenantCode = tenant.getTenantCode();

        UsernameSequence sequence = sequenceRepository
                .findByTenantCodeAndRole(tenantCode, role)
                .orElseGet(() ->
                        createInitialAdmissionNoSequence(tenantCode, role)
                );

        sequence.setNextValue(sequence.getNextValue() + 1);
        sequenceRepository.save(sequence);
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

    private UsernameSequence createInitialAdmissionNoSequence(String tenantCode, String role) {
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
    private String formatAdmissionNo(String tenantCode, long value) {
        return tenantCode
                + value;
    }

}
