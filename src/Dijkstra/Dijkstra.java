package Dijkstra;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

import javax.swing.*;
import java.util.*;

import static java.lang.Double.POSITIVE_INFINITY;
import static java.lang.Double.min;

public class Dijkstra {
    public enum Steps{UNVISITED_VERTEX_SELECTION, NEAREST_NEIGHBOR_SELECTION, RELAXATION};

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

    /**
     * получение текущего значения step для координирования выполнения алгоритма
     */
    public Steps getStep() {
        return step;
    }

    /**
     * алгоритм заканчивается, когда все вершины просмотрены,
     * либо когда расстояния до всех оставшихся непросмотренных вершин равно бесконености
     */
    public boolean isNextStep() {
        return !unvisitedVertices.isEmpty() && minDistance() < POSITIVE_INFINITY;
    }

    /**
     * после просмотра всех исходящих ребер из вершины
     * она удаляется из списка непросмотренных
     */
    public void removeVertex(Object vertex, Object edge) {
        if (edge.equals(vertex))
            unvisitedVertices.remove(vertex);
    }

    /**
     * выбор следующей просматриваемой вершины из еще непросмотренных
     */
    public Object selectUnvisitedVertex(JTextPane textPane) {
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

        textPane.setText(textPane.getText() + "выбрана вершина '" + ((mxCell) vertex).getId() + "'" + "\n");
        return vertex;
    }

    /**
     * выбор непросмотренной вершины, ближайшей к текущей просматриваемой
     */
    public Object selectNearestNeighbor(Object vertex, JTextPane textPane) {
        if (outgoingEdges.get(vertex).isEmpty()) {
            step = Steps.UNVISITED_VERTEX_SELECTION;
            textPane.setText(textPane.getText() + "  все исходящие ребра просмотрены" + "\n");
            return vertex;
        }
        else if (unvisitedVertices.contains(((mxCell) outgoingEdges.get(vertex).first()).getTarget())) {
            step = Steps.RELAXATION;
            Object result = outgoingEdges.get(vertex).first();
            outgoingEdges.get(vertex).remove(result);
            textPane.setText(textPane.getText() + "  выбрано ребро до вершины '" + ((mxCell) result).getTarget().getId() + "'" + "\n");
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
    public double relax(Object edge, JTextPane textPane) {
        Object source = ((mxCell) edge).getSource();
        Object target = ((mxCell) edge).getTarget();
        double value = (double)((mxCell) edge).getValue();

        double newDistance = Math.min(distance.get(target), distance.get(source) + value);

        if (newDistance < distance.get(target)) {
            textPane.setText(textPane.getText() + "    релаксация прошла успешно:" + "\n" + "      " + distance.get(source) +
            " + " + value + " < " + distance.get(target) + "\n");
            parents.put(target, source);
        }
        else {
            textPane.setText(textPane.getText() + "    релаксация прошла неуспешно:" + "\n" + "      " + distance.get(source)
            + " + " + value + " ≥ " + distance.get(target) + "\n");
        }
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
                builder.append("вершина = ");
                builder.append(((mxCell) e.getKey()).getId());
                builder.append(", расстояние = ");
                builder.append(e.getValue().toString());
                builder.append("\n");
            }
        }

        builder.append("\n" + "пути:" + "\n");

        for (Object v: parents.keySet())
            builder.append(pathRestoration(v));

        return builder.toString();
    }
}
