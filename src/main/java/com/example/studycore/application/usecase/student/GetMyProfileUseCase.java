package com.example.studycore.application.usecase.student;

import com.example.studycore.application.usecase.student.output.GetMyProfileOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.enums.UserStatus;
import com.example.studycore.domain.port.LevelProfileGateway;
import com.example.studycore.domain.port.StudentGateway;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetMyProfileUseCase {

    private final StudentGateway studentGateway;
    private final LevelProfileGateway levelProfileGateway;

    public GetMyProfileOutput execute(UUID studentId) {
        final var student = studentGateway.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Aluno não encontrado."));

        // Validar se o aluno está bloqueado
        if (UserStatus.BLOCKED == student.getStatus()) {
            throw new BusinessException("Sua conta foi bloqueada. Entre em contato com o professor.");
        }

        // Buscar o perfil de nível
        GetMyProfileOutput.LevelProfileMinimal levelProfileMinimal = null;
        if (student.getLevelProfileId() != null) {
            final var levelProfile = levelProfileGateway.findById(student.getLevelProfileId())
                    .orElseThrow(() -> new NotFoundException("Perfil de nível não encontrado."));

            levelProfileMinimal = new GetMyProfileOutput.LevelProfileMinimal(
                    levelProfile.getId(),
                    levelProfile.getName(),
                    levelProfile.getCode()
            );
        }

        return new GetMyProfileOutput(
                student.getId(),
                student.getName(),
                student.getEmail(),
                student.getAvatarUrl(),
                student.getStatus().toString(),
                levelProfileMinimal,
                student.getClassDays(),
                student.getClassTime(),
                student.getClassDuration(),
                student.getClassRate(),
                student.getMeetPlatform(),
                student.getMeetLink(),
                student.getStartDate(),
                student.getCreatedAt()
        );
    }
}


