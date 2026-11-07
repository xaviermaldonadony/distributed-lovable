package com.example.distributed_lovable.intelligence_service.mapper;

import com.example.distributed_lovable.intelligence_service.entity.ChatMessage;
import com.example.distributed_lovable.intelligence_service.dto.chat.ChatResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatMapper {
//    ChatEventResponse toChatEventResponse(ChatEvent chatEvent);

    List<ChatResponse> fromListOfChatMessages(List<ChatMessage> chatMessages);
}
