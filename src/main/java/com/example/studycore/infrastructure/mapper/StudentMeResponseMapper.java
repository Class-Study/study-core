package com.example.studycore.infrastructure.mapper;

import com.example.studycore.application.usecase.activity.output.GetMyActivitiesOutput;
import com.example.studycore.application.usecase.billing.input.ConfirmPaymentInput;
import com.example.studycore.application.usecase.billing.output.ConfirmPaymentOutput;
import com.example.studycore.application.usecase.billing.output.GetStudentBillingOutput;
import com.example.studycore.application.usecase.classroom.output.GetStudentScheduleOutput;
import com.example.studycore.application.usecase.student.output.GetMyProfileOutput;
import com.example.studycore.application.usecase.student.output.GetMyStatsOutput;
import com.example.studycore.application.usecase.student.output.GetStudentWorkspaceFoldersOutput;
import com.example.studycore.application.usecase.studentnote.output.GetMyNotesOutput;
import com.example.studycore.infrastructure.api.controllers.student.request.ConfirmPaymentRequest;
import com.example.studycore.infrastructure.api.controllers.student.response.ConfirmPaymentResponse;
import com.example.studycore.infrastructure.api.controllers.student.response.GetMyActivitiesResponse;
import com.example.studycore.infrastructure.api.controllers.student.response.GetMyProfileResponse;
import com.example.studycore.infrastructure.api.controllers.student.response.GetMyStatsResponse;
import com.example.studycore.infrastructure.api.controllers.student.response.GetStudentBillingResponse;
import com.example.studycore.infrastructure.api.controllers.student.response.GetStudentScheduleResponse;
import com.example.studycore.infrastructure.api.controllers.student.response.GetStudentWorkspaceFoldersResponse;
import com.example.studycore.infrastructure.api.controllers.studentnote.response.GetMyNotesResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper
public interface StudentMeResponseMapper {

    StudentMeResponseMapper INSTANCE = Mappers.getMapper(StudentMeResponseMapper.class);

    @Mapping(target = "classroom", source = "output.classroom")
    GetMyProfileResponse toGetMyProfileResponse(GetMyProfileOutput output);

    GetMyActivitiesResponse toGetMyActivitiesResponse(GetMyActivitiesOutput output);

    GetMyStatsResponse toGetMyStatsResponse(GetMyStatsOutput output);

    GetMyNotesResponse toGetMyNotesResponse(GetMyNotesOutput output);

    GetStudentBillingResponse toGetStudentBillingResponse(GetStudentBillingOutput output);

    GetStudentScheduleResponse toGetStudentScheduleResponse(GetStudentScheduleOutput output);

    ConfirmPaymentResponse toConfirmPaymentResponse(ConfirmPaymentOutput output);

    @Mapping(target = "studentId", source = "studentId")
    @Mapping(target = "billingId", source = "request.billingId")
    @Mapping(target = "paymentMethod", source = "request.paymentMethod")
    @Mapping(target = "pixKey", source = "request.pixKey")
    @Mapping(target = "amount", source = "request.amount")
    ConfirmPaymentInput toConfirmPaymentInput(ConfirmPaymentRequest request, UUID studentId);

    GetStudentWorkspaceFoldersResponse toGetStudentWorkspaceFoldersResponse(GetStudentWorkspaceFoldersOutput output);

    GetStudentWorkspaceFoldersResponse.FolderItem toFolderItem(GetStudentWorkspaceFoldersOutput.FolderItem item);

    GetStudentWorkspaceFoldersResponse.SubfolderItem toSubfolderItem(GetStudentWorkspaceFoldersOutput.SubfolderItem item);

    GetStudentWorkspaceFoldersResponse.ActivityItem toActivityItem(GetStudentWorkspaceFoldersOutput.ActivityItem item);

    GetStudentWorkspaceFoldersResponse.StudyMaterialItem toStudyMaterialItem(GetStudentWorkspaceFoldersOutput.StudyMaterialItem item);
}
