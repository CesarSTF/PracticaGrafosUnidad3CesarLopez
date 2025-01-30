package com.practicaGrafos.controller.dao;

import com.practicaGrafos.controller.dao.implement.Contador;
import com.practicaGrafos.controller.excepcion.ValueAlreadyExistException;
import com.practicaGrafos.models.Usuario;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.practicaGrafos.controller.dao.implement.AdapterDao;
import com.practicaGrafos.controller.tda.graph.Adycencia;
import com.practicaGrafos.controller.tda.graph.GrapLabelNoDirect;
import com.practicaGrafos.controller.tda.graph.algoritmos.BellmanFord;
import com.practicaGrafos.controller.tda.graph.algoritmos.Floyd;
import com.practicaGrafos.controller.tda.list.LinkedList;

@SuppressWarnings({ "unchecked", "ConvertToTryWithResources" })
public class UsuarioDao extends AdapterDao<Usuario> {
    private Usuario usuario;
    private LinkedList<Usuario> listAll;
    private GrapLabelNoDirect<String> graph;
    private LinkedList<String> vertexName;
    private static final String GRAPH_PATH = "media/";
    private String graphFileName = "usuarioGrafo.json";

    public UsuarioDao() {
        super(Usuario.class);
    }

    public GrapLabelNoDirect<String> creategraph() {
        if (vertexName == null) {
            vertexName = new LinkedList<>();
        }
        if (graph == null) {
            initializeGraph();
        }
        return this.graph;
    }

