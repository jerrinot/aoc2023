package info.jerrinot.aoc2023;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Day4 {

    public static void main(String[] args) throws Exception {
        List<String> strings = Files.readAllLines(Path.of(Day1.class.getClassLoader().getResource("4.txt").toURI()));

        int totalScore = 0;
        Set<Integer> winningNumbers = new HashSet<>();
        Deck deck = new Deck(strings.size());
        for (int i = 0; i < strings.size(); i++) {
            winningNumbers.clear();
            String[] numbers = strings.get(i).split(":");
            String[] data = numbers[1].split("\\|");

            Arrays.stream(data[0].split(" "))
                    .map(String::trim)
                    .filter(n -> !n.isEmpty())
                    .map(Integer::parseInt)
                    .forEach(winningNumbers::add);

            long winningCount = getWinningCount(data, winningNumbers);
            deck.addNewCard(i, (int) winningCount);
            int gameScore = winningCount == 0 ? 0 : 1 << (winningCount - 1);
            totalScore += gameScore;
        }
        System.out.println("Part1: " + totalScore);
        int totalCards = deck.countCards();
        System.out.println("Part2: " + totalCards);
    }

    private static long getWinningCount(String[] data, Set<Integer> winningNumbers) {
        return Arrays.stream(data[1].split(" "))
                .map(String::trim)
                .filter(n -> !n.isEmpty())
                .map(Integer::parseInt)
                .filter(winningNumbers::contains)
                .count();
    }

    static class Deck {
        int[] winningCount;
        int[] copies;

        private Deck(int size) {
            winningCount = new int[size];
            copies = new int[size];
        }

        void addNewCard(int n, int winningCount) {
            this.winningCount[n] = winningCount;
            copies[n]++;
            for (int i = n + 1; i < winningCount + n + 1; i++) {
                copies[i] += copies[n];
            }
        }

        int countCards() {
            return Arrays.stream(copies).sum();
        }
    }
}
