package events.y2026;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import com.google.common.graph.Traverser;
import common.AbstractQuest;
import common.support.interfaces.MainEvent2026;
import common.support.interfaces.Quest06;
import common.support.params.ExecutionParameters;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static events.y2026.EC2026Quest06.NodeSelector.ingredientSelector;
import static events.y2026.EC2026Quest06.NodeSelector.itemSelector;
import static java.util.function.Function.identity;

@SuppressWarnings("UnstableApiUsage")
public class EC2026Quest06 extends AbstractQuest implements MainEvent2026, Quest06 {

    @Override
    protected String solvePart1(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        ImmutableGraph<String> graph = createGraph(inputLines, identity(), itemSelector(), ingredientSelector());

        Traverser<String> traverser = Traverser.forGraph(graph);

        Set<String> distinctIngredients = Sets.newTreeSet(traverser.breadthFirst("NULL"));
        distinctIngredients.remove("NULL");

        return Integer.toString(distinctIngredients.size());
    }

    @Override
    protected String solvePart2(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        // Implementation identical to part 1 except that the graph's direction is flipped

        ImmutableGraph<String> graph = createGraph(inputLines, identity(), ingredientSelector(), itemSelector());

        Traverser<String> traverser = Traverser.forGraph(graph);

        Set<String> distinctIngredients = Sets.newTreeSet(traverser.breadthFirst("NULL"));
        distinctIngredients.remove("NULL");

        return Integer.toString(distinctIngredients.size());
    }

    private record Ingredient(String name) {
        public boolean isNull() {
            return name.startsWith("NULL");
        }
    }

    @Override
    protected String solvePart3(String input, List<String> inputLines, ExecutionParameters executionParameters) {

        ImmutableGraph<Ingredient> graph = createGraph(inputLines, Ingredient::new, ingredientSelector(), itemSelector());

        Multimap<Ingredient, Ingredient> derivedProductsToNullIngredients = HashMultimap.create();

        for (Ingredient nullIngredient: graph.nodes()) {
            if (nullIngredient.isNull()) {

                Traverser<Ingredient> traverser = Traverser.forGraph(graph);
                Set<Ingredient> derivedIngredients = Sets.newHashSet(traverser.breadthFirst(nullIngredient));
                derivedIngredients.remove(nullIngredient);
                derivedIngredients.forEach(derivedIngredient -> derivedProductsToNullIngredients.put(derivedIngredient, nullIngredient));

            }
        }

        int totalCount = (int) derivedProductsToNullIngredients
                .asMap()
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() > 1)
                .count();

        return Integer.toString(totalCount);
    }

    @FunctionalInterface
    interface NodeSelector<T> {
        T selectNode(T item, T ingredient);

        static <S> NodeSelector<S> itemSelector() {
            return (item, _) -> item;
        }

        static <S> NodeSelector<S> ingredientSelector() {
            return (_, ingredient) -> ingredient;
        }
    }

    private <T> ImmutableGraph<T> createGraph(List<String> inputLines,
                                              Function<String, T> nameToObj,
                                              NodeSelector<T> nodeUSelector,
                                              NodeSelector<T> nodeVSelector) {

        ImmutableGraph.Builder<T> graphBuilder = GraphBuilder.directed().immutable();

        for (String line: inputLines) {
            String[] parts = line.split(":");

            String itemName = parts[0];
            T item = nameToObj.apply(itemName);

            for (String ingredientName: parts[1].split(",")) {

                T ingredient = nameToObj.apply(ingredientName);

                T nodeU = nodeUSelector.selectNode(item, ingredient);
                T nodeV = nodeVSelector.selectNode(item, ingredient);

                graphBuilder.putEdge(nodeU, nodeV);
            }

        }

        return graphBuilder.build();

    }

}
