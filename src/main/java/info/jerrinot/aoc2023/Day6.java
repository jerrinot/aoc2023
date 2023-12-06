package info.jerrinot.aoc2023;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Day6 {

    static double discriminant(double a, double b, double c) {
        return b * b - 4 * a * c;
    }
    static double x1(double a, double b, double discriminant) {
        return (-b + Math.sqrt(discriminant)) / (2 * a);
    }
    static double x2(double a, double b, double discriminant) {
        return (-b - Math.sqrt(discriminant)) / (2 * a);
    }

    static long waysToWin(long time, long distance) {
        double a = -1;
        double b = time;
        double c = -1 * distance;

        double discriminant = discriminant(a, b, c);

        long x1 = (long) Math.floor(x1(a, b, discriminant) + 1);
        long x2 = (long) Math.ceil(x2(a, b, discriminant) - 1);
        return x2 - x1 + 1;
    }

    public static void main(String[] args) throws Exception {
        List<String> strings = Files.readAllLines(Path.of(Day1.class.getClassLoader().getResource("6.txt").toURI()));
        String times = strings.get(0);
        String distances = strings.get(1);
        part1(times, distances);
        part2(times, distances);
    }

    static void part2(String t, String d) {
        String time = Arrays.stream(t.split(":")[1].split(" "))
                .filter(s -> !s.isEmpty())
                .reduce("", (a, b) -> a + b);
        String distance = Arrays.stream(d.split(":")[1].split(" "))
                .filter(s -> !s.isEmpty())
                .reduce("", (a, b) -> a + b);
        System.out.println("Part 2: " + waysToWin(Long.parseLong(time), Long.parseLong(distance)));
    }

    static void part1(String t, String d) {
        long[] times = Arrays.stream(t.split(":")[1].split(" "))
                .filter(s -> !s.isEmpty())
                .mapToLong(Long::parseLong)
                .toArray();
        long[] distances = Arrays.stream(d.split(":")[1].split(" "))
                .filter(s -> !s.isEmpty())
                .mapToLong(Long::parseLong)
                .toArray();
        long no = IntStream.range(0, times.length)
                .mapToLong(i -> waysToWin(times[i], distances[i]))
                .reduce(1, (a, b) -> a * b);
        System.out.println("Part 1: " + no);
    }
}
