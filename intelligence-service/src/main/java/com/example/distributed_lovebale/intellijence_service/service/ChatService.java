package com.example.distributed_lovebale.intellijence_service.service;

import com.example.distributed_lovebale.intellijence_service.dto.chat.ChatResponse;

import java.util.List;

public interface ChatService {
    List<ChatResponse> getProjectChatHistory(Long projectId);
}
