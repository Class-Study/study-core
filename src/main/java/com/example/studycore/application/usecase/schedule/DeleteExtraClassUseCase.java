package com.example.studycore.application.usecase.schedule;

import com.example.studycore.domain.port.ExtraClassGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteExtraClassUseCase {

    private final ExtraClassGateway extraClassGateway;

    public void execute(UUID id) {
        if (id == null) throw new IllegalArgumentException("id cannot be null");
        extraClassGateway.deleteById(id);
    }
}
