import javax.swing.*;
import java.awt.*;


public class Main {
    public static void main(String[] args) {
        GUI applet = new GUI();
        applet.init();
        JFrame frame  = new JFrame();
        frame.getContentPane().add(applet);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dimension = toolkit.getScreenSize();
        frame.setLocation(dimension.width/2 - 400, dimension.height/2 - 250);
        frame.setTitle("Graph creator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
