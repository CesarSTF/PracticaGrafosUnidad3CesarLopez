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
            StringBuilder graphJson = new StringBuilder();
            graphJson.append("[");

            for (int i = 1; i <= graph.nro_Ver().intValue(); i++) {
                Integer vertexId = Integer.valueOf(i);
                graphJson.append("{");
                graphJson.append("\"id\":").append(vertexId).append(",");
                graphJson.append("\"nombre\":\"").append(graph.getLabelL(vertexId)).append("\",");

                LinkedList<Adycencia> adyacenciasList = graph.adyacencias(vertexId);
                graphJson.append("\"adyacencias\":[");
                if (adyacenciasList != null && !adyacenciasList.isEmpty()) {
                    Object[] adyArray = adyacenciasList.toArray();
                    for (int j = 0; j < adyArray.length; j++) {
                        if (adyArray[j] instanceof Adycencia) {
                            Adycencia ady = (Adycencia) adyArray[j];
                            graphJson.append("{");
                            graphJson.append("\"destino\":").append(ady.getDestination()).append(",");
                            graphJson.append("\"peso\":").append(ady.getWeight());
                            graphJson.append("}");
                            if (j < adyArray.length - 1) {
                                graphJson.append(",");
                            }
                        }
                    }
                }
                graphJson.append("],");
                graphJson.append("\"grado\":").append(adyacenciasList != null ? adyacenciasList.getSize() : 0);
                graphJson.append("}");
                if (i < graph.nro_Ver().intValue()) {
                    graphJson.append(",");
                }
            }

            graphJson.append("]");

            Files.createDirectories(Paths.get("media/", new String[0]));

            Files.write(
                    Paths.get("media/" + graphFileName),
                    graphJson.toString().getBytes(),
                    new OpenOption[0]);

            System.out.println("Grafo guardado correctamente en: media/" + graphFileName);
            System.out.println("JSON generado: \n" + graphJson.toString());

        } catch (Exception e) {
            System.err.println("Error al guardar el grafo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void loadGraph() {
        try {
            String content = new String(Files.readAllBytes(Paths.get("media/" + graphFileName)));
            JsonArray graphArray = JsonParser.parseString(content).getAsJsonArray();

            graph = new GrapLabelNoDirect<>(graphArray.size(), String.class);

            LinkedList<Integer> vertices = new LinkedList<>();

            for (JsonElement element : graphArray) {
                JsonObject vertex = element.getAsJsonObject();
                Integer id = vertex.get("id").getAsInt();
                String nombre = vertex.get("nombre").getAsString();
                graph.labelsVertices(id, nombre);
                vertices.add(id); 
            }

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
            int connections = random.nextInt(3) + 2;

            for (int i = 1; i <= graph.nro_Ver().intValue(); i++) {
                int attemptedConnections = 0;
                int successfulConnections = 0;

                while (successfulConnections < connections && attemptedConnections < 10) {
                    Integer destino = random.nextInt(graph.nro_Ver().intValue()) + 1;

                    if (!destino.equals(i) && !graph.is_Edge(i, destino).booleanValue()) {
                        float peso = random.nextFloat() * 10;
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
                return;
            }

            LinkedList<Usuario> usuarios = this.listAll();
            if (!usuarios.isEmpty()) {
                this.graph = new GrapLabelNoDirect<>(usuarios.getSize(), String.class);
                Usuario[] usuariosArray = new Usuario[usuarios.getSize()];
                for (int i = 0; i < usuarios.getSize(); i++) {
                    usuariosArray[i] = usuarios.get(i);
                }

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

    public String caminoCorto(int origen, int destino, int algoritmo) throws Exception {
        loadGraph();
        if (graph == null) {
            throw new Exception("Grafo no existe");
        }

        System.out.println("Calculando camino corto desde " + origen + " hasta " + destino);

        String camino = "";

        if (algoritmo == 1) {
            Floyd floydWarshall = new Floyd(graph, origen, destino);
            camino = floydWarshall.caminoCorto();

            saveMatricesToJson(floydWarshall.getMatrices(), "floyd_matrices.json");
        } else {
            BellmanFord bellmanFord = new BellmanFord(graph, origen, destino);
            camino = bellmanFord.caminoCorto(algoritmo);

            saveMatricesToJson(bellmanFord.getMatrices(), "bellman_ford_matrices.json");
        }

        System.out.println("Camino corto calculado: " + camino);
        return camino;
    }

    private void saveMatricesToJson(Object matrices, String fileName) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonOutput = gson.toJson(matrices);

            Files.createDirectories(Paths.get("media/", new String[0]));
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