package info.jerrinot.aoc2023;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;

public class Day3 {
    public static void main(String[] args) throws Exception {
        List<String> strings = Files.readAllLines(Path.of(Day1.class.getClassLoader().getResource("3.txt").toURI()));
        CountingMap countingMap = newSymbolMap(strings, Day3::isSymbol);
        System.out.println("Part1: " + countingMap.partNumbers());

        countingMap = newSymbolMap(strings, Day3::isGearSymbol);
        System.out.println("Part2: " + countingMap.getGearRatios());
    }

    private static CountingMap newSymbolMap(List<String> strings, Predicate<Character> symbolPredicate) {
        CountingMap countingMap = new CountingMap(strings.get(0).length());
        for (int y = 0; y < strings.size(); y++) {
            String s = strings.get(y);
            for (int x = 0; x < s.length(); x++) {
                char c = s.charAt(x);
                if (symbolPredicate.test(c)) {
                    countingMap.onSymbol(x, y);
                }
            }
        }
        for (int y = 0; y < strings.size(); y++) {
            String s = strings.get(y);
            scanLine(countingMap, y, s);
        }
        return countingMap;
    }

    static void scanLine(CountingMap map, int y, String line) {
        for (int x = 0; x < line.length(); x++) {
            char ch = line.charAt(x);
            if (Character.isDigit(ch)) {
                map.onDigit(x, y, ch);
            } else {
                map.onNonDigit();
            }
        }
        map.onNonDigit();
    }


    static boolean isSymbol(char ch) {
        return ch != '.' && !Character.isDigit(ch);
    }

    static boolean isGearSymbol(char ch) {
        return ch == '*';
    }

    static class CountingMap {
        private final BitSet symbolMap = new BitSet(); // map of all symbols
        private final BitSet touched = new BitSet(); // map of symbols touched by the currently parsed number
        private final int width;
        private int currentNumber;
        private final Map<Integer, List<Integer>> touching = new HashMap<>();  // key = symbol, value = numbers touching the symbol

        CountingMap(int maxX) {
            this.width = maxX + 2;
        }

        void onSymbol(int x, int y) {
            symbolMap.set(flatten(x, y));
        }

        void onDigit(int x, int y, char digit) {
            currentNumber = currentNumber * 10 + Character.getNumericValue(digit);
            markSurrounding(x, y);
        }

        void onNonDigit() {
            for (int i = touched.nextSetBit(0); i >= 0; i = touched.nextSetBit(i + 1)) {
                List<Integer> set = touching.computeIfAbsent(i, k -> new ArrayList<>());
                set.add(currentNumber);
            }
            currentNumber = 0;
            touched.clear();
        }

        int partNumbers() {
            return touching
                    .values()
                    .stream()
                    .flatMapToInt(l -> l.stream().mapToInt(Integer::intValue))
                    .sum();
        }

        int getGearRatios() {
            return touching
                    .values()
                    .stream()
                    .filter(l -> l.size() == 2)
                    .mapToInt(l -> l.get(0) * l.get(1))
                    .sum();
        }

        private void mark(int x, int y) {
            int coords = flatten(x, y);
            if (symbolMap.get(coords)) {
                touched.set(coords);
            }
        }

        private void markSurrounding(int x, int y) {
            mark(x - 1, y);
            mark(x - 1, y - 1);
            mark(x - 1, y + 1);
            mark(x + 1, y);
            mark(x + 1, y - 1);
            mark(x + 1, y + 1);
            mark(x, y - 1);
            mark(x, y + 1);
        }

        private int flatten(int x, int y) {
            return (y + 1) * width + (x + 1);
        }
    }
}
