package com.example.distributed_lovebale.workspace_service.service.impl;

import com.example.distributed_lovable.common_lib.dto.UserDto;
import com.example.distributed_lovable.common_lib.error.ResourceNotFoundException;
import com.example.distributed_lovable.common_lib.security.AuthUtil;
import com.example.distributed_lovebale.workspace_service.client.AccountClient;
import com.example.distributed_lovebale.workspace_service.dto.member.InviteMemberRequest;
import com.example.distributed_lovebale.workspace_service.dto.member.MemberResponse;
import com.example.distributed_lovebale.workspace_service.dto.member.updateMemberRoleRequest;
import com.example.distributed_lovebale.workspace_service.entity.Project;
import com.example.distributed_lovebale.workspace_service.entity.ProjectMember;
import com.example.distributed_lovebale.workspace_service.entity.ProjectMemberId;
import com.example.distributed_lovebale.workspace_service.mapper.ProjectMemberMapper;
import com.example.distributed_lovebale.workspace_service.repository.ProjectMemberRepository;
import com.example.distributed_lovebale.workspace_service.repository.ProjectRepository;
import com.example.distributed_lovebale.workspace_service.service.ProjectMemberService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class ProjectMemberServiceImpl implements ProjectMemberService {

    ProjectMemberRepository projectMemberRepository;
    ProjectRepository projectRepository;
    ProjectMemberMapper projectMemberMapper;
    AuthUtil authUtil;
    AccountClient accountClient;

    @Override
    @PreAuthorize("@security.canViewMembers(#projectId)")
    public @Nullable List<MemberResponse> getProjectMembers(Long projectId) {
        return projectMemberRepository
                .findByIdProjectId(projectId)
                .stream()
                .map(projectMemberMapper::toProjectMemberResponseFromMember)
                .toList();
    }

    @Override
    @PreAuthorize("@security.canManageMembers(#projectId)")
    public MemberResponse inviteMember(Long projectId, InviteMemberRequest request) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessbileProject(projectId, userId);

        UserDto invitee = accountClient.getUserByEmail(request.username()).orElseThrow(
                () -> new ResourceNotFoundException("User", request.username())
        );


        if(invitee.id().equals(userId)){
            throw new RuntimeException("Cannot invite yourself");
        }

        ProjectMemberId projectMemberId  = new ProjectMemberId(projectId, invitee.id());

        if(projectMemberRepository.existsById(projectMemberId)){
            throw new RuntimeException("Already invited");
        }

        ProjectMember projectMember = ProjectMember.builder()
                .id(projectMemberId)
                .project(project)
                .projectRole(request.role())
                .invitedAt(Instant.now())
                .build();

        projectMemberRepository.save(projectMember);

        return projectMemberMapper.toProjectMemberResponseFromMember(projectMember);
    }

    @Override
    @PreAuthorize("@security.canManageMembers(#projectId)")
    public MemberResponse updateMemberRole(Long projectId, Long memberId, updateMemberRoleRequest request) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessbileProject(projectId, userId);

        ProjectMemberId projectMemberId  = new ProjectMemberId(projectId, memberId);
        ProjectMember projectMember = projectMemberRepository.findById(projectMemberId).orElseThrow();

        projectMember.setProjectRole(request.role());

        projectMemberRepository.save(projectMember);

        return projectMemberMapper.toProjectMemberResponseFromMember(projectMember);
    }

    @Override
    @PreAuthorize("@security.canManageMembers(#projectId)")
    public void removeProjectMember(Long projectId, Long memberId) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessbileProject(projectId, userId);
        ProjectMemberId projectMemberId  = new ProjectMemberId(projectId, memberId);

        if(!projectMemberRepository.existsById(projectMemberId)){
            throw new RuntimeException("Member not found in project");
        }

        projectMemberRepository.deleteById(projectMemberId);
    }

    private Project getAccessbileProject(Long projectId, Long userId) {
        return projectRepository.findAccessibleProjectById(projectId, userId).orElseThrow();
    }
}
