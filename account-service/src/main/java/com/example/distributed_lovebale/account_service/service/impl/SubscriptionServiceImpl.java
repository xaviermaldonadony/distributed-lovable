package com.example.distributed_lovebale.account_service.service.impl;

import com.example.distributed_lovable.common_lib.dto.PlanDto;
import com.example.distributed_lovable.common_lib.enums.SubscriptionStatus;
import com.example.distributed_lovable.common_lib.error.ResourceNotFoundException;
import com.example.distributed_lovable.common_lib.security.AuthUtil;
import com.example.distributed_lovebale.account_service.dto.subscription.SubscriptionResponse;
import com.example.distributed_lovebale.account_service.entity.Plan;
import com.example.distributed_lovebale.account_service.entity.Subscription;
import com.example.distributed_lovebale.account_service.entity.User;
import com.example.distributed_lovebale.account_service.mapper.SubscriptionMapper;
import com.example.distributed_lovebale.account_service.repository.PlanRepository;
import com.example.distributed_lovebale.account_service.repository.SubscriptionRepository;
import com.example.distributed_lovebale.account_service.repository.UserRepository;
import com.example.distributed_lovebale.account_service.service.SubscriptionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final AuthUtil authUtil;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;

    private final Integer FREE_TIER_PROJECTS_ALLOWED = 100;

//    @Override
//    public boolean canCreateNewProject() {
//        Long userId = authUtil.getCurrentUserId();
//        SubscriptionResponse currentSubscription = getCurrentSubscription();
//
//        int countOfOwnedProjects = projectMemberRepository.countProjectOwnedByUser(userId);
//
//        if(currentSubscription.plan() == null){
//            return  countOfOwnedProjects < FREE_TIER_PROJECTS_ALLOWED;
//        }
//
//        return  countOfOwnedProjects < currentSubscription.plan().maxProjects();
//    }

    @Override
    public SubscriptionResponse getCurrentSubscription() {
        Long userId =  authUtil.getCurrentUserId();

        var currentSubscription = subscriptionRepository.findByUserIdAndStatusIn(userId, Set.of(
                SubscriptionStatus.ACTIVE, SubscriptionStatus.PAST_DUE,
                SubscriptionStatus.TRIALING)).orElse(new Subscription());

        return subscriptionMapper.toSubscriptionResponse(currentSubscription);
    }

    @Override
    public void activateSubscription(Long userId, Long planId, String subscriptionId ,String customerId) {
        boolean exists = subscriptionRepository.existsByStripeSubscriptionId(subscriptionId);
        if(exists){
            return;
        }

        User user = getUser(userId);
        Plan plan = getPlan(planId);

        Subscription subscription = Subscription.builder()
                .user(user)
                .plan(plan)
                .stripeSubscriptionId(subscriptionId)
                .status(SubscriptionStatus.INCOMPLETE)
                .build();

        subscriptionRepository.save(subscription);
    }

    @Override
    @Transactional
    public void updateSubscription(String gatewaySubscriptionId, SubscriptionStatus status, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, Long planId) {
        Subscription subscription = getSubscription(gatewaySubscriptionId);

        boolean subscriptionUpdated = false;

        if (status != null && status != subscription.getStatus()) {
            subscription.setStatus(status);
            subscriptionUpdated = true;
        }
        if (periodStart != null && !periodStart.equals(subscription.getCurrentPeriodStart())) {
            subscription.setCurrentPeriodStart(periodStart);
            subscriptionUpdated = true;
        }
        if (periodEnd != null && !periodEnd.equals(subscription.getCurrentPeriodEnd())) {
            subscription.setCurrentPeriodEnd(periodEnd);
            subscriptionUpdated = true;
        }
        if (cancelAtPeriodEnd != null && !cancelAtPeriodEnd.equals(subscription.getCancelAtPeriodEnd())) {
            subscription.setCancelAtPeriodEnd(cancelAtPeriodEnd);
            subscriptionUpdated = true;
        }
        if (planId != null && !planId.equals(subscription.getPlan().getId())) {
            Plan newPlan = getPlan(planId);
            subscription.setPlan(newPlan);
            subscriptionUpdated = true;
        }

        if (subscriptionUpdated) {
            log.debug("Subscription updated: {}", subscription);
            subscriptionRepository.save(subscription);
        }
    }

    @Override
    public void cancelSubscription(String subscriptionId) {
        Subscription subscription = getSubscription(subscriptionId);
        subscription.setStatus(SubscriptionStatus.CANCELED);
        subscriptionRepository.save(subscription);
    }

    @Override
    public void renewSubscriptionPeriod(String gatewaySubscriptionId, Instant periodStart, Instant periodEnd) {
        Subscription subscription = getSubscription(gatewaySubscriptionId);

        Instant newStart = periodStart != null ? periodStart: subscription.getCurrentPeriodEnd();

        subscription.setCurrentPeriodStart(newStart);
        subscription.setCurrentPeriodEnd(periodEnd);

        if(subscription.getStatus() == SubscriptionStatus.PAST_DUE || subscription.getStatus() == SubscriptionStatus.INCOMPLETE){
            subscription.setStatus(SubscriptionStatus.ACTIVE);
        }

        subscriptionRepository.save(subscription);
    }


    @Override
    public void markSubscriptonPastDue(String gatewaySubscriptionId) {
        Subscription subscription = getSubscription(gatewaySubscriptionId);

        if(subscription.getStatus() == SubscriptionStatus.PAST_DUE){
           log.debug("Subscription is already past due, gatewaySubscriptionId: {}", gatewaySubscriptionId);
           return;
        }
        subscription.setStatus(SubscriptionStatus.PAST_DUE);
        subscriptionRepository.save(subscription);

        // Notify via email
    }

    @Override
    public PlanDto getCurrentSubscribedPlanByUser() {
        SubscriptionResponse subscriptionResponse = getCurrentSubscription();
        return subscriptionResponse.plan();
    }

    // utility methods
    private User getUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));
    }

    private Plan getPlan(Long planId){
        return planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("User", planId.toString()));
    }

    private Subscription getSubscription(String gatewaySubscriptionId) {
        return   subscriptionRepository.findByStripeSubscriptionId(gatewaySubscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", gatewaySubscriptionId));
    }
}
