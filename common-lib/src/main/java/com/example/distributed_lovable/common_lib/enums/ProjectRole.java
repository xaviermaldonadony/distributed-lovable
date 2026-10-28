package com.example.distributed_lovable.common_lib.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import static com.example.distributed_lovable.common_lib.enums.ProjectPermission.DELETE;
import static com.example.distributed_lovable.common_lib.enums.ProjectPermission.EDIT;
import static com.example.distributed_lovable.common_lib.enums.ProjectPermission.MANAGE_MEMBERS;
import static com.example.distributed_lovable.common_lib.enums.ProjectPermission.VIEW;
import static com.example.distributed_lovable.common_lib.enums.ProjectPermission.VIEW_MEMBERS;


@RequiredArgsConstructor
@Getter
public enum ProjectRole {
    EDITOR(EDIT, DELETE, VIEW, VIEW_MEMBERS),
    VIEWER(VIEW, VIEW_MEMBERS),
    OWNER(VIEW, EDIT, DELETE, MANAGE_MEMBERS, VIEW_MEMBERS);

    ProjectRole(ProjectPermission... permissions){
       this.permissions = Set.of(permissions);
    }

    private final Set<ProjectPermission> permissions;
}
