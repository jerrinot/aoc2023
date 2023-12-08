package info.jerrinot.aoc2023;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

public class Day7 {
    private static final String TIEBREAKER_POINTS_PART_TWO = "J23456789TQKA";
    private static final String TIEBREAKER_POINTS_PART_ONE = "23456789TJQKA";
    private static final Comparator<HandAndBid> SORTER = Comparator.comparingInt((HandAndBid h) -> h.hand.kind.ordinal())
            .thenComparingInt(h -> h.hand.tieBreakPoints)
            .reversed();

    record Hand(Kind kind, int tieBreakPoints){}
    record HandAndBid(Hand hand, int bid){}

    enum Kind {
        FiveOfAKind,
        FourOfAKind,
        FullHouse,
        ThreeOfAKind,
        TwoPairs,
        OnePair,
        HighCard;

        Kind improveBy(int levels) {
            return Kind.values()[ordinal() - levels];
        }
    }

    public static void main(String[] args) throws Exception {
        List<String> strings = Files.readAllLines(Path.of(Day7.class.getClassLoader().getResource("7.txt").toURI()));
        System.out.println("Part I: " + parseAndScore(strings, Day7::parsePartOne));
        System.out.println("Part II: " + parseAndScore(strings, Day7::parsePartTwo));
    }

    private static int tieBreakers(String cards, String alphabet) {
        int points = 0;
        int multiplier = 1;
        for (int i = cards.length() - 1; i >= 0; i--) {
            char c = cards.charAt(i);
            points += alphabet.indexOf(c) * multiplier;
            multiplier *= alphabet.length();
        }
        return -points;
    }

    static Hand parsePartOne(String cards) {
        return new Hand(
                getKindPartOne(cards),
                tieBreakers(cards, TIEBREAKER_POINTS_PART_ONE)
        );
    }

    static Hand parsePartTwo(String cards) {
        return new Hand(
                getKindPartTwo(cards),
                tieBreakers(cards, TIEBREAKER_POINTS_PART_TWO)
        );
    }

    private static int parseAndScore(List<String> strings, Function<String, Hand> handParser) {
        var hands = strings.stream()
                .map(s -> s.split(" "))
                .map(split -> new HandAndBid(handParser.apply(split[0]), Integer.parseInt(split[1])))
                .sorted(SORTER)
                .toList();
        return IntStream.range(0, hands.size())
                .map(i -> hands.get(i).bid * (i + 1))
                .sum();
    }

    static Kind getKindPartTwo(String hand) {
        var counts = new HashMap<Character, Integer>();
        int jokerCount = countCardsExcept(hand, counts, 'J');

        // simple cases
        if (jokerCount == 5 || jokerCount == 4) {
            return Kind.FiveOfAKind;
        } else if (jokerCount == 0) {
            return getKindPartOne(counts);
        }

        int maxSameCards = counts.values().stream().mapToInt(i -> i).max().getAsInt();
        return switch (maxSameCards) {
            case 4 -> Kind.FiveOfAKind;
            case 3 -> Kind.FullHouse.improveBy(jokerCount);
            case 2 -> {
                if (jokerCount == 1) {
                    yield counts.size() == 2 ? Kind.FullHouse : Kind.ThreeOfAKind;
                }
                // we have a pair of same cards and 2 or 3 jokers
                // if we have 2 jokers then we have FourOfAKind.
                // if we have 3 jokers then we have FiveOfAKind.
                yield Kind.ThreeOfAKind.improveBy(jokerCount);
            }
            case 1 -> Kind.HighCard.improveBy(jokerCount * 2 - 1);
            default -> throw new AssertionError("wtf?");
        };
    }

    private static int countCardsExcept(String cards, HashMap<Character, Integer> results, char exception) {
        int exceptionCount = 0;
        for (int i = 0; i < cards.length(); i++) {
            char c = cards.charAt(i);
            if (c == exception) {
                exceptionCount++;
            } else {
                results.compute(c, (k, v) -> v == null ? 1 : v + 1);
            }
        }
        return exceptionCount;
    }

    static Kind getKindPartOne(String hand) {
        var counts = new HashMap<Character, Integer>();
        countCardsExcept(hand, counts, '*'); // dummy exception value, we want to count *all* cards
        return getKindPartOne(counts);
    }

    private static Kind getKindPartOne(HashMap<Character, Integer> cardCounts) {
        return switch (cardCounts.values().stream().mapToInt(i -> i).max().getAsInt()) {
            case 5 -> Kind.FiveOfAKind;
            case 4 -> Kind.FourOfAKind;
            case 3 -> cardCounts.size() == 2 ? Kind.FullHouse : Kind.ThreeOfAKind;
            case 2 -> cardCounts.size() == 3 ? Kind.TwoPairs : Kind.OnePair;
            case 1 -> Kind.HighCard;
            default -> throw new AssertionError("wtf?");
        };
    }
}