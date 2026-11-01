package com.example.distributed_lovebale.intellijence_service.service;

public interface UsageService {
    void recordTokenUsage(Long userId, int actualTokens);

    void checkDailyTokensUsage();
}
