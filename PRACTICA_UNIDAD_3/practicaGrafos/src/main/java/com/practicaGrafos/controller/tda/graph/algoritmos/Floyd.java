package com.practicaGrafos.controller.tda.graph.algoritmos;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.practicaGrafos.controller.tda.graph.GrapLabelNoDirect;

public class Floyd {
    private GrapLabelNoDirect<String> grafo;
    private int origen;
    private int destino;
    private float[][] distancias;
    private int[][] siguiente;

    public Floyd(GrapLabelNoDirect<String> grafo, int origen, int destino) {
        this.grafo = grafo;
        this.origen = origen;
        this.destino = destino;
        int n = grafo.nro_Ver();
        this.distancias = new float[n + 1][n + 1];
        this.siguiente = new int[n + 1][n + 1];
        inicializarMatrices(n);
    }

    private void inicializarMatrices(int n) {
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= n; j++) {
                if (i == j) {
                    distancias[i][j] = 0;
                    siguiente[i][j] = -1;
                } else {
                    try {
                        float peso = grafo.getWeigth2(i, j);
                        if (Float.isNaN(peso) || peso <= 0) {
                            distancias[i][j] = Float.MAX_VALUE;
                            siguiente[i][j] = -1;
                        } else {
                            distancias[i][j] = peso;
                            siguiente[i][j] = j;
                        }
                    } catch (Exception e) {
                        distancias[i][j] = Float.MAX_VALUE;
                        siguiente[i][j] = -1;
                    }
                }
            }
        }
    }

    public String caminoCorto() throws Exception {
        int n = grafo.nro_Ver();
        aplicarFloydWarshall(n);
        return reconstruirCamino(origen, destino);
    }

    private void aplicarFloydWarshall(int n) {
        for (int k = 1; k <= n; k++) {
            for (int i = 1; i <= n; i++) {
                for (int j = 1; j <= n; j++) {
                    if (distancias[i][k] != Float.MAX_VALUE && distancias[k][j] != Float.MAX_VALUE &&
                            distancias[i][k] + distancias[k][j] < distancias[i][j]) {
                        distancias[i][j] = distancias[i][k] + distancias[k][j];
                        siguiente[i][j] = siguiente[i][k];
                    }
                }
            }
        }
    }

    private String reconstruirCamino(int origen, int destino) {
        if (siguiente[origen][destino] == -1) {
            return "No hay camino";
        }

        StringBuilder camino = new StringBuilder();
        int actual = origen;
        float distanciaTotal = 0;

        while (actual != destino) {
            if (siguiente[actual][destino] == -1) {
                return "Error: Camino interrumpido inesperadamente.";
            }
            camino.append(actual).append(" -> ");
            distanciaTotal += distancias[actual][siguiente[actual][destino]];
            actual = siguiente[actual][destino];
        }
        camino.append(destino);
        distanciaTotal += distancias[actual][destino];

        System.out.println("Distancia total recorrida: " + distanciaTotal);

        return "Camino: " + camino.toString() + "|" + "Distancia total: " + distanciaTotal;
    }

    public JsonObject getMatrices() {
        JsonObject matrices = new JsonObject();
        matrices.add("distancias", crearJsonArray(distancias));
        matrices.add("siguiente", crearJsonArray(siguiente));
        return matrices;
    }

    private JsonArray crearJsonArray(float[][] matriz) {
        JsonArray array = new JsonArray();
        for (int i = 1; i < matriz.length; i++) {
            JsonArray fila = new JsonArray();
            for (int j = 1; j < matriz[i].length; j++) {
                fila.add(matriz[i][j]);
            }
            array.add(fila);
        }
        return array;
    }

    private JsonArray crearJsonArray(int[][] matriz) {
        JsonArray array = new JsonArray();
        for (int i = 1; i < matriz.length; i++) {
            JsonArray fila = new JsonArray();
            for (int j = 1; j < matriz[i].length; j++) {
                fila.add(matriz[i][j]);
            }
            array.add(fila);
        }
        return array;
    }
}