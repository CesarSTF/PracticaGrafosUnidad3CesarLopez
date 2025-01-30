package com.practicaGrafos.controller.tda.stack;

import com.practicaGrafos.controller.excepcion.ListEmptyException;
import com.practicaGrafos.controller.excepcion.OverFlowException;
import com.practicaGrafos.controller.tda.list.LinkedList;

public class StackOperation<E> extends LinkedList<E> {
       private Integer top;

    public StackOperation() {}

    public StackOperation(Integer top) {
        this.top = top;
    }

    public Integer getTop() {
        return top;
    }

    public void setTop(Integer top) {
        this.top = top;
    }

    @Override
    public boolean isEmpty() {
        return getSize() == 0;
    }

    public void push(E data) throws OverFlowException, IndexOutOfBoundsException, ListEmptyException {
        add(data, 0);
    }

    // Modificado peek para manejar correctamente el caso de pila vacía
    public E peek() {
        if (isEmpty()) {
            return null;  // Puedes lanzar una excepción personalizada si prefieres
        }
        return this.getHead().getData();
    }

    public E pop() throws ListEmptyException {
        if (isEmpty()) {
            throw new ListEmptyException("Pila vacia");
        } else {
            E data = getHead().getData();  // Guardar el valor antes de eliminarlo
            deleteHeader();  // Eliminar el nodo cabeza
            return data;
        }
    }
}
