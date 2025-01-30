package com.practicaGrafos.controller.dao;

import com.practicaGrafos.controller.dao.implement.Contador;
import com.practicaGrafos.controller.excepcion.ValueAlreadyExistException;
import com.practicaGrafos.models.Tarea;
import com.practicaGrafos.models.Enums.Prioridad;

import java.lang.reflect.Method;

import com.google.gson.Gson;
import com.practicaGrafos.controller.dao.implement.AdapterDao;
import com.practicaGrafos.controller.tda.list.LinkedList;

@SuppressWarnings({ "unchecked", "ConvertToTryWithResources" })
public class TareaDao extends AdapterDao<Tarea> {
    private Tarea tarea;
    private LinkedList<Tarea> listAll;

    public TareaDao() {
        super(Tarea.class);
    }

    public Tarea getTarea() {
        if (this.tarea == null) {
            this.tarea = new Tarea();
        }
        return this.tarea;
    }

    public void setUsuario(Tarea tarea) {
        this.tarea = tarea;
    }

    public LinkedList<Tarea> getListAll() throws Exception {
        if (listAll == null) {
            this.listAll = listAll();
        }
        return listAll;
    }

    public boolean save() throws Exception {
        Integer id = Contador.obtenerValorActual(Tarea.class);
        try {
            this.tarea.setId(id);
            this.persist(this.tarea);
            Contador.actualizarContador(Tarea.class);
            this.listAll = listAll();
            return true;
        } catch (Exception e) {
            throw new Exception("Error al guardar el tarea: " + e.getMessage());
        }
    }

    public Boolean update() throws Exception {
        if (this.tarea == null || this.tarea.getId() == null) {
            throw new Exception("No se ha seleccionado un tarea para actualizar.");
        }
        if (listAll == null) {
            listAll = listAll();
        }
        Integer index = getUsuarioIndex("id", this.tarea.getId());
        if (index == -1) {
            throw new Exception("Tarea no encontrado.");
        }
        try {
            this.merge(this.tarea, index);
            listAll = listAll();
            return true;
        } catch (Exception e) {
            throw new Exception("Error al actualizar el tarea: " + e.getMessage());
        }
    }

    public Boolean delete() throws Exception {
        if (this.tarea == null || this.tarea.getId() == null) {
            throw new Exception("No se ha seleccionado un tarea para eliminar.");
        }
        if (listAll == null) {
            listAll = listAll();
        }
        Integer index = getUsuarioIndex("id", this.tarea.getId());
        if (index == -1) {
            throw new Exception("Tarea no encontrado.");
        }
        try {
            this.delete(index);
            listAll = listAll();
            return true;
        } catch (Exception e) {
            throw new Exception("Error al eliminar el tarea: " + e.getMessage());
        }
    }

    private LinkedList<Tarea> linearBinarySearch(String attribute, Object value) throws Exception {
        LinkedList<Tarea> lista = this.listAll().quickSort(attribute, 1);
        LinkedList<Tarea> trabajadors = new LinkedList<>();
        if (!lista.isEmpty()) {
            Tarea[] aux = lista.toArray();
            Integer low = 0;
            Integer high = aux.length - 1;
            Integer mid;
            Integer index = -1;
            String searchValue = value.toString().toLowerCase();
            while (low <= high) {
                mid = (low + high) / 2;

                String midValue = obtenerAttributeValue(aux[mid], attribute).toString().toLowerCase();
                System.out.println("Comparando: " + midValue + " con " + searchValue);

                if (midValue.startsWith(searchValue)) {
                    if (mid == 0 || !obtenerAttributeValue(aux[mid - 1], attribute).toString().toLowerCase()
                            .startsWith(searchValue)) {
                        index = mid;
                        break;
                    } else {
                        high = mid - 1;
                    }
                } else if (midValue.compareToIgnoreCase(searchValue) < 0) {
                    low = mid + 1;
                } else {
                    high = mid - 1;
                }
            }

            if (index.equals(-1)) {
                return trabajadors;
            }

            Integer i = index;
            while (i < aux.length
                    && obtenerAttributeValue(aux[i], attribute).toString().toLowerCase().startsWith(searchValue)) {
                trabajadors.add(aux[i]);
                System.out.println("Agregando: " + aux[i].getNombre());
                i++;
            }
        }
        return trabajadors;
    }

