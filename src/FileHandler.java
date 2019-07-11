import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

class FileHandler {
    /**
     * Метод создания проводника
     */
    private static JFileChooser createFileChooser(String exampleFileName){
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

    /**
     * Метод для считывания из файла
     */
    static ArrayList fileReader(Container contentPane) {
        JFileChooser jFileChooser = createFileChooser("file.txt");
        ArrayList<String[]> parts = new ArrayList<>();
        int i = jFileChooser.showOpenDialog(contentPane);
        File pathToFile = jFileChooser.getCurrentDirectory();
        File file = jFileChooser.getSelectedFile();
        JOptionPane jOptionPane = new JOptionPane();
        if (i == jFileChooser.APPROVE_OPTION && file.getName().endsWith("txt")) {
            try {
                File f = new File(pathToFile.toString(), file.getName());
                BufferedReader br = new BufferedReader(new FileReader(f));
                String strLine;
                while ((strLine = br.readLine()) != null) {
                    String[] oneSplit = strLine.split(" ", 3);
                    if (oneSplit.length < 3) {
                        JOptionPane.showMessageDialog(null, "Некорректная строка: \"" + strLine +
                                "\"\nЗадавайте ребра в виде: \"начальная вершина\" \"конечная вершина\" \"вес\"", "Warning!", JOptionPane.PLAIN_MESSAGE);
                        return null;
                    }
                    parts.add(oneSplit);
                }
                return parts;
            } catch (IOException error) {
                JOptionPane.showMessageDialog(null, "Ошибка!", "Warning!", JOptionPane.PLAIN_MESSAGE);
            }
        }
        else if (i == jFileChooser.CANCEL_OPTION) { return null; }
        else {
            jOptionPane.showMessageDialog(null, "<html><h2>Ошибка открытия файла!</h2><p>" + "<html><h2>Выберете файл с расширение *.txt!</h2><p>", "Ошибка сохранения файла", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        return null;
    }

    /**
     * Метод для записи в файл
     */
    static void fileSaver(Container contentPane) {
        JFileChooser jFileChooser = createFileChooser("result.txt");
        int i = jFileChooser.showSaveDialog(contentPane);
        File file = jFileChooser.getSelectedFile();
        JOptionPane jOptionPane = new JOptionPane();
        if(i == jFileChooser.APPROVE_OPTION && file.getName().endsWith("txt")) {
            try {
                FileWriter fw = new FileWriter(file);
                fw.write("Результат работы агоритма Дейкстры:\n" + '\n' + GAdapter.getStartId() + " - стартовая вершина\n"+ '\n' + GAdapter.getDijkstraResult());
                jOptionPane.showMessageDialog(null, "<html><h2>Файл сохранен успешно!</h2><p>", "Сохранение файла", JOptionPane.INFORMATION_MESSAGE);
                fw.flush();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        else if(i == jFileChooser.CANCEL_OPTION){}
        else {
            jOptionPane.showMessageDialog(null, "<html><h2>Ошибка сохранения файла!</h2><p>" + "<html><h2>Выберете файл с расширение *.txt!</h2><p>", "Ошибка сохранения файла", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
