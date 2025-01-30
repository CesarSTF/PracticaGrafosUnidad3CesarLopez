package com.practicaGrafos.controller.tda.graph;

import java.lang.reflect.Array;
import java.util.HashMap;

import com.practicaGrafos.controller.excepcion.LabelException;
import com.practicaGrafos.controller.tda.list.LinkedList;

public class GraphLabelDirect<E> extends GrahpDirect {
    protected E labels[];
    protected HashMap<E, Integer> dictVertices;
    private Class<E> clazz;

    public GraphLabelDirect(Integer nro_vertices, Class<E> clazz) {
        super(nro_vertices);
        this.clazz = clazz;
        labels = (E[]) Array.newInstance(clazz, nro_vertices + 1);
        dictVertices = new HashMap<>();
    }

    public Boolean is_EdgeL(E v1, E v2) throws Exception {
        if (isLabelsGraph()) {
            return is_Edge(getVerticeL(v1), getVerticeL(v2));
        } else {
            throw new LabelException("Grafo no etiquetado");
        }
    }

    public void insertEdgeL(E v1, E v2, Float weight) throws Exception {
        if (isLabelsGraph()) {
            add_edge(getVerticeL(v1), getVerticeL(v2), weight);
        } else {
            throw new LabelException("Grafo no etiquetado");
        }

    }

    public void insertEdgeL(E v1, E v2) throws Exception {
        if (isLabelsGraph()) {
            insertEdgeL(v1, v2, Float.NaN);
            // add_edge(getVerticeL(v1), getVerticeL(v2), Float.NaN);
        } else {
            throw new LabelException("Grafo no etiquetado");
        }

    }

    public LinkedList<Adycencia> adyacenciasL(E v) throws Exception {
        if (isLabelsGraph()) {
            return adyacencias(getVerticeL(v));
        } else {
            throw new LabelException("Grafo no etiquetado");
        }
    }

    public void labelsVertices(Integer v, E data) {
        labels[v] = data;
        dictVertices.put(data, v);
    }

    public Boolean isLabelsGraph() {
        Boolean band = true;
        for (int i = 1; i < labels.length; i++) {
            if (labels[i] == null) {
                band = false;
                break;
            }

        }
        return band;
    }

    public Integer getVerticeL(E label) {
        return dictVertices.get(label);
    }

    public E getLabelL(Integer v1) {
        return labels[v1];
    }

    @Override
    public String toString() {
        String grafo = "";
        try {
            for (int i = 1; i <= this.nro_Ver(); i++) {
                grafo += "V" + i + " [" + getLabelL(i).toString() + "]" + "\n";
                LinkedList<Adycencia> lista = adyacencias(i);
                if (!lista.isEmpty()) {
                    Adycencia[] ady = lista.toArray();
                    for (int j = 0; j < ady.length; j++) {
                        Adycencia a = ady[j];
                        grafo += "ady " + "V" + a.getDestination() + " weight:" + a.getWeight() + " ["
                                + getLabelL(a.getDestination()).toString() + "]" + "\n";
                    }
                }

            }
        } catch (Exception e) {
            System.out.println("Error Graph:" + e);
        }

        return grafo;
    }

    public String drawGraph() {
        StringBuilder grafo = new StringBuilder();

        grafo.append("digraph G {\n");
        try {
            grafo.append("digraph G {\n");
            for (int i = 1; i <= this.nro_Ver(); i++) {
                grafo.append("V").append(i).append(" [")
                        .append("label=\"").append(getLabelL(i).toString()).append("\"]")
                        .append("\n");
                LinkedList<Adycencia> lista = adyacencias(i);
                if (!lista.isEmpty()) {
                    Adycencia[] ady = lista.toArray();
                    for (Adycencia a : ady) {
                        grafo.append("V").append(i).append(" -> V").append(a.getDestination())
                                .append(" [label=\"").append(a.getWeight()).append("\"]\n");
                    }
                }
            }
            grafo.append("}");
        } catch (Exception e) {
            grafo.append("Error: ").append(e.getMessage());
        }
        return grafo.toString();
    }
}
