package com.example.studycore.application.usecase.levelsubfolder.output;

import java.util.List;

public record CreateSubfoldersBatchOutput(
        List<BatchSubfolderOutput> subfolders
) {}

