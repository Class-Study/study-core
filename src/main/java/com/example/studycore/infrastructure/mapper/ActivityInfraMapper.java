package com.example.studycore.infrastructure.mapper;

import com.example.studycore.application.usecase.activity.input.CreateActivityInput;
import com.example.studycore.application.usecase.activity.input.UpdateActivityContentInput;
import com.example.studycore.application.usecase.activity.input.UpdateActivityInput;
import com.example.studycore.application.usecase.activity.output.GetActivityOutput;
import com.example.studycore.application.usecase.activity.output.ListActivitiesOutput;
import com.example.studycore.domain.model.Activity;
import com.example.studycore.infrastructure.api.controllers.activity.request.CreateActivityRequest;
import com.example.studycore.infrastructure.api.controllers.activity.request.UpdateActivityContentRequest;
import com.example.studycore.infrastructure.api.controllers.activity.request.UpdateActivityRequest;
import com.example.studycore.infrastructure.api.controllers.activity.response.GetActivityResponse;
import com.example.studycore.infrastructure.api.controllers.activity.response.ListActivitiesResponse;
import com.example.studycore.infrastructure.persistence.activity.ActivityEntity;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ActivityInfraMapper {

    ActivityInfraMapper INSTANCE = Mappers.getMapper(ActivityInfraMapper.class);

    default Activity fromEntity(ActivityEntity entity) {
        if (entity == null) return null;
        return Activity.with(
                entity.getId(),
                entity.getFolderId(),
                entity.getTitle(),
                entity.getType(),
                entity.getContentHtml(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    @Mapping(target = "id", source = "id")
    @Mapping(target = "folderId", source = "folderId")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "contentHtml", source = "contentHtml")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    ActivityEntity toEntity(Activity activity);

    default CreateActivityInput toCreateActivityInput(UUID teacherId, UUID folderId, CreateActivityRequest request) {
        return new CreateActivityInput(teacherId, folderId, request.title(), request.type(), request.contentHtml());
    }

    default UpdateActivityInput toUpdateActivityInput(UUID teacherId, UUID activityId, UpdateActivityRequest request) {
        return new UpdateActivityInput(teacherId, activityId, request.title(), request.type());
    }

    default UpdateActivityContentInput toUpdateActivityContentInput(UUID userId, boolean teacher, UUID activityId, UpdateActivityContentRequest request) {
        return new UpdateActivityContentInput(userId, teacher, activityId, request.contentHtml());
    }

    GetActivityResponse toGetActivityResponse(GetActivityOutput output);

    ListActivitiesResponse toListActivitiesResponse(ListActivitiesOutput output);
}

