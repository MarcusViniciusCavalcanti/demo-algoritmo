package com.demo.vinicius;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Teste - Unidade - Job")
class JobTest {

    @ParameterizedTest
    @ValueSource(strings = {
        "8",
        "8:00",
        "8:00:00",
        "8:00:00:00"
    })
    @DisplayName("Deve formatar tempo estimado para HH:mm:ss:fff ttt")
    void shouldHaveFormatterEstimatedTimeEnd(String value) {
        var job = Job.builder()
            .id(0)
            .description("Description")
            .dateMaxCompleteProcess(LocalDateTime.now())
            .estimatedTimeEnd(value)
            .build();

        assertEquals( "8:00:00:00", job.getEstimatedTimeEnd());
    }

    @ParameterizedTest
    @ValueSource(strings = {"8:00:00:00:00", "text"})
    @DisplayName("Deve lançar exception quando data passada por argumento inválido")
    void shouldThrowsException(String value) {
        assertThrows(RuntimeException.class, () -> Job.builder()
            .id(0)
            .description("Description")
            .dateMaxCompleteProcess(LocalDateTime.now())
            .estimatedTimeEnd(value)
            .build());
    }
}
