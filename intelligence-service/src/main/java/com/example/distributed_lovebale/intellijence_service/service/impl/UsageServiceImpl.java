package com.example.distributed_lovebale.intellijence_service.service.impl;

import com.example.distributed_lovable.common_lib.dto.PlanDto;
import com.example.distributed_lovable.common_lib.security.AuthUtil;
import com.example.distributed_lovebale.intellijence_service.client.AccountClient;
import com.example.distributed_lovebale.intellijence_service.entity.UsageLog;
import com.example.distributed_lovebale.intellijence_service.repository.UsageLogRepository;
import com.example.distributed_lovebale.intellijence_service.service.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RequiredArgsConstructor
@Service
public class UsageServiceImpl implements UsageService {
    private final UsageLogRepository usageLogRepository;
    private final AuthUtil authUtil;
    private final AccountClient accountClient;

    @Override
    public void recordTokenUsage(Long userId, int actualTokens) {
        LocalDate today = LocalDate.now();

        UsageLog todayLog = usageLogRepository.findByUserIdAndDate(userId, today)
                .orElseGet(() -> createNewDailyLog(userId, today));

        todayLog.setTokensUsed(todayLog.getTokensUsed() + actualTokens);
        usageLogRepository.save(todayLog);
    }

    @Override
    public void checkDailyTokensUsage() {
        Long userId = authUtil.getCurrentUserId();
        PlanDto plan = accountClient.getCurrentSubscribedPlanByUser();

        LocalDate today = LocalDate.now();

        UsageLog todayLog = usageLogRepository.findByUserIdAndDate(userId, today)
                .orElseGet(() -> createNewDailyLog(userId, today));

        if (plan.unlimitedAi()) return;

        int currentUsage = todayLog.getTokensUsed();
        int limit = plan.maxTokensPerDay();

        if (currentUsage >= limit) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Daily token limit exceeded");
        }
    }

    private UsageLog createNewDailyLog(Long userId, LocalDate today) {
        return usageLogRepository.save(UsageLog.builder()
                .userId(userId)
                .date(today)
                .tokensUsed(0)
                .build());
    }
}
