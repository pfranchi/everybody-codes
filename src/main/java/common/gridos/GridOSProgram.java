package common.gridos;

import com.google.common.primitives.Chars;
import common.geo.CardinalDirection2D;
import common.geo.MutableCell2D;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class GridOSProgram {

    public enum Movement {
        U, R, D, L, S;

        public void move(MutableCell2D mutableCell2D) {
            switch (this) {
                case U -> mutableCell2D.move(CardinalDirection2D.NORTH);
                case R -> mutableCell2D.move(CardinalDirection2D.EAST);
                case D -> mutableCell2D.move(CardinalDirection2D.SOUTH);
                case L -> mutableCell2D.move(CardinalDirection2D.WEST);
            }
        }

    }

    @FunctionalInterface
    private interface ReadRule {
        boolean check(char c);
    }

    private static final ReadRule READ_EMPTY_VALUE = c -> c == '\u0000';
    private static final ReadRule READ_ANY_VALUE = _ -> true;
    private static final ReadRule READ_NON_EMPTY_VALUE = c -> c != '\u0000';

    private record ConstantReadRule(char value) implements ReadRule {

        @Override
            public boolean check(char c) {
                return c == value;
            }
        }

    private interface WriteRule {
        char write(char original);
    }

    private static final WriteRule WRITE_EMPTY_VALUE = _ -> '\u0000';
    private static final WriteRule WRITE_LEAVE_UNCHANGED = c -> c;

    private record ConstantWriteRule(char value) implements WriteRule {
        @Override
            public char write(char original) {
                return value;
            }
        }

    public class Rule {

        private final String readState;
        private final List<ReadRule> readRules;
        private final String writeState;
        private final List<WriteRule> writeRules;
        private final List<Movement> movements;

        private Rule(int numberOfHeads, String readState, List<ReadRule> readRules, String writeState, List<WriteRule> writeRules, List<Movement> movements) {
            this.readState = readState;
            this.readRules = readRules;
            this.writeState = writeState;
            this.writeRules = writeRules;
            this.movements = movements;
        }

        public boolean canBeApplied(String currentState, List<Character> charactersCurrentlyUnderHeads) {

            if (!currentState.equals(readState)) {
                return false;
            }

            for (int headIndex = 0; headIndex < numberOfHeads; headIndex++) {

                char characterCurrentlyUnderThisHead = charactersCurrentlyUnderHeads.get(headIndex);
                ReadRule readRuleForThisHead = readRules.get(headIndex);

                if (!readRuleForThisHead.check(characterCurrentlyUnderThisHead)) {
                    return false;
                }

            }

            return true;

        }

        public List<Character> writeNewValues(List<Character> originalCharacters) {
            return IntStream.range(0, numberOfHeads)
                    .mapToObj(headIndex -> writeRules.get(headIndex).write(originalCharacters.get(headIndex)))
                    .toList();
        }

        public String getWriteState() {
            return writeState;
        }

        public List<Movement> getMovements() {
            return movements;
        }

    }

    private String programName;
    private int numberOfHeads;
    private List<Character> headStartingPositions;
    private List<Rule> rules;

    public String getProgramName() {
        return programName;
    }

    public int getNumberOfHeads() {
        return numberOfHeads;
    }

    public List<Character> getHeadStartingPositions() {
        return headStartingPositions;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void loadProgram(String programName, List<String> ruleLines) {

        this.programName = programName;

        List<String> nonEmptyRuleLines = new ArrayList<>(ruleLines.stream().filter(line -> !line.isEmpty()).toList());
        String firstLine = nonEmptyRuleLines.removeFirst();

        String[] firstLineParts = firstLine.split(" ");
        if (firstLineParts.length != 2 || !firstLineParts[0].equals("HEADS")) {
            throw new IllegalArgumentException("HEADS command is absent or is not well formatted");
        }

        this.headStartingPositions = Chars.asList(firstLineParts[1].toCharArray());

        this.numberOfHeads = firstLineParts[1].length();

        rules = new ArrayList<>();

        for (String nonEmptyRuleLine: nonEmptyRuleLines) {

            String[] ruleLineParts = nonEmptyRuleLine.split(" ");
            char[] readRuleCharArray = ruleLineParts[1].toCharArray();
            char[] writeRuleCharArray = ruleLineParts[3].toCharArray();
            char[] movementCharArray = ruleLineParts[4].toCharArray();

            if (ruleLineParts.length != 5 || readRuleCharArray.length != numberOfHeads
                || writeRuleCharArray.length != numberOfHeads ||  movementCharArray.length != numberOfHeads) {
                throw new IllegalArgumentException("Rule line is not well formatted: " + nonEmptyRuleLine);
            }

            String readState = ruleLineParts[0];

            List<ReadRule> readRules = new ArrayList<>();
            for (char readRuleChar: readRuleCharArray) {
                ReadRule readRule = switch (readRuleChar) {
                    case '_' -> READ_EMPTY_VALUE;
                    case '*' -> READ_ANY_VALUE;
                    case '!' ->  READ_NON_EMPTY_VALUE;
                    default -> new ConstantReadRule(readRuleChar);
                };
                readRules.add(readRule);
            }

            String writeState = ruleLineParts[2];

            List<WriteRule> writeRules = new ArrayList<>();
            for (char writeRuleChar: writeRuleCharArray) {
                WriteRule writeRule = switch (writeRuleChar) {
                    case '_' -> WRITE_EMPTY_VALUE;
                    case '*' -> WRITE_LEAVE_UNCHANGED;
                    default -> new ConstantWriteRule(writeRuleChar);
                };
                writeRules.add(writeRule);
            }

            List<Movement> movements = new ArrayList<>();
            for (char movementChar: movementCharArray) {
                Movement movement = switch (movementChar) {
                    case 'U' -> Movement.U;
                    case 'R' -> Movement.R;
                    case 'D' -> Movement.D;
                    case 'L' -> Movement.L;
                    case 'S' -> Movement.S;
                    default -> throw new IllegalArgumentException("Movement not supported: " + movementChar);
                };
                movements.add(movement);
            }

            Rule rule = new Rule(numberOfHeads, readState, readRules, writeState, writeRules, movements);
            rules.add(rule);

        }

    }

}
