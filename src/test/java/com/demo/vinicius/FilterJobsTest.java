package com.demo.vinicius;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("Teste - Unidade - FilterJobs")
class FilterJobsTest {

    private static final String ID_PROPERTY_NAME = "id";

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

    @Test
    @DisplayName("Deve retonar jobs entre duas datas com ordenação da menor para maior data de execução")
    void shouldReturnJobsBetweenDatesAndSortedLessThen() {
        var job1 = Job.builder()
            .id(1)
            .description("Description")
            .dateMaxCompleteProcess(LocalDateTime.of(dateTenDay, LocalTime.MIDNIGHT))
            .estimatedTimeEnd("4:00")
            .build();

        var job2 = Job.builder()
            .id(2)
            .description("Description")
            .dateMaxCompleteProcess(LocalDateTime.of(dateNineDay, LocalTime.NOON))
            .estimatedTimeEnd("4:00")
            .build();

        var job3 = Job.builder()
            .id(3)
            .description("Description")
            .dateMaxCompleteProcess(LocalDateTime.of(dateNineDay, LocalTime.of(9, 0)))
            .estimatedTimeEnd("4:00")
            .build();

        var job4 = Job.builder()
            .id(4)
            .description("Description")
            .dateMaxCompleteProcess(LocalDateTime.of(dateNineDay, LocalTime.of(1, 0)))
            .estimatedTimeEnd("4:00")
            .build();

        var jobs = List.of(job1, job2, job3, job4);

        var filterJobs = FilterJobs.createFilter(jobs, 8);

        var resultList = filterJobs.getStreamSeparateByRange(
            LocalDateTime.of(dateNineDay, LocalTime.MIDNIGHT),
            LocalDateTime.of(dateTenDay, twentyThreeAndFiftyNine)
        ).collect(Collectors.toList());

        assertEquals(job4, resultList.get(0));
        assertEquals(job3, resultList.get(1));
        assertEquals(job2, resultList.get(2));
        assertEquals(job1, resultList.get(3));
    }

    @Test
    @DisplayName("Deve retonar a matrix de jobs, cada coluna da matrix deve conter jobs onde a execução não pode ultrapassar o limite de duração")
    void shouldReturnJobs() {
        var filterJobs = FilterJobs.createFilter(getJobs(), 8);

        var matrixJobs = filterJobs.separatedJobs(
            LocalDateTime.of(dateTenDay, LocalTime.MIDNIGHT),
            LocalDateTime.of(dateElevenDay, twentyThreeAndFiftyNine)
        );

        var resultList = matrixJobs.stream()
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

        assertEquals(17, resultList.size());
        assertEquals(11, matrixJobs.size());

        var listIdZeroToTem = IntStream.range(0, 10)
            .mapToObj(index -> hasProperty(ID_PROPERTY_NAME, is(index)));

        var listIdFortyToFortySeven = IntStream.range(40, 47)
            .mapToObj(index -> hasProperty(ID_PROPERTY_NAME, is(index)));

        var allIds =
            Stream.concat(listIdZeroToTem, listIdFortyToFortySeven).collect(Collectors.toList());

        assertThat(resultList, contains(allIds));
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
