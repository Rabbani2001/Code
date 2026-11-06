package com.schooltech.sms.utility;

import com.schooltech.multitenant.TenantContext;
import com.schooltech.security.jwt.service.TenantService;
import com.schooltech.sms.constant.AppConstant;
import com.schooltech.sms.dao.client.sequence.SessionSequenceRepository;
import com.schooltech.sms.entity.client.sequence.SessionSequence;
import com.schooltech.sms.entity.master.SchoolTenant;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class SessionSequenceUtility {

    private final SessionSequenceRepository sequenceRepository;
    private final TenantService tenantService;

    public SessionSequenceUtility(SessionSequenceRepository sequenceRepository,
                                  TenantService tenantService) {
        this.sequenceRepository = sequenceRepository;
        this.tenantService = tenantService;
    }

    @Transactional
    public String generateSessionSequence(String type) {
        String tenantId = TenantContext.getCurrentTenant();
        SchoolTenant tenant = tenantService.getSchoolTenantByTenantId(tenantId);
        String tenantCode = tenant.getTenantCode();

        String session = determineCurrentSession();
        String sessionKey = formatSessionKey(session);

        SessionSequence sequence = sequenceRepository
                .findByTenantCodeAndSessionKeyAndType(tenantCode, sessionKey, type)
                .orElseGet(() -> createInitialSequence(tenantCode, sessionKey, type));

        long currentValue = sequence.getNextValue();
        sequence.setNextValue(currentValue + 1);
        sequenceRepository.save(sequence);

        return formatResultSequence(sessionKey, tenantCode, currentValue, type).toLowerCase();
    }

    private String determineCurrentSession() {
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int nextYear = today.getMonthValue() >= 4 ? currentYear + 1 : currentYear;
        int startYear = nextYear - 1;

        return startYear + "-" + nextYear;
    }

    private String formatSessionKey(String session) {
        String[] years = session.split("-");
        return years[0].substring(2) + years[1].substring(2);
    }

    private SessionSequence createInitialSequence(String tenantCode, String sessionKey, String type) {
        SessionSequence seq = new SessionSequence();
        seq.setTenantCode(tenantCode);
        seq.setSessionKey(sessionKey);
        seq.setNextValue(1L);
        seq.setType(type);
        return sequenceRepository.save(seq);
    }

    private String formatResultSequence(String sessionKey, String tenantCode, long value, String type) {
        if (type.equalsIgnoreCase(AppConstant.FEE_TYPE_SEQUENCE) || type.equalsIgnoreCase(AppConstant.BUSFEE_TYPE_SEQUENCE)) {
            return sessionKey + "-" + type + "-" + value;
        } else if (type.equalsIgnoreCase(AppConstant.RESULT_TYPE_SEQUENCE)) {
            return sessionKey + "-" + tenantCode + "-" + value;
        } else if (type.equalsIgnoreCase(AppConstant.VISTOR_TYPE_SEQUENCE)) {
            return sessionKey + type + value;
        }
        throw new RuntimeException("Invalid sequence type: " + type);
    }
}