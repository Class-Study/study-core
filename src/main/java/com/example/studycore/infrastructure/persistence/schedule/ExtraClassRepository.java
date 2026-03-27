package com.example.studycore.infrastructure.persistence.schedule;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExtraClassRepository extends JpaRepository<ExtraClassEntity, UUID> {
    // Busca aulas avulsas em um intervalo de datas (LocalDate) — ordenado por date asc
    List<ExtraClassEntity> findByDateBetweenOrderByDateAsc(LocalDate start, LocalDate end);

    // Busca aulas avulsas para um conjunto de estudantes em um intervalo de datas (LocalDate)
    List<ExtraClassEntity> findByStudentIdInAndDateBetweenOrderByDateAsc(Collection<UUID> studentIds, LocalDate start, LocalDate end);
    Optional<ExtraClassEntity> findById(UUID id);
}



