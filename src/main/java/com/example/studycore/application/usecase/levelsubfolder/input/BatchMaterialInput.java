package com.example.studycore.application.usecase.levelsubfolder.input;

public record BatchMaterialInput(
        String title,
        String type,
        String url,
        String convertedHtml,
        String originalFilename,
        String description
) {}

