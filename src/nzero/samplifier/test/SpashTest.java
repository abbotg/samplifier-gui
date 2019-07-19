package nzero.samplifier.test;

import javax.swing.*;
import java.net.URL;

public class SpashTest {
    public static void main(String[] args) throws Exception{
        JWindow window = new JWindow();
        window.getContentPane().add(
                new JLabel("", new ImageIcon("res/splash.png"), SwingConstants.CENTER));
        window.setBounds(500, 150, 300, 200);
        window.setVisible(true);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        window.setVisible(false);
        JFrame frame = new JFrame();
        frame.add(new JLabel("Welcome"));
        frame.setVisible(true);
        frame.setSize(300,100);
        window.dispose();
    }
}
