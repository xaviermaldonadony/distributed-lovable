package com.example.distributed_lovable.account_service.dto.subscription;

public record PlanLimitsResponse(
        String planName,
        Integer maxTokensPerDay,
        Integer maxProjects,
        boolean unlimitedAi
) {
}
