package com.practicaGrafos.rest;

import java.util.Arrays;
import java.util.LinkedList;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/graph-algorithms")
public class GraphAlgorithmsResource {

    // Método para ejecutar el algoritmo de Floyd-Warshall
    private static void executeFloydWarshall(int[][] graph) {
        int V = graph.length;
        int[][] dist = Arrays.stream(graph).map(int[]::clone).toArray(int[][]::new);

        for (int k = 0; k < V; k++) {
            for (int i = 0; i < V; i++) {
                for (int j = 0; j < V; j++) {
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }
    }

    // Método para ejecutar el algoritmo de Bellman-Ford
    private static void executeBellmanFord(LinkedList<int[]> edges, int V, int src) {
        int[] dist = new int[V];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[src] = 0;

        for (int i = 1; i < V; i++) {
            for (int[] edge : edges) {
                int u = edge[0], v = edge[1], weight = edge[2];
                if (dist[u] != Integer.MAX_VALUE && dist[u] + weight < dist[v]) {
                    dist[v] = dist[u] + weight;
                }
            }
        }
    }

    // Método para medir el tiempo de ejecución de un algoritmo
    private static long measureExecutionTime(Runnable algorithm) {
        long startTime = System.nanoTime();
        algorithm.run();
        return System.nanoTime() - startTime;
    }

    // Método para generar un grafo aleatorio
    private static int[][] generateRandomGraph(int size) {
        int[][] graph = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                graph[i][j] = (i == j) ? 0 : (int) (Math.random() * 10);
            }
        }
        return graph;
    }

    // Método para generar una lista de aristas a partir de un grafo
    private static LinkedList<int[]> generateEdgesFromGraph(int[][] graph) {
        LinkedList<int[]> edges = new LinkedList<>();
        for (int i = 0; i < graph.length; i++) {
            for (int j = 0; j < graph[i].length; j++) {
                if (graph[i][j] != 0) {
                    edges.add(new int[]{i, j, graph[i][j]});
                }
            }
        }
        return edges;
    }

    // Método para formatear la salida con un marco visual
    private static String formatOutput(String title, String content) {
        StringBuilder frame = new StringBuilder();
        frame.append("********************************************\n");
        frame.append("* ").append(String.format("%-40s", title)).append(" *\n");
        frame.append("********************************************\n");
        frame.append("* ").append(String.format("%-40s", content)).append(" *\n");
        frame.append("********************************************\n");
        return frame.toString();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String compareAlgorithms() {
        StringBuilder result = new StringBuilder();

        // Pruebas con grafos de diferentes tamaños
        int[] sizes = {10, 20, 30};
        for (int size : sizes) {
            int[][] graph = generateRandomGraph(size);
            LinkedList<int[]> edges = generateEdgesFromGraph(graph);

            long floydTime = measureExecutionTime(() -> executeFloydWarshall(graph));
            long bellmanTime = measureExecutionTime(() -> executeBellmanFord(edges, size, 0));

            String title = "Resultados para " + size + " nodos";
            String content = "Floyd-Warshall: " + floydTime + " ns\n" +
                             "Bellman-Ford:   " + bellmanTime + " ns";
            result.append(formatOutput(title, content)).append("\n");
        }

        return result.toString();
    }
}