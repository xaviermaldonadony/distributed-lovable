package com.example.distributed_lovebale.intellijence_service.service;

import com.example.distributed_lovebale.intellijence_service.dto.chat.StreamResponse;
import reactor.core.publisher.Flux;

public interface AiGenerationService {
    Flux<StreamResponse> streamResponse(String message, Long projectId);
}
