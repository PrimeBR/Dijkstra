import Dijkstra.*;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.model.*;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

class GAdapter {
    /**
     * dijkstra - объект класса Dijkstra
     * layout - круговой макет
     * graph - граф, с которым необходимо работать
     * parent - рродитель для всех остальных ячеек
     * graphComponent - компонента для вывода графа в окно
     * currV - текущая вершина
     * currE - текущее ребро
     * cell - текущая ячейка
     * checker - флаг для проверки наличия в графе начальной вершины
     * start - наальная вершина в графе
     */
    private Dijkstra dijkstra;
    private mxCircleLayout layout;
    private mxGraph graph;
    private Object parent;
    private mxGraphComponent graphComponent;

    private Object currV = new mxCell();
    private Object currE = new mxCell();
    private Object cell = new mxCell();

    private boolean checker = false;
    private Object start;

    /**
     * Геттер для получения объект класса Dijkstra
     */
    Dijkstra getDijkstra() { return dijkstra; }

    /**
     * Геттер для получения объекта начальной вершины
     */
    Object getStart() { return start; }

    /**
     * Метод инициализации кругового макета
     */
    void initCircleLayout() {
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
    void initGraph() {
        graph = new mxGraph();
        parent = graph.getDefaultParent();
        graphComponent = new mxGraphComponent(graph);
    }

    /**
     * Геттер для получения компоненты графа
     */
    mxGraphComponent getGraphComponent() {
        return graphComponent;
    }

    /**
     * Метод для считывания из файла
     */
    void fileReader(JFileChooser jFileChooser, Container contentPane) {
        int i = jFileChooser.showOpenDialog(contentPane);
        File pathToFile = jFileChooser.getCurrentDirectory();
        File file = jFileChooser.getSelectedFile();
        JOptionPane jOptionPane = new JOptionPane();
        if (i == jFileChooser.APPROVE_OPTION && file.getName().endsWith("txt")) {
            try {
                graph.removeCells(graph.getChildCells(graph.getDefaultParent(), true, true));
                GUI.getShowResultAlgoButton().setEnabled(false);
                GUI.getAddVertexButton().setEnabled(true);
                GUI.getAddEdgeButton().setEnabled(true);
                checker = false;
                File f = new File(pathToFile.toString(), file.getName());
                BufferedReader br = new BufferedReader(new FileReader(f));
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

                GUI.clearLog();
            } catch (IOException error) {
                JOptionPane.showMessageDialog(null, "Ошибка!", "Warning!", JOptionPane.PLAIN_MESSAGE);
            }
        } else if (i == jFileChooser.CANCEL_OPTION) {
        } else {
            jOptionPane.showMessageDialog(null, "<html><h2>Ошибка открытия файла!</h2><p>" + "<html><h2>Выберете файл с расширение *.txt!</h2><p>", "Ошибка сохранения файла", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Метод для добавления вершины в граф
     */
    void addVertexButtonEventListener(String input) {
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

    /**
     * Метод для проверки введённого для реьра веса и добавления его в граф
     */
    boolean DoubleParser(mxCell From, mxCell To, String weight, String strLine) {
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

    /**
     * Метод перехода к следующей итерации
     */
    boolean nextIteration() {
        if(!checker) {
            if(!checkStartVertex())
                return false;
        }
        if (dijkstra.getStep() == Dijkstra.Steps.NEAREST_NEIGHBOR_SELECTION)
            cell = currV;
        else if (dijkstra.getStep() == Dijkstra.Steps.RELAXATION)
            cell = currE;
        Object result = nextStep(cell);
        if (result instanceof Double) {
            Object target = ((mxCell) cell).getTarget();
            ((mxCell) target).setValue("\n\n"+ ((mxCell) target).getId() + "\n\n(" + String.format("%.3f", result) + ")");
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

        if (!dijkstra.isNextStep()) {
            GUI.getNextButton().setEnabled(false);
            GUI.getAddEdgeButton().setEnabled(false);
            GUI.getAddVertexButton().setEnabled(false);
            GUI.getShowResultAlgoButton().setEnabled(true);
            GUI.getSaveButton().setEnabled(true);
            return false;
        }
        else {
            GUI.getShowResultAlgoButton().setEnabled(true);
            GUI.getAddEdgeButton().setEnabled(false);
            GUI.getAddVertexButton().setEnabled(false);
            return true;
        }
    }

    /**
     * Метод для проверки наличия в графе начальной вершины
     */
    private boolean checkStartVertex() {
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
        for(Object v: tmp)
            if((result.toString()).equals(((mxCell) v).getValue())) {
                checker = true;
                graph.setCellStyle("defaultVertex;shape=ellipse;fillColor=#A9A9A9", new Object[]{v});
                start = v;
                dijkstra = new Dijkstra(graph, v);
                return true;
            }
        return false;
    }

    /**
     * Метод для поиска вершины в графе
     */
    mxCell cellFounder(String result) {
        for(Object v: graph.getChildVertices(graph.getDefaultParent())) {
            if (result.equals(((mxCell) v).getValue())) {
                return (mxCell) v;
            }
        }
        return null;
    }

    /**
     * Метод для выбора объекта на следующей итерации
     */
    private Object nextStep(Object cell) {
        Object result = new mxCell();
        switch (dijkstra.getStep()) {
            case UNVISITED_VERTEX_SELECTION:
                currV = dijkstra.selectUnvisitedVertex();
                GUI.addLog("выбрана вершина '" + ((mxCell) currV).getId() + "'" + "\n");
                result = currV;
                break;
            case NEAREST_NEIGHBOR_SELECTION:
                currE = dijkstra.selectNearestNeighbor(cell);
                if (currE.equals(cell) && dijkstra.getStep() == Dijkstra.Steps.UNVISITED_VERTEX_SELECTION) {
                    dijkstra.removeVertex(cell, currE);
                    GUI.addLog("  все исходящие ребра просмотрены" + "\n");
                }
                else if (!currE.equals(cell)) {
                    GUI.addLog("  выбрано ребро до вершины '" + ((mxCell) currE).getTarget().getId() + "'" + "\n");
                }
                result = currE;
                break;
            case RELAXATION:
                if (dijkstra.getDistance(((mxCell) cell).getTarget()) > dijkstra.getDistance(((mxCell) cell).getSource()) + (double)((mxCell) cell).getValue()) {
                    GUI.addLog("    релаксация прошла успешно:" + "\n" + "      " + dijkstra.getDistance(((mxCell) cell).getSource()) +
                    " + " + (double)((mxCell) cell).getValue() + " < " + dijkstra.getDistance(((mxCell) currE).getTarget()) + "\n");
                    result = dijkstra.relax(cell);
                }
                else {
                    GUI.addLog("    релаксация прошла неудачно:" + "\n" + "      " + dijkstra.getDistance(((mxCell) cell).getSource()) +
                    " + " + (double)((mxCell) cell).getValue() + " ≥ " + dijkstra.getDistance(((mxCell) currE).getTarget()) + "\n");
                    dijkstra.setStep(Dijkstra.Steps.NEAREST_NEIGHBOR_SELECTION);
                }
                break;
        }

        return result;
    }

    /**
     * Метод для проверки на наличие между двумя вершинами ребра
     */
    private boolean isEnable(Object v1, Object v2) {
        for(Object v: graph.getEdgesBetween(v1, v2))
            if(((mxCell) v).getTarget().equals(v1)) {
                JOptionPane.showMessageDialog(null, "Между вершинами " + ((mxCell) v1).getValue().toString()
                        + " и " + ((mxCell) v2).getValue().toString() + " уже существует ребро!", "Warning!", JOptionPane.PLAIN_MESSAGE);
                return false;
            }
        return true;
    }
}


