package com.practicaGrafos.controller.dao.services;

import java.util.HashMap;

import com.practicaGrafos.controller.dao.TareaDao;
import com.practicaGrafos.models.Tarea;
import com.practicaGrafos.models.Enums.Prioridad;
import com.practicaGrafos.controller.tda.list.LinkedList;

public class TareaServices {
    private TareaDao obj;

    public Object[] listShowAll() throws Exception {
        if (!obj.getListAll().isEmpty()) {
            Tarea[] lista = (Tarea[]) obj.getListAll().toArray();
            Object[] respuesta = new Object[lista.length];
            for (int i = 0; i < lista.length; i++) {
                //Usuario usuario = new UsuarioServices().get(lista[i].getIdUsuario());
                HashMap<String, Object> mapa = new HashMap<>();
                mapa.put("id", lista[i].getId());
                mapa.put("nombre", lista[i].getNombre());
                mapa.put("prioridad", lista[i].getPrioridad());
                mapa.put("descripcion", lista[i].getDescripcion());
                mapa.put("Usuario", //usuario);
                        new UsuarioServices().showOne(lista[i].getIdUsuario()));
                respuesta[i] = mapa;
            }
            return respuesta;
        }
        return new Object[] {};
    }

    public Object showOne(Integer id) throws Exception {
        try {
            Tarea tarea = obj.getById(id);
            HashMap<String, Object> mapa = new HashMap<>();
            mapa.put("id", tarea.getId());
            mapa.put("nombre", tarea.getNombre());
            mapa.put("prioridad", tarea.getPrioridad());
            mapa.put("descripcion", tarea.getDescripcion());
            mapa.put("Usuario", new UsuarioServices().showOne(id));
            return mapa;
        } catch (Exception e) {
            throw new Exception("Error al mostrar el tarea: " + e.getMessage());
        }
    }

    public TareaServices() {
        obj = new TareaDao();
    }

    public Tarea getTarea() {
        return obj.getTarea();
    }

    public Boolean save() throws Exception {
        return obj.save();
    }

    public Boolean delete() throws Exception {
        return obj.delete();
    }

    public LinkedList<Tarea> listAll() throws Exception {
        return obj.getListAll();
    }

    public void setUsuario(Tarea trabajador) {
        obj.setUsuario(trabajador);
    }

    public Tarea getById(Integer id) throws Exception {
        return obj.getById(id);

    }

    public String toJson() throws Exception {
        return obj.toJson();
    }

    public LinkedList<Tarea> getBy(String atributo, Object valor) throws Exception {
        return obj.buscar(atributo, valor);
    }

    public LinkedList<Tarea> order(String atributo, Integer type) throws Exception {
        return obj.order(atributo, type);
    }

    public Tarea obtenerPor(String atributo, Object valor) throws Exception {
        return obj.buscarPor(atributo, valor);
    }

    public Boolean update() throws Exception {
        return obj.update();
    }

    public String[] getAttributeLists() {
        return obj.getAttributeLists();
    }

    public Boolean isUnique(String campo, Object value) throws Exception {
        return obj.isUnique(campo, value);
    }

    public Prioridad getPrioridad(String prioridad) {
        return obj.getPrioridad(prioridad);
    }

    public Prioridad[] getPrioridads() {
        return obj.getPrioridades();
    }

    // public void validateField(String field, HashMap<String, Object> map,
    // String... validations) throws Exception {
    // Tarea persona = this.getUsuario();
    // FieldValidator.validateAndSet(persona, map, field, validations);
    // }
}
