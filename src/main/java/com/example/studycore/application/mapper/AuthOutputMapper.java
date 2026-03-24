package com.example.studycore.application.mapper;

import com.example.studycore.application.usecase.auth.output.LoginOutput;
import com.example.studycore.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AuthOutputMapper {

    AuthOutputMapper INSTANCE = Mappers.getMapper(AuthOutputMapper.class);

    default LoginOutput toLoginOutput(User user, String accessToken, String refreshToken, long expiresIn) {
        if (user == null) return null;

        return new LoginOutput(
                accessToken,
                refreshToken,
                expiresIn,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.getPreferenceTheme()
        );
    }
}
