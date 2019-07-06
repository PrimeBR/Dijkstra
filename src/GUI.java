import Dijkstra.Dijkstra;
import com.mxgraph.layout.*;
import com.mxgraph.model.*;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.border.Border;

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
    private final String TITLE_message = "Справка";

    /**
     * checker - флаг, для проверки, что начальная вершина была выбрана
     * start - объект стартовой вершины
     */
    private boolean checker = false;
    private Object start;

    /**
     *applet - наша окно интерфейса
     *frame - рамка для нашего окна
     */
//    public static void main(String[] args) {
//        GUI applet = new GUI();
//        applet.init();
//        JFrame frame  = new JFrame();
//        frame.getContentPane().add(applet);
//        frame.setTitle("JGraphT Adapter to JGraphX Demo");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.pack();
//        frame.setVisible(true);
//    }

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
        nextButton.addActionListener(new nextIteration());
        getContentPane().add(nextButton);
        nextButton.setBorder(new RoundedBorder(10));

        executeButton.setBounds(740, 300, 50, 50);
        executeButton.addActionListener(new nextIteration());
        getContentPane().add(executeButton);
        executeButton.setBorder(new RoundedBorder(10));

        helpButton.setBounds(740, 80, 50, 50);
        helpButton.addActionListener(e -> JOptionPane.showMessageDialog(GUI.this,
                "<html><h2>Справка:</h2><p>Данный графический интерфейс визуализирует алгоритм<br> поиска кратчайшего пути в графе - алгоритм Дейкстры.<br>" +
                        "<i><br>Описание кнопок:</i> <br>" +
                        "1) V - кнопка добавления вершины в граф;<br>2) E - кнопка добавления ребра в граф;<br>" +
                        "3) ▶ - кнопка для перехода к следующей итерации алгоритма;<br>4) ▶▶ - кнопка вывода конечного результата алгоритма.</p>", TITLE_message, JOptionPane.INFORMATION_MESSAGE));
        getContentPane().add(helpButton);
        helpButton.setBorder(new RoundedBorder(10));
    }

    /**
     * Метод для вызова инициализации графа, кнопок и кругового макета
     */
    @Override
    public void init() {
        initButtons();
        initGraph();
        model.beginUpdate();
        Object v1 = graph.insertVertex(parent, null, "1", 0, 0, 80, 30);
        Object v2 = graph.insertVertex(parent, null, "2", 0, 0, 80, 30);
        Object v3 = graph.insertVertex(parent, null, "3", 0, 0, 80, 30);
        Object v4 = graph.insertVertex(parent, null, "4", 0, 0, 80, 30);

        graph.insertEdge(parent, "1", 3.14, v1, v2);
        graph.insertEdge(parent, "2", 2.71828, v2, v3);
        graph.insertEdge(parent, "3", 2.28, v3, v1);
        graph.insertEdge(parent, "4", 1.448, v4, v3);
        model.endUpdate();
        initCircleLayout();
        Dijkstra test = new Dijkstra(graph, v1);
        test.getPaths();
        System.out.println(test.toString());
    }

    /**
     * Метод инициализации кругового макета
     */
    private void initCircleLayout() {
        layout = new mxCircleLayout(graph);
        int radius = 100;
        layout.setX0((DEFAULT_SIZE.width / 2.0) - 2*radius);
        layout.setY0((DEFAULT_SIZE.height / 2.0) - radius);
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

    /**
     * Класс с реализацией действий для добавления вершины в граф
     */
    class addVertexButtonEventListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            /*
            В input занасится имя для новой переменной,
            далее в цикле for происходит обход по всем вершинам графа в поиске совпадения с input,
            если совпадение происходит, значит вершина с таким именем уже есть и происходит вывод в окно соответствующего сообщения,
            иначе она добавляется в граф
             */
            String input = JOptionPane.showInputDialog(
                    GUI.this,
                    "<html><h2>Введите вершину:");
            if(input != null) {
                if(input.equals(""))
                    return;
                for(Object v: graph.getChildVertices(graph.getDefaultParent())) {
                    if (input.equals(((mxCell) v).getValue())) {
                        JOptionPane.showMessageDialog(null, "this vertex has already been added", "warning", JOptionPane.PLAIN_MESSAGE);
                        return;
                    }
                }
                graph.insertVertex(parent, null, input, 100, 100, 80, 30);
                layout.execute(graph.getDefaultParent());
            }
        }

    }

    /**
     * Класс с реализацией действий для добавления ребра в граф
     */
    class addEdgeButtonEventListener implements ActionListener {
        /*
        vFrom, vTo - объекты вершин, между которыми необходимо построить ребро.
        from, to - строки, в которые будет введено названия вершин.
        Принцип работы аналогичен методу по добавлению ребра в граф.
         */
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
            Object tmp = graph.insertEdge(parent, null, weight, vFrom, vTo);
            graph.setCellStyles(mxConstants.STYLE_STROKECOLOR ,"red", new Object[]{tmp});

        }

    }

    /**
     * Класс перехода к следующей итерации
     */
    class nextIteration implements ActionListener {
        /*
        tmp - все рёбра графа
        source, target - источник и сток, вершины определённого ребра графа
        edges - все рёбра, которые имеют source и target
        На данный момент метод производит проверку на наличие стартовой вершины в графе
        при помощи стороннего метода checkStartVertex(), а также выделяет другим цветом
        выбранную для следующей итерации вершину и , соответственно, ребро.
         */
        public void actionPerformed(ActionEvent e) {
            if(!checker)
                checkStartVertex();
            Object[] tmp = graph.getAllEdges(graph.getChildVertices(graph.getDefaultParent()));
            for(Object v: tmp) {
                Object source = ((mxCell) v).getSource();
                Object target = ((mxCell) v).getTarget();
                Object[] edges = graph.getEdgesBetween(source, target);
                if(edges.length == 0)
                    return;
                graph.setCellStyles(mxConstants.STYLE_STROKECOLOR ,"FA8072", new Object[]{edges[0]});
                if(!start.equals(((mxCell) source).getValue()))
                    graph.setCellStyle("defaultVertex;fillColor=#FA8072", new Object[]{source});

            }
        }
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

    /**
     * Метод для проверки на наличие в графе начальной вершины
     */
    private void checkStartVertex() {
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
        if(result == null) return;
        start = result;
        for(Object v: tmp)
            if((result.toString()).equals(((mxCell) v).getValue())) {
                checker = true;
                graph.setCellStyle("defaultVertex;fillColor=#98FB98", new Object[]{v});
                return;
            }
    }

    /**
     *Метод для вывода сообщения в случае наудачного добавления ребра
     */
    private void exceptionChecker(int flag) {
        if(flag == 1)
            JOptionPane.showMessageDialog(null, "Missing start vertex", "warning", JOptionPane.PLAIN_MESSAGE);
        else if(flag == 2)
            JOptionPane.showMessageDialog(null, "Missing final vertex", "warning", JOptionPane.PLAIN_MESSAGE);

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


}