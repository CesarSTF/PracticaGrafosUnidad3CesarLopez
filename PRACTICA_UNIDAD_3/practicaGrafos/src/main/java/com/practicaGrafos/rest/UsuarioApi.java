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

import com.practicaGrafos.controller.dao.UsuarioDao;
import com.practicaGrafos.controller.dao.services.UsuarioServices;
import com.practicaGrafos.controller.excepcion.ValueAlreadyExistException;
import com.practicaGrafos.controller.tda.graph.GrapLabelNoDirect;
import com.practicaGrafos.controller.tda.list.LinkedList;
import com.practicaGrafos.models.Usuario;

@Path("/usuario")
public class UsuarioApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list")
    public Response gelAll(@HeaderParam("Authorization") String authHeader) {
        HashMap<String, Object> res = new HashMap<>();
        UsuarioServices ts = new UsuarioServices();

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
            // Log antes de llamar al servicio
            System.out.println("Llamando a UsuarioServices.listAll...");
            LinkedList<Usuario> lista = ts.listAll();

            // Log para verificar si la lista obtenida es nula o vacía
            if (lista == null) {
                System.out.println("La lista de usuarios es nula.");
            } else if (lista.isEmpty()) {
                System.out.println("La lista de usuarios está vacía.");
            } else {
                System.out.println("Lista obtenida con " + lista.getSize() + " usuarios.");
            }

            res.put("status", "OK");
            res.put("msg", "Consulta exitosa.");
            res.put("data", lista == null || lista.isEmpty() ? new Object[] {} : lista.toArray());
            return Response.ok(res).build();
        } catch (Exception e) {
            // Log del error capturado
            System.err.println("Error al obtener la lista de usuarios: " + e.getMessage());
            e.printStackTrace();

            res.put("status", "ERROR");
            res.put("msg", "Error al obtener la lista de usuarios: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/get/{id}")
    public Response getById(@PathParam("id") Integer id) throws Exception {
        HashMap<String, Object> map = new HashMap<>();
        UsuarioServices ts = new UsuarioServices();
        try {
            map.put("msg", "OK");
            map.put("data", ts.getById(id));
            if (ts.getById(id) == null) {
                map.put("msg", "ERROR");
                map.put("error", "No se encontro el usuario con id: " + id);
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
        UsuarioServices ts = new UsuarioServices();

        try {

            if (map.get("apellido") == null || map.get("nombre") == null || map.get("correo") == null) {
                throw new IllegalArgumentException("El apellido es obligatorio.");
            }
            ts.getUsuario().setApellido(map.get("apellido").toString());
            ts.getUsuario().setNombre(map.get("nombre").toString());
            String correo = map.get("correo").toString();
            if (ts.isUnique("correo", correo)) {
                ts.getUsuario().setCorreo(map.get("correo").toString());
            } else {
                throw new ValueAlreadyExistException("El correo ya existe.");
            }

            ts.save();

            res.put("estado", "Ok");
            res.put("data", "Registro guardado con éxito.");
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

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/delete")
    public Response delete(@PathParam("id") Integer id) throws Exception {

        HashMap<String, Object> res = new HashMap<>();
        UsuarioServices ts = new UsuarioServices();
        try {
            ts.getUsuario().setId(id);
            ts.delete();
            res.put("estado", "Ok");
            res.put("data", "Registro eliminado con exito.");

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
        UsuarioServices ts = new UsuarioServices();

        try {
            if (map.get("id") == null) {
                throw new IllegalArgumentException("El id es obligatorio");
            }

            int id = (int) map.get("id");
            ts.setUsuario(ts.getById(id));

            if (map.get("nombre") != null) {
                ts.getUsuario().setNombre(map.get("nombre").toString());
            }
            if (map.get("apellido") != null) {
                ts.getUsuario().setApellido(map.get("apellido").toString());
            }
            if (map.get("correo") != null) {
                String correo = map.get("correo").toString();
                if (ts.isUnique("correo", correo)) {
                    ts.getUsuario().setCorreo(correo);
                } else {
                    throw new ValueAlreadyExistException("El correo ya existe.");
                }
            }

            ts.update();

            res.put("estado", "Ok");
            res.put("data", "Registro actualizado con éxito.");
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
        UsuarioServices ps = new UsuarioServices();

        try {
            LinkedList<Usuario> results;
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
                map.put("msg", "No se encontraron Usuarios");
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
        UsuarioServices ps = new UsuarioServices();
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
    @Path("/grafo")
    public Response grafo() {
        HashMap<String, Object> res = new HashMap<>();

        try {
            UsuarioServices usuarioServices = new UsuarioServices();
            LinkedList<Usuario> lista = usuarioServices.listAll();
            GrapLabelNoDirect<String> grafo = usuarioServices.crearGrafo();
            usuarioServices.guardarGrafo();
            usuarioServices.generarConexionesAleatorias();
            res.put("estado", "Ok");
            res.put("msg", "Grafo creado y guardado con éxito.");
            res.put("data", lista.toArray()); 
            res.put("grafo", grafo); 
            System.out.println("Ady" + grafo.toString());

            return Response.ok(res).build();
        } catch (Exception e) {
            res.put("estado", "error");
            res.put("msg", "Error interno del servidor: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(res).build();
        }
    }

    @Path("/camino_corto/{origen}/{destino}/{algoritmo}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response calcularCaminoCorto(
            @PathParam("origen") int origen,
            @PathParam("destino") int destino,
            @PathParam("algoritmo") int algoritmo) {

        HashMap<String, Object> response = new HashMap<>();

        try {
            if (origen <= 0 || destino <= 0) {
                throw new IllegalArgumentException("Los valores de origen y destino deben ser mayores que 0.");
            }
            if (algoritmo != 1 && algoritmo != 2) {
                throw new IllegalArgumentException("El algoritmo debe ser 1 (Floyd) o 2 (Bellman-Ford).");
            }

            UsuarioDao usuarioDao = new UsuarioDao();

            String resultado = usuarioDao.caminoCorto(origen, destino, algoritmo);

            response.put("status", "success");
            response.put("message", "Camino corto calculado exitosamente");
            response.put("resultado", resultado);

            return Response.ok(response).build();

        } catch (IllegalArgumentException e) {
            response.put("status", "error");
            response.put("message", "Parámetros inválidos: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(response).build();

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error al calcular el camino corto: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
        }
    }
}