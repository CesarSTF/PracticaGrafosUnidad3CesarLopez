package com.practicaGrafos.controller.tda.graph;

public class Adycencia {
    private Integer destination;
    private float weight;

    public Adycencia(Integer destination, float weight) {
        this.destination = destination;
        this.weight = weight;
    }
    
    //constructor por defecto
    public Adycencia(){}

    public Integer getDestination() {
        return this.destination;
    }

    public void setDestination(Integer destination) {
        this.destination = destination;
    }

    public float getWeight() {
        return this.weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }
}
