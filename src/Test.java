import com.mxgraph.layout.*;
import com.mxgraph.swing.*;
import org.jgrapht.*;
import org.jgrapht.ext.*;
import org.jgrapht.graph.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import javax.swing.border.Border;

public class Test extends JApplet {
    private static final long serialVersionUID = 2202072534703043194L;
    private static final Dimension DEFAULT_SIZE = new Dimension(800, 500);
    private mxCircleLayout layout;
    private JGraphXAdapter<String, DefaultEdge> jgxAdapter;
    private ListenableGraph<String, DefaultEdge> g;

    private JButton addVertexButton = new JButton("V");
    private JButton addEdgeButton = new JButton("E");
    private JButton nextButton = new JButton("▶");
    private JButton executeButton = new JButton("▶▶");
    private JButton helpButton = new JButton(" ? ");
    private final String TITLE_message = "Справка";
    //private JTextField inputVertex = new JTextField();
    //private JTextField inputFrom = new JTextField();
    //private JTextField inputTo = new JTextField();
    //private JTextField inputWeight = new JTextField();

    public static void main(String[] args) {
        Test applet = new Test();
        applet.init();

        JFrame frame = new JFrame();
        frame.getContentPane().add(applet);
        frame.setTitle("JGraphT Adapter to JGraphX Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void init() {
        /*inputVertex.setBounds(635, 330, 40, 40);
        getContentPane().add(inputVertex);

        inputFrom.setBounds(635, 380, 40, 40);
        getContentPane().add(inputFrom);

        inputTo.setBounds(685, 380, 40, 40);
        getContentPane().add(inputTo);

        inputWeight.setBounds(735, 380, 40, 40);
        getContentPane().add(inputWeight);*/

        addVertexButton.setBounds( 740, 135, 50, 50);
        addVertexButton.addActionListener(new addVertexButtonEventListener());
        getContentPane().add(addVertexButton);
        addVertexButton.setBorder(new RoundedBorder(10));
        //addVertexButton.setBorder(new RoundedBorder(10));

        addEdgeButton.setBounds(740, 190, 50, 50);
        addEdgeButton.addActionListener(new addEdgeButtonEventListener());
        getContentPane().add(addEdgeButton);
        addEdgeButton.setBorder(new RoundedBorder(10));

        nextButton.setBounds(740, 245, 50, 50);
        //nextButton.addActionListener(new addEdgeButtonEventListener());
        nextButton.addActionListener(new checkStartVertex());
        getContentPane().add(nextButton);
        nextButton.setBorder(new RoundedBorder(10));

        executeButton.setBounds(740, 300, 50, 50);
        //executeButton.addActionListener(new addEdgeButtonEventListener());
        getContentPane().add(executeButton);
        executeButton.setBorder(new RoundedBorder(10));
        helpButton.setBounds(740, 80, 50, 50);
        helpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(Test.this,
                        "<html><h2>Справка:</h2><p>Данный графический интерфейс визуализирует алгоритм<br> поиска кратчайшего пути в графе - алгоритм Дейкстры.<br>" +
                                "<i><br>Описание кнопок:</i> <br>" +
                                "1) V - кнопка добавления вершины в граф;<br>2) E - кнопка добавления ребра в граф;<br>" +
                                "3) ▶ - кнопка для перехода к следующей итерации алгоритма;<br>4) ▶▶ - кнопка вывода конечного результата алгоритма.</p>", TITLE_message, JOptionPane.INFORMATION_MESSAGE);
            }
        });
        getContentPane().add(helpButton);
        helpButton.setBorder(new RoundedBorder(10));


// create a JGraphT graph
        g = new DefaultListenableGraph<>(new DefaultDirectedGraph<>(DefaultEdge.class));

// create a visualization using JGraph, via an adapter
        jgxAdapter = new JGraphXAdapter<>(g);

        setPreferredSize(DEFAULT_SIZE);
        mxGraphComponent component = new mxGraphComponent(jgxAdapter);
        component.setConnectable(false);
        component.getGraph().setAllowDanglingEdges(false);
        getContentPane().add(component);
        resize(DEFAULT_SIZE);

        String v1 = "v1";
        String v2 = "v2";
        String v3 = "v3";
        String v4 = "v4";


