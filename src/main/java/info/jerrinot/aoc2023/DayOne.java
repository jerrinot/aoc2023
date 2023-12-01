package info.jerrinot.aoc2023;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class DayOne {
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
        List<SingleMatcher> pool = new ArrayList<>();
        List<SingleMatcher> matchers = new ArrayList<>();

        int push(char c, String[] candidates) {
            addMatcherIfNeeded();
            Iterator<SingleMatcher> iterator = matchers.iterator();
            while (iterator.hasNext()) {
                SingleMatcher matcher = iterator.next();
                int match = matcher.push(c, candidates);
                if (match > 0) {
                    return match;
                }
                if (match == MATCHER_EXHAUSTED) {
                    iterator.remove();
                    matcher.reset();
                    pool.add(matcher);
                }
            }
            return NO_MATCH;
        }

        private void addMatcherIfNeeded() {
            if (pool.isEmpty()) {
                matchers.add(new SingleMatcher());
            } else {
                SingleMatcher matcher = pool.remove(pool.size() - 1);
                matcher.reset();
                matchers.add(matcher);
            }
        }

        void reset() {
            pool.addAll(matchers);
            matchers.clear();
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
        List<String> strings = Files.readAllLines(Path.of(DayOne.class.getClassLoader().getResource("day1.txt").toURI()));
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
