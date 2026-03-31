package com.example.studycore.application.mapper;

import com.example.studycore.application.usecase.student.output.GetStudentOutput;
import com.example.studycore.application.usecase.student.output.ListStudentsOutput;
import com.example.studycore.domain.model.Classroom;
import com.example.studycore.domain.model.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper
public interface StudentOutputMapper {

    StudentOutputMapper INSTANCE = Mappers.getMapper(StudentOutputMapper.class);

    @Mapping(target = "status", expression = "java(student.getStatus().name())")
    @Mapping(target = "provisoryPass", source = "provisoryPass")
    GetStudentOutput toGetStudentOutput(Student student, String provisoryPass);

    @Mapping(target = "id", source = "student.id")
    @Mapping(target = "teacherId", source = "student.teacherId")
    @Mapping(target = "status", expression = "java(student.getStatus().name())")
    @Mapping(target = "createdAt", source = "student.createdAt")
    @Mapping(target = "provisoryPass", ignore = true)
    @Mapping(target = "extraClass", source = "extraClass")
    GetStudentOutput toGetStudentOutputAndExtraClass(Student student, Classroom classroom);

    default ListStudentsOutput toListStudentsOutput(List<Student> students) {
        if (students == null) {
            return new ListStudentsOutput(List.of());
        }
        return new ListStudentsOutput(students.stream().map(this::toStudentItem).toList());
    }

    @Mapping(target = "status", expression = "java(student.getStatus().name())")
    ListStudentsOutput.StudentItem toStudentItem(Student student);
}
