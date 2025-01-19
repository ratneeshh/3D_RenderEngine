package tetrahedron;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Render extends JPanel {
    private BufferedImage img;
    private double[] zBuffer;  // Add z-buffer as class field
    List<Triangle> tris;
    double zoomFactor = 1.0;
    int x = 0;
    int y = 0;

    Render() {
        tris = new ArrayList<>();

        tris.add(new Triangle
                (new Vertex(100, 100, 100),
                        new Vertex(-100, -100, 100),
                        new Vertex(-100, 100, -100),
                        Color.WHITE));
        tris.add(new Triangle
                (new Vertex(100, 100, 100),
                        new Vertex(-100, -100, 100),
                        new Vertex(100, -100, -100),
                        Color.RED));
        tris.add(new Triangle(
                new Vertex(-100, 100, -100),
                new Vertex(100, -100, -100),
                new Vertex(100, 100, 100),
                Color.GREEN));
        tris.add(new Triangle
                (new Vertex(-100, 100, -100),
                        new Vertex(100, -100, -100),
                        new Vertex(-100, -100, 100),
                        Color.BLUE));
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        // Recreate both buffers when the component size changes
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        zBuffer = new double[width * height];
    }

    static boolean sameSide(Vertex A, Vertex B, Vertex C, Vertex p) {
        Vertex AB = new Vertex(B.x - A.x, B.y - A.y, B.z - A.z);
        Vertex AC = new Vertex(C.x - A.x, C.y - A.y, C.z - A.z);
        Vertex AP = new Vertex(p.x - A.x, p.y - A.y, p.z - A.z);

        double ABxAC = AB.x * AC.y - AC.x * AB.y;
        double ABxAP = AB.x * AP.y - AP.x * AB.y;

        return ABxAC * ABxAP >= 0;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Create or recreate buffers if needed
        if (img == null || img.getWidth() != getWidth() || img.getHeight() != getHeight()) {
            img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
            zBuffer = new double[getWidth() * getHeight()];
        }

        // Initialize z-buffer for this frame
        for (int i = 0; i < zBuffer.length; i++) {
            zBuffer[i] = Double.NEGATIVE_INFINITY;
        }

        // Get graphics for the buffer
        Graphics2D g2d = img.createGraphics();

        // Set the background color to black
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Draw triangles with transformations
        double heading = Math.toRadians(x);
        Matrix3 headingTransform = new Matrix3(new double[]{
                Math.cos(heading), 0, -Math.sin(heading),
                0, 1, 0,
                Math.sin(heading), 0, Math.cos(heading)
        });

        double pitch = Math.toRadians(y);
        Matrix3 pitchTransform = new Matrix3(new double[]{
                1, 0, 0,
                0, Math.cos(pitch), Math.sin(pitch),
                0, -Math.sin(pitch), Math.cos(pitch)
        });

        Matrix3 zoomTransform = new Matrix3(new double[]{
                zoomFactor, 0, 0,
                0, zoomFactor, 0,
                0, 0, zoomFactor
        });

        Matrix3 transform = headingTransform.multiply(pitchTransform).multiply(zoomTransform);

        for (Triangle t : tris) {
            Vertex v1 = transform.transform(t.v1);
            Vertex v2 = transform.transform(t.v2);
            Vertex v3 = transform.transform(t.v3);

            // Translate vertices to screen coordinates
            v1.x += getWidth() / 2.0;
            v1.y += getHeight() / 2.0;
            v2.x += getWidth() / 2.0;
            v2.y += getHeight() / 2.0;
            v3.x += getWidth() / 2.0;
            v3.y += getHeight() / 2.0;

            int minX = (int) Math.max(0, Math.ceil(Math.min(v1.x, Math.min(v2.x, v3.x))));
            int maxX = (int) Math.min(img.getWidth() - 1,
                    Math.floor(Math.max(v1.x, Math.max(v2.x, v3.x))));
            int minY = (int) Math.max(0, Math.ceil(Math.min(v1.y, Math.min(v2.y, v3.y))));
            int maxY = (int) Math.min(img.getHeight() - 1,
                    Math.floor(Math.max(v1.y, Math.max(v2.y, v3.y))));

            for (int y = minY; y <= maxY; y++) {
                for (int x = minX; x <= maxX; x++) {
                    Vertex p = new Vertex(x, y, 0);
                    boolean V1 = sameSide(v1, v2, v3, p);
                    boolean V2 = sameSide(v2, v3, v1, p);
                    boolean V3 = sameSide(v3, v1, v2, p);

                    if (V3 && V2 && V1) {
                        // Calculate depth before drawing
                        double depth = (v1.z + v2.z + v3.z) / 3.0;  // Average depth of triangle
                        int zIndex = y * img.getWidth() + x;

                        // Only draw if this point is closer than what's already there
                        if (zBuffer[zIndex] < depth) {
                            img.setRGB(x, y, t.color.getRGB());
                            zBuffer[zIndex] = depth;
                        }
                    }
                }
            }
        }

        g2d.dispose();
        g2.drawImage(img, 0, 0, null);
    }
}