    public LinkedList<Tarea> buscar(String attribute, Object value) throws Exception {
        return linearBinarySearch(attribute, value);
    }

    public Tarea buscarPor(String attribute, Object value) throws Exception {
        LinkedList<Tarea> lista = listAll();
        Tarea p = null;

        try {
            if (!lista.isEmpty()) {
                Tarea[] trabajadors = lista.toArray();
                for (int i = 0; i < trabajadors.length; i++) {
                    if (obtenerAttributeValue(trabajadors[i], attribute).toString().toLowerCase()
                            .equals(value.toString().toLowerCase())) {
                        p = trabajadors[i];
                        break;
                    }
                }
            }
            if (p == null) {
                throw new Exception("No se encontró el tarea con " + attribute + ": " + value);
            }
        } catch (Exception e) {
            throw new Exception("Error al buscar el tarea: " + e.getMessage());
        }

        return p;
    }

    private Integer getUsuarioIndex(String attribute, Object value) throws Exception {
        if (this.listAll == null) {
            this.listAll = listAll();
        }
        Integer index = -1;
        if (!this.listAll.isEmpty()) {
            Tarea[] trabajadors = this.listAll.toArray();
            for (int i = 0; i < trabajadors.length; i++) {
                if (obtenerAttributeValue(trabajadors[i], attribute).toString().toLowerCase()
                        .equals(value.toString().toLowerCase())) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    private Object obtenerAttributeValue(Object object, String attribute) throws Exception {
        String normalizedAttribute = "get" + attribute.substring(0, 1).toUpperCase()
                + attribute.substring(1).toLowerCase();
        Method[] methods = object.getClass().getMethods();

        for (Method method : methods) {
            if (method.getName().equalsIgnoreCase(normalizedAttribute) && method.getParameterCount() == 0) {
                return method.invoke(object);
            }
        }

        throw new NoSuchMethodException("No se encontor el atributo: " + attribute);
    }

    public String[] getAttributeLists() {
        LinkedList<String> attributes = new LinkedList<>();
        for (Method m : Tarea.class.getDeclaredMethods()) {
            if (m.getName().startsWith("get")) {
                String attribute = m.getName().substring(3);
                if (!attribute.equalsIgnoreCase("id")) {
                    attributes.add(attribute.substring(0, 1).toLowerCase() + attribute.substring(1));
                }
            }
        }
        return attributes.toArray();
    }

    public Boolean isUnique(String campo, Object value) throws Exception {
        if (campo == null || value == null) {
            throw new IllegalArgumentException("El atributo y el valor no pueden ser nulos.");
        }

        if (this.listAll == null) {
            this.listAll = listAll();
        }

        if (this.listAll.isEmpty()) {
            return true;
        }

        Tarea[] correos = this.listAll.toArray();

        for (Tarea tarea : correos) {
            Object attributeValue = obtenerAttributeValue(tarea, campo);
            if (attributeValue != null && attributeValue.toString().equalsIgnoreCase(value.toString())) {
                throw new ValueAlreadyExistException("El correo ya existe.");
            }
        }

        return true;
    }

    public LinkedList<Tarea> order(String attribute, Integer type) throws Exception {
        LinkedList<Tarea> lista = listAll();
        return lista.isEmpty() ? lista : lista.mergeSort(attribute, type);
    }

    public String toJson() throws Exception {
        Gson g = new Gson();
        return g.toJson(this.tarea);
    }

    public Tarea getById(Integer id) throws Exception {
        return get(id);
    }

    public String getJsonByIndex(Integer index) throws Exception {
        Gson g = new Gson();
        return g.toJson(get(index));
    }

    public String getJson(Integer Index) throws Exception {
        Gson g = new Gson();
        return g.toJson(get(Index));
    }

    public Prioridad getPrioridad(String tarea) {
        return Prioridad.valueOf(tarea);
    }

    public Prioridad[] getPrioridades() {
        return Prioridad.values();
    }
}