package com.example.studycore.application.mapper;

import com.example.studycore.application.usecase.folder.output.GetFolderOutput;
import com.example.studycore.application.usecase.folder.output.ListFoldersOutput;
import com.example.studycore.application.usecase.folder.output.WorkspaceOutput;
import com.example.studycore.domain.model.Folder;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FolderOutputMapper {

    FolderOutputMapper INSTANCE = Mappers.getMapper(FolderOutputMapper.class);

    default GetFolderOutput toGetFolderOutput(Folder folder) {
        return new GetFolderOutput(folder.getId(), folder.getStudentId(), folder.getName(), folder.getPosition(), folder.getCreatedAt());
    }

    default ListFoldersOutput toListFoldersOutput(List<Folder> folders) {
        final var items = folders == null ? List.<ListFoldersOutput.Item>of() : folders.stream()
                .map(f -> new ListFoldersOutput.Item(f.getId(), f.getStudentId(), f.getName(), f.getPosition(), f.getCreatedAt()))
                .toList();
        return new ListFoldersOutput(items);
    }

    default WorkspaceOutput.WorkspaceFolderOutput toWorkspaceFolderOutput(
            Folder folder,
            List<WorkspaceOutput.WorkspaceActivityOutput> activities
    ) {
        return new WorkspaceOutput.WorkspaceFolderOutput(
                folder.getId(),
                folder.getName(),
                folder.getPosition(),
                activities
        );
    }
}

