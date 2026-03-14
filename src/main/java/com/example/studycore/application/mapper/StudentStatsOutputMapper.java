package com.example.studycore.application.mapper;

import com.example.studycore.application.usecase.student.output.StudentStatsOutput;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StudentStatsOutputMapper {

    StudentStatsOutputMapper INSTANCE = Mappers.getMapper(StudentStatsOutputMapper.class);

    default StudentStatsOutput toOutput(
            int activitiesCompleted,
            int activitiesTotal,
            int classesThisMonth,
            int classesTotal,
            int overallProgress
    ) {
        return new StudentStatsOutput(
                activitiesCompleted,
                activitiesTotal,
                classesThisMonth,
                classesTotal,
                overallProgress
        );
    }
}

