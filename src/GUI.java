import Dijkstra.*;
import com.mxgraph.layout.*;
import com.mxgraph.model.*;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.border.Border;
import javax.swing.filechooser.FileFilter;

/**
 * Класс реализации графического интерфейса
 */
public class GUI extends JApplet {
    /**
     * Dimension - размер окно интерфейса
     * layout - расширяет макет mxGraph для реализации кругового макета для заданного радиуса.
     * graph - граф, в котором будут искать кратчайшие пути
     * parent - родитель для вставки новых ячеек.
     * model - графовая модель, модель действует оболочка с уведомлением о событии для всех изменений,
     * тогда как ячейки содержат атомарные операции для обновления фактической структуры данных.
     */
    private static final Dimension DEFAULT_SIZE = new Dimension(800, 500);
    private mxCircleLayout layout;
    private mxGraph graph;
    private Object parent;
    private mxGraphModel model;
    private Dijkstra test;

    private Object currV = new mxCell();
    private Object currE = new mxCell();
    private Object cell = new mxCell();
    /**
     *     addVertexButton - кнопка для добавления вершины в граф
     *     addEdgeButton - кнопка для добавления ребра в граф
     *     nextButton - кнопка перехода к следующей итерации
     *     executeButton - кнопка перехода к результату алгоритма
     *     helpButton - справочная кнопка
     */
    private JButton addVertexButton = new JButton("V");
    private JButton addEdgeButton = new JButton("E");
    private JButton nextButton = new JButton("▶");
    private JButton executeButton = new JButton("▶▶");
    private JButton helpButton = new JButton(" ? ");
    private JButton fileButton = new JButton("\uD83D\uDCC1");
    private JButton saveButton = new JButton("\uD83D\uDCBE");
    private JButton showResultAlgoButton = new JButton("\uD83C\uDFC1");

    private final String TITLE_message = "Справка";

    /**
     * checker - флаг, для проверки, что начальная вершина была выбрана
     * start - объект стартовой вершины
     */
    private boolean checker = false;
    private Object start;

    /**
     * Задание формы кнопок и их инициализация
     */
    private void initButtons() {
        addVertexButton.setBounds(740, 135, 50, 50);
        addVertexButton.addActionListener(new addVertexButtonEventListener());
        getContentPane().add(addVertexButton);
        addVertexButton.setBorder(new RoundedBorder(10));

        addEdgeButton.setBounds(740, 190, 50, 50);
        addEdgeButton.addActionListener(new addEdgeButtonEventListener());
        getContentPane().add(addEdgeButton);
        addEdgeButton.setBorder(new RoundedBorder(10));

        nextButton.setBounds(740, 245, 50, 50);
        nextButton.addActionListener(new nextIterationButton());
        getContentPane().add(nextButton);
        nextButton.setBorder(new RoundedBorder(10));

        executeButton.setBounds(740, 300, 50, 50);
        executeButton.addActionListener(new Execute());
        getContentPane().add(executeButton);
        executeButton.setBorder(new RoundedBorder(10));

        showResultAlgoButton.setBounds(740, 355, 50, 50);
        showResultAlgoButton.addActionListener(new showResultAlgo());
        getContentPane().add(showResultAlgoButton);
        showResultAlgoButton.setBorder(new RoundedBorder(10));
        showResultAlgoButton.setEnabled(false);

        helpButton.setBounds(740, 25, 50, 50);
        helpButton.addActionListener(e -> JOptionPane.showMessageDialog(GUI.this,
                "<html><h2>Справка:</h2><p>Данный графический интерфейс визуализирует алгоритм<br> поиска кратчайшего пути в графе - алгоритм Дейкстры.<br>" +
                        "<i><br>Описание кнопок:</i> <br>" +
                        "1) V - кнопка добавления вершины в граф;<br>2) E - кнопка добавления ребра в граф;<br>" +
                        "3) ▶ - кнопка для перехода к следующей итерации алгоритма;<br>4) ▶▶ - кнопка вывода конечного результата алгоритма.<br>" +
                        "4) \uD83D\uDCBE - кнопака для считывания из файла.<br>" +
                        "5) \uD83C\uDFC1 - кнопка показа конечного результата алгоритма<br></p>" +
                        "<br>Значения в () - минимальное расстояние до вершин из начальной.", TITLE_message, JOptionPane.INFORMATION_MESSAGE));

        getContentPane().add(helpButton);
        helpButton.setBorder(new RoundedBorder(10));

        fileButton.setBounds(740, 80, 50, 50);
        fileButton.addActionListener(new fileReader());
        getContentPane().add(fileButton);
        fileButton.setBorder(new RoundedBorder(10));

        saveButton.setBounds(740, 410, 50, 50);
        saveButton.addActionListener(new saveResultFile());
        getContentPane().add(saveButton);
        saveButton.setBorder(new RoundedBorder(10));
        saveButton.setEnabled(false);
    }

