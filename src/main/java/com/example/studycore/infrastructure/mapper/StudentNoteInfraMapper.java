package com.example.studycore.infrastructure.mapper;

import com.example.studycore.application.usecase.studentnote.input.CreateStudentNoteInput;
import com.example.studycore.application.usecase.studentnote.output.StudentNoteOutput;
import com.example.studycore.domain.model.StudentNote;
import com.example.studycore.infrastructure.api.controllers.studentnote.request.CreateStudentNoteRequest;
import com.example.studycore.infrastructure.api.controllers.studentnote.response.StudentNoteResponse;
import com.example.studycore.infrastructure.persistence.studentnote.StudentNoteEntity;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StudentNoteInfraMapper {

    StudentNoteInfraMapper INSTANCE = Mappers.getMapper(StudentNoteInfraMapper.class);

    default CreateStudentNoteInput toCreateStudentNoteInput(
            UUID teacherId,
            UUID studentId,
            UUID authorId,
            CreateStudentNoteRequest request
    ) {
        return new CreateStudentNoteInput(
                teacherId,
                studentId,
                authorId,
                request.type(),
                request.content()
        );
    }

    default StudentNote fromEntity(StudentNoteEntity entity) {
        if (entity == null) {
            return null;
        }
        return StudentNote.with(
                entity.getId(),
                entity.getStudentId(),
                entity.getAuthorId(),
                entity.getType(),
                entity.getContent(),
                entity.getCreatedAt()
        );
    }

    @Mapping(target = "id", source = "id")
    @Mapping(target = "studentId", source = "studentId")
    @Mapping(target = "authorId", source = "authorId")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "createdAt", source = "createdAt")
    StudentNoteEntity toEntity(StudentNote note);

    StudentNoteResponse toResponse(StudentNoteOutput output);
}

