package info.jerrinot.aoc2023;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public class Day7 {
    private static final String TIEBREAKER_POINTS_PART_TWO = "J23456789TQKA";
    private static final String TIEBREAKER_POINTS_PART_ONE = "23456789TJQKA";
    private static final Comparator<HandAndBid> SORTER = Comparator.comparingInt((HandAndBid h) -> h.hand.kind.ordinal())
            .thenComparingInt(h -> h.hand.tieBreakPoints)
            .thenComparingInt(h -> h.bid)
            .reversed();

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

    private static int tieBreakerPoints(String cards, String alphabet) {
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
                tieBreakerPoints(cards, TIEBREAKER_POINTS_PART_ONE)
        );
    }

    static Hand parsePartTwo(String cards) {
        return new Hand(
                getKindPartTwo(cards),
                tieBreakerPoints(cards, TIEBREAKER_POINTS_PART_TWO)
        );
    }

    private static int parseAndScore(List<String> strings, Function<String, Hand> handParser) {
        var hands = new ArrayList<HandAndBid>();
        for (String s : strings) {
            String[] split = s.split(" ");
            var hand = handParser.apply(split[0]);
            var bid = Integer.parseInt(split[1]);
            hands.add(new HandAndBid(hand, bid));
        }
        hands.sort(SORTER);
        int score = 0;
        for (int i = 0; i < hands.size(); i++) {
            HandAndBid handAndBid = hands.get(i);
            score += handAndBid.bid * (i + 1);
        }
        return score;
    }

    static Kind getKindPartTwo(String hand) {
        var cards = new HashMap<Character, Integer>();
        int jokerCount = 0;
        for (int i = 0; i < hand.length(); i++) {
            char c = hand.charAt(i);
            if (c == 'J') {
                jokerCount++;
            } else {
                cards.compute(c, (k, v) -> v == null ? 1 : v + 1);
            }
        }

        // simple cases
        if (jokerCount == 5 || jokerCount == 4) {
            return Kind.FiveOfAKind;
        } else if (jokerCount == 0) {
            return getKindPartOne(hand);
        }

        int maxSameCards = cards.values().stream().mapToInt(i -> i).max().getAsInt();
        return switch (maxSameCards) {
            case 4 -> Kind.FiveOfAKind;
            case 3 -> Kind.FullHouse.improveBy(jokerCount);
            case 2 -> {
                if (jokerCount == 1) {
                    yield cards.size() == 2 ? Kind.FullHouse : Kind.ThreeOfAKind;
                } else {
                    yield Kind.ThreeOfAKind.improveBy(jokerCount);
                }
            }
            case 1 -> // all non-joker cards are unique
                    switch (jokerCount) {
                        case 1 -> Kind.OnePair;
                        case 2 -> Kind.ThreeOfAKind;
                        case 3 -> Kind.FourOfAKind;
                        default -> throw new AssertionError("wtf?");
                    };
            default -> throw new AssertionError("wtf?");
        };
    }

    static Kind getKindPartOne(String card) {
        var counts = new HashMap<Character, Integer>();
        for (int i = 0; i < card.length(); i++) {
            char c = card.charAt(i);
            counts.compute(c, (k, v) -> v == null ? 1 : v + 1);
        }
        return switch (counts.values().stream().mapToInt(i -> i).max().getAsInt()) {
            case 5 -> Kind.FiveOfAKind;
            case 4 -> Kind.FourOfAKind;
            case 3 -> {
                if (counts.size() == 2) {
                    yield Kind.FullHouse;
                }
                yield Kind.ThreeOfAKind;
            }
            case 2 -> {
                if (counts.size() == 3) {
                    yield Kind.TwoPairs;
                }
                yield Kind.OnePair;
            }
            case 1 -> Kind.HighCard;
            default -> throw new AssertionError("wtf?");
        };
    }


    record Hand(Kind kind, int tieBreakPoints)  {

    }

    record HandAndBid(Hand hand, int bid) {

    }
}