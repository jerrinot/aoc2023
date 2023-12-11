package info.jerrinot.aoc2023;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.TreeSet;

public class Day11 {

    static int distanceBetween(int linear1, int linear2, int width, TreeSet<Integer> xExpansionPoints, TreeSet<Integer> yExpansionPoints, int growFactor) {
        int x1 = linear1 % width;
        int y1 = linear1 / width;
        int x2 = linear2 % width;
        int y2 = linear2 / width;
        int distanceX = Math.abs(x1 - x2);
        int distanceY = Math.abs(y1 - y2);
        int expansionXDelta = xExpansionPoints.subSet(Math.min(x1, x2), false, Math.max(x1, x2), false).size() * (growFactor - 1);
        int expansionYDelta = yExpansionPoints.subSet(Math.min(y1, y2), false, Math.max(y1, y2), false).size() * (growFactor - 1);
        return distanceX + distanceY + expansionXDelta + expansionYDelta;
    }

    public static void main(String[] args) throws Exception {
        var strings = Files.readAllLines(Path.of(Day8.class.getClassLoader().getResource("11.txt").toURI()));

        var galaxies = new TreeSet<Integer>();
        var yExpansionsPoints = new TreeSet<Integer>();
        for (int y = 0; y < strings.size(); y++) {
            String string = strings.get(y);
            boolean expanded = true;
            for (int x = 0; x < string.length(); x++) {
                if (string.charAt(x) == '#') {
                    galaxies.add(y * string.length() + x);
                    expanded = false;
                }
            }
            if (expanded) {
                yExpansionsPoints.add(y);
            }
        }
        var xExpansionPoints = xExpansionPoints(strings);

        long sum = sumDistances(galaxies, strings.get(0).length(), xExpansionPoints, yExpansionsPoints, 2);
        System.out.println("Part 1: " + sum);

        sum = sumDistances(galaxies, strings.get(0).length(), xExpansionPoints, yExpansionsPoints, 1_000_000);
        System.out.println("Part 2: " + sum);
    }

    private static TreeSet<Integer> xExpansionPoints(List<String> strings) {
        var xExpansionPoints = new TreeSet<Integer>();
        for (int x = 0; x < strings.get(0).length(); x++) {
            boolean expanded = true;
            for (String string : strings) {
                if (string.charAt(x) == '#') {
                    expanded = false;
                    break;
                }
            }
            if (expanded) {
                xExpansionPoints.add(x);
            }
        }
        return xExpansionPoints;
    }

    private static long sumDistances(TreeSet<Integer> galaxies, int width, TreeSet<Integer> xExpansions, TreeSet<Integer> yExpansions, int growFactor) {
        long sum = 0;
        for (int first : galaxies) {
            var seconds = galaxies.tailSet(first, false);
            for (Integer second : seconds) {
                sum += distanceBetween(first, second, width, xExpansions, yExpansions, growFactor);
            }
        }
        return sum;
    }
}
