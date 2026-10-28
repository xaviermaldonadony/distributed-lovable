package com.example.distributed_lovebale.account_service.dto.subscription;

import com.example.distributed_lovable.common_lib.dto.PlanDto;

import java.time.Instant;

public record SubscriptionResponse(
    PlanDto plan,
    String status,
    Instant currentPeriodEnd,
    Long tokensUsedThisCycle
) {
}
