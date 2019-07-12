import com.mxgraph.model.mxCell;
import java.awt.Color;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import java.util.ArrayList;
/**
 * Класс реализации графического интерфейса
 */
class GUI extends JFrame {
    private GAdapter graph = new GAdapter();
    private static ArrayList<JButton> buttons = new ArrayList<>();
    private String[] button_name = {" ? ", "\uD83D\uDCC1", "V", "E", "▶", "▶▶", "\uD83C\uDFC1", "\uD83D\uDCBE",};
    private static JCheckBox logChecker = new JCheckBox("логи");
    private static JTextPane logsPane = new JTextPane();
    private JScrollPane scrollPane = new JScrollPane(logsPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    private final String TITLE_message = "Справка";

    /**
     * Задание формы кнопок и их инициализация
     */
    private void initButtons() {
        ButtonsHandler();

        buttons.get(0).addActionListener(e -> JOptionPane.showMessageDialog(GUI.this,
                "<html><h2>Справка:</h2><p>Данный графический интерфейс визуализирует алгоритм<br> поиска кратчайшего пути в графе - алгоритм Дейкстры.<br>" +
                        "<i><br>Описание кнопок:</i> <br>" +
                        "1) V - кнопка добавления вершины в граф;<br>" +
                        "2) E - кнопка добавления ребра в граф;<br>" +
                        "3) ▶ - кнопка для перехода к следующей итерации алгоритма;<br>" +
                        "4) ▶▶ - кнопка вывода конечного результата алгоритма.<br>" +
                        "5) \uD83D\uDCC1 - кнопка для считывания из файла.<br>" +
                        "6) \uD83C\uDFC1 - кнопка показа конечного результата алгоритма<br></p>" +
                        "7) \uD83D\uDCBE - кнопка вывода результата алгоритма в файл<br></p>" +
                        "<br>Значения в () - минимальное расстояние до вершин из начальной.", TITLE_message, JOptionPane.INFORMATION_MESSAGE));

        buttons.get(1).addActionListener(e -> graph.fileReaderHandler(getContentPane()));

        buttons.get(2).addActionListener(e -> {
            String input = JOptionPane.showInputDialog(
                    GUI.this,
                    "<html><h2>Введите вершину:");
            graph.addVertexButtonEventListener(input);
        });

        buttons.get(3).addActionListener(e -> {
            String from = JOptionPane.showInputDialog(
                    GUI.this,
                    "<html><h2>Введите первую вершину:");
            if(from == null) {
                exceptionChecker(1);
                return;
            }
            mxCell vFrom = graph.cellFounder(from);
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
            mxCell vTo = graph.cellFounder(to);
            if(vTo == null) {
                exceptionChecker(2);
                return;
            }
            String weight = JOptionPane.showInputDialog(
                    GUI.this,
                    "<html><h2>Введите вес ребра:");
            if(weight == null)
                return;
            graph.DoubleParser(vFrom, vTo, weight, weight);
        });

        buttons.get(4).addActionListener(e -> graph.nextIteration());
        buttons.get(4).setEnabled(false);

        buttons.get(5).addActionListener(e -> {
            while(graph.nextIteration());
            buttons.get(5).setEnabled(false);
            buttons.get(7).setEnabled(true);
        });
        buttons.get(5).setEnabled(false);

        buttons.get(6).addActionListener(e -> {
            JOptionPane jOptionPane = new JOptionPane();
            jOptionPane.showMessageDialog(GUI.this,
                    "<html><h2>Результат работы агоритма Дейкстры:</h2><p>" + ((mxCell) graph.getStart()).getId() + " - стартовая вершина\n" + graph.getDijkstra().toString(), "Вывод", JOptionPane.INFORMATION_MESSAGE);

        });
        buttons.get(6).setEnabled(false);

        buttons.get(7).addActionListener(e -> FileHandler.fileSaver(getContentPane()));
        buttons.get(7).setEnabled(false);

        logChecker.setBounds(736, 3, 55, 25);
        getContentPane().add(logChecker);
        logChecker.addItemListener(e -> {
            if (e.getStateChange() == 1) {
                scrollPane.setVisible(true);
            }
            else {
                scrollPane.setVisible(false);
            }
        });
        logsPane.setBounds(555, 30, 230, 1000);
        logsPane.setEditable(false);
        logsPane.setBackground(Color.getColor("238 238 238"));

        scrollPane.setBounds(555, 30, 230, 455);
        getContentPane().add(scrollPane);
        scrollPane.setVisible(false);
    }

    /**
     * Метод для вызова инициализации графа, кнопок и кругового макета
     */
    void init() {
        initButtons();
        graph.initGraph();
        graph.initCircleLayout();
        getContentPane().add(graph.getGraphComponent());
    }

    /**
     *Геттер возвращающий объект кнопки для вставки вершины
     */
    static JButton getAddVertexButton() { return buttons.get(2); }
    /**
     *Геттер возвращающий объект кнопки для вставки ребра
     */
    static JButton getAddEdgeButton() { return buttons.get(3); }
    /**
     *Геттер возвращающий объект кнопки для перехода к следующей итерации
     */
    static JButton getNextButton() { return buttons.get(4); }
    /**
     *Геттер возвращающий объект кнопки для перехода к конечному результату алгоритма
     */
    static JButton getExecuteButton() { return buttons.get(5); }
    /**
     *Геттер возвращающий объект кнопки для ввода графа из файла
     */
    static JButton getFileButton() { return buttons.get(1); }
    /**
     *Геттер возвращающий объект кнопки для сохранения результатов в файл
     */
    static JButton getSaveButton() { return buttons.get(7); }
    /**
     *Геттер возвращающий объект кнопки для показа результатов программы
     */
    static JButton getShowResultAlgoButton() { return buttons.get(6); }

    /**
     *Геттер возвращающий объект для отображения логов прогрммы.
     */
    static void addLog(String string) {
        logsPane.setText(logsPane.getText() + string);
    }
    static void clearLog() {
        logsPane.setText("");
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
     * Метод для обработки кнопок
     */
    private void ButtonsHandler() {
        int y = 15;
        for(int i = 0; i < 8; i++) {
            buttons.add(new JButton());
            buttons.get(i).setText(button_name[i]);
            buttons.get(i).setBounds(10, y, 50, 50);
            getContentPane().add(buttons.get(i));
            buttons.get(i).setBorder(new RoundedBorder(10));
            y += 55;
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
}