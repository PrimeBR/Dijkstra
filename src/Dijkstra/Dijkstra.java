package Dijkstra;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

import java.util.*;

import static java.lang.Double.POSITIVE_INFINITY;

public class Dijkstra {
    /**
     * step - метка, указывающая, какой шаг алгоритма должен выполнятся следующим
     * distance - карта вершин и расстояний до них
     * unvisitedVertices - список непросмотренных вершин
     * outgoingEdges - карта вершин и выходящих из них ребер
     * parents - список пар вершин, по которым восстанавливаются кратчайшие пути
     * graph - граф, в котором ищутся кратчайшие пути
     * source - начальная вершина в алгоритме Дейкстры
     */
    private Steps step = Steps.UNVISITED_VERTEX_SELECTION;
    private HashMap<Object, Double> distance;
    private ArrayList<Object> unvisitedVertices;
    private HashMap<Object, TreeSet<Object>> outgoingEdges;
    private HashMap<Object, Object> parents;
    private mxGraph graph;
    private Object source;
    public enum Steps{UNVISITED_VERTEX_SELECTION, NEAREST_NEIGHBOR_SELECTION, RELAXATION};


    public Dijkstra(mxGraph graph, Object source) {
        distance = new HashMap<>();
        unvisitedVertices = new ArrayList<>();
        outgoingEdges = new HashMap<>();
        parents = new HashMap<>();
        this.graph = graph;
        this.source = source;

        /**
         * изначально все вершины считаются непросмотренными и расстояния до них равно бесконечности
         */
        for (Object v : graph.getChildVertices(graph.getDefaultParent())) {
            distance.put(v, POSITIVE_INFINITY);
            unvisitedVertices.add(v);
        }

        /**
         * расстояние от начальной вершины до себя, очевидно, равно 0
         */
        distance.put(source, 0.0);

        /**
         * дублирование вершин с выходящими из них ребрами для возможности реализации визуализации
         */
        for (Object v : graph.getChildVertices(graph.getDefaultParent())) {
            TreeSet<Object> set = new TreeSet<>(new EdgeComparator());
            set.addAll(Arrays.asList(graph.getOutgoingEdges(v)));
            outgoingEdges.put(v, set);
        }

    }

    public Steps getStep() {
        return step;
    }

    public boolean isNextStep() {
        return !unvisitedVertices.isEmpty();
    }

    public void removeVertex(Object vertex, Object edge) {
        if (edge.equals(vertex))
            unvisitedVertices.remove(vertex);
    }

    /**
     * главный метод алгоритма Дейкстры разбит на небольшие методы
     * опять же для возможности реализации визуализации
     */
    public void getPaths() {
        Object currVertex = new mxCell();
        Object currEdge = new mxCell();
        while (isNextStep())
            switch (step) {
                case UNVISITED_VERTEX_SELECTION:
                    currVertex = selectUnvisitedVertex();
                    break;
                case NEAREST_NEIGHBOR_SELECTION:
                    currEdge = selectNearestNeighbor(currVertex);
                    removeVertex(currVertex, currEdge);
                    break;
                case RELAXATION:
                    relax(currEdge);
                    break;
            }
    }

    /**
     * выбор следующей просматриваемой вершины из еще непросмотренных
     */
    public Object selectUnvisitedVertex() {
        step = Steps.NEAREST_NEIGHBOR_SELECTION;

        /**
         * выбирается непросмотренная вершина с наименьшим текущем расстоянии до начальной вершины
         */
        Object vertex = new mxCell();
        double mindistance = minDistance();
        for (Object v: distance.keySet()) {
            if (unvisitedVertices.contains(v) && distance.get(v).equals(mindistance)) {
                vertex = v;
                break;
            }
        }

        return vertex;
    }

    /**
     * выбор непросмотренной вершины, ближайшей к текущей просматриваемой
     */
    public Object selectNearestNeighbor(Object vertex) {
        if (outgoingEdges.get(vertex).isEmpty()) {
            step = Steps.UNVISITED_VERTEX_SELECTION;
            return vertex;
        }
        else if (unvisitedVertices.contains(((mxCell) outgoingEdges.get(vertex).first()).getTarget())) {
            step = Steps.RELAXATION;
            Object result = outgoingEdges.get(vertex).first();
            outgoingEdges.get(vertex).remove(result);
            return result;
        }
        else {
            Object result = outgoingEdges.get(vertex).first();
            outgoingEdges.get(vertex).remove(result);
            return vertex;
        }
    }

    /**
     * обновление расстояния до вершины
     */
    public double relax(Object edge) {
        Object source = ((mxCell) edge).getSource();
        Object target = ((mxCell) edge).getTarget();
        double value = (double)((mxCell) edge).getValue();

        double newDistance = Math.min(distance.get(target), distance.get(source) + value);

        if (newDistance < distance.get(target))
            parents.put(target, source);
        distance.put(target, newDistance);
        step = Steps.NEAREST_NEIGHBOR_SELECTION;

        return newDistance;
    }

    /**
     * поиск кратчайшего расстояния до непросмотренных вершин
     */
    private double minDistance() {
        double result = POSITIVE_INFINITY;
        for (Object v: unvisitedVertices)
            if (distance.get(v) < result)
                result = distance.get(v);

        return result;
    }

    /**
     *  восстановление пути до вершины
     */
    public String pathRestoration(Object v) {
        StringBuilder builder = new StringBuilder();
        Stack<Object> stack = new Stack<>();

        do {
            stack.push(v);
            v = parents.get(v);
        } while (!v.equals(source));
        stack.push(source);

        while (!stack.empty())
            builder.append(((mxCell) stack.pop()).getId().toString() + " ");
        builder.append("\n");

        return builder.toString();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry e: distance.entrySet()) {
            if (!((mxCell) e.getKey()).equals(source)) {
                builder.append("vertex = ");
                builder.append(((mxCell) e.getKey()).getId());
                builder.append(", distance = ");
                builder.append(e.getValue().toString());
                builder.append("\n");
            }
        }

        builder.append("\n" + "Paths:" + "\n");

        for (Object v: parents.keySet())
            builder.append(pathRestoration(v));

        return builder.toString();
    }
}
