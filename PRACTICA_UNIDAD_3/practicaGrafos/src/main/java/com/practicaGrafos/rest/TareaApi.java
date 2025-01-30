package com.practicaGrafos.rest;

import java.util.HashMap;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.practicaGrafos.controller.dao.services.TareaServices;
import com.practicaGrafos.controller.dao.services.UsuarioServices;
import com.practicaGrafos.controller.excepcion.ListEmptyException;
import com.practicaGrafos.controller.excepcion.ValueAlreadyExistException;
import com.practicaGrafos.controller.tda.list.LinkedList;
import com.practicaGrafos.models.Tarea;

@Path("/tarea")
public class TareaApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list")
    public Response gelAll(@HeaderParam("Authorization") String authHeader) {
        HashMap<String, Object> res = new HashMap<>();
        TareaServices ts = new TareaServices();

        // Log para verificar el encabezado recibido
        System.out.println("Authorization Header: " + authHeader);

        // Verificar si el encabezado de autorización es nulo o no es "public"
        if (authHeader == null || !"public".equals(authHeader)) {
            System.out.println("Acceso no autorizado. Encabezado incorrecto o ausente.");
            res.put("status", "ERROR");
            res.put("msg", "Acceso no autorizado. Agrega el encabezado 'Authorization: public'.");
            return Response.status(Response.Status.UNAUTHORIZED).entity(res).build();
        }

        try {
            res.put("data", ts.listShowAll());
            if (ts.listAll().isEmpty()) {
                res.put("data", new Object[] {});
            }
        } catch (Exception e) {
            res.put("data", new Object[] {});
            System.out.println("Error " + e);
        }
        return Response.ok(res).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/get/{id}")
    public Response getById(@PathParam("id") Integer id) throws Exception {
        HashMap<String, Object> map = new HashMap<>();
        TareaServices ts = new TareaServices();
        try {
            map.put("msg", "OK");
            map.put("data", ts.getById(id));
            if (ts.getById(id) == null) {
                map.put("msg", "ERROR");
                map.put("error", "No se encontro el tarea con id: " + id);
                return Response.status(Status.NOT_FOUND).entity(map).build();
            }
            return Response.ok(map).build();
        } catch (Exception e) {
            e.printStackTrace();
            map.put("msg", "ERROR");
            map.put("error", "Error inesperado: " + e.getMessage());
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(map).build();
        }
    }

    @Path("/save")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(HashMap<String, Object> map) {
        HashMap<String, Object> res = new HashMap<>();
        TareaServices ts = new TareaServices();

        try {
            if (map.get("idUsuario") != null) {
                UsuarioServices us = new UsuarioServices();
                us.setUsuario(us.get(Integer.parseInt(map.get("idUsuario").toString())));
                if (us.getUsuario().getId() == null) {
                    throw new IllegalArgumentException("El idUsuario es obligatorio.");
                }

                if (map.get("nombre") == null || map.get("nombre").toString().isEmpty()) {
                    throw new IllegalArgumentException("El nombre es obligatorio.");
                }
                if (map.get("descripcion") == null || map.get("descripcion").toString().isEmpty()) {
                    throw new IllegalArgumentException("La descripción es obligatoria.");
                }
                if (map.get("prioridad") == null || map.get("prioridad").toString().isEmpty()) {
                    throw new IllegalArgumentException("La prioridad es obligatoria.");
                }

                ts.getTarea().setIdUsuario(us.getUsuario().getId());
                ts.getTarea().setDescripcion(map.get("descripcion").toString());
                ts.getTarea().setNombre(map.get("nombre").toString());
                ts.getTarea().setPrioridad(ts.getPrioridad(map.get("prioridad").toString()));

                ts.save();

                res.put("estado", "Ok");
                res.put("data", "Tarea guardada con éxito.");
                return Response.ok(res).build();
            } else {
                throw new IllegalArgumentException("El idUsuario es obligatorio.");
            }
        } catch (ValueAlreadyExistException e) {
            res.put("estado", "error");
            res.put("data", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
        } catch (IllegalArgumentException e) {
            res.put("estado", "error");
            res.put("data", "Error de validación: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
        } catch (Exception e) {
            res.put("estado", "error");
            res.put("data", "Error interno del servidor: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/delete")
    public Response delete(@PathParam("id") Integer id) throws Exception {

        HashMap<String, Object> res = new HashMap<>();
        TareaServices ts = new TareaServices();
        try {
            ts.getTarea().setId(id);
            ts.delete();
            res.put("estado", "Ok");
            res.put("data", "Tarea eliminado con exito.");

            return Response.ok(res).build();
        } catch (Exception e) {
            res.put("estado", "error");
            res.put("data", "Error interno del servidor: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update")
    public Response update(HashMap<String, Object> map) {
        HashMap<String, Object> res = new HashMap<>();
        TareaServices ts = new TareaServices();

        try {
            if (map.get("id") == null) {
                throw new IllegalArgumentException("El id es obligatorio");
            }

            // Obtener el id de la tarea para actualizar
            int id = Integer.parseInt(map.get("id").toString());
            ts.getTarea().setId(id);

            if (ts.getTarea() == null) {
                throw new IllegalArgumentException("La tarea con el id proporcionado no existe.");
            }

            // Validaciones para los campos
            if (map.get("nombre") == null || map.get("nombre").toString().isEmpty()) {
                throw new IllegalArgumentException("El nombre es obligatorio.");
            }
            if (map.get("descripcion") == null || map.get("descripcion").toString().isEmpty()) {
                throw new IllegalArgumentException("La descripción es obligatoria.");
            }
            if (map.get("prioridad") == null || map.get("prioridad").toString().isEmpty()) {
                throw new IllegalArgumentException("La prioridad es obligatoria.");
            }

            // Verificar si el idUsuario está presente y es válido
            if (map.get("idUsuario") == null) {
                throw new IllegalArgumentException("El idUsuario es obligatorio.");
            }

            int idUsuario = Integer.parseInt(map.get("idUsuario").toString());
            UsuarioServices us = new UsuarioServices();
            us.setUsuario(us.get(idUsuario));

            if (us.getUsuario().getId() == null) {
                throw new IllegalArgumentException("El idUsuario proporcionado no es válido.");
            }

            // Asignar los valores a la tarea para la actualización
            ts.getTarea().setNombre(map.get("nombre").toString());
            ts.getTarea().setDescripcion(map.get("descripcion").toString());
            ts.getTarea().setPrioridad(ts.getPrioridad(map.get("prioridad").toString()));
            ts.getTarea().setIdUsuario(us.getUsuario().getId());

            // Realizar la actualización
            ts.update();

            res.put("estado", "Ok");
            res.put("data", "Tarea actualizada con éxito.");
            return Response.ok(res).build();

        } catch (ValueAlreadyExistException e) {
            res.put("estado", "error");
            res.put("data", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
        } catch (IllegalArgumentException e) {
            res.put("estado", "error");
            res.put("data", "Error de validación: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
        } catch (Exception e) {
            res.put("estado", "error");
            res.put("data", "Error interno del servidor: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/search/{attribute}/{value}")
    public Response binarySearchLin(@PathParam("attribute") String attribute, @PathParam("value") String value) {
        HashMap<String, Object> map = new HashMap<>();
        TareaServices ps = new TareaServices();

        try {
            LinkedList<Tarea> results;
            try {
                results = ps.getBy(attribute, value);
            } catch (NumberFormatException e) {
                map.put("msg", "El valor proporcionado no es un numero valido");
                return Response.status(Status.BAD_REQUEST).entity(map).build();
            }

            if (results != null && !results.isEmpty()) {
                map.put("msg", "OK");
                map.put("data", results.toArray());
                return Response.ok(map).build();
            } else {
                map.put("msg", "No se encontraron Tareas");
                return Response.status(Status.NOT_FOUND).entity(map).build();
            }

        } catch (Exception e) {
            map.put("msg", "Error en la busqueda");
            map.put("error", e.getMessage());
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(map).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/order/{atributo}/{orden}")
    public Response ordenar(@PathParam("atributo") String atributo, @PathParam("orden") Integer orden)
            throws Exception {
        HashMap<String, Object> res = new HashMap<>();
        TareaServices ps = new TareaServices();
        try {
            res.put("estado", "Ok");
            res.put("data", ps.order(atributo, orden).toArray());
            if (ps.order(atributo, orden).isEmpty()) {
                res.put("data", new Object[] {});
            }
            return Response.ok(res).build();
        } catch (Exception e) {
            res.put("estado", "error");
            res.put("data", "Error interno del servidor: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/prioridad")
    public Response getPrioridad() throws ListEmptyException, Exception {
        HashMap<String, Object> map = new HashMap<>();
        TareaServices ts = new TareaServices();
        map.put("msg", "OK");
        map.put("data", ts.getPrioridads());
        return Response.ok(map).build();
    }
}