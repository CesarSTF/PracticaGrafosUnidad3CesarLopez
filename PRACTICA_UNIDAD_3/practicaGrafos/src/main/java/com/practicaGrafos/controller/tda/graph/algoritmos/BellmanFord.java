package com.practicaGrafos.controller.tda.graph.algoritmos;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.practicaGrafos.controller.tda.graph.Adycencia;
import com.practicaGrafos.controller.tda.graph.GrapLabelNoDirect;
import com.practicaGrafos.controller.tda.list.LinkedList;

import java.util.Arrays;

public class BellmanFord {
    private GrapLabelNoDirect<String> graph;
    private int source;
    private float[] distances;
    private int[] predecessors;

    public BellmanFord(GrapLabelNoDirect<String> graph, int source, int destination) {
        this.graph = graph;
        this.source = source;
        int n = graph.nro_Ver();
        this.distances = new float[n + 1];
        this.predecessors = new int[n + 1];
    }

    public String caminoCorto(int destination) throws Exception {
        int n = graph.nro_Ver();

        Arrays.fill(distances, Float.MAX_VALUE);
        distances[source] = 0;
        Arrays.fill(predecessors, -1);

        for (int i = 1; i < n; i++) {
            for (int u = 1; u <= n; u++) {
                LinkedList<Adycencia> adyacencias = graph.adyacencias(u);
                for (int j = 0; j < adyacencias.getSize(); j++) {
                    Adycencia adyacencia = adyacencias.get(j);
                    int v = adyacencia.getDestination();
                    float peso = adyacencia.getWeight();
                    if (distances[u] != Float.MAX_VALUE && distances[u] + peso < distances[v]) {
                        distances[v] = distances[u] + peso;
                        predecessors[v] = u;

                        System.out.println("Predecesor de " + v + " es " + u);
                    }
                }
            }
        }

        for (int u = 1; u <= n; u++) {
            LinkedList<Adycencia> adyacencias = graph.adyacencias(u);
            for (int j = 0; j < adyacencias.getSize(); j++) {
                Adycencia adyacencia = adyacencias.get(j);
                int v = adyacencia.getDestination();
                float peso = adyacencia.getWeight();
                if (distances[u] != Float.MAX_VALUE && distances[u] + peso < distances[v]) {
                    return "El graph tiene un ciclo negativo";
                }
            }
        }

        return reconstruirCamino(source, destination);
    }

    private String reconstruirCamino(int source, int destination) throws Exception {
        if (distances[destination] == Float.MAX_VALUE) {
            return "No hay camino";
        }

        StringBuilder camino = new StringBuilder();
        int actual = destination;

        while (actual != -1) {
            String nombre = graph.getLabelL(actual);
            camino.insert(0, nombre + "(" + actual + ")");

            if (predecessors[actual] != -1) {
                camino.insert(0, " <- "); 
            }
            actual = predecessors[actual];
        }
        return camino.toString();

    }

    public JsonObject getMatrices() {
        JsonObject matrix = new JsonObject();

        JsonArray distanciasArray = new JsonArray();
        for (int i = 1; i < distances.length; i++) {
            distanciasArray.add(distances[i]);
        }
        matrix.add("distances", distanciasArray);

        JsonArray predecesoresArray = new JsonArray();
        for (int i = 1; i < predecessors.length; i++) {
            predecesoresArray.add(predecessors[i]);
        }
        matrix.add("predecessors", predecesoresArray);
        return matrix;
    }
}