// add some sample data (graph manipulated via JGraphX)
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);

        //g.addEdge(v1, v2);
        g.addEdge(v2, v3);
        g.addEdge(v3, v1);
        g.addEdge(v4, v3);

// positioning via jgraphx layouts
        layout = new mxCircleLayout(jgxAdapter);

// center the circle
        int radius = 100;
        layout.setX0((DEFAULT_SIZE.width / 2.0) - radius);
        layout.setY0((DEFAULT_SIZE.height / 2.0) - radius);
        layout.setRadius(radius);
        layout.setMoveCircle(true);

        layout.execute(jgxAdapter.getDefaultParent());
// that's all there is to it!...
    }

    class addVertexButtonEventListener implements ActionListener {

        /*public void actionPerformed(ActionEvent e) {
            String input = inputVertex.getText();
            if (!g.containsVertex(input))
                g.addVertex(input);
            else
                JOptionPane.showMessageDialog(null, "this vertex has already been added", "warning", JOptionPane.PLAIN_MESSAGE);
            inputVertex.setText("");
        }*/
        public void actionPerformed(ActionEvent e) {
            String input = JOptionPane.showInputDialog(
                    Test.this,
                    "<html><h2>Введите вершину:");
            if(input != null) {
                if(input.equals(""))
                    return;
                if (!g.containsVertex(input)) {
                    g.addVertex(input);
                    layout.execute(jgxAdapter.getDefaultParent());
                }
                else
                    JOptionPane.showMessageDialog(null, "this vertex has already been added", "warning", JOptionPane.PLAIN_MESSAGE);
            }
            //inputVertex.setText("");
        }

    }

    class addEdgeButtonEventListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String from = JOptionPane.showInputDialog(
                    Test.this,
                    "<html><h2>Введите первую вершину:");
            String to = JOptionPane.showInputDialog(
                    Test.this,
                    "<html><h2>Введите вторую вершину:");
            String weight = JOptionPane.showInputDialog(
                    Test.this,
                    "<html><h2>Введите вес ребра:");
            //String from = inputFrom.getText();
            //String to = inputTo.getText();
            //String weight = inputWeight.getText();
            if (!g.containsVertex(from) && !g.containsVertex(to))
                JOptionPane.showMessageDialog(null, "Missing both vertices", "warning", JOptionPane.PLAIN_MESSAGE);
            else if (!g.containsVertex(from) && g.containsVertex(to))
                JOptionPane.showMessageDialog(null, "Missing start vertex", "warning", JOptionPane.PLAIN_MESSAGE);
            else if (g.containsVertex(from) && !g.containsVertex(to))
                JOptionPane.showMessageDialog(null, "Missing final vertex", "warning",

                        JOptionPane.PLAIN_MESSAGE);
            else if (g.containsVertex(from) && g.containsVertex(to))
                g.addEdge(from, to);

            //inputFrom.setText("");
            //inputTo.setText("");
            //inputWeight.setText("");
        }

    }

    class checkStartVertex implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // Диалоговое окно ввода данных
            HashSet<String> tmp = new HashSet<>(g.vertexSet());
            String[] vertices = tmp.toArray(new String[tmp.size()]);
            Object result = JOptionPane.showInputDialog(
                    Test.this,
                    "Выбирете начальную вершину :",
                    "Выбор вершины",
                    JOptionPane.QUESTION_MESSAGE, null,
                    vertices, vertices[0]);
        }
    }

    private static class RoundedBorder implements Border {

        private int radius;


        RoundedBorder(int radius) {
            this.radius = radius;
        }


        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius+1, this.radius+1, this.radius+2, this.radius);
        }


        public boolean isBorderOpaque() {
            return true;
        }


        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.drawRoundRect(x, y, width-1, height-1, radius, radius);
        }
    }


}