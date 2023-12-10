package info.jerrinot.aoc2023;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Day10 {
    private static final int EAST = 0;
    private static final int NORTH = 1;
    private static final int WEST = 2;
    private static final int SOUTH = 3;
    private static final int UNKNOWN = -1;

    private static final int LEFT = 0;
    private static final int RIGHT = 1;

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

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";

    public static void main(String[] args) throws Exception {
        var strings = Files.readAllLines(Path.of(Day8.class.getClassLoader().getResource("10.txt").toURI()));
        var map = new char[strings.size()][];
        for (int i = 0; i < strings.size(); i++) {
            map[i] = strings.get(i).toCharArray();
        }

        var pipeline = findPath(map);
        System.out.println("Part 1: " + pipeline.size() / 2);


        Stack<Coordinates> toBeExplored = populateInitialPoints(pipeline, map);

        List<Coordinates> leaking = new ArrayList<>();
        List<Coordinates> explored = new ArrayList<>();
        while (!toBeExplored.isEmpty()) {
            var exploring = toBeExplored.pop();
            if (explored.contains(exploring)) {
                continue;
            }
            explored.add(exploring);
            if (!leaking.contains(exploring) && pipeline.stream().noneMatch(p -> p.coordinates().equals(exploring))) {
                leaking.add(exploring);
            }

            List<Coordinates> candidates = getPossibleLeakages(map, exploring, pipeline);
            for (Coordinates candidate : candidates) {
                toBeExplored.push(candidate);
            }
        };

        int count = 0;
        for (int y = 0; y < map.length; y++) {
            char[] row = map[y];
            for (int x = 0; x < row.length; x++) {
                int finalX = x;
                int finalY = y;
                if (pipeline.stream().anyMatch(p -> p.coordinates().equals(new Coordinates(finalX, finalY)))) {
//                    System.out.print(ANSI_RED + row[x] + ANSI_RESET);
                } else if (leaking.contains(new Coordinates(x, y))) {
//                    System.out.print(ANSI_GREEN + row[x] + ANSI_RESET);
                } else {
//                    System.out.print(row[x]);
                    count++;
                }
            }
//            System.out.println();
        }
        System.out.println("Part 2:" + count);

    }


    private static Stack<Coordinates> populateInitialPoints(List<Position> pipeline, char[][] map) {
        int maxX = map[0].length - 1;
        int maxY = map.length - 1;
        Stack<Coordinates> toBeExplored = new Stack<>();

        Position explorationStart = pipeline.stream().min(Comparator.comparing(p -> p.coordinates().y())).get();
        int offset = pipeline.indexOf(explorationStart);

        // looking right
        for (int i = 0; i < pipeline.size(); i++) {
            Position current = pipeline.get((i + offset) % pipeline.size());
            Coordinates currentCoordinates = current.coordinates();
            char ch = map[currentCoordinates.y][currentCoordinates.x];

            switch (ch) {
                case NORTH2SOUTH: {
                    if (current.headingTo() == NORTH) {
                        if (currentCoordinates.x != maxX) {
                            Coordinates candidate = new Coordinates(currentCoordinates.x + 1, currentCoordinates.y);
                            if (pipeline.stream().noneMatch(p -> p.coordinates().equals(candidate))) {
                                toBeExplored.push(candidate);
                            }
                        }
                    } else if (current.headingTo() == SOUTH) {
                        if (currentCoordinates.x != 0) {
                            Coordinates candidate = new Coordinates(currentCoordinates.x - 1, currentCoordinates.y);
                            if (pipeline.stream().noneMatch(p -> p.coordinates().equals(candidate))) {
                                toBeExplored.push(candidate);
                            }
                        }
                    }
                    break;
                }
                case (EAST2WEST): {
                    if (current.headingTo() == EAST) {
                        if (currentCoordinates.y != maxY) {
                            Coordinates candidate = new Coordinates(currentCoordinates.x, currentCoordinates.y + 1);
                            if (pipeline.stream().noneMatch(p -> p.coordinates().equals(candidate))) {
                                toBeExplored.push(candidate);
                            }
                        }
                    } else if (current.headingTo() == WEST) {
                        if (currentCoordinates.y != 0) {
                            Coordinates candidate = new Coordinates(currentCoordinates.x, currentCoordinates.y - 1);
                            if (pipeline.stream().noneMatch(p -> p.coordinates().equals(candidate))) {
                                toBeExplored.push(candidate);
                            }
                        }
                    }
                    break;
                }
                case (NORTH2EAST): {
                    if (current.headingTo() == SOUTH) {
                        if (currentCoordinates.y != maxY) {
                            Coordinates candidate = new Coordinates(currentCoordinates.x, currentCoordinates.y + 1);
                            if (pipeline.stream().noneMatch(p -> p.coordinates().equals(candidate))) {
                                toBeExplored.push(candidate);
                            }
                        }
                        if (currentCoordinates.x != 0) {
                            Coordinates candidate = new Coordinates(currentCoordinates.x - 1, currentCoordinates.y);
                            if (pipeline.stream().noneMatch(p -> p.coordinates().equals(candidate))) {
                                toBeExplored.push(candidate);
                            }
                        }
                    }
                    break;
                }
                case (NORTH2WEST): {
                    if (current.headingTo() == EAST) {
                        if (currentCoordinates.y != maxY) {
                            Coordinates candidate = new Coordinates(currentCoordinates.x, currentCoordinates.y + 1);
                            if (pipeline.stream().noneMatch(p -> p.coordinates().equals(candidate))) {
                                toBeExplored.push(candidate);
                            }
                        }
                        if (currentCoordinates.x != maxX) {
                            Coordinates candidate = new Coordinates(currentCoordinates.x + 1, currentCoordinates.y);
                            if (pipeline.stream().noneMatch(p -> p.coordinates().equals(candidate))) {
                                toBeExplored.push(candidate);
                            }
                        }
                    }
                    break;
                }
                case (SOUTH2EAST): {
                    if (current.headingTo() == WEST) {
                        if (currentCoordinates.y != 0) {
                            Coordinates candidate = new Coordinates(currentCoordinates.x, currentCoordinates.y - 1);
                            if (pipeline.stream().noneMatch(p -> p.coordinates().equals(candidate))) {
                                toBeExplored.push(candidate);
                            }
                        }
                        if (currentCoordinates.x != 0) {
                            Coordinates candidate = new Coordinates(currentCoordinates.x - 1, currentCoordinates.y);
                            if (pipeline.stream().noneMatch(p -> p.coordinates().equals(candidate))) {
                                toBeExplored.push(candidate);
                            }
                        }
                    }
                    break;
                }
                case (SOUTH2WEST): {
                    if (current.headingTo() == NORTH) {
                        if (currentCoordinates.y != 0) {
                            Coordinates candidate = new Coordinates(currentCoordinates.x, currentCoordinates.y - 1);
                            if (pipeline.stream().noneMatch(p -> p.coordinates().equals(candidate))) {
                                toBeExplored.push(candidate);
                            }
                        }
                        if (currentCoordinates.x != maxX) {
                            Coordinates candidate = new Coordinates(currentCoordinates.x + 1, currentCoordinates.y);
                            if (pipeline.stream().noneMatch(p -> p.coordinates().equals(candidate))) {
                                toBeExplored.push(candidate);
                            }
                        }
                    }
                    break;
                }
            }
        }
        return toBeExplored;
    }

    static List<Coordinates> getPossibleLeakages(char[][] map, Coordinates current, List<Position> pipeline) {
        int maxX = map[0].length - 1;
        int maxY = map.length - 1;

        var result = new ArrayList<Coordinates>();

        // try north
        Coordinates candidate1 = new Coordinates(current.x, current.y - 1);
        if (current.y > 0 && pipeline.stream().noneMatch(p -> p.coordinates().equals(candidate1))) {
            result.add(candidate1);
        }
        // try south
        Coordinates candidate2 = new Coordinates(current.x, current.y + 1);
        if (current.y < maxY && pipeline.stream().noneMatch(p -> p.coordinates().equals(candidate2))) {
            result.add(candidate2);
        }
        // try west
        Coordinates candidate3 = new Coordinates(current.x - 1, current.y);
        if (current.x > 0 && pipeline.stream().noneMatch(p -> p.coordinates().equals(candidate3))) {
            result.add(candidate3);
        }
        // try east
        Coordinates candidate4 = new Coordinates(current.x + 1, current.y);
        if (current.x < maxX && pipeline.stream().noneMatch(p -> p.coordinates().equals(candidate4))) {
            result.add(candidate4);
        }

        return result;
    }

    static List<Position> findPath(char[][] map) {
        var start = findStart(map);
        var coordinates = start;
        var path = new ArrayList<Position>();
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

    private static Position findNext(Position currentPosition, char[][] map, List<Position> path) {
        Coordinates current = currentPosition.coordinates();
        char currentShape = map[current.y][current.x];
        // try west
        if (current.x > 0 && isValidDirectionsFrom(currentShape, WEST)) {
            char west = map[current.y][current.x - 1];
            if (canGoTo(west, VALID_WESTERN)) {
                var nextCandidate = new Coordinates(current.x - 1, current.y);
                if (path.stream().noneMatch(p -> p.coordinates().equals(nextCandidate))) {
                    return new Position(nextCandidate, WEST);
                }
            }
        }
        // try east
        if (current.x < map[0].length - 1) {
            char east = map[current.y][current.x + 1];
            if (canGoTo(east, VALID_EASTERN) && isValidDirectionsFrom(currentShape, EAST)) {
                var nextCandidate =  new Coordinates(current.x + 1, current.y);
                if (path.stream().noneMatch(p -> p.coordinates().equals(nextCandidate))) {
                    return new Position(nextCandidate, EAST);
                }
            }
        }
        // try north
        if (current.y > 0) {
            char north = map[current.y - 1][current.x];
            if (canGoTo(north, VALID_NORTHERN) && isValidDirectionsFrom(currentShape, NORTH)) {
                var nextCandidate =  new Coordinates(current.x, current.y - 1);
                if (path.stream().noneMatch(p -> p.coordinates().equals(nextCandidate))) {
                    return new Position(nextCandidate, NORTH);
                }
            }
        }
        // try south
        if (current.y < map.length - 1) {
            char south = map[current.y + 1][current.x];
            if (canGoTo(south, VALID_SOUTHERN) && isValidDirectionsFrom(currentShape, SOUTH)) {
                var nextCandidate =  new Coordinates(current.x, current.y + 1);
                if (path.stream().noneMatch(p -> p.coordinates().equals(nextCandidate))) {
                    return new Position(nextCandidate, SOUTH);
                }
            }
        }
        
        // fix heading of the Start tile
        Position startPos = path.get(0);
        Position lastPos = path.get(path.size() - 1);
        int newHeadingTo;
        if (lastPos.coordinates.x() > startPos.coordinates.x()) {
            newHeadingTo = WEST;
        } else if (lastPos.coordinates.x() < startPos.coordinates.x()) {
            newHeadingTo = EAST;
        } else if (lastPos.coordinates.y() > startPos.coordinates.y()) {
            newHeadingTo = NORTH;
        } else if (lastPos.coordinates.y() < startPos.coordinates.y()) {
            newHeadingTo = SOUTH;
        } else {
            throw new IllegalStateException("Cannot find next position");
        }
        Position newStartingPos = new Position(startPos.coordinates(), newHeadingTo);
        path.set(0, newStartingPos);
        return null;
    }

    private static boolean canGoTo(char next, char[] validNext) {
        for (char c : validNext) {
            if (c == next) {
                return true;
            }
        }
        return false;
    }

    static Position findStart(char[][] map) {
        for (int y = 0; y < map.length; y++) {
            char[] row = map[y];
            for (int x = 0; x < row.length; x++) {
                if (row[x] == START) {
                    Coordinates coordinates = new Coordinates(x, y);
                    return new Position(coordinates, UNKNOWN);
                }
            }
        }
        throw new IllegalStateException("No start found");
    }

    record Coordinates(int x, int y) {  }

    record Position(Coordinates coordinates, int headingTo) { }
}
