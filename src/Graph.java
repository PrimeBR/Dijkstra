import Dijkstra.*;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.model.*;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

public class Graph {
    private Dijkstra test;
    private mxCircleLayout layout;
    private mxGraph graph;
    private Object parent;
    private mxGraphModel model;
    private mxGraphComponent graphComponent;

    private Object currV = new mxCell();
    private Object currE = new mxCell();
    private Object cell = new mxCell();

    private boolean checker = false;
    private Object start;

    public Dijkstra getTest() { return test; }

    public Object getStart() { return start; }

    public void initCircleLayout() {
        layout = new mxCircleLayout(graph);
        int radius = 150;
        layout.setX0((GUI.getDimension().width / 2.0) - 1.33 * radius);
        layout.setY0((GUI.getDimension().height / 2.0) - 1.1 * radius);
        layout.setRadius(radius);
        layout.setMoveCircle(true);
        layout.execute(graph.getDefaultParent());
    }

    /**
     * Метод инициализации графа
     */
    public void initGraph() {
        graph = new mxGraph();
        parent = graph.getDefaultParent();
        graphComponent = new mxGraphComponent(graph);
    }

    public mxGraphComponent getGraphComponent() {
        return graphComponent;
    }

    public void fileReader(BufferedReader br) throws IOException {
        graph.removeCells(graph.getChildCells(graph.getDefaultParent(), true, true));
        String strLine;
        while ((strLine = br.readLine()) != null) {
            String[] parts = strLine.split(" ", 3);
            if (parts.length < 3) {
                JOptionPane.showMessageDialog(null, "Некорректная строка: \"" + strLine +
                        "\"\nЗадавайте ребра в виде: \"начальная вершина\" \"конечная вершина\" \"вес\"", "Warning!", JOptionPane.PLAIN_MESSAGE);
                graph.removeCells(graph.getChildCells(graph.getDefaultParent(), true, true));
                return;
            }
            Object From = null;
            Object To = null;
            for (Object v : graph.getChildVertices(graph.getDefaultParent())) {
                if (parts[0].equals(((mxCell) v).getValue().toString()))
                    From = v;
                if (parts[1].equals(((mxCell) v).getValue().toString()))
                    To = v;
            }
            if (From == null) {
                From = graph.insertVertex(parent, null, parts[0], 100, 100, 45, 45, "shape=ellipse");
                ((mxCell) From).setId(parts[0]);
                layout.execute(graph.getDefaultParent());
            }
            if (To == null) {
                To = graph.insertVertex(parent, null, parts[1], 100, 100, 45, 45, "shape=ellipse");
                ((mxCell) To).setId(parts[1]);
                layout.execute(graph.getDefaultParent());
            }
            if (!DoubleParser((mxCell) From, (mxCell) To, parts[2], strLine))
                return;
        }
        GUI.getNextButton().setEnabled(true);
        GUI.getExecuteButton().setEnabled(true);
        GUI.getFileButton().setEnabled(true);

        GUI.getLogsPane().setText("");
    }

    public void addVertexButtonEventListener(String input) {
        if (input != null) {
            if (input.equals(""))
                return;
            for (Object v : graph.getChildVertices(graph.getDefaultParent())) {
                if (input.equals(((mxCell) v).getValue())) {
                    JOptionPane.showMessageDialog(null, "Данная вершина уже есть в графе", "Warning!", JOptionPane.PLAIN_MESSAGE);
                    return;
                }
            }
            Object tmp = graph.insertVertex(parent, null, input, 100, 100, 45, 45, "shape=ellipse");
            ((mxCell) tmp).setId(input);
            layout.execute(graph.getDefaultParent());
            GUI.getNextButton().setEnabled(true);
            GUI.getExecuteButton().setEnabled(true);
        }
    }

    public boolean DoubleParser(mxCell From, mxCell To, String weight, String strLine) {
        try {
            if(Double.parseDouble(weight) < 0) {
                JOptionPane.showMessageDialog(null, "Введено ребро отрицательного веса", "Warning!", JOptionPane.PLAIN_MESSAGE);
                return false;
            }
            if(isEnable(From, To) && isEnable(To, From))
                graph.insertEdge(parent, null, Double.parseDouble(weight), From, To);
            return true;
        }
        catch (NumberFormatException w) {
            JOptionPane.showMessageDialog(null, "Неккоректная строка: \"" + strLine +
                    "\"\n<html><i>Совет: используйте \".\" для разделения целой и дробной частей.", "Warning!", JOptionPane.PLAIN_MESSAGE);
            graph.removeCells (graph.getChildCells (graph.getDefaultParent (), true, true));
            return false;
        }
    }

