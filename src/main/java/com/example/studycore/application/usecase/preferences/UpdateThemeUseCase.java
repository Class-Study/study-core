package com.example.studycore.application.usecase.preferences;

import com.example.studycore.application.usecase.preferences.input.UpdateThemeInput;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.port.UserGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateThemeUseCase {

    private final UserGateway userGateway;

    @Transactional
    public void execute(UpdateThemeInput input) {
        final var user = userGateway.findById(input.userId())
                .orElseThrow(() -> new NotFoundException("User not found."));

        user.updateTheme(input.theme());
        userGateway.save(user);

        log.debug("[UpdateTheme] Theme updated | userId={} | theme={}", input.userId(), input.theme());
    }
}

