package com.example.distributed_lovebale.intellijence_service.service.impl;

import com.example.distributed_lovable.common_lib.security.AuthUtil;
import com.example.distributed_lovebale.intellijence_service.dto.chat.ChatResponse;
import com.example.distributed_lovebale.intellijence_service.entity.ChatMessage;
import com.example.distributed_lovebale.intellijence_service.entity.ChatSession;
import com.example.distributed_lovebale.intellijence_service.entity.ChatSessionId;
import com.example.distributed_lovebale.intellijence_service.mapper.ChatMapper;
import com.example.distributed_lovebale.intellijence_service.repository.ChatMessageRepository;
import com.example.distributed_lovebale.intellijence_service.repository.ChatSessionRepository;
import com.example.distributed_lovebale.intellijence_service.service.ChatService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatServiceImpl implements ChatService {

    ChatMessageRepository chatMessageRepository;
    ChatSessionRepository chatSessionRepository;
    ChatMapper chatMapper;
    AuthUtil authUtil;

    @Override
    public List<ChatResponse> getProjectChatHistory(Long projectId) {
        Long userId = authUtil.getCurrentUserId();
        
        ChatSession chatSession = chatSessionRepository
                .getReferenceById(new ChatSessionId(projectId, userId));

        List<ChatMessage> chatMessageList = chatMessageRepository.findByChatSession(chatSession);
        
        return chatMapper.fromListOfChatMessages(chatMessageList);
    }
}