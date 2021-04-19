package com.demo.vinicius;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class CollectorJobs
    implements Collector<Job, Map<Integer, List<Job>>, Map<Integer, List<Job>>> {

    private final Duration maxDuration;
    private final AtomicInteger counter = new AtomicInteger(0);
    private Duration totalDuration = Duration.ZERO;
    private Integer identifier = counter.getAndIncrement();
    private List<Job> arrayList = new ArrayList<>();

    public CollectorJobs(Duration maxDuration) {
        this.maxDuration = maxDuration;
    }

    @Override
    public Supplier<Map<Integer, List<Job>>> supplier() {
        return HashMap::new;
    }

    @Override
    public BiConsumer<Map<Integer, List<Job>>, Job> accumulator() {
        return (map, job) -> {
            var duration = getDuration(job);
            totalDuration = totalDuration.plus(duration);

            if (isExceedLimit()) {
                totalDuration = duration;
                setIdentifier();
                arrayList = new ArrayList<>();
            }

            arrayList.add(job);
            map.put(identifier, arrayList);
        };
    }

    @Override
    public BinaryOperator<Map<Integer, List<Job>>> combiner() {
        return (actual, ele) -> {
            actual.putAll(ele);
            return actual;
        };
    }

    @Override
    public Function<Map<Integer, List<Job>>, Map<Integer, List<Job>>> finisher() {
        return (map) -> map.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (oldValue, newValue) -> oldValue, LinkedHashMap::new)
            );
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.unmodifiableSet(EnumSet.of(Characteristics.IDENTITY_FINISH));
    }

    private Duration getDuration(Job job) {
        var split = job.getEstimatedTimeEnd().split(":");

        var hoursNano = TimeUnit.HOURS.toNanos(Integer.parseInt(split[0]));
        var minutesNano = TimeUnit.MINUTES.toNanos(Integer.parseInt(split[1]));
        var secondNano = TimeUnit.SECONDS.toNanos(Integer.parseInt(split[2]));
        var milliNano = TimeUnit.MILLISECONDS.toNanos(Integer.parseInt(split[3]));

        var totalTime = hoursNano + milliNano + secondNano + minutesNano;
        return Duration.ofNanos(totalTime);
    }

    private boolean isExceedLimit() {
        return totalDuration.compareTo(maxDuration) > 0;
    }

    private void setIdentifier() {
        identifier = counter.getAndIncrement();
    }
}
