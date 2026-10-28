package com.example.distributed_lovebale.workspace_service.service.impl;

import com.example.distributed_lovable.common_lib.dto.PlanDto;
import com.example.distributed_lovable.common_lib.enums.ProjectRole;
import com.example.distributed_lovable.common_lib.error.BadRequestException;
import com.example.distributed_lovable.common_lib.error.ResourceNotFoundException;
import com.example.distributed_lovable.common_lib.security.AuthUtil;
import com.example.distributed_lovebale.workspace_service.client.AccountClient;
import com.example.distributed_lovebale.workspace_service.dto.project.ProjectRequest;
import com.example.distributed_lovebale.workspace_service.dto.project.ProjectResponse;
import com.example.distributed_lovebale.workspace_service.dto.project.ProjectSummaryResponse;
import com.example.distributed_lovebale.workspace_service.entity.Project;
import com.example.distributed_lovebale.workspace_service.entity.ProjectMember;
import com.example.distributed_lovebale.workspace_service.entity.ProjectMemberId;
import com.example.distributed_lovebale.workspace_service.mapper.ProjectMapper;
import com.example.distributed_lovebale.workspace_service.repository.ProjectMemberRepository;
import com.example.distributed_lovebale.workspace_service.repository.ProjectRepository;
import com.example.distributed_lovebale.workspace_service.service.ProjectService;
import com.example.distributed_lovebale.workspace_service.service.ProjectTemplateService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class ProjectServiceImpl implements ProjectService {

    ProjectRepository projectRepository;
    ProjectMapper projectMapper;
    ProjectMemberRepository projectMemberRepository;
    ProjectTemplateService projectTemplateService;
    AuthUtil authUtil;
    AccountClient accountClient;

    @Override
    public ProjectResponse createProject(ProjectRequest request) {

        if (!canCreateProject())
            throw new BadRequestException("User cannot crate a new Project with ");

        Long ownerId = authUtil.getCurrentUserId();

        Project project = Project.builder()
                .name(request.name())
                .isPublic(false)
                .build();

        project = projectRepository.save(project);

        ProjectMemberId projectMemberId = new ProjectMemberId(project.getId(), ownerId);
        ProjectMember projectMember = ProjectMember.builder()
                .id(projectMemberId)
                .projectRole(ProjectRole.OWNER)
                .acceptedAt(Instant.now())
                .invitedAt(Instant.now())
                .project(project)
                .build();

        projectMember = projectMemberRepository.save(projectMember);
        projectTemplateService.initializeProjectFromTemplate(project.getId());

        return projectMapper.toProjectResponse(project);
    }

    @Override
    public List<ProjectSummaryResponse> getUserProjects() {
        Long userId = authUtil.getCurrentUserId();
        var projectsWithRoles = projectRepository.findAllAccessibleByUser(userId);

        return projectsWithRoles.stream()
                .map(p -> projectMapper.toProjectSummaryResponse(p.getProject(), p.getRole()))
                .toList();
    }

    @Override
    @PreAuthorize("@security.canViewProject(#projectId)")
    public ProjectSummaryResponse getUserProjectById(Long projectId) {
        Long userId = authUtil.getCurrentUserId();
        ProjectRepository.ProjectWithRole projectWithRole = projectRepository
                .findAccessibleProjectByIdWithRole(projectId, userId)
                .orElseThrow(() -> new BadRequestException("Project Not Found"));

        return projectMapper
                .toProjectSummaryResponse(projectWithRole.getProject(), projectWithRole.getRole());
    }

    @Override
    public ProjectResponse updateProject(Long id, ProjectRequest request) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessbileProjectById(id, userId);
        project.setName(request.name());
        project = projectRepository.save(project);

        return projectMapper.toProjectResponse(project);
    }

    @Override
    public void softDelete(Long id) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessbileProjectById(id, userId);

        project.setDeletedAt(Instant.now());
        projectRepository.save(project);
    }

    private  Project getAccessbileProjectById(Long projectId, Long userId){

        return projectRepository.findAccessibleProjectById(projectId, userId).orElseThrow(
                () -> new ResourceNotFoundException("Project not found", projectId.toString())
        );
    }

    ///  INTERNAL FUNCTIONS

    public Project getAccessibleProjectById(Long projectId, Long userId) {
        return projectRepository.findAccessibleProjectById(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId.toString()));
    }

    private boolean canCreateProject() {
        Long userId = authUtil.getCurrentUserId();

        if (userId == null) {
            return false;
        }

        PlanDto plan = accountClient.getCurrentSubscribedPlanByUser();

        int maxAllowed = plan.maxProjects();
        int ownedCount = projectMemberRepository.countProjectOwnedByUser(userId);

        return ownedCount < maxAllowed;
    }

}
