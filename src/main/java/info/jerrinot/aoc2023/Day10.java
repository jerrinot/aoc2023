package info.jerrinot.aoc2023;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Day10 {
    private static final int EAST = 0;
    private static final int NORTH = 1;
    private static final int WEST = 2;
    private static final int SOUTH = 3;

    private static final char NORTH2SOUTH = '|';
    private static final char EAST2WEST = '-';
    private static final char NORTH2EAST = 'L';
    private static final char NORTH2WEST = 'J';
    private static final char SOUTH2WEST = '7';
    private static final char SOUTH2EAST = 'F';
    private static final char GROUND = '.';
    private static final char START = 'S';

    private static final char[] VALID_NORTHERN = new char[] {NORTH2SOUTH, SOUTH2WEST, SOUTH2EAST, START};
    private static final char[] VALID_SOUTHERN = new char[] {NORTH2SOUTH, NORTH2WEST, NORTH2EAST, START};
    private static final char[] VALID_EASTERN = new char[] {EAST2WEST, NORTH2WEST, SOUTH2WEST, START};
    private static final char[] VALID_WESTERN = new char[] {EAST2WEST, NORTH2EAST, SOUTH2EAST, START};

    public static void main(String[] args) throws Exception {
        var strings = Files.readAllLines(Path.of(Day8.class.getClassLoader().getResource("10.txt").toURI()));
        var map = new char[strings.size()][];

        for (int i = 0; i < strings.size(); i++) {
            map[i] = strings.get(i).toCharArray();
        }

        var coordinates = findStart(map);
        System.out.println(coordinates);
        var path = findPath(map);
        System.out.println(path);
        System.out.println("Path length: " + path.size());
        System.out.println("Part 1: " + path.size() / 2);
    }

    static List<Coordinates> findPath(char[][] map) {
        var start = findStart(map);
        var coordinates = start;
        var path = new ArrayList<Coordinates>();
        path.add(start);
        while (true) {
            var next = findNext(coordinates, map, path);
            if (next == null) {
                break;
            }
            path.add(next);
            coordinates = next;
        }
        return path;
    }

    private static boolean isValidDirectionsFrom(char current, int direction) {
        return switch (direction) {
            case EAST -> current == START || current == EAST2WEST || current == NORTH2EAST || current == SOUTH2EAST;
            case NORTH -> current == START || current == NORTH2SOUTH || current == NORTH2EAST || current == NORTH2WEST;
            case WEST -> current == START || current == EAST2WEST || current == NORTH2WEST || current == SOUTH2WEST;
            case SOUTH -> current == START || current == NORTH2SOUTH || current == SOUTH2EAST || current == SOUTH2WEST;
            default -> throw new IllegalArgumentException("Unknown direction: " + direction);
        };
    }

    private static Coordinates findNext(Coordinates current, char[][] map, List<Coordinates> path) {
        char currentShape = map[current.y][current.x];
        // try west
        if (current.x > 0 && isValidDirectionsFrom(currentShape, WEST)) {
            char west = map[current.y][current.x - 1];
            if (getGoTo(west, VALID_WESTERN)) {
                var nextCandidate = new Coordinates(current.x - 1, current.y);
                if (!path.contains(nextCandidate)) {
                    return nextCandidate;
                }
            }
        }
        // try east
        if (current.x < map[0].length - 1) {
            char east = map[current.y][current.x + 1];
            if (getGoTo(east, VALID_EASTERN) && isValidDirectionsFrom(currentShape, EAST)) {
                var nextCandidate =  new Coordinates(current.x + 1, current.y);
                if (!path.contains(nextCandidate)) {
                    return nextCandidate;
                }
            }
        }
        // try north
        if (current.y > 0) {
            char north = map[current.y - 1][current.x];
            if (getGoTo(north, VALID_NORTHERN) && isValidDirectionsFrom(currentShape, NORTH)) {
                var nextCandidate =  new Coordinates(current.x, current.y - 1);
                if (!path.contains(nextCandidate)) {
                    return nextCandidate;
                }
            }
        }
        // try south
        if (current.y < map.length - 1) {
            char south = map[current.y + 1][current.x];
            if (getGoTo(south, VALID_SOUTHERN) && isValidDirectionsFrom(currentShape, SOUTH)) {
                var nextCandidate =  new Coordinates(current.x, current.y + 1);
                if (!path.contains(nextCandidate)) {
                    return nextCandidate;
                }
            }
        }
        return null;
    }

    private static boolean getGoTo(char next, char[] validNext) {
        for (char c : validNext) {
            if (c == next) {
                return true;
            }
        }
        return false;
    }

    static Coordinates findStart(char[][] map) {
        for (int y = 0; y < map.length; y++) {
            char[] row = map[y];
            for (int x = 0; x < row.length; x++) {
                if (row[x] == START) {
                    return new Coordinates(x, y);
                }
            }
        }
        throw new IllegalStateException("No start found");
    }

    record Coordinates(int x, int y) {}
}
