package com.example.studycore.infrastructure.api.controllers.levelsubfolder.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record UpdateSubfolderWithContentsRequest(

        @NotBlank
        String name,

        @NotNull
        Boolean propagateToStudents,

        @NotNull
        List<@Valid ExerciseItem> exercises,

        @NotNull
        List<@Valid MaterialItem> materials,

        @NotNull
        List<UUID> deletedExerciseIds,

        @NotNull
        List<UUID> deletedMaterialIds

) {
    /**
     * An exercise item.
     * If {@code id} is present the item already exists — all other fields are ignored.
     * If {@code id} is null the item will be created; {@code title} becomes mandatory.
     */
    public record ExerciseItem(
            UUID id,
            String title,
            String type,
            String originalFilename,
            String convertedHtml
    ) {}

    /**
     * A material item.
     * Same id-presence rule as {@link ExerciseItem}.
     */
    public record MaterialItem(
            UUID id,
            String title,
            String type,
            String url,
            String convertedHtml,
            String originalFilename,
            String description
    ) {}
}

