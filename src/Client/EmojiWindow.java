package Client;

import javax.swing.*;
import java.awt.*;

public class EmojiWindow extends JWindow {
    static int i;

    EmojiWindow(int i) {
        this.i=i;
        setLocationRelativeTo(null);
        setSize(30, 30);
        //getContentPane().setLayout(null);
        JPanel panel = new ImagePanel();
        //panel.setBounds(0, 0, 30, 30);
        getContentPane().add(panel);
        setVisible(true);
        try {
            Thread.currentThread().sleep(3000);
            dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class ImagePanel extends JPanel {
        public void paint(Graphics g) {
            super.paint(g);
            ImageIcon icon = new ImageIcon("face/"+i+".gif");
            g.drawImage(icon.getImage(), 0, 0, 30, 30, this);
        }
    }

//    public static void main(String[] args) {
//        int i = 10;
//        new EmojiWindow(i);
//    }
}