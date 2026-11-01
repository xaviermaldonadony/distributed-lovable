package com.example.distributed_lovebale.intellijence_service.repository;

import com.example.distributed_lovebale.intellijence_service.entity.ChatEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatEventRepository extends JpaRepository<ChatEvent, Long> {
}
