package info.jerrinot.aoc2023;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class DayOne {
    private static final String[] NUMBERS = {"one", "two", "three", "four", "five", "six", "seven", "eight", "nine"};
    private static final String[] NUMBERS_REVERSED = reverse(NUMBERS);

    private static String[] reverse(String[] numbers) {
        String[] res = new String[numbers.length];
        for (int i = 0; i < numbers.length; i++) {
            res[i] = new StringBuilder(numbers[i]).reverse().toString();
        }
        return res;
    }

    static class Matcher {
        private int pos;
        private final BitSet blackList = new BitSet();

        int push(char c, String[] candidates) {
            for (int i = 0; i < candidates.length; i++) {
                if (blackList.get(i)) {
                    continue;
                }
                String candidate = candidates[i];
                if (candidate.charAt(pos) == c) {
                    if (pos == candidate.length() - 1) {
                        return i + 1;
                    }
                } else {
                    blackList.set(i);
                }
            }
            pos++;
            return -1;
        }
    }


    private static int firstDigit(String s) {
        List<Matcher> matchers = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            matchers.add(new Matcher());
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                return Character.getNumericValue(c);
            }
            for (Matcher matcher : matchers) {
                int match = matcher.push(c, NUMBERS);
                if (match != -1) {
                    return match;
                }
            }
        }
        return -1;
    }

    private static int lastDigit(String s) {
        List<Matcher> matchers = new ArrayList<>();
        for (int i = s.length() - 1; i >= 0; i--) {
            matchers.add(new Matcher());
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                return Character.getNumericValue(c);
            }
            for (Matcher matcher : matchers) {
                int match = matcher.push(c, NUMBERS_REVERSED);
                if (match != -1) {
                    return match;
                }
            }
        }
        return -1;
    }

    public static void main(String[] args) throws Exception {
        List<String> strings = Files.readAllLines(Path.of(DayOne.class.getClassLoader().getResource("day1.txt").toURI()));
        int sum = 0;
        for (String s : strings) {
            int firstDigit = firstDigit(s);
            if (firstDigit == -1) {
                System.out.println("No digit found: " + s);
                continue;
            }
            int lastDigit = lastDigit(s);
            System.out.println(firstDigit + " " + lastDigit);
            int number = 10 * firstDigit + lastDigit;
            sum += number;
        }
        System.out.println(sum);
    }
}
