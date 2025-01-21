package diamond;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class Main {
    public static void main(String[] args) {

        JFrame frame = new JFrame("3D Renderer");
        Render renderPanel = new Render();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(renderPanel, BorderLayout.CENTER);
        frame.setSize(600, 600);
        frame.setVisible(true);

        renderPanel.addMouseMotionListener(new MouseMotionListener() {
            private int lastX = 0;
            private int lastY = 0;

            @Override
            public void mouseDragged(MouseEvent e) {
                int deltaX = e.getX() - lastX;
                int deltaY = e.getY() - lastY;

                double yi = 180.0 / renderPanel.getHeight();
                double xi = 180.0 / renderPanel.getWidth();

                renderPanel.x += -(deltaX * xi);
                renderPanel.y += (deltaY * yi);

                lastX = e.getX();
                lastY = e.getY();
                renderPanel.repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                lastX = e.getX();
                lastY = e.getY();
            }
        });

    }
}
