package info.jerrinot.aoc2023;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day4 {

    public static void main(String[] args) throws Exception {
        List<String> strings = Files.readAllLines(Path.of(Day1.class.getClassLoader().getResource("4.txt").toURI()));

        int totalScore = 0;
        Set<Integer> winningNumbers = new HashSet<>();
        for (String s : strings) {
            winningNumbers.clear();
            String[] numbers = s.split(":");
            String header = numbers[0];
            String[] data = numbers[1].split("\\|");

            Arrays.stream(data[0].split(" "))
                    .map(String::trim)
                    .filter(n -> !n.isEmpty())
                    .map(Integer::parseInt)
                    .forEach(winningNumbers::add);

            long winningCount = Arrays.stream(data[1].split(" "))
                    .map(String::trim)
                    .filter(n -> !n.isEmpty())
                    .map(Integer::parseInt)
                    .filter(winningNumbers::contains)
                    .count();

            int gameScore = winningCount == 0 ? 0 : 1 << (winningCount - 1);
            totalScore += gameScore;
        }
        System.out.println("Part1: " + totalScore);
    }
}
