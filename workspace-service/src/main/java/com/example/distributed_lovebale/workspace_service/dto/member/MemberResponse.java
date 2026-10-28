package com.example.distributed_lovebale.workspace_service.dto.member;

import com.example.distributed_lovable.common_lib.enums.ProjectRole;

import java.time.Instant;

public record MemberResponse(
        Long userId,
        String username ,
        String name,
        ProjectRole role,
        Instant invitedAt
) {
}
