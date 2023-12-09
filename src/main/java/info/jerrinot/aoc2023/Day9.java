package info.jerrinot.aoc2023;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import static java.util.stream.Collectors.toCollection;

public class Day9 {
    public static void main(String[] args) throws Exception {
        List<String> strings = Files.readAllLines(Path.of(Day8.class.getClassLoader().getResource("9.txt").toURI()));

        var predictionSum = new Prediction(0, 0);
        for (String s : strings) {
            var seq = parseSequence(s);
            predictionSum = predictionSum.add(predict(seq));
        }
        System.out.println("Part 1: " + predictionSum.last);
        System.out.println("Part 2: " + predictionSum.first);
    }

    private static Prediction predict(List<Integer> numbers) {
        int last = 0;
        var firstStack = new Stack<Integer>();
        List<Integer> nextLine = new ArrayList<>();
        do {
            for (int i = 1; i < numbers.size(); i++) {
                int nextNumber = numbers.get(i) - numbers.get(i - 1);
                nextLine.add(nextNumber);
            }
            last += numbers.get(numbers.size() - 1);
            firstStack.push(numbers.get(0));

            // swap numbers and nextLine, so we can avoid allocations
            var tmp = numbers;
            tmp.clear();
            numbers = nextLine;
            nextLine = tmp;
        } while (!numbers.stream().allMatch(n -> n == 0));

        int first = 0;
        while (!firstStack.isEmpty()) {
            int firstNumber = firstStack.pop();
            first = firstNumber - first;
        }
        return new Prediction(first, last);
    }

    record Prediction(int first, int last) {
        Prediction add(Prediction other) {
            return new Prediction(first + other.first, last + other.last);
        }
    }

    static List<Integer> parseSequence(String line) {
        return Arrays.stream(line.split(" "))
                .map(Integer::parseInt)
                .collect(toCollection(ArrayList::new)); // we will mutate the list
    }
}
