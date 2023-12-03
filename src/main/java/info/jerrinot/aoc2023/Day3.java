package info.jerrinot.aoc2023;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;

public class Day3 {
    public static void main(String[] args) throws Exception {
        List<String> strings = Files.readAllLines(Path.of(Day1.class.getClassLoader().getResource("3.txt").toURI()));
        CountingMap countingMap = buildSymbolMap(strings, Day3::isGearSymbol);
        for (int y = 0; y < strings.size(); y++) {
            String s = strings.get(y);
            scanLine(countingMap, y, s);
        }
        System.out.println("Part2: " + countingMap.sum());
    }

    private static CountingMap buildSymbolMap(List<String> strings, Predicate<Character> symbolPredicate) {
        CountingMap countingMap = new CountingMap(strings.get(0).length());
        for (int y = 0; y < strings.size(); y++) {
            String s = strings.get(y);
            for (int x = 0; x < s.length(); x++) {
                char c = s.charAt(x);
                if (symbolPredicate.test(c)) {
                    countingMap.onSymbolAt(x, y);
                }
            }
        }
        return countingMap;
    }

    static void scanLine(CountingMap map, int y, String line) {
        for (int x = 0; x < line.length(); x++) {
            char ch = line.charAt(x);
            if (Character.isDigit(ch)) {
                map.digit(x, y, ch);
            } else {
                map.nonDigitOrEOL();
            }
        }
        map.nonDigitOrEOL();
    }


    static boolean isSymbolAt(char ch) {
        return ch != '.' && !Character.isDigit(ch);
    }

    static boolean isGearSymbol(char ch) {
        return ch == '*';
    }

    static class CountingMap {
        private final BitSet map = new BitSet();
        private final BitSet touched = new BitSet();
        private final int maxX;
        private int currentNumber;
        private final Map<Integer, List<Integer>> touching = new HashMap<>();

        CountingMap(int maxX) {
            this.maxX = maxX + 2;
        }

        void nonDigitOrEOL() {
            for (int i = touched.nextSetBit(0); i >= 0; i = touched.nextSetBit(i + 1)) {
                List<Integer> set = touching.computeIfAbsent(i, k -> new ArrayList<>());
                set.add(currentNumber);
            }
            currentNumber = 0;
            touched.clear();
        }

        int sum() {
            int sum = 0;
            for (Map.Entry<Integer, List<Integer>> entry : touching.entrySet()) {
                if (entry.getValue().size() == 2) {
                    // multiply
                    sum += entry.getValue().stream().reduce(1, (a, b) -> a * b);
                }
            }
            return sum;
        }

        void markIfTouched(int x, int y) {
            int coords = flattenCoords(x, y);
            if (map.get(coords)) {
                touched.set(coords);
            }
        }

        void digit(int x, int y, char digit) {
            currentNumber = currentNumber * 10 + Character.getNumericValue(digit);
            markIfTouched(x - 1, y);
            markIfTouched(x - 1, y - 1);
            markIfTouched(x - 1, y + 1);
            markIfTouched(x + 1, y);
            markIfTouched(x + 1, y - 1);
            markIfTouched(x + 1, y + 1);
            markIfTouched(x, y - 1);
            markIfTouched(x, y + 1);
        }

        void onSymbolAt(int x, int y) {
            map.set(flattenCoords(x, y));
        }

        int flattenCoords(int x, int y) {
            return (y + 1) * maxX + (x + 1);
        }
    }
}
