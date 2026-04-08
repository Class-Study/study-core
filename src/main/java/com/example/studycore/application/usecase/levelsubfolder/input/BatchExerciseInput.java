package com.example.studycore.application.usecase.levelsubfolder.input;

public record BatchExerciseInput(
        String title,
        String type,
        String originalFilename,
        String convertedHtml
) {}

