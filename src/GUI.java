import com.mxgraph.model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileFilter;

/**
 * Класс реализации графического интерфейса
 */
public class GUI extends JApplet {
    private static final Dimension DEFAULT_SIZE = new Dimension(800, 500);
    private Graph graph = new Graph();
    /**
     *     addVertexButton - кнопка для добавления вершины в граф
     *     addEdgeButton - кнопка для добавления ребра в граф
     *     nextButton - кнопка перехода к следующей итерации
     *     executeButton - кнопка перехода к результату алгоритма
     *     helpButton - справочная кнопка
     */
    private static JButton addVertexButton = new JButton("V");
    private static JButton addEdgeButton = new JButton("E");
    private static JButton nextButton = new JButton("▶");
    private static JButton executeButton = new JButton("▶▶");
    private static JButton helpButton = new JButton(" ? ");
    private static JButton fileButton = new JButton("\uD83D\uDCC1");
    private static JButton saveButton = new JButton("\uD83D\uDCBE");
    private static JButton showResultAlgoButton = new JButton("\uD83C\uDFC1");
    private static JCheckBox logChecker = new JCheckBox("логи");
    private static JTextPane logsPane = new JTextPane();
    private JScrollPane scrollPane = new JScrollPane(logsPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    private final String TITLE_message = "Справка";

    /**
     * Задание формы кнопок и их инициализация
     */
    public static Dimension getDimension() {
        return DEFAULT_SIZE;
    }

    public void initButtons() {
        addVertexButton.setBounds(10, 135, 50, 50);
        addVertexButton.addActionListener(new addVertexButtonEventListener());
        getContentPane().add(addVertexButton);
        addVertexButton.setBorder(new RoundedBorder(10));

        addEdgeButton.setBounds(10, 190, 50, 50);
        addEdgeButton.addActionListener(new addEdgeButtonEventListener());
        getContentPane().add(addEdgeButton);
        addEdgeButton.setBorder(new RoundedBorder(10));

        nextButton.setBounds(10, 245, 50, 50);
        nextButton.addActionListener(new nextIterationButton());
        getContentPane().add(nextButton);
        nextButton.setBorder(new RoundedBorder(10));
        nextButton.setEnabled(false);

        executeButton.setBounds(10, 300, 50, 50);
        executeButton.addActionListener(new Execute());
        getContentPane().add(executeButton);
        executeButton.setBorder(new RoundedBorder(10));
        executeButton.setEnabled(false);

        showResultAlgoButton.setBounds(10, 355, 50, 50);
        showResultAlgoButton.addActionListener(new showResultAlgo());
        getContentPane().add(showResultAlgoButton);
        showResultAlgoButton.setBorder(new RoundedBorder(10));
        showResultAlgoButton.setEnabled(false);

        helpButton.setBounds(10, 25, 50, 50);
        helpButton.addActionListener(e -> JOptionPane.showMessageDialog(GUI.this,
                "<html><h2>Справка:</h2><p>Данный графический интерфейс визуализирует алгоритм<br> поиска кратчайшего пути в графе - алгоритм Дейкстры.<br>" +
                        "<i><br>Описание кнопок:</i> <br>" +
                        "1) V - кнопка добавления вершины в граф;<br>2) E - кнопка добавления ребра в граф;<br>" +
                        "3) ▶ - кнопка для перехода к следующей итерации алгоритма;<br>4) ▶▶ - кнопка вывода конечного результата алгоритма.<br>" +
                        "4) \uD83D\uDCC1 - кнопка для считывания из файла.<br>" +
                        "5) \uD83C\uDFC1 - кнопка показа конечного результата алгоритма<br></p>" +
                        "6) \uD83D\uDCBE - кнопка вывода результата алгоритма в файл<br></p>" +
                        "<br>Значения в () - минимальное расстояние до вершин из начальной.", TITLE_message, JOptionPane.INFORMATION_MESSAGE));

        getContentPane().add(helpButton);
        helpButton.setBorder(new RoundedBorder(10));

        fileButton.setBounds(10, 80, 50, 50);
        fileButton.addActionListener(new fileReader());
        getContentPane().add(fileButton);
        fileButton.setBorder(new RoundedBorder(10));

        saveButton.setBounds(10, 410, 50, 50);
        saveButton.addActionListener(new saveResultFile());
        getContentPane().add(saveButton);
        saveButton.setBorder(new RoundedBorder(10));
        saveButton.setEnabled(false);

        logChecker.setBounds(740, 3, 55, 25);
        getContentPane().add(logChecker);
        logChecker.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                scrollPane.setVisible(true);
            }
            else {
                scrollPane.setVisible(false);
            }
        });

        logsPane.setBounds(555, 30, 220, 1000);
        logsPane.setBackground( new Color(238, 238, 238));


        scrollPane.setBounds(555, 30, 220, 400);
        getContentPane().add(scrollPane);

        scrollPane.setVisible(false);

    }

    /**
     * Метод для вызова инициализации графа, кнопок и кругового макета
     */
    public void init() {
        initButtons();
        graph.initGraph();
        graph.initCircleLayout();
        getContentPane().add(graph.getGraphComponent());
        setPreferredSize(DEFAULT_SIZE);

    }

    public static JButton getAddVertexButton() {
        return addVertexButton;
    }

    public static JButton getAddEdgeButton() {
        return addEdgeButton;
    }

    public static JButton getNextButton() {
        return nextButton;
    }

    public static JButton getExecuteButton() {
        return executeButton;
    }

    public static JButton getFileButton() {
        return fileButton;
    }

    public static JButton getSaveButton() {
        return saveButton;
    }

    public static JButton getShowResultAlgoButton() {
        return showResultAlgoButton;
    }

    public static JTextPane getLogsPane() { return logsPane; }

    private JFileChooser createFileChooser(String exampleFileName){
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setCurrentDirectory(new File("."));
        jFileChooser.setSelectedFile(new File(exampleFileName));
        jFileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().endsWith("txt");
            }
            @Override
            public String getDescription() {
                return "Текстовые файлы (*.txt)";
            }
        });
        return jFileChooser;
    }

    class fileReader implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser jFileChooser = createFileChooser("file.txt");
            int i = jFileChooser.showOpenDialog(getContentPane());
            File pathToFile = jFileChooser.getCurrentDirectory();
            File file = jFileChooser.getSelectedFile();
            JOptionPane jOptionPane = new JOptionPane();
            if (i == jFileChooser.APPROVE_OPTION && file.getName().endsWith("txt")) {
                try {
                    addVertexButton.setEnabled(true);
                    addEdgeButton.setEnabled(true);
                    File f = new File(pathToFile.toString(), file.getName());
                    BufferedReader br = new BufferedReader(new FileReader(f));
                    graph.fileReader(br);
                } catch (IOException error) {
                    JOptionPane.showMessageDialog(null, "Ошибка!", "Warning!", JOptionPane.PLAIN_MESSAGE);
                }
            } else if (i == jFileChooser.CANCEL_OPTION) {
            } else {
                jOptionPane.showMessageDialog(null, "<html><h2>Ошибка открытия файла!</h2><p>" + "<html><h2>Выберете файл с расширение *.txt!</h2><p>", "Ошибка сохранения файла", JOptionPane.INFORMATION_MESSAGE);
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
            graph.addVertexButtonEventListener(input);
        }

    }

    /**
     * Класс с реализацией действий для добавления ребра в граф
     */
    class addEdgeButtonEventListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
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

        }

    }

    class Execute implements ActionListener {
        @Override
        public void actionPerformed (ActionEvent e) {
            while(graph.nextIteration());
            executeButton.setEnabled(false);
            saveButton.setEnabled(true);
        }
    }

    /**
     * Класс перехода к следующей итерации
     */
    class nextIterationButton implements ActionListener {
        @Override
        public void actionPerformed (ActionEvent e) {
            graph.nextIteration();
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
                    "<html><h2>Результат работы агоритма Дейкстры:</h2><p>" + ((mxCell) graph.getStart()).getId() + " - стартовая вершина\n" + graph.getTest().toString(), "Вывод", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    class saveResultFile implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser jFileChooser = createFileChooser("result.txt");
            int i = jFileChooser.showSaveDialog(getContentPane());
            File file = jFileChooser.getSelectedFile();
            JOptionPane jOptionPane = new JOptionPane();
            if(i == jFileChooser.APPROVE_OPTION && file.getName().endsWith("txt")){
                try {
                    FileWriter fw = new FileWriter(file);
                    fw.write("Результат работы агоритма Дейкстры:\n" + '\n' + ((mxCell) graph.getStart()).getId() + " - стартовая вершина\n"+ '\n' + graph.getTest().toString());
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