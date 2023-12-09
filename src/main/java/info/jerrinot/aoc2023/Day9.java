package info.jerrinot.aoc2023;

import java.nio.file.*;
import java.util.*;

import static java.util.stream.Collectors.toCollection;

public class Day9 {
    public static void main(String[] args) throws Exception {
        var strings = Files.readAllLines(Path.of(Day8.class.getClassLoader().getResource("9.txt").toURI()));
        var predictionSum = new Prediction(0, 0);
        for (String s : strings) {
            var seq = parseSequence(s);
            predictionSum = predictionSum.add(predict(seq));
        }
        System.out.println("Part 1: " + predictionSum.next);
        System.out.println("Part 2: " + predictionSum.prev);
    }

    private static Prediction predict(List<Integer> numbers) {
        int next = 0;
        var prevStack = new Stack<Integer>();

        do {
            next += numbers.get(numbers.size() - 1);
            prevStack.push(numbers.get(0));
            for (int i = 1; i < numbers.size(); i++) {
                int nextNumber = numbers.get(i) - numbers.get(i - 1);
                numbers.set(i - 1, nextNumber);
            }
            numbers.remove(numbers.size() - 1); // remove last
        } while (!numbers.stream().allMatch(n -> n == 0));

        int prev = 0;
        while (!prevStack.isEmpty()) {
            prev = prevStack.pop() - prev;
        }
        return new Prediction(prev, next);
    }

    record Prediction(int prev, int next) {
        Prediction add(Prediction other) {
            return new Prediction(prev + other.prev, next + other.next);
        }
    }

    static List<Integer> parseSequence(String line) {
        return Arrays.stream(line.split(" "))
                .map(Integer::parseInt)
                .collect(toCollection(ArrayList::new)); // we will mutate the list
    }
}
