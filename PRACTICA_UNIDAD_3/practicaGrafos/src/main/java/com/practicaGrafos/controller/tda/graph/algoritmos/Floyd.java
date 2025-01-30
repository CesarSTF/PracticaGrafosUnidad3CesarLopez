package com.practicaGrafos.controller.tda.graph.algoritmos;

import com.practicaGrafos.controller.tda.graph.GrapLabelNoDirect;
import com.practicaGrafos.controller.tda.list.LinkedList;

public class Floyd {
    private GrapLabelNoDirect<String> grafo;
    private int origen;
    private int destino;
    private LinkedList<LinkedList<Float>> distancias;
    private LinkedList<LinkedList<Integer>> siguiente;

    public Floyd(GrapLabelNoDirect<String> grafo, int origen, int destino) {
        this.grafo = grafo;
        this.origen = origen;
        this.destino = destino;
        int n = grafo.nro_Ver();
        this.distancias = new LinkedList<>();
        this.siguiente = new LinkedList<>();
        inicializarMatrices(n);
    }

    private void inicializarMatrices(int n) {
        for (int i = 0; i <= n; i++) {
            LinkedList<Float> filaDistancias = new LinkedList<>();
            LinkedList<Integer> filaSiguiente = new LinkedList<>();
            for (int j = 0; j <= n; j++) {
                if (i == j) {
                    filaDistancias.add(0f);
                    filaSiguiente.add(-1);
                } else {
                    try {
                        float peso = grafo.getWeigth2(i, j);
                        if (Float.isNaN(peso) || peso <= 0) {
                            filaDistancias.add(Float.MAX_VALUE);
                            filaSiguiente.add(-1);
                        } else {
                            filaDistancias.add(peso);
                            filaSiguiente.add(j);
                        }
                    } catch (Exception e) {
                        filaDistancias.add(Float.MAX_VALUE);
                        filaSiguiente.add(-1);
                    }
                }
            }
            distancias.add(filaDistancias);
            siguiente.add(filaSiguiente);
        }
    }

    public String caminoCorto() throws Exception {
        int n = grafo.nro_Ver();
        aplicarFloydWarshall(n);
        return reconstruirCamino(origen, destino);
    }

    private void aplicarFloydWarshall(int n) throws Exception {
        for (int k = 1; k <= n; k++) {
            for (int i = 1; i <= n; i++) {
                for (int j = 1; j <= n; j++) {
                    float distanciaIK = distancias.get(i).get(k);
                    float distanciaKJ = distancias.get(k).get(j);
                    float distanciaIJ = distancias.get(i).get(j);

                    if (distanciaIK != Float.MAX_VALUE && distanciaKJ != Float.MAX_VALUE &&
                        distanciaIK + distanciaKJ < distanciaIJ) {
                        distancias.get(i).set(j, distanciaIK + distanciaKJ);
                        siguiente.get(i).set(j, siguiente.get(i).get(k));
                    }
                }
            }
        }
    }

    private String reconstruirCamino(int origen, int destino) throws Exception {
        if (siguiente.get(origen).get(destino) == -1) {
            return "No hay camino";
        }

        StringBuilder camino = new StringBuilder();
        int actual = origen;
        float distanciaTotal = 0;

        while (actual != destino) {
            if (siguiente.get(actual).get(destino) == -1) {
                return "Error: Camino interrumpido inesperadamente.";
            }
            camino.append(actual).append(" -> ");
            distanciaTotal += distancias.get(actual).get(siguiente.get(actual).get(destino));
            actual = siguiente.get(actual).get(destino);
        }
        camino.append(destino);
        distanciaTotal += distancias.get(actual).get(destino);

        System.out.println("Distancia total recorrida: " + distanciaTotal);

        return "Camino: " + camino.toString() + "|" + "Distancia total: " + distanciaTotal;
    }

    public String getMatrices() throws Exception {
        StringBuilder matrices = new StringBuilder();
        matrices.append("Distancias:\n");
        for (int i = 1; i < distancias.getSize(); i++) {
            for (int j = 1; j < distancias.get(i).getSize(); j++) {
                matrices.append(distancias.get(i).get(j)).append(" ");
            }
            matrices.append("\n");
        }
        matrices.append("Siguiente:\n");
        for (int i = 1; i < siguiente.getSize(); i++) {
            for (int j = 1; j < siguiente.get(i).getSize(); j++) {
                matrices.append(siguiente.get(i).get(j)).append(" ");
            }
            matrices.append("\n");
        }
        return matrices.toString();
    }
}