package com.demo.vinicius;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FilterJobs {

    private final List<Job> jobs;

    public List<List<Job>> separatedJobs(LocalDateTime start, LocalDateTime end) {
        if (Duration.between(start, end).isNegative() || Duration.between(start, end).isZero()) {
            throw new IllegalArgumentException(
                String.format("Datas informadas são inválidas data de início: %s data fim: %s",
                    start.toString(),
                    end.toString()
                )
            );
        }

        return null;
    }

    /**
     * Retornar um Stream de jobs entre duas dadas utilizando a abordagem inclusiva
     *
     * @param start data de início do range
     * @param end   data fim do range
     * @return Stream de jobs
     */
    Stream<Job> getStreamSeparateByRange(LocalDateTime start, LocalDateTime end) {
        return jobs.stream()
            .filter(filterJobsRangeDate(start, end))
            .sorted(
                Comparator.comparing(Job::getDateMaxCompleteProcess, Comparator.naturalOrder()));
    }

    private Predicate<Job> filterJobsRangeDate(LocalDateTime start, LocalDateTime end) {
        return job -> {
            var dateMaxCompleteProcess = job.getDateMaxCompleteProcess();
            return !dateMaxCompleteProcess.isBefore(start) && !dateMaxCompleteProcess.isAfter(end);
        };
    }

    public static FilterJobs createFilter(List<Job> jobs) {
        return new FilterJobs(jobs);
    }
}
