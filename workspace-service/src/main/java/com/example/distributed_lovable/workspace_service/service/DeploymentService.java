package com.example.distributed_lovable.workspace_service.service;

import com.example.distributed_lovable.workspace_service.dto.deploy.DeployResponse;
import jakarta.annotation.Nullable;

public interface DeploymentService {
    @Nullable
    DeployResponse deploy(Long projectId);
}