    /**
     * Метод для вызова инициализации графа, кнопок и кругового макета
     */
    public void init() {
        initButtons();
        initGraph();
//        model.beginUpdate();
//        Object v1 = graph.insertVertex(parent, null, "1", 0, 0, 45, 45, "shape=ellipse");
//        ((mxCell) v1).setId("1");
//        Object v2 = graph.insertVertex(parent, null, "2", 0, 0, 45, 45, "shape=ellipse");
//        ((mxCell) v2).setId("2");
//        Object v3 = graph.insertVertex(parent, null, "3", 0, 0, 45, 45, "shape=ellipse");
//        ((mxCell) v3).setId("3");
//        Object v4 = graph.insertVertex(parent, null, "4", 0, 0, 45, 45, "shape=ellipse");
//        ((mxCell) v4).setId("4");

//        graph.insertEdge(parent, "1", 3.14, v1, v2);
//        graph.insertEdge(parent, "2", 2.71828, v2, v3);
//        graph.insertEdge(parent, "3", 2.28, v3, v1);
//        graph.insertEdge(parent, "4", 1.448, v4, v3);
//        graph.insertEdge(parent, "5", 5.0, v1, v4);
//        model.endUpdate();
        initCircleLayout();
    }

    /**
     * Метод инициализации кругового макета
     */
    private void initCircleLayout() {
        layout = new mxCircleLayout(graph);
        int radius = 150;
        layout.setX0((DEFAULT_SIZE.width / 2.0) - 1.33*radius);
        layout.setY0((DEFAULT_SIZE.height / 2.0) - 1.1*radius);
        layout.setRadius(radius);
        layout.setMoveCircle(true);
        layout.execute(graph.getDefaultParent());
    }

    /**
     * Метод инициализации графа
     */
    private void initGraph() {
        graph = new mxGraph();
        model = new mxGraphModel();
        parent = graph.getDefaultParent();

        setPreferredSize(DEFAULT_SIZE);
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        getContentPane().add(graphComponent);

    }

