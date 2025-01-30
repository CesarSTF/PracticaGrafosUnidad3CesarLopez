package com.practicaGrafos.controller.tda.stack;

public class Stack<E>{
    private StackOperation<E> stackOperation;

    public Stack(Integer cant){
        stackOperation = new StackOperation<>(cant);
    }

    public void push(E data) throws Exception {
        stackOperation.push(data);
    }

    public Integer getSize() {
        return stackOperation.getSize();
    }

    public void clear(){
        this.stackOperation.reset();
    }

    public E getTop() {
        E topElement = stackOperation.peek();
        if (topElement == null) {
            throw new IllegalStateException("La pila está vacía.");
        }
        return topElement;
    }

    public E pop() throws Exception {
        return stackOperation.pop();
    }

    public void print(){
        System.out.println("PILA");
        System.out.println(stackOperation.toString());
        System.out.println("********");
    }

    @Override
    public String toString(){
        return stackOperation.toString();
    }

    public StackOperation<E> getStackOperation(){
        return stackOperation;
    }

    public void setStackOperation(StackOperation<E> stackOperation) {
        this.stackOperation = stackOperation;
    }

    // Agregar isEmpty para verificar si la pila está vacía
    public boolean isEmpty() {
        return stackOperation.getSize() == 0;
    }
}
