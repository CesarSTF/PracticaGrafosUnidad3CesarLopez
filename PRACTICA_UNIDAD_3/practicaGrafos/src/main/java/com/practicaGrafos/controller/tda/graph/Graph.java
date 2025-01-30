package com.practicaGrafos.controller.tda.graph;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.practicaGrafos.controller.dao.TareaDao;
import com.practicaGrafos.controller.dao.UsuarioDao;
import com.practicaGrafos.controller.excepcion.ListEmptyException;
import com.practicaGrafos.controller.tda.list.LinkedList;
import com.practicaGrafos.models.Tarea;
import com.practicaGrafos.models.Usuario;

public abstract class Graph {

    public abstract Integer nro_Ver();

    public abstract Integer nro_Edges();

    public abstract Boolean is_Edge(Integer v1, Integer v2) throws Exception;

    public abstract Float weight_edge(Integer v1, Integer v2) throws Exception;

    public abstract void add_edge(Integer v1, Integer v2) throws Exception;

    public abstract void add_edge(Integer v1, Integer v2, Float weight) throws Exception;

    public abstract LinkedList<Adycencia> adyacencias(Integer v1);

    @Override

    public String toString() {
        String grafo = "";
        try {
            for (int i = 1; i <= this.nro_Ver(); i++) {
                grafo += "V" + i + "\n";
                LinkedList<Adycencia> lista = adyacencias(i);
                if (!lista.isEmpty()) {
                    Adycencia[] ady = lista.toArray();
                    for (int j = 0; j < ady.length; j++) {
                        Adycencia a = ady[j];
                        grafo += "ady " + "V" + a.getDestination() + " weight:" + a.getWeight() + "\n";
                    }
                }

            }
        } catch (Exception e) {
            System.out.println("Error Graph:" + e);
        }

        return grafo;
    }
    /*
     * public static String path = "media/";
     * 
     * private Map<Integer, Object> vertexModels = new HashMap<>();
     * 
     * @Override
     * public String toString() {
     * StringBuilder grafo = new StringBuilder();
     * try {
     * for (int i = 1; i <= this.nro_Ver(); i++) {
     * grafo.append("Vértice ").append(i).append(":\n");
     * LinkedList<Adycencia> lista = adyacencias(i);
     * if (!lista.isEmpty()) {
     * grafo.append("  Total adyacencias: ").append(lista.getSize()).append("\n");
     * Adycencia[] ady = lista.toArray();
     * for (Adycencia a : ady) {
     * grafo.append("    → V").append(a.getDestination())
     * .append(" [Peso: ").append(a.getWeight()).append("]\n");
     * }
     * } else {
     * grafo.append("  Sin adyacencias.\n");
     * }
     * }
     * } catch (Exception e) {
     * grafo.append("Error al generar el grafo: ").append(e.getMessage()).append(
     * "\n");
     * }
     * return grafo.toString();
     * }
     * 
     * public void saveGraph(String filename) {
     * try {
     * // Crear un JSON para representar el grafo
     * JsonArray graphArray = new JsonArray();
     * for (int i = 1; i <= this.nro_Ver(); i++) {
     * JsonObject vertex = new JsonObject();
     * vertex.addProperty("id", i);
     * System.out.println("id" + i);
     * 
     * LinkedList<Adycencia> adyacenciasList = adyacencias(i);
     * if (adyacenciasList == null) {
     * vertex.addProperty("grado", 0);
     * vertex.add("adyacencias", new JsonArray());
     * } else {
     * vertex.addProperty("grado", adyacenciasList.getSize());
     * JsonArray edges = new JsonArray();
     * for (Adycencia ady : adyacenciasList.toArray()) {
     * JsonObject edge = new JsonObject();
     * edge.addProperty("destino", ady.getDestination());
     * edge.addProperty("peso", ady.getWeight());
     * edges.add(edge);
     * }
     * vertex.add("adyacencias", edges);
     * }
     * 
     * graphArray.add(vertex);
     * }
     * 
     * // Escribir el JSON en un archivo
     * Files.createDirectories(Paths.get(path));
     * Files.write(Paths.get(path + filename), graphArray.toString().getBytes());
     * } catch (Exception e) {
     * System.err.println("Error al guardar el grafo: " + e.getMessage());
     * }
     * }
     * 
     * public void loadGraph(String filename) {
     * try {
     * String content = new String(Files.readAllBytes(Paths.get(path + filename)));
     * JsonArray graphArray = JsonParser.parseString(content).getAsJsonArray();
     * 
     * for (JsonElement element : graphArray) {
     * JsonObject vertex = element.getAsJsonObject();
     * int id = vertex.get("id").getAsInt();
     * 
     * JsonArray edges = vertex.getAsJsonArray("adyacencias");
     * for (JsonElement edgeElement : edges) {
     * JsonObject edge = edgeElement.getAsJsonObject();
     * int destino = edge.get("destino").getAsInt();
     * float peso = edge.get("peso").getAsFloat();
     * System.out.println("arista" + id + " " + destino + " " + peso);
     * add_edge(id, destino, peso);
     * }
     * }
     * } catch (Exception e) {
     * System.err.println("Error al cargar el grafo: " + e.getMessage());
     * }
     * }
     * 
     * public void clearEdges() {
     * for (int i = 0; i < this.nro_Ver(); i++) {
     * this.adyacencias(i).reset();
     * }
     * }
     * 
     * public void loadModels() throws ListEmptyException, Exception {
     * UsuarioDao usuarioDao = new UsuarioDao();
     * TareaDao tareaDao = new TareaDao();
     * LinkedList<Usuario> usuarios = usuarioDao.getListAll();
     * LinkedList<Tarea> tareas = tareaDao.getListAll();
     * 
     * // Agregar vértices
     * for (int i = 0; i < usuarios.getSize(); i++) {
     * Usuario usuario = usuarios.get(i);
     * vertexModels.put(usuario.getId(), usuario);
     * System.out.println("Usuario cargado: " + usuario.getId() + " - " +
     * usuario.getNombre());
     * }
     * 
     * for (int i = 0; i < tareas.getSize(); i++) {
     * Tarea tarea = tareas.get(i);
     * vertexModels.put(tarea.getId(), tarea);
     * System.out.println("Tarea cargada: " + tarea.getId() + " - " +
     * tarea.getNombre());
     * }
     * 
     * // Crear aristas entre usuarios y sus tareas asignadas
     * for (int i = 0; i < tareas.getSize(); i++) {
     * Tarea tarea = tareas.get(i);
     * if (tarea.getIdUsuario() != null) {
     * // Conectar usuario con su tarea
     * add_edge(tarea.getIdUsuario(), tarea.getId(), 1.0f);
     * }
     * }
     * 
     * // Crear aristas entre tareas relacionadas
     * for (int i = 0; i < tareas.getSize(); i++) {
     * Tarea tareaA = tareas.get(i);
     * for (int j = i + 1; j < tareas.getSize(); j++) {
     * Tarea tareaB = tareas.get(j);
     * float distancia = calcularDistancia(tareaA, tareaB);
     * add_edge(tareaA.getId(), tareaB.getId(), distancia);
     * }
     * }
     * }
     * 
     * private float calcularDistancia(Tarea tareaA, Tarea tareaB) {
     * float distancia = 0;
     * 
     * // Factor de prioridad
     * int difPrioridad = Math.abs(tareaA.getPrioridad().ordinal() -
     * tareaB.getPrioridad().ordinal());
     * distancia += difPrioridad * 2; // Mayor peso a la diferencia de prioridad
     * 
     * // Factor de usuario
     * if (tareaA.getIdUsuario().equals(tareaB.getIdUsuario())) {
     * distancia += 1; // Tareas del mismo usuario están más cercanas
     * } else {
     * distancia += 5; // Tareas de diferentes usuarios están más distantes
     * }
     * return distancia;
     * }
     * 
     * public void loadGraphWithRandomEdges(String filename) throws Exception {
     * loadGraph(filename);
     * loadModels();
     * clearEdges();
     * 
     * Random random = new Random();
     * 
     * // Obtener listas de usuarios y tareas para manejarlas por separado
     * UsuarioDao usuarioDao = new UsuarioDao();
     * TareaDao tareaDao = new TareaDao();
     * LinkedList<Usuario> usuarios = usuarioDao.getListAll();
     * LinkedList<Tarea> tareas = tareaDao.getListAll();
     * 
     * // Para cada tarea, aseguramos conexiones mínimas
     * for (int i = 0; i < tareas.getSize(); i++) {
     * try {
     * Tarea tareaActual = tareas.get(i);
     * LinkedList<Adycencia> existingEdges = this.adyacencias(tareaActual.getId());
     * int connectionsCount = existingEdges.getSize();
     * 
     * // Aseguramos que cada tarea tenga al menos 3 conexiones
     * while (connectionsCount < 3) {
     * // Decidir aleatoriamente si conectar con un usuario o con otra tarea
     * boolean conectarConUsuario = random.nextBoolean();
     * 
     * if (conectarConUsuario && usuarios.getSize() > 0) {
     * // Conectar con un usuario aleatorio
     * int randomUserIndex = random.nextInt(usuarios.getSize());
     * Usuario randomUser = usuarios.get(randomUserIndex);
     * 
     * if (!is_Edge(tareaActual.getId(), randomUser.getId())) {
     * // El peso podría ser 1.0f o calcularse según algún criterio
     * add_edge(tareaActual.getId(), randomUser.getId(), 1.0f);
     * connectionsCount++;
     * }
     * } else {
     * // Conectar con otra tarea aleatoria
     * int randomTaskIndex = random.nextInt(tareas.getSize());
     * Tarea randomTarea = tareas.get(randomTaskIndex);
     * 
     * if (tareaActual.getId() != randomTarea.getId() &&
     * !is_Edge(tareaActual.getId(), randomTarea.getId())) {
     * 
     * float weight = calcularDistancia(tareaActual, randomTarea);
     * add_edge(tareaActual.getId(), randomTarea.getId(), weight);
     * connectionsCount++;
     * }
     * }
     * }
     * } catch (Exception e) {
     * System.err.println("Error al procesar tarea: " + e.getMessage());
     * }
     * }
     * saveGraph(filename);
     * }
     * 
     * // public void addVertexWithModel(Integer vertexId, Object model) {
     * // if (model instanceof Usuario || model instanceof Tarea) {
     * // vertexModels.put(vertexId, model);
     * // } else {
     * // throw new IllegalArgumentException("El modelo debe ser una instancia de
     * // Usuario o Tarea");
     * // }
     * // }
     * 
     * // Para usuarios
     * public void addVertexWithUsuario(Integer vertexId, Usuario usuario) {
     * vertexModels.put(vertexId, usuario);
     * }
     * 
     * // Para tareas
     * public void addVertexWithTarea(Integer vertexId, Tarea tarea) {
     * vertexModels.put(vertexId, tarea);
     * }
     * 
     * // Método genérico que decide cuál usar
     * public void addVertexWithModel(Integer vertexId, Object model) {
     * if (model instanceof Usuario) {
     * addVertexWithUsuario(vertexId, (Usuario) model);
     * } else if (model instanceof Tarea) {
     * addVertexWithTarea(vertexId, (Tarea) model);
     * } else {
     * throw new
     * IllegalArgumentException("El modelo debe ser una instancia de Usuario o Tarea"
     * );
     * }
     * }
     * 
     * public Boolean existFile(String filename) {
     * return Files.exists(Paths.get(path + filename));
     * }
     * 
     * public void guardarGrafo() {
     * try {
     * String filaname = "grafo.json";
     * saveGraph(filaname);
     * } catch (Exception e) {
     * System.err.println("Error al guardar el grafo: " + e.getMessage());
     * }
     * }
     * // me falta obtener los pesos de las aritas
     */
}
