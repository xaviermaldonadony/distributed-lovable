package com.example.distributed_lovebale.account_service.service;


import com.example.distributed_lovable.common_lib.dto.PlanDto;
import com.example.distributed_lovable.common_lib.enums.SubscriptionStatus;
import com.example.distributed_lovebale.account_service.dto.subscription.SubscriptionResponse;

import java.time.Instant;

public interface SubscriptionService {
    SubscriptionResponse getCurrentSubscription();

    void activateSubscription(Long userId, Long planId,String subscriptionId, String customerId);

    void updateSubscription(String gatewaySubscriptionId, SubscriptionStatus status, Instant periosStart, Instant periosEnd, Boolean cancelAtPeriodEnd, Long planId);

    void cancelSubscription(String gatewaySubscriptionId);

    void renewSubscriptionPeriod(String subId, Instant periodStart, Instant periodEnd);

    void markSubscriptonPastDue(String gatewaySubscriptionId);

    PlanDto getCurrentSubscribedPlanByUser();
}
