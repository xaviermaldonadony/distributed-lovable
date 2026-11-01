package com.example.distributed_lovebale.workspace_service.service;


import com.example.distributed_lovable.common_lib.dto.FileTreeDto;

public interface ProjectFileService {
    FileTreeDto getFileTree(Long projectId);

    String getFileContent (Long projectId, String path);

    void saveFile(Long projectId, String path, String content);
}
