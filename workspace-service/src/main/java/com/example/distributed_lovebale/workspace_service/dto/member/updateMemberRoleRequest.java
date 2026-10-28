package com.example.distributed_lovebale.workspace_service.dto.member;

import com.example.distributed_lovable.common_lib.enums.ProjectRole;
import jakarta.validation.constraints.NotNull;

public record updateMemberRoleRequest(
        @NotNull
        ProjectRole role
) {
}
