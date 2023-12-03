package info.jerrinot.aoc2023;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Day3 {
    public static void main(String[] args) throws Exception {
        List<String> strings = Files.readAllLines(Path.of(Day1.class.getClassLoader().getResource("3.txt").toURI()));
//        SymbolMap map = new SymbolMap(strings.get(0).length(), strings.size());
//        for (int y = 0; y < strings.size(); y++) {
//            String s = strings.get(y);
//            for (int x = 0; x < s.length(); x++) {
//                char c = s.charAt(x);
//                if (isSymbolAt(c)) {
//                    System.out.println("onSymbolAt(" + x + ", " + y + ");");
//                    map.onSymbolAt(x, y);
//                }
//            }
//        }
//
//        int sum = 0;
//        for (int y = 0; y < strings.size(); y++) {
//            String s = strings.get(y);
//            sum += sumOfTouchingNumbers(map, y, s);
//        }
//        System.out.println("Part1: " + sum);

        CountingMap countingMap = new CountingMap(strings.get(0).length(), strings.size());
        for (int y = 0; y < strings.size(); y++) {
            String s = strings.get(y);
            for (int x = 0; x < s.length(); x++) {
                char c = s.charAt(x);
                if (isGearSymbol(c)) {
                    System.out.println("onSymbolAt(" + x + ", " + y + ");");
                    countingMap.onSymbolAt(x, y);
                }
            }
        }
        for (int y = 0; y < strings.size(); y++) {
            String s = strings.get(y);
            sumOfGears(countingMap, y, s);
        }
        System.out.println("Part2: " + countingMap.sum());
    }

    static void sumOfGears(CountingMap map, int y, String line) {
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
        private final int maxY;
        private int currentNumber;
        private final Map<Integer, Set<Integer>> touching = new HashMap<>();

        CountingMap(int maxX, int maxY) {
            this.maxX = maxX + 2;
            this.maxY = maxY + 2;
        }

        void nonDigitOrEOL() {
            for (int i = touched.nextSetBit(0); i >= 0; i = touched.nextSetBit(i + 1)) {
                Set<Integer> set = touching.computeIfAbsent(i, k -> new HashSet<>());
                set.add(currentNumber);
            }
            currentNumber = 0;
            touched.clear();
        }

        int sum() {
            int sum = 0;
            for (Map.Entry<Integer, Set<Integer>> entry : touching.entrySet()) {
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
