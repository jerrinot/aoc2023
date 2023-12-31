package info.jerrinot.aoc2023;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Day1 {
    private static final String[] NUMBERS = {"one", "two", "three", "four", "five", "six", "seven", "eight", "nine"};
    private static final String[] NUMBERS_REVERSED = reverse(NUMBERS);
    private static final int NO_MATCH = -1;
    private static final int MATCHER_EXHAUSTED = -2;

    private static String reverse(String s) {
        return new StringBuilder(s).reverse().toString();
    }

    private static String[] reverse(String[] numbers) {
        String[] res = new String[numbers.length];
        for (int i = 0; i < numbers.length; i++) {
            res[i] = reverse(numbers[i]);
        }
        return res;
    }

    static class SingleMatcher {
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

            if (blackList.cardinality() == candidates.length) {
                return MATCHER_EXHAUSTED;
            }
            pos++;
            return NO_MATCH;
        }

        void reset() {
            pos = 0;
            blackList.clear();
        }
    }

    static class MultiMatcher {
        private final List<SingleMatcher> matchers = new ArrayList<>();
        private final BitSet pooled = new BitSet();

        int push(char c, String[] candidates) {
            addMatcher();
            for (int i = 0; i < matchers.size(); i++) {
                if (pooled.get(i)) {
                    continue;
                }
                SingleMatcher matcher = matchers.get(i);
                int match = matcher.push(c, candidates);
                if (match > 0) {
                    return match;
                }
                if (match == MATCHER_EXHAUSTED) {
                    pooled.set(i);
                }
            }
            return NO_MATCH;
        }

        private void addMatcher() {
            int i = pooled.nextSetBit(0);
            if (i == -1) {
                matchers.add(new SingleMatcher());
            } else {
                matchers.get(i).reset();
                pooled.clear(i);
            }
        }

        void reset() {
            pooled.set(0, matchers.size());
        }
    }

    private static int firstDigit(String s, String[] candidates, MultiMatcher matcher) {
        matcher.reset();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                return Character.getNumericValue(c);
            }
            int match = matcher.push(c, candidates);
            if (match > 0) {
                return match;
            }
        }
        return NO_MATCH;
    }

    public static void main(String[] args) throws Exception {
        List<String> strings = Files.readAllLines(Path.of(Day1.class.getClassLoader().getResource("day1.txt").toURI()));
        int sum = 0;
        MultiMatcher matcher = new MultiMatcher();
        for (String s : strings) {
            int firstDigit = firstDigit(s, NUMBERS, matcher);
            if (firstDigit < 1) {
                continue;
            }
            int lastDigit = firstDigit(reverse(s), NUMBERS_REVERSED, matcher);
            int number = 10 * firstDigit + lastDigit;
            sum += number;
        }
        System.out.println(sum);
    }
}