    class fileReader implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try{
                String filename = JOptionPane.showInputDialog(
                        GUI.this,
                        "<html><h2>Введите название файла");
                URL path = GUI.class.getResource(filename + ".txt");
                if(path == null)
                    return;
                File f = new File(path.getFile());
                BufferedReader br = new BufferedReader(new FileReader(f));
                String strLine;
                while ((strLine = br.readLine()) != null) {
                    String[] parts = strLine.split(" ");
                    if(parts.length < 3) {
                        JOptionPane.showMessageDialog(null, "Некорректный ввод данных в файле.\n" +
                                "Задавайте ребра в виде: \"VERTEX1\" \"VERTEX2\" \"WEIGHT\"", "Warning!", JOptionPane.PLAIN_MESSAGE);
                        graph.removeCells(graph.getChildVertices(graph.getDefaultParent()));
                        return;
                    }
                    Object From = null;
                    Object To = null;
                    for(Object v : graph.getChildVertices(graph.getDefaultParent())) {
                        if (parts[0].equals(((mxCell) v).getValue().toString()))
                            From = v;
                        if (parts[1].equals(((mxCell) v).getValue().toString()))
                            To = v;
                    }
                    if(((mxCell) From) == null) {
                        From = graph.insertVertex(parent, null, parts[0], 100, 100, 45, 45, "shape=ellipse");
                        ((mxCell) From).setId(parts[0]);
                        layout.execute(graph.getDefaultParent());
                    }
                    if(((mxCell) To) == null) {
                        To = graph.insertVertex(parent, null, parts[1], 100, 100, 45, 45, "shape=ellipse");
                        ((mxCell) To).setId(parts[1]);
                        layout.execute(graph.getDefaultParent());
                    }
                    DoubleParser((mxCell) From, (mxCell) To, parts[2]);
                }
                fileButton.setEnabled(false);
            } catch (IOException error){
                System.out.println("Ошибка");
            }
        }
    }

    /**
     * Класс с реализацией действий для добавления вершины в граф
     */
    class addVertexButtonEventListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String input = JOptionPane.showInputDialog(
                    GUI.this,
                    "<html><h2>Введите вершину:");
            if(input != null) {
                if(input.equals(""))
                    return;
                for(Object v: graph.getChildVertices(graph.getDefaultParent())) {
                    if (input.equals(((mxCell) v).getValue())) {
                        JOptionPane.showMessageDialog(null, "Данная вершина уже есть в графе", "Warning!", JOptionPane.PLAIN_MESSAGE);
                        return;
                    }
                }
                Object tmp = graph.insertVertex(parent, null, input, 100, 100, 45, 45, "shape=ellipse");
                ((mxCell) tmp).setId(input);
                layout.execute(graph.getDefaultParent());
            }
        }

    }

    /**
     * Класс с реализацией действий для добавления ребра в граф
     */
    class addEdgeButtonEventListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //mxCell vFrom = new mxCell();
            //mxCell vTo = new mxCell();
            String from = JOptionPane.showInputDialog(
                    GUI.this,
                    "<html><h2>Введите первую вершину:");
            if(from == null) {
                exceptionChecker(1);
                return;
            }
            mxCell vFrom = cellFounder(from);
            if(vFrom == null) {
                exceptionChecker(1);
                return;
            }
            String to = JOptionPane.showInputDialog(
                    GUI.this,
                    "<html><h2>Введите вторую вершину:");
            if(to == null) {
                exceptionChecker(2);
                return;
            }
            mxCell vTo = cellFounder(to);
            if(vTo == null) {
                exceptionChecker(2);
                return;
            }
            String weight = JOptionPane.showInputDialog(
                    GUI.this,
                    "<html><h2>Введите вес ребра:");
            DoubleParser(vFrom, vTo, weight);

        }

    }

    private void DoubleParser(mxCell From, mxCell To, String weight) {
        try {
            if(Double.parseDouble(weight) < 0) {
                JOptionPane.showMessageDialog(null, "Введено ребро отрицательного веса", "Warning!", JOptionPane.PLAIN_MESSAGE);
                return;
            }
            graph.insertEdge(parent, null, Double.parseDouble(weight), From, To);
        }
        catch (NumberFormatException w) {
            JOptionPane.showMessageDialog(null, "Введено неккоректная запись числа!\n" +
                    "Совет: используйте \".\" для разделения целой и дробной частей.", "Warning!", JOptionPane.PLAIN_MESSAGE);
        }
    }

    class Execute implements ActionListener {
        @Override
        public void actionPerformed (ActionEvent e) {
            while(nextIteration());
            executeButton.setEnabled(false);
            saveButton.setEnabled(true);
//            if(!checker)
//                checkStartVertex();
//            test.getPaths();
//            System.out.println(test.toString());
        }
    }

    /**
     * Класс перехода к следующей итерации
     */
    class nextIterationButton implements ActionListener {
        @Override
        public void actionPerformed (ActionEvent e) {
            nextIteration();
        }
    }

    private boolean nextIteration() {
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
            nextButton.setEnabled(false);
            addEdgeButton.setEnabled(true);
            addVertexButton.setEnabled(true);
            showResultAlgoButton.setEnabled(true);
            saveButton.setEnabled(true);
        //    System.out.println(test.toString());
            return false;
        }
        else {
            addEdgeButton.setEnabled(false);
            addVertexButton.setEnabled(false);
            return true;
        }
    }

    /**
     * Класс вывода результата работы алгоритма Дейкстры в новом диалоговом окне
     */

    class showResultAlgo implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane jOptionPane = new JOptionPane();
            jOptionPane.showMessageDialog(GUI.this,
                    "<html><h2>Результат работы агоритма Дейкстры:</h2><p>" + ((mxCell) start).getId() + " - initial vertex\n" + test.toString(), "Вывод", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Метод для проверки на наличие в графе начальной вершины
     */
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
                GUI.this,
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

    class saveResultFile implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setCurrentDirectory(new File("."));
            jFileChooser.setSelectedFile(new File("result.txt"));
            jFileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if(f.getName().endsWith("txt")){
                        return true;
                    }
                    return false;
                }
                @Override
                public String getDescription() {
                    return "Текстовые файлы (*.txt)";
                }
            });
            int i = jFileChooser.showSaveDialog(getContentPane());
            File file = jFileChooser.getSelectedFile();
            JOptionPane jOptionPane = new JOptionPane();
            if(i == jFileChooser.APPROVE_OPTION && file.getName().endsWith("txt")){
                try {
                    FileWriter fw = new FileWriter(file);
                    fw.write("Результат работы агоритма Дейкстры:\n\n" + ((mxCell) start).getId() + " - initial vertex\n\n" + test.toString());
                    jOptionPane.showMessageDialog(GUI.this, "<html><h2>Файл сохранен успешно!</h2><p>", "Сохранение файла", JOptionPane.INFORMATION_MESSAGE);
                    fw.flush();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            else if(i == jFileChooser.CANCEL_OPTION){}
            else{
                jOptionPane.showMessageDialog(GUI.this, "<html><h2>Ошибка сохранения файла!</h2><p>" + "<html><h2>Выберете файл с расширение *.txt!</h2><p>", "Ошибка сохранения файла", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    /**
     *Метод для вывода сообщения в случае наудачного добавления ребра
     */
    private void exceptionChecker(int flag) {
        if(flag == 1)
            JOptionPane.showMessageDialog(null, "Пропущен ввод стартовой вершины", "Warning!", JOptionPane.PLAIN_MESSAGE);
        else if(flag == 2)
            JOptionPane.showMessageDialog(null, "Пропущен ввод конечной вершины", "Warning!", JOptionPane.PLAIN_MESSAGE);

    }

    /**
     *Метод для поиска вершины в графе по её названию
     */
    private mxCell cellFounder(String result) {
        for(Object v: graph.getChildVertices(graph.getDefaultParent())) {
            if (result.equals(((mxCell) v).getValue())) {
                return (mxCell) v;
            }
        }
        return null;
    }

    private Object nextStep(Object cell) {
        Object result = new mxCell();
        switch (test.getStep()) {
            case UNVISITED_VERTEX_SELECTION:
                currV = test.selectUnvisitedVertex();
                result = currV;
                break;
            case NEAREST_NEIGHBOR_SELECTION:
                currE = test.selectNearestNeighbor(cell);
                test.removeVertex(cell, currE);
                result = currE;
                break;
            case RELAXATION:
                result = test.relax(cell);
                break;
        }

        return result;
    }

    /**
     * Класс с методами и полями, необходимыми для скругления краёв у кнопок
     */
    private static class RoundedBorder implements Border {

        private int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }
        /*
        Возвращает вставки границы.
         */
        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius+1, this.radius+1, this.radius+2, this.radius);
        }
        /*
        Возвращает, является ли граница непрозрачной.
         */
        public boolean isBorderOpaque() {
            return true;
        }
        /*
        Рисует границу для указанного компонента с указанным положением и размером.
         */
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.drawRoundRect(x, y, width-1, height-1, radius, radius);
        }
    }
}