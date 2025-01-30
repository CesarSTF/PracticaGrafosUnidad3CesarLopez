package com.practicaGrafos.controller.tda.graph.algoritmos;

import com.practicaGrafos.controller.tda.graph.Adycencia;
import com.practicaGrafos.controller.tda.graph.GrapLabelNoDirect;
import com.practicaGrafos.controller.tda.list.LinkedList;

public class BellmanFord {
    private GrapLabelNoDirect<String> graph;
    private int source;
    private LinkedList<Float> distances;
    private LinkedList<Integer> predecessors;

    public BellmanFord(GrapLabelNoDirect<String> graph, int source, int destination) {
        this.graph = graph;
        this.source = source;
        int n = graph.nro_Ver();
        this.distances = new LinkedList<>();
        this.predecessors = new LinkedList<>();

        for (int i = 0; i <= n; i++) {
            distances.add(Float.MAX_VALUE);
            predecessors.add(-1);
        }
    }

    public String caminoCorto(int destination) throws Exception {
        int n = graph.nro_Ver();

        distances.set(source, 0f);

        for (int i = 1; i < n; i++) {
            for (int u = 1; u <= n; u++) {
                LinkedList<Adycencia> adyacencias = graph.adyacencias(u);
                for (int j = 0; j < adyacencias.getSize(); j++) {
                    Adycencia adyacencia = adyacencias.get(j);
                    int v = adyacencia.getDestination();
                    float peso = adyacencia.getWeight();
                    if (distances.get(u) != Float.MAX_VALUE && distances.get(u) + peso < distances.get(v)) {
                        distances.set(v, distances.get(u) + peso);
                        predecessors.set(v, u);

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
                if (distances.get(u) != Float.MAX_VALUE && distances.get(u) + peso < distances.get(v)) {
                    return "El graph tiene un ciclo negativo";
                }
            }
        }

        return reconstruirCamino(source, destination);
    }

    private String reconstruirCamino(int source, int destination) throws Exception {
        if (distances.get(destination) == Float.MAX_VALUE) {
            return "No hay camino";
        }

        StringBuilder camino = new StringBuilder();
        int actual = destination;

        while (actual != -1) {
            String nombre = graph.getLabelL(actual);
            camino.insert(0, nombre + "(" + actual + ")");

            if (predecessors.get(actual) != -1) {
                camino.insert(0, " <- ");
            }
            actual = predecessors.get(actual);
        }
        return camino.toString();
    }

    public String getMatrices() throws Exception {
        StringBuilder matrices = new StringBuilder();
        matrices.append("Distancias: ");
        for (int i = 1; i < distances.getSize(); i++) {
            matrices.append(distances.get(i)).append(" ");
        }
        matrices.append("\nPredecesores: ");
        for (int i = 1; i < predecessors.getSize(); i++) {
            matrices.append(predecessors.get(i)).append(" ");
        }
        return matrices.toString();
    }
}