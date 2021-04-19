package com.demo.vinicius;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("Teste - Unidade - FilterJobs")
class FilterJobsTest {

    private static final LocalTime twentyThreeAndFiftyNine = LocalTime.MAX;
    private static final LocalTime elevenAndFiftyNine = LocalTime.of(11, 59);

    private static final LocalDate dateElevenDay = LocalDate.of(2020, 10, 11);
    private static final LocalDate dateTenDay = LocalDate.of(2020, 10, 10);
    private static final LocalDate dateNineDay = LocalDate.of(2020, 10, 9);
    private static final LocalDate dateEightDay = LocalDate.of(2020, 10, 8);

    @ParameterizedTest
    @MethodSource("providerRangeInvalid")
    @DisplayName("Deve lançar exception quando as data de início e fim estão inválida")
    void shouldThrowsILegalArgumentException(LocalDateTime start, LocalDateTime end) {
        var filterJobs = FilterJobs.createFilter(Collections.emptyList(), 8);
        assertThrows(IllegalArgumentException.class, () -> filterJobs.separatedJobs(start, end));
    }

    @ParameterizedTest
    @MethodSource("provideArgumentToTestBetweenDates")
    @DisplayName("Deve retonar jobs entre duas datas")
    void shouldReturnJobsBetweenDates(LocalDateTime start, LocalDateTime end, int expectedSize) {
        var filterJobs = FilterJobs.createFilter(getJobs(), 8);

        var resultList = filterJobs.getStreamSeparateByRange(start, end)
            .collect(Collectors.toList());

        assertEquals(expectedSize, resultList.size());
    }

    private List<Job> getJobs() {
        var list = new ArrayList<Job>();

        var list01 = getListJobBy(
            0,
            10,
            "Importação de arquivos de fundos #%s",
            LocalDateTime.of(dateTenDay, LocalTime.NOON),
            "2:30");
        var list02 = getListJobBy(
            10,
            5,
            "Importação de dados da Base Legada #%s",
            LocalDateTime.of(dateNineDay, twentyThreeAndFiftyNine),
            "4");
        var list03 = getListJobBy(
            15,
            25,
            "Importação de dados de integração #%s",
            LocalDateTime.of(dateEightDay, elevenAndFiftyNine),
            "4:59");
        var list04 = getListJobBy(
            40,
            47,
            "Leitura de arquivo remessa #%s",
            LocalDateTime.of(dateElevenDay, twentyThreeAndFiftyNine),
            "6:15:43");

        list.addAll(list01);
        list.addAll(list02);
        list.addAll(list03);
        list.addAll(list04);

        return list;
    }

    private List<Job> getListJobBy(int startRange, int endRange, String description,
        LocalDateTime dateMaxCompleteProcess, String estimatedTimeEnd) {
        return IntStream.range(startRange, endRange)
            .mapToObj(index -> Job.builder()
                .id(index)
                .description(String.format(description, index))
                .dateMaxCompleteProcess(dateMaxCompleteProcess)
                .estimatedTimeEnd(estimatedTimeEnd)
                .build()
            ).collect(Collectors.toList());
    }

    private static Stream<Arguments> providerRangeInvalid() {
        return Stream.of(
            Arguments.of(
                LocalDateTime.of(2020, 10, 10, 9, 0, 0),
                LocalDateTime.of(2020, 10, 8, 12, 0, 0)
            ),
            Arguments.of(
                LocalDateTime.of(2020, 10, 9, 23, 59, 59),
                LocalDateTime.of(2020, 10, 9, 0, 0, 0)
            ),
            Arguments.of(
                LocalDateTime.of(2020, 10, 9, 23, 59, 59),
                LocalDateTime.of(2020, 10, 9, 23, 59, 59)
            ),
            Arguments.of(
                LocalDateTime.of(2021, 10, 9, 23, 59, 59),
                LocalDateTime.of(2020, 10, 9, 0, 0, 0)
            ),
            Arguments.of(
                LocalDateTime.of(2020, 10, 9, 23, 59, 59),
                LocalDateTime.of(2020, 9, 9, 0, 0, 0)
            )
        );
    }

    private static Stream<Arguments> provideArgumentToTestBetweenDates() {
        return Stream.of(
            Arguments.of(
                LocalDateTime.of(dateTenDay, LocalTime.MIDNIGHT),
                LocalDateTime.of(dateElevenDay, twentyThreeAndFiftyNine),
                17
            ),
            Arguments.of(
                LocalDateTime.of(dateEightDay, LocalTime.NOON),
                LocalDateTime.of(dateNineDay, twentyThreeAndFiftyNine),
                0
            )
        );
    }
}
