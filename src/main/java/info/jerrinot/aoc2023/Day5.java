package info.jerrinot.aoc2023;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Day5 {
    public static void main(String[] args) throws Exception {
        var strings = Files.readAllLines(Path.of(Day1.class.getClassLoader().getResource("5.txt").toURI()));

        var seeds = Arrays.stream(strings.get(0).split(":")[1].split(" "))
                .filter(s -> !s.isEmpty())
                .map(String::trim)
                .map(Long::parseLong)
                .toList();
        var intervalMappers = toMappers(strings);

        // part 1
        var intervals = seeds.stream().map(i -> new Interval(i, i + 1)).toList();
        for (IntervalMapper intervalMapper : intervalMappers) {
            intervals = intervalMapper.map(intervals);
        }
        System.out.println("Part 1 " + intervals.stream().mapToLong(i -> i.from).min().getAsLong());

        // part 2
        intervals = IntStream.range(0, seeds.size())
                .filter(i -> i % 2 == 0)
                .mapToObj(i -> new Interval(seeds.get(i), seeds.get(i) + seeds.get(i + 1)))
                .toList();
        for (IntervalMapper intervalMapper : intervalMappers) {
            intervals = intervalMapper.map(intervals);
        }
        System.out.println("Part 2 " + intervals.stream().mapToLong(i -> i.from).min().getAsLong());
    }

    private static List<IntervalMapper> toMappers(List<String> strings) {
        var intervalMappers = new ArrayList<IntervalMapper>();
        for (int i = 2; i < strings.size(); i++) {
            var intervalMapper = new IntervalMapper();
            for (i++; i < strings.size(); i++) {
                var line = strings.get(i);
                if (line.isEmpty()) {
                    break;
                }
                String[] data = line.split(" ");
                long dstStart = Long.parseLong(data[0]);
                long srcStart = Long.parseLong(data[1]);
                long rangeLen = Long.parseLong(data[2]);
                intervalMapper = intervalMapper.withInterval(srcStart, srcStart + rangeLen, dstStart - srcStart);
            }
            intervalMappers.add(intervalMapper);
        }
        return intervalMappers;
    }

    static class IntervalMapper {
        final List<IntervalMapping> mappings;

        IntervalMapper() {
            mappings = new ArrayList<>();
            mappings.add(new IntervalMapping(new Interval(0, Long.MAX_VALUE), 0));
        }

        private IntervalMapper(List<IntervalMapping> mappings) {
            this.mappings = mappings;
        }

        IntervalMapper withInterval(long from, long to, long offset) {
            var newMappings = new ArrayList<IntervalMapping>();
            var newInterval = new Interval(from, to);
            for (IntervalMapping currentInterval : mappings) {
                var intersection = currentInterval.interval.intersect(newInterval);
                if (intersection == null) {
                    // no overlap
                    newMappings.add(currentInterval);
                } else if (intersection.from == currentInterval.interval.from && intersection.to == currentInterval.interval.to) {
                    // exact overlap
                    newMappings.add(IntervalMapping.of(currentInterval.interval.from, currentInterval.interval.to, offset));
                } else if (intersection.from == currentInterval.interval.from) {
                    // intersect | original suffix
                    newMappings.add(IntervalMapping.of(intersection.from, intersection.to, offset));
                    newMappings.add(IntervalMapping.of(intersection.to + 1, currentInterval.interval.to, currentInterval.offset));
                } else if (intersection.to == currentInterval.interval.to) {
                    // original prefix | intersect
                    newMappings.add(IntervalMapping.of(currentInterval.interval.from, intersection.from - 1, currentInterval.offset));
                    IntervalMapping newMapping = IntervalMapping.of(intersection.from, intersection.to, offset);
                    newMappings.add(newMapping);
                } else {
                    // original prefix | intersect | original suffix
                    newMappings.add(IntervalMapping.of(currentInterval.interval.from, intersection.from - 1, currentInterval.offset));
                    newMappings.add(IntervalMapping.of(intersection.from, intersection.to, offset));
                    newMappings.add(IntervalMapping.of(intersection.to + 1, currentInterval.interval.to, currentInterval.offset));
                }
            }
            return new IntervalMapper(newMappings);
        }

        List<Interval> map(List<Interval> intervals) {
            var result = new ArrayList<Interval>();
            for (Interval interval : intervals) {
                result.addAll(map(interval));
            }
            return result;
        }

        private List<Interval> map(Interval interval) {
            var result = new ArrayList<Interval>();
            for (IntervalMapping mapping : mappings) {
                var intersection = mapping.interval.intersect(interval);
                if (intersection == null) {
                    continue;
                }
                result.add(new Interval(intersection.from + mapping.offset, intersection.to + mapping.offset));
            }
            return result;
        }
    }

    record IntervalMapping(Interval interval, long offset) {
        static IntervalMapping of(long from, long to, long offset) {
            return new IntervalMapping(new Interval(from, to), offset);
        }
    }

    record Interval(long from, long to) {
        Interval intersect(Interval other) {
            long intersectFrom = Math.max(from, other.from);
            long intersectTo = Math.min(to, other.to);
            if (intersectFrom >= intersectTo) {
                return null;
            }
            return new Interval(intersectFrom, intersectTo);
        }
    }
}
