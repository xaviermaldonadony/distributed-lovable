package com.example.distributed_lovebale.workspace_service.mapper;

import com.example.distributed_lovable.common_lib.enums.ProjectRole;
import com.example.distributed_lovebale.workspace_service.dto.project.ProjectResponse;
import com.example.distributed_lovebale.workspace_service.dto.project.ProjectSummaryResponse;
import com.example.distributed_lovebale.workspace_service.entity.Project;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    ProjectResponse toProjectResponse(Project project);

    ProjectSummaryResponse toProjectSummaryResponse(Project project, ProjectRole role);

    List<ProjectSummaryResponse> toListProjectSummaryResponse(List<Project> projects);
}
