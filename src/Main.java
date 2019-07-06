import javax.swing.*;


public class Main {
    public static void main(String[] args) {
        GUI applet = new GUI();
        applet.init();
        JFrame frame  = new JFrame();
        frame.getContentPane().add(applet);
        frame.setTitle("JGraphT Adapter to JGraphX Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
