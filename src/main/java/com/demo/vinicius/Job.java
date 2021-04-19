package com.demo.vinicius;

import java.time.LocalDateTime;
import java.util.function.Predicate;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Job {

    private static final Predicate<String> simpleHours =
        (time) -> time.matches("^(\\d|[0-9]{2})");
    private static final Predicate<String> hoursAndMinutes =
        (time) -> time.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$");
    private static final Predicate<String> hoursAndMinutesSecond =
        (time) -> time.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$");
    private static final Predicate<String> hoursAndMinutesSecondMillis =
        (time) -> time.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]:\\d{1,2}$");

    @Setter
    private Integer id;

    @Setter
    private String description;

    @Setter
    private LocalDateTime dateMaxCompleteProcess;

    private String estimatedTimeEnd;

    @Builder
    public Job(Integer id, String description, LocalDateTime dateMaxCompleteProcess,
        String estimatedTimeEnd) {
        this.id = id;
        this.description = description;
        this.dateMaxCompleteProcess = dateMaxCompleteProcess;
        setEstimatedTimeEnd(estimatedTimeEnd);
    }

    public void setEstimatedTimeEnd(String estimatedTimeEnd) {
        if (simpleHours.test(estimatedTimeEnd)) {
            this.estimatedTimeEnd = String.format("%s:00:00:00", estimatedTimeEnd);
        } else if (hoursAndMinutes.test(estimatedTimeEnd)) {
            this.estimatedTimeEnd = String.format("%s:00:00", estimatedTimeEnd);
        } else if (hoursAndMinutesSecond.test(estimatedTimeEnd)) {
            this.estimatedTimeEnd = String.format("%s:00", estimatedTimeEnd);
        } else if (hoursAndMinutesSecondMillis.test(estimatedTimeEnd)){
            this.estimatedTimeEnd = estimatedTimeEnd;
        } else
            throw new RuntimeException("Data formato inválido");
    }
}
