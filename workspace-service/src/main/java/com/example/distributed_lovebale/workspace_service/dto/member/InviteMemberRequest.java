package com.example.distributed_lovebale.workspace_service.dto.member;

import com.example.distributed_lovable.common_lib.enums.ProjectRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record InviteMemberRequest(
        @Email @NotNull
        String username,

        @NotNull
        ProjectRole role
) {
}
