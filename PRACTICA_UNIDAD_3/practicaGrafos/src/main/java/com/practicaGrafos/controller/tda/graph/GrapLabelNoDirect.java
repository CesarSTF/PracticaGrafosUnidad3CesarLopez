package com.practicaGrafos.controller.tda.graph;

import com.practicaGrafos.controller.excepcion.LabelException;

public class GrapLabelNoDirect<E> extends GraphLabelDirect<E> {
    public GrapLabelNoDirect(Integer nro_vertices, Class<E> clazz) {
        super(nro_vertices, clazz);
    }

    public void insertEdgeL(E v1, E v2, Float weight) throws Exception {
        if (isLabelsGraph()) {
            // add_edge(getVerticeL(v1), getVerticeL(v2), weight);
            add_edge(getVerticeL(v1), getVerticeL(v2), weight);
            add_edge(getVerticeL(v2), getVerticeL(v1), weight);

        } else {
            throw new LabelException("Grafo no etiquetado");
        }
    }

    public Float getWeigth2(Integer v1, Integer v2) throws Exception {
        return weight_edge(v1, v2);
    }
}
