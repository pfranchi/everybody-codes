package stories.s03;

import common.AbstractQuest;
import common.support.interfaces.Quest03;
import common.support.interfaces.Story03;
import common.support.params.ExecutionParameters;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
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
        Map<Integer, Node> nodesById = new HashMap<>();

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

            Element plug = new Element(plugColor, plugShape);
            Element leftSocket = new Element(leftSocketColor, leftSocketShape);
            Element rightSocket = new Element(rightSocketColor, rightSocketShape);

            Node node = new Node(id, plug, leftSocket, rightSocket);
            nodesById.put(id, node);

            if (root == null) {
                root = node;
            } else {
                appendPart3(nodesById, root, node);
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

    private void appendPart3(@Nonnull Map<Integer, Node> nodesById, @Nonnull Node root, Node nodeToAppend) {

        Traverser traverser = Traverser.initialize(nodesById, root);

        while (traverser.hasNext()) {

            SocketBond socketBond = traverser.next();
            int sourceNodeId = socketBond.sourceNodeId();
            Node sourceNode = nodesById.get(sourceNodeId);
            Side side = socketBond.side();
            @Nullable Bond originalBond = socketBond.bond();

            Element socketInTheSource = side == Side.LEFT ? sourceNode.getLeftSocket() : sourceNode.getRightSocket();

            Optional<BondType> potentialBondTypeWithNewNode = socketInTheSource.getBond(nodeToAppend.getPlug());

            if (potentialBondTypeWithNewNode.isPresent()) {
                // Can bond here!

                BondType bondTypeWithNewNode = potentialBondTypeWithNewNode.get();

                if (originalBond.isEmptyBond()) {
                    // This socket was empty. Attach the node here and do nothing else

                    if (side == Side.LEFT) {
                        sourceNode.setLeftChild(nodeToAppend);
                        sourceNode.setLeftBond(bondTypeWithNewNode);
                    } else {
                        sourceNode.setRightChild(nodeToAppend);
                        sourceNode.setRightBond(bondTypeWithNewNode);
                    }

                    return;

                } else if (originalBond.type() == BondType.WEAK && bondTypeWithNewNode == BondType.STRONG) {

                    // Detachment of the original child and chain reaction

                    Node detachedChild = side == Side.LEFT ?  sourceNode.getLeftChild() : sourceNode.getRightChild();
                    performChainReactionAfterDetachment(nodesById, root, sourceNode, nodeToAppend, detachedChild, side);

                    return;

                }

            }

        }

    }

    private void performChainReactionAfterDetachment(@Nonnull Map<Integer, Node> nodesById, @Nonnull Node root, Node strongBondSourceNode,
                                                     Node strongBondNodeToAppend, Node weakBondDetachedNode, Side detachmentSide) {

        // Perform the detachment, so that the socket remains empty.

        if (detachmentSide == Side.LEFT) {
            strongBondSourceNode.setLeftChild(null);
            strongBondSourceNode.setLeftBond(null);
        } else {
            strongBondSourceNode.setRightChild(null);
            strongBondSourceNode.setRightBond(null);
        }

        // Create a traverser and bring it to the position after the detachment

        Traverser traverser = Traverser.initialize(nodesById, root);

        while (traverser.hasNext()) {
            SocketBond socketBond = traverser.next();

            int traversalSourceNodeId = socketBond.sourceNodeId();
            Side traversalSide = socketBond.side();

            if (traversalSourceNodeId == strongBondSourceNode.getId() && traversalSide == detachmentSide) {
                break;
            }

        }

        // Perform the attachment of the node that forms the strong bond

        if (detachmentSide == Side.LEFT) {
            strongBondSourceNode.setLeftChild(strongBondNodeToAppend);
            strongBondSourceNode.setLeftBond(BondType.STRONG);
        } else {
            strongBondSourceNode.setRightChild(strongBondNodeToAppend);
            strongBondSourceNode.setRightBond(BondType.STRONG);
        }

        // Iterate to find the location where the detached node can be reattached (same code as appendPart3)

        while (traverser.hasNext()) {

            SocketBond socketBond = traverser.next();
            int sourceNodeId = socketBond.sourceNodeId();
            Node sourceNode = nodesById.get(sourceNodeId);
            Side side = socketBond.side();
            @Nullable Bond originalBond = socketBond.bond();

            Element socketInTheSource = side == Side.LEFT ? sourceNode.getLeftSocket() : sourceNode.getRightSocket();

            Optional<BondType> potentialBondTypeWithNewNode = socketInTheSource.getBond(weakBondDetachedNode.getPlug());

            if (potentialBondTypeWithNewNode.isPresent()) {
                // Can bond here!

                BondType bondTypeWithNewNode = potentialBondTypeWithNewNode.get();

                if (originalBond.isEmptyBond()) {
                    // This socket was empty. Attach the node here and do nothing else

                    if (side == Side.LEFT) {
                        sourceNode.setLeftChild(weakBondDetachedNode);
                        sourceNode.setLeftBond(bondTypeWithNewNode);
                    } else {
                        sourceNode.setRightChild(weakBondDetachedNode);
                        sourceNode.setRightBond(bondTypeWithNewNode);
                    }

                    return;

                } else if (originalBond.type() == BondType.WEAK && bondTypeWithNewNode == BondType.STRONG) {

                    // Detachment of the original child and chain reaction

                    Node detachedChild = side == Side.LEFT ?  sourceNode.getLeftChild() : sourceNode.getRightChild();
                    performChainReactionAfterDetachment(nodesById, root, sourceNode, weakBondDetachedNode, detachedChild, side);

                    return;

                }

            }

        }

    }

    private enum Side { LEFT, RIGHT }
    private record Bond(int sourceNodeId, int destinationNodeId, BondType type) {

        public static Bond noBond(int sourceNodeId) {
            return new Bond(sourceNodeId, -1, null);
        }

        public boolean isEmptyBond() {
            return type == null;
        }

    }
    private record SocketBond(int sourceNodeId, Side side, Element socket, @Nonnull Bond bond) {}

    private static final class Traverser implements Iterator<SocketBond> {

        private final Map<Integer, Node> nodesById;
        private final Node root;
        private final Deque<SocketBond> stages = new ArrayDeque<>();

        private Traverser(Map<Integer, Node> nodesById, Node root) {
            this.nodesById = nodesById;
            this.root = root;
        }

        public static Traverser initialize(Map<Integer, Node> nodesById, Node root) {

            Traverser traverser =  new Traverser(nodesById, root);
            traverser.setToStartingPosition();
            return traverser;
        }

        private void setToStartingPosition() {

            stages.clear();

            int rootId = root.getId();
            Node leftChild = root.getLeftChild();
            Bond leftBond;
            if (leftChild == null) {
                leftBond = Bond.noBond(rootId);
            } else {
                leftBond = new Bond(rootId, leftChild.getId(), root.getLeftBond());
            }
            SocketBond rootLeftBond = new SocketBond(root.getId(), Side.LEFT, root.getLeftSocket(), leftBond);

            stages.add(rootLeftBond);

        }

        @Override
        public boolean hasNext() {
            return !stages.isEmpty();
        }

        @Override
        public SocketBond next() {

            SocketBond toReturn = stages.pop();

            Side side = toReturn.side();
            @Nonnull Bond bond = toReturn.bond();

            int sourceNodeId = toReturn.sourceNodeId();
            Node sourceNode = nodesById.get(sourceNodeId);

            if (side == Side.LEFT) {

                if (bond.isEmptyBond()) {
                    // Nothing on the left. Move to the right of this same node

                    Bond rightBond = Bond.noBond(sourceNodeId);
                    if (sourceNode.getRightChild() != null) {
                        rightBond = new Bond(sourceNodeId, sourceNode.getRightChild().getId(), sourceNode.getRightBond());
                    }

                    SocketBond socketBond = new SocketBond(sourceNodeId, Side.RIGHT, sourceNode.getRightSocket(), rightBond);

                    stages.push(socketBond);

                } else {

                    // Readd the removed socket bond and move to the left socket of the left child
                    stages.push(toReturn);

                    Node leftChild = sourceNode.getLeftChild();
                    assert leftChild != null;

                    Bond nextBond = Bond.noBond(leftChild.getId());
                    if (leftChild.getLeftChild() != null) {
                        nextBond = new Bond(leftChild.getId(), leftChild.getLeftChild().getId(), leftChild.getLeftBond());
                    }

                    SocketBond socketBond = new SocketBond(leftChild.getId(), Side.LEFT, leftChild.getLeftSocket(), nextBond);

                    stages.push(socketBond);

                }

            } else {

                if (bond.isEmptyBond()) {

                    // Remove all right side explorations until we encounter a left, or we completely drain the stack
                    boolean continueRemoval = true;
                    SocketBond leftSocketBondRemoved = null;
                    while (continueRemoval && !stages.isEmpty()) {

                        SocketBond removedSocketBond = stages.pop();
                        if (removedSocketBond.side() == Side.LEFT) {
                            leftSocketBondRemoved = removedSocketBond;
                            continueRemoval = false;
                        }

                    }

                    // If the stack is empty and we did not encounter a left socket, it means that we reached the root from the right side
                    // and this is the end of the iteration. In this case leftSocketBondRemoved is null. Go back to the starting position

                    // Otherwise, we must have encountered a left, therefore leftSocketBondRemoved is not null. Add its corresponding right
                    // socket as the next exploration stage.

                    if (leftSocketBondRemoved == null) {
                        setToStartingPosition();
                    } else {

                        int removedLeftSourceNodeId = leftSocketBondRemoved.sourceNodeId();
                        Node removedLeftSourceNode = nodesById.get(removedLeftSourceNodeId);

                        Bond rightBond = Bond.noBond(removedLeftSourceNodeId);
                        if (removedLeftSourceNode.getRightChild() != null) {
                            rightBond = new Bond(removedLeftSourceNodeId, removedLeftSourceNode.getRightChild().getId(), removedLeftSourceNode.getRightBond());
                        }

                        SocketBond socketBond = new SocketBond(removedLeftSourceNodeId, Side.RIGHT, removedLeftSourceNode.getRightSocket(), rightBond);

                        stages.push(socketBond);

                    }

                } else {

                    // Readd the removed socket bond and move to the left socket of the right child
                    stages.push(toReturn);

                    Node rightChild = sourceNode.getRightChild();
                    assert rightChild != null;

                    Bond nextBond = Bond.noBond(rightChild.getId());
                    if (rightChild.getLeftChild() != null) {
                        nextBond = new Bond(rightChild.getId(), rightChild.getLeftChild().getId(), rightChild.getLeftBond());
                    }

                    SocketBond socketBond = new SocketBond(rightChild.getId(), Side.LEFT, rightChild.getLeftSocket(), nextBond);

                    stages.push(socketBond);

                }

            }

            return toReturn;
        }
    }

}
