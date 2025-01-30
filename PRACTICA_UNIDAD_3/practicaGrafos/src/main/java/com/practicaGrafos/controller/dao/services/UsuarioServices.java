package com.practicaGrafos.controller.dao.services;

import java.util.HashMap;

import com.practicaGrafos.controller.dao.UsuarioDao;
import com.practicaGrafos.models.Usuario;
import com.practicaGrafos.controller.tda.graph.GrapLabelNoDirect;
import com.practicaGrafos.controller.tda.graph.algoritmos.Floyd;
import com.practicaGrafos.controller.tda.list.LinkedList;

public class UsuarioServices {
    private UsuarioDao obj;

    public Object showOne(Integer id) throws Exception {
        try {
            Usuario us = obj.getById(id);
            HashMap<String, Object> mapa = new HashMap<>();
            mapa.put("id", us.getId());
            mapa.put("nombre", us.getNombre());
            mapa.put("apellido", us.getApellido());
            mapa.put("correo", us.getCorreo());
            return mapa;
        } catch (Exception e) {
            throw new Exception("Error al mostrar el usuario: " + e.getMessage());
        }
    }

    public UsuarioServices() {
        obj = new UsuarioDao();
    }

    public Usuario getUsuario() {
        return obj.getUsuario();
    }

    public Boolean save() throws Exception {
        return obj.save();
    }

    public Boolean delete() throws Exception {
        return obj.delete();
    }

    public LinkedList<Usuario> listAll() throws Exception {
        return obj.getListAll();
    }

    public void setUsuario(Usuario trabajador) {
        obj.setUsuario(trabajador);
    }

    public Usuario getById(Integer id) throws Exception {
        return obj.getById(id);

    }

    public String toJson() throws Exception {
        return obj.toJson();
    }

    public Usuario get(Integer index) throws Exception {
        return obj.get(index);
    }

    public LinkedList<Usuario> getBy(String atributo, Object valor) throws Exception {
        return obj.buscar(atributo, valor);
    }

    public LinkedList<Usuario> order(String atributo, Integer type) throws Exception {
        return obj.order(atributo, type);
    }

    public Usuario obtenerPor(String atributo, Object valor) throws Exception {
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

    // Métodos para manejar el grafo
    public GrapLabelNoDirect<String> crearGrafo() {
        return obj.creategraph();
    }

    public void guardarGrafo() {
        obj.saveGraph();
    }

    public void cargarGrafo() {
        obj.loadGraph();
    }

    public void generarConexionesAleatorias() {
        obj.generateRandomConnections();
    }

    public void inicializarGrafo() {
        obj.initializeGraph();
    }

    public boolean existeArchivoGrafo() {
        return obj.existsGraphFile();
    }

    public GrapLabelNoDirect<String> obtenerGrafo() {
        return obj.getGraph();
    }

    public void setNombreArchivoGrafo(String nombreArchivo) {
        obj.setGraphFileName(nombreArchivo);
    }

    public String calcularCaminoCorto(int origen, int destino, int algoritmo) throws Exception {
        return obj.caminoCorto(origen, destino, algoritmo);
    }
}
