package com.example.studycore.application.mapper;

import com.example.studycore.application.usecase.activity.output.GetActivityOutput;
import com.example.studycore.application.usecase.activity.output.ListActivitiesOutput;
import com.example.studycore.application.usecase.folder.output.WorkspaceOutput;
import com.example.studycore.domain.model.Activity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ActivityOutputMapper {

    ActivityOutputMapper INSTANCE = Mappers.getMapper(ActivityOutputMapper.class);

    default GetActivityOutput toGetActivityOutput(Activity activity) {
        return new GetActivityOutput(
                activity.getId(),
                activity.getFolderId(),
                activity.getTitle(),
                activity.getType(),
                activity.getConvertedHtml(),
                activity.getCreatedBy(),
                activity.getCreatedAt(),
                activity.getUpdatedAt()
        );
    }

    default ListActivitiesOutput toListActivitiesOutput(List<Activity> activities) {
        final var items = activities == null ? List.<ListActivitiesOutput.Item>of() : activities.stream()
                .map(a -> new ListActivitiesOutput.Item(
                        a.getId(),
                        a.getFolderId(),
                        a.getTitle(),
                        a.getType(),
                        a.getConvertedHtml(),
                        a.getCreatedBy(),
                        a.getCreatedAt(),
                        a.getUpdatedAt()
                ))
                .toList();
        return new ListActivitiesOutput(items);
    }

    default WorkspaceOutput.WorkspaceActivityOutput toOutput(Activity activity) {
        if (activity == null) return null;
        return new WorkspaceOutput.WorkspaceActivityOutput(
                activity.getId(),
                activity.getTitle(),
                activity.getType(),
                activity.getConvertedHtml(),
                activity.getFolderId(),
                activity.getCreatedAt() != null ? activity.getCreatedAt().toString() : ""
        );
    }
}

