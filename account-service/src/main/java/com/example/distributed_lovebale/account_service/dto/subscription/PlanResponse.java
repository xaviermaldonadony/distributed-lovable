package com.example.distributed_lovebale.account_service.dto.subscription;

public record PlanResponse(
       Long id,
       String name,
       Integer maxProjects,
       Integer maxTokensPerDay,
       Boolean unlimitedAi,
       String price
) {
}
