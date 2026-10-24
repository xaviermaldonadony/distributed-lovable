package com.example.distributed_lovebale.account_service.dto.subscription;

public record UsageTodayResponse(
        int tokenUsed,
        int tokensLimit,
        int previewsRunning,
        int previewLimit
) {
}
