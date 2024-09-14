import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Graph {

    public final HashMap<String, List<Edge>> graph;

    public Graph() {
        this.graph = new HashMap<>();
    }

    static class Edge {
        String from, to;
        int weight, plantCost;;

        public Edge(String from, String to, int weight, int plantCost) { // Modify constructor
            this.from = from;
            this.to = to;
            this.weight = weight;
            this.plantCost = plantCost; // Initialize plantCost
        }
    }

    static class Path implements Comparable<Path> {
        String lastNode;
        int weight;
        List<String> nodes;

        public Path(String lastNode, int weight, List<String> nodes) {
            this.lastNode = lastNode;
            this.weight = weight;
            this.nodes = nodes;
        }

        @Override
        public int compareTo(Path other) {
            return Integer.compare(this.weight, other.weight);
        }

        // Possibly add a method to add a node to the path, updating the weight
        // accordingly
        public void addNode(String newNode, int edgeWeight, int plantCost) {
            this.nodes.add(newNode);
            this.lastNode = newNode;
            this.weight += edgeWeight + plantCost;
        }
    }

    public void addEdge(String from, String to, int weight, int plantCost) {
        // System.out.println("from:" + from + ",to:" + to + ",from:" + from.isEmpty() +
        // ",to:" + to.isEmpty() + ",from:"
        // + from.length() + ",to:" + to.length());
        if (from == null || to == null)
            return;
        if (from.isEmpty() || to.isEmpty() || to.equals(" ")
                || from.equals(" ") || from.length() == 2 || to.length() == 2) {
            // System.out.println("Warning: Skipping edge with empty node(s): " + from +
            // "->" + to);
            return;
        }
        this.graph.putIfAbsent(from, new ArrayList<>());
        for (Edge existingEdge : this.graph.get(from)) {
            if (existingEdge.to.equals(to) && existingEdge.weight == weight) {
                // System.out.println("Warning: Skipping duplicate edge: " + from + " -> " + to
                // + " with weight " + weight);
                return;
            }
        }
        this.graph.get(from).add(new Edge(from, to, weight, plantCost));
        // System.out.println("Added edge: " + from + " -> " + to + " with weight " +
        // weight);
    }

    public void updateEdgeWeight(String from, String to, int newWeight) {
        if (graph.containsKey(from)) {
            for (Edge edge : graph.get(from)) {
                if (edge.to.equals(to)) {
                    edge.weight = newWeight;
                    return;
                }
            }
        }
        throw new IllegalArgumentException("Edge from " + from + " to " + to + " not found!");
    }

    public void printGraph() {
        for (Map.Entry<String, List<Edge>> entry : this.graph.entrySet()) {
            String fromNode = entry.getKey();
            List<Edge> edges = entry.getValue();

            for (Edge edge : edges) {
                System.out.println("From: " + edge.from + ", To: " + edge.to + ", Weight: " + edge.weight
                        + ", plantCost:" + edge.plantCost);
            }
        }
    }

}