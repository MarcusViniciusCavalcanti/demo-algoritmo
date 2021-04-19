package com.demo.vinicius;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapWithSize.aMapWithSize;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Teste - Unidade - CollectorJobs")
class CollectorJobsTest {

    @Test
    @DisplayName("Deve retornar map de jobs separados por lista de execução com base no limite máximo de execução de PT8H")
    void shouldReturnMapJobs() {
        var jobs01 = buildJobs(1, "Description 7:59", "7:59");
        var jobs02 = buildJobs(2, "Description 0:01", "0:01");
        var jobs03 = buildJobs(3, "Description 0:01", "0:01");
        var jobs04 = buildJobs(4, "Description 8:00", "8:00");
        var jobs05 = buildJobs(5, "Description 7:59:59", "7:59:59");
        var jobs06 = buildJobs(6, "Description 00:00:01", "00:00:01");
        var jobs07 = buildJobs(7, "Description 7:59:59:9", "7:59:59:9");
        var jobs08 = buildJobs(8, "Description 00:00:00:1", "00:00:00:1");
        var jobs09 = buildJobs(9, "Description 7:59:59:59:999999999", "7:59:59:59:999999999");
        var jobs10 = buildJobs(10, "Description 00:00:00:00:000000001", "00:00:00:00:000000001");

        var collector = new CollectorJobs(Duration.ofHours(8));

        var accumulator = collector.accumulator();

        var map = new HashMap<Integer, List<Job>>();

        accumulator.accept(map, jobs01);
        accumulator.accept(map, jobs02);
        accumulator.accept(map, jobs03);
        accumulator.accept(map, jobs04);
        accumulator.accept(map, jobs05);
        accumulator.accept(map, jobs06);
        accumulator.accept(map, jobs07);
        accumulator.accept(map, jobs08);
        accumulator.accept(map, jobs09);
        accumulator.accept(map, jobs10);

        assertThat(map, aMapWithSize(6));
    }

    @Test
    @DisplayName("Deve concatenar a lsita de jobs dentro do map")
    void shouldHaveConcat() {
        var jobs01 = buildJobs(1, "Description", "0:59");
        var jobs02 = buildJobs(2, "Description", "0:01");

        var listJobs = List.of(jobs01, jobs02);
        var collector = new CollectorJobs(Duration.ofHours(8));

        var map = new HashMap<Integer, List<Job>>();
        var concatMap = Map.of(0, listJobs);

        var combiner = collector.combiner();
        combiner.apply(map, concatMap);

        assertThat(map, aMapWithSize(1));
    }

    @Test
    @DisplayName("Deve retonar map ordenado por execução (chave) do menor para o maior")
    void shouldReturnMapOrderLessThen() {
        var jobs01 = buildJobs(1, "Description", "7:59");
        var jobs02 = buildJobs(2, "Description", "0:01");
        var jobs03 = buildJobs(3, "Description", "0:01");
        var jobs04 = buildJobs(4, "Description", "8:00");
        var jobs05 = buildJobs(5, "Description", "7:59:59");
        var jobs06 = buildJobs(6, "Description", "00:00:01");

        var listJobs01 = List.of(jobs01, jobs02);
        var listJobs02 = List.of(jobs04);
        var listJobs03 = List.of(jobs05, jobs06);
        var listJobs04 = List.of(jobs03);

        var collector = new CollectorJobs(Duration.ofHours(8));

        var mapJobsResult = Map.of(
            1, listJobs02,
            3, listJobs04,
            2, listJobs03,
            0, listJobs01
        );

        var finisher = collector.finisher();
        var mapJobFinisher = finisher.apply(mapJobsResult);

        assertEquals(mapJobFinisher.get(0), listJobs01);
        assertEquals(mapJobFinisher.get(1), listJobs02);
        assertEquals(mapJobFinisher.get(2), listJobs03);
        assertEquals(mapJobFinisher.get(3), listJobs04);
    }

    private Job buildJobs(int id, String description, String estimatedTimeEnd) {
        return Job.builder()
            .id(id)
            .description(String.format(description, id))
            .dateMaxCompleteProcess(LocalDateTime.of(LocalDate.of(2020, 10, 10), LocalTime.NOON))
            .estimatedTimeEnd(estimatedTimeEnd)
            .build();
    }
}
