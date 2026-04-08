package com.example.studycore.infrastructure.mapper;

import com.example.studycore.application.usecase.levelsubfolder.input.BatchExerciseInput;
import com.example.studycore.application.usecase.levelsubfolder.input.BatchMaterialInput;
import com.example.studycore.application.usecase.levelsubfolder.input.BatchSubfolderInput;
import com.example.studycore.application.usecase.levelsubfolder.input.CreateSubfoldersBatchInput;
import com.example.studycore.application.usecase.levelsubfolder.input.ExerciseItemInput;
import com.example.studycore.application.usecase.levelsubfolder.input.MaterialItemInput;
import com.example.studycore.application.usecase.levelsubfolder.input.UpdateSubfolderWithContentsInput;
import com.example.studycore.application.usecase.levelsubfolder.output.LevelSubfolderOutput;
import com.example.studycore.infrastructure.api.controllers.levelsubfolder.request.CreateSubfoldersBatchRequest;
import com.example.studycore.infrastructure.api.controllers.levelsubfolder.request.UpdateSubfolderWithContentsRequest;
import com.example.studycore.infrastructure.api.controllers.levelsubfolder.response.LevelSubfolderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper
public interface LevelSubfolderInfraMapper {

    LevelSubfolderInfraMapper INSTANCE = Mappers.getMapper(LevelSubfolderInfraMapper.class);

    // ── Response ─────────────────────────────────────────────────────────────

    /** Output → HTTP response (field names match — MapStruct generates). */
    LevelSubfolderResponse toResponse(LevelSubfolderOutput output);

    @Mapping(source = "profileId",          target = "levelProfileId")
    @Mapping(source = "folderId",           target = "levelFolderId")
    @Mapping(source = "request.subfolders", target = "subfolders")
    @Mapping(source = "createdBy",          target = "createdBy")
    CreateSubfoldersBatchInput toBatchInput(UUID profileId, UUID folderId,
                                           CreateSubfoldersBatchRequest request, UUID createdBy);
    BatchSubfolderInput toSubfolderInput(CreateSubfoldersBatchRequest.SubfolderBatchItem item);

    /** ExerciseBatchItem → BatchExerciseInput (field names match). */
    BatchExerciseInput toExerciseInput(CreateSubfoldersBatchRequest.ExerciseBatchItem item);

    /** MaterialBatchItem → BatchMaterialInput (field names match). */
    BatchMaterialInput toMaterialInput(CreateSubfoldersBatchRequest.MaterialBatchItem item);

    // ── PUT subfolder (update with contents) ─────────────────────────────────

    /**
     * PUT body → use-case input.
     * profileId/folderId/subfolderId/updatedBy come as separate path/auth params;
     * the remaining fields are sourced from the request body.
     */
    @Mapping(source = "profileId",                  target = "levelProfileId")
    @Mapping(source = "folderId",                   target = "levelFolderId")
    @Mapping(source = "subfolderId",                target = "subfolderId")
    @Mapping(source = "request.name",               target = "name")
    @Mapping(source = "request.propagateToStudents",target = "propagateToStudents")
    @Mapping(source = "request.exercises",          target = "exercises")
    @Mapping(source = "request.materials",          target = "materials")
    @Mapping(source = "request.deletedExerciseIds", target = "deletedExerciseIds")
    @Mapping(source = "request.deletedMaterialIds", target = "deletedMaterialIds")
    @Mapping(source = "updatedBy",                  target = "updatedBy")
    UpdateSubfolderWithContentsInput toUpdateInput(UUID profileId, UUID folderId, UUID subfolderId,
                                                   UpdateSubfolderWithContentsRequest request, UUID updatedBy);

    /** ExerciseItem → ExerciseItemInput (field names match). */
    ExerciseItemInput toExerciseItemInput(UpdateSubfolderWithContentsRequest.ExerciseItem item);

    /** MaterialItem → MaterialItemInput (field names match). */
    MaterialItemInput toMaterialItemInput(UpdateSubfolderWithContentsRequest.MaterialItem item);
}
