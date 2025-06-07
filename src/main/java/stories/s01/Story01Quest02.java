package stories.s01;

import common.stats.AbstractQuest;
import common.support.interfaces.Quest02;
import common.support.interfaces.Story01;
import common.support.params.ExecutionParameters;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Story01Quest02 extends AbstractQuest implements Story01, Quest02 {

    private static final Pattern ADD_PATTERN = Pattern.compile(
            "^ADD id=(?<id>\\d+) left=\\[(?<leftRank>\\d+),(?<leftSymbol>.)] right=\\[(?<rightRank>\\d+),(?<rightSymbol>.)]$");
    private static final Pattern SWAP_PATTERN = Pattern.compile("^SWAP (?<id>\\d+)");

    private static class Node {

        private int id;
        private int rank;
        private String symbol;
        private Node leftChild;
        private Node rightChild;

    }

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        //inputLines.forEach(this::log);

        int numberOfInstructions = inputLines.size();

        String firstInstruction = inputLines.getFirst();
        Matcher firstInstructionMatcher = ADD_PATTERN.matcher(firstInstruction);

        if (!firstInstructionMatcher.matches()) {
            throw new IllegalArgumentException();
        }

        int rootId = Integer.parseInt(firstInstructionMatcher.group("id"));

        Node leftRoot = new Node();
        leftRoot.id = rootId;
        leftRoot.rank = Integer.parseInt(firstInstructionMatcher.group("leftRank"));
        leftRoot.symbol = firstInstructionMatcher.group("leftSymbol");

        Node rightRoot = new Node();
        rightRoot.id = rootId;
        rightRoot.rank = Integer.parseInt(firstInstructionMatcher.group("rightRank"));
        rightRoot.symbol = firstInstructionMatcher.group("rightSymbol");

        for (int instructionIndex = 1; instructionIndex < numberOfInstructions; instructionIndex++) {

            String instruction = inputLines.get(instructionIndex);
            Matcher matcher = ADD_PATTERN.matcher(instruction);

            if (!matcher.matches()) {
                throw new IllegalArgumentException();
            }

            int id = Integer.parseInt(matcher.group("id"));

            int leftRank = Integer.parseInt(matcher.group("leftRank"));
            String leftSymbol = matcher.group("leftSymbol");
            int rightRank = Integer.parseInt(matcher.group("rightRank"));
            String rightSymbol = matcher.group("rightSymbol");

            addNode(leftRoot, id, leftRank, leftSymbol);
            addNode(rightRoot, id, rightRank, rightSymbol);

        }

        return readMessage(leftRoot) + readMessage(rightRoot);
    }

    private void addNode(@Nonnull Node currentNode, int id, int rank, String symbol) {

        int currentRank = currentNode.rank;

        if (rank < currentRank) {
            // Left child is relevant

            Node leftChild = currentNode.leftChild;

            if (leftChild != null) {
                addNode(leftChild, id, rank, symbol);
            } else {

                Node newlyCreatedNode = new Node();
                newlyCreatedNode.id = id;
                newlyCreatedNode.rank = rank;
                newlyCreatedNode.symbol = symbol;
                currentNode.leftChild = newlyCreatedNode;

            }

        } else if (rank > currentRank) {

            // Right child is relevant

            Node rightChild = currentNode.rightChild;

            if (rightChild != null) {
                addNode(rightChild, id, rank, symbol);
            } else {

                Node newlyCreatedNode = new Node();
                newlyCreatedNode.id = id;
                newlyCreatedNode.rank = rank;
                newlyCreatedNode.symbol = symbol;
                currentNode.rightChild = newlyCreatedNode;

            }

        } else {
            throw new UnsupportedOperationException("Cannot handle equal ranks");
        }

    }

    private String readMessage(Node root) {

        int treeHeight = height(root);

        StringBuilder[] stringBuilders = IntStream.range(0, treeHeight)
                .mapToObj(_ -> new StringBuilder())
                .toArray(StringBuilder[]::new); // one for each level
        appendSymbols(stringBuilders, root, 0);

        int currentMaxLength = Integer.MIN_VALUE;
        String currentLongestMessage = "";

        for (StringBuilder sb: stringBuilders) {

            if (sb.length() > currentMaxLength) {

                currentMaxLength = sb.length();
                currentLongestMessage = sb.toString();

            }

        }


        return currentLongestMessage;

    }

    private int height(Node node) {
        if (node == null) {
            return 0;
        }
        return Integer.max(height(node.leftChild), height(node.rightChild)) + 1;
    }

    private void appendSymbols(StringBuilder[] stringBuilders, Node currentNode, int currentLevel) {

        if (currentNode != null) {

            StringBuilder sb = stringBuilders[currentLevel];
            sb.append(currentNode.symbol);

            appendSymbols(stringBuilders, currentNode.leftChild, currentLevel + 1);
            appendSymbols(stringBuilders, currentNode.rightChild, currentLevel + 1);

        }

    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int numberOfInstructions = inputLines.size();

        String firstInstruction = inputLines.getFirst();
        Matcher firstInstructionMatcher = ADD_PATTERN.matcher(firstInstruction);

        if (!firstInstructionMatcher.matches()) {
            throw new IllegalArgumentException();
        }

        int rootId = Integer.parseInt(firstInstructionMatcher.group("id"));

        Node leftRoot = new Node();
        leftRoot.id = rootId;
        leftRoot.rank = Integer.parseInt(firstInstructionMatcher.group("leftRank"));
        leftRoot.symbol = firstInstructionMatcher.group("leftSymbol");

        Node rightRoot = new Node();
        rightRoot.id = rootId;
        rightRoot.rank = Integer.parseInt(firstInstructionMatcher.group("rightRank"));
        rightRoot.symbol = firstInstructionMatcher.group("rightSymbol");

        for (int instructionIndex = 1; instructionIndex < numberOfInstructions; instructionIndex++) {

            String instruction = inputLines.get(instructionIndex);
            Matcher addMatcher = ADD_PATTERN.matcher(instruction);
            Matcher swapMatcher = SWAP_PATTERN.matcher(instruction);

            if (addMatcher.matches()) {

                int id = Integer.parseInt(addMatcher.group("id"));

                int leftRank = Integer.parseInt(addMatcher.group("leftRank"));
                String leftSymbol = addMatcher.group("leftSymbol");
                int rightRank = Integer.parseInt(addMatcher.group("rightRank"));
                String rightSymbol = addMatcher.group("rightSymbol");

                addNode(leftRoot, id, leftRank, leftSymbol);
                addNode(rightRoot, id, rightRank, rightSymbol);
            } else if (swapMatcher.matches()) {

                int swapId = Integer.parseInt(swapMatcher.group("id"));
                //log("Swapping nodes with id {}", swapId);

                Node leftNodeToSwap = findNodeById(leftRoot, swapId);
                Node rightNodeToSwap = findNodeById(rightRoot, swapId);

                if (leftNodeToSwap == null || rightNodeToSwap == null) {
                    throw new IllegalStateException();
                }

                int tempRank = leftNodeToSwap.rank;
                String tempSymbol = leftNodeToSwap.symbol;

                leftNodeToSwap.rank = rightNodeToSwap.rank;
                leftNodeToSwap.symbol = rightNodeToSwap.symbol;

                rightNodeToSwap.rank = tempRank;
                rightNodeToSwap.symbol = tempSymbol;

            } else {
                throw new IllegalArgumentException("Invalid instruction: " + instruction);
            }

        }

        return readMessage(leftRoot) + readMessage(rightRoot);
    }

    private @Nullable Node findNodeById(@Nonnull Node node, int id) {
        if (node.id == id) {
            return node;
        }

        if (node.leftChild != null) {
            Node nodeFoundInLeftChild = findNodeById(node.leftChild, id);
            if (nodeFoundInLeftChild != null) {
                return nodeFoundInLeftChild;
            }
        }

        if (node.rightChild != null) {
            return findNodeById(node.rightChild, id);
        }

        return null;

    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        int numberOfInstructions = inputLines.size();

        String firstInstruction = inputLines.getFirst();
        Matcher firstInstructionMatcher = ADD_PATTERN.matcher(firstInstruction);

        if (!firstInstructionMatcher.matches()) {
            throw new IllegalArgumentException();
        }

        int rootId = Integer.parseInt(firstInstructionMatcher.group("id"));

        Node leftRoot = new Node();
        leftRoot.id = rootId;
        leftRoot.rank = Integer.parseInt(firstInstructionMatcher.group("leftRank"));
        leftRoot.symbol = firstInstructionMatcher.group("leftSymbol");

        Node rightRoot = new Node();
        rightRoot.id = rootId;
        rightRoot.rank = Integer.parseInt(firstInstructionMatcher.group("rightRank"));
        rightRoot.symbol = firstInstructionMatcher.group("rightSymbol");

        for (int instructionIndex = 1; instructionIndex < numberOfInstructions; instructionIndex++) {

            String instruction = inputLines.get(instructionIndex);
            Matcher addMatcher = ADD_PATTERN.matcher(instruction);
            Matcher swapMatcher = SWAP_PATTERN.matcher(instruction);

            if (addMatcher.matches()) {

                int id = Integer.parseInt(addMatcher.group("id"));

                int leftRank = Integer.parseInt(addMatcher.group("leftRank"));
                String leftSymbol = addMatcher.group("leftSymbol");
                int rightRank = Integer.parseInt(addMatcher.group("rightRank"));
                String rightSymbol = addMatcher.group("rightSymbol");

                addNode(leftRoot, id, leftRank, leftSymbol);
                addNode(rightRoot, id, rightRank, rightSymbol);
            } else if (swapMatcher.matches()) {

                int swapId = Integer.parseInt(swapMatcher.group("id"));
                //log("Swapping nodes with id {}", swapId);

                SearchWithParentResult node1ToSwap = findNodeWithParent(null, leftRoot, swapId, null, true);
                if (node1ToSwap == null) {
                    node1ToSwap = findNodeWithParent(null, rightRoot, swapId, null, false);
                }

                if (node1ToSwap == null) {
                    throw new IllegalStateException();
                }

                SearchWithParentResult node2ToSwap = findNodeWithParent(null, leftRoot, swapId, node1ToSwap.searchTarget(), true);

                if (node2ToSwap == null) {
                    node2ToSwap = findNodeWithParent(null, rightRoot, swapId, node1ToSwap.searchTarget(), false);
                }

                if (node2ToSwap == null) {
                    throw new IllegalArgumentException();
                }

                Node tempNode = node1ToSwap.searchTarget();

                Node parent1 = node1ToSwap.parent();

                if (parent1 == null) {
                    // This means that node1ToSwap is actually one of the roots (leftRoot if isLeft, otherwise rightRoot)

                    if (node1ToSwap.isLeft()) {
                        leftRoot = node2ToSwap.searchTarget();
                    } else {
                        rightRoot = node2ToSwap.searchTarget();
                    }

                } else {

                    if (node1ToSwap.isLeft()) {
                        parent1.leftChild = node2ToSwap.searchTarget();
                    } else {
                        parent1.rightChild = node2ToSwap.searchTarget();
                    }

                }

                Node parent2 = node2ToSwap.parent();

                if (parent2 == null) {

                    if (node2ToSwap.isLeft()) {
                        leftRoot = tempNode;
                    } else {
                        rightRoot = tempNode;
                    }

                } else {

                    if (node2ToSwap.isLeft()) {
                        parent2.leftChild = tempNode;
                    } else {
                        parent2.rightChild = tempNode;
                    }

                }

            } else {
                throw new IllegalArgumentException("Invalid instruction: " + instruction);
            }

        }

        return readMessage(leftRoot) + readMessage(rightRoot);

    }

    private record SearchWithParentResult(Node parent, Node searchTarget, boolean isLeft) {

    }

    private SearchWithParentResult findNodeWithParent(Node parent, Node node, int id,
                                                      Node firstSearchOccurrence, boolean isLeft) {

        if (node.id == id && node != firstSearchOccurrence) {
            return new SearchWithParentResult(parent, node, isLeft);
        }

        if (node.leftChild != null) {
            SearchWithParentResult searchWithParentResultFromLeftChild = findNodeWithParent(node, node.leftChild, id, firstSearchOccurrence, true);
            if (searchWithParentResultFromLeftChild != null) {
                return searchWithParentResultFromLeftChild;
            }
        }

        if (node.rightChild != null) {
            return findNodeWithParent(node, node.rightChild, id, firstSearchOccurrence, false);
        }

        return null;

    }

}