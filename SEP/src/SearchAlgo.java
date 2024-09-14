import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;

public class SearchAlgo {

    public Set<Map.Entry<List<String>, Integer>> kShortestPaths(Map<String, List<Graph.Edge>> graph, String start,
            String end, int K) {
        Set<Map.Entry<List<String>, Integer>> result = new HashSet<>();
        PriorityQueue<Graph.Path> pq = new PriorityQueue<>();

        pq.add(new Graph.Path(start, 0, Arrays.asList(start)));

        while (K > 0 && !pq.isEmpty()) {
            Graph.Path currentPath = pq.poll();
            String lastNode = currentPath.lastNode;
            if (lastNode.equals(end)) {
                result.add(new AbstractMap.SimpleEntry<>(currentPath.nodes, currentPath.weight));
                K--;
            }
            List<Graph.Edge> neighbours = graph.get(lastNode);
            if (neighbours != null) {
                for (Graph.Edge edge : neighbours) {
                    // int newPathWeight = currentPath.weight + edge.weight + edge.plantCost; //
                    // Include plantCost in the
                    // path
                    // weight
                    int newPathWeight = currentPath.weight + edge.weight; // always add the edge weight
                    // Only add the plantCost if the node is not the start or end node
                    if (!edge.to.equals(start) && !edge.to.equals(end)) {
                        newPathWeight += edge.plantCost;
                    }
                    List<String> newPathNodes = new ArrayList<>(currentPath.nodes);
                    newPathNodes.add(edge.to);
                    Graph.Path newPath = new Graph.Path(edge.to, newPathWeight, newPathNodes);
                    pq.add(newPath);
                }
            }
        }
        return result;
    }

    public Set<Map.Entry<List<String>, Integer>> kShortestPaths2(Map<String, List<Graph.Edge>> graph, String startNode,
            List<String> endNodes, int K) {
        // Step 1: Find k shortest paths from start to each end.
        Map<String, Set<Map.Entry<List<String>, Integer>>> allPaths = new HashMap<>();
        for (String endNode : endNodes) {
            Set<Map.Entry<List<String>, Integer>> pathsToEndNode = kShortestPaths(graph, startNode, endNode, K);
            allPaths.put(endNode, pathsToEndNode);
        }

        // Step 2: Generate all possible path combinations, handling any overlapping
        // path segments.
        PriorityQueue<Map.Entry<List<String>, Integer>> allPathCombinations = new PriorityQueue<>(
                Comparator.comparingInt(Map.Entry::getValue));
        for (Map.Entry<List<String>, Integer> path1 : allPaths.get(endNodes.get(0))) {
            for (Map.Entry<List<String>, Integer> path2 : allPaths.get(endNodes.get(1))) {
                // Calculate the combined cost of path1 and path2.
                int combinedCost = calculateCombinedPathCost(graph, path1.getKey(), path2.getKey());
                List<String> combinedPath = new ArrayList<>(path1.getKey());
                combinedPath.addAll(path2.getKey());
                allPathCombinations.add(new AbstractMap.SimpleEntry<>(combinedPath, combinedCost));
            }
        }

        // Step 3: Find the k path combinations with the lowest total cost.
        Set<Map.Entry<List<String>, Integer>> kShortestPathCombinations = new HashSet<>();
        for (int i = 0; i < K && !allPathCombinations.isEmpty(); i++) {
            kShortestPathCombinations.add(allPathCombinations.poll());
        }

        return kShortestPathCombinations;
    }

    private int calculateCombinedPathCost(Map<String, List<Graph.Edge>> graph, List<String> path1, List<String> path2) {
        int cost = 0;
        Set<String> visitedNodes = new HashSet<>();
        Set<String> visitedEdges = new HashSet<>();

        // Calculate cost for path1.
        cost = getCost(graph, path1, cost, visitedNodes, visitedEdges);

        // Calculate cost for path2.
        cost = getCost(graph, path2, cost, visitedNodes, visitedEdges);

        return cost;
    }

    private int getCost(Map<String, List<Graph.Edge>> graph, List<String> path1, int cost, Set<String> visitedNodes, Set<String> visitedEdges) {
        for (int i = 0; i < path1.size() - 1; i++) {
            String currentNode = path1.get(i);
            String nextNode = path1.get(i + 1);

            // Add node cost if it has not been visited yet and is not the start node.
            if (!visitedNodes.contains(currentNode) && i != 0) {
                cost += graph.get(currentNode).stream()
                        .filter(edgeToNextNode -> edgeToNextNode.to.equals(nextNode))
                        .findFirst().get().plantCost;
                visitedNodes.add(currentNode);
            }

            // Add edge cost if it has not been visited yet.
            String edge = currentNode + "->" + nextNode;
            if (!visitedEdges.contains(edge)) {
                cost += graph.get(currentNode).stream()
                        .filter(edgeToNextNode -> edgeToNextNode.to.equals(nextNode))
                        .findFirst().get().weight;
                visitedEdges.add(edge);
            }
        }
        return cost;
    }
}
