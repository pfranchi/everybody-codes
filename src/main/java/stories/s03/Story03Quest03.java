package stories.s03;

import common.AbstractQuest;
import common.support.interfaces.Quest03;
import common.support.interfaces.Story03;
import common.support.params.ExecutionParameters;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Story03Quest03 extends AbstractQuest implements Story03, Quest03 {

    private static final Pattern PATTERN = Pattern.compile("^id=(?<id>\\d+), plug=(?<plugColor>[A-Z]+) (?<plugShape>[A-Z]+), " +
                                                           "leftSocket=(?<lsColor>[A-Z]+) (?<lsShape>[A-Z]+), rightSocket=(?<rsColor>[A-Z]+) (?<rsShape>[A-Z]+), data=(?<data>.*)$");

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        Node root = null;

        for (String line: inputLines) {
            Matcher matcher = PATTERN.matcher(line);
            if (!matcher.matches()) {
                throw new IllegalArgumentException("Invalid input line: " + line);
            }

            int id =  Integer.parseInt(matcher.group("id"));
            String plugColor = matcher.group("plugColor");
            String plugShape = matcher.group("plugShape");
            String leftSocketColor = matcher.group("lsColor");
            String leftSocketShape =  matcher.group("lsShape");
            String rightSocketColor = matcher.group("rsColor");
            String rightSocketShape =  matcher.group("rsShape");
            String data =  matcher.group("data");

            log("id = {}, plugColor = {}, plugShape = {}, leftSocketColor = {}, leftSocketShape = {}, rightSocketColor = {}, rightSocketShape = {}, data = {}",
                    id, plugColor, plugShape, leftSocketColor, leftSocketShape, rightSocketColor, rightSocketShape, data);

            Element plug = new Element(plugColor, plugShape);
            Element leftSocket = new Element(leftSocketColor, leftSocketShape);
            Element rightSocket = new Element(rightSocketColor, rightSocketShape);

            Node node = new Node(id, plug, leftSocket, rightSocket);

            if (root == null) {
                root = node;
            } else {
                boolean _ = append(root, node);
            }

        }

        if (root == null) {
            throw new IllegalArgumentException("At least one input row is required");
        }

        List<Integer> ids = new ArrayList<>();
        appendIds(root, ids);

        int checksum = IntStream.rangeClosed(1, ids.size())
                .map(i -> i * ids.get(i - 1)).sum();

        return Integer.toString(checksum);
    }

    private static final class Node {

        private final int id;
        private final Element plug;
        private final Element leftSocket;
        private final Element rightSocket;

        private Node leftChild;
        private BondType leftBond;
        private Node rightChild;
        private BondType rightBond;

        public Node(int id, Element plug, Element leftSocket, Element rightSocket) {
            this.id = id;
            this.plug = plug;
            this.leftSocket = leftSocket;
            this.rightSocket = rightSocket;
        }

        public int getId() {
            return id;
        }

        public Element getPlug() {
            return plug;
        }

        public Element getLeftSocket() {
            return leftSocket;
        }

        public Element getRightSocket() {
            return rightSocket;
        }

        public Node getLeftChild() {
            return leftChild;
        }

        public void setLeftChild(Node leftChild) {
            this.leftChild = leftChild;
        }

        public BondType getLeftBond() {
            return leftBond;
        }

        public void setLeftBond(BondType leftBond) {
            this.leftBond = leftBond;
        }

        public Node getRightChild() {
            return rightChild;
        }

        public void setRightChild(Node rightChild) {
            this.rightChild = rightChild;
        }

        public BondType getRightBond() {
            return rightBond;
        }

        public void setRightBond(BondType rightBond) {
            this.rightBond = rightBond;
        }
    }

    private record Element(String color, String shape) {

        public boolean bondsTo(Element other) {
            return this.equals(other) || this.color.equals(other.color) || this.shape.equals(other.shape);
        }

        public Optional<BondType> getBond(Element other) {
            if (this.equals(other)) {
                return Optional.of(BondType.STRONG);
            }

            if (this.color.equals(other.color) || this.shape.equals(other.shape)) {
                return  Optional.of(BondType.WEAK);
            }

            return Optional.empty();

        }

    }

    private enum BondType {
        WEAK, STRONG
    }

    private boolean append(@Nonnull Node original, Node nodeToAppend) {

        Node leftChild = original.getLeftChild();
        if (leftChild == null) {
            // Check if left socket and plug math
            if (original.getLeftSocket().equals(nodeToAppend.getPlug())) {
                // Append here
                original.setLeftChild(nodeToAppend);
                return true;
            }
        }

        // Explore the left child before processing anything on the right side
        if (leftChild != null) {
            boolean appendedOnTheLeftSide = append(leftChild, nodeToAppend);
            if (appendedOnTheLeftSide) {
                return true;
            }
        }

        Node rightChild = original.getRightChild();
        if (rightChild != null) {
            boolean appendedOnTheRightSide = append(rightChild, nodeToAppend);
            if (appendedOnTheRightSide) {
                return true;
            }
        }

        // Check right socket and plug
        if (rightChild == null) {
            if (original.getRightSocket().equals(nodeToAppend.getPlug())) {
                original.setRightChild(nodeToAppend);
                return true;
            }
        }

        return false;

    }

    private void appendIds(@Nonnull Node node, List<Integer> ids) {

        if (node.getLeftChild() != null) {
            appendIds(node.getLeftChild(), ids);
        }

        ids.add(node.getId());

        if (node.getRightChild() != null) {
            appendIds(node.getRightChild(), ids);
        }

    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        Node root = null;

        for (String line: inputLines) {
            Matcher matcher = PATTERN.matcher(line);
            if (!matcher.matches()) {
                throw new IllegalArgumentException("Invalid input line: " + line);
            }

            int id =  Integer.parseInt(matcher.group("id"));
            String plugColor = matcher.group("plugColor");
            String plugShape = matcher.group("plugShape");
            String leftSocketColor = matcher.group("lsColor");
            String leftSocketShape =  matcher.group("lsShape");
            String rightSocketColor = matcher.group("rsColor");
            String rightSocketShape =  matcher.group("rsShape");
            String data =  matcher.group("data");

            log("id = {}, plugColor = {}, plugShape = {}, leftSocketColor = {}, leftSocketShape = {}, rightSocketColor = {}, rightSocketShape = {}, data = {}",
                    id, plugColor, plugShape, leftSocketColor, leftSocketShape, rightSocketColor, rightSocketShape, data);

            Element plug = new Element(plugColor, plugShape);
            Element leftSocket = new Element(leftSocketColor, leftSocketShape);
            Element rightSocket = new Element(rightSocketColor, rightSocketShape);

            Node node = new Node(id, plug, leftSocket, rightSocket);

            if (root == null) {
                root = node;
            } else {
                boolean _ = appendPart2(root, node);
            }

        }

        if (root == null) {
            throw new IllegalArgumentException("At least one input row is required");
        }

        boolean breakpoint = true;

        List<Integer> ids = new ArrayList<>();
        appendIds(root, ids);

        int checksum = IntStream.rangeClosed(1, ids.size())
                .map(i -> i * ids.get(i - 1)).sum();

        return Integer.toString(checksum);

    }

    private boolean appendPart2(@Nonnull Node original, Node nodeToAppend) {

        Node leftChild = original.getLeftChild();
        if (leftChild == null) {
            // Check if left socket and plug math
            if (original.getLeftSocket().bondsTo(nodeToAppend.getPlug())) {
                // Append here
                original.setLeftChild(nodeToAppend);
                return true;
            }
        }

        // Explore the left child before processing anything on the right side
        if (leftChild != null) {
            boolean appendedOnTheLeftSide = appendPart2(leftChild, nodeToAppend);
            if (appendedOnTheLeftSide) {
                return true;
            }
        }

        Node rightChild = original.getRightChild();
        if (rightChild != null) {
            boolean appendedOnTheRightSide = appendPart2(rightChild, nodeToAppend);
            if (appendedOnTheRightSide) {
                return true;
            }
        }

        // Check right socket and plug
        if (rightChild == null) {
            if (original.getRightSocket().bondsTo(nodeToAppend.getPlug())) {
                original.setRightChild(nodeToAppend);
                return true;
            }
        }

        return false;

    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        Node root = null;

        for (String line: inputLines) {
            Matcher matcher = PATTERN.matcher(line);
            if (!matcher.matches()) {
                throw new IllegalArgumentException("Invalid input line: " + line);
            }

            int id =  Integer.parseInt(matcher.group("id"));
            String plugColor = matcher.group("plugColor");
            String plugShape = matcher.group("plugShape");
            String leftSocketColor = matcher.group("lsColor");
            String leftSocketShape =  matcher.group("lsShape");
            String rightSocketColor = matcher.group("rsColor");
            String rightSocketShape =  matcher.group("rsShape");
            String data =  matcher.group("data");

            log("id = {}, plugColor = {}, plugShape = {}, leftSocketColor = {}, leftSocketShape = {}, rightSocketColor = {}, rightSocketShape = {}, data = {}",
                    id, plugColor, plugShape, leftSocketColor, leftSocketShape, rightSocketColor, rightSocketShape, data);

            Element plug = new Element(plugColor, plugShape);
            Element leftSocket = new Element(leftSocketColor, leftSocketShape);
            Element rightSocket = new Element(rightSocketColor, rightSocketShape);

            Node node = new Node(id, plug, leftSocket, rightSocket);

            if (root == null) {
                root = node;
            } else {
                boolean _ = append(root, node);
            }

        }

        if (root == null) {
            throw new IllegalArgumentException("At least one input row is required");
        }

        List<Integer> ids = new ArrayList<>();
        appendIds(root, ids);

        int checksum = IntStream.rangeClosed(1, ids.size())
                .map(i -> i * ids.get(i - 1)).sum();

        return Integer.toString(checksum);

    }

    private boolean appendPart3(@Nonnull Node root, @Nonnull Node original, Node nodeToAppend) {

        Node leftChild = original.getLeftChild();

        Optional<BondType> optionalLeftBond = original.getLeftSocket().getBond(nodeToAppend.getPlug());

        if (leftChild == null && optionalLeftBond.isPresent()) {

            // Form the first bond here

            BondType leftBond = optionalLeftBond.get();

            original.setLeftChild(nodeToAppend);
            original.setLeftBond(leftBond);

            return true;

        }

        // Explore the left child before processing anything on the right side
        if (leftChild != null) {

            // If the current left child has a WEAK bond and the nodeToAppend forms a STRONG BOND, the current left child gets detached

            if (original.getLeftBond() == BondType.WEAK && optionalLeftBond.isPresent() && optionalLeftBond.get() == BondType.STRONG) {

                Node detached = original.getLeftChild();

                original.setLeftChild(nodeToAppend);
                original.setLeftBond(BondType.STRONG);

                // Reattach the detached node somewhere else.
                // The append process is complete
                return reattachDetachedNode(root, detached, original, true, root, new Phase());

            }

            // Process the left side

            boolean appendedOnTheLeftSide = appendPart3(root, leftChild, nodeToAppend);
            if (appendedOnTheLeftSide) {
                return true;
            }
        }

        Node rightChild = original.getRightChild();

        Optional<BondType> optionalRightBond = original.getRightSocket().getBond(nodeToAppend.getPlug());

        if (rightChild != null) {

            if (original.getRightBond() == BondType.WEAK && optionalRightBond.isPresent() && optionalRightBond.get() == BondType.STRONG) {

                Node detached = original.getRightChild();

                original.setRightChild(nodeToAppend);
                original.setRightBond(BondType.STRONG);

                // Reattach the detached node somewhere else. TODO

                // The append process is complete
                return reattachDetachedNode(root, detached, original, false, root, new Phase());

            }

            boolean appendedOnTheRightSide = appendPart3(root, rightChild, nodeToAppend);
            if (appendedOnTheRightSide) {
                return true;
            }
        }

        // Check right socket and plug
        if (rightChild == null &&  optionalRightBond.isPresent()) {

            original.setRightChild(nodeToAppend);
            original.setRightBond(optionalRightBond.get());

            return true;

        }

        return false;

    }

    private boolean reattachDetachedNode(Node root, Node detached, Node previousParentOfTheDetached,
                                         boolean detachmentHappenedOnTheLeft, Node currentCandidate, Phase phase) {

        if (currentCandidate.getId() == previousParentOfTheDetached.getId()) {

            // We reached the previous parent of the detached

            if (phase.inExploration) {

                phase.inExploration = false; // Change from exploration phase to attachment phase



            } else {

                // Circled the whole tree and could not reattach. Reattachment was not successful

                // Check the left node if the detachment happened on the right

                return false;

            }

        }

        return false;

    }

    private static final class Phase {

        private boolean inExploration;

    }

}
