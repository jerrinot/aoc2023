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
        private final int width;
        private int currentNumber;
        private int startingX;
        private int startingY;
        private int numberLen;
        private final Map<Integer, List<Integer>> touchedSymbols = new HashMap<>();  // key = symbol, value = numbers touching the symbol

        CountingMap(int maxX) {
            this.width = maxX + 2;
        }

        void onSymbol(int x, int y) {
            symbolMap.set(flatten(x, y));
        }

        void onDigit(int x, int y, char digit) {
            if (numberLen == 0) {
                startingX = x;
                startingY = y;
            }
            numberLen++;
            currentNumber = currentNumber * 10 + Character.getNumericValue(digit);
        }

        void onNonDigit() {
            if (numberLen == 0) {
                return;
            }
            for (int x = startingX - 1; x < startingX + numberLen + 1; x++) {
                for (int y = startingY - 1; y < startingY + 2; y++) {
                    int coord = flatten(x, y);
                    if (symbolMap.get(coord)) {
                        touchedSymbols.computeIfAbsent(coord, k -> new ArrayList<>()).add(currentNumber);
                    }
                }
            }
            numberLen = 0;
            currentNumber = 0;
        }

        int partNumbers() {
            return touchedSymbols
                    .values()
                    .stream()
                    .flatMapToInt(l -> l.stream().mapToInt(Integer::intValue))
                    .sum();
        }

        int getGearRatios() {
            return touchedSymbols
                    .values()
                    .stream()
                    .filter(l -> l.size() == 2)
                    .mapToInt(l -> l.get(0) * l.get(1))
                    .sum();
        }

        private int flatten(int x, int y) {
            return (y + 1) * width + (x + 1);
        }
    }
}
