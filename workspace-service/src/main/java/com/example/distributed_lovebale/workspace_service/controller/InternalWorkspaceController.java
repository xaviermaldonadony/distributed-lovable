package com.example.distributed_lovebale.workspace_service.controller;

import com.example.distributed_lovable.common_lib.dto.FileTreeDto;
import com.example.distributed_lovable.common_lib.enums.ProjectPermission;
import com.example.distributed_lovebale.workspace_service.service.ProjectFileService;
import com.example.distributed_lovebale.workspace_service.service.ProjectService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@RequestMapping("/internal/v1/")
@RestController
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class InternalWorkspaceController {

    ProjectService projectService;
    ProjectFileService projectFileService;

    @GetMapping("/projects/{projectId}/files/tree")
    public FileTreeDto getFileTree(@PathVariable Long projectId) {
        return projectFileService.getFileTree(projectId);
    }

    @GetMapping("/projects/{projectId}/files/content")
    public String getFileContent(@PathVariable Long projectId, @RequestParam String path) {
        return projectFileService.getFileContent(projectId, path);
    }

    @GetMapping("/projects/{projectId}/permissions/check")
    public boolean checkProjectPermission(
            @PathVariable Long projectId,
            @RequestParam ProjectPermission permission) {
        return projectService.hasPermission(projectId, permission);
    }
}