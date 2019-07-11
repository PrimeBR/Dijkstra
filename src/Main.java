import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        GUI frame = new GUI();
        frame.init();
        frame.setSize(800, 500);
        frame.setLocationRelativeTo(null);
        frame.setTitle("Dijkstra visualiser");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
