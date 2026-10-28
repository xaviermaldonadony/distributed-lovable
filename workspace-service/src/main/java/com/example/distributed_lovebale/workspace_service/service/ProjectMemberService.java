package com.example.distributed_lovebale.workspace_service.service;

import com.example.distributed_lovebale.workspace_service.dto.member.InviteMemberRequest;
import com.example.distributed_lovebale.workspace_service.dto.member.MemberResponse;
import com.example.distributed_lovebale.workspace_service.dto.member.updateMemberRoleRequest;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface ProjectMemberService {
    @Nullable List<MemberResponse> getProjectMembers(Long projectId);

    MemberResponse inviteMember(Long projectId, InviteMemberRequest request);

    MemberResponse updateMemberRole(Long projectId, Long memberId, updateMemberRoleRequest request);

    void removeProjectMember(Long projectId, Long memberId);
}
