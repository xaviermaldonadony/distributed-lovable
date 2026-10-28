package com.example.distributed_lovebale.account_service.mapper;

import com.example.distributed_lovable.common_lib.dto.PlanDto;
import com.example.distributed_lovebale.account_service.dto.subscription.SubscriptionResponse;
import com.example.distributed_lovebale.account_service.entity.Plan;
import com.example.distributed_lovebale.account_service.entity.Subscription;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    SubscriptionResponse toSubscriptionResponse(Subscription subscription);

    PlanDto toPlanResponse(Plan plan);
}