    public void saveGraph() {
        if (graph == null) {
            System.err.println("Error: El grafo no está inicializado.");
            return;
        }

        try {
            JsonArray graphArray = new JsonArray();

            // Iterar sobre todos los vértices
            for (int i = 1; i <= graph.nro_Ver().intValue(); i++) {
                Integer vertexId = Integer.valueOf(i);
                JsonObject vertex = new JsonObject();

                // Información básica del vértice
                vertex.addProperty("id", vertexId);
                vertex.addProperty("nombre", (String) graph.getLabelL(vertexId));

                // Obtener y procesar las adyacencias
                LinkedList<Adycencia> adyacenciasList = graph.adyacencias(vertexId);
                JsonArray edges = new JsonArray();

                if (adyacenciasList != null && !adyacenciasList.isEmpty()) {
                    Object[] adyArray = adyacenciasList.toArray();
                    for (Object obj : adyArray) {
                        if (obj instanceof Adycencia) {
                            Adycencia ady = (Adycencia) obj;
                            JsonObject edge = new JsonObject();
                            edge.addProperty("destino", ady.getDestination());
                            edge.addProperty("peso", ady.getWeight());
                            edges.add(edge);
                        }
                    }
                }

                // Añadir el array de adyacencias al vértice
                vertex.add("adyacencias", edges);
                vertex.addProperty("grado", edges.size());

                // Añadir el vértice al array principal
                graphArray.add(vertex);
            }

            // Crear el directorio si no existe
            Files.createDirectories(Paths.get("media/", new String[0]));

            // Convertir a JSON con formato legible
            String jsonOutput = new GsonBuilder()
                    .setPrettyPrinting()
                    .create()
                    .toJson(graphArray);

            // Guardar el archivo
            Files.write(
                    Paths.get("media/" + graphFileName),
                    jsonOutput.getBytes(),
                    new OpenOption[0]);

            System.out.println("Grafo guardado correctamente en: media/" + graphFileName);
            System.out.println("JSON generado: \n" + jsonOutput);

        } catch (Exception e) {
            System.err.println("Error al guardar el grafo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void loadGraph() {
        try {
            String content = new String(Files.readAllBytes(Paths.get("media/" + graphFileName)));
            JsonArray graphArray = JsonParser.parseString(content).getAsJsonArray();

            // Inicializar el grafo con el número correcto de vértices
            graph = new GrapLabelNoDirect(Integer.valueOf(graphArray.size()), String.class);

            // Primera pasada: establecer las etiquetas de los vértices
            for (JsonElement element : graphArray) {
                JsonObject vertex = element.getAsJsonObject();
                Integer id = vertex.get("id").getAsInt();
                String nombre = vertex.get("nombre").getAsString();
                graph.labelsVertices(id, nombre);
            }

            // Segunda pasada: establecer las adyacencias
            for (JsonElement element : graphArray) {
                JsonObject vertex = element.getAsJsonObject();
                Integer sourceId = vertex.get("id").getAsInt();

                JsonArray adyacencias = vertex.getAsJsonArray("adyacencias");
                if (adyacencias != null) {
                    for (JsonElement adyElement : adyacencias) {
                        JsonObject ady = adyElement.getAsJsonObject();
                        Integer destinoId = ady.get("destino").getAsInt();
                        Float peso = ady.get("peso").getAsFloat();

                        graph.add_edge(sourceId, destinoId, peso);
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error al cargar el grafo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void generateRandomConnections() {
        try {
            Random random = new Random();
            int connections = random.nextInt(3) + 2; // Genera entre 2 y 4 conexiones por vértice

            // Para cada vértice
            for (int i = 1; i <= graph.nro_Ver().intValue(); i++) {
                int attemptedConnections = 0;
                int successfulConnections = 0;

                // Intentar crear el número deseado de conexiones
                while (successfulConnections < connections && attemptedConnections < 10) {
                    Integer destino = random.nextInt(graph.nro_Ver().intValue()) + 1;

                    // Evitar auto-conexiones y conexiones duplicadas
                    if (!destino.equals(i) && !graph.is_Edge(i, destino).booleanValue()) {
                        float peso = random.nextFloat() * 10; // Peso entre 0 y 10
                        graph.add_edge(i, destino, peso);
                        System.out.println("Conexión generada entre: " + i + " y " + destino + " con peso: " + peso);
                        successfulConnections++;
                    }
                    attemptedConnections++;
                }
            }
        } catch (Exception e) {
            System.err.println("Error al generar conexiones aleatorias: " + e.getMessage());
        }
    }

    public void initializeGraph() {
        try {
            if (this.graph != null) {
                return; // El grafo ya está inicializado
            }

            LinkedList<Usuario> usuarios = this.listAll();
            if (!usuarios.isEmpty()) {
                this.graph = new GrapLabelNoDirect<>(usuarios.getSize(), String.class);
                Usuario[] usuariosArray = new Usuario[usuarios.getSize()];
                for (int i = 0; i < usuarios.getSize(); i++) {
                    usuariosArray[i] = usuarios.get(i);
                }

                // Inicializar vértices con nombres de usuarios
                for (int i = 0; i < usuariosArray.length; i++) {
                    this.graph.labelsVertices(i + 1, usuariosArray[i].getNombre());
                    // this.graph.insertEdgeL(usuariosArray[i].getNombre(),
                    // usuariosArray[i].getNombre(), 0.0f);
                }
            }
            generateRandomConnections();
        } catch (Exception e) {
            System.err.println("Error al inicializar el grafo: " + e.getMessage());
        }
    }

    public boolean existsGraphFile() {
        return Files.exists(Paths.get(GRAPH_PATH + graphFileName));
    }

    // Getters y setters para el grafo
    public GrapLabelNoDirect<String> getGraph() {
        return this.graph;
    }

    public void setGraphFileName(String fileName) {
        this.graphFileName = fileName;
    }

    public Usuario getUsuario() {
        if (this.usuario == null) {
            this.usuario = new Usuario();
        }
        return this.usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LinkedList<Usuario> getListAll() throws Exception {
        if (listAll == null) {
            this.listAll = listAll();
        }
        return listAll;
    }

    public boolean save() throws Exception {
        Integer id = Contador.obtenerValorActual(Usuario.class);
        try {
            this.usuario.setId(id);
            this.persist(this.usuario);
            Contador.actualizarContador(Usuario.class);
            this.listAll = listAll();
            return true;
        } catch (Exception e) {
            throw new Exception("Error al guardar el usuario: " + e.getMessage());
        }
    }

    public Boolean update() throws Exception {
        if (this.usuario == null || this.usuario.getId() == null) {
            throw new Exception("No se ha seleccionado un usuario para actualizar.");
        }
        if (listAll == null) {
            listAll = listAll();
        }
        Integer index = getUsuarioIndex("id", this.usuario.getId());
        if (index == -1) {
            throw new Exception("Usuario no encontrado.");
        }
        try {
            this.merge(this.usuario, index);
            listAll = listAll();
            return true;
        } catch (Exception e) {
            throw new Exception("Error al actualizar el usuario: " + e.getMessage());
        }
    }

    public Boolean delete() throws Exception {
        if (this.usuario == null || this.usuario.getId() == null) {
            throw new Exception("No se ha seleccionado un usuario para eliminar.");
        }
        if (listAll == null) {
            listAll = listAll();
        }
        Integer index = getUsuarioIndex("id", this.usuario.getId());
        if (index == -1) {
            throw new Exception("Usuario no encontrado.");
        }
        try {
            this.delete(index);
            listAll = listAll();
            return true;
        } catch (Exception e) {
            throw new Exception("Error al eliminar el usuario: " + e.getMessage());
        }
    }

    private LinkedList<Usuario> linearBinarySearch(String attribute, Object value) throws Exception {
        LinkedList<Usuario> lista = this.listAll().quickSort(attribute, 1);
        LinkedList<Usuario> trabajadors = new LinkedList<>();
        if (!lista.isEmpty()) {
            Usuario[] aux = lista.toArray();
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

    public LinkedList<Usuario> buscar(String attribute, Object value) throws Exception {
        return linearBinarySearch(attribute, value);
    }

    public Usuario buscarPor(String attribute, Object value) throws Exception {
        LinkedList<Usuario> lista = listAll();
        Usuario p = null;

        try {
            if (!lista.isEmpty()) {
                Usuario[] trabajadors = lista.toArray();
                for (int i = 0; i < trabajadors.length; i++) {
                    if (obtenerAttributeValue(trabajadors[i], attribute).toString().toLowerCase()
                            .equals(value.toString().toLowerCase())) {
                        p = trabajadors[i];
                        break;
                    }
                }
            }
            if (p == null) {
                throw new Exception("No se encontró el usuario con " + attribute + ": " + value);
            }
        } catch (Exception e) {
            throw new Exception("Error al buscar el usuario: " + e.getMessage());
        }

        return p;
    }

    private Integer getUsuarioIndex(String attribute, Object value) throws Exception {
        if (this.listAll == null) {
            this.listAll = listAll();
        }
        Integer index = -1;
        if (!this.listAll.isEmpty()) {
            Usuario[] trabajadors = this.listAll.toArray();
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
        for (Method m : Usuario.class.getDeclaredMethods()) {
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

        Usuario[] correos = this.listAll.toArray();

        for (Usuario usuario : correos) {
            Object attributeValue = obtenerAttributeValue(usuario, campo);
            if (attributeValue != null && attributeValue.toString().equalsIgnoreCase(value.toString())) {
                throw new ValueAlreadyExistException("El correo ya existe.");
            }
        }

        return true;
    }

    public LinkedList<Usuario> order(String attribute, Integer type) throws Exception {
        LinkedList<Usuario> lista = listAll();
        return lista.isEmpty() ? lista : lista.mergeSort(attribute, type);
    }

    public String toJson() throws Exception {
        Gson g = new Gson();
        return g.toJson(this.usuario);
    }

    public Usuario getById(Integer id) throws Exception {
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

    // Método para calcular el camino corto
    public String caminoCorto(int origen, int destino, int algoritmo) throws Exception {
        // Cargar el grafo desde el archivo .json
        loadGraph();

        if (graph == null) {
            throw new Exception("Grafo no existe");
        }

        System.out.println("Calculando camino corto desde " + origen + " hasta " + destino);

        String camino = "";

        if (algoritmo == 1) { // Usar algoritmo de Floyd
            Floyd floydWarshall = new Floyd(graph, origen, destino);
            camino = floydWarshall.caminoCorto(); // Se asume que Floyd tiene un método para calcular el camino corto

            // Guardar las matrices de adyacencia en un nuevo archivo .json
            saveMatricesToJson(floydWarshall.getMatrices(), "floyd_matrices.json");
        } else { // Usar algoritmo de Bellman-Ford (o cualquier otro algoritmo como Dijkstra)
            BellmanFord bellmanFord = new BellmanFord(graph, origen, destino);
            camino = bellmanFord.caminoCorto(algoritmo); // Se asume que BellmanFord tiene un método para calcular el
                                                         // camino corto

            // Guardar las matrices de adyacencia en un nuevo archivo .json
            saveMatricesToJson(bellmanFord.getMatrices(), "bellman_ford_matrices.json");
        }

        System.out.println("Camino corto calculado: " + camino);
        return camino; // Regresar el camino calculado
    }

    private void saveMatricesToJson(Object matrices, String fileName) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonOutput = gson.toJson(matrices);

            // Crear el directorio si no existe
            Files.createDirectories(Paths.get("media/", new String[0]));

            // Guardar el archivo
            Files.write(
                    Paths.get("media/" + fileName),
                    jsonOutput.getBytes(),
                    new OpenOption[0]);

            System.out.println("Matrices guardadas correctamente en: media/" + fileName);
            System.out.println("JSON generado: \n" + jsonOutput);

        } catch (Exception e) {
            System.err.println("Error al guardar las matrices: " + e.getMessage());
            e.printStackTrace();
        }
    }
}