    public boolean nextIteration() {
        if(!checker) {
            if(!checkStartVertex())
                return false;
        }
        if (test.getStep() == Dijkstra.Steps.NEAREST_NEIGHBOR_SELECTION)
            cell = currV;
        else if (test.getStep() == Dijkstra.Steps.RELAXATION)
            cell = currE;
        Object result = nextStep(cell);
        if (result instanceof Double) {
            Object target = ((mxCell) cell).getTarget();
            ((mxCell) target).setValue("\n\n"+ ((mxCell) target).getId() + "\n\n(" + result + ")");
            graph.setCellStyle("defaultVertex;shape=ellipse", new Object[]{target});
            graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, "FA8072", new Object[]{cell});
        }
        else if(((mxCell) result).isEdge()) {
            cell = result;
            graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, "FA8072", new Object[]{cell});
        }
        else if(((mxCell) result).isVertex()) {
            if(cell.equals(result))
                graph.setCellStyle("defaultVertex;shape=ellipse;fillColor=#A9A9A9", new Object[]{result});
            else
                graph.setCellStyle("defaultVertex;shape=ellipse;fillColor=#FA8072", new Object[]{result});
            cell = result;
        }

        if (!test.isNextStep()) {
            GUI.getNextButton().setEnabled(false);
            GUI.getAddEdgeButton().setEnabled(false);
            GUI.getAddVertexButton().setEnabled(false);
            GUI.getShowResultAlgoButton().setEnabled(true);
            GUI.getSaveButton().setEnabled(true);
            //    System.out.println(test.toString());
            return false;
        }
        else {
            GUI.getAddEdgeButton().setEnabled(false);
            GUI.getAddVertexButton().setEnabled(false);
            return true;
        }
    }

    private boolean checkStartVertex() {
        /*
        tmp - массив объектов со всеми вершинами графа
        vertices - массив строк с названиями всех вершин
         */
        Object[] tmp = graph.getChildVertices(graph.getDefaultParent());
        ArrayList<String> vertices = new ArrayList<>();
        for(Object v: tmp)
            vertices.add((String)((mxCell) v).getValue());
        String[] array = vertices.toArray(new String[vertices.size()]);
        Object result = JOptionPane.showInputDialog(
                null,
                "Выбирете начальную вершину :",
                "Выбор вершины",
                JOptionPane.QUESTION_MESSAGE, null,
                array, array[0]);
        if(result == null) return false;
//        start = result;
        for(Object v: tmp)
            if((result.toString()).equals(((mxCell) v).getValue())) {
                checker = true;
                graph.setCellStyle("defaultVertex;shape=ellipse;fillColor=#A9A9A9", new Object[]{v});
                start = v;
                test = new Dijkstra(graph, v);
                return true;
            }
        return false;
    }

    public mxCell cellFounder(String result) {
        for(Object v: graph.getChildVertices(graph.getDefaultParent())) {
            if (result.equals(((mxCell) v).getValue())) {
                return (mxCell) v;
            }
        }
        return null;
    }

    public Object nextStep(Object cell) {
        Object result = new mxCell();
        switch (test.getStep()) {
            case UNVISITED_VERTEX_SELECTION:
                currV = test.selectUnvisitedVertex(GUI.getLogsPane());
                result = currV;
                break;
            case NEAREST_NEIGHBOR_SELECTION:
                currE = test.selectNearestNeighbor(cell, GUI.getLogsPane());
                test.removeVertex(cell, currE);
                result = currE;
                break;
            case RELAXATION:
                result = test.relax(cell, GUI.getLogsPane());
                break;
        }

        return result;
    }

    public boolean isEnable(Object v1, Object v2) {
        for(Object v: graph.getEdgesBetween(v1, v2))
            if(((mxCell) v).getTarget().equals(v1)) {
                JOptionPane.showMessageDialog(null, "Между вершинами " + ((mxCell) v1).getValue().toString()
                        + " и " + ((mxCell) v2).getValue().toString() + " уже существует ребро!", "Warning!", JOptionPane.PLAIN_MESSAGE);
                return false;
            }
        return true;
    }
}


