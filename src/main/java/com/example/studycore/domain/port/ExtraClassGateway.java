package com.example.studycore.domain.port;

import com.example.studycore.domain.model.ExtraClass;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExtraClassGateway {
	ExtraClass save(ExtraClass extra);
	List<ExtraClass> findByStudentIdsAndStartAtUtcBetween(java.util.Collection<java.util.UUID> studentIds, OffsetDateTime start, OffsetDateTime end);
	Optional<ExtraClass> findById(UUID id);
	void deleteById(UUID id);
}
