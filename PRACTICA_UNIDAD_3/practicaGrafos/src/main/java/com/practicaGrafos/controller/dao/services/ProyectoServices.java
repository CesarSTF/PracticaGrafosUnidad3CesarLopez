package com.practicaGrafos.controller.dao.services;

import java.util.HashMap;

import com.practicaGrafos.controller.dao.ProyectoDao;
import com.practicaGrafos.models.Proyecto;
import com.practicaGrafos.controller.tda.list.LinkedList;

public class ProyectoServices {
    private ProyectoDao obj;

    public ProyectoServices() {
        obj = new ProyectoDao();
    }

    public Proyecto getProyecto() {
        return obj.getProyecto();
    }

    public Boolean save() throws Exception {
        return obj.save();
    }

    public Boolean delete() throws Exception {
        return obj.delete();
    }

    public LinkedList<Proyecto> listAll() throws Exception {
        return obj.getListAll();
    }

    public void setProyecto(Proyecto proyecto) {
        obj.setProyecto(proyecto);
    }

    public Proyecto getById(Integer id) throws Exception {
        return obj.getById(id);

    }

    public String toJson() throws Exception {
        return obj.toJson();
    }

    public LinkedList<Proyecto> getBy(String atributo, Object valor) throws Exception {
        return obj.buscar(atributo, valor);
    }

    public LinkedList<Proyecto> order(String atributo, Integer type) throws Exception {
        return obj.order(atributo, type);
    }

    public Proyecto obtenerPor(String atributo, Object valor) throws Exception {
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

    // public void validateField(String field, HashMap<String, Object> map, String... validations) throws Exception {
    //     Proyecto persona = this.getProyecto();
    //     FieldValidator.validateAndSet(persona, map, field, validations);
    // }
}
