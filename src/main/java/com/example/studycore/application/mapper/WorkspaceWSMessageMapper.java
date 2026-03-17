package com.example.studycore.application.mapper;

import org.mapstruct.Mapper;

/**
 * Mapper para converter entre WorkspaceWSMessageDTO e modelos de domínio.
 *
 * Reservado para futuras conversões:
 * - WorkspaceOperation toDomain(WorkspaceWSMessageDTO dto);
 * - WorkspaceWSMessageDTO toDTO(WorkspaceOperation operation);
 */
@Mapper(componentModel = "spring")
public interface WorkspaceWSMessageMapper {
    // Futuras conversões entre WSMessageDTO e domain model
}

