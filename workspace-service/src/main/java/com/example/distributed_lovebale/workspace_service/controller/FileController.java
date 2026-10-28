package com.example.distributed_lovebale.workspace_service.controller;

import com.example.distributed_lovebale.workspace_service.dto.project.FileContentResponse;
import com.example.distributed_lovebale.workspace_service.dto.project.FileTreeResponse;
import com.example.distributed_lovebale.workspace_service.service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/{projectId}/files")
public class FileController {
    private final ProjectFileService projectFileService;

    @GetMapping
    public ResponseEntity<FileTreeResponse> getFileTree(@PathVariable Long projectId) {
       Long userId = 1L;
       return ResponseEntity.ok(projectFileService.getFileTree(projectId));
    }

    @GetMapping("/content") // /src/hooks/AppHok.jsx
    public ResponseEntity<FileContentResponse> getFile(
            @PathVariable Long projectId,
            @RequestParam String path
    ) {
        return ResponseEntity.ok(projectFileService.getFileContent(projectId, path));
    }
}