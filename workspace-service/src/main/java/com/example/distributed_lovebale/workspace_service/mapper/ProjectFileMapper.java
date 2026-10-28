package com.example.distributed_lovebale.workspace_service.mapper;

import com.example.distributed_lovebale.workspace_service.dto.project.FileNode;
import com.example.distributed_lovebale.workspace_service.entity.ProjectFile;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectFileMapper {

    List<FileNode> toListOfFileNode(List<ProjectFile> projectFiles);
}
