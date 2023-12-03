package info.jerrinot.aoc2023;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.BitSet;
import java.util.List;

public class Day3 {
    public static void main(String[] args) throws Exception {
        List<String> strings = Files.readAllLines(Path.of(Day1.class.getClassLoader().getResource("3.txt").toURI()));
        SymbolMap map = new SymbolMap(strings.get(0).length(), strings.size());
        for (int y = 0; y < strings.size(); y++) {
            String s = strings.get(y);
            for (int x = 0; x < s.length(); x++) {
                char c = s.charAt(x);
                if (isSymbolAt(c)) {
                    System.out.println("onSymbolAt(" + x + ", " + y + ");");
                    map.onSymbolAt(x, y);
                }
            }
        }

        int sum = 0;
        for (int y = 0; y < strings.size(); y++) {
            String s = strings.get(y);
            sum += sumOfTouchingNumbers(map, y, s);
        }
        System.out.println("Part1: " + sum);
    }

    static int sumOfTouchingNumbers(SymbolMap map, int y, String line) {
        int sum = 0;
        boolean touching = false;
        boolean parsing = false;
        int currentNumber = 0;
        for (int x = 0; x < line.length(); x++) {
            char ch = line.charAt(x);
            if (Character.isDigit(ch)) {
                touching = touching || map.isTouching(x, y);
                if (!parsing) {
                    parsing = true;
                    currentNumber = Character.getNumericValue(ch);
                } else {
                    currentNumber = currentNumber * 10 + Character.getNumericValue(ch);
                }
            } else {
                if (parsing) {
                    if (touching) {
                        sum += currentNumber;
                    }
                    parsing = false;
                    touching = false;
                }
            }
        }
        if (parsing) {
            if (touching) {
                sum += currentNumber;
            }
        }
        return sum;
    }

    static boolean isSymbolAt(char ch) {
        return ch != '.' && !Character.isDigit(ch);
    }

    static class SymbolMap {
        private final BitSet map = new BitSet();
        private final int maxX;
        private final int maxY;

        SymbolMap(int maxX, int maxY) {
            this.maxX = maxX;
            this.maxY = maxY;
        }

        void onSymbolAt(int x, int y) {
            if (x != 0) {
                map.set(flattenCoords(x - 1, y));
                if (y != 0) {
                    map.set(flattenCoords(x - 1, y - 1));
                }
                if (y != maxY) {
                    map.set(flattenCoords(x - 1, y + 1));
                }
            }
            if (x != maxX) {
                map.set(flattenCoords(x + 1, y));
                if (y != 0) {
                    map.set(flattenCoords(x + 1, y - 1));
                }
                if (y != maxY) {
                    map.set(flattenCoords(x + 1, y + 1));
                }
            }
            if (y != 0) {
                map.set(flattenCoords(x, y - 1));
            }
            if (y != maxY) {
                map.set(flattenCoords(x, y + 1));
            }
        }

        boolean isTouching(int x, int y) {
            return map.get(flattenCoords(x, y));
        }

        int flattenCoords(int x, int y) {
            return y * maxX + x;
        }
    }
}
