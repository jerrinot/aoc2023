package info.jerrinot.aoc2023;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day8 {
    private static final byte LEFT = 0;
    private static final byte RIGHT = 1;

    private static final Predicate<String> PART_ONE_STARTING_POSITION = s -> s.equals("AAA");
    private static final Predicate<String> PART_ONE_FINAL_POSITION = s -> s.equals("ZZZ");
    private static final Predicate<String> PART_TWO_STARTING_POSITION = s -> s.endsWith("A");
    private static final Predicate<String> PART_TWO_FINAL_POSITION = s -> s.endsWith("Z");

    public static void main(String[] args) throws Exception {
        List<String> strings = Files.readAllLines(Path.of(Day7.class.getClassLoader().getResource("8.txt").toURI()));

        System.out.println("Part1: " + distance(strings, PART_ONE_STARTING_POSITION, PART_ONE_FINAL_POSITION));
        System.out.println("Part2: " + distance(strings, PART_TWO_STARTING_POSITION, PART_TWO_FINAL_POSITION));
    }

    static long leastCommonMultiple(long a, long b) {
        return (a * b) / greatestCommonDivisor(a, b);
    }

    static long greatestCommonDivisor(long a, long b) {
        if (b == 0) {
            return a;
        }
        return greatestCommonDivisor(b, a % b);
    }

    static long leastCommonMultiple(long[] input) {
        if (input.length == 1) {
            return input[0];
        }
        long result = input[0];
        for(int i = 1; i < input.length; i++) {
            result = leastCommonMultiple(result, input[i]);
        }
        return result;
    }

    static long distance(List<String> strings, Predicate<String> startingPosition, Predicate<String> finalPosition) {
        HashMap<String, String[]> map = newMap(strings);
        var navigator = newNavigator(strings);

        var positions =  map.keySet().stream().filter(startingPosition).toList();
        long[] strides = new long[positions.size()];
        for (long steps = 0, rem = positions.size(); rem > 0; steps++) {
            for (int i = 0; i < positions.size(); i++) {
                String currentPos = positions.get(i);
                if (strides[i] == 0 && finalPosition.test(currentPos)) {
                    strides[i] = steps;
                    rem--;
                }
            }
            byte turn = navigator.nextTurn();
            positions = positions.stream()
                    .map(s -> map.get(s)[turn])
                    .toList();
        }
        return leastCommonMultiple(strides);
    }

    static HashMap<String, String[]> newMap(List<String> strings) {
        var map = new HashMap<String, String[]>();
        Pattern pattern = Pattern.compile("(\\w+)\\s*=\\s*\\((\\w+),\\s*(\\w+)\\)");
        for (int i = 2; i < strings.size(); i++) {
            Matcher matcher = pattern.matcher(strings.get(i));
            if (!matcher.matches()) {
                throw new IllegalArgumentException("Cannot parse: " + strings.get(i));
            }
            String from = matcher.group(1);
            String toLeft = matcher.group(2);
            String toRight = matcher.group(3);
            map.put(from, new String[]{toLeft, toRight});
        }
        return map;
    }

    static Navigator newNavigator(List<String> strings) {
        var dir = strings.get(0);
        byte[] directions = new byte[dir.length()];
        for (int i = 0; i < dir.length(); i++) {
            char c = dir.charAt(i);
            directions[i] = c == 'L' ? LEFT : RIGHT;
        }
        return new Navigator(directions);
    }

    static class Navigator {
        final byte[] directions;
        int pos = -1;

        Navigator(byte[] directions) {
            this.directions = directions;
        }

        byte nextTurn() {
            return directions[++pos % directions.length];
        }
    }
}
