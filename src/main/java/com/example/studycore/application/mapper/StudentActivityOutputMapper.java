package com.example.studycore.application.mapper;

import com.example.studycore.application.usecase.activity.output.StudentActivityOutput;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StudentActivityOutputMapper {

    StudentActivityOutputMapper INSTANCE = Mappers.getMapper(StudentActivityOutputMapper.class);

    default StudentActivityOutput toOutput(
            java.util.UUID id,
            String title,
            String type,
            String folderName,
            java.util.UUID folderId,
            java.time.OffsetDateTime createdAt
    ) {
        return new StudentActivityOutput(id, title, type, folderName, folderId, createdAt);
    }
}

