package info.jerrinot.aoc2023;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Day4 {

    public static void main(String[] args) throws Exception {
        List<String> strings = Files.readAllLines(Path.of(Day1.class.getClassLoader().getResource("4.txt").toURI()));

        int totalScore = 0;
        Set<Integer> winningNumbers = new HashSet<>();
        Deck deck = new Deck();
        for (String s : strings) {
            winningNumbers.clear();
            String[] numbers = s.split(":");
            String[] data = numbers[1].split("\\|");

            Arrays.stream(data[0].split(" "))
                    .map(String::trim)
                    .filter(n -> !n.isEmpty())
                    .map(Integer::parseInt)
                    .forEach(winningNumbers::add);

            long winningCount = getWinningCount(data, winningNumbers);
            deck.addNewCard((int) winningCount);
            int gameScore = winningCount == 0 ? 0 : 1 << (winningCount - 1);
            totalScore += gameScore;
        }
        System.out.println("Part1: " + totalScore);
        deck.processCards();
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
        List<Integer> winningCount = new ArrayList<>();
        List<Integer> copies = new ArrayList<>();

        void addNewCard(int winningCount) {
            this.winningCount.add(winningCount);
            copies.add(1);
        }

        void processCards() {
            for (int i = 0; i < winningCount.size(); i++) {
                int cardWins = winningCount.get(i);
                int cardCopies = copies.get(i);
                for (int y = 0; y < cardWins; y++) {
                    int card = i + y + 1;
                    copies.set(card, copies.get(card) + cardCopies);
                }
            }
        }

        int countCards() {
            return copies.stream().mapToInt(Integer::intValue).sum();
        }
    }
}
