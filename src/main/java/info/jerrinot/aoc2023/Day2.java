package info.jerrinot.aoc2023;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class Day2 {
    private static final int RED = 0;
    private static final int GREEN = 1;
    private static final int BLUE = 2;
    private static final int COLOR_SIZE = BLUE + 1;

    private static final int RED_LIMIT = 12;
    private static final int GREEN_LIMIT = 13;
    private static final int BLUE_LIMIT = 14;

    public static void main(String[] args) throws Exception {
        List<String> strings = Files.readAllLines(Path.of(Day1.class.getClassLoader().getResource("2.txt").toURI()));
        int part1 = 0, part2 = 0;
        Tokenizer tokenizer = new Tokenizer();
        GameState acc = new GameState();
        for (String s : strings) {
            acc.reset();
            tokenizer.of(s);
            expectToken(tokenizer, "Game");
            int gameId = Integer.parseInt(tokenizer.nextToken());
            expectToken(tokenizer, ":");
            int minPowers = getMinPowers(tokenizer, acc);
            part2 += Math.abs(minPowers);
            if (minPowers >= 0) {
                part1 += gameId;
            }
        }
        System.out.println("Part1: " + part1);
        System.out.println("Part2: " + part2);
    }

    private static void expectToken(Tokenizer tokenizer, String expected) {
        String token = tokenizer.nextToken();
        if (!expected.equals(token)) {
            throw new IllegalArgumentException("Expected '" + expected + "' token, got '" + token + "'");
        }
    }

    static class GameState {
        private final int[] current = new int[COLOR_SIZE];
        private final int[] max = new int[COLOR_SIZE];

        void add(String name, int val) {
            current[toCode(name)] = val;
        }

        private static int toCode(String color) {
            return switch (color) {
                case "red" -> RED;
                case "green" -> GREEN;
                case "blue" -> BLUE;
                default -> throw new IllegalArgumentException("Unknown color: " + color);
            };
        }


        boolean isGamePossible() {
            return max[RED] <= RED_LIMIT
                    && max[GREEN] <= GREEN_LIMIT
                    && max[BLUE] <= BLUE_LIMIT;
        }

        void reset() {
            Arrays.fill(current, 0);
            Arrays.fill(max, 0);
        }

        void setDone() {
            for (int i = 0; i < COLOR_SIZE; i++) {
                max[i] = Math.max(max[i], current[i]);
            }
            Arrays.fill(current, 0);
        }

        int powers() {
            int pw = 1;
            for (int i = 0; i < COLOR_SIZE; i++) {
                pw *= max[i];
            }
            return pw;
        }
    }

    static int getMinPowers(Tokenizer tokenizer, GameState acc) {
        String token = tokenizer.nextToken();
        while (token != null) {
            int val = Integer.parseInt(token);
            token = tokenizer.nextToken();
            acc.add(token, val);
            token = tokenizer.nextToken();
            if (token == null) {
                // EOL
                break;
            } else if (token.equals(";")) {
                acc.setDone();
            } else if (!token.equals(",")) {
                throw new IllegalArgumentException("Unexpected delimiter: " + token);
            }
            token = tokenizer.nextToken();
        }
        acc.setDone();
        return acc.isGamePossible() ? acc.powers() : -acc.powers();
    }


    static class Tokenizer {
        private String s;
        private int pos;

        void of(String s) {
            this.s = s;
            this.pos = 0;
        }

        void skipWhitespaces() {
            while (pos < s.length() && s.charAt(pos) == ' ') {
                pos++;
            }
        }

        String nextToken() {
            skipWhitespaces();
            int i = pos;
            if (i == s.length()) {
                return null;
            }
            while (i < s.length() && !isDelim(s.charAt(i))) {
                i++;
            }
            if (i == pos) {
                if (isDelim(s.charAt(i))) {
                    pos++;
                    return String.valueOf(s.charAt(i));
                } else {
                    return null;
                }
            }
            String token = s.substring(pos, i);
            pos = i;
            return token;
        }

        private static boolean isDelim(char c) {
            return c == ',' || c == ';' || c == ':' || c == ' ';
        }
    }
}
