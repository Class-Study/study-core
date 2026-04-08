package com.example.studycore.infrastructure.api.controllers.levelsubfolder.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateSubfoldersBatchRequest(
        @NotNull @NotEmpty List<@Valid SubfolderBatchItem> subfolders
) {
    public record SubfolderBatchItem(
            @NotBlank String name,
            Integer position,
            Boolean propagateToStudents,
            List<@Valid ExerciseBatchItem> exercises,
            List<@Valid MaterialBatchItem> materials
    ) {}

    public record ExerciseBatchItem(
            @NotBlank String title,
            String type,
            String originalFilename,
            String convertedHtml
    ) {}

    public record MaterialBatchItem(
            @NotBlank String title,
            @NotBlank String type,
            String url,
            String convertedHtml,
            String originalFilename,
            String description
    ) {}
}

