package com.example.distributed_lovebale.account_service.dto.subscription;

import java.time.Instant;

public record SubscriptionResponse(
    PlanResponse plan,
    String status,
    Instant currentPeriodEnd,
    Long tokensUsedThisCycle
) {
}
