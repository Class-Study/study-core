package com.example.studycore.application.mapper;

import com.example.studycore.application.usecase.studentnote.output.StudentNoteOutput;
import com.example.studycore.domain.model.StudentNote;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StudentNoteOutputMapper {

    StudentNoteOutputMapper INSTANCE = Mappers.getMapper(StudentNoteOutputMapper.class);

    StudentNoteOutput toOutput(StudentNote note);
}

