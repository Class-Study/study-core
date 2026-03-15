package com.example.studycore.infrastructure.mapper;

import com.example.studycore.application.usecase.activity.output.GetMyActivitiesOutput;
import com.example.studycore.application.usecase.student.output.GetMyProfileOutput;
import com.example.studycore.application.usecase.student.output.GetMyStatsOutput;
import com.example.studycore.application.usecase.studentnote.output.GetMyNotesOutput;
import com.example.studycore.infrastructure.api.controllers.student.response.GetMyActivitiesResponse;
import com.example.studycore.infrastructure.api.controllers.student.response.GetMyProfileResponse;
import com.example.studycore.infrastructure.api.controllers.student.response.GetMyStatsResponse;
import com.example.studycore.infrastructure.api.controllers.studentnote.response.GetMyNotesResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StudentMeResponseMapper {

    StudentMeResponseMapper INSTANCE = Mappers.getMapper(StudentMeResponseMapper.class);

    GetMyProfileResponse toGetMyProfileResponse(GetMyProfileOutput output);

    GetMyActivitiesResponse toGetMyActivitiesResponse(GetMyActivitiesOutput output);

    GetMyStatsResponse toGetMyStatsResponse(GetMyStatsOutput output);

    GetMyNotesResponse toGetMyNotesResponse(GetMyNotesOutput output);
}

