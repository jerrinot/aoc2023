package info.jerrinot.aoc2023;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day5 {
    public static void main(String[] args) throws Exception {
        List<String> strings = Files.readAllLines(Path.of(Day1.class.getClassLoader().getResource("5.txt").toURI()));

        List<Long> seeds = new ArrayList<>();
        Arrays.stream(strings.get(0).split(":")[1].split(" "))
                .filter(s -> !s.isEmpty())
                .forEach(s -> seeds.add(Long.parseLong(s.trim())));

        Mapper mapper = new Mapper();
        Mapper firstMapper = mapper;
        Mapper lastMapper = new Mapper(); // dummy to avoid null checks inside the loop
        for (int i = 2; i < strings.size(); i++) {
            for (i++; i < strings.size(); i++) {
                String line = strings.get(i);
                if (line.isEmpty()) {
                    break;
                }
                String[] data = line.split(" ");
                long dstStart = Long.parseLong(data[0]);
                long srcStart = Long.parseLong(data[1]);
                long rangeLen = Long.parseLong(data[2]);
                mapper.addMapping(dstStart, srcStart, rangeLen);
            }
            lastMapper.setNext(mapper);
            lastMapper = mapper;
            mapper = new Mapper();
        }

        System.out.println("Part 1 " + seeds.stream().map(firstMapper::map).mapToLong(Long::longValue).min().getAsLong());
        long min = Long.MAX_VALUE;
        for (int i = 0; i < seeds.size(); i += 2) {
            System.out.println("Mapping " + i + " out of " + seeds.size());
            long start = seeds.get(i);
            long range = seeds.get(i + 1);
            for (long y = start; y < start + range; y++) {
                min = Math.min(min, firstMapper.map(y));
            }
        }
        System.out.println(min);

    }

    static class Mapper {
        List<Long> rangeLen = new ArrayList<>();
        List<Long> start = new ArrayList<>();
        List<Long> offset = new ArrayList<>();
        Mapper next;

        void addMapping(long dstStart, long srcStart, long rangeLen) {
            this.start.add(srcStart);
            this.offset.add(dstStart - srcStart);
            this.rangeLen.add(rangeLen);
        }


        long map(long src) {
            return next == null ? localRemap(src) : next.map(localRemap(src));
        }

        void setNext(Mapper next) {
            this.next = next;
        }

        private long localRemap(long src) {
            for (int i = 0; i < start.size(); i++) {
                long s = start.get(i);
                if (src >= s && src < s + rangeLen.get(i)) {
                    return src + offset.get(i);
                }
            }
            return src;
        }
    }
